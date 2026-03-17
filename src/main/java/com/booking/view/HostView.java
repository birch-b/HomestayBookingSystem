package com.booking.view;

import com.booking.model.User;
import com.booking.util.AppColors;

import javax.swing.*;
import java.awt.*;

/**
 * 民宿主界面
 */
public class HostView extends MainView {

    private int hostHomestayId = 1; // TODO: 从数据库获取民宿主拥有的民宿ID

    public HostView(User user) {
        super(user, "民宿主工作台");
    }

    @Override
    protected void initMenu() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(AppColors.PRIMARY_PURPLE);
        menuBar.setBorder(BorderFactory.createLineBorder(AppColors.PRIMARY_PURPLE));

        // 民宿管理
        JMenu homestayMenu = new JMenu("民宿管理");
        homestayMenu.setForeground(AppColors.WHITE);
        homestayMenu.setFont(new Font("微软雅黑", Font.BOLD, 14));

        JMenuItem myHomestayItem = new JMenuItem("我的民宿");
        JMenuItem editHomestayItem = new JMenuItem("编辑民宿信息");
        styleMenuItem(myHomestayItem);
        styleMenuItem(editHomestayItem);

        homestayMenu.add(myHomestayItem);
        homestayMenu.add(editHomestayItem);

        // 房间管理
        JMenu roomMenu = new JMenu("房间管理");
        roomMenu.setForeground(AppColors.WHITE);
        roomMenu.setFont(new Font("微软雅黑", Font.BOLD, 14));

        JMenuItem roomListItem = new JMenuItem("房间列表");
        JMenuItem addRoomItem = new JMenuItem("添加房间");
        styleMenuItem(roomListItem);
        styleMenuItem(addRoomItem);

        roomMenu.add(roomListItem);
        roomMenu.add(addRoomItem);

        // 订单管理
        JMenu orderMenu = new JMenu("订单管理");
        orderMenu.setForeground(AppColors.WHITE);
        orderMenu.setFont(new Font("微软雅黑", Font.BOLD, 14));

        JMenuItem orderListItem = new JMenuItem("订单列表");
        JMenuItem todayOrderItem = new JMenuItem("今日订单");
        styleMenuItem(orderListItem);
        styleMenuItem(todayOrderItem);

        orderMenu.add(orderListItem);
        orderMenu.add(todayOrderItem);

        // 评价管理
        JMenu reviewMenu = new JMenu("评价管理");
        reviewMenu.setForeground(AppColors.WHITE);
        reviewMenu.setFont(new Font("微软雅黑", Font.BOLD, 14));

        JMenuItem reviewListItem = new JMenuItem("查看评价");
        JMenuItem pendingReplyItem = new JMenuItem("待回复");
        styleMenuItem(reviewListItem);
        styleMenuItem(pendingReplyItem);

        reviewMenu.add(reviewListItem);
        reviewMenu.add(pendingReplyItem);

        // 收入统计
        JMenu incomeMenu = new JMenu("收入统计");
        incomeMenu.setForeground(AppColors.WHITE);
        incomeMenu.setFont(new Font("微软雅黑", Font.BOLD, 14));

        JMenuItem monthIncomeItem = new JMenuItem("月度收入");
        JMenuItem yearIncomeItem = new JMenuItem("年度收入");
        styleMenuItem(monthIncomeItem);
        styleMenuItem(yearIncomeItem);

        incomeMenu.add(monthIncomeItem);
        incomeMenu.add(yearIncomeItem);

        menuBar.add(homestayMenu);
        menuBar.add(roomMenu);
        menuBar.add(orderMenu);
        menuBar.add(reviewMenu);
        menuBar.add(incomeMenu);

        setJMenuBar(menuBar);

        // ========== 添加菜单点击事件 ==========
        myHomestayItem.addActionListener(e -> openMyHomestay());
        editHomestayItem.addActionListener(e -> openEditHomestay());
        roomListItem.addActionListener(e -> openRoomManage());
        addRoomItem.addActionListener(e -> openAddRoom());
        orderListItem.addActionListener(e -> openOrderList());
        todayOrderItem.addActionListener(e -> openTodayOrder());
        reviewListItem.addActionListener(e -> openReviewManage());
        pendingReplyItem.addActionListener(e -> openPendingReply());
        monthIncomeItem.addActionListener(e -> openMonthIncome());
        yearIncomeItem.addActionListener(e -> openYearIncome());
    }

    @Override
    protected void initContent() {
        contentPanel.setBackground(AppColors.LIGHT_PURPLE);

        JLabel welcomeLabel = new JLabel("欢迎民宿主 " + currentUser.getRealName(), JLabel.CENTER);
        welcomeLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        welcomeLabel.setForeground(AppColors.PRIMARY_PURPLE);

        // 快捷操作面板
        JPanel quickPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        quickPanel.setBackground(AppColors.LIGHT_PURPLE);
        quickPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        String[] buttons = {"我的民宿", "房间管理", "订单管理", "评价回复", "收入统计", "今日入住"};
        for (String btnText : buttons) {
            JButton btn = new JButton(btnText);
            btn.setFont(new Font("微软雅黑", Font.BOLD, 16));
            btn.setBackground(AppColors.BUTTON_PURPLE);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(150, 80));

            btn.addActionListener(e -> {
                switch (btnText) {
                    case "我的民宿": openMyHomestay(); break;
                    case "房间管理": openRoomManage(); break;
                    case "订单管理": openOrderList(); break;
                    case "评价回复": openPendingReply(); break;
                    case "收入统计": openMonthIncome(); break;
                    case "今日入住": openTodayCheckIn(); break;
                }
            });

            quickPanel.add(btn);
        }

        // 今日概览
        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        infoPanel.setBackground(AppColors.WHITE);
        infoPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(AppColors.PRIMARY_PURPLE),
                "今日概览",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("微软雅黑", Font.BOLD, 16),
                AppColors.PRIMARY_PURPLE
        ));

        infoPanel.add(new JLabel("今日订单:"));
        infoPanel.add(new JLabel("0"));
        infoPanel.add(new JLabel("今日入住:"));
        infoPanel.add(new JLabel("0"));

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(AppColors.LIGHT_PURPLE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);
        mainPanel.add(quickPanel, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);

        contentPanel.add(mainPanel);
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
    private void openMyHomestay() {
        new HomestayManageView(currentUser).setVisible(true);
    }

    private void openEditHomestay() {
        JOptionPane.showMessageDialog(this, "编辑民宿信息", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openRoomManage() {
        new RoomManageView(currentUser, hostHomestayId, "我的民宿").setVisible(true);
    }

    private void openAddRoom() {
        JOptionPane.showMessageDialog(this, "添加房间", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openOrderList() {
        new OrderListView(currentUser, "HOST", hostHomestayId).setVisible(true);
    }

    private void openTodayOrder() {
        JOptionPane.showMessageDialog(this, "今日订单", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openReviewManage() {
        new ReviewView(currentUser, "HOST", hostHomestayId).setVisible(true);
    }

    private void openPendingReply() {
        new ReviewView(currentUser, "HOST", hostHomestayId).setVisible(true);
    }

    private void openMonthIncome() {
        JOptionPane.showMessageDialog(this, "月度收入统计", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openYearIncome() {
        JOptionPane.showMessageDialog(this, "年度收入统计", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openTodayCheckIn() {
        JOptionPane.showMessageDialog(this, "今日入住", "提示", JOptionPane.INFORMATION_MESSAGE);
    }
}