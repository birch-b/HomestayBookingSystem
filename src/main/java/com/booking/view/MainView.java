package com.booking.view;

import com.booking.model.User;
import com.booking.util.AppColors;

import javax.swing.*;
import java.awt.*;

/**
 * 主界面基类 - 粉紫色系
 */
public abstract class MainView extends JFrame {

    protected User currentUser;
    protected JPanel contentPanel;
    protected JLabel userInfoLabel;

    public MainView(User user, String title) {
        this.currentUser = user;
        initBaseUI(title);
        initMenu();
        initContent();
    }

    private void initBaseUI(String title) {
        setTitle(title + " - 当前用户: " + currentUser.getRealName());
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 设置窗口边框颜色
        getRootPane().setBorder(BorderFactory.createLineBorder(AppColors.PRIMARY_PURPLE, 2));

        // 顶部信息栏
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        topPanel.setBackground(AppColors.LIGHT_PURPLE);

        userInfoLabel = new JLabel("欢迎您，" + currentUser.getRealName() +
                " (" + getRoleName(currentUser.getRole()) + ")");
        userInfoLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        userInfoLabel.setForeground(AppColors.DARK_PURPLE);

        JButton logoutButton = new JButton("退出登录");
        logoutButton.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        logoutButton.setBackground(AppColors.BUTTON_PURPLE);
        logoutButton.setForeground(AppColors.DARK_PURPLE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(3, 12, 3, 12));

        // 按钮悬停效果（与登录界面一致）
        logoutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutButton.setBackground(AppColors.HOVER_PURPLE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutButton.setBackground(AppColors.BUTTON_PURPLE);
            }
        });

        logoutButton.addActionListener(e -> logout());

        topPanel.add(userInfoLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);

        // 内容面板 - 改成 LIGHT_PURPLE
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(AppColors.LIGHT_PURPLE);

        // 主布局
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        // 设置整体背景色 - 也改成 LIGHT_PURPLE
        getContentPane().setBackground(AppColors.LIGHT_PURPLE);
    }

    /**
     * 初始化菜单（由子类实现）
     */
    protected abstract void initMenu();

    /**
     * 初始化内容（由子类实现）
     */
    protected abstract void initContent();

    /**
     * 获取角色名称
     */
    private String getRoleName(String role) {
        switch (role) {
            case "ADMIN": return "管理员";
            case "HOST": return "民宿主";
            case "GUEST": return "游客";
            default: return "未知";
        }
    }

    /**
     * 退出登录
     */
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "确定要退出登录吗？",
                "确认", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginView().setVisible(true);
        }
    }
}