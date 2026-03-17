package com.booking.view;

import com.booking.model.User;
import com.booking.util.AppColors;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 订单列表界面
 */
public class OrderListView extends JFrame {

    private User currentUser;
    private String userRole; // ADMIN, HOST, GUEST
    private int targetId; // 如果是HOST则是homestayId，如果是GUEST则是userId

    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusFilter;
    private JTextField startDateField;
    private JTextField endDateField;
    private JButton searchButton;
    private JButton refreshButton;
    private JButton viewDetailButton;
    private JButton backButton;

    public OrderListView(User user, String role, int id) {
        this.currentUser = user;
        this.userRole = role;
        this.targetId = id;
        initUI();
        loadData();
    }

    private void initUI() {
        String title = "";
        switch (userRole) {
            case "ADMIN": title = "所有订单管理"; break;
            case "HOST": title = "我的民宿订单"; break;
            case "GUEST": title = "我的订单"; break;
        }
        setTitle(title + " - " + currentUser.getRealName());
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(AppColors.LIGHT_PURPLE);

        // 标题
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);

        // 筛选面板
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(AppColors.LIGHT_PURPLE);

        filterPanel.add(new JLabel("状态:"));
        String[] statuses = {"全部", "待支付", "已支付", "已确认", "已入住", "已完成", "已取消"};
        statusFilter = new JComboBox<>(statuses);
        statusFilter.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        statusFilter.setBackground(Color.WHITE);

