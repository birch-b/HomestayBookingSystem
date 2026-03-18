package com.booking.view;
import com.booking.model.Reservation;
import com.booking.model.User;
import com.booking.service.ReservationService;
import com.booking.service.impl.ReservationServiceImpl;
import com.booking.util.AppColors;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 订单列表界面
 */
public class OrderListView extends JFrame {

    private User currentUser;
    private String userRole; // ADMIN, HOST, GUEST
    private int targetId; // 如果是HOST则是homestayId，如果是GUEST则是userId
    private ReservationService reservationService;
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
       this.reservationService = new ReservationServiceImpl();
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
        titleLabel.setForeground(AppColors.PRIMARY_PURPLE);

        // 筛选面板
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(AppColors.LIGHT_PURPLE);

        filterPanel.add(new JLabel("状态:"));
        String[] statuses = {"全部", "PENDING", "PAID", "CONFIRMED", "CHECKED_IN", "COMPLETED", "CANCELLED"};
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
        orderTable.getTableHeader().setForeground(Color.WHITE);
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
        mainPanel.add(topPanel, BorderLayout.NORTH);
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
    
    private String getStatusName(String status) {
        switch (status) {
            case "PENDING": return "待支付";
            case "PAID": return "已支付";
            case "CONFIRMED": return "已确认";
            case "CHECKED_IN": return "已入住";
            case "COMPLETED": return "已完成";
            case "CANCELLED": return "已取消";
            default: return status;
        }
    }

    private void loadData() {
        // TODO: 从Service加载数据
        tableModel.setRowCount(0);
        List<Reservation> orderList=null;
        // 根据角色加载不同数据
        switch (userRole) {
            case "ADMIN":
                // 管理员看所有订单（不分页，取前100条）
                orderList = reservationService.searchReservations(null, null, null, null, 1, 100);
                break;
            case "HOST":
                // 民宿主看自己民宿的订单
                orderList = reservationService.getHomestayReservations(targetId, 1, 100);
                break;
            case "GUEST":
                // 游客看自己的订单
                orderList = reservationService.getUserReservations(targetId, 1, 100);
                break;
        }
      if(orderList==null)return;
  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        for (Reservation r : orderList) {
            // 这里需要获取关联信息，暂时用ID代替
            String homestayName = "民宿" + r.getRoomId(); // 需要改进
            String roomNumber = "房间" + r.getRoomId();   // 需要改进
            
            Object[] row = {
                r.getReservationId(),
                r.getReservationNo(),
                homestayName,
                roomNumber,
                r.getGuestName(),
                sdf.format(r.getCheckInDate()),
                sdf.format(r.getCheckOutDate()),
                r.getGuestsCount(),
                r.getTotalPrice(),
                getStatusName(r.getStatus()),
                r.getCreateTime() != null ? sdf.format(r.getCreateTime()) : ""
            };
            tableModel.addRow(row);
        }
        
        updateStats();
    }

    private void updateStats() {
        int total = tableModel.getRowCount();
        double totalAmount = 0;
        for (int i = 0; i < total; i++) {
            totalAmount += (double) tableModel.getValueAt(i, 8);
        }

        Container contentPane = getContentPane();
        if (contentPane.getComponentCount() > 0) {
            Component mainComp = contentPane.getComponent(0);
            if (mainComp instanceof JPanel) {
                JPanel mainPanel = (JPanel) mainComp;
                int compCount = mainPanel.getComponentCount();
                if (compCount > 0) {
                    Component bottomComp = mainPanel.getComponent(compCount - 1);
                    if (bottomComp instanceof JPanel) {
                        JPanel bottomPanel = (JPanel) bottomComp;
                        if (bottomPanel.getComponentCount() > 0) {
                            Component labelComp = bottomPanel.getComponent(0);
                            if (labelComp instanceof JLabel) {
                                JLabel statsLabel = (JLabel) labelComp;
                                statsLabel.setText(String.format("总订单数: %d | 总金额: %.2f元", total, totalAmount));
                            }
                        }
                    }
                }
            }
        }
    }

    private void searchOrders() {
        String status = (String) statusFilter.getSelectedItem();
        if ("全部".equals(status)) status = null;
        
        String startDateStr = startDateField.getText().trim();
        String endDateStr = endDateField.getText().trim();
        
        Date start = null, end = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        try {
            if (!startDateStr.isEmpty()) start = sdf.parse(startDateStr);
            if (!endDateStr.isEmpty()) end = sdf.parse(endDateStr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "日期格式错误", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 调用Service搜索
        List<Reservation> searchResult = reservationService.searchReservations(
            null, status, start, end, 1, 100);
        
        // 更新表格
        tableModel.setRowCount(0);
        for (Reservation r : searchResult) {
            String homestayName = "民宿" + r.getRoomId();
            String roomNumber = "房间" + r.getRoomId();
            
            Object[] row = {
                r.getReservationId(),
                r.getReservationNo(),
                homestayName,
                roomNumber,
                r.getGuestName(),
                sdf.format(r.getCheckInDate()),
                sdf.format(r.getCheckOutDate()),
                r.getGuestsCount(),
                r.getTotalPrice(),
                getStatusName(r.getStatus()),
                r.getCreateTime() != null ? sdf.format(r.getCreateTime()) : ""
            };
            tableModel.addRow(row);
        }
        
        updateStats();
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