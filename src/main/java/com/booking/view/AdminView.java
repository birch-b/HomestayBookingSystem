package com.booking.view;

import com.booking.model.User;
import com.booking.util.AppColors;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * 管理员主界面 - 粉紫色系
 */
public class AdminView extends MainView {

    public AdminView(User user) {
        super(user, "管理员控制台");
    }

    @Override
    protected void initMenu() {
        // 移除导航栏
    }

    @Override
    protected void initContent() {
        // 内容面板背景设为浅紫色
        contentPanel.setBackground(AppColors.LIGHT_PURPLE);

        // 添加窗口大小变化监听器
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                updateLayout();
            }
        });

        // 初始化布局
        updateLayout();
    }

    private void updateLayout() {
        // 清空内容面板
        contentPanel.removeAll();

        // 欢迎标签
        JLabel welcomeLabel = new JLabel("欢迎管理员 " + currentUser.getRealName(), JLabel.CENTER);
        welcomeLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        welcomeLabel.setForeground(AppColors.PRIMARY_PURPLE);

        // 获取窗口宽度
        int width = getWidth();
        int height = getHeight();

        // 根据窗口宽度调整快捷操作面板布局
        int rows = 2;
        int cols = 3;
        if (width < 700) {
            rows = 3;
            cols = 2;
        }

        // 快捷操作面板
        JPanel quickPanel = new JPanel(new GridLayout(rows, cols, 10, 10));
        quickPanel.setBackground(AppColors.LIGHT_PURPLE);
        int padding = Math.max(15, width / 25);
        quickPanel.setBorder(BorderFactory.createEmptyBorder(15, padding, 15, padding));

        String[] buttons = {"用户管理", "民宿管理", "订单管理", "评价管理", "数据统计", "系统设置"};
        for (String btnText : buttons) {
            JButton btn = new JButton(btnText);
            btn.setFont(new Font("微软雅黑", Font.BOLD, 12));
            btn.setBackground(AppColors.BUTTON_PURPLE);
            btn.setForeground(AppColors.DARK_PURPLE);
            btn.setFocusPainted(false);
            int btnWidth = Math.max(100, width / (cols + 2));
            int btnHeight = Math.max(50, height / 15);
            btn.setPreferredSize(new Dimension(btnWidth, btnHeight));

            // 添加点击事件
            btn.addActionListener(e -> {
                switch (btnText) {
                    case "用户管理": openUserManage(); break;
                    case "民宿管理": openHomestayManage(); break;
                    case "订单管理": openOrderStats(); break;
                    case "评价管理": openReviewManage(); break;
                    case "数据统计": openIncomeStats(); break;
                    case "系统设置": openSystemSettings(); break;
                }
            });

            quickPanel.add(btn);
        }

        // 统计面板
        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        infoPanel.setBackground(AppColors.WHITE);
        infoPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(AppColors.PRIMARY_PURPLE),
                "系统概览",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("微软雅黑", Font.BOLD, 12),
                AppColors.PRIMARY_PURPLE
        ));

        // 统计标签
        JLabel totalUsersLabel = new JLabel("总用户数:");
        totalUsersLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        totalUsersLabel.setForeground(AppColors.DARK_PURPLE);

        JLabel totalUsersValue = new JLabel("--");
        totalUsersValue.setFont(new Font("微软雅黑", Font.BOLD, 12));
        totalUsersValue.setForeground(AppColors.PRIMARY_PURPLE);

        JLabel totalHomestaysLabel = new JLabel("总民宿数:");
        totalHomestaysLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        totalHomestaysLabel.setForeground(AppColors.DARK_PURPLE);

        JLabel totalHomestaysValue = new JLabel("--");
        totalHomestaysValue.setFont(new Font("微软雅黑", Font.BOLD, 12));
        totalHomestaysValue.setForeground(AppColors.PRIMARY_PURPLE);

        JLabel totalOrdersLabel = new JLabel("总订单数:");
        totalOrdersLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        totalOrdersLabel.setForeground(AppColors.DARK_PURPLE);

        JLabel totalOrdersValue = new JLabel("--");
        totalOrdersValue.setFont(new Font("微软雅黑", Font.BOLD, 12));
        totalOrdersValue.setForeground(AppColors.PRIMARY_PURPLE);

        infoPanel.add(totalUsersLabel);
        infoPanel.add(totalUsersValue);
        infoPanel.add(totalHomestaysLabel);
        infoPanel.add(totalHomestaysValue);
        infoPanel.add(totalOrdersLabel);
        infoPanel.add(totalOrdersValue);

        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(AppColors.LIGHT_PURPLE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);
        mainPanel.add(quickPanel, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);

        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void styleMenuItem(JMenuItem item) {
        item.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        item.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
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
    private void openUserManage() {
        new UserManageView(currentUser).setVisible(true);
    }

    private void openAddUser() {
        JOptionPane.showMessageDialog(this, "打开添加用户界面", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openHomestayManage() {
        new HomestayManageView(currentUser).setVisible(true);
    }

    private void openHomestayVerify() {
        JOptionPane.showMessageDialog(this, "打开民宿审核界面", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openUserStats() {
        new OrderListView(currentUser, "ADMIN", 0).setVisible(true);
    }

    private void openOrderStats() {
        new OrderListView(currentUser, "ADMIN", 0).setVisible(true);
    }

    private void openIncomeStats() {
        new IncomeStatsView(currentUser).setVisible(true);
    }

    private void openSystemSettings() {
        new SystemSettingsView(currentUser).setVisible(true);
    }
    private void openReviewManage() {
        new ReviewView(currentUser, "ADMIN", 0).setVisible(true);
    }
}