        filterPanel.add(new JLabel("开始日期:"));
        startDateField = new JTextField(10);
        startDateField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        startDateField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));
        startDateField.setText(getFirstDayOfMonth());

        filterPanel.add(new JLabel("结束日期:"));
        endDateField = new JTextField(10);
        endDateField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        endDateField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));
        endDateField.setText(getCurrentDate());

        searchButton = new JButton("搜索");
        refreshButton = new JButton("刷新");

        styleButton(searchButton);
        styleButton(refreshButton);

        filterPanel.add(statusFilter);
        filterPanel.add(startDateField);
        filterPanel.add(endDateField);
        filterPanel.add(searchButton);
        filterPanel.add(refreshButton);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setBackground(AppColors.LIGHT_PURPLE);

        viewDetailButton = new JButton("查看详情");
        backButton = new JButton("返回");

        styleButton(viewDetailButton);
        styleButton(backButton);

        buttonPanel.add(viewDetailButton);
        buttonPanel.add(backButton);

        // 合并顶部面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(AppColors.LIGHT_PURPLE);
        topPanel.add(filterPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // 表格
        String[] columns = {"订单ID", "订单号", "民宿", "房间", "客人", "入住", "离店", "人数", "总价", "状态", "创建时间"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        orderTable = new JTable(tableModel);
        orderTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        orderTable.setRowHeight(25);
        orderTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 13));
        orderTable.getTableHeader().setBackground(AppColors.PRIMARY_PURPLE);
        orderTable.getTableHeader().setForeground(Color.BLACK);
        orderTable.setSelectionBackground(AppColors.HOVER_PURPLE);

        // 设置列宽
        orderTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        orderTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        orderTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        orderTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        orderTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        orderTable.getColumnModel().getColumn(5).setPreferredWidth(90);
        orderTable.getColumnModel().getColumn(6).setPreferredWidth(90);
        orderTable.getColumnModel().getColumn(7).setPreferredWidth(50);
        orderTable.getColumnModel().getColumn(8).setPreferredWidth(80);
        orderTable.getColumnModel().getColumn(9).setPreferredWidth(80);
        orderTable.getColumnModel().getColumn(10).setPreferredWidth(130);

        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppColors.PRIMARY_PURPLE));

        // 底部统计
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(AppColors.LIGHT_PURPLE);
        JLabel statsLabel = new JLabel("总订单数: 0 | 总金额: 0.00元");
        statsLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        statsLabel.setForeground(AppColors.DARK_PURPLE);
        bottomPanel.add(statsLabel);

        // 组装界面
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // 事件监听
        searchButton.addActionListener(e -> searchOrders());
        refreshButton.addActionListener(e -> loadData());
        viewDetailButton.addActionListener(e -> viewOrderDetail());
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

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    private String getFirstDayOfMonth() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(cal.getTime());
    }

    private void loadData() {
        // TODO: 从Service加载数据
        tableModel.setRowCount(0);

        // 临时测试数据
        Object[] row1 = {1, "202603150001", "云中山居", "101", "张三", "2026-03-15", "2026-03-17", 2, 776.00, "已完成", "2026-03-01"};
        Object[] row2 = {2, "202603160002", "海边小筑", "A01", "李四", "2026-03-16", "2026-03-18", 2, 1176.00, "已入住", "2026-03-02"};
        Object[] row3 = {3, "202603170003", "山里人家", "201", "王五", "2026-03-17", "2026-03-19", 2, 776.00, "待支付", "2026-03-03"};
        Object[] row4 = {4, "202603180004", "云中山居", "301", "赵六", "2026-03-18", "2026-03-20", 3, 1776.00, "已支付", "2026-03-04"};
        Object[] row5 = {5, "202603190005", "海边小筑", "B01", "张三", "2026-03-19", "2026-03-21", 2, 2576.00, "已确认", "2026-03-05"};

        tableModel.addRow(row1);
        tableModel.addRow(row2);
        tableModel.addRow(row3);
        tableModel.addRow(row4);
        tableModel.addRow(row5);

        updateStats();
    }

    private void updateStats() {
        int total = tableModel.getRowCount();
        double totalAmount = 0;
        for (int i = 0; i < total; i++) {
            totalAmount += (double) tableModel.getValueAt(i, 8);
        }

        JPanel bottomPanel = (JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(3);
        JLabel statsLabel = (JLabel) bottomPanel.getComponent(0);
        statsLabel.setText(String.format("总订单数: %d | 总金额: %.2f元", total, totalAmount));
    }

    private void searchOrders() {
        String status = (String) statusFilter.getSelectedItem();
        String startDate = startDateField.getText().trim();
        String endDate = endDateField.getText().trim();

        // TODO: 调用Service搜索
        StringBuilder msg = new StringBuilder("搜索条件:\n");
        msg.append("状态: ").append(status).append("\n");
        msg.append("日期范围: ").append(startDate).append(" 至 ").append(endDate);

        JOptionPane.showMessageDialog(this, msg.toString(), "搜索功能待实现", JOptionPane.INFORMATION_MESSAGE);
    }

    private void viewOrderDetail() {
        int row = orderTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要查看的订单", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int orderId = (int) tableModel.getValueAt(row, 0);
        String orderNo = (String) tableModel.getValueAt(row, 1);
        String homestayName = (String) tableModel.getValueAt(row, 2);
        String roomNumber = (String) tableModel.getValueAt(row, 3);
        String guestName = (String) tableModel.getValueAt(row, 4);
        String checkIn = (String) tableModel.getValueAt(row, 5);
        String checkOut = (String) tableModel.getValueAt(row, 6);
        int guests = (int) tableModel.getValueAt(row, 7);
        double price = (double) tableModel.getValueAt(row, 8);
        String status = (String) tableModel.getValueAt(row, 9);

        String detail = String.format(
                "订单详情\n\n" +
                        "订单ID: %d\n" +
                        "订单号: %s\n" +
                        "民宿: %s\n" +
                        "房间: %s\n" +
                        "客人: %s\n" +
                        "入住: %s\n" +
                        "离店: %s\n" +
                        "人数: %d\n" +
                        "总价: %.2f元\n" +
                        "状态: %s",
                orderId, orderNo, homestayName, roomNumber, guestName,
                checkIn, checkOut, guests, price, status);

        JOptionPane.showMessageDialog(this, detail, "订单详情", JOptionPane.INFORMATION_MESSAGE);
    }
}