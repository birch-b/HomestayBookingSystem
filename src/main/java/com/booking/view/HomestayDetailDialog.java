package com.booking.view;

import com.booking.model.Homestay;
import com.booking.model.User;
import com.booking.service.HomestayService;
import com.booking.service.impl.HomestayServiceImpl;
import com.booking.util.AppColors;

import javax.swing.*;
import java.awt.*;

/**
 * 统一的民宿详情对话框
 */
public class HomestayDetailDialog {

    /**
     * 显示民宿详情对话框
     * @param parent 父窗口
     * @param currentUser 当前用户
     * @param homestayId 民宿ID
     */
    public static void show(JFrame parent, User currentUser, int homestayId) {
        show(parent, currentUser, homestayId, null, null, null);
    }

    /**
     * 显示民宿详情对话框（带日期范围）
     * @param parent 父窗口
     * @param currentUser 当前用户
     * @param homestayId 民宿ID
     * @param checkIn 入住日期
     * @param checkOut 离店日期
     * @param people 人数
     */
    public static void show(JFrame parent, User currentUser, int homestayId, String checkIn, String checkOut, String people) {
        // 获取民宿详情
        HomestayService homestayService = new HomestayServiceImpl();
        Homestay homestay = homestayService.getHomestayById(homestayId);
        if (homestay == null) {
            JOptionPane.showMessageDialog(parent, "未找到民宿信息");
            return;
        }

        // 创建详情对话框
        JDialog dialog = new JDialog(parent, "民宿详情", true);
        dialog.setSize(550, 450);
        dialog.setLocationRelativeTo(parent);

        // 主面板 - 使用紫色背景
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(AppColors.LIGHT_PURPLE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 内容面板 - 白色卡片样式
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.PRIMARY_PURPLE, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // 民宿名称 - 大标题
        JLabel nameLabel = new JLabel(homestay.getName());
        nameLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        nameLabel.setForeground(AppColors.DARK_PURPLE);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 分隔线
        JSeparator separator = new JSeparator();
        separator.setForeground(AppColors.PRIMARY_PURPLE);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 信息面板 - 使用GridBagLayout对齐
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // 城市
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel cityTitle = new JLabel("所在城市");
        cityTitle.setFont(new Font("微软雅黑", Font.BOLD, 14));
        cityTitle.setForeground(AppColors.DARK_PURPLE);
        infoPanel.add(cityTitle, gbc);

        gbc.gridx = 1;
        JLabel cityValue = new JLabel(homestay.getCity());
        cityValue.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        cityValue.setForeground(Color.BLACK);
        infoPanel.add(cityValue, gbc);

        // 地址
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel addressTitle = new JLabel("详细地址");
        addressTitle.setFont(new Font("微软雅黑", Font.BOLD, 14));
        addressTitle.setForeground(AppColors.DARK_PURPLE);
        infoPanel.add(addressTitle, gbc);

        gbc.gridx = 1;
        JLabel addressValue = new JLabel(homestay.getAddress());
        addressValue.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        addressValue.setForeground(Color.BLACK);
        infoPanel.add(addressValue, gbc);

        // 评分
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel ratingTitle = new JLabel("用户评分");
        ratingTitle.setFont(new Font("微软雅黑", Font.BOLD, 14));
        ratingTitle.setForeground(AppColors.DARK_PURPLE);
        infoPanel.add(ratingTitle, gbc);

        gbc.gridx = 1;
        JLabel ratingValue = new JLabel(String.format("%.1f", homestay.getRating()) + " ★");
        ratingValue.setFont(new Font("微软雅黑", Font.BOLD, 14));
        ratingValue.setForeground(Color.ORANGE);
        infoPanel.add(ratingValue, gbc);

        // 描述标题
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JLabel descTitle = new JLabel("民宿介绍");
        descTitle.setFont(new Font("微软雅黑", Font.BOLD, 14));
        descTitle.setForeground(AppColors.DARK_PURPLE);
        infoPanel.add(descTitle, gbc);

        // 描述内容 - 使用JTextArea自动换行
        JTextArea descArea = new JTextArea(homestay.getDescription());
        descArea.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        descArea.setForeground(Color.BLACK);
        descArea.setBackground(Color.WHITE);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBorder(BorderFactory.createLineBorder(AppColors.LIGHT_PURPLE));
        descScroll.setPreferredSize(new Dimension(450, 80));
        descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        descScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 组装内容面板
        contentPanel.add(nameLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(separator);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(infoPanel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(descScroll);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(AppColors.LIGHT_PURPLE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton bookBtn = new JButton("立即预订");
        bookBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        bookBtn.setBackground(AppColors.BUTTON_PURPLE);
        bookBtn.setForeground(AppColors.DARK_PURPLE);
        bookBtn.setFocusPainted(false);
        bookBtn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        bookBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bookBtn.addActionListener(e -> {
            dialog.dispose();
            if (checkIn != null && checkOut != null && people != null) {
                new ReservationView(currentUser, homestay.getHomestayId(), checkIn, checkOut, people).setVisible(true);
            } else {
                new ReservationView(currentUser, homestay.getHomestayId()).setVisible(true);
            }
        });

        JButton closeBtn = new JButton("关闭");
        closeBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        closeBtn.setBackground(Color.WHITE);
        closeBtn.setForeground(AppColors.DARK_PURPLE);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));
        closeBtn.setPreferredSize(new Dimension(100, 40));
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(bookBtn);
        buttonPanel.add(closeBtn);

        // 组装主面板
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
}
