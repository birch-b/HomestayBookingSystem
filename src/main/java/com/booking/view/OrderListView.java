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
    
    // 分页相关
    private int currentPage = 1;
    private int pageSize = 10;
    private int totalPages = 1;
    private int totalCount = 0;
    private JButton firstPageButton;
    private JButton prevPageButton;
    private JButton nextPageButton;
    private JButton lastPageButton;
    private JTextField pageInput;
    private JLabel pageInfoLabel;
    
    // 保存当前搜索条件
    private String currentSearchStatus = null;
    private Date currentSearchStartDate = null;
    private Date currentSearchEndDate = null;
    private boolean isSearchMode = false; // 是否处于搜索模式

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
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);

        // 筛选面板 - 每个字段后面紧跟对应的输入框
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        filterPanel.setBackground(AppColors.LIGHT_PURPLE);

        // 状态: 标签 + 下拉框
        filterPanel.add(new JLabel("状态:"));
        String[] statuses = {"全部", "待支付", "已支付", "已确认", "已入住", "已完成", "已取消"};
        statusFilter = new JComboBox<>(statuses);
        statusFilter.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        statusFilter.setBackground(Color.WHITE);
        statusFilter.setPreferredSize(new Dimension(80, 22));
        filterPanel.add(statusFilter);

        // 开始日期: 标签 + 输入框
        filterPanel.add(new JLabel("开始日期:"));
        startDateField = new JTextField(8);
        startDateField.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        startDateField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));
        startDateField.setPreferredSize(new Dimension(100, 22));
        filterPanel.add(startDateField);

        // 结束日期: 标签 + 输入框
        filterPanel.add(new JLabel("结束日期:"));
        endDateField = new JTextField(8);
        endDateField.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        endDateField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));
        endDateField.setPreferredSize(new Dimension(100, 22));
        filterPanel.add(endDateField);

        searchButton = new JButton("搜索");
        refreshButton = new JButton("刷新");

        styleButton(searchButton);
        styleButton(refreshButton);

        filterPanel.add(searchButton);
        filterPanel.add(refreshButton);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        buttonPanel.setBackground(AppColors.LIGHT_PURPLE);

        viewDetailButton = new JButton("详情");
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

        // 分页面板
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        paginationPanel.setBackground(AppColors.LIGHT_PURPLE);

        firstPageButton = new JButton("首页");
        prevPageButton = new JButton("上一页");
        nextPageButton = new JButton("下一页");
        lastPageButton = new JButton("末页");
        pageInput = new JTextField(3);
        pageInfoLabel = new JLabel("第 1 页 / 共 1 页");

        styleButton(firstPageButton);
        styleButton(prevPageButton);
        styleButton(nextPageButton);
        styleButton(lastPageButton);

        pageInput.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        pageInput.setHorizontalAlignment(JTextField.CENTER);
        pageInfoLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        pageInfoLabel.setForeground(AppColors.DARK_PURPLE);

        paginationPanel.add(firstPageButton);
        paginationPanel.add(prevPageButton);
        paginationPanel.add(new JLabel("跳转到:"));
        paginationPanel.add(pageInput);
        paginationPanel.add(new JLabel("页"));
        paginationPanel.add(pageInfoLabel);
        paginationPanel.add(nextPageButton);
        paginationPanel.add(lastPageButton);

        // 底部统计
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(AppColors.LIGHT_PURPLE);
        JLabel statsLabel = new JLabel("总订单数: 0 | 总金额: 0.00元");
        statsLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        statsLabel.setForeground(Color.BLACK);
        bottomPanel.add(statsLabel);

        // 组装界面
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(AppColors.LIGHT_PURPLE);
        northPanel.add(titleLabel, BorderLayout.NORTH);
        northPanel.add(topPanel, BorderLayout.SOUTH);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(AppColors.LIGHT_PURPLE);
        southPanel.add(paginationPanel, BorderLayout.NORTH);
        southPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        mainPanel.add(northPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // 事件监听
        searchButton.addActionListener(e -> searchOrders());
        refreshButton.addActionListener(e -> refreshOrders());
        viewDetailButton.addActionListener(e -> viewOrderDetail());
        backButton.addActionListener(e -> dispose());
        
        // 分页事件
        firstPageButton.addActionListener(e -> goToFirstPage());
        prevPageButton.addActionListener(e -> goToPrevPage());
        nextPageButton.addActionListener(e -> goToNextPage());
        lastPageButton.addActionListener(e -> goToLastPage());
        pageInput.addActionListener(e -> goToPage());
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
    
    private String getStatusCode(String statusName) {
        switch (statusName) {
            case "待支付": return "PENDING";
            case "已支付": return "PAID";
            case "已确认": return "CONFIRMED";
            case "已入住": return "CHECKED_IN";
            case "已完成": return "COMPLETED";
            case "已取消": return "CANCELLED";
            default: return null;
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Reservation> orderList = null;
        
        // 根据是否处于搜索模式决定使用搜索条件还是普通加载
        if (isSearchMode) {
            // 使用搜索条件加载数据（分页）
            orderList = reservationService.searchReservations(
                null, currentSearchStatus, currentSearchStartDate, currentSearchEndDate, 
                currentPage, pageSize);
            
            // 获取总数
            List<Reservation> allResults = reservationService.searchReservations(
                null, currentSearchStatus, currentSearchStartDate, currentSearchEndDate, 
                1, Integer.MAX_VALUE);
            totalCount = allResults != null ? allResults.size() : 0;
        } else {
            // 普通模式，根据角色加载数据
            switch (userRole) {
                case "ADMIN":
                    orderList = reservationService.searchReservations(null, null, null, null, currentPage, pageSize);
                    // 获取总数
                    List<Reservation> allOrders = reservationService.searchReservations(null, null, null, null, 1, Integer.MAX_VALUE);
                    totalCount = allOrders != null ? allOrders.size() : 0;
                    break;
                case "HOST":
                    orderList = reservationService.getHomestayReservations(targetId, currentPage, pageSize);
                    // 获取总数
                    List<Reservation> allHostOrders = reservationService.getHomestayReservations(targetId, 1, Integer.MAX_VALUE);
                    totalCount = allHostOrders != null ? allHostOrders.size() : 0;
                    break;
                case "GUEST":
                    orderList = reservationService.getUserReservations(targetId, currentPage, pageSize);
                    // 获取总数
                    List<Reservation> allGuestOrders = reservationService.getUserReservations(targetId, 1, Integer.MAX_VALUE);
                    totalCount = allGuestOrders != null ? allGuestOrders.size() : 0;
                    break;
            }
        }
        
        if (orderList == null || orderList.isEmpty()) {
            // 没有数据时显示提示
            if (isSearchMode) {
                tableModel.addRow(new Object[]{"", "", "", "", "", "", "", "", "", "暂无数据", ""});
            }
            updatePaginationInfo();
            updateStats();
            return;
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        for (Reservation r : orderList) {
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
        updatePaginationInfo();
    }

    private void updateStats() {
        int total = tableModel.getRowCount();
        double totalAmount = 0;
        for (int i = 0; i < total; i++) {
            Object value = tableModel.getValueAt(i, 8);
            if (value instanceof Double) {
                totalAmount += (double) value;
            } else if (value instanceof Number) {
                totalAmount += ((Number) value).doubleValue();
            }
        }

        // 更新底部统计
        Container contentPane = getContentPane();
        if (contentPane.getComponentCount() > 0) {
            Component mainComp = contentPane.getComponent(0);
            if (mainComp instanceof JPanel) {
                JPanel mainPanel = (JPanel) mainComp;
                int compCount = mainPanel.getComponentCount();
                if (compCount > 0) {
                    Component southComp = mainPanel.getComponent(compCount - 1);
                    if (southComp instanceof JPanel) {
                        JPanel southPanel = (JPanel) southComp;
                        if (southPanel.getComponentCount() > 0) {
                            Component bottomComp = southPanel.getComponent(1);
                            if (bottomComp instanceof JPanel) {
                                JPanel bottomPanel = (JPanel) bottomComp;
                                if (bottomPanel.getComponentCount() > 0) {
                                    Component labelComp = bottomPanel.getComponent(0);
                                    if (labelComp instanceof JLabel) {
                                        JLabel statsLabel = (JLabel) labelComp;
                                        statsLabel.setText(String.format("总订单数: %d | 总金额: %.2f元", totalCount, totalAmount));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void searchOrders() {
        // 获取搜索条件
        String status = (String) statusFilter.getSelectedItem();
        if ("全部".equals(status)) {
            currentSearchStatus = null;
        } else {
            currentSearchStatus = getStatusCode(status);
        }
        
        String startDateStr = startDateField.getText().trim();
        String endDateStr = endDateField.getText().trim();
        
        currentSearchStartDate = null;
        currentSearchEndDate = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        try {
            if (!startDateStr.isEmpty()) {
                currentSearchStartDate = sdf.parse(startDateStr);
            }
            if (!endDateStr.isEmpty()) {
                currentSearchEndDate = sdf.parse(endDateStr);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "日期格式错误（示例：2024-01-01）", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 进入搜索模式
        isSearchMode = true;
        currentPage = 1; // 重置到第一页
        
        // 加载搜索数据
        loadData();
        
        // 如果搜索不到数据，提示用户
        if (tableModel.getRowCount() == 0 || 
            (tableModel.getRowCount() == 1 && "暂无数据".equals(tableModel.getValueAt(0, 9)))) {
            JOptionPane.showMessageDialog(this, "未找到符合条件的订单", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void refreshOrders() {
        // 清空搜索条件
        statusFilter.setSelectedIndex(0);
        startDateField.setText("");
        endDateField.setText("");
        
        // 重置搜索状态
        isSearchMode = false;
        currentSearchStatus = null;
        currentSearchStartDate = null;
        currentSearchEndDate = null;
        currentPage = 1;
        
        // 重新加载数据
        loadData();
    }

    private void viewOrderDetail() {
        int row = orderTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要查看的订单", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 检查是否是"暂无数据"行
        Object orderIdObj = tableModel.getValueAt(row, 0);
        if (orderIdObj == null || "".equals(orderIdObj.toString())) {
            return;
        }

        int orderId = (int) orderIdObj;
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
    
    // 分页相关方法
    private void updatePaginationInfo() {
        totalPages = (totalCount + pageSize - 1) / pageSize;
        if (totalPages < 1) totalPages = 1;
        
        pageInfoLabel.setText("第 " + currentPage + " 页 / 共 " + totalPages + " 页");
        pageInput.setText(String.valueOf(currentPage));
        
        // 更新按钮状态
        firstPageButton.setEnabled(currentPage > 1);
        prevPageButton.setEnabled(currentPage > 1);
        nextPageButton.setEnabled(currentPage < totalPages);
        lastPageButton.setEnabled(currentPage < totalPages);
    }
    
    private void goToFirstPage() {
        if (currentPage > 1) {
            currentPage = 1;
            loadData();
        }
    }
    
    private void goToPrevPage() {
        if (currentPage > 1) {
            currentPage--;
            loadData();
        }
    }
    
    private void goToNextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            loadData();
        }
    }
    
    private void goToLastPage() {
        if (currentPage < totalPages) {
            currentPage = totalPages;
            loadData();
        }
    }
    
    private void goToPage() {
        try {
            int page = Integer.parseInt(pageInput.getText().trim());
            if (page >= 1 && page <= totalPages) {
                currentPage = page;
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, 
                    String.format("页码超出范围（1-%d）", totalPages), 
                    "提示", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效的页码", "提示", JOptionPane.WARNING_MESSAGE);
        }
    }
}