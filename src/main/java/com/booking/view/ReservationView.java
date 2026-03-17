package com.booking.view;

import com.booking.model.Room;
import com.booking.model.User;
import com.booking.util.AppColors;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 预订界面 - 游客预订房间
 */
public class ReservationView extends JFrame {

    private User currentUser;
    private List<Room> availableRooms;

    private JTextField cityField;
    private JTextField checkInField;
    private JTextField checkOutField;
    private JTextField peopleField;
    private JButton searchButton;
    private JTable roomTable;
    private DefaultTableModel tableModel;
    private JButton reserveButton;
    private JButton backButton;
    private JLabel selectedRoomLabel;

    private int selectedRoomId = -1;
    private String selectedRoomInfo = "";

    public ReservationView(User user) {
        this.currentUser = user;
        this.availableRooms = new ArrayList<>();
        initUI();
    }

    private void initUI() {
        setTitle("预订房间 - " + currentUser.getRealName());
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(AppColors.LIGHT_PURPLE);

        // 标题
        JLabel titleLabel = new JLabel("预订房间", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(AppColors.PRIMARY_PURPLE);

        // 搜索面板
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(AppColors.LIGHT_PURPLE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 城市
        gbc.gridx = 0;
        gbc.gridy = 0;
        searchPanel.add(new JLabel("城市:"), gbc);

        gbc.gridx = 1;
        cityField = new JTextField(15);
        cityField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        cityField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));
        searchPanel.add(cityField, gbc);

        // 入住日期
        gbc.gridx = 2;
        searchPanel.add(new JLabel("入住:"), gbc);

        gbc.gridx = 3;
        checkInField = new JTextField(10);
        checkInField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        checkInField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));
        checkInField.setText(getCurrentDate());
        searchPanel.add(checkInField, gbc);

        // 离店日期
        gbc.gridx = 4;
        searchPanel.add(new JLabel("离店:"), gbc);

        gbc.gridx = 5;
        checkOutField = new JTextField(10);
        checkOutField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        checkOutField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));
        checkOutField.setText(getTomorrowDate());
        searchPanel.add(checkOutField, gbc);

        // 人数
        gbc.gridx = 6;
        searchPanel.add(new JLabel("人数:"), gbc);

        gbc.gridx = 7;
        peopleField = new JTextField(5);
        peopleField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        peopleField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));
        peopleField.setText("2");
        searchPanel.add(peopleField, gbc);

        // 搜索按钮
        gbc.gridx = 8;
        searchButton = new JButton("搜索");
        styleButton(searchButton);
        searchPanel.add(searchButton, gbc);

        // 提示标签
        JLabel tipLabel = new JLabel("日期格式: yyyy-MM-dd (例如: 2026-03-20)");
        tipLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        tipLabel.setForeground(AppColors.DARK_PURPLE);

        // 表格
        String[] columns = {"选择", "ID", "民宿", "房间号", "房型", "可住人数", "价格/晚", "状态"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // 只有选择列可编辑
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Boolean.class;
                return super.getColumnClass(columnIndex);
            }
        };

        roomTable = new JTable(tableModel);
        roomTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        roomTable.setRowHeight(25);
        roomTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 13));
        roomTable.getTableHeader().setBackground(AppColors.PRIMARY_PURPLE);
        roomTable.getTableHeader().setForeground(Color.WHITE);
        roomTable.setSelectionBackground(AppColors.HOVER_PURPLE);

        // 设置列宽
        roomTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        roomTable.getColumnModel().getColumn(1).setPreferredWidth(50);
        roomTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        roomTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        roomTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        roomTable.getColumnModel().getColumn(5).setPreferredWidth(70);
        roomTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        roomTable.getColumnModel().getColumn(7).setPreferredWidth(80);

        // 选择事件
        roomTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = roomTable.getSelectedRow();
                if (row != -1) {
                    Boolean selected = (Boolean) tableModel.getValueAt(row, 0);
                    // 单选：取消其他选择
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        if (i != row) {
                            tableModel.setValueAt(false, i, 0);
                        }
                    }
                    tableModel.setValueAt(selected == null ? true : !selected, row, 0);

                    if (selected == null || !selected) {
                        selectedRoomId = (int) tableModel.getValueAt(row, 1);
                        selectedRoomInfo = tableModel.getValueAt(row, 2) + " " +
                                tableModel.getValueAt(row, 3) + " " +
                                tableModel.getValueAt(row, 4);
                        selectedRoomLabel.setText("已选房间: " + selectedRoomInfo);
                    } else {
                        selectedRoomId = -1;
                        selectedRoomInfo = "";
                        selectedRoomLabel.setText("已选房间: 未选择");
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(roomTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppColors.PRIMARY_PURPLE));

        // 底部面板
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(AppColors.LIGHT_PURPLE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        selectedRoomLabel = new JLabel("已选房间: 未选择");
        selectedRoomLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        selectedRoomLabel.setForeground(AppColors.DARK_PURPLE);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(AppColors.LIGHT_PURPLE);

        reserveButton = new JButton("立即预订");
        backButton = new JButton("返回");

        styleButton(reserveButton);
        styleButton(backButton);

        buttonPanel.add(reserveButton);
        buttonPanel.add(backButton);

        bottomPanel.add(selectedRoomLabel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        // 组装界面
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(AppColors.LIGHT_PURPLE);
        northPanel.add(searchPanel, BorderLayout.CENTER);
        northPanel.add(tipLabel, BorderLayout.SOUTH);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(northPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // 事件监听
        searchButton.addActionListener(e -> searchRooms());
        reserveButton.addActionListener(e -> reserveRoom());
        backButton.addActionListener(e -> dispose());

        // 初始加载数据
        loadTestData();
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        button.setBackground(AppColors.BUTTON_PURPLE);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(AppColors.HOVER_PURPLE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(AppColors.BUTTON_PURPLE);
            }
        });
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    private String getTomorrowDate() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(cal.getTime());
    }

    private void loadTestData() {
        tableModel.setRowCount(0);

        Object[] row1 = {false, 1, "云中山居", "101", "大床房", 2, 388.00, "可用"};
        Object[] row2 = {false, 2, "云中山居", "102", "大床房", 2, 428.00, "可用"};
        Object[] row3 = {false, 3, "云中山居", "201", "标准间", 2, 388.00, "可用"};
        Object[] row4 = {false, 4, "海边小筑", "A01", "海景房", 2, 588.00, "可用"};
        Object[] row5 = {false, 5, "海边小筑", "B01", "套房", 2, 1288.00, "可用"};

        tableModel.addRow(row1);
        tableModel.addRow(row2);
        tableModel.addRow(row3);
        tableModel.addRow(row4);
        tableModel.addRow(row5);
    }

    private void searchRooms() {
        String city = cityField.getText().trim();
        String checkIn = checkInField.getText().trim();
        String checkOut = checkOutField.getText().trim();
        String people = peopleField.getText().trim();

        // TODO: 调用Service搜索可用房间
        StringBuilder msg = new StringBuilder("搜索条件:\n");
        msg.append("城市: ").append(city).append("\n");
        msg.append("入住: ").append(checkIn).append("\n");
        msg.append("离店: ").append(checkOut).append("\n");
        msg.append("人数: ").append(people);

        JOptionPane.showMessageDialog(this, msg.toString(), "搜索功能待实现", JOptionPane.INFORMATION_MESSAGE);

        // 演示：还是加载测试数据
        loadTestData();
    }

    private void reserveRoom() {
        if (selectedRoomId == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要预订的房间", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String checkIn = checkInField.getText().trim();
        String checkOut = checkOutField.getText().trim();
        String people = peopleField.getText().trim();

        if (checkIn.isEmpty() || checkOut.isEmpty() || people.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请填写完整的日期和人数", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // TODO: 跳转到订单确认界面
        int confirm = JOptionPane.showConfirmDialog(this,
                "确认预订以下房间？\n\n" +
                        selectedRoomInfo + "\n" +
                        "入住: " + checkIn + "\n" +
                        "离店: " + checkOut + "\n" +
                        "人数: " + people,
                "确认预订", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "预订成功！（演示模式）", "成功", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }
    }
}