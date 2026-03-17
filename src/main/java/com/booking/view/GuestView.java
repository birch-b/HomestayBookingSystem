package com.booking.view;

import com.booking.model.User;
import com.booking.util.AppColors;

import javax.swing.*;
import java.awt.*;

/**
 * 游客界面
 */
public class GuestView extends MainView {

    public GuestView(User user) {
        super(user, "游客中心");
    }

    @Override
    protected void initMenu() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(AppColors.PRIMARY_PURPLE);
        menuBar.setBorder(BorderFactory.createLineBorder(AppColors.PRIMARY_PURPLE));

        // 民宿浏览
        JMenu browseMenu = new JMenu("民宿浏览");
        browseMenu.setForeground(AppColors.WHITE);
        browseMenu.setFont(new Font("微软雅黑", Font.BOLD, 14));

        JMenuItem allHomestayItem = new JMenuItem("所有民宿");
        JMenuItem searchItem = new JMenuItem("搜索民宿");
        JMenuItem cityItem = new JMenuItem("按城市浏览");
        styleMenuItem(allHomestayItem);
        styleMenuItem(searchItem);
        styleMenuItem(cityItem);

        browseMenu.add(allHomestayItem);
        browseMenu.add(searchItem);
        browseMenu.add(cityItem);

        // 我的订单
        JMenu orderMenu = new JMenu("我的订单");
        orderMenu.setForeground(AppColors.WHITE);
        orderMenu.setFont(new Font("微软雅黑", Font.BOLD, 14));

        JMenuItem myOrderItem = new JMenuItem("订单列表");
        JMenuItem pendingOrderItem = new JMenuItem("待支付");
        JMenuItem historyOrderItem = new JMenuItem("历史订单");
        styleMenuItem(myOrderItem);
        styleMenuItem(pendingOrderItem);
        styleMenuItem(historyOrderItem);

        orderMenu.add(myOrderItem);
        orderMenu.add(pendingOrderItem);
        orderMenu.add(historyOrderItem);

        // 我的评价
        JMenu reviewMenu = new JMenu("我的评价");
        reviewMenu.setForeground(AppColors.WHITE);
        reviewMenu.setFont(new Font("微软雅黑", Font.BOLD, 14));

        JMenuItem myReviewItem = new JMenuItem("我的评价");
        JMenuItem addReviewItem = new JMenuItem("发表评价");
        styleMenuItem(myReviewItem);
        styleMenuItem(addReviewItem);

        reviewMenu.add(myReviewItem);
        reviewMenu.add(addReviewItem);

        // 个人中心
        JMenu profileMenu = new JMenu("个人中心");
        profileMenu.setForeground(AppColors.WHITE);
        profileMenu.setFont(new Font("微软雅黑", Font.BOLD, 14));

        JMenuItem infoItem = new JMenuItem("个人信息");
        JMenuItem passwordItem = new JMenuItem("修改密码");
        styleMenuItem(infoItem);
        styleMenuItem(passwordItem);

        profileMenu.add(infoItem);
        profileMenu.add(passwordItem);

        menuBar.add(browseMenu);
        menuBar.add(orderMenu);
        menuBar.add(reviewMenu);
        menuBar.add(profileMenu);

        setJMenuBar(menuBar);

