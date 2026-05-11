package com.booking.view;

import com.booking.model.User;
import com.booking.service.UserService;
import com.booking.service.impl.UserServiceImpl;
import com.booking.util.AppColors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 用户注册界面 - 粉紫色系
 */
public class RegisterView extends JDialog {

    private UserService userService;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField realNameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JComboBox<String> roleComboBox;
    private JButton registerButton;
    private JFrame parentFrame;

    public RegisterView(JFrame parent) {
        super(parent, "用户注册", true);
        this.userService = new UserServiceImpl();
        this.parentFrame = parent;
        initUI();
    }

    private void initUI() {
        setSize(450, 450);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(AppColors.LIGHT_PURPLE);

        JLabel titleLabel = new JLabel("用户注册", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 22));
        titleLabel.setForeground(AppColors.PRIMARY_PURPLE);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(AppColors.LIGHT_PURPLE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("用户名:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        usernameField = new JTextField(18);
        usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        formPanel.add(new JLabel("密码:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(18);
        passwordField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        formPanel.add(new JLabel("确认密码:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        confirmPasswordField = new JPasswordField(18);
        confirmPasswordField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        confirmPasswordField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));
        formPanel.add(confirmPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        formPanel.add(new JLabel("真实姓名:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        realNameField = new JTextField(18);
        realNameField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        realNameField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));
        formPanel.add(realNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        formPanel.add(new JLabel("手机号:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        phoneField = new JTextField(18);
        phoneField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        phoneField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));
        formPanel.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        formPanel.add(new JLabel("邮箱:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        emailField = new JTextField(18);
        emailField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        formPanel.add(new JLabel("注册角色:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        String[] roles = {"游客", "民宿主"};
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        roleComboBox.setBackground(Color.WHITE);
        formPanel.add(roleComboBox, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(AppColors.LIGHT_PURPLE);

        registerButton = new JButton("注册");
        registerButton.setPreferredSize(new Dimension(100, 35));
        registerButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        registerButton.setBackground(AppColors.BUTTON_PURPLE);
        registerButton.setForeground(AppColors.DARK_PURPLE);
        registerButton.setFocusPainted(false);
        registerButton.setBorder(BorderFactory.createEmptyBorder());

        registerButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                registerButton.setBackground(AppColors.HOVER_PURPLE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                registerButton.setBackground(AppColors.BUTTON_PURPLE);
            }
        });

        buttonPanel.add(registerButton);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });

        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmPasswordField.requestFocus();
            }
        });

        confirmPasswordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });

        // 登录提示标签（链接式按钮）
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        loginPanel.setBackground(AppColors.LIGHT_PURPLE);
        
        JLabel tipLabel = new JLabel("已经有账号？");
        tipLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        tipLabel.setForeground(AppColors.DARK_PURPLE);
        
        JButton loginButton = new JButton("点击登录");
        loginButton.setPreferredSize(new Dimension(90, 30));
        loginButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        loginButton.setBackground(AppColors.BUTTON_PURPLE);
        loginButton.setForeground(AppColors.DARK_PURPLE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder());
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(AppColors.HOVER_PURPLE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(AppColors.BUTTON_PURPLE);
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                if (parentFrame != null) {
                    parentFrame.setVisible(true);
                }
            }
        });

        loginPanel.add(tipLabel);
        loginPanel.add(loginButton);

        // 创建底部面板（包含按钮和登录提示）
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(AppColors.LIGHT_PURPLE);
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        bottomPanel.add(loginPanel, BorderLayout.SOUTH);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        getContentPane().setBackground(AppColors.LIGHT_PURPLE);
    }

    private void register() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String realName = realNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String role = (String) roleComboBox.getSelectedItem();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入用户名", "提示", JOptionPane.WARNING_MESSAGE);
            usernameField.requestFocus();
            return;
        }

        if (username.length() < 4) {
            JOptionPane.showMessageDialog(this, "用户名至少4个字符", "提示", JOptionPane.WARNING_MESSAGE);
            usernameField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入密码", "提示", JOptionPane.WARNING_MESSAGE);
            passwordField.requestFocus();
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "密码至少6个字符", "提示", JOptionPane.WARNING_MESSAGE);
            passwordField.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "两次输入的密码不一致", "提示", JOptionPane.WARNING_MESSAGE);
            confirmPasswordField.setText("");
            confirmPasswordField.requestFocus();
            return;
        }

        if (realName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入真实姓名", "提示", JOptionPane.WARNING_MESSAGE);
            realNameField.requestFocus();
            return;
        }

        if (!phone.isEmpty() && !phone.matches("^1[3-9]\\d{9}$")) {
            JOptionPane.showMessageDialog(this, "请输入正确的手机号码", "提示", JOptionPane.WARNING_MESSAGE);
            phoneField.requestFocus();
            return;
        }

        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            JOptionPane.showMessageDialog(this, "请输入正确的邮箱格式", "提示", JOptionPane.WARNING_MESSAGE);
            emailField.requestFocus();
            return;
        }

        String roleCode = "GUEST";
        if ("民宿主".equals(role)) {
            roleCode = "HOST";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRealName(realName);
        user.setPhone(phone);
        user.setEmail(email);
        user.setRole(roleCode);

        int result = userService.register(user);

        if (result == 1) {
            JOptionPane.showMessageDialog(this, "注册成功！请登录", "成功", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else if (result == -1) {
            JOptionPane.showMessageDialog(this, "用户名已存在", "注册失败", JOptionPane.WARNING_MESSAGE);
            usernameField.requestFocus();
        } else {
            JOptionPane.showMessageDialog(this, "注册失败，请重试", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void showRegisterDialog(JFrame parent) {
        RegisterView dialog = new RegisterView(parent);
        dialog.setVisible(true);
    }

    public static void showRegisterDialog() {
        showRegisterDialog(null);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RegisterView(null).setVisible(true);
            }
        });
    }
}