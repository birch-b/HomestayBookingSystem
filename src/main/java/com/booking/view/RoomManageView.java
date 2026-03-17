package com.booking.view;

import com.booking.model.Room;
import com.booking.model.User;
import com.booking.util.AppColors;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * 房间管理界面
 */
public class RoomManageView extends JFrame {

    private User currentUser;
    private int homestayId;  // 当前管理的民宿ID
    private String homestayName;  // 当前管理的民宿名称
    private List<Room> roomList;

    private JTable roomTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> typeFilter;
    private JComboBox<String> statusFilter;
    private JButton searchButton;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton backButton;

    public RoomManageView(User user, int homestayId, String homestayName) {
        this.currentUser = user;
        this.homestayId = homestayId;
        this.homestayName = homestayName;
        this.roomList = new ArrayList<>();
        initUI();
        loadData();
    }

    private void initUI() {
        setTitle("房间管理 - " + homestayName + " - " + currentUser.getRealName());
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(AppColors.LIGHT_PURPLE);

        // 标题
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(AppColors.LIGHT_PURPLE);

        JLabel titleLabel = new JLabel("房间管理 - " + homestayName, JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);

        backButton = new JButton("返回民宿列表");
        styleSmallButton(backButton);

        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(backButton, BorderLayout.EAST);

        // 筛选面板
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(AppColors.LIGHT_PURPLE);

        filterPanel.add(new JLabel("房间号:"));
        searchField = new JTextField(15);
        searchField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));

        filterPanel.add(new JLabel("房型:"));
        String[] types = {"全部", "单人间", "双人间", "大床房", "套房", "家庭房"};
        typeFilter = new JComboBox<>(types);
        typeFilter.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        typeFilter.setBackground(Color.WHITE);

        filterPanel.add(new JLabel("状态:"));
        String[] statuses = {"全部", "可用", "已订", "维护"};
        statusFilter = new JComboBox<>(statuses);
        statusFilter.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        statusFilter.setBackground(Color.WHITE);

        searchButton = new JButton("搜索");
        styleButton(searchButton);

        filterPanel.add(searchField);
        filterPanel.add(typeFilter);
        filterPanel.add(statusFilter);
        filterPanel.add(searchButton);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setBackground(AppColors.LIGHT_PURPLE);

        addButton = new JButton("新增房间");
        editButton = new JButton("编辑");
        deleteButton = new JButton("删除");
        refreshButton = new JButton("刷新");

        styleButton(addButton);
        styleButton(editButton);
        styleButton(deleteButton);
        styleButton(refreshButton);

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        // 合并顶部面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(AppColors.LIGHT_PURPLE);
        topPanel.add(filterPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // 表格
        String[] columns = {"ID", "房间号", "房型", "床型", "面积", "可住人数", "价格", "状态"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        roomTable = new JTable(tableModel);
        roomTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        roomTable.setRowHeight(25);
        roomTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 13));
        roomTable.getTableHeader().setBackground(AppColors.PRIMARY_PURPLE);
        roomTable.getTableHeader().setForeground(Color.BLACK);
        roomTable.setSelectionBackground(AppColors.HOVER_PURPLE);

        // 设置列宽
        roomTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        roomTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        roomTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        roomTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        roomTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        roomTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        roomTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        roomTable.getColumnModel().getColumn(7).setPreferredWidth(80);

        JScrollPane scrollPane = new JScrollPane(roomTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppColors.PRIMARY_PURPLE));

        // 底部统计
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(AppColors.LIGHT_PURPLE);
        JLabel statsLabel = new JLabel("总房间数: 0 | 可用: 0 | 已订: 0 | 维护: 0");
        statsLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        statsLabel.setForeground(AppColors.DARK_PURPLE);
        bottomPanel.add(statsLabel);

        // 组装界面
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // 添加事件监听
        addButton.addActionListener(e -> addRoom());
        editButton.addActionListener(e -> editRoom());
        deleteButton.addActionListener(e -> deleteRoom());
        refreshButton.addActionListener(e -> loadData());
        searchButton.addActionListener(e -> searchRoom());
        backButton.addActionListener(e -> {
            dispose();
            // 返回民宿管理界面
            new HomestayManageView(currentUser).setVisible(true);
        });
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        button.setBackground(AppColors.BUTTON_PURPLE);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(AppColors.HOVER_PURPLE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(AppColors.BUTTON_PURPLE);
            }
        });
    }

    private void styleSmallButton(JButton button) {
        button.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        button.setBackground(AppColors.BUTTON_PURPLE);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    private void loadData() {
        // TODO: 从Service加载数据
        tableModel.setRowCount(0);

        // 临时测试数据
        Object[] row1 = {1, "101", "大床房", "1.8米大床", 25, 2, 388.00, "可用"};
        Object[] row2 = {2, "102", "大床房", "1.8米大床", 28, 2, 428.00, "可用"};
        Object[] row3 = {3, "201", "标准间", "1.2米双床", 30, 2, 388.00, "可用"};
        Object[] row4 = {4, "301", "套房", "2米大床", 50, 3, 888.00, "已订"};
        Object[] row5 = {5, "302", "家庭房", "1.8米+1.2米", 45, 4, 688.00, "可用"};

        tableModel.addRow(row1);
        tableModel.addRow(row2);
        tableModel.addRow(row3);
        tableModel.addRow(row4);
        tableModel.addRow(row5);

        updateStats();
    }

    private void updateStats() {
        // 更新底部统计信息
        int total = tableModel.getRowCount();
        int available = 0;
        int booked = 0;
        int maintenance = 0;

        for (int i = 0; i < total; i++) {
            String status = (String) tableModel.getValueAt(i, 7);
            if ("可用".equals(status)) available++;
            else if ("已订".equals(status)) booked++;
            else if ("维护".equals(status)) maintenance++;
        }

        JPanel bottomPanel = (JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(3);
        JLabel statsLabel = (JLabel) bottomPanel.getComponent(0);
        statsLabel.setText(String.format("总房间数: %d | 可用: %d | 已订: %d | 维护: %d",
                total, available, booked, maintenance));
    }

    private void searchRoom() {
        String keyword = searchField.getText().trim();
        String type = (String) typeFilter.getSelectedItem();
        String status = (String) statusFilter.getSelectedItem();

        // TODO: 调用Service搜索
        StringBuilder msg = new StringBuilder("搜索条件:\n");
        if (!keyword.isEmpty()) msg.append("房间号: ").append(keyword).append("\n");
        if (!"全部".equals(type)) msg.append("房型: ").append(type).append("\n");
        if (!"全部".equals(status)) msg.append("状态: ").append(status);

        JOptionPane.showMessageDialog(this, msg.toString(), "搜索功能待实现", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addRoom() {
        JOptionPane.showMessageDialog(this, "新增房间功能待实现", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private void editRoom() {
        int row = roomTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要编辑的房间", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        String roomNumber = (String) tableModel.getValueAt(row, 1);
        JOptionPane.showMessageDialog(this, "编辑房间 " + roomNumber + " (ID:" + id + ") 功能待实现",
                "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteRoom() {
        int row = roomTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的房间", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String roomNumber = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this, "确定要删除房间 " + roomNumber + " 吗？",
                "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.removeRow(row);
            updateStats();
            JOptionPane.showMessageDialog(this, "删除成功（演示模式）", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}