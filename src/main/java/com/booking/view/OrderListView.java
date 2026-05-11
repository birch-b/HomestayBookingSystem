package com.booking.view;

import com.booking.model.Homestay;
import com.booking.model.Reservation;
import com.booking.model.User;
import com.booking.service.HomestayService;
import com.booking.service.ReservationService;
import com.booking.service.impl.HomestayServiceImpl;
import com.booking.service.impl.ReservationServiceImpl;
import com.booking.util.AppColors;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单列表界面 - 支持房客、房东、管理员三种视角
 */
public class OrderListView extends JFrame {

    private User currentUser;
    private String userRole; // ADMIN, HOST, GUEST
    private int targetId;    // HOST则传userId，GUEST则传userId

    private ReservationService reservationService;
    private HomestayService homestayService;

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
    private double currentTotalAmount = 0.0; // 当前查询结果的总金额

    private JButton firstPageButton;
    private JButton prevPageButton;
    private JButton nextPageButton;
    private JButton lastPageButton;
    private JTextField pageInput;
    private JLabel pageInfoLabel;

    // 当前搜索条件缓存
    private String currentSearchStatus = null;
    private String currentSearchKeyword = null;
    private boolean isSearchMode = false;

    public OrderListView(User user, String role, int id) {
        this.currentUser = user;
        this.userRole = role;
        this.targetId = id;
        this.reservationService = new ReservationServiceImpl();
        this.homestayService = new HomestayServiceImpl();

        initUI();
        loadData();
    }

    private void initUI() {
        String titleText = "";
        switch (userRole) {
            case "ADMIN": titleText = "平台所有订单"; break;
            case "HOST": titleText = "民宿订单管理"; break;
            case "GUEST": titleText = "我的订单中心"; break;
        }
        setTitle(titleText + " - " + currentUser.getRealName());
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(AppColors.LIGHT_PURPLE);

        // --- 顶部面板 ---
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(AppColors.LIGHT_PURPLE);

        JLabel titleLabel = new JLabel(titleText, JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 22));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        northPanel.add(titleLabel, BorderLayout.NORTH);

        // 筛选工具栏
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(AppColors.LIGHT_PURPLE);

        filterPanel.add(new JLabel("订单状态:"));
        String[] statuses = {"全部", "待支付", "已支付", "已确认", "已入住", "已完成", "已取消"};
        statusFilter = new JComboBox<>(statuses);
        
        filterPanel.add(new JLabel(" 关键词:"));
        searchField = new JTextField(12);
        
        searchButton = new JButton("搜索");
        refreshButton = new JButton("刷新");
        styleButton(searchButton);
        styleButton(refreshButton);

        filterPanel.add(statusFilter);
        filterPanel.add(searchField);
        filterPanel.add(searchButton);
        filterPanel.add(refreshButton);

        // 右侧操作按钮
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        actionPanel.setBackground(AppColors.LIGHT_PURPLE);

        if ("GUEST".equals(userRole)) {
            JButton payButton = new JButton("立即支付");
            JButton cancelButton = new JButton("取消订单");
            styleButton(payButton);
            styleButton(cancelButton);
            payButton.setBackground(new Color(100, 200, 100)); // 支付按钮设为绿色系
            actionPanel.add(payButton);
            actionPanel.add(cancelButton);

            payButton.addActionListener(e -> payOrder());
            cancelButton.addActionListener(e -> cancelOrder());
        }

        backButton = new JButton("返回");
        styleButton(backButton);
        actionPanel.add(backButton);

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(AppColors.LIGHT_PURPLE);
        toolbar.add(filterPanel, BorderLayout.WEST);
        toolbar.add(actionPanel, BorderLayout.EAST);
        northPanel.add(toolbar, BorderLayout.SOUTH);