        // ========== 添加菜单点击事件 ==========
        allHomestayItem.addActionListener(e -> openAllHomestay());
        searchItem.addActionListener(e -> openSearchHomestay());
        cityItem.addActionListener(e -> openCityBrowse());
        myOrderItem.addActionListener(e -> openMyOrders());
        pendingOrderItem.addActionListener(e -> openPendingOrders());
        historyOrderItem.addActionListener(e -> openHistoryOrders());
        myReviewItem.addActionListener(e -> openMyReviews());
        addReviewItem.addActionListener(e -> openAddReview());
        infoItem.addActionListener(e -> openPersonalInfo());
        passwordItem.addActionListener(e -> openChangePassword());
    }

    @Override
    protected void initContent() {
        contentPanel.setBackground(AppColors.LIGHT_PURPLE);

        // 欢迎标签
        JLabel welcomeLabel = new JLabel("欢迎游客 " + currentUser.getRealName(), JLabel.CENTER);
        welcomeLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        welcomeLabel.setForeground(AppColors.PRIMARY_PURPLE);

        // 快捷操作面板
        JPanel quickPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        quickPanel.setBackground(AppColors.LIGHT_PURPLE);
        quickPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        String[] buttons = {"浏览民宿", "搜索民宿", "我的订单", "我的评价", "个人信息", "立即预订"};
        for (String btnText : buttons) {
            JButton btn = new JButton(btnText);
            btn.setFont(new Font("微软雅黑", Font.BOLD, 16));
            btn.setBackground(AppColors.BUTTON_PURPLE);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(150, 80));

            btn.addActionListener(e -> {
                switch (btnText) {
                    case "浏览民宿": openAllHomestay(); break;
                    case "搜索民宿": openSearchHomestay(); break;
                    case "我的订单": openMyOrders(); break;
                    case "我的评价": openMyReviews(); break;
                    case "个人信息": openPersonalInfo(); break;
                    case "立即预订": openReservation(); break;
                }
            });

            quickPanel.add(btn);
        }

        // 搜索面板
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel.setBackground(AppColors.LIGHT_PURPLE);
        searchPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(AppColors.PRIMARY_PURPLE),
                "快速搜索民宿",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("微软雅黑", Font.BOLD, 16),
                AppColors.PRIMARY_PURPLE
        ));

        JTextField cityField = new JTextField(10);
        cityField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        cityField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));

        JTextField dateField = new JTextField(10);
        dateField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        dateField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));
        dateField.setText("2026-03-20");

        JTextField peopleField = new JTextField(5);
        peopleField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        peopleField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));
        peopleField.setText("2");

        JButton searchBtn = new JButton("搜索");
        searchBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        searchBtn.setBackground(AppColors.BUTTON_PURPLE);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        searchBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                searchBtn.setBackground(AppColors.HOVER_PURPLE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                searchBtn.setBackground(AppColors.BUTTON_PURPLE);
            }
        });

        searchBtn.addActionListener(e -> {
            String city = cityField.getText().trim();
            String date = dateField.getText().trim();
            String people = peopleField.getText().trim();
            openSearchResult(city, date, people);
        });

        searchPanel.add(new JLabel("城市:"));
        searchPanel.add(cityField);
        searchPanel.add(new JLabel("日期:"));
        searchPanel.add(dateField);
        searchPanel.add(new JLabel("人数:"));
        searchPanel.add(peopleField);
        searchPanel.add(searchBtn);

        // 推荐民宿
        JPanel recommendPanel = new JPanel(new BorderLayout());
        recommendPanel.setBackground(AppColors.LIGHT_PURPLE);
        recommendPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(AppColors.PRIMARY_PURPLE),
                "推荐民宿",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("微软雅黑", Font.BOLD, 16),
                AppColors.PRIMARY_PURPLE
        ));

        JPanel recommendList = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        recommendList.setBackground(AppColors.LIGHT_PURPLE);

        String[] recNames = {"云中山居", "海边小筑", "山里人家", "溪畔小筑"};
        for (String name : recNames) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(AppColors.WHITE);
            card.setBorder(BorderFactory.createLineBorder(AppColors.PRIMARY_PURPLE));
            card.setPreferredSize(new Dimension(180, 100));

            JLabel nameLabel = new JLabel(name, JLabel.CENTER);
            nameLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
            nameLabel.setForeground(AppColors.PRIMARY_PURPLE);

            JButton viewBtn = new JButton("查看");
            viewBtn.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            viewBtn.setBackground(AppColors.BUTTON_PURPLE);
            viewBtn.setForeground(Color.WHITE);
            viewBtn.setFocusPainted(false);

            viewBtn.addActionListener(e -> openHomestayDetail(name));

            card.add(nameLabel, BorderLayout.CENTER);
            card.add(viewBtn, BorderLayout.SOUTH);

            recommendList.add(card);
        }

        recommendPanel.add(new JScrollPane(recommendList), BorderLayout.CENTER);

        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(AppColors.LIGHT_PURPLE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);
        mainPanel.add(quickPanel, BorderLayout.CENTER);
        mainPanel.add(searchPanel, BorderLayout.CENTER);
        mainPanel.add(recommendPanel, BorderLayout.SOUTH);

        contentPanel.add(mainPanel, BorderLayout.CENTER);
    }

    private void styleMenuItem(JMenuItem item) {
        item.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        item.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        item.setOpaque(true);
        item.setBackground(AppColors.PRIMARY_PURPLE);
        item.setForeground(AppColors.WHITE);

        item.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                item.setBackground(AppColors.HOVER_PURPLE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                item.setBackground(AppColors.PRIMARY_PURPLE);
            }
        });
    }

    // ========== 界面跳转方法 ==========
    private void openAllHomestay() {
        JOptionPane.showMessageDialog(this, "浏览所有民宿", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openSearchHomestay() {
        openReservation();
    }

    private void openCityBrowse() {
        JOptionPane.showMessageDialog(this, "按城市浏览民宿", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openMyOrders() {
        new OrderListView(currentUser, "GUEST", currentUser.getUserId()).setVisible(true);
    }

    private void openPendingOrders() {
        JOptionPane.showMessageDialog(this, "待支付订单", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openHistoryOrders() {
        JOptionPane.showMessageDialog(this, "历史订单", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openMyReviews() {
        new ReviewView(currentUser, "GUEST", currentUser.getUserId()).setVisible(true);
    }

    private void openAddReview() {
        JOptionPane.showMessageDialog(this, "发表评价", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openPersonalInfo() {
        JOptionPane.showMessageDialog(this, "个人信息", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openChangePassword() {
        JOptionPane.showMessageDialog(this, "修改密码", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openReservation() {
        new ReservationView(currentUser).setVisible(true);
    }

    private void openSearchResult(String city, String date, String people) {
        String message = String.format("搜索条件:\n城市: %s\n日期: %s\n人数: %s", city, date, people);
        JOptionPane.showMessageDialog(this, message, "搜索民宿", JOptionPane.INFORMATION_MESSAGE);
        openReservation();
    }

    private void openHomestayDetail(String homestayName) {
        JOptionPane.showMessageDialog(this, "查看民宿详情: " + homestayName, "提示", JOptionPane.INFORMATION_MESSAGE);
    }
}