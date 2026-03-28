package com.booking.view;

import com.booking.model.User;
import com.booking.model.Homestay;
import com.booking.service.HomestayService;
import com.booking.service.impl.HomestayServiceImpl;
import com.booking.util.AppColors;

import javax.swing.*;
// import javax.swing.event.ListSelectionEvent;
// import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.List;

/**
 * 游客界面（最终版：大布局 + 大按钮 + 固定3列 + 自适应宽度）
 */
public class GuestView extends MainView {

    private HomestayService homestayService;
    private JTextField cityField;

    public GuestView(User user) {
        super(user, "游客中心");
        setPreferredSize(new Dimension(1000, 578));
        setMinimumSize(new Dimension(850, 578));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 578));
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
        // int height = getHeight();

        // 明确设置窗口最大高度为578px
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 578));
        
        // ================= 主容器 =================
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(AppColors.LIGHT_PURPLE);
        // 适当内边距，保持美观
        int padding = Math.max(15, width / 30);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
        mainPanel.setMinimumSize(new Dimension(900, 550));

        // ================= 欢迎 =================
        JLabel welcomeLabel = new JLabel("欢迎游客 " + currentUser.getRealName(), JLabel.CENTER);
        // 适当字体大小
        welcomeLabel.setFont(new Font("微软雅黑", Font.BOLD, Math.min(24, width / 25)));
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

        // 适当输入框大小
        cityField = new JTextField(12);
        JTextField dateField = new JTextField("2026-03-20", 12);
        JTextField peopleField = new JTextField("2", 3);

        // 设置输入框字体大小
        Font inputFont = new Font("微软雅黑", Font.PLAIN, 14);
        cityField.setFont(inputFont);
        dateField.setFont(inputFont);
        peopleField.setFont(inputFont);

        JButton searchBtn = new JButton("搜索");
        // 搜索按钮样式 - 紫色主题
        searchBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        searchBtn.setBackground(AppColors.BUTTON_PURPLE);
        searchBtn.setForeground(AppColors.DARK_PURPLE);
        searchBtn.setFocusPainted(false);
        searchBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 添加悬停效果
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
            if (city.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入城市名称", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            searchHomestayByCity(city);
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

        // ================= 快捷按钮 =================
        JPanel quickPanel = new JPanel();
        // 根据宽度调整按钮布局
        int rows = 1;
        int cols = 4;
        if (width < 700) {
            rows = 2;
            cols = 2;
        }
        // 适当按钮间距
        quickPanel.setLayout(new GridLayout(rows, cols, 15, 15));
        quickPanel.setBackground(AppColors.LIGHT_PURPLE);
        // 适当外边距
        quickPanel.setBorder(BorderFactory.createEmptyBorder(15, padding, 15, padding));
        quickPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] buttons = {"浏览民宿", "我的订单", "我的评价", "个人信息"};

        for (String text : buttons) {
            JButton btn = new JButton(text);

            // 按钮样式参考AdminView
            btn.setFont(new Font("微软雅黑", Font.BOLD, 14));
            btn.setBackground(AppColors.BUTTON_PURPLE);
            btn.setForeground(AppColors.DARK_PURPLE);
            btn.setFocusPainted(false);
            int btnWidth = Math.max(120, width / (cols + 2));
            int btnHeight = Math.max(50, getHeight() / 12);
            btn.setPreferredSize(new Dimension(btnWidth, btnHeight));

            // 添加悬停效果
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btn.setBackground(AppColors.HOVER_PURPLE);
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btn.setBackground(AppColors.BUTTON_PURPLE);
                }
            });

            btn.addActionListener(e -> {
                switch (text) {
                    case "浏览民宿": openAllHomestay(); break;
                    case "搜索民宿": openAllHomestay(); break;
                    case "我的订单": openMyOrders(); break;
                    case "我的评价": openMyReviews(); break;
                    case "个人信息": openPersonalInfo(); break;
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
        // 适当卡片间距
        recommendPanel.setLayout(new GridLayout(1, cardCols, 15, 15));
        recommendPanel.setBackground(AppColors.LIGHT_PURPLE);
        recommendPanel.setBorder(BorderFactory.createTitledBorder("推荐民宿"));
        recommendPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        List<Homestay> list = homestayService.getTopRatedHomestays(cardCols);

        if (list != null) {
            for (Homestay h : list) {

                JPanel card = new JPanel(new BorderLayout());
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppColors.PRIMARY_PURPLE, 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                card.setBackground(Color.WHITE);
                // 设置卡片大小
                card.setPreferredSize(new Dimension(150, 120));

                // 民宿名称
                JLabel name = new JLabel(h.getName(), JLabel.CENTER);
                name.setFont(new Font("微软雅黑", Font.BOLD, Math.min(16, width / 35)));
                name.setForeground(AppColors.DARK_PURPLE);

                // 评分
                JLabel ratingLabel = new JLabel(String.format("%.1f★", h.getRating()), JLabel.CENTER);
                ratingLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
                ratingLabel.setForeground(Color.ORANGE);

                // 信息面板
                JPanel infoPanel = new JPanel(new GridLayout(2, 1));
                infoPanel.setBackground(Color.WHITE);
                infoPanel.add(name);
                infoPanel.add(ratingLabel);

                // 查看按钮 - 紫色主题
                JButton btn = new JButton("查看详情");
                btn.setFont(new Font("微软雅黑", Font.BOLD, 12));
                btn.setBackground(AppColors.BUTTON_PURPLE);
                btn.setForeground(AppColors.DARK_PURPLE);
                btn.setFocusPainted(false);
                btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

                // 添加悬停效果
                btn.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        btn.setBackground(AppColors.HOVER_PURPLE);
                    }
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        btn.setBackground(AppColors.BUTTON_PURPLE);
                    }
                });

                btn.addActionListener(e -> openHomestayDetail(h.getHomestayId()));

                card.add(infoPanel, BorderLayout.CENTER);
                card.add(btn, BorderLayout.SOUTH);

                recommendPanel.add(card);
            }
        }

        // ================= 组装 =================
        mainPanel.add(welcomeLabel);
        // 适当垂直间距
        mainPanel.add(Box.createVerticalStrut(20));

        if (width >= 700) {
            mainPanel.add(searchPanel);
        }
        // 适当垂直间距
        mainPanel.add(Box.createVerticalStrut(25));

        mainPanel.add(quickPanel);
        // 适当垂直间距
        mainPanel.add(Box.createVerticalStrut(25));

        mainPanel.add(recommendPanel);

        // ================= 居中 =================
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        // 滚动更顺滑
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        // 禁止横向滚动（更美观）
        scrollPane.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        contentPanel.add(scrollPane, BorderLayout.CENTER);
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
        // 打开独立的民宿列表页面
        new HomestayListView(currentUser).setVisible(true);
    }

    private void searchHomestayByCity(String city) {
        // 根据城市搜索民宿
        List<Homestay> homestays = homestayService.getHomestaysByCity(city, 1, 100);
        if (homestays == null || homestays.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "该城市没有对应民宿",
                "提示",
                JOptionPane.INFORMATION_MESSAGE);
            // 清空搜索框
            cityField.setText("");
        } else {
            // 打开搜索结果页面
            new HomestaySearchResultView(currentUser, city, homestays).setVisible(true);
        }
    }

    private void openPersonalInfo() {
        // 创建个人信息对话框
        JDialog dialog = new JDialog(this, "个人信息", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(AppColors.LIGHT_PURPLE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        
        // 添加用户信息
        JLabel nameLabel = new JLabel("姓名：" + currentUser.getRealName());
        nameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(nameLabel, gbc);
        
        JLabel phoneLabel = new JLabel("电话：" + currentUser.getPhone());
        phoneLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridy = 1;
        mainPanel.add(phoneLabel, gbc);
        
        JLabel emailLabel = new JLabel("邮箱：" + (currentUser.getEmail() != null ? currentUser.getEmail() : "未设置"));
        emailLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridy = 2;
        mainPanel.add(emailLabel, gbc);
        
        JLabel userIdLabel = new JLabel("用户ID：" + currentUser.getUserId());
        userIdLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridy = 3;
        mainPanel.add(userIdLabel, gbc);
        
        JLabel roleLabel = new JLabel("角色：" + getRoleName(currentUser.getRole()));
        roleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridy = 4;
        mainPanel.add(roleLabel, gbc);
        
        // 添加关闭按钮
        JButton closeBtn = new JButton("关闭");
        closeBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        closeBtn.addActionListener(e -> dialog.dispose());
        
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(closeBtn, gbc);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private String getRoleName(String role) {
        switch (role) {
            case "ADMIN": return "管理员";
            case "HOST": return "民宿主";
            case "GUEST": return "游客";
            default: return "未知";
        }
    }

    private void openReservation() {
        new ReservationView(currentUser).setVisible(true);
    }

    private void openSearchResult(String city, String date, String people) {
        JOptionPane.showMessageDialog(this,
                "搜索：" + city + " " + date + " " + people);
    }

    private void openHomestayDetail(int id) {
        // 使用统一的民宿详情对话框
        HomestayDetailDialog.show(this, currentUser, id);
    }
}