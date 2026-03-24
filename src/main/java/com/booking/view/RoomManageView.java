package com.booking.view;

import com.booking.model.Room;
import com.booking.model.User;
import com.booking.service.RoomService;
import com.booking.service.HomestayService;
import com.booking.service.impl.RoomServiceImpl;
import com.booking.service.impl.HomestayServiceImpl;
import com.booking.util.AppColors;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * 房间管理界面
 */
public class RoomManageView extends JFrame {

    private User currentUser;
    private int homestayId;
    private String homestayName;
    private RoomService roomService;
    // private HomestayService homestayService;
    private List<Room> roomList;

    private JTable roomTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> typeFilter;
    private JComboBox<String> statusFilter;
    private JButton searchButton;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton backButton;
    
    // 分页相关变量
    private int currentPage = 1;
    private int pageSize = 10;
    private int totalPages = 1;
    
    // ========== 添加组件引用 ==========
    private JLabel statsLabel;  // 统计信息标签
    private JTextField pageField;  // 当前页码输入框
    private JLabel pageInfoLabel;  // 总页数标签
    // ================================

    public RoomManageView(User user, int homestayId, String homestayName) {
        this.currentUser = user;
        this.homestayId = homestayId;
        this.homestayName = homestayName;
        this.roomService = new RoomServiceImpl();
        // this.homestayService = new HomestayServiceImpl();
        initUI();
        loadData();
    }

