package com.booking.view;

import com.booking.model.CheckinRecord;
import com.booking.model.User;
import com.booking.service.CheckinRecordService;
import com.booking.service.ReservationService;
import com.booking.service.impl.CheckinRecordServiceImpl;
import com.booking.service.impl.ReservationServiceImpl;
import com.booking.util.AppColors;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 今日入住管理界面
 */
public class TodayCheckInView extends JFrame {

    private User currentUser;
    private CheckinRecordService checkinRecordService;
    private ReservationService reservationService;
    private List<CheckinRecord> checkinList;

    private JTable checkinTable;
    private DefaultTableModel tableModel;
    private JButton checkInButton;
    private JButton checkOutButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton backButton;
    private JButton toggleViewButton; // 切换今日/历史视图
    private JLabel statsLabel;
    private boolean showHistory = false; // 是否显示历史记录

    public TodayCheckInView(User user) {
        this.currentUser = user;
        this.checkinRecordService = new CheckinRecordServiceImpl();
        this.reservationService = new ReservationServiceImpl();
        initUI();
        loadData();
    }

    private void initUI() {
        setTitle("今日入住管理 - " + currentUser.getRealName());
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(AppColors.LIGHT_PURPLE);

        // 标题
        JLabel titleLabel = new JLabel("今日入住管理", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        titleLabel.setForeground(AppColors.DARK_PURPLE);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        buttonPanel.setBackground(AppColors.LIGHT_PURPLE);

        checkInButton = new JButton("办理入住");
        checkOutButton = new JButton("办理退房");
        deleteButton = new JButton("删除记录");
        refreshButton = new JButton("刷新");
        backButton = new JButton("返回");
        toggleViewButton = new JButton("查看历史");

        styleButton(checkInButton);
        styleButton(checkOutButton);
        styleButton(deleteButton);
        styleButton(refreshButton);
        styleButton(backButton);
        styleButton(toggleViewButton);

        buttonPanel.add(checkInButton);
        buttonPanel.add(checkOutButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(toggleViewButton);
        buttonPanel.add(backButton);

        // 合并顶部面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(AppColors.LIGHT_PURPLE);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);

        // 表格
        String[] columns = {"记录ID", "订单号", "客人姓名", "房间号", "民宿名称", "预订入住", "实际入住", "押金", "状态"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        checkinTable = new JTable(tableModel);
        checkinTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        checkinTable.setRowHeight(25);
        checkinTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 13));
        checkinTable.getTableHeader().setBackground(AppColors.PRIMARY_PURPLE);
        checkinTable.getTableHeader().setForeground(AppColors.DARK_PURPLE);
        checkinTable.setSelectionBackground(AppColors.HOVER_PURPLE);

        // 设置列宽
        checkinTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        checkinTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        checkinTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        checkinTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        checkinTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        checkinTable.getColumnModel().getColumn(5).setPreferredWidth(120);
        checkinTable.getColumnModel().getColumn(6).setPreferredWidth(120);
        checkinTable.getColumnModel().getColumn(7).setPreferredWidth(80);
        checkinTable.getColumnModel().getColumn(8).setPreferredWidth(80);

        JScrollPane scrollPane = new JScrollPane(checkinTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppColors.PRIMARY_PURPLE));

        // 底部统计信息
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(AppColors.LIGHT_PURPLE);
        statsLabel = new JLabel("今日入住: 0 人");
        statsLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        statsLabel.setForeground(AppColors.DARK_PURPLE);
        bottomPanel.add(statsLabel);

        // 组装界面
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // 事件监听
        checkInButton.addActionListener(e -> checkIn());
        checkOutButton.addActionListener(e -> checkOut());
        deleteButton.addActionListener(e -> deleteRecord());
        refreshButton.addActionListener(e -> loadData());
        backButton.addActionListener(e -> dispose());
        toggleViewButton.addActionListener(e -> toggleView());
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        button.setBackground(AppColors.BUTTON_PURPLE);
        button.setForeground(AppColors.DARK_PURPLE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        button.setPreferredSize(new Dimension(100, 22));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(AppColors.HOVER_PURPLE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(AppColors.BUTTON_PURPLE);
            }
        });
    }

    private void loadData() {
        tableModel.setRowCount(0);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        if (showHistory) {
            // 获取所有已入住但未退房的历史记录
            checkinList = checkinRecordService.getCheckedInRecords();
            
            for (CheckinRecord record : checkinList) {
                Object[] row = {
                    record.getRecordId(),
                    record.getReservation() != null ? record.getReservation().getReservationId() : "",
                    record.getGuest() != null ? record.getGuest().getRealName() : "",
                    record.getRoom() != null ? record.getRoom().getRoomNumber() : "",
                    record.getHomestay() != null ? record.getHomestay().getName() : "",
                    record.getReservation() != null && record.getReservation().getCheckInDate() != null ? 
                        sdf.format(record.getReservation().getCheckInDate()) : "",
                    record.getActualCheckIn() != null ? sdf.format(record.getActualCheckIn()) : "",
                    record.getDeposit(),
                    "已入住"
                };
                tableModel.addRow(row);
            }
        } else {
            // 获取今日入住记录
            checkinList = checkinRecordService.getTodayCheckIn();
            
            for (CheckinRecord record : checkinList) {
                String status = record.getActualCheckIn() != null ? "已入住" : "待入住";
                
                Object[] row = {
                    record.getRecordId(),
                    record.getReservation() != null ? record.getReservation().getReservationId() : "",
                    record.getGuest() != null ? record.getGuest().getRealName() : "",
                    record.getRoom() != null ? record.getRoom().getRoomNumber() : "",
                    record.getHomestay() != null ? record.getHomestay().getName() : "",
                    record.getReservation() != null && record.getReservation().getCheckInDate() != null ? 
                        sdf.format(record.getReservation().getCheckInDate()) : "",
                    record.getActualCheckIn() != null ? sdf.format(record.getActualCheckIn()) : "",
                    record.getDeposit(),
                    status
                };
                tableModel.addRow(row);
            }
        }
        
        updateStats();
    }

    private void updateStats() {
        int count = checkinList.size();
        statsLabel.setText("今日入住: " + count + " 人");
    }

    private void checkIn() {
        int row = checkinTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一条记录", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int recordId = (int) tableModel.getValueAt(row, 0);
        Integer reservationId = null;
        
        // 尝试获取记录
        CheckinRecord record = checkinRecordService.getRecordById(recordId);
        
        if (record == null) {
            // 对于临时记录，从表格中获取reservationId
            Object reservationIdObj = tableModel.getValueAt(row, 1);
            if (reservationIdObj instanceof Integer) {
                reservationId = (Integer) reservationIdObj;
            } else {
                JOptionPane.showMessageDialog(this, "无法获取订单信息", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            // 对于已有记录，检查是否已经办理入住
            if (record.getActualCheckIn() != null) {
                JOptionPane.showMessageDialog(this, "该记录已经办理入住", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            reservationId = record.getReservationId();
        }
        
        // 创建final副本用于lambda表达式
        final Integer finalReservationId = reservationId;

        // 创建办理入住对话框
        JDialog dialog = new JDialog(this, "办理入住", true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);
        
        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(AppColors.LIGHT_PURPLE);
        
        // 表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(AppColors.LIGHT_PURPLE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 押金
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("押金:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextField depositField = new JTextField(10);
        depositField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        depositField.setText("0.0");
        formPanel.add(depositField, gbc);
        
        // 房卡数量
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("房卡数量:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextField roomKeysField = new JTextField(10);
        roomKeysField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        roomKeysField.setText("1");
        formPanel.add(roomKeysField, gbc);
        
        // 备注
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("备注:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextArea remarksArea = new JTextArea(3, 20);
        remarksArea.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        remarksArea.setLineWrap(true);
        remarksArea.setWrapStyleWord(true);
        JScrollPane remarksScrollPane = new JScrollPane(remarksArea);
        remarksScrollPane.setPreferredSize(new Dimension(200, 60));
        formPanel.add(remarksScrollPane, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(AppColors.LIGHT_PURPLE);
        
        JButton saveButton = new JButton("保存");
        JButton cancelButton = new JButton("取消");
        
        styleButton(saveButton);
        styleButton(cancelButton);
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // 组装界面
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setContentPane(mainPanel);
        
        // 保存按钮事件
        saveButton.addActionListener(e -> {
            try {
                double deposit = Double.parseDouble(depositField.getText().trim());
                int roomKeys = Integer.parseInt(roomKeysField.getText().trim());
                String remarks = remarksArea.getText().trim();
                
                int result = checkinRecordService.checkIn(finalReservationId, deposit, roomKeys, remarks);
                
                if (result == 1) {
                    JOptionPane.showMessageDialog(dialog, "办理入住成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadData();
                } else if (result == -1) {
                    JOptionPane.showMessageDialog(dialog, "订单状态错误，无法办理入住", "错误", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(dialog, "办理入住失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "请输入有效的数字", "提示", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        // 取消按钮事件
        cancelButton.addActionListener(e -> {
            dialog.dispose();
        });
        
        dialog.setVisible(true);
    }

    /**
     * 删除入住记录
     */
    private void deleteRecord() {
        int row = checkinTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一条记录", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int recordId = (int) tableModel.getValueAt(row, 0);
        int reservationId = (int) tableModel.getValueAt(row, 1);
        String status = (String) tableModel.getValueAt(row, 8);

        // 确认删除
        int confirm = JOptionPane.showConfirmDialog(this, 
            "确定要删除这条入住记录吗？\n记录ID: " + recordId + "\n订单号: " + reservationId, 
            "确认删除", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // 1. 删除入住记录
            int result = checkinRecordService.deleteRecord(recordId);
            
            if (result > 0) {
                // 2. 更新订单状态为"已支付"（PAID）
                boolean updateResult = reservationService.updateReservationStatus(reservationId, "PAID");
                
                if (updateResult) {
                    JOptionPane.showMessageDialog(this, "删除成功！订单状态已重置为已支付", "成功", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "删除成功，但订单状态更新失败", "警告", JOptionPane.WARNING_MESSAGE);
                }
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 办理退房
     */
    private void checkOut() {
        int row = checkinTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一条记录", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int recordId = (int) tableModel.getValueAt(row, 0);
        String status = (String) tableModel.getValueAt(row, 8);

        // 检查是否已经入住
        if (!"已入住".equals(status)) {
            JOptionPane.showMessageDialog(this, "该记录尚未办理入住，无法退房", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 创建退房对话框
        JDialog dialog = new JDialog(this, "办理退房", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(AppColors.LIGHT_PURPLE);

        // 表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(AppColors.LIGHT_PURPLE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 退还押金
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("退还押金:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextField depositReturnField = new JTextField(10);
        depositReturnField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        depositReturnField.setText("0.0");
        formPanel.add(depositReturnField, gbc);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(AppColors.LIGHT_PURPLE);

        JButton confirmButton = new JButton("确认退房");
        JButton cancelButton = new JButton("取消");

        styleButton(confirmButton);
        styleButton(cancelButton);

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        // 组装界面
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(mainPanel);

        // 确认按钮事件
        confirmButton.addActionListener(e -> {
            try {
                double depositReturn = Double.parseDouble(depositReturnField.getText().trim());

                // 调用退房服务
                boolean result = checkinRecordService.checkOut(recordId, depositReturn);

                if (result) {
                    JOptionPane.showMessageDialog(dialog, "退房成功！订单状态已更新为已完成", "成功", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "退房失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "请输入有效的数字", "提示", JOptionPane.WARNING_MESSAGE);
            }
        });

        // 取消按钮事件
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    /**
     * 切换今日/历史视图
     */
    private void toggleView() {
        showHistory = !showHistory;
        if (showHistory) {
            toggleViewButton.setText("查看今日");
            setTitle("历史入住管理 - " + currentUser.getRealName());
        } else {
            toggleViewButton.setText("查看历史");
            setTitle("今日入住管理 - " + currentUser.getRealName());
        }
        loadData();
    }
}
