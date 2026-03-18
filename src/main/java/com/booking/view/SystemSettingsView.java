package com.booking.view;

import com.booking.model.User;
import com.booking.util.AppColors;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Properties;

/**
 * 系统设置界面
 */
public class SystemSettingsView extends JFrame {

    private User currentUser;
    private JTextField siteNameField;
    private JTextField contactEmailField;
    private JTextField contactPhoneField;
    private JCheckBox enableRegisterCheck;
    private JCheckBox enableReviewCheck;
    private JComboBox<String> themeCombo;
    private JButton saveButton;
    private JButton cancelButton;
    private JButton backButton;
    
    private Properties configProps;
    private static final String CONFIG_FILE = "config.properties";

    public SystemSettingsView(User user) {
        this.currentUser = user;
        this.configProps = new Properties();
        loadConfig();
        initUI();
        loadSettings();
    }

    /**
     * 从配置文件加载配置
     */
    private void loadConfig() {
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                configProps.load(fis);
                System.out.println("配置文件加载成功");
            } catch (IOException e) {
                System.out.println("配置文件加载失败，使用默认值");
                setDefaultConfig();
            }
        } else {
            System.out.println("配置文件不存在，使用默认值");
            setDefaultConfig();
        }
    }

    /**
     * 设置默认配置
     */
    private void setDefaultConfig() {
        configProps.setProperty("site.name", "乡村旅游精品民宿预订系统");
        configProps.setProperty("contact.email", "admin@homestay.com");
        configProps.setProperty("contact.phone", "400-123-4567");
        configProps.setProperty("register.enabled", "true");
        configProps.setProperty("review.enabled", "true");
        configProps.setProperty("theme", "粉紫色（默认）");
    }

    /**
     * 保存配置到文件
     */
    private void saveConfig() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            configProps.store(fos, "System Configuration");
            System.out.println("配置文件保存成功");
            return;
        } catch (IOException e) {
            System.out.println("配置文件保存失败: " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "保存失败: " + e.getMessage(), 
                "错误", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initUI() {
        setTitle("系统设置 - " + currentUser.getRealName());
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(AppColors.LIGHT_PURPLE);

        // 标题
        JLabel titleLabel = new JLabel("系统设置", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(AppColors.PRIMARY_PURPLE);

        // 设置面板
        JPanel settingsPanel = new JPanel(new GridBagLayout());
        settingsPanel.setBackground(AppColors.LIGHT_PURPLE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 站点名称
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        settingsPanel.add(new JLabel("站点名称:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        siteNameField = new JTextField(20);
        siteNameField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        siteNameField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));
        settingsPanel.add(siteNameField, gbc);

        // 联系邮箱
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        settingsPanel.add(new JLabel("联系邮箱:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        contactEmailField = new JTextField(20);
        contactEmailField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        contactEmailField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));
        settingsPanel.add(contactEmailField, gbc);

        // 联系电话
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        settingsPanel.add(new JLabel("联系电话:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        contactPhoneField = new JTextField(20);
        contactPhoneField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        contactPhoneField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));
        settingsPanel.add(contactPhoneField, gbc);

        // 允许注册
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        settingsPanel.add(new JLabel("允许注册:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        enableRegisterCheck = new JCheckBox("允许新用户注册");
        enableRegisterCheck.setBackground(AppColors.LIGHT_PURPLE);
        enableRegisterCheck.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        settingsPanel.add(enableRegisterCheck, gbc);

        // 允许评价
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        settingsPanel.add(new JLabel("评价设置:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        enableReviewCheck = new JCheckBox("入住后可评价");
        enableReviewCheck.setBackground(AppColors.LIGHT_PURPLE);
        enableReviewCheck.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        settingsPanel.add(enableReviewCheck, gbc);

        // 主题设置
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        settingsPanel.add(new JLabel("主题颜色:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        String[] themes = {"粉紫色（默认）", "蓝色系", "绿色系", "橙色系"};
        themeCombo = new JComboBox<>(themes);
        themeCombo.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        themeCombo.setBackground(Color.WHITE);
        settingsPanel.add(themeCombo, gbc);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(AppColors.LIGHT_PURPLE);

        saveButton = new JButton("保存设置");
        cancelButton = new JButton("取消");
        backButton = new JButton("返回");

        styleButton(saveButton);
        styleButton(cancelButton);
        styleButton(backButton);

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(backButton);

        // 组装界面
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(settingsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // 事件监听
        saveButton.addActionListener(e -> saveSettings());
        cancelButton.addActionListener(e -> dispose());
        backButton.addActionListener(e -> dispose());
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("微软雅黑", Font.BOLD, 12));
        button.setBackground(AppColors.BUTTON_PURPLE);
        button.setForeground(AppColors.DARK_PURPLE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setPreferredSize(new Dimension(100, 30));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(AppColors.HOVER_PURPLE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(AppColors.BUTTON_PURPLE);
            }
        });
    }

    /**
     * 从配置加载到界面
     */
    private void loadSettings() {
        siteNameField.setText(configProps.getProperty("site.name"));
        contactEmailField.setText(configProps.getProperty("contact.email"));
        contactPhoneField.setText(configProps.getProperty("contact.phone"));
        enableRegisterCheck.setSelected(Boolean.parseBoolean(configProps.getProperty("register.enabled")));
        enableReviewCheck.setSelected(Boolean.parseBoolean(configProps.getProperty("review.enabled")));
        
        String theme = configProps.getProperty("theme");
        for (int i = 0; i < themeCombo.getItemCount(); i++) {
            if (themeCombo.getItemAt(i).equals(theme)) {
                themeCombo.setSelectedIndex(i);
                break;
            }
        }
    }

    /**
     * 保存设置到配置文件
     */
    private void saveSettings() {
        // 从界面获取值
        String siteName = siteNameField.getText().trim();
        String email = contactEmailField.getText().trim();
        String phone = contactPhoneField.getText().trim();
        boolean registerEnabled = enableRegisterCheck.isSelected();
        boolean reviewEnabled = enableReviewCheck.isSelected();
        String theme = (String) themeCombo.getSelectedItem();

        // 验证输入
        if (siteName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "站点名称不能为空", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "联系邮箱不能为空", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "联系电话不能为空", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 保存到Properties对象
        configProps.setProperty("site.name", siteName);
        configProps.setProperty("contact.email", email);
        configProps.setProperty("contact.phone", phone);
        configProps.setProperty("register.enabled", String.valueOf(registerEnabled));
        configProps.setProperty("review.enabled", String.valueOf(reviewEnabled));
        configProps.setProperty("theme", theme);

        // 保存到文件
        saveConfig();

        // 显示成功消息
        String message = String.format(
                "设置已保存到配置文件\n\n" +
                "站点名称: %s\n" +
                "联系邮箱: %s\n" +
                "联系电话: %s\n" +
                "允许注册: %s\n" +
                "允许评价: %s\n" +
                "主题: %s\n\n" +
                "配置文件路径: %s",
                siteName, email, phone, 
                registerEnabled ? "是" : "否",
                reviewEnabled ? "是" : "否", 
                theme,
                new File(CONFIG_FILE).getAbsolutePath());

        JOptionPane.showMessageDialog(this, message, "保存成功", JOptionPane.INFORMATION_MESSAGE);
    }
}