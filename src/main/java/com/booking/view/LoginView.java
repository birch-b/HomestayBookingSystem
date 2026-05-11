package com.booking.view;

import com.booking.model.User;
import com.booking.service.UserService;
import com.booking.service.impl.UserServiceImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.booking.util.AppColors;
/**
 * 登录界面 - 粉紫色系
 */
public class LoginView extends JFrame {

    private UserService userService;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton exitButton;
    private JButton registerButton;
    public LoginView() {
        this.userService = new UserServiceImpl();
        initUI();
    }

    public LoginView(UserService userService) {
        this.userService = userService;
        initUI();
    }

    private void initUI() {
        // 设置窗口基本属性
        setTitle("乡村旅游精品民宿预订与评价系统");
        setSize(500, 330);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(AppColors.LIGHT_PURPLE);

        // 标题
        JLabel titleLabel = new JLabel("民宿预订系统", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(AppColors.PRIMARY_PURPLE);

        // 表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(AppColors.LIGHT_PURPLE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // 用户名标签
        JLabel userLabel = new JLabel("用户名:");
        userLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        userLabel.setForeground(AppColors.PRIMARY_PURPLE);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(userLabel, gbc);

        // 用户名输入框
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        usernameField = new JTextField(15);
        usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));
        formPanel.add(usernameField, gbc);

        // 密码标签
        JLabel passLabel = new JLabel("密码:");
        passLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        passLabel.setForeground(AppColors.PRIMARY_PURPLE);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        formPanel.add(passLabel, gbc);

        // 密码输入框
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));
        formPanel.add(passwordField, gbc);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(AppColors.LIGHT_PURPLE);

        loginButton = new JButton("登录");
        loginButton.setPreferredSize(new Dimension(100, 35));
        loginButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        loginButton.setBackground(Color.WHITE);
        loginButton.setForeground(AppColors.PRIMARY_PURPLE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder());

        // 登录按钮悬停效果
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(AppColors.PRIMARY_PURPLE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(AppColors.HOVER_PURPLE);
            }
        });

        exitButton = new JButton("退出");
        exitButton.setPreferredSize(new Dimension(100, 35));
        exitButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        exitButton.setBackground(Color.WHITE);
        exitButton.setForeground(AppColors.PRIMARY_PURPLE);
        exitButton.setFocusPainted(false);
        exitButton.setBorder(BorderFactory.createLineBorder(AppColors.HOVER_PURPLE));

        buttonPanel.add(loginButton);
        buttonPanel.add(exitButton);

        // 添加事件监听
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // 回车键登录
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        // 注册提示标签（链接式按钮）
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        registerPanel.setBackground(AppColors.LIGHT_PURPLE);
        
        JLabel tipLabel = new JLabel("还没有账号？");
        tipLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        tipLabel.setForeground(AppColors.DARK_PURPLE);
        
        JButton registerLinkButton = new JButton("点击注册");
        registerLinkButton.setPreferredSize(new Dimension(90, 30));
        registerLinkButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        registerLinkButton.setBackground(AppColors.BUTTON_PURPLE);
        registerLinkButton.setForeground(AppColors.DARK_PURPLE);
        registerLinkButton.setFocusPainted(false);
        registerLinkButton.setBorder(BorderFactory.createEmptyBorder());
        registerLinkButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        registerLinkButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                registerLinkButton.setBackground(AppColors.HOVER_PURPLE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                registerLinkButton.setBackground(AppColors.BUTTON_PURPLE);
            }
        });

        registerLinkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                RegisterView.showRegisterDialog(LoginView.this);
                setVisible(true);
            }
        });

        registerPanel.add(tipLabel);
        registerPanel.add(registerLinkButton);

        // 创建底部面板（包含按钮和注册提示）
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(AppColors.LIGHT_PURPLE);
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        bottomPanel.add(registerPanel, BorderLayout.SOUTH);

        // 组装界面
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // 设置整体背景色
        getContentPane().setBackground(AppColors.LIGHT_PURPLE);
    }

    /**
     * 登录方法
     */
    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // 输入验证
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入用户名", "提示", JOptionPane.WARNING_MESSAGE);
            usernameField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入密码", "提示", JOptionPane.WARNING_MESSAGE);
            passwordField.requestFocus();
            return;
        }

        // 调用Service层进行登录验证
        User user = userService.login(username, password);

        if (user != null) {
            JOptionPane.showMessageDialog(this, "登录成功！欢迎 " + user.getRealName(),
                    "成功", JOptionPane.INFORMATION_MESSAGE);

            // 根据角色跳转到不同的主界面
            dispose();  // 关闭登录窗口
            openMainView(user);
        } else {
            JOptionPane.showMessageDialog(this, "用户名或密码错误", "登录失败",
                    JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }

    /**
     * 根据用户角色打开不同的主界面
     */
    private void openMainView(User user) {
        switch (user.getRole()) {
            case "ADMIN":
                new AdminView(user).setVisible(true);
                break;
            case "HOST":
                new HostView(user).setVisible(true);
                break;
            case "GUEST":
                new GuestView(user).setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(this, "未知的用户角色", "错误",
                        JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 主方法（用于测试）
     */
    public static void main(String[] args) {
        // 设置Swing外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginView().setVisible(true);
            }
        });
    }
}