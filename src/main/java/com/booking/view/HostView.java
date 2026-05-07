package com.booking.view;

import com.booking.model.User;
import com.booking.util.AppColors;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 民宿主界面（优化版：支持实时刷新）
 */
public class HostView extends MainView {

    private int hostHomestayId = -1;

    // ⭐ 关键：做成成员变量，方便动态更新
    private JLabel orderValue;
    private JLabel checkinValue;

    public HostView(User user) {
        super(user, "民宿主工作台");

        // 获取民宿ID
        com.booking.service.HomestayService homestayService =
                new com.booking.service.impl.HomestayServiceImpl();

        List<com.booking.model.Homestay> homestays =
                homestayService.getHomestaysByHostId(user.getUserId());

        if (homestays != null && !homestays.isEmpty()) {
            hostHomestayId = homestays.get(0).getHomestayId();
        }
    }

    @Override
    protected void initMenu() {
        // 无菜单
    }

    @Override
    protected void initContent() {

        contentPanel.setBackground(AppColors.LIGHT_PURPLE);

        JLabel welcomeLabel = new JLabel("欢迎民宿主 " + currentUser.getRealName(), JLabel.CENTER);
        welcomeLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        welcomeLabel.setForeground(AppColors.PRIMARY_PURPLE);

        // ================= 快捷按钮 =================
        JPanel quickPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        quickPanel.setBackground(AppColors.LIGHT_PURPLE);
        quickPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        String[] buttons = {"我的民宿", "订单管理", "评价回复", "收入统计", "今日入住"};

        for (String btnText : buttons) {
            JButton btn = new JButton(btnText);
            btn.setFont(new Font("微软雅黑", Font.BOLD, 16));
            btn.setBackground(AppColors.BUTTON_PURPLE);
            btn.setForeground(AppColors.DARK_PURPLE);
            btn.setFocusPainted(false);

            btn.addActionListener(e -> {
                switch (btnText) {
                    case "我的民宿": openMyHomestay(); break;
                    case "订单管理": openOrderList(); break;
                    case "评价回复": openPendingReply(); break;
                    case "收入统计": openMonthIncome(); break;
                    case "今日入住": openTodayCheckIn(); break;
                }
            });

            quickPanel.add(btn);
        }

        // ================= 今日概览 =================
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

        JLabel orderLabel = new JLabel("今日订单:");
        JLabel checkinLabel = new JLabel("今日入住:");

        orderLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        checkinLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        orderLabel.setForeground(AppColors.DARK_PURPLE);
        checkinLabel.setForeground(AppColors.DARK_PURPLE);

        // ⭐ 初始化为0
        orderValue = new JLabel("0");
        checkinValue = new JLabel("0");

        orderValue.setFont(new Font("微软雅黑", Font.BOLD, 16));
        checkinValue.setFont(new Font("微软雅黑", Font.BOLD, 16));

        orderValue.setForeground(AppColors.PRIMARY_PURPLE);
        checkinValue.setForeground(AppColors.PRIMARY_PURPLE);

        infoPanel.add(orderLabel);
        infoPanel.add(orderValue);
        infoPanel.add(checkinLabel);
        infoPanel.add(checkinValue);

        // ================= 主布局 =================
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(AppColors.LIGHT_PURPLE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        mainPanel.add(welcomeLabel, BorderLayout.NORTH);
        mainPanel.add(quickPanel, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);

        contentPanel.add(mainPanel);

        // ⭐ 初始化加载数据
        refreshData();

        // ⭐ 定时刷新（每5秒）
        startAutoRefresh();
    }

    // ================= 核心：刷新数据 =================
    private void refreshData() {
        refreshTodayOrderCount();
        refreshTodayCheckInCount();
    }

   // ================= 今日订单（SQL统计版） =================
private void refreshTodayOrderCount() {

    int count = 0;

    if (hostHomestayId != -1) {
        com.booking.service.ReservationService service =
                new com.booking.service.impl.ReservationServiceImpl();

        // ⭐ 直接数据库统计
        count = service.countTodayOrders(hostHomestayId);
    }

    orderValue.setText(String.valueOf(count));
}
    // ================= 今日入住 =================
    private void refreshTodayCheckInCount() {
        int count = 0;

        com.booking.service.CheckinRecordService service =
                new com.booking.service.impl.CheckinRecordServiceImpl();

        List<com.booking.model.CheckinRecord> list = service.getTodayCheckIn();

        if (list != null) {
            count = list.size();
        }

        checkinValue.setText(String.valueOf(count));
    }

    // ================= 自动刷新 =================
    private void startAutoRefresh() {
        new javax.swing.Timer(5000, e -> refreshData()).start();
    }

    // ================= 跳转 =================
    private void openMyHomestay() {
        new HomestayManageView(currentUser).setVisible(true);
    }

    private void openOrderList() {
        new OrderListView(currentUser, "HOST", currentUser.getUserId()).setVisible(true);
    }

    private void openPendingReply() {
        new ReviewView(currentUser, "HOST", currentUser.getUserId()).setVisible(true);
    }

    private void openMonthIncome() {
        new IncomeStatsView(currentUser).setVisible(true);
    }

    private void openTodayCheckIn() {
        new TodayCheckInView(currentUser).setVisible(true);
    }
}