        // --- 中间表格 ---
        String[] columns = {"订单号", "入住人", "联系电话", "入住日期", "离店日期", "人数", "总金额", "状态", "下单时间"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        orderTable = new JTable(tableModel);
        orderTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        orderTable.setRowHeight(30);
        orderTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 13));
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(orderTable);

        // --- 底部面板 ---
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(AppColors.LIGHT_PURPLE);

        // 分页控件
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

        paginationPanel.add(firstPageButton);
        paginationPanel.add(prevPageButton);
        paginationPanel.add(new JLabel("跳转:"));
        paginationPanel.add(pageInput);
        paginationPanel.add(pageInfoLabel);
        paginationPanel.add(nextPageButton);
        paginationPanel.add(lastPageButton);

        // 统计条
        statsLabel = new JLabel("加载中...");
        statsLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        statsLabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 0));

        southPanel.add(paginationPanel, BorderLayout.NORTH);
        southPanel.add(statsLabel, BorderLayout.SOUTH);

        // 组装
        mainPanel.add(northPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // 绑定事件
        searchButton.addActionListener(e -> searchOrders());
        refreshButton.addActionListener(e -> refreshOrders());
        backButton.addActionListener(e -> dispose());
        firstPageButton.addActionListener(e -> { currentPage = 1; loadData(); });
        prevPageButton.addActionListener(e -> { if(currentPage > 1) { currentPage--; loadData(); } });
        nextPageButton.addActionListener(e -> { if(currentPage < totalPages) { currentPage++; loadData(); } });
        lastPageButton.addActionListener(e -> { currentPage = totalPages; loadData(); });
        pageInput.addActionListener(e -> goToPage());
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        button.setBackground(AppColors.BUTTON_PURPLE);
        button.setFocusPainted(false);
    }

    private String getStatusDisplay(String status) {
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

    /**
     * 核心数据加载逻辑
     */
    private void loadData() {
        tableModel.setRowCount(0);
        List<Reservation> pageList;
        List<Reservation> totalList; // 用于计算总金额和总数

        // 1. 获取当前页数据
        if (isSearchMode) {
            if ("GUEST".equals(userRole)) {
                pageList  = reservationService.searchReservations(targetId, currentSearchKeyword, currentSearchStatus, null, null, currentPage, pageSize);
                totalList = reservationService.searchReservations(targetId, currentSearchKeyword, currentSearchStatus, null, null, 1, Integer.MAX_VALUE);
            } else if ("HOST".equals(userRole)) {
                // HOST模式：先获取该房东所有民宿的订单再筛选
                totalList = getHostReservations();
                if (currentSearchStatus != null) {
                    totalList.removeIf(r -> !currentSearchStatus.equals(r.getStatus()));
                }
                if (currentSearchKeyword != null && !currentSearchKeyword.isEmpty()) {
                    String kw = currentSearchKeyword;
                    totalList.removeIf(r ->
                        (r.getGuestName()    == null || !r.getGuestName().contains(kw)) &&
                        (r.getReservationNo()== null || !r.getReservationNo().contains(kw)) &&
                        (r.getGuestPhone()   == null || !r.getGuestPhone().contains(kw)));
                }
                int s = (currentPage - 1) * pageSize;
                int e = Math.min(s + pageSize, totalList.size());
                pageList = s >= totalList.size() ? new ArrayList<>() : new ArrayList<>(totalList.subList(s, e));
            } else {
                // ADMIN
                pageList  = reservationService.searchReservations(null, currentSearchKeyword, currentSearchStatus, null, null, currentPage, pageSize);
                totalList = reservationService.searchReservations(null, currentSearchKeyword, currentSearchStatus, null, null, 1, Integer.MAX_VALUE);
            }
        } else {
            // 默认展示模式
            if ("GUEST".equals(userRole)) {
                pageList  = reservationService.getUserReservations(targetId, currentPage, pageSize);
                totalList = reservationService.getUserReservations(targetId, 1, Integer.MAX_VALUE);
            } else if ("HOST".equals(userRole)) {
                // HOST模式：展示该房东所有民宿的所有订单
                totalList = getHostReservations();
                int s = (currentPage - 1) * pageSize;
                int e = Math.min(s + pageSize, totalList.size());
                pageList = s >= totalList.size() ? new ArrayList<>() : new ArrayList<>(totalList.subList(s, e));
            } else {
                pageList  = reservationService.searchReservations(null, null, null, null, null, currentPage, pageSize);
                totalList = reservationService.searchReservations(null, null, null, null, null, 1, Integer.MAX_VALUE);
            }
        }

        // 2. 渲染表格
        if (pageList != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for (Reservation r : pageList) {
                tableModel.addRow(new Object[]{
                        r.getReservationNo(),
                        r.getGuestName(),
                        r.getGuestPhone(),
                        sdf.format(r.getCheckInDate()),
                        sdf.format(r.getCheckOutDate()),
                        r.getGuestsCount(),
                        String.format("%.2f", r.getTotalPrice()),
                        getStatusDisplay(r.getStatus()),
                        r.getCreateTime() != null ? sdf.format(r.getCreateTime()) : "-",
                        r.getReservationId() // 隐藏列：用于内部获取订单ID
                });
            }
        }

        // 3. 更新分页和统计
        totalCount = (totalList != null) ? totalList.size() : 0;
        currentTotalAmount = 0;
        if (totalList != null) {
            for (Reservation r : totalList) {
                currentTotalAmount += r.getTotalPrice();
            }
        }

        totalPages = (int) Math.ceil((double) totalCount / pageSize);
        if (totalPages == 0) totalPages = 1;
        
        updateUIState();
    }

    /**
     * 获取该房东名下所有民宿的全部订单（用于HOST角色）
     * targetId = 房东的userId
     */
    private List<Reservation> getHostReservations() {
        List<Homestay> homestays = homestayService.getHomestaysByHostId(targetId);
        List<Reservation> result = new ArrayList<>();
        if (homestays != null) {
            for (Homestay h : homestays) {
                List<Reservation> rs = reservationService.getHomestayReservations(
                        h.getHomestayId(), 1, Integer.MAX_VALUE);
                if (rs != null) result.addAll(rs);
            }
        }
        return result;
    }

    private void updateUIState() {
        pageInfoLabel.setText("第 " + currentPage + " 页 / 共 " + totalPages + " 页");
        pageInput.setText(String.valueOf(currentPage));
        statsLabel.setText(String.format("筛选统计 -> 订单总数: %d 笔 | 累计总金额: %.2f 元", totalCount, currentTotalAmount));
        
        firstPageButton.setEnabled(currentPage > 1);
        prevPageButton.setEnabled(currentPage > 1);
        nextPageButton.setEnabled(currentPage < totalPages);
        lastPageButton.setEnabled(currentPage < totalPages);
    }

    private void searchOrders() {
        String statusText = (String) statusFilter.getSelectedItem();
        if ("全部".equals(statusText)) {
            currentSearchStatus = null;
        } else {
            switch (statusText) {
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
    }

    private void refreshOrders() {
        statusFilter.setSelectedIndex(0);
        searchField.setText("");
        isSearchMode = false;
        currentSearchStatus = null;
        currentSearchKeyword = null;
        currentPage = 1;
        loadData();
    }

    private void goToPage() {
        try {
            int target = Integer.parseInt(pageInput.getText().trim());
            if (target >= 1 && target <= totalPages) {
                currentPage = target;
                loadData();
            }
        } catch (Exception e) {
            pageInput.setText(String.valueOf(currentPage));
        }
    }

    private void payOrder() {
        int row = orderTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一个待支付的订单");
            return;
        }
        String status = (String) tableModel.getValueAt(row, 7);
        if (!"待支付".equals(status)) {
            JOptionPane.showMessageDialog(this, "只有[待支付]状态的订单可以执行此操作");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 8);
        int choice = JOptionPane.showConfirmDialog(this, "确认支付该订单吗？", "支付确认", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            if (reservationService.updateReservationStatus(id, "PAID")) {
                JOptionPane.showMessageDialog(this, "支付成功！");
                loadData();
            }
        }
    }

    private void cancelOrder() {
        int row = orderTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请选择要取消的订单");
            return;
        }
        String status = (String) tableModel.getValueAt(row, 7);
        if (!"待支付".equals(status) && !"已支付".equals(status)) {
            JOptionPane.showMessageDialog(this, "该状态下的订单无法直接取消，请联系房东");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 8);
        int choice = JOptionPane.showConfirmDialog(this, "确定要取消此订单吗？", "取消确认", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            if (reservationService.updateReservationStatus(id, "CANCELLED")) {
                JOptionPane.showMessageDialog(this, "订单已成功取消");
                loadData();
            }
        }
    }
}