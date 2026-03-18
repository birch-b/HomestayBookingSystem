package com.booking.view;

import com.booking.model.Room;
import com.booking.model.User;
import com.booking.service.RoomService;
import com.booking.service.HomestayService;
import com.booking.service.impl.RoomServiceImpl;
import com.booking.service.impl.HomestayServiceImpl;
import com.booking.util.AppColors;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * 房间管理界面
 */
public class RoomManageView extends JFrame {

    private User currentUser;
    private int homestayId;
    private String homestayName;
    private RoomService roomService;
    // private HomestayService homestayService;
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
        this.roomService = new RoomServiceImpl();
        // this.homestayService = new HomestayServiceImpl();
        initUI();
        loadData();
    }

    private void initUI() {
        setTitle("房间管理 - " + homestayName + " - " + currentUser.getRealName());
        setSize(900, 600);
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
        titleLabel.setForeground(AppColors.PRIMARY_PURPLE);

        backButton = new JButton("返回民宿列表");
        styleButton(backButton);
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
        String[] statuses = {"全部", "AVAILABLE", "BOOKED", "MAINTENANCE"};
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
        roomTable.getTableHeader().setForeground(Color.WHITE);
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
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // 事件监听
        searchButton.addActionListener(e -> searchRooms());
        addButton.addActionListener(e -> addRoom());
        editButton.addActionListener(e -> editRoom());
        deleteButton.addActionListener(e -> deleteRoom());
        refreshButton.addActionListener(e -> loadData());
        backButton.addActionListener(e -> {
            dispose();
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

    private void loadData() {
        tableModel.setRowCount(0);
        
        // 获取该民宿的所有房间
        roomList = roomService.getRoomsByHomestayId(homestayId);
        
        for (Room r : roomList) {
            Object[] row = {
                r.getRoomId(),
                r.getRoomNumber(),
                getRoomTypeName(r.getRoomType()),
                r.getBedType(),
                r.getArea(),
                r.getMaxPeople(),
                r.getPrice(),
                getStatusName(r.getStatus())
            };
            tableModel.addRow(row);
        }
        
        updateStats();
    }

   private String getRoomTypeName(String type) {
        switch (type) {
            case "SINGLE": return "单人间";
            case "DOUBLE": return "大床房";
            case "TWIN": return "双人间";
            case "SUITE": return "套房";
            case "FAMILY": return "家庭房";
            default: return type;
        }
    }
   private String getStatusName(String status) {
        switch (status) {
            case "AVAILABLE": return "可用";
            case "BOOKED": return "已订";
            case "MAINTENANCE": return "维护";
            default: return status;
        }
    }

    // private String getStatusCode(String statusName) {
    //     switch (statusName) {
    //         case "可用": return "AVAILABLE";
    //         case "已订": return "BOOKED";
    //         case "维护": return "MAINTENANCE";
    //         default: return statusName;
    //     }
    // }

    private void updateStats() {
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

        Container contentPane = getContentPane();
        if (contentPane.getComponentCount() > 0) {
            Component mainComp = contentPane.getComponent(0);
            if (mainComp instanceof JPanel) {
                JPanel mainPanel = (JPanel) mainComp;
                int compCount = mainPanel.getComponentCount();
                if (compCount > 0) {
                    Component bottomComp = mainPanel.getComponent(compCount - 1);
                    if (bottomComp instanceof JPanel) {
                        JPanel bottomPanel = (JPanel) bottomComp;
                        if (bottomPanel.getComponentCount() > 0) {
                            Component labelComp = bottomPanel.getComponent(0);
                            if (labelComp instanceof JLabel) {
                                JLabel statsLabel = (JLabel) labelComp;
                                statsLabel.setText(String.format("总房间数: %d | 可用: %d | 已订: %d | 维护: %d",
                                        total, available, booked, maintenance));
                            }
                        }
                    }
                }
            }
        }
    }

    private void searchRooms() {
        String keyword = searchField.getText().trim();
        String type = (String) typeFilter.getSelectedItem();
        String status = (String) statusFilter.getSelectedItem();
        
        // 获取所有房间
        List<Room> allRooms = roomService.getRoomsByHomestayId(homestayId);
        
        // 筛选
        List<Room> filtered = allRooms.stream()
            .filter(r -> keyword.isEmpty() || r.getRoomNumber().contains(keyword))
            .filter(r -> "全部".equals(type) || getRoomTypeName(r.getRoomType()).equals(type))
            .filter(r -> "全部".equals(status) || getStatusName(r.getStatus()).equals(status))
            .toList();
        
        // 更新表格
        tableModel.setRowCount(0);
        for (Room r : filtered) {
            Object[] row = {
                r.getRoomId(),
                r.getRoomNumber(),
                getRoomTypeName(r.getRoomType()),
                r.getBedType(),
                r.getArea(),
                r.getMaxPeople(),
                r.getPrice(),
                getStatusName(r.getStatus())
            };
            tableModel.addRow(row);
        }
        
        updateStats();
    }

    private void addRoom() {
        // 权限检查：只有民宿主和管理员可以添加
        if ("GUEST".equals(currentUser.getRole())) {
            JOptionPane.showMessageDialog(this, "您没有权限添加房间", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // TODO: 打开添加房间对话框
        JOptionPane.showMessageDialog(this, "添加房间功能开发中", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private void editRoom() {
        int row = roomTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要编辑的房间", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 权限检查
        if ("GUEST".equals(currentUser.getRole())) {
            JOptionPane.showMessageDialog(this, "您没有权限编辑房间", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int roomId = (int) tableModel.getValueAt(row, 0);
        String roomNumber = (String) tableModel.getValueAt(row, 1);
        
        // TODO: 打开编辑房间对话框
        JOptionPane.showMessageDialog(this, 
            "编辑房间 " + roomNumber + " (ID:" + roomId + ")\n功能开发中", 
            "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteRoom() {
        int row = roomTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的房间", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 权限检查
        if ("GUEST".equals(currentUser.getRole())) {
            JOptionPane.showMessageDialog(this, "您没有权限删除房间", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int roomId = (int) tableModel.getValueAt(row, 0);
        String roomNumber = (String) tableModel.getValueAt(row, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要删除房间 " + roomNumber + " 吗？",
                "确认删除", JOptionPane.YES_NO_OPTION);
                
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = roomService.deleteRoom(roomId);
            if (success) {
                JOptionPane.showMessageDialog(this, "删除成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}