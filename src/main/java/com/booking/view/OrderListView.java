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
    private JTextField searchField;
    private JButton searchButton;
    private JButton refreshButton;
    private JButton backButton;
    private JLabel statsLabel;

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
    private String currentSearchKeyword = null;
    private boolean isSearchMode = false;

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

        // 筛选面板
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        filterPanel.setBackground(AppColors.LIGHT_PURPLE);

        JLabel statusLabel = new JLabel("状态:");
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        statusLabel.setForeground(Color.BLACK);
        filterPanel.add(statusLabel);

        String[] statuses = {"全部", "待支付", "已支付", "已确认", "已入住", "已完成", "已取消"};
        statusFilter = new JComboBox<>(statuses);
        statusFilter.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        statusFilter.setForeground(Color.BLACK);
        statusFilter.setBackground(Color.WHITE);
        statusFilter.setPreferredSize(new Dimension(80, 22));
        filterPanel.add(statusFilter);

        // 添加搜索框
        JLabel keywordLabel = new JLabel("关键词:");
        keywordLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        keywordLabel.setForeground(Color.BLACK);
        filterPanel.add(keywordLabel);

        searchField = new JTextField(10);
        searchField.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        searchField.setForeground(Color.BLACK);
        searchField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));
        searchField.setPreferredSize(new Dimension(100, 22));
        filterPanel.add(searchField);

        searchButton = new JButton("搜索");
        refreshButton = new JButton("刷新");

        styleButton(searchButton);
        styleButton(refreshButton);

        filterPanel.add(searchButton);
        filterPanel.add(refreshButton);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        buttonPanel.setBackground(AppColors.LIGHT_PURPLE);

        backButton = new JButton("返回");
        styleButton(backButton);
        
        // 只有游客角色显示支付和取消按钮
        if ("GUEST".equals(userRole)) {
            JButton payButton = new JButton("支付");
            JButton cancelButton = new JButton("取消");
            
            styleButton(payButton);
            styleButton(cancelButton);
            
            buttonPanel.add(payButton);
            buttonPanel.add(cancelButton);
            
            // 支付按钮事件
            payButton.addActionListener(e -> payOrder());
            // 取消按钮事件
            cancelButton.addActionListener(e -> cancelOrder());
        }
        
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
        orderTable.setRowHeight(30);
        orderTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 13));
        orderTable.getTableHeader().setBackground(AppColors.PRIMARY_PURPLE);
        orderTable.getTableHeader().setForeground(Color.BLACK);
        orderTable.setSelectionBackground(AppColors.HOVER_PURPLE);

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
        statsLabel = new JLabel("总订单数: 0 | 总金额: 0.00元");
        statsLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        statsLabel.setForeground(AppColors.DARK_PURPLE);
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

    private void loadData() {
        tableModel.setRowCount(0);
        List<Reservation> orderList = null;
        List<Reservation> allOrders = null;

        if (isSearchMode) {
            // 搜索模式
            if ("HOST".equals(userRole)) {
                // 民宿主搜索自己民宿的订单
                orderList = reservationService.getHomestayReservations(targetId, currentPage, pageSize);
                allOrders = reservationService.getHomestayReservations(targetId, 1, Integer.MAX_VALUE);
            } else {
                // 管理员和游客的搜索
                orderList = reservationService.searchReservations(
                        "GUEST".equals(userRole) ? targetId : null,
                        currentSearchKeyword,
                        currentSearchStatus,
                        null,
                        null,
                        currentPage,
                        pageSize
                );
                allOrders = reservationService.searchReservations(
                        "GUEST".equals(userRole) ? targetId : null,
                        currentSearchKeyword,
                        currentSearchStatus,
                        null,
                        null,
                        1,
                        Integer.MAX_VALUE
                );
            }
        } else {
            // 普通模式
            switch (userRole) {
                case "ADMIN":
                    // 管理员查看所有订单
                    orderList = reservationService.searchReservations(null, null, null, null, null, currentPage, pageSize);
                    allOrders = reservationService.searchReservations(null, null, null, null, null, 1, Integer.MAX_VALUE);
                    break;
                case "GUEST":
                    // 游客查看自己的订单
                    orderList = reservationService.getUserReservations(targetId, currentPage, pageSize);
                    allOrders = reservationService.getUserReservations(targetId, 1, Integer.MAX_VALUE);
                    break;
                case "HOST":
                    // 民宿主查看自己民宿的订单
                    orderList = reservationService.getHomestayReservations(targetId, currentPage, pageSize);
                    allOrders = reservationService.getHomestayReservations(targetId, 1, Integer.MAX_VALUE);
                    break;
            }
        }

        if (orderList == null) {
            updateStats();
            updatePaginationInfo();
            return;
        }

        totalCount = allOrders != null ? allOrders.size() : 0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Reservation r : orderList) {
            Object[] row = {
                    r.getReservationId(),
                    r.getReservationNo(),
                    "民宿" + r.getRoomId(), // 需要改进：获取真实民宿名
                    "房间" + r.getRoomId(), // 需要改进：获取真实房间号
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
            try {
                Object priceObj = tableModel.getValueAt(i, 8);
                if (priceObj instanceof Double) {
                    totalAmount += (Double) priceObj;
                }
            } catch (Exception e) {
                // 忽略
            }
        }

        statsLabel.setText(String.format("总订单数: %d | 总金额: %.2f元", total, totalAmount));
    }

    private void searchOrders() {
        String status = (String) statusFilter.getSelectedItem();
        if ("全部".equals(status)) {
            currentSearchStatus = null;
        } else {
            switch (status) {
                case "待支付": currentSearchStatus = "PENDING"; break;
                case "已支付": currentSearchStatus = "PAID"; break;
                case "已确认": currentSearchStatus = "CONFIRMED"; break;
                case "已入住": currentSearchStatus = "CHECKED_IN"; break;
                case "已完成": currentSearchStatus = "COMPLETED"; break;
                case "已取消": currentSearchStatus = "CANCELLED"; break;
            }
        }

        currentSearchKeyword = searchField.getText().trim();
        isSearchMode = true;
        currentPage = 1;

        loadData();

        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "未找到符合条件的订单", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void refreshOrders() {
        statusFilter.setSelectedIndex(0);
        searchField.setText("");
        currentSearchStatus = null;
        currentSearchKeyword = null;
        isSearchMode = false;
        currentPage = 1;

        loadData();
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
        currentPage = 1;
        loadData();
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
        currentPage = totalPages;
        loadData();
    }

    private void goToPage() {
        try {
            int page = Integer.parseInt(pageInput.getText().trim());
            if (page >= 1 && page <= totalPages) {
                currentPage = page;
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "页码超出范围", "提示", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效的页码", "提示", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * 支付订单
     */
    private void payOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要支付的订单", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int reservationId = (int) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 9);

        if (!"待支付".equals(status)) {
            JOptionPane.showMessageDialog(this, "只有待支付的订单才能进行支付", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "确定要支付该订单吗？", "确认支付", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = reservationService.updateReservationStatus(reservationId, "PAID");
            if (success) {
                JOptionPane.showMessageDialog(this, "支付成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "支付失败，请重试", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 取消订单
     */
    private void cancelOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要取消的订单", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int reservationId = (int) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 9);

        if (!"待支付".equals(status)) {
            JOptionPane.showMessageDialog(this, "只有待支付的订单才能取消", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "确定要取消该订单吗？", "确认取消", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = reservationService.updateReservationStatus(reservationId, "CANCELLED");
            if (success) {
                JOptionPane.showMessageDialog(this, "取消成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "取消失败，请重试", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}