package com.booking.view;

import com.booking.model.Homestay;
import com.booking.model.User;
import com.booking.util.AppColors;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * 民宿管理界面
 */
public class HomestayManageView extends JFrame {

    private User currentUser;
    private List<Homestay> homestayList;

    private JTable homestayTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton searchButton;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;

    public HomestayManageView(User user) {
        this.currentUser = user;
        this.homestayList = new ArrayList<>();
        initUI();
        loadData();
    }

    private void initUI() {
        setTitle("民宿管理 - " + currentUser.getRealName());
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(AppColors.LIGHT_PURPLE);

        // 标题
        JLabel titleLabel = new JLabel("民宿管理", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);

        // 搜索面板
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(AppColors.LIGHT_PURPLE);

        searchField = new JTextField(20);
        searchField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));

        searchButton = new JButton("搜索");
        styleButton(searchButton);

        searchPanel.add(new JLabel("民宿名称:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setBackground(AppColors.LIGHT_PURPLE);

        addButton = new JButton("新增民宿");
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

        JScrollPane scrollPane = new JScrollPane(homestayTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppColors.PRIMARY_PURPLE));

        // 组装界面
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);

        // 添加事件监听
        addButton.addActionListener(e -> addHomestay());
        editButton.addActionListener(e -> editHomestay());
        deleteButton.addActionListener(e -> deleteHomestay());
        refreshButton.addActionListener(e -> loadData());
        searchButton.addActionListener(e -> searchHomestay());
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
        // TODO: 从Service加载数据
        tableModel.setRowCount(0);

        // 临时测试数据
        Object[] row1 = {1, "云中山居", "北京", "怀柔区雁栖镇", "010-12345678", 4.8, "营业"};
        Object[] row2 = {2, "海边小筑", "秦皇岛", "北戴河区海滨路", "0335-1234567", 4.6, "营业"};
        Object[] row3 = {3, "山里人家", "北京", "门头沟区斋堂镇", "010-87654321", 4.5, "营业"};

        tableModel.addRow(row1);
        tableModel.addRow(row2);
        tableModel.addRow(row3);
    }

    private void searchHomestay() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadData();
            return;
        }

        // TODO: 调用Service搜索
        JOptionPane.showMessageDialog(this, "搜索功能待实现", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addHomestay() {
        JOptionPane.showMessageDialog(this, "新增民宿功能待实现", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private void editHomestay() {
        int row = homestayTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要编辑的民宿", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        JOptionPane.showMessageDialog(this, "编辑民宿 ID: " + id + " 功能待实现", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteHomestay() {
        int row = homestayTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的民宿", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "确定要删除该民宿吗？",
                "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // TODO: 调用Service删除
            JOptionPane.showMessageDialog(this, "删除功能待实现", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}