    private void initUI() {
        setTitle("房间管理 - " + homestayName + " - " + currentUser.getRealName());
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(AppColors.LIGHT_PURPLE);

        // 标题
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(AppColors.LIGHT_PURPLE);

        JLabel titleLabel = new JLabel("房间管理 - " + homestayName, JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);

        backButton = new JButton("返回");
        styleButton(backButton);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(backButton, BorderLayout.EAST);

        // 筛选面板
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        filterPanel.setBackground(AppColors.LIGHT_PURPLE);

        JLabel roomNumberLabel = new JLabel("房间号:");
        roomNumberLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        roomNumberLabel.setForeground(Color.BLACK);
        filterPanel.add(roomNumberLabel);
        
        searchField = new JTextField(10);
        searchField.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        searchField.setForeground(Color.BLACK);
        searchField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));
        searchField.setPreferredSize(new Dimension(100, 22));
        filterPanel.add(searchField);

        JLabel typeLabel = new JLabel("房型:");
        typeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        typeLabel.setForeground(Color.BLACK);
        filterPanel.add(typeLabel);
        
        String[] types = {"全部", "单人间", "双人间", "大床房", "套房", "家庭房"};
        typeFilter = new JComboBox<>(types);
        typeFilter.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        typeFilter.setForeground(Color.BLACK);
        typeFilter.setBackground(Color.WHITE);
        typeFilter.setPreferredSize(new Dimension(80, 22));
        filterPanel.add(typeFilter);

        JLabel statusLabel = new JLabel("状态:");
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        statusLabel.setForeground(Color.BLACK);
        filterPanel.add(statusLabel);
        
        String[] statuses = {"全部", "可用", "已订", "维护"};
        statusFilter = new JComboBox<>(statuses);
        statusFilter.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        statusFilter.setForeground(Color.BLACK);
        statusFilter.setBackground(Color.WHITE);
        statusFilter.setPreferredSize(new Dimension(80, 22));
        filterPanel.add(statusFilter);

        searchButton = new JButton("搜索");
        styleButton(searchButton);
        filterPanel.add(searchButton);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        buttonPanel.setBackground(AppColors.LIGHT_PURPLE);

        addButton = new JButton("新增");
        editButton = new JButton("编辑");
        deleteButton = new JButton("删除");
        refreshButton = new JButton("刷新");

        styleButton(addButton);
        styleButton(editButton);
        styleButton(deleteButton);
        styleButton(refreshButton);

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        // 合并顶部面板
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(AppColors.LIGHT_PURPLE);
        topPanel.add(filterPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // 表格
        String[] columns = {"ID", "房间号", "房型", "床型", "面积", "可住人数", "价格", "状态"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        roomTable = new JTable(tableModel);
        roomTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        roomTable.setRowHeight(25);
        roomTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 13));
        roomTable.getTableHeader().setBackground(AppColors.PRIMARY_PURPLE);
        roomTable.getTableHeader().setForeground(Color.BLACK);
        roomTable.setSelectionBackground(AppColors.HOVER_PURPLE);

        // 设置列宽
        roomTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        roomTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        roomTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        roomTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        roomTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        roomTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        roomTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        roomTable.getColumnModel().getColumn(7).setPreferredWidth(80);

        JScrollPane scrollPane = new JScrollPane(roomTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppColors.PRIMARY_PURPLE));

        // 底部统计和分页
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(AppColors.LIGHT_PURPLE);
        
        // 统计信息 - 保存引用
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBackground(AppColors.LIGHT_PURPLE);
        statsLabel = new JLabel("总房间数: 0 | 可用: 0 | 已订: 0 | 维护: 0");
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
        northContainer.add(titlePanel, BorderLayout.NORTH);
        northContainer.add(topPanel, BorderLayout.CENTER);

        // 组装界面
        mainPanel.add(northContainer, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // 事件监听
        searchButton.addActionListener(e -> searchRooms());
        addButton.addActionListener(e -> addRoom());
        editButton.addActionListener(e -> editRoom());
        deleteButton.addActionListener(e -> deleteRoom());
        refreshButton.addActionListener(e -> loadData());
        backButton.addActionListener(e -> {
            dispose();
            new HomestayManageView(currentUser).setVisible(true);
        });
        
        // ========== 分页按钮事件 ==========
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
        button.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        button.setBackground(AppColors.BUTTON_PURPLE);
        button.setForeground(AppColors.DARK_PURPLE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        button.setPreferredSize(new Dimension(80, 22));

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
        typeFilter.setSelectedIndex(0); // 重置房型筛选
        statusFilter.setSelectedIndex(0); // 重置状态筛选
        currentPage = 1; // 重置到第一页
        
        // 获取该民宿的所有房间
        roomList = roomService.getRoomsByHomestayId(homestayId);
        
        // 计算总页数
        totalPages = (int) Math.ceil((double) roomList.size() / pageSize);
        
        // 分页显示
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, roomList.size());
        List<Room> paginatedRooms = roomList.subList(startIndex, endIndex);
        
        for (Room r : paginatedRooms) {
            Object[] row = {
                r.getRoomId(),
                r.getRoomNumber(),
                getRoomTypeName(r.getRoomType()),
                r.getBedType(),
                r.getArea(),
                r.getMaxPeople(),
                r.getPrice(),
                getStatusName(r.getStatus())
            };
            tableModel.addRow(row);
        }
        
        updateStats();
        updatePaginationInfo();
    }
    
    private void loadData(int page) {
        tableModel.setRowCount(0);
        currentPage = page;
        
        // 获取该民宿的所有房间
        roomList = roomService.getRoomsByHomestayId(homestayId);
        
        // 计算总页数
        totalPages = (int) Math.ceil((double) roomList.size() / pageSize);
        
        // 分页显示
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, roomList.size());
        List<Room> paginatedRooms = roomList.subList(startIndex, endIndex);
        
        for (Room r : paginatedRooms) {
            Object[] row = {
                r.getRoomId(),
                r.getRoomNumber(),
                getRoomTypeName(r.getRoomType()),
                r.getBedType(),
                r.getArea(),
                r.getMaxPeople(),
                r.getPrice(),
                getStatusName(r.getStatus())
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

    private String getRoomTypeName(String type) {
        switch (type) {
            case "SINGLE": return "单人间";
            case "DOUBLE": return "大床房";
            case "TWIN": return "双人间";
            case "SUITE": return "套房";
            case "FAMILY": return "家庭房";
            default: return type;
        }
    }
    
    private String getStatusName(String status) {
        switch (status) {
            case "AVAILABLE": return "可用";
            case "BOOKED": return "已订";
            case "MAINTENANCE": return "维护";
            default: return status;
        }
    }
    
    private String getStatusCode(String statusName) {
        switch (statusName) {
            case "可用": return "AVAILABLE";
            case "已订": return "BOOKED";
            case "维护": return "MAINTENANCE";
            default: return statusName;
        }
    }

    // ========== 简化的统计信息更新 ==========
    private void updateStats() {
        int total = tableModel.getRowCount();
        int available = 0;
        int booked = 0;
        int maintenance = 0;

        for (int i = 0; i < total; i++) {
            String status = (String) tableModel.getValueAt(i, 7);
            if ("可用".equals(status)) available++;
            else if ("已订".equals(status)) booked++;
            else if ("维护".equals(status)) maintenance++;
        }

        if (statsLabel != null) {
            statsLabel.setText(String.format("总房间数: %d | 可用: %d | 已订: %d | 维护: %d",
                    total, available, booked, maintenance));
        }
    }

    private void searchRooms() {
        String keyword = searchField.getText().trim();
        String type = (String) typeFilter.getSelectedItem();
        String status = (String) statusFilter.getSelectedItem();
        
        // 获取所有房间
        List<Room> allRooms = roomService.getRoomsByHomestayId(homestayId);
        
        // 筛选
        List<Room> filtered = allRooms.stream()
            .filter(r -> keyword.isEmpty() || r.getRoomNumber().contains(keyword))
            .filter(r -> "全部".equals(type) || getRoomTypeName(r.getRoomType()).equals(type))
            .filter(r -> "全部".equals(status) || r.getStatus().equals(getStatusCode(status)))
            .toList();
        
        // 更新表格
        tableModel.setRowCount(0);
        if (filtered.isEmpty()) {
            // 搜索不到数据，显示提示并清空搜索框
            JOptionPane.showMessageDialog(this, "未找到符合条件的房间", "提示", JOptionPane.INFORMATION_MESSAGE);
            searchField.setText(""); // 清空搜索框
            typeFilter.setSelectedIndex(0); // 重置房型筛选
            statusFilter.setSelectedIndex(0); // 重置状态筛选
            // 重新加载所有房间数据
            loadData();
        } else {
            for (Room r : filtered) {
                Object[] row = {
                    r.getRoomId(),
                    r.getRoomNumber(),
                    getRoomTypeName(r.getRoomType()),
                    r.getBedType(),
                    r.getArea(),
                    r.getMaxPeople(),
                    r.getPrice(),
                    getStatusName(r.getStatus())
                };
                tableModel.addRow(row);
            }
        }
        
        updateStats();
    }

    private void addRoom() {
        // 权限检查：只有民宿主和管理员可以添加
        if ("GUEST".equals(currentUser.getRole())) {
            JOptionPane.showMessageDialog(this, "您没有权限添加房间", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 创建添加房间对话框
        JDialog dialog = new JDialog(this, "添加房间", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(AppColors.LIGHT_PURPLE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 房间号
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("房间号:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        JTextField roomNumberField = new JTextField(15);
        roomNumberField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        panel.add(roomNumberField, gbc);
        
        // 房型
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("房型:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        String[] roomTypesCN = {"单人间", "大床房", "双人间", "套房", "家庭房"};
        JComboBox<String> roomTypeCombo = new JComboBox<>(roomTypesCN);
        roomTypeCombo.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        roomTypeCombo.setBackground(Color.WHITE);
        panel.add(roomTypeCombo, gbc);
        
        // 床型
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("床型:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        JTextField bedTypeField = new JTextField(15);
        bedTypeField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        panel.add(bedTypeField, gbc);
        
        // 面积
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("面积(㎡):"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        JTextField areaField = new JTextField(15);
        areaField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        panel.add(areaField, gbc);
        
        // 可住人数
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("可住人数:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        JTextField maxPeopleField = new JTextField(15);
        maxPeopleField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        panel.add(maxPeopleField, gbc);
        
        // 价格
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("价格(元):"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        JTextField priceField = new JTextField(15);
        priceField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        panel.add(priceField, gbc);
        
        // 状态
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("状态:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        String[] statusesCN = {"可用", "维护"};
        JComboBox<String> statusCombo = new JComboBox<>(statusesCN);
        statusCombo.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        statusCombo.setBackground(Color.WHITE);
        panel.add(statusCombo, gbc);
        
        // 描述
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("描述:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        JTextArea descriptionArea = new JTextArea(3, 15);
        descriptionArea.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        panel.add(scrollPane, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(AppColors.LIGHT_PURPLE);
        
        JButton saveButton = new JButton("保存");
        JButton cancelButton = new JButton("取消");
        
        styleButton(saveButton);
        styleButton(cancelButton);
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // 组装对话框
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setContentPane(contentPanel);
        
        // 保存按钮事件
        saveButton.addActionListener(e -> {
            try {
                // 验证输入
                String roomNumber = roomNumberField.getText().trim();
                if (roomNumber.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "请输入房间号", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // 转换房型（中文转英文）
                String[] roomTypesCNAdd = {"单人间", "大床房", "双人间", "套房", "家庭房"};
                String[] roomTypesENAdd = {"SINGLE", "DOUBLE", "TWIN", "SUITE", "FAMILY"};
                String roomTypeCNAdd = (String) roomTypeCombo.getSelectedItem();
                String roomType = roomTypesENAdd[0]; // 默认值
                for (int i = 0; i < roomTypesCNAdd.length; i++) {
                    if (roomTypesCNAdd[i].equals(roomTypeCNAdd)) {
                        roomType = roomTypesENAdd[i];
                        break;
                    }
                }
                
                String bedType = bedTypeField.getText().trim();
                if (bedType.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "请输入床型", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                int area = Integer.parseInt(areaField.getText().trim());
                int maxPeople = Integer.parseInt(maxPeopleField.getText().trim());
                double price = Double.parseDouble(priceField.getText().trim());
                
                // 转换状态（中文转英文）
                String[] statusesCNAdd = {"可用", "维护"};
                String[] statusesENAdd = {"AVAILABLE", "MAINTENANCE"};
                String statusCNAdd = (String) statusCombo.getSelectedItem();
                String status = statusesENAdd[0]; // 默认值
                for (int i = 0; i < statusesCNAdd.length; i++) {
                    if (statusesCNAdd[i].equals(statusCNAdd)) {
                        status = statusesENAdd[i];
                        break;
                    }
                }
                
                String description = descriptionArea.getText().trim();
                
                // 创建房间对象
                Room room = new Room();
                room.setHomestayId(homestayId);
                room.setRoomNumber(roomNumber);
                room.setRoomType(roomType);
                room.setBedType(bedType);
                room.setArea(area);
                room.setMaxPeople(maxPeople);
                room.setPrice(price);
                room.setStatus(status);
                room.setDescription(description);
                
                // 保存房间
                int result = roomService.addRoom(room);
                if (result == 1) {
                    JOptionPane.showMessageDialog(dialog, "添加成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "添加失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "请输入有效的数字", "提示", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        // 取消按钮事件
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.setVisible(true);
    }

    private void editRoom() {
        int row = roomTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要编辑的房间", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 权限检查
        if ("GUEST".equals(currentUser.getRole())) {
            JOptionPane.showMessageDialog(this, "您没有权限编辑房间", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int roomId = (int) tableModel.getValueAt(row, 0);
        String roomNumber = (String) tableModel.getValueAt(row, 1);
        
        // 获取房间信息
        Room room = roomService.getRoomById(roomId);
        if (room == null) {
            JOptionPane.showMessageDialog(this, "无法获取房间信息", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 创建编辑房间对话框
        JDialog dialog = new JDialog(this, "编辑房间 - " + roomNumber, true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(AppColors.LIGHT_PURPLE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 房间号
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("房间号:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        JTextField roomNumberField = new JTextField(room.getRoomNumber(), 15);
        roomNumberField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        panel.add(roomNumberField, gbc);
        
        // 房型
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("房型:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        String[] roomTypesCN = {"单人间", "大床房", "双人间", "套房", "家庭房"};
        String[] roomTypesEN = {"SINGLE", "DOUBLE", "TWIN", "SUITE", "FAMILY"};
        JComboBox<String> roomTypeCombo = new JComboBox<>(roomTypesCN);
        roomTypeCombo.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        roomTypeCombo.setBackground(Color.WHITE);
        // 设置默认值
        for (int i = 0; i < roomTypesEN.length; i++) {
            if (roomTypesEN[i].equals(room.getRoomType())) {
                roomTypeCombo.setSelectedIndex(i);
                break;
            }
        }
        panel.add(roomTypeCombo, gbc);
        
        // 床型
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("床型:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        JTextField bedTypeField = new JTextField(room.getBedType(), 15);
        bedTypeField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        panel.add(bedTypeField, gbc);
        
        // 面积
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("面积(㎡):"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        JTextField areaField = new JTextField(String.valueOf(room.getArea()), 15);
        areaField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        panel.add(areaField, gbc);
        
        // 可住人数
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("可住人数:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        JTextField maxPeopleField = new JTextField(String.valueOf(room.getMaxPeople()), 15);
        maxPeopleField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        panel.add(maxPeopleField, gbc);
        
        // 价格
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("价格(元):"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        JTextField priceField = new JTextField(String.valueOf(room.getPrice()), 15);
        priceField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        panel.add(priceField, gbc);
        
        // 状态
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("状态:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        String[] statusesCN = {"可用", "已订", "维护"};
        String[] statusesEN = {"AVAILABLE", "BOOKED", "MAINTENANCE"};
        JComboBox<String> statusCombo = new JComboBox<>(statusesCN);
        statusCombo.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        statusCombo.setBackground(Color.WHITE);
        // 设置默认值
        for (int i = 0; i < statusesEN.length; i++) {
            if (statusesEN[i].equals(room.getStatus())) {
                statusCombo.setSelectedIndex(i);
                break;
            }
        }
        panel.add(statusCombo, gbc);
        
        // 描述
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("描述:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        JTextArea descriptionArea = new JTextArea(room.getDescription() != null ? room.getDescription() : "", 3, 15);
        descriptionArea.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        panel.add(scrollPane, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(AppColors.LIGHT_PURPLE);
        
        JButton saveButton = new JButton("保存");
        JButton cancelButton = new JButton("取消");
        
        styleButton(saveButton);
        styleButton(cancelButton);
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // 组装对话框
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setContentPane(contentPanel);
        
        // 保存按钮事件
        saveButton.addActionListener(e -> {
            try {
                // 验证输入
                String roomNumberVal = roomNumberField.getText().trim();
                if (roomNumberVal.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "请输入房间号", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // 转换房型（中文转英文）
                String[] roomTypesCNSave = {"单人间", "大床房", "双人间", "套房", "家庭房"};
                String[] roomTypesENSave = {"SINGLE", "DOUBLE", "TWIN", "SUITE", "FAMILY"};
                String roomTypeCNVal = (String) roomTypeCombo.getSelectedItem();
                String roomType = roomTypesENSave[0]; // 默认值
                for (int i = 0; i < roomTypesCNSave.length; i++) {
                    if (roomTypesCNSave[i].equals(roomTypeCNVal)) {
                        roomType = roomTypesENSave[i];
                        break;
                    }
                }
                
                String bedType = bedTypeField.getText().trim();
                if (bedType.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "请输入床型", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                int area = Integer.parseInt(areaField.getText().trim());
                int maxPeople = Integer.parseInt(maxPeopleField.getText().trim());
                double price = Double.parseDouble(priceField.getText().trim());
                
                // 转换状态（中文转英文）
                String[] statusesCNSave = {"可用", "已订", "维护"};
                String[] statusesENSave = {"AVAILABLE", "BOOKED", "MAINTENANCE"};
                String statusCNVal = (String) statusCombo.getSelectedItem();
                String status = statusesENSave[0]; // 默认值
                for (int i = 0; i < statusesCNSave.length; i++) {
                    if (statusesCNSave[i].equals(statusCNVal)) {
                        status = statusesENSave[i];
                        break;
                    }
                }
                
                String description = descriptionArea.getText().trim();
                
                // 更新房间对象
                room.setRoomNumber(roomNumberVal);
                room.setRoomType(roomType);
                room.setBedType(bedType);
                room.setArea(area);
                room.setMaxPeople(maxPeople);
                room.setPrice(price);
                room.setStatus(status);
                room.setDescription(description);
                
                // 保存房间
                boolean success = roomService.updateRoom(room);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "更新成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "更新失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "请输入有效的数字", "提示", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        // 取消按钮事件
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.setVisible(true);
    }

    private void deleteRoom() {
        int row = roomTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的房间", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 权限检查
        if ("GUEST".equals(currentUser.getRole())) {
            JOptionPane.showMessageDialog(this, "您没有权限删除房间", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int roomId = (int) tableModel.getValueAt(row, 0);
        String roomNumber = (String) tableModel.getValueAt(row, 1);
        
        // 检查房间是否有订单
        try {
            // 创建ReservationDAO实例来检查房间是否有订单
            com.booking.dao.ReservationDAO reservationDAO = new com.booking.dao.impl.ReservationDAOImpl();
            List<com.booking.model.Reservation> reservations = reservationDAO.selectByRoomId(roomId);
            
            if (!reservations.isEmpty()) {
                JOptionPane.showMessageDialog(this, "删除失败：该房间已有订单关联", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "检查订单失败", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要删除房间 " + roomNumber + " 吗？",
                "确认删除", JOptionPane.YES_NO_OPTION);
                
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = roomService.deleteRoom(roomId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "删除成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "删除失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "删除失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}