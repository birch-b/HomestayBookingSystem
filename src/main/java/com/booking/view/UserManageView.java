package com.booking.view;

import com.booking.model.User;
import com.booking.model.Reservation;
import com.booking.service.UserService;
import com.booking.service.ReservationService;
import com.booking.service.impl.UserServiceImpl;
import com.booking.service.impl.ReservationServiceImpl;
import com.booking.util.AppColors;
import com.booking.util.MD5Util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户管理界面 - 管理员专用
 */
public class UserManageView extends JFrame {
    
    private User currentUser;
    private UserService userService;
    private List<User> userList;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> roleFilter;
    private JButton searchButton;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton enableButton;
    private JButton disableButton;
    private JButton refreshButton;
    private JButton backButton;
    
    // 分页相关变量
    private int currentPage = 1;
    private int pageSize = 10;
    private int totalPages = 1;
    
    // 添加这两个成员变量，保存组件引用
    private JLabel statsLabel;  // 统计信息标签
    private JTextField pageField;  // 当前页码输入框
    private JLabel pageInfoLabel;  // 总页数标签

    public UserManageView(User user) {
        this.currentUser = user;
        this.userService = new UserServiceImpl();
        this.userList = new ArrayList<>();
        initUI();
        loadData();
    }

    private void initUI() {
        setTitle("用户管理 - " + currentUser.getRealName());
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(AppColors.LIGHT_PURPLE);

        // 标题
        JLabel titleLabel = new JLabel("用户管理", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);

        // 搜索面板
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        searchPanel.setBackground(AppColors.LIGHT_PURPLE);

        JLabel keywordLabel = new JLabel("关键词:");
        keywordLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        keywordLabel.setForeground(Color.BLACK);
        searchPanel.add(keywordLabel);
        
        searchField = new JTextField(10);
        searchField.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        searchField.setForeground(Color.BLACK);
        searchField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));
        searchField.setPreferredSize(new Dimension(100, 22));
        searchPanel.add(searchField);

        JLabel roleLabel = new JLabel("角色:");
        roleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        roleLabel.setForeground(Color.BLACK);
        searchPanel.add(roleLabel);
        
        String[] roles = {"全部", "管理员", "民宿主", "游客"};
        roleFilter = new JComboBox<>(roles);
        roleFilter.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        roleFilter.setForeground(Color.BLACK);
        roleFilter.setBackground(Color.WHITE);
        roleFilter.setPreferredSize(new Dimension(80, 22));
        searchPanel.add(roleFilter);

        searchButton = new JButton("搜索");
        styleButton(searchButton);
        searchPanel.add(searchButton);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        buttonPanel.setBackground(AppColors.LIGHT_PURPLE);

        addButton = new JButton("新增");
        JButton batchAddButton = new JButton("批量添加");
        editButton = new JButton("编辑");
        deleteButton = new JButton("删除");
        enableButton = new JButton("启用");
        disableButton = new JButton("禁用");
        refreshButton = new JButton("刷新");
        backButton = new JButton("返回");

        styleButton(addButton);
        styleButton(batchAddButton);
        styleButton(editButton);
        styleButton(deleteButton);
        styleButton(enableButton);
        styleButton(disableButton);
        styleButton(refreshButton);
        styleButton(backButton);

        buttonPanel.add(addButton);
        buttonPanel.add(batchAddButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(enableButton);
        buttonPanel.add(disableButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);

        // 合并顶部面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(AppColors.LIGHT_PURPLE);
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);

        // 表格
        String[] columns = {"ID", "用户名", "真实姓名", "角色", "手机号", "邮箱", "状态", "创建时间"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userTable = new JTable(tableModel);
        userTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        userTable.setRowHeight(25);
        userTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 13));
        userTable.getTableHeader().setBackground(AppColors.PRIMARY_PURPLE);
        userTable.getTableHeader().setForeground(Color.BLACK);
        userTable.setSelectionBackground(AppColors.HOVER_PURPLE);

        // 设置列宽
        userTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        userTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        userTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        userTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        userTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        userTable.getColumnModel().getColumn(5).setPreferredWidth(150);
        userTable.getColumnModel().getColumn(6).setPreferredWidth(60);
        userTable.getColumnModel().getColumn(7).setPreferredWidth(130);

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppColors.PRIMARY_PURPLE));

        // 底部统计和分页
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(AppColors.LIGHT_PURPLE);
        
        // 统计信息 - 保存statsLabel的引用
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBackground(AppColors.LIGHT_PURPLE);
        statsLabel = new JLabel("总用户数: 0 | 管理员: 0 | 民宿主: 0 | 游客: 0");
        statsLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        statsLabel.setForeground(Color.BLACK);
        statsPanel.add(statsLabel);
        
        // 分页组件 - 保存分页组件的引用
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        paginationPanel.setBackground(AppColors.LIGHT_PURPLE);
        
        JButton firstPageButton = new JButton("首页");
        JButton prevPageButton = new JButton("上一页");
        pageField = new JTextField(4);  // 保存引用
        pageField.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        pageField.setHorizontalAlignment(JTextField.CENTER);
        pageField.setText("1");
        pageField.setPreferredSize(new Dimension(40, 22));
        
        pageInfoLabel = new JLabel("/ 1");  // 保存引用
        pageInfoLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
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

        // 修改布局：将标题和顶部面板合并到一个容器中
        JPanel northContainer = new JPanel(new BorderLayout());
        northContainer.setBackground(AppColors.LIGHT_PURPLE);
        northContainer.add(titleLabel, BorderLayout.NORTH);
        northContainer.add(topPanel, BorderLayout.CENTER);

        // 组装界面
        mainPanel.add(northContainer, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // 添加事件监听
        searchButton.addActionListener(e -> searchUsers());
        addButton.addActionListener(e -> addUser());
        batchAddButton.addActionListener(e -> batchAddUsers());
        editButton.addActionListener(e -> editUser());
        deleteButton.addActionListener(e -> deleteUser());
        enableButton.addActionListener(e -> enableUser());
        disableButton.addActionListener(e -> disableUser());
        refreshButton.addActionListener(e -> loadData());
        backButton.addActionListener(e -> dispose());
        
        // 分页按钮事件 - 直接使用按钮变量，不需要再查找
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
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.PRIMARY_PURPLE, 1, true),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.setPreferredSize(new Dimension(85, 28));

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
        roleFilter.setSelectedIndex(0); // 重置角色筛选
        currentPage = 1; // 重置到第一页
        
        // 从Service获取最新数据
        userList = userService.getAllUsers(currentPage, pageSize);
        
        // 计算总页数
        long totalCount = userService.getTotalUserCount();
        totalPages = (int) Math.ceil((double) totalCount / pageSize);
        
        for (User u : userList) {
            String status = u.getStatus() == 1 ? "正常" : "禁用";
            String createTime = u.getCreateTime() != null ? 
                u.getCreateTime().toString().substring(0, 10) : "未知";
            
            Object[] row = {
                u.getUserId(),
                u.getUsername(),
                u.getRealName(),
                getRoleName(u.getRole()),
                u.getPhone(),
                u.getEmail(),
                status,
                createTime
            };
            tableModel.addRow(row);
        }
        
        updateStats();
        updatePaginationInfo();
    }
    
    private void loadData(int page) {
        tableModel.setRowCount(0);
        currentPage = page;
        
        // 从Service获取指定页的数据
        userList = userService.getAllUsers(currentPage, pageSize);
        
        for (User u : userList) {
            String status = u.getStatus() == 1 ? "正常" : "禁用";
            String createTime = u.getCreateTime() != null ? 
                u.getCreateTime().toString().substring(0, 10) : "未知";
            
            Object[] row = {
                u.getUserId(),
                u.getUsername(),
                u.getRealName(),
                getRoleName(u.getRole()),
                u.getPhone(),
                u.getEmail(),
                status,
                createTime
            };
            tableModel.addRow(row);
        }
        
        updatePaginationInfo();
    }
    
    private void updatePaginationInfo() {
        // 直接使用保存的引用更新分页信息
        if (pageField != null) {
            pageField.setText(String.valueOf(currentPage));
        }
        if (pageInfoLabel != null) {
            pageInfoLabel.setText("/ " + totalPages);
        }
    }

    private String getRoleName(String role) {
        switch (role) {
            case "ADMIN": return "管理员";
            case "HOST": return "民宿主";
            case "GUEST": return "游客";
            default: return role;
        }
    }

    private void updateStats() {
        // 获取各角色用户数量
        List<User> admins = userService.getAdminList();
        List<User> hosts = userService.getHostList();
        List<User> guests = userService.getGuestList();
        int totalCount = admins.size() + hosts.size() + guests.size();
        
        // 直接使用保存的statsLabel引用更新文本
        if (statsLabel != null) {
            statsLabel.setText(String.format("总用户数: %d | 管理员: %d | 民宿主: %d | 游客: %d",
                    totalCount, admins.size(), hosts.size(), guests.size()));
        }
    }

    private void searchUsers() {
        String usernameKeyword = searchField.getText().trim();
        String role = (String) roleFilter.getSelectedItem();
        
        // 调用Service获取所有用户
        List<User> allUsers = userService.getAllUsers(1, 100);
        
        // 按用户名和角色筛选
        List<User> searchResult = new ArrayList<>();
        for (User user : allUsers) {
            // 按用户名筛选（包含关系）
            if (user.getUsername().contains(usernameKeyword)) {
                // 按角色筛选
                if ("全部".equals(role)) {
                    searchResult.add(user);
                } else {
                    String roleCode = "";
                    switch (role) {
                        case "管理员": roleCode = "ADMIN"; break;
                        case "民宿主": roleCode = "HOST"; break;
                        case "游客": roleCode = "GUEST"; break;
                    }
                    if (roleCode.equals(user.getRole())) {
                        searchResult.add(user);
                    }
                }
            }
        }
        
        // 更新表格
        tableModel.setRowCount(0);
        if (searchResult.isEmpty()) {
            // 搜索不到数据，显示提示并清空搜索框
            JOptionPane.showMessageDialog(this, "未找到符合条件的用户", "提示", JOptionPane.INFORMATION_MESSAGE);
            searchField.setText(""); // 清空搜索框
            // 重新加载所有用户数据
            loadData();
        } else {
            for (User u : searchResult) {
                String status = u.getStatus() == 1 ? "正常" : "禁用";
                Object[] row = {
                    u.getUserId(),
                    u.getUsername(),
                    u.getRealName(),
                    getRoleName(u.getRole()),
                    u.getPhone(),
                    u.getEmail(),
                    status,
                    u.getCreateTime() != null ? u.getCreateTime().toString().substring(0, 10) : ""
                };
                tableModel.addRow(row);
            }
        }
        
        updateStats();
    }


    private void addUser() {
        // 创建添加用户对话框
        JDialog dialog = new JDialog(this, "添加用户", true);
        dialog.setSize(600, 450);
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
        
        // 用户名
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("用户名:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextField usernameField = new JTextField(30);
        usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        usernameField.setPreferredSize(new Dimension(300, 25));
        formPanel.add(usernameField, gbc);
        
        // 密码
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("密码:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JPasswordField passwordField = new JPasswordField(30);
        passwordField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        passwordField.setPreferredSize(new Dimension(300, 25));
        formPanel.add(passwordField, gbc);
        
        // 真实姓名
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("真实姓名:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextField realNameField = new JTextField(30);
        realNameField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        realNameField.setPreferredSize(new Dimension(300, 25));
        formPanel.add(realNameField, gbc);
        
        // 角色
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("角色:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        String[] rolesCN = {"管理员", "民宿主", "游客"};
        JComboBox<String> roleCombo = new JComboBox<>(rolesCN);
        roleCombo.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        roleCombo.setPreferredSize(new Dimension(300, 25));
        roleCombo.setSelectedIndex(2); // 默认游客
        formPanel.add(roleCombo, gbc);
        
        // 手机号
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("手机号:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextField phoneField = new JTextField(30);
        phoneField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        phoneField.setPreferredSize(new Dimension(300, 25));
        formPanel.add(phoneField, gbc);
        
        // 邮箱
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("邮箱:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextField emailField = new JTextField(30);
        emailField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        emailField.setPreferredSize(new Dimension(300, 25));
        formPanel.add(emailField, gbc);
        
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
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String realName = realNameField.getText().trim();
            
            // 转换角色（中文转英文）
            String[] rolesCNAdd = {"管理员", "民宿主", "游客"};
            String[] rolesENAdd = {"ADMIN", "HOST", "GUEST"};
            String roleCNAdd = (String) roleCombo.getSelectedItem();
            String role = rolesENAdd[2]; // 默认游客
            for (int i = 0; i < rolesCNAdd.length; i++) {
                if (rolesCNAdd[i].equals(roleCNAdd)) {
                    role = rolesENAdd[i];
                    break;
                }
            }
            
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            
            // 验证输入
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "用户名不能为空", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "密码不能为空", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (realName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "真实姓名不能为空", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // 检查用户名是否已存在
            if (userService.isUsernameExists(username)) {
                JOptionPane.showMessageDialog(dialog, "用户名已存在", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // 创建用户对象
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setRealName(realName);
            newUser.setRole(role);
            newUser.setPhone(phone);
            newUser.setEmail(email);
            newUser.setStatus(1); // 默认正常状态
            
            // 调用Service添加用户
            int result = userService.register(newUser);
            
            if (result == 1) {
                JOptionPane.showMessageDialog(dialog, "添加成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadData(); // 刷新用户列表
            } else if (result == -1) {
                JOptionPane.showMessageDialog(dialog, "用户名已存在", "提示", JOptionPane.WARNING_MESSAGE);
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

    private void editUser() {
        int row = userTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要编辑的用户", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(row, 0);
        User user = userService.getUserById(userId);
        
        if (user != null) {
            // 创建编辑用户对话框
            JDialog dialog = new JDialog(this, "编辑用户", true);
            dialog.setSize(600, 450);
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
            gbc.weightx = 1.0;
            
            // 用户名
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            formPanel.add(new JLabel("用户名:"), gbc);
            
            gbc.gridx = 1;
            gbc.gridwidth = 2;
            JTextField usernameField = new JTextField(user.getUsername());
            usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
            usernameField.setPreferredSize(new Dimension(300, 25));
            usernameField.setEditable(true); // 用户名可以编辑
            formPanel.add(usernameField, gbc);
            
            // 密码
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 1;
            formPanel.add(new JLabel("密码:"), gbc);
            
            gbc.gridx = 1;
            gbc.gridwidth = 2;
            JPasswordField passwordField = new JPasswordField();
            passwordField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
            passwordField.setPreferredSize(new Dimension(300, 25));
            formPanel.add(passwordField, gbc);
            
            // 真实姓名
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 1;
            formPanel.add(new JLabel("真实姓名:"), gbc);
            
            gbc.gridx = 1;
            gbc.gridwidth = 2;
            JTextField realNameField = new JTextField(user.getRealName());
            realNameField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
            realNameField.setPreferredSize(new Dimension(300, 25));
            formPanel.add(realNameField, gbc);
            
            // 角色
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("角色:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        String[] rolesCN = {"管理员", "民宿主", "游客"};
        JComboBox<String> roleCombo = new JComboBox<>(rolesCN);
        roleCombo.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        roleCombo.setPreferredSize(new Dimension(300, 25));
        // 设置默认值
        String[] rolesEN = {"ADMIN", "HOST", "GUEST"};
        for (int i = 0; i < rolesEN.length; i++) {
            if (rolesEN[i].equals(user.getRole())) {
                roleCombo.setSelectedIndex(i);
                break;
            }
        }
        formPanel.add(roleCombo, gbc);
            
            // 手机号
            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.gridwidth = 1;
            formPanel.add(new JLabel("手机号:"), gbc);
            
            gbc.gridx = 1;
            gbc.gridwidth = 2;
            JTextField phoneField = new JTextField(user.getPhone());
            phoneField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
            phoneField.setPreferredSize(new Dimension(300, 25));
            formPanel.add(phoneField, gbc);
            
            // 邮箱
            gbc.gridx = 0;
            gbc.gridy = 5;
            gbc.gridwidth = 1;
            formPanel.add(new JLabel("邮箱:"), gbc);
            
            gbc.gridx = 1;
            gbc.gridwidth = 2;
            JTextField emailField = new JTextField(user.getEmail());
            emailField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
            emailField.setPreferredSize(new Dimension(300, 25));
            formPanel.add(emailField, gbc);
            
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
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String realName = realNameField.getText().trim();
            
            // 转换角色（中文转英文）
            String[] rolesCNEdit = {"管理员", "民宿主", "游客"};
            String[] rolesENEdit = {"ADMIN", "HOST", "GUEST"};
            String roleCNEdit = (String) roleCombo.getSelectedItem();
            String role = rolesENEdit[2]; // 默认游客
            for (int i = 0; i < rolesCNEdit.length; i++) {
                if (rolesCNEdit[i].equals(roleCNEdit)) {
                    role = rolesENEdit[i];
                    break;
                }
            }
            
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
                
                // 验证输入
                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "用户名不能为空", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                if (realName.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "真实姓名不能为空", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // 更新用户信息
                user.setUsername(username);
                user.setRealName(realName);
                user.setRole(role);
                user.setPhone(phone);
                user.setEmail(email);
                
                // 如果密码不为空，更新密码（需要加密）
                if (!password.isEmpty()) {
                    user.setPassword(MD5Util.md5(password));
                }
                
                // 调用Service更新用户
                boolean success = userService.updateUser(user);
                
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "更新成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadData(); // 刷新用户列表
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

    private void deleteUser() {
        int row = userTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的用户", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = (String) tableModel.getValueAt(row, 1);
        if (username.equals("admin")) {
            JOptionPane.showMessageDialog(this, "不能删除管理员账号", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(row, 0);
        
        // 检查用户是否有相关的订单记录
        try {
            ReservationService reservationService = new ReservationServiceImpl();
            List<Reservation> userOrders = reservationService.getUserReservations(userId, 1, 1);
            
            if (userOrders != null && !userOrders.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "无法删除用户 " + username + "，因为该用户有相关的订单记录。\n" +
                    "请先删除或处理该用户的订单，然后再尝试删除用户。",
                    "删除失败", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (Exception e) {
            // 如果检查失败，仍然尝试删除，但记录错误
            System.err.println("检查用户订单时出错: " + e.getMessage());
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要删除用户 " + username + " 吗？",
                "确认删除", JOptionPane.YES_NO_OPTION);
                
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = userService.deleteUser(userId);
            if (success) {
                JOptionPane.showMessageDialog(this, "删除成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadData();  // 刷新列表
            } else {
                JOptionPane.showMessageDialog(this, "删除失败，请检查该用户是否有相关的订单记录", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void enableUser() {
        int row = userTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要启用的用户", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(row, 0);
        boolean success = userService.updateUserStatus(userId, 1);
        
        if (success) {
            JOptionPane.showMessageDialog(this, "用户已启用", "成功", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "操作失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void disableUser() {
        int row = userTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要禁用的用户", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = (String) tableModel.getValueAt(row, 1);
        if (username.equals("admin")) {
            JOptionPane.showMessageDialog(this, "不能禁用管理员账号", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要禁用用户 " + username + " 吗？",
                "确认禁用", JOptionPane.YES_NO_OPTION);
                
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = userService.updateUserStatus(userId, 0);
            if (success) {
                JOptionPane.showMessageDialog(this, "用户已禁用", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "操作失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void batchAddUsers() {
        JDialog dialog = new JDialog(this, "批量添加用户", true);
        dialog.setSize(550, 500);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(AppColors.LIGHT_PURPLE);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(AppColors.LIGHT_PURPLE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("添加数量:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextField countField = new JTextField(10);
        countField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        countField.setPreferredSize(new Dimension(150, 25));
        countField.setText("5");
        formPanel.add(countField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("用户角色:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        String[] rolesCN = {"管理员", "民宿主", "游客"};
        JComboBox<String> roleCombo = new JComboBox<>(rolesCN);
        roleCombo.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        roleCombo.setPreferredSize(new Dimension(150, 25));
        roleCombo.setSelectedIndex(0);
        formPanel.add(roleCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("用户名生成:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        String[] namePatterns = {"序号 (admin1, admin2)", "随机字母 (xy2a8)", "时间戳 (user_123456789)"};
        JComboBox<String> patternCombo = new JComboBox<>(namePatterns);
        patternCombo.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        patternCombo.setPreferredSize(new Dimension(280, 25));
        patternCombo.setSelectedIndex(0);
        formPanel.add(patternCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("基础前缀:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextField baseUsernameField = new JTextField(20);
        baseUsernameField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        baseUsernameField.setPreferredSize(new Dimension(150, 25));
        baseUsernameField.setText("admin");
        formPanel.add(baseUsernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("统一密码:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        passwordField.setPreferredSize(new Dimension(150, 25));
        passwordField.setText("123456");
        formPanel.add(passwordField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(AppColors.LIGHT_PURPLE);

        JButton saveButton = new JButton("批量添加");
        JButton cancelButton = new JButton("取消");

        styleButton(saveButton);
        styleButton(cancelButton);

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(mainPanel);

        saveButton.addActionListener(e -> {
            try {
                int count = Integer.parseInt(countField.getText().trim());
                if (count <= 0 || count > 100) {
                    JOptionPane.showMessageDialog(dialog, "添加数量必须在1-100之间", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String basePrefix = baseUsernameField.getText().trim();
                if (basePrefix.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "请输入基础前缀", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String password = new String(passwordField.getPassword());
                if (password.length() < 6) {
                    JOptionPane.showMessageDialog(dialog, "密码长度至少6位", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String roleCN = (String) roleCombo.getSelectedItem();
                String[] rolesCNAdd = {"管理员", "民宿主", "游客"};
                String[] rolesENAdd = {"ADMIN", "HOST", "GUEST"};
                String role = "GUEST";
                for (int i = 0; i < rolesCNAdd.length; i++) {
                    if (rolesCNAdd[i].equals(roleCN)) {
                        role = rolesENAdd[i];
                        break;
                    }
                }

                int patternIndex = patternCombo.getSelectedIndex();
                int successCount = 0;
                int failCount = 0;
                int attemptCount = 0;
                int maxAttempts = count * 10; // 最多尝试10倍数量来避免无限循环

                List<String> generatedUsernames = new ArrayList<>(); // 记录已生成的用户名，避免重复

                while (successCount < count && attemptCount < maxAttempts) {
                    attemptCount++;
                    
                    // 生成用户名
                    String username;
                    switch (patternIndex) {
                        case 1: // 随机字母
                            username = generateRandomUsername(basePrefix, 5);
                            break;
                        case 2: // 时间戳
                            username = basePrefix + "_" + System.currentTimeMillis() + "_" + attemptCount;
                            break;
                        default: // 序号（默认）
                            username = basePrefix + (successCount + 1);
                    }

                    // 检查是否在本次生成中已重复
                    if (generatedUsernames.contains(username)) {
                        continue;
                    }

                    // 检查数据库中是否已存在该用户名
                    User existingUser = null;
                    try {
                        existingUser = userService.getUserByUsername(username);
                    } catch (Exception ex) {
                        // 忽略查询异常，继续尝试
                    }
                    
                    if (existingUser != null) {
                        if (patternIndex == 0) { // 如果是序号模式遇到重复，改用随机模式
                            username = generateRandomUsername(basePrefix, 5);
                        } else {
                            continue; // 其他模式则跳过本次尝试
                        }
                    }

                    // 生成用户信息
                    User user = new User();
                    user.setUsername(username);
                    user.setPassword(password);
                    user.setRealName(basePrefix + (successCount + 1));
                    user.setRole(role);
                    user.setPhone("138" + String.format("%08d", (int)(Math.random() * 100000000)));
                    user.setEmail(username + "@example.com");
                    user.setStatus(1);

                    // 注册用户
                    int result = userService.register(user);
                    if (result > 0) {
                        successCount++;
                        generatedUsernames.add(username);
                    } else {
                        failCount++;
                    }
                }

                String message = String.format("批量添加完成！\n成功: %d 个\n失败: %d 个", successCount, failCount);
                if (successCount < count) {
                    message += "\n(部分用户因用户名冲突未生成)";
                }
                
                JOptionPane.showMessageDialog(dialog, message, "批量添加结果", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadData();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "请输入有效的数量", "提示", JOptionPane.WARNING_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    /**
     * 生成随机用户名
     * @param prefix 前缀
     * @param length 随机部分长度
     * @return 生成的用户名
     */
    private String generateRandomUsername(String prefix, int length) {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(prefix);
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}