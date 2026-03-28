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
        // 移除导航栏，参照管理员界面
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

        String[] buttons = {"我的民宿", "订单管理", "评价回复", "收入统计", "今日入住"};
        for (String btnText : buttons) {
            JButton btn = new JButton(btnText);
            btn.setFont(new Font("微软雅黑", Font.BOLD, 16));
            btn.setBackground(AppColors.BUTTON_PURPLE);
            btn.setForeground(AppColors.DARK_PURPLE);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(150, 80));

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

        JLabel orderLabel = new JLabel("今日订单:");
        orderLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        orderLabel.setForeground(AppColors.DARK_PURPLE);
        infoPanel.add(orderLabel);
        
        JLabel orderValue = new JLabel("0");
        orderValue.setFont(new Font("微软雅黑", Font.BOLD, 14));
        orderValue.setForeground(AppColors.PRIMARY_PURPLE);
        infoPanel.add(orderValue);
        
        JLabel checkinLabel = new JLabel("今日入住:");
        checkinLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        checkinLabel.setForeground(AppColors.DARK_PURPLE);
        infoPanel.add(checkinLabel);
        
        JLabel checkinValue = new JLabel("0");
        checkinValue.setFont(new Font("微软雅黑", Font.BOLD, 14));
        checkinValue.setForeground(AppColors.PRIMARY_PURPLE);
        infoPanel.add(checkinValue);

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
        new IncomeStatsView(currentUser).setVisible(true);
    }

    private void openYearIncome() {
        new IncomeStatsView(currentUser).setVisible(true);
    }

    private void openTodayCheckIn() {
        new TodayCheckInView(currentUser).setVisible(true);
    }
}