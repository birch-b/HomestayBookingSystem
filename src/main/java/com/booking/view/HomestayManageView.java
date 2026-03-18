package com.booking.view;

import com.booking.model.Homestay;
import com.booking.model.User;
import com.booking.service.HomestayService;
import com.booking.service.impl.HomestayServiceImpl;
import com.booking.util.AppColors;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * 民宿管理界面
 */
public class HomestayManageView extends JFrame {

    private User currentUser;
    private HomestayService homestayService;
    private List<Homestay> homestayList;

    private JTable homestayTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton searchButton;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton backButton;
    private JButton roomManageButton; // 房间管理按钮

    public HomestayManageView(User user) {
        this.currentUser = user;
        this.homestayService = new HomestayServiceImpl();
        initUI();
        loadData();
    }

    private void initUI() {
        setTitle("民宿管理 - " + currentUser.getRealName());
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(AppColors.LIGHT_PURPLE);

        // 标题
        JLabel titleLabel = new JLabel("民宿管理", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);

        // 搜索面板
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(AppColors.LIGHT_PURPLE);

        JLabel nameLabel = new JLabel("民宿名称:");
        nameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        nameLabel.setForeground(Color.BLACK);
        searchPanel.add(nameLabel);
        
        searchField = new JTextField(20);
        searchField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        searchField.setForeground(Color.BLACK);
        searchField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));

        searchButton = new JButton("搜索");
        styleButton(searchButton);

        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setBackground(AppColors.LIGHT_PURPLE);

        addButton = new JButton("新增民宿");
        editButton = new JButton("编辑");
        deleteButton = new JButton("删除");
        roomManageButton = new JButton("房间管理");
        refreshButton = new JButton("刷新");
        backButton = new JButton("返回");

        styleButton(addButton);
        styleButton(editButton);
        styleButton(deleteButton);
        styleButton(roomManageButton);
        styleButton(refreshButton);
        styleButton(backButton);

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(roomManageButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);

        // 合并顶部面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(AppColors.LIGHT_PURPLE);
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // 表格
        String[] columns = {"ID", "民宿名称", "城市", "地址", "电话", "评分", "状态"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        homestayTable = new JTable(tableModel);
        homestayTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        homestayTable.setRowHeight(25);
        homestayTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 13));
        homestayTable.getTableHeader().setBackground(AppColors.PRIMARY_PURPLE);
        homestayTable.getTableHeader().setForeground(Color.BLACK);
        homestayTable.setSelectionBackground(AppColors.HOVER_PURPLE);

        // 设置列宽
        homestayTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        homestayTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        homestayTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        homestayTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        homestayTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        homestayTable.getColumnModel().getColumn(5).setPreferredWidth(60);
        homestayTable.getColumnModel().getColumn(6).setPreferredWidth(60);

        JScrollPane scrollPane = new JScrollPane(homestayTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppColors.PRIMARY_PURPLE));

        // 底部统计
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(AppColors.LIGHT_PURPLE);
        JLabel statsLabel = new JLabel("总民宿数: 0");
        statsLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        statsLabel.setForeground(AppColors.DARK_PURPLE);
        bottomPanel.add(statsLabel);

        // 组装界面
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // 事件监听
        searchButton.addActionListener(e -> searchHomestay());
        addButton.addActionListener(e -> addHomestay());
        editButton.addActionListener(e -> editHomestay());
        deleteButton.addActionListener(e -> deleteHomestay());
        roomManageButton.addActionListener(e -> openRoomManage());
        refreshButton.addActionListener(e -> loadData());
        backButton.addActionListener(e -> dispose());
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        button.setBackground(AppColors.BUTTON_PURPLE);
        button.setForeground(AppColors.DARK_PURPLE);
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
        
        // 根据角色加载不同数据
        if ("ADMIN".equals(currentUser.getRole())) {
            // 管理员看所有民宿
            homestayList = homestayService.getAllHomestays(1, 100);
        } else {
            // 民宿主只看自己的民宿
            homestayList = homestayService.getHomestaysByHostId(currentUser.getUserId());
        }
        
        for (Homestay h : homestayList) {
            String status = h.getStatus() == 1 ? "营业" : "暂停";
            
            Object[] row = {
                h.getHomestayId(),
                h.getName(),
                h.getCity(),
                h.getAddress(),
                h.getPhone(),
                h.getRating(),
                status
            };
            tableModel.addRow(row);
        }
        
        updateStats();
    }

    private void updateStats() {
        int total = tableModel.getRowCount();
        
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
                                statsLabel.setText("总民宿数: " + total);
                            }
                        }
                    }
                }
            }
        }
    }

    private void searchHomestay() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadData();
            return;
        }

        // 调用Service搜索
        List<Homestay> searchResult = homestayService.searchHomestays(keyword, 1, 100);
        
        // 更新表格
        tableModel.setRowCount(0);
        for (Homestay h : searchResult) {
            String status = h.getStatus() == 1 ? "营业" : "暂停";
            Object[] row = {
                h.getHomestayId(),
                h.getName(),
                h.getCity(),
                h.getAddress(),
                h.getPhone(),
                h.getRating(),
                status
            };
            tableModel.addRow(row);
        }
        
        updateStats();
    }

    private void addHomestay() {
        // 只有民宿主和管理员可以添加
        if ("GUEST".equals(currentUser.getRole())) {
            JOptionPane.showMessageDialog(this, "您没有权限添加民宿", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // TODO: 打开添加民宿对话框
        JOptionPane.showMessageDialog(this, "添加民宿功能开发中", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private void editHomestay() {
        int row = homestayTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要编辑的民宿", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int homestayId = (int) tableModel.getValueAt(row, 0);
        Homestay homestay = homestayService.getHomestayById(homestayId);
        
        if (homestay != null) {
            // 权限检查：只有管理员或民宿主本人可以编辑
            if (!"ADMIN".equals(currentUser.getRole()) && 
                homestay.getHostId() != currentUser.getUserId()) {
                JOptionPane.showMessageDialog(this, "您没有权限编辑此民宿", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // TODO: 打开编辑民宿对话框
            JOptionPane.showMessageDialog(this, 
                "编辑民宿: " + homestay.getName() + "\n功能开发中", 
                "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void deleteHomestay() {
        int row = homestayTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的民宿", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int homestayId = (int) tableModel.getValueAt(row, 0);
        String homestayName = (String) tableModel.getValueAt(row, 1);
        
        // 权限检查
        Homestay homestay = homestayService.getHomestayById(homestayId);
        if (!"ADMIN".equals(currentUser.getRole()) && 
            homestay.getHostId() != currentUser.getUserId()) {
            JOptionPane.showMessageDialog(this, "您没有权限删除此民宿", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要删除民宿 " + homestayName + " 吗？\n删除后无法恢复！",
                "确认删除", JOptionPane.YES_NO_OPTION);
                
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = homestayService.deleteHomestay(homestayId);
            if (success) {
                JOptionPane.showMessageDialog(this, "删除成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openRoomManage() {
        int row = homestayTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一个民宿", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int homestayId = (int) tableModel.getValueAt(row, 0);
        String homestayName = (String) tableModel.getValueAt(row, 1);
        
        // 打开房间管理界面
        new RoomManageView(currentUser, homestayId, homestayName).setVisible(true);
    }
}