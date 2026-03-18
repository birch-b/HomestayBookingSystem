package com.booking.view;

import com.booking.model.Review;
import com.booking.model.User;
import com.booking.service.ReviewService;
import com.booking.service.impl.ReviewServiceImpl;
import com.booking.util.AppColors;

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

    private JTable reviewTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> ratingFilter;
    private JButton searchButton;
    private JButton refreshButton;
    private JButton replyButton; // 民宿主回复
    private JButton addButton; // 游客发表评价
    private JButton backButton;
    private JTextArea replyArea;
    private JLabel statsLabel;

    public ReviewView(User user, String role, int id) {
        this.currentUser = user;
        this.userRole = role;
        this.targetId = id;
        this.reviewService = new ReviewServiceImpl();
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
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);

        // 筛选面板
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        filterPanel.setBackground(AppColors.LIGHT_PURPLE);

        JLabel ratingLabel = new JLabel("评分:");
        ratingLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        ratingLabel.setForeground(Color.BLACK);
        filterPanel.add(ratingLabel);
        
        String[] ratings = {"全部", "5星", "4星", "3星", "2星", "1星"};
        ratingFilter = new JComboBox<>(ratings);
        ratingFilter.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        ratingFilter.setForeground(Color.BLACK);
        ratingFilter.setBackground(Color.WHITE);
        filterPanel.add(ratingFilter);
        
        // 添加空白面板增加间距
        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(30, 10));
        spacer.setBackground(AppColors.LIGHT_PURPLE);
        filterPanel.add(spacer);

        searchButton = new JButton("搜索");
        refreshButton = new JButton("刷新");

        styleButton(searchButton);
        styleButton(refreshButton);

        filterPanel.add(searchButton);
        filterPanel.add(refreshButton);

        // 按钮面板（根据角色不同显示不同按钮）
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setBackground(AppColors.LIGHT_PURPLE);

        if ("HOST".equals(userRole)) {
            replyButton = new JButton("回复评价");
            styleButton(replyButton);
            buttonPanel.add(replyButton);
        } else if ("GUEST".equals(userRole)) {
            addButton = new JButton("发表评价");
            styleButton(addButton);
            buttonPanel.add(addButton);
        }

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

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

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

    // 修复1：将参数类型改为 int
    private String getStatusName(int status) {
        return status == 1 ? "显示" : "隐藏";
    }

    private void loadData() {
        tableModel.setRowCount(0);

        List<Review> reviewList = null;
        
        // 根据角色加载不同数据
        switch (userRole) {
            case "ADMIN":
                // 管理员看所有评价（需要ReviewService提供此方法）
                reviewList = reviewService.getLatestReviews(100);
                break;
            case "HOST":
                // 民宿主看自己民宿的评价
                reviewList = reviewService.getReviewsByHomestayId(targetId, 1, 100);
                break;
            case "GUEST":
                // 游客看自己的评价
                reviewList = reviewService.getReviewsByGuestId(targetId, 1, 100);
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
        
        List<Review> searchResult = null;
        
        if (!"全部".equals(rating)) {
            // 解析评分值，从"5星"格式中提取数字
            int ratingValue = Integer.parseInt(rating.substring(0, 1));
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
                int finalRatingValue = ratingValue;
                searchResult.removeIf(r -> r.getRating() != finalRatingValue);
            }
        } else {
            // 重新加载所有数据
            loadData();
            return;
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
        JOptionPane.showMessageDialog(this, "发表评价功能待实现\n需要选择已完成且未评价的订单",
                "提示", JOptionPane.INFORMATION_MESSAGE);
    }
}