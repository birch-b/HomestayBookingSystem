package com.booking.view;

import com.booking.model.User;
import com.booking.model.Homestay;
import com.booking.util.AppColors;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * 民宿搜索结果页面
 */
public class HomestaySearchResultView extends JFrame {

    private User currentUser;
    private String searchCity;
    private String checkIn;
    private String checkOut;
    private String people;
    private List<Homestay> homestays;

    public HomestaySearchResultView(User user, String city, String checkIn, String checkOut, String people, List<Homestay> homestays) {
        this.currentUser = user;
        this.searchCity = city;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.people = people;
        this.homestays = homestays;

        initUI();
    }

    private void initUI() {
        setTitle("\"" + searchCity + "\" 的民宿搜索结果");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(AppColors.LIGHT_PURPLE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 标题
        JLabel titleLabel = new JLabel("\"" + searchCity + "\" 的民宿搜索结果 (共" + homestays.size() + "家)", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        titleLabel.setForeground(AppColors.PRIMARY_PURPLE);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // 结果列表面板
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBackground(AppColors.LIGHT_PURPLE);

        for (Homestay h : homestays) {
            JPanel card = createHomestayCard(h);
            resultPanel.add(card);
            resultPanel.add(Box.createVerticalStrut(15));
        }

        JScrollPane scrollPane = new JScrollPane(resultPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // 底部按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(AppColors.LIGHT_PURPLE);

        JButton backBtn = new JButton("返回");
        backBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        backBtn.addActionListener(e -> dispose());

        buttonPanel.add(backBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHomestayCard(Homestay homestay) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.PRIMARY_PURPLE, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        // 左侧信息
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(homestay.getName());
        nameLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        nameLabel.setForeground(AppColors.DARK_PURPLE);

        JLabel addressLabel = new JLabel("地址：" + homestay.getAddress());
        addressLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        JLabel ratingLabel = new JLabel("评分：" + String.format("%.1f", homestay.getRating()) + " ★");
        ratingLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        ratingLabel.setForeground(Color.ORANGE);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(addressLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(ratingLabel);

        // 右侧按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton detailBtn = new JButton("查看详情");
        detailBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        detailBtn.addActionListener(e -> openHomestayDetail(homestay.getHomestayId()));

        buttonPanel.add(detailBtn);

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.EAST);

        return card;
    }

    private void openHomestayDetail(int id) {
        // 使用统一的民宿详情对话框
        HomestayDetailDialog.show(this, currentUser, id, checkIn, checkOut, people);
    }
}
