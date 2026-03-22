package com.booking.view;

import com.booking.model.Homestay;
import com.booking.model.User;
import com.booking.service.HomestayService;
import com.booking.service.impl.HomestayServiceImpl;
import com.booking.util.AppColors;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * 民宿管理界面
 */
public class HomestayManageView extends JFrame {

    private User currentUser;
    private HomestayService homestayService;
    private List<Homestay> homestayList;

    private JTable homestayTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton searchButton;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton backButton;
    private JButton roomManageButton; // 房间管理按钮
    
    // 分页相关变量
    private int currentPage = 1;
    private int pageSize = 10;
    private int totalPages = 1;
    
    // ========== 添加组件引用 ==========
    private JLabel statsLabel;  // 统计信息标签
    private JTextField pageField;  // 当前页码输入框
    private JLabel pageInfoLabel;  // 总页数标签
    // ================================

    public HomestayManageView(User user) {
        this.currentUser = user;
        this.homestayService = new HomestayServiceImpl();
        initUI();
        loadData();
    }

    private void initUI() {
        setTitle("民宿管理 - " + currentUser.getRealName());
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(AppColors.LIGHT_PURPLE);

        // 标题
        JLabel titleLabel = new JLabel("民宿管理", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);

        // 搜索面板
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(AppColors.LIGHT_PURPLE);

        JLabel nameLabel = new JLabel("民宿名称:");
        nameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        nameLabel.setForeground(Color.BLACK);
        searchPanel.add(nameLabel);
        
        searchField = new JTextField(20);
        searchField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        searchField.setForeground(Color.BLACK);
        searchField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));

        searchButton = new JButton("搜索");
        styleButton(searchButton);

        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setBackground(AppColors.LIGHT_PURPLE);

        addButton = new JButton("新增民宿");
        editButton = new JButton("编辑");
        deleteButton = new JButton("删除");
        roomManageButton = new JButton("房间管理");
        refreshButton = new JButton("刷新");
        backButton = new JButton("返回");

        styleButton(addButton);
        styleButton(editButton);
        styleButton(deleteButton);
        styleButton(roomManageButton);
        styleButton(refreshButton);
        styleButton(backButton);

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(roomManageButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);

        // 合并顶部面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(AppColors.LIGHT_PURPLE);
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // 表格
        String[] columns = {"ID", "民宿名称", "城市", "地址", "电话", "评分", "状态"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        homestayTable = new JTable(tableModel);
        homestayTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        homestayTable.setRowHeight(25);
        homestayTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 13));
        homestayTable.getTableHeader().setBackground(AppColors.PRIMARY_PURPLE);
        homestayTable.getTableHeader().setForeground(Color.BLACK);
        homestayTable.setSelectionBackground(AppColors.HOVER_PURPLE);

        // 设置列宽
        homestayTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        homestayTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        homestayTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        homestayTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        homestayTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        homestayTable.getColumnModel().getColumn(5).setPreferredWidth(60);
        homestayTable.getColumnModel().getColumn(6).setPreferredWidth(60);

        JScrollPane scrollPane = new JScrollPane(homestayTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppColors.PRIMARY_PURPLE));

        // 底部统计和分页
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(AppColors.LIGHT_PURPLE);
        
        // 统计信息 - 保存引用
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBackground(AppColors.LIGHT_PURPLE);
        statsLabel = new JLabel("总民宿数: 0");
        statsLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        statsLabel.setForeground(Color.BLACK);
        statsPanel.add(statsLabel);
        
        // 分页组件 - 保存引用
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        paginationPanel.setBackground(AppColors.LIGHT_PURPLE);
        
        JButton firstPageButton = new JButton("首页");
        JButton prevPageButton = new JButton("上一页");
        pageField = new JTextField(5);
        pageField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        pageField.setHorizontalAlignment(JTextField.CENTER);
        pageField.setText("1");
        
        pageInfoLabel = new JLabel("/ 1");
        pageInfoLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        pageInfoLabel.setForeground(Color.BLACK);
        
        JButton nextPageButton = new JButton("下一页");
        JButton lastPageButton = new JButton("末页");
        
        styleButton(firstPageButton);
        styleButton(prevPageButton);
        styleButton(nextPageButton);
        styleButton(lastPageButton);
        
        paginationPanel.add(firstPageButton);
        paginationPanel.add(prevPageButton);
        paginationPanel.add(new JLabel("第"));
        paginationPanel.add(pageField);
        paginationPanel.add(pageInfoLabel);
        paginationPanel.add(new JLabel("页"));
        paginationPanel.add(nextPageButton);
        paginationPanel.add(lastPageButton);
        
        bottomPanel.add(statsPanel, BorderLayout.WEST);
        bottomPanel.add(paginationPanel, BorderLayout.EAST);

        // ========== 修复布局：将标题和顶部面板合并 ==========
        JPanel northContainer = new JPanel(new BorderLayout());
        northContainer.setBackground(AppColors.LIGHT_PURPLE);
        northContainer.add(titleLabel, BorderLayout.NORTH);
        northContainer.add(topPanel, BorderLayout.CENTER);

        // 组装界面
        mainPanel.add(northContainer, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // 事件监听
        searchButton.addActionListener(e -> searchHomestay());
        addButton.addActionListener(e -> addHomestay());
        editButton.addActionListener(e -> editHomestay());
        deleteButton.addActionListener(e -> deleteHomestay());
        roomManageButton.addActionListener(e -> openRoomManage());
        refreshButton.addActionListener(e -> loadData());
        backButton.addActionListener(e -> dispose());
        
        // ========== 分页按钮事件（简化版） ==========
        firstPageButton.addActionListener(e -> {
            if (currentPage > 1) {
                loadData(1);
            }
        });
        
        prevPageButton.addActionListener(e -> {
            if (currentPage > 1) {
                loadData(currentPage - 1);
            }
        });
        
        nextPageButton.addActionListener(e -> {
            if (currentPage < totalPages) {
                loadData(currentPage + 1);
            }
        });
        
        lastPageButton.addActionListener(e -> {
            if (currentPage < totalPages) {
                loadData(totalPages);
            }
        });
        
        // 页码输入框事件
        pageField.addActionListener(e -> {
            try {
                int page = Integer.parseInt(pageField.getText());
                if (page >= 1 && page <= totalPages) {
                    loadData(page);
                } else {
                    JOptionPane.showMessageDialog(this, "页码超出范围", "提示", JOptionPane.WARNING_MESSAGE);
                    pageField.setText(String.valueOf(currentPage));
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "请输入有效的页码", "提示", JOptionPane.WARNING_MESSAGE);
                pageField.setText(String.valueOf(currentPage));
            }
        });
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        button.setBackground(AppColors.BUTTON_PURPLE);
        button.setForeground(AppColors.DARK_PURPLE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(AppColors.HOVER_PURPLE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(AppColors.BUTTON_PURPLE);
            }
        });
    }

    private void loadData() {
        tableModel.setRowCount(0);
        searchField.setText(""); // 清空搜索框
        currentPage = 1; // 重置到第一页
        
        // 根据角色加载不同数据
        if ("ADMIN".equals(currentUser.getRole())) {
            // 管理员看所有民宿
            homestayList = homestayService.getAllHomestays(currentPage, pageSize);
        } else {
            // 民宿主只看自己的民宿
            homestayList = homestayService.getHomestaysByHostId(currentUser.getUserId());
        }
        
        // 计算总页数
        long totalCount = homestayService.getTotalCount();
        totalPages = (int) Math.ceil((double) totalCount / pageSize);
        
        for (Homestay h : homestayList) {
            String status = h.getStatus() == 1 ? "营业" : "暂停";
            
            Object[] row = {
                h.getHomestayId(),
                h.getName(),
                h.getCity(),
                h.getAddress(),
                h.getPhone(),
                h.getRating(),
                status
            };
            tableModel.addRow(row);
        }
        
        updateStats();
        updatePaginationInfo();
    }
    
    private void loadData(int page) {
        tableModel.setRowCount(0);
        currentPage = page;
        
        // 根据角色加载不同数据
        if ("ADMIN".equals(currentUser.getRole())) {
            // 管理员看所有民宿
            homestayList = homestayService.getAllHomestays(currentPage, pageSize);
        } else {
            // 民宿主只看自己的民宿
            homestayList = homestayService.getHomestaysByHostId(currentUser.getUserId());
        }
        
        for (Homestay h : homestayList) {
            String status = h.getStatus() == 1 ? "营业" : "暂停";
            
            Object[] row = {
                h.getHomestayId(),
                h.getName(),
                h.getCity(),
                h.getAddress(),
                h.getPhone(),
                h.getRating(),
                status
            };
            tableModel.addRow(row);
        }
        
        updatePaginationInfo();
    }
    
    // ========== 简化的分页信息更新 ==========
    private void updatePaginationInfo() {
        if (pageField != null) {
            pageField.setText(String.valueOf(currentPage));
        }
        if (pageInfoLabel != null) {
            pageInfoLabel.setText("/ " + totalPages);
        }
    }

    // ========== 简化的统计信息更新 ==========
    private void updateStats() {
        int total = tableModel.getRowCount();
        
        if (statsLabel != null) {
            statsLabel.setText("总民宿数: " + total);
        }
    }

    private void searchHomestay() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadData();
            return;
        }

        // 调用Service搜索
        List<Homestay> searchResult = homestayService.searchHomestays(keyword, 1, 100);
        
        // 更新表格
        tableModel.setRowCount(0);
        if (searchResult.isEmpty()) {
            // 搜索不到数据，显示提示并清空搜索框
            JOptionPane.showMessageDialog(this, "未找到符合条件的民宿", "提示", JOptionPane.INFORMATION_MESSAGE);
            searchField.setText(""); // 清空搜索框
            // 重新加载所有民宿数据
            loadData();
        } else {
            for (Homestay h : searchResult) {
                String status = h.getStatus() == 1 ? "营业" : "暂停";
                Object[] row = {
                    h.getHomestayId(),
                    h.getName(),
                    h.getCity(),
                    h.getAddress(),
                    h.getPhone(),
                    h.getRating(),
                    status
                };
                tableModel.addRow(row);
            }
        }
        
        updateStats();
    }

    private void addHomestay() {
        // 只有民宿主和管理员可以添加
        if ("GUEST".equals(currentUser.getRole())) {
            JOptionPane.showMessageDialog(this, "您没有权限添加民宿", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 创建添加民宿对话框
        JDialog dialog = new JDialog(this, "添加民宿", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        
        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(AppColors.LIGHT_PURPLE);
        
        // 表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(AppColors.LIGHT_PURPLE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 民宿名称
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("民宿名称:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextField nameField = new JTextField(30);
        nameField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        nameField.setPreferredSize(new Dimension(300, 25));
        formPanel.add(nameField, gbc);
        
        // 城市
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("城市:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextField cityField = new JTextField(30);
        cityField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        cityField.setPreferredSize(new Dimension(300, 25));
        formPanel.add(cityField, gbc);
        
        // 地址
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("地址:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextField addressField = new JTextField(30);
        addressField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        addressField.setPreferredSize(new Dimension(300, 25));
        formPanel.add(addressField, gbc);
        
        // 电话
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("电话:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextField phoneField = new JTextField(30);
        phoneField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        phoneField.setPreferredSize(new Dimension(300, 25));
        formPanel.add(phoneField, gbc);
        
        // 描述
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("描述:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextArea descriptionArea = new JTextArea(3, 30);
        descriptionArea.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        descriptionScrollPane.setPreferredSize(new Dimension(300, 80));
        formPanel.add(descriptionScrollPane, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(AppColors.LIGHT_PURPLE);
        
        JButton saveButton = new JButton("保存");
        JButton cancelButton = new JButton("取消");
        
        styleButton(saveButton);
        styleButton(cancelButton);
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // 组装界面
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setContentPane(mainPanel);
        
        // 保存按钮事件
        saveButton.addActionListener(e -> {
            // 获取输入
            String name = nameField.getText().trim();
            String city = cityField.getText().trim();
            String address = addressField.getText().trim();
            String phone = phoneField.getText().trim();
            String description = descriptionArea.getText().trim();
            
            // 验证输入
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "民宿名称不能为空", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (city.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "城市不能为空", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (address.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "地址不能为空", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (phone.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "电话不能为空", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // 创建民宿对象
            Homestay newHomestay = new Homestay();
            newHomestay.setName(name);
            newHomestay.setCity(city);
            newHomestay.setAddress(address);
            newHomestay.setPhone(phone);
            newHomestay.setDescription(description);
            newHomestay.setHostId(currentUser.getUserId());
            newHomestay.setStatus(1); // 默认营业状态
            newHomestay.setRating(0.0); // 初始评分
            
            // 调用Service添加民宿
            int result = homestayService.addHomestay(newHomestay);
            boolean success = result > 0;
            
            if (success) {
                JOptionPane.showMessageDialog(dialog, "添加成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadData(); // 刷新民宿列表
            } else {
                JOptionPane.showMessageDialog(dialog, "添加失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // 取消按钮事件
        cancelButton.addActionListener(e -> {
            dialog.dispose();
        });
        
        dialog.setVisible(true);
    }

    private void editHomestay() {
        int row = homestayTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要编辑的民宿", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int homestayId = (int) tableModel.getValueAt(row, 0);
        Homestay homestay = homestayService.getHomestayById(homestayId);
        
        if (homestay != null) {
            // 权限检查：只有管理员或民宿主本人可以编辑
            if (!"ADMIN".equals(currentUser.getRole()) && 
                homestay.getHostId() != currentUser.getUserId()) {
                JOptionPane.showMessageDialog(this, "您没有权限编辑此民宿", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // 创建编辑民宿对话框
            JDialog dialog = new JDialog(this, "编辑民宿", true);
            dialog.setSize(600, 500);
            dialog.setLocationRelativeTo(this);
            
            // 主面板
            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            mainPanel.setBackground(AppColors.LIGHT_PURPLE);
            
            // 表单面板
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBackground(AppColors.LIGHT_PURPLE);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            
            // 民宿名称
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            formPanel.add(new JLabel("民宿名称:"), gbc);
            
            gbc.gridx = 1;
            gbc.gridwidth = 2;
            JTextField nameField = new JTextField(homestay.getName());
            nameField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
            nameField.setPreferredSize(new Dimension(300, 25));
            formPanel.add(nameField, gbc);
            
            // 城市
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 1;
            formPanel.add(new JLabel("城市:"), gbc);
            
            gbc.gridx = 1;
            gbc.gridwidth = 2;
            JTextField cityField = new JTextField(homestay.getCity());
            cityField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
            cityField.setPreferredSize(new Dimension(300, 25));
            formPanel.add(cityField, gbc);
            
            // 地址
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 1;
            formPanel.add(new JLabel("地址:"), gbc);
            
            gbc.gridx = 1;
            gbc.gridwidth = 2;
            JTextField addressField = new JTextField(homestay.getAddress());
            addressField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
            addressField.setPreferredSize(new Dimension(300, 25));
            formPanel.add(addressField, gbc);
            
            // 电话
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridwidth = 1;
            formPanel.add(new JLabel("电话:"), gbc);
            
            gbc.gridx = 1;
            gbc.gridwidth = 2;
            JTextField phoneField = new JTextField(homestay.getPhone());
            phoneField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
            phoneField.setPreferredSize(new Dimension(300, 25));
            formPanel.add(phoneField, gbc);
            
            // 描述
            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.gridwidth = 1;
            formPanel.add(new JLabel("描述:"), gbc);
            
            gbc.gridx = 1;
            gbc.gridwidth = 2;
            JTextArea descriptionArea = new JTextArea(homestay.getDescription() != null ? homestay.getDescription() : "");
            descriptionArea.setFont(new Font("微软雅黑", Font.PLAIN, 13));
            descriptionArea.setLineWrap(true);
            descriptionArea.setWrapStyleWord(true);
            JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
            descriptionScrollPane.setPreferredSize(new Dimension(300, 80));
            formPanel.add(descriptionScrollPane, gbc);
            
            // 状态
            gbc.gridx = 0;
            gbc.gridy = 5;
            gbc.gridwidth = 1;
            formPanel.add(new JLabel("状态:"), gbc);
            
            gbc.gridx = 1;
            gbc.gridwidth = 2;
            String[] statusOptions = {"营业", "暂停"};
            JComboBox<String> statusCombo = new JComboBox<>(statusOptions);
            statusCombo.setFont(new Font("微软雅黑", Font.PLAIN, 13));
            statusCombo.setPreferredSize(new Dimension(300, 25));
            statusCombo.setSelectedIndex(homestay.getStatus() == 1 ? 0 : 1);
            formPanel.add(statusCombo, gbc);
            
            // 按钮面板
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            buttonPanel.setBackground(AppColors.LIGHT_PURPLE);
            
            JButton saveButton = new JButton("保存");
            JButton cancelButton = new JButton("取消");
            
            styleButton(saveButton);
            styleButton(cancelButton);
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            
            // 组装界面
            mainPanel.add(formPanel, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            dialog.setContentPane(mainPanel);
            
            // 保存按钮事件
            saveButton.addActionListener(e -> {
                // 获取输入
                String name = nameField.getText().trim();
                String city = cityField.getText().trim();
                String address = addressField.getText().trim();
                String phone = phoneField.getText().trim();
                String description = descriptionArea.getText().trim();
                int status = statusCombo.getSelectedIndex() == 0 ? 1 : 0;
                
                // 验证输入
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "民宿名称不能为空", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                if (city.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "城市不能为空", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                if (address.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "地址不能为空", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                if (phone.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "电话不能为空", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // 更新民宿信息
                homestay.setName(name);
                homestay.setCity(city);
                homestay.setAddress(address);
                homestay.setPhone(phone);
                homestay.setDescription(description);
                homestay.setStatus(status);
                
                // 调用Service更新民宿
                boolean success = homestayService.updateHomestay(homestay);
                
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "更新成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadData(); // 刷新民宿列表
                } else {
                    JOptionPane.showMessageDialog(dialog, "更新失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            // 取消按钮事件
            cancelButton.addActionListener(e -> {
                dialog.dispose();
            });
            
            dialog.setVisible(true);
        }
    }

    private void deleteHomestay() {
        int row = homestayTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的民宿", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int homestayId = (int) tableModel.getValueAt(row, 0);
        String homestayName = (String) tableModel.getValueAt(row, 1);
        
        // 权限检查
        Homestay homestay = homestayService.getHomestayById(homestayId);
        if (!"ADMIN".equals(currentUser.getRole()) && 
            homestay.getHostId() != currentUser.getUserId()) {
            JOptionPane.showMessageDialog(this, "您没有权限删除此民宿", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要删除民宿 " + homestayName + " 吗？\n删除后无法恢复！",
                "确认删除", JOptionPane.YES_NO_OPTION);
                
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = homestayService.deleteHomestay(homestayId);
            if (success) {
                JOptionPane.showMessageDialog(this, "删除成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openRoomManage() {
        int row = homestayTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一个民宿", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int homestayId = (int) tableModel.getValueAt(row, 0);
        String homestayName = (String) tableModel.getValueAt(row, 1);
        
        // 打开房间管理界面
        new RoomManageView(currentUser, homestayId, homestayName).setVisible(true);
    }
}