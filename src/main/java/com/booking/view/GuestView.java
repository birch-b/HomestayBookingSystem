package com.booking.view;

import com.booking.model.User;
import com.booking.model.Homestay;
import com.booking.service.HomestayService;
import com.booking.service.impl.HomestayServiceImpl;
import com.booking.util.AppColors;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * 游客界面（最终版：大布局 + 大按钮 + 固定3列 + 自适应宽度）
 */
public class GuestView extends MainView {

    private HomestayService homestayService;

    public GuestView(User user) {
        super(user, "游客中心");
    }

    @Override
    protected void initMenu() {}

    @Override
    protected void initContent() {

        homestayService = new HomestayServiceImpl();

        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(AppColors.LIGHT_PURPLE);

        // ✅ 监听窗口变化（实现自适应）
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                updateLayout();
            }
        });

        updateLayout();
    }

    private void updateLayout() {

        contentPanel.removeAll();

        int width = getWidth();

        // ================= 主容器 =================
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(AppColors.LIGHT_PURPLE);
        int padding = Math.max(15, width / 30);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));

        // 页面整体宽度自适应
        mainPanel.setMaximumSize(new Dimension(Math.max(1000, width - 50), Integer.MAX_VALUE));

        // ================= 欢迎 =================
        JLabel welcomeLabel = new JLabel("欢迎游客 " + currentUser.getRealName(), JLabel.CENTER);
        welcomeLabel.setFont(new Font("微软雅黑", Font.BOLD, Math.min(28, width / 30)));
        welcomeLabel.setForeground(AppColors.PRIMARY_PURPLE);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ================= 搜索 =================
        JPanel searchPanel = new JPanel();
        // 根据宽度调整搜索面板布局
        if (width < 700) {
            searchPanel.setLayout(new GridLayout(3, 2, 10, 10));
        } else {
            searchPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
        }
        searchPanel.setBackground(AppColors.LIGHT_PURPLE);
        searchPanel.setBorder(BorderFactory.createTitledBorder("快速搜索民宿"));
        searchPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField cityField = new JTextField(12);
        JTextField dateField = new JTextField("2026-03-20", 12);
        JTextField peopleField = new JTextField("2", 4);

        JButton searchBtn = new JButton("搜索");
        searchBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));

        searchBtn.addActionListener(e -> {
            openSearchResult(
                    cityField.getText(),
                    dateField.getText(),
                    peopleField.getText()
            );
        });

        if (width < 700) {
            searchPanel.add(new JLabel("城市:"));
            searchPanel.add(cityField);
            searchPanel.add(new JLabel("日期:"));
            searchPanel.add(dateField);
            searchPanel.add(new JLabel("人数:"));
            searchPanel.add(peopleField);
            // 单独添加搜索按钮
            JPanel searchButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            searchButtonPanel.setBackground(AppColors.LIGHT_PURPLE);
            searchButtonPanel.add(searchBtn);
            mainPanel.add(searchPanel);
            mainPanel.add(searchButtonPanel);
        } else {
            searchPanel.add(new JLabel("城市:"));
            searchPanel.add(cityField);
            searchPanel.add(new JLabel("日期:"));
            searchPanel.add(dateField);
            searchPanel.add(new JLabel("人数:"));
            searchPanel.add(peopleField);
            searchPanel.add(searchBtn);
            mainPanel.add(searchPanel);
        }

        searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        // ================= 快捷按钮 =================
        JPanel quickPanel = new JPanel();
        // 根据宽度调整按钮布局
        int rows = 2;
        int cols = 3;
        if (width < 700) {
            rows = 3;
            cols = 2;
        }
        quickPanel.setLayout(new GridLayout(rows, cols, 20, 20));
        quickPanel.setBackground(AppColors.LIGHT_PURPLE);
        quickPanel.setBorder(BorderFactory.createEmptyBorder(20, padding, 20, padding));
        quickPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] buttons = {"浏览民宿", "搜索民宿", "我的订单", "我的评价", "个人信息", "立即预订"};

        for (String text : buttons) {
            JButton btn = new JButton(text);

            // 按钮大小自适应
            int btnWidth = Math.max(100, width / (cols + 3));
            int btnHeight = Math.max(50, width / 15);
            btn.setFont(new Font("微软雅黑", Font.BOLD, Math.min(16, width / 40)));
            btn.setPreferredSize(new Dimension(btnWidth, btnHeight));
            btn.setFocusPainted(false);

            btn.addActionListener(e -> {
                switch (text) {
                    case "浏览民宿": openAllHomestay(); break;
                    case "搜索民宿": openReservation(); break;
                    case "我的订单": openMyOrders(); break;
                    case "我的评价": openMyReviews(); break;
                    case "个人信息": openPersonalInfo(); break;
                    case "立即预订": openReservation(); break;
                }
            });

            quickPanel.add(btn);
        }

        // ================= 推荐民宿 =================
        JPanel recommendPanel = new JPanel();
        // 根据宽度调整推荐民宿布局
        int cardCols = 4;
        if (width < 900) cardCols = 3;
        if (width < 700) cardCols = 2;
        if (width < 400) cardCols = 1;
        recommendPanel.setLayout(new GridLayout(1, cardCols, 20, 15));
        recommendPanel.setBackground(AppColors.LIGHT_PURPLE);
        recommendPanel.setBorder(BorderFactory.createTitledBorder("推荐民宿"));
        recommendPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        List<Homestay> list = homestayService.getTopRatedHomestays(cardCols);

        if (list != null) {
            for (Homestay h : list) {

                JPanel card = new JPanel(new BorderLayout());
                card.setBorder(BorderFactory.createLineBorder(Color.GRAY));

                JLabel name = new JLabel(h.getName(), JLabel.CENTER);
                name.setFont(new Font("微软雅黑", Font.BOLD, Math.min(14, width / 40)));

                JButton btn = new JButton("查看");
                btn.setFont(new Font("微软雅黑", Font.PLAIN, 13));

                btn.addActionListener(e ->
                        openHomestayDetail(h.getHomestayId())
                );

                card.add(name, BorderLayout.CENTER);
                card.add(btn, BorderLayout.SOUTH);

                recommendPanel.add(card);
            }
        }

        // 卡片区域高度自适应
        recommendPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Math.min(300, width / 2)));

        // ================= 组装 =================
        mainPanel.add(welcomeLabel);
        mainPanel.add(Box.createVerticalStrut(15));

        if (width >= 700) {
            mainPanel.add(searchPanel);
        }
        mainPanel.add(Box.createVerticalStrut(20));

        mainPanel.add(quickPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        mainPanel.add(recommendPanel);

        // ================= 居中 =================
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapper.setBackground(AppColors.LIGHT_PURPLE);
        wrapper.add(mainPanel);

        contentPanel.add(wrapper, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ================= 跳转 =================

    private void openMyOrders() {
        new OrderListView(currentUser, "GUEST", currentUser.getUserId()).setVisible(true);
    }

    private void openMyReviews() {
        new ReviewView(currentUser, "GUEST", currentUser.getUserId()).setVisible(true);
    }

    private void openAllHomestay() {
        JOptionPane.showMessageDialog(this, "浏览民宿");
    }

    private void openPersonalInfo() {
        JOptionPane.showMessageDialog(this, "个人信息");
    }

    private void openReservation() {
        new ReservationView(currentUser).setVisible(true);
    }

    private void openSearchResult(String city, String date, String people) {
        JOptionPane.showMessageDialog(this,
                "搜索：" + city + " " + date + " " + people);
    }

    private void openHomestayDetail(int id) {
        JOptionPane.showMessageDialog(this, "查看民宿ID: " + id);
    }
}