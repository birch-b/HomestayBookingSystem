package com.booking.view;

import com.booking.model.User;
import com.booking.util.AppColors;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 收入统计界面
 */
public class IncomeStatsView extends JFrame {

    private User currentUser;
    private JComboBox<String> yearCombo;
    private JComboBox<String> monthCombo;
    private JButton queryButton;
    private JButton exportButton;
    private JButton backButton;
    private JTable statsTable;
    private DefaultTableModel tableModel;
    private JLabel totalIncomeLabel;

    public IncomeStatsView(User user) {
        this.currentUser = user;
        initUI();
        loadData();
    }

    private void initUI() {
        setTitle("收入统计 - " + currentUser.getRealName());
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(AppColors.LIGHT_PURPLE);

        // 标题
        JLabel titleLabel = new JLabel("收入统计", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);

        // 查询面板
        JPanel queryPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        queryPanel.setBackground(AppColors.LIGHT_PURPLE);

        queryPanel.add(new JLabel("年份:"));
        String[] years = {"2026", "2025", "2024", "2023"};
        yearCombo = new JComboBox<>(years);
        yearCombo.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        yearCombo.setBackground(Color.WHITE);

        queryPanel.add(new JLabel("月份:"));
        String[] months = {"全部", "1月", "2月", "3月", "4月", "5月", "6月",
                "7月", "8月", "9月", "10月", "11月", "12月"};
        monthCombo = new JComboBox<>(months);
        monthCombo.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        monthCombo.setBackground(Color.WHITE);

        queryButton = new JButton("查询");
        exportButton = new JButton("导出报表");
        backButton = new JButton("返回");

        styleButton(queryButton);
        styleButton(exportButton);
        styleButton(backButton);

        queryPanel.add(yearCombo);
        queryPanel.add(monthCombo);
        queryPanel.add(queryButton);
        queryPanel.add(exportButton);
        queryPanel.add(backButton);

        // 表格
        String[] columns = {"日期", "订单数", "房间收入", "其他收入", "总收入"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        statsTable = new JTable(tableModel);
        statsTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        statsTable.setRowHeight(25);
        statsTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 13));
        statsTable.getTableHeader().setBackground(AppColors.PRIMARY_PURPLE);
        statsTable.getTableHeader().setForeground(Color.BLACK);
        statsTable.setSelectionBackground(AppColors.HOVER_PURPLE);
        JScrollPane scrollPane = new JScrollPane(statsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppColors.PRIMARY_PURPLE));

        // 底部统计
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(AppColors.LIGHT_PURPLE);
        totalIncomeLabel = new JLabel("总计收入: 0.00 元");
        totalIncomeLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        totalIncomeLabel.setForeground(Color.BLACK);
        bottomPanel.add(totalIncomeLabel);

        // 组装界面
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(queryPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // 事件监听
        queryButton.addActionListener(e -> queryStats());
        exportButton.addActionListener(e -> exportReport());
        backButton.addActionListener(e -> dispose());
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
        // 模拟数据
        tableModel.setRowCount(0);

        Object[] row1 = {"2026-03-01", "5", "1940.00", "200.00", "2140.00"};
        Object[] row2 = {"2026-03-02", "3", "1164.00", "150.00", "1314.00"};
        Object[] row3 = {"2026-03-03", "4", "1552.00", "0.00", "1552.00"};
        Object[] row4 = {"2026-03-04", "2", "776.00", "100.00", "876.00"};
        Object[] row5 = {"2026-03-05", "6", "2328.00", "300.00", "2628.00"};

        tableModel.addRow(row1);
        tableModel.addRow(row2);
        tableModel.addRow(row3);
        tableModel.addRow(row4);
        tableModel.addRow(row5);

        updateTotal();
    }

    private void updateTotal() {
        double total = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String totalStr = (String) tableModel.getValueAt(i, 4);
            total += Double.parseDouble(totalStr.replace(",", ""));
        }
        totalIncomeLabel.setText(String.format("总计收入: %.2f 元", total));
    }

    private void queryStats() {
        String year = (String) yearCombo.getSelectedItem();
        String month = (String) monthCombo.getSelectedItem();
        JOptionPane.showMessageDialog(this, "查询 " + year + "年 " + month + " 的数据",
                "提示", JOptionPane.INFORMATION_MESSAGE);
        loadData();
    }

    private void exportReport() {
        JOptionPane.showMessageDialog(this, "导出报表功能待实现", "提示", JOptionPane.INFORMATION_MESSAGE);
    }
}