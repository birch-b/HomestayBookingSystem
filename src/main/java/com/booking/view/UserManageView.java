package com.booking.view;

import com.booking.model.User;
import com.booking.util.AppColors;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户管理界面 - 管理员专用
 */
public class UserManageView extends JFrame {

    private User currentUser;
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

    public UserManageView(User user) {
        this.currentUser = user;
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
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);

        // 搜索面板
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(AppColors.LIGHT_PURPLE);

        searchPanel.add(new JLabel("关键词:"));
        searchField = new JTextField(15);
        searchField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));

        searchPanel.add(new JLabel("角色:"));
        String[] roles = {"全部", "管理员", "民宿主", "游客"};
        roleFilter = new JComboBox<>(roles);
        roleFilter.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        roleFilter.setBackground(Color.WHITE);

        searchButton = new JButton("搜索");
        styleButton(searchButton);

        searchPanel.add(searchField);
        searchPanel.add(roleFilter);
        searchPanel.add(searchButton);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setBackground(AppColors.LIGHT_PURPLE);

        addButton = new JButton("新增用户");
        editButton = new JButton("编辑");
        deleteButton = new JButton("删除");
        enableButton = new JButton("启用");
        disableButton = new JButton("禁用");
        refreshButton = new JButton("刷新");
        backButton = new JButton("返回");

        styleButton(addButton);
        styleButton(editButton);
        styleButton(deleteButton);
        styleButton(enableButton);
        styleButton(disableButton);
        styleButton(refreshButton);
        styleButton(backButton);

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(enableButton);
        buttonPanel.add(disableButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);

        // 合并顶部面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(AppColors.LIGHT_PURPLE);
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

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

        // 底部统计
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(AppColors.LIGHT_PURPLE);
        JLabel statsLabel = new JLabel("总用户数: 0 | 管理员: 0 | 民宿主: 0 | 游客: 0");
        statsLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        statsLabel.setForeground(AppColors.DARK_PURPLE);
        bottomPanel.add(statsLabel);

        // 组装界面
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // 添加事件监听
        searchButton.addActionListener(e -> searchUsers());
        addButton.addActionListener(e -> addUser());
        editButton.addActionListener(e -> editUser());
        deleteButton.addActionListener(e -> deleteUser());
        enableButton.addActionListener(e -> enableUser());
        disableButton.addActionListener(e -> disableUser());
        refreshButton.addActionListener(e -> loadData());
        backButton.addActionListener(e -> dispose());
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        button.setBackground(AppColors.BUTTON_PURPLE);
        button.setForeground(Color.WHITE);
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
        // TODO: 从UserService加载数据
        tableModel.setRowCount(0);

        // 临时测试数据
        Object[] row1 = {1, "admin", "管理员", "管理员", "13800001111", "admin@test.com", "正常", "2026-01-01"};
        Object[] row2 = {2, "host1", "王老板", "民宿主", "13912345678", "wang@test.com", "正常", "2026-01-02"};
        Object[] row3 = {3, "guest1", "张三", "游客", "15812345678", "zhang@test.com", "正常", "2026-01-03"};
        Object[] row4 = {4, "guest2", "李四", "游客", "15987654321", "li@test.com", "禁用", "2026-01-04"};

        tableModel.addRow(row1);
        tableModel.addRow(row2);
        tableModel.addRow(row3);
        tableModel.addRow(row4);

        updateStats();
    }

    private void updateStats() {
        int total = tableModel.getRowCount();
        int admin = 0, host = 0, guest = 0;

        for (int i = 0; i < total; i++) {
            String role = (String) tableModel.getValueAt(i, 3);
            if ("管理员".equals(role)) admin++;
            else if ("民宿主".equals(role)) host++;
            else if ("游客".equals(role)) guest++;
        }

        JPanel bottomPanel = (JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(3);
        JLabel statsLabel = (JLabel) bottomPanel.getComponent(0);
        statsLabel.setText(String.format("总用户数: %d | 管理员: %d | 民宿主: %d | 游客: %d",
                total, admin, host, guest));
    }

    private void searchUsers() {
        String keyword = searchField.getText().trim();
        String role = (String) roleFilter.getSelectedItem();

        StringBuilder msg = new StringBuilder("搜索条件:\n");
        if (!keyword.isEmpty()) msg.append("关键词: ").append(keyword).append("\n");
        if (!"全部".equals(role)) msg.append("角色: ").append(role);

        JOptionPane.showMessageDialog(this, msg.toString(), "搜索功能待实现", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addUser() {
        JOptionPane.showMessageDialog(this, "新增用户功能待实现", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private void editUser() {
        int row = userTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要编辑的用户", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String username = (String) tableModel.getValueAt(row, 1);
        JOptionPane.showMessageDialog(this, "编辑用户: " + username + " (ID:" + id + ")",
                "提示", JOptionPane.INFORMATION_MESSAGE);
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

        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要删除用户 " + username + " 吗？",
                "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.removeRow(row);
            updateStats();
            JOptionPane.showMessageDialog(this, "删除成功（演示模式）", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void enableUser() {
        int row = userTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要启用的用户", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String status = (String) tableModel.getValueAt(row, 6);
        if ("正常".equals(status)) {
            JOptionPane.showMessageDialog(this, "该用户已是正常状态", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        tableModel.setValueAt("正常", row, 6);
        JOptionPane.showMessageDialog(this, "用户已启用", "提示", JOptionPane.INFORMATION_MESSAGE);
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

        String status = (String) tableModel.getValueAt(row, 6);
        if ("禁用".equals(status)) {
            JOptionPane.showMessageDialog(this, "该用户已是禁用状态", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要禁用用户 " + username + " 吗？",
                "确认禁用", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.setValueAt("禁用", row, 6);
            JOptionPane.showMessageDialog(this, "用户已禁用", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}