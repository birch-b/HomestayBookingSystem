package com.booking.view;

import com.booking.model.Review;
import com.booking.model.User;
import com.booking.model.Reservation;
import com.booking.service.ReviewService;
import com.booking.service.ReservationService;
import com.booking.service.impl.ReviewServiceImpl;
import com.booking.service.impl.ReservationServiceImpl;
import com.booking.util.AppColors;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
// import java.util.Date;
import java.util.List;

/**
 * 评价管理界面
 */
public class ReviewView extends JFrame {

    private User currentUser;
    private String userRole; // ADMIN, HOST, GUEST
    private int targetId; // 如果是HOST则是homestayId，如果是GUEST则是userId
    private ReviewService reviewService;
    private ReservationService reservationService;

    private JTable reviewTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> ratingFilter;
    private JTextField searchField;
    private JButton searchButton;
    private JButton refreshButton;
    private JButton replyButton; // 民宿主回复
    private JButton addButton; // 游客发表评价
    private JButton backButton;
    private JTextArea replyArea;
    private JLabel statsLabel;
    
    // 分页相关
    private int currentPage = 1;
    private int pageSize = 10;
    private int totalPages = 1;
    private JButton firstPageButton;
    private JButton prevPageButton;
    private JButton nextPageButton;
    private JButton lastPageButton;
    private JTextField pageInput;
    private JLabel pageInfoLabel;

    public ReviewView(User user, String role, int id) {
        this.currentUser = user;
        this.userRole = role;
        this.targetId = id;
        this.reviewService = new ReviewServiceImpl();
        this.reservationService = new ReservationServiceImpl();
        initUI();
        loadData();
    }

    private void initUI() {
        String title = "";
        switch (userRole) {
            case "ADMIN": title = "所有评价管理"; break;
            case "HOST": title = "民宿评价管理"; break;
            case "GUEST": title = "我的评价"; break;
        }
        setTitle(title + " - " + currentUser.getRealName());
        setSize(1000, 650);
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

        JLabel ratingLabel = new JLabel("评分:");
        ratingLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        ratingLabel.setForeground(Color.BLACK);
        filterPanel.add(ratingLabel);
        
        String[] ratings = {"全部", "5星", "4星", "3星", "2星", "1星"};
        ratingFilter = new JComboBox<>(ratings);
        ratingFilter.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        ratingFilter.setForeground(Color.BLACK);
        ratingFilter.setBackground(Color.WHITE);
        ratingFilter.setPreferredSize(new Dimension(80, 22));
        filterPanel.add(ratingFilter);
        
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

        // 按钮面板（根据角色不同显示不同按钮）
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        buttonPanel.setBackground(AppColors.LIGHT_PURPLE);

        if ("HOST".equals(userRole)) {
            replyButton = new JButton("回复");
            styleButton(replyButton);
            buttonPanel.add(replyButton);
        } else if ("GUEST".equals(userRole)) {
            addButton = new JButton("发表");
            styleButton(addButton);
            buttonPanel.add(addButton);
        }

        // 添加删除按钮
        JButton deleteButton = new JButton("删除");
        styleButton(deleteButton);
        buttonPanel.add(deleteButton);

        backButton = new JButton("返回");
        styleButton(backButton);
        buttonPanel.add(backButton);

        // 合并顶部面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(AppColors.LIGHT_PURPLE);
        topPanel.add(filterPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // 表格
        String[] columns = {"评价ID", "订单号", "民宿", "房间", "客人", "评分", "评价内容", "房东回复", "评价时间", "状态"};
        if ("HOST".equals(userRole)) {
            columns = new String[]{"评价ID", "订单号", "房间", "客人", "评分", "评价内容", "房东回复", "评价时间", "状态"};
        } else if ("GUEST".equals(userRole)) {
            columns = new String[]{"评价ID", "订单号", "民宿", "房间", "评分", "评价内容", "房东回复", "评价时间", "状态"};
        }

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reviewTable = new JTable(tableModel);
        reviewTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        reviewTable.setRowHeight(30);
        reviewTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 13));
        reviewTable.getTableHeader().setBackground(AppColors.PRIMARY_PURPLE);
        reviewTable.getTableHeader().setForeground(Color.BLACK);
        reviewTable.setSelectionBackground(AppColors.HOVER_PURPLE);

        JScrollPane scrollPane = new JScrollPane(reviewTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppColors.PRIMARY_PURPLE));

        // 回复面板（只有民宿主可见）
        JPanel replyPanel = new JPanel(new BorderLayout());
        replyPanel.setBackground(AppColors.LIGHT_PURPLE);
        replyPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        if ("HOST".equals(userRole)) {
            JLabel replyLabel = new JLabel("回复内容:");
            replyLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
            replyLabel.setForeground(AppColors.DARK_PURPLE);

            replyArea = new JTextArea(3, 50);
            replyArea.setFont(new Font("微软雅黑", Font.PLAIN, 13));
            replyArea.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));
            replyArea.setLineWrap(true);

            JScrollPane replyScroll = new JScrollPane(replyArea);

            JButton submitReplyButton = new JButton("提交回复");
            styleButton(submitReplyButton);
            submitReplyButton.addActionListener(e -> submitReply());

            JPanel replyInputPanel = new JPanel(new BorderLayout(5, 5));
            replyInputPanel.setBackground(AppColors.LIGHT_PURPLE);
            replyInputPanel.add(replyLabel, BorderLayout.NORTH);
            replyInputPanel.add(replyScroll, BorderLayout.CENTER);
            replyInputPanel.add(submitReplyButton, BorderLayout.EAST);

            replyPanel.add(replyInputPanel, BorderLayout.CENTER);
        }

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
        statsLabel = new JLabel("总评价数: 0 | 平均评分: 0.0星");
        statsLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        statsLabel.setForeground(AppColors.DARK_PURPLE);
        bottomPanel.add(statsLabel);

        // 组装界面
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        if ("HOST".equals(userRole)) {
            centerPanel.add(replyPanel, BorderLayout.SOUTH);
        }

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(AppColors.LIGHT_PURPLE);
        northPanel.add(titleLabel, BorderLayout.NORTH);
        northPanel.add(topPanel, BorderLayout.SOUTH);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(AppColors.LIGHT_PURPLE);
        southPanel.add(paginationPanel, BorderLayout.NORTH);
        southPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        mainPanel.add(northPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // 事件监听
        searchButton.addActionListener(e -> searchReviews());
        refreshButton.addActionListener(e -> loadData());
        backButton.addActionListener(e -> dispose());

        if ("HOST".equals(userRole) && replyButton != null) {
            replyButton.addActionListener(e -> prepareReply());
        }
        if ("GUEST".equals(userRole) && addButton != null) {
            addButton.addActionListener(e -> addReview());
        }

        // 删除按钮事件
        deleteButton.addActionListener(e -> deleteReview());
        
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

    // 修复1：将参数类型改为 int
    private String getStatusName(int status) {
        return status == 1 ? "显示" : "隐藏";
    }

    private void loadData() {
        tableModel.setRowCount(0);
        if (searchField != null) {
            searchField.setText(""); // 清空搜索框
        }
        ratingFilter.setSelectedIndex(0); // 重置评分筛选

        List<Review> reviewList = null;
        
        // 根据角色加载不同数据
        switch (userRole) {
            case "ADMIN":
                // 管理员看所有评价（使用limit参数）
                reviewList = reviewService.getLatestReviews(10);
                break;
            case "HOST":
                // 民宿主看自己民宿的评价（分页）
                reviewList = reviewService.getReviewsByHomestayId(targetId, currentPage, pageSize);
                break;
            case "GUEST":
                // 游客看自己的评价（分页）
                reviewList = reviewService.getReviewsByGuestId(targetId, currentPage, pageSize);
                break;
        }

        if (reviewList == null) return;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (Review r : reviewList) {
            // 根据角色不同，表格列不同
            if ("ADMIN".equals(userRole)) {
                Object[] row = {
                    r.getReviewId(),
                    "订单" + r.getReservationId(), // 需要改进：获取真实订单号
                    "民宿" + r.getReservationId(), // 需要改进：获取真实民宿名
                    "房间" + r.getReservationId(), // 需要改进：获取真实房间号
                    "用户" + r.getGuestId(),       // 需要改进：获取真实用户名
                    r.getRating(),
                    r.getComment(),
                    r.getHostReply() != null ? r.getHostReply() : "",
                    r.getCreateTime() != null ? sdf.format(r.getCreateTime()) : "",
                    getStatusName(r.getStatus())   // 修复：传入 int
                };
                tableModel.addRow(row);
            } else if ("HOST".equals(userRole)) {
                Object[] row = {
                    r.getReviewId(),
                    "订单" + r.getReservationId(),
                    "房间" + r.getReservationId(),
                    "用户" + r.getGuestId(),
                    r.getRating(),
                    r.getComment(),
                    r.getHostReply() != null ? r.getHostReply() : "",
                    r.getCreateTime() != null ? sdf.format(r.getCreateTime()) : "",
                    getStatusName(r.getStatus())   // 修复：传入 int
                };
                tableModel.addRow(row);
            } else if ("GUEST".equals(userRole)) {
                Object[] row = {
                    r.getReviewId(),
                    "订单" + r.getReservationId(),
                    "民宿" + r.getReservationId(),
                    "房间" + r.getReservationId(),
                    r.getRating(),
                    r.getComment(),
                    r.getHostReply() != null ? r.getHostReply() : "",
                    r.getCreateTime() != null ? sdf.format(r.getCreateTime()) : "",
                    getStatusName(r.getStatus())   // 修复：传入 int
                };
                tableModel.addRow(row);
            }
        }

        updateStats();
        updatePaginationInfo();
    }

    private void updateStats() {
        int total = tableModel.getRowCount();
        double totalRating = 0;
        int ratingCount = 0;

        for (int i = 0; i < total; i++) {
            int ratingCol = "ADMIN".equals(userRole) ? 5 : 4;
            try {
                Object ratingObj = tableModel.getValueAt(i, ratingCol);
                if (ratingObj instanceof Integer) {
                    totalRating += (Integer) ratingObj;
                    ratingCount++;
                }
            } catch (Exception e) {
                // 忽略
            }
        }

        double avgRating = ratingCount > 0 ? totalRating / ratingCount : 0;
        statsLabel.setText(String.format("总评价数: %d | 平均评分: %.1f星", total, avgRating));
    }

    private void searchReviews() {
        String rating = (String) ratingFilter.getSelectedItem();
        String keyword = searchField.getText().trim();
        
        List<Review> searchResult = null;
        
        // 根据角色获取不同数据后再筛选
        switch (userRole) {
            case "ADMIN":
                searchResult = reviewService.getLatestReviews(100);
                break;
            case "HOST":
                searchResult = reviewService.getReviewsByHomestayId(targetId, 1, 100);
                break;
            case "GUEST":
                searchResult = reviewService.getReviewsByGuestId(targetId, 1, 100);
                break;
        }
        
        if (searchResult != null) {
            // 只显示状态为1（显示）的评价
            searchResult.removeIf(r -> r.getStatus() != 1);
            
            // 按评分筛选
            if (!"全部".equals(rating)) {
                int ratingValue = Integer.parseInt(rating.substring(0, 1));
                int finalRatingValue = ratingValue;
                searchResult.removeIf(r -> r.getRating() != finalRatingValue);
            }
            
            // 按关键词筛选
            if (!keyword.isEmpty()) {
                String finalKeyword = keyword;
                searchResult.removeIf(r -> {
                    String comment = r.getComment() != null ? r.getComment() : "";
                    String reply = r.getHostReply() != null ? r.getHostReply() : "";
                    return !comment.contains(finalKeyword) && !reply.contains(finalKeyword);
                });
            }
        }

        // 更新表格
        tableModel.setRowCount(0);
        if (searchResult != null && !searchResult.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            for (Review r : searchResult) {
                if ("ADMIN".equals(userRole)) {
                    Object[] row = {
                        r.getReviewId(),
                        "订单" + r.getReservationId(),
                        "民宿" + r.getReservationId(),
                        "房间" + r.getReservationId(),
                        "用户" + r.getGuestId(),
                        r.getRating(),
                        r.getComment(),
                        r.getHostReply() != null ? r.getHostReply() : "",
                        r.getCreateTime() != null ? sdf.format(r.getCreateTime()) : "",
                        getStatusName(r.getStatus())   // 修复：传入 int
                    };
                    tableModel.addRow(row);
                } else if ("HOST".equals(userRole)) {
                    Object[] row = {
                        r.getReviewId(),
                        "订单" + r.getReservationId(),
                        "房间" + r.getReservationId(),
                        "用户" + r.getGuestId(),
                        r.getRating(),
                        r.getComment(),
                        r.getHostReply() != null ? r.getHostReply() : "",
                        r.getCreateTime() != null ? sdf.format(r.getCreateTime()) : "",
                        getStatusName(r.getStatus())   // 修复：传入 int
                    };
                    tableModel.addRow(row);
                } else if ("GUEST".equals(userRole)) {
                    Object[] row = {
                        r.getReviewId(),
                        "订单" + r.getReservationId(),
                        "民宿" + r.getReservationId(),
                        "房间" + r.getReservationId(),
                        r.getRating(),
                        r.getComment(),
                        r.getHostReply() != null ? r.getHostReply() : "",
                        r.getCreateTime() != null ? sdf.format(r.getCreateTime()) : "",
                        getStatusName(r.getStatus())   // 修复：传入 int
                    };
                    tableModel.addRow(row);
                }
            }
        } else {
            // 显示无数据提示
            JOptionPane.showMessageDialog(this, "未找到符合条件的评价", "提示", JOptionPane.INFORMATION_MESSAGE);
            // 重置搜索框并返回全部数据
            ratingFilter.setSelectedIndex(0); // 重置为"全部"
            searchField.setText(""); // 清空搜索框
            loadData(); // 重新加载所有数据
        }
        updateStats();
    }

    private void prepareReply() {
        int row = reviewTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要回复的评价", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String guestName = (String) tableModel.getValueAt(row, 3);
        String content = (String) tableModel.getValueAt(row, 5);
        String existingReply = (String) tableModel.getValueAt(row, 6);

        if (existingReply != null && !existingReply.isEmpty()) {
            JOptionPane.showMessageDialog(this, "该评价已有回复，不能重复回复", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        replyArea.setText("");
        replyArea.requestFocus();
        JOptionPane.showMessageDialog(this, "请在下方输入回复内容", "回复评价", JOptionPane.INFORMATION_MESSAGE);
    }

    private void submitReply() {
        int row = reviewTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要回复的评价", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String reply = replyArea.getText().trim();
        if (reply.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入回复内容", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int reviewId = (int) tableModel.getValueAt(row, 0);
        
        // 调用Service保存回复
        boolean success = reviewService.replyReview(reviewId, reply);
        
        if (success) {
            tableModel.setValueAt(reply, row, 6);
            JOptionPane.showMessageDialog(this, "回复成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            replyArea.setText("");
            loadData(); // 重新加载数据
        } else {
            JOptionPane.showMessageDialog(this, "回复失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addReview() {
        // 获取用户的所有订单
        List<Reservation> allOrders = reservationService.getUserReservations(targetId, 1, Integer.MAX_VALUE);
        
        // 过滤出已完成的订单
        List<Reservation> completedOrders = new ArrayList<>();
        if (allOrders != null) {
            for (Reservation order : allOrders) {
                if ("COMPLETED".equals(order.getStatus())) {
                    completedOrders.add(order);
                }
            }
        }
        
        if (completedOrders == null || completedOrders.isEmpty()) {
            JOptionPane.showMessageDialog(this, "没有已完成的订单", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // 创建订单选择对话框
        JDialog orderDialog = new JDialog(this, "选择订单", true);
        orderDialog.setSize(500, 400);
        orderDialog.setLocationRelativeTo(this);
        
        JPanel orderPanel = new JPanel(new BorderLayout());
        orderPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        orderPanel.setBackground(AppColors.LIGHT_PURPLE);
        
        DefaultListModel<String> orderListModel = new DefaultListModel<>();
        for (Reservation order : completedOrders) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String orderInfo = "订单号: " + order.getReservationNo() + ", 民宿: 民宿" + order.getRoomId() + ", 入住: " + sdf.format(order.getCheckInDate());
            orderListModel.addElement(orderInfo);
        }
        
        JList<String> orderList = new JList<>(orderListModel);
        orderList.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        orderList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JButton selectBtn = new JButton("选择");
        selectBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        
        selectBtn.addActionListener(e -> {
            int selectedIndex = orderList.getSelectedIndex();
            if (selectedIndex >= 0) {
                Reservation selectedOrder = completedOrders.get(selectedIndex);
                orderDialog.dispose();
                showReviewForm(selectedOrder);
            } else {
                JOptionPane.showMessageDialog(orderDialog, "请选择一个订单", "提示", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(AppColors.LIGHT_PURPLE);
        buttonPanel.add(selectBtn);
        
        orderPanel.add(new JScrollPane(orderList), BorderLayout.CENTER);
        orderPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        orderDialog.add(orderPanel);
        orderDialog.setVisible(true);
    }
    
    private void showReviewForm(Reservation order) {
        // 创建评价表单对话框
        JDialog reviewDialog = new JDialog(this, "发表评价", true);
        reviewDialog.setSize(600, 450);
        reviewDialog.setLocationRelativeTo(this);
        
        JPanel reviewPanel = new JPanel(new GridBagLayout());
        reviewPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        reviewPanel.setBackground(AppColors.LIGHT_PURPLE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 订单信息
        JLabel orderLabel = new JLabel("订单号: " + order.getReservationNo());
        orderLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        reviewPanel.add(orderLabel, gbc);
        
        // 评分
        JLabel ratingLabel = new JLabel("评分:");
        ratingLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        reviewPanel.add(ratingLabel, gbc);
        
        JComboBox<Integer> ratingComboBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        ratingComboBox.setSelectedIndex(4); // 默认5星
        ratingComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 1;
        reviewPanel.add(ratingComboBox, gbc);
        
        // 评价内容
        JLabel commentLabel = new JLabel("评价内容:");
        commentLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        reviewPanel.add(commentLabel, gbc);
        
        JTextArea commentArea = new JTextArea(8, 40);
        commentArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        commentArea.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        JScrollPane commentScroll = new JScrollPane(commentArea);
        commentScroll.setPreferredSize(new Dimension(400, 150));
        gbc.gridx = 1;
        reviewPanel.add(commentScroll, gbc);
        
        // 提交按钮
        JButton submitBtn = new JButton("提交评价");
        submitBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        
        submitBtn.addActionListener(e -> {
            int rating = (int) ratingComboBox.getSelectedItem();
            String comment = commentArea.getText().trim();
            
            if (comment.isEmpty()) {
                JOptionPane.showMessageDialog(reviewDialog, "请输入评价内容", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // 创建评价对象
            Review review = new Review();
            review.setReservationId(order.getReservationId());
            review.setGuestId(targetId);
            review.setRating(rating);
            review.setComment(comment);
            review.setStatus(1); // 1表示显示
            review.setCreateTime(new Date());
            
            // 保存评价
            int result = reviewService.addReview(review);
            
            if (result == 1) {
                JOptionPane.showMessageDialog(reviewDialog, "评价发表成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                reviewDialog.dispose();
                loadData(); // 重新加载评价列表
            } else if (result == -1) {
                JOptionPane.showMessageDialog(reviewDialog, "该订单已经评价过了", "提示", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(reviewDialog, "评价发表失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        reviewPanel.add(submitBtn, gbc);
        
        reviewDialog.add(reviewPanel);
        reviewDialog.setVisible(true);
    }
    
    private void deleteReview() {
        int row = reviewTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的评价", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int reviewId = (int) tableModel.getValueAt(row, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, "确定要删除这条评价吗？", "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = reviewService.deleteReview(reviewId);
            if (success) {
                JOptionPane.showMessageDialog(this, "评价删除成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadData(); // 重新加载评价列表
            } else {
                JOptionPane.showMessageDialog(this, "评价删除失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // 分页相关方法
    private void updatePaginationInfo() {
        // 计算总页数（根据实际数据量计算）
        int totalCount = tableModel.getRowCount();
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
}