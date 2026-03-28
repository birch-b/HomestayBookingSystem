package com.booking.view;

import com.booking.model.Reservation;
import com.booking.model.User;
import com.booking.util.AppColors;
import com.booking.service.ReservationService;
import com.booking.service.impl.ReservationServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * 收入统计界面
 */
public class IncomeStatsView extends JFrame {

    private User currentUser;
    private ReservationService reservationService;
    private JTextField yearField;
    private JComboBox<String> monthCombo;
    private JButton queryButton;
    private JButton exportButton;
    private JButton backButton;
    private JTable statsTable;
    private DefaultTableModel tableModel;
    private JLabel totalIncomeLabel;
    
    // 分页相关
    private int currentPage = 1;
    private int pageSize = 10;
    private int totalPages = 1;
    private JButton firstPageButton;
    private JButton prevPageButton;
    private JButton nextPageButton;
    private JButton lastPageButton;
    private JTextField pageInput;
    private JLabel pageInfoLabel;

    public IncomeStatsView(User user) {
        this.currentUser = user;
        this.reservationService = new ReservationServiceImpl();
        initUI();
        loadData(); // 页面初始化时执行loadData，使用当前年份显示默认数据
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
        yearField = new JTextField("", 8);
        yearField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        yearField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));
        queryPanel.add(yearField);

        queryPanel.add(new JLabel("月份:"));
        String[] months = {"全部", "1月", "2月", "3月", "4月", "5月", "6月",
                "7月", "8月", "9月", "10月", "11月", "12月"};
        monthCombo = new JComboBox<>(months);
        monthCombo.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        monthCombo.setBackground(Color.WHITE);
        monthCombo.setPreferredSize(new Dimension(100, 25));
        queryPanel.add(monthCombo);

        queryButton = new JButton("查询");
        exportButton = new JButton("导出报表");
        backButton = new JButton("返回");

        styleButton(queryButton);
        styleButton(exportButton);
        styleButton(backButton);

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

        // 分页面板
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        paginationPanel.setBackground(AppColors.LIGHT_PURPLE);

        firstPageButton = new JButton("首页");
        prevPageButton = new JButton("上一页");
        nextPageButton = new JButton("下一页");
        lastPageButton = new JButton("末页");
        pageInput = new JTextField(3);
        pageInfoLabel = new JLabel("第 1 页 / 共 1 页");

        styleButton(firstPageButton);
        styleButton(prevPageButton);
        styleButton(nextPageButton);
        styleButton(lastPageButton);

        pageInput.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        pageInput.setHorizontalAlignment(JTextField.CENTER);
        pageInfoLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        pageInfoLabel.setForeground(AppColors.DARK_PURPLE);

        paginationPanel.add(firstPageButton);
        paginationPanel.add(prevPageButton);
        paginationPanel.add(new JLabel("跳转到:"));
        paginationPanel.add(pageInput);
        paginationPanel.add(new JLabel("页"));
        paginationPanel.add(pageInfoLabel);
        paginationPanel.add(nextPageButton);
        paginationPanel.add(lastPageButton);

        // 底部统计
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(AppColors.LIGHT_PURPLE);
        totalIncomeLabel = new JLabel("总计收入: 0.00 元");
        totalIncomeLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        totalIncomeLabel.setForeground(Color.BLACK);
        bottomPanel.add(totalIncomeLabel);

        // 组装界面
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(AppColors.LIGHT_PURPLE);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(queryPanel, BorderLayout.SOUTH);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(AppColors.LIGHT_PURPLE);
        southPanel.add(paginationPanel, BorderLayout.NORTH);
        southPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // 事件监听
        queryButton.addActionListener(e -> queryStats());
        exportButton.addActionListener(e -> exportReport());
        backButton.addActionListener(e -> dispose());
        
        // 分页事件
        firstPageButton.addActionListener(e -> goToFirstPage());
        prevPageButton.addActionListener(e -> goToPrevPage());
        nextPageButton.addActionListener(e -> goToNextPage());
        lastPageButton.addActionListener(e -> goToLastPage());
        pageInput.addActionListener(e -> goToPage());
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        button.setBackground(AppColors.BUTTON_PURPLE);
        button.setForeground(AppColors.DARK_PURPLE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setPreferredSize(new Dimension(100, 25));

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

        String yearText = yearField.getText().trim();
        String monthStr = (String) monthCombo.getSelectedItem();
        
        // 获取要显示的年份
        int displayYear;
        if (yearText.isEmpty()) {
            // 如果年份为空，使用当前年份
            displayYear = Calendar.getInstance().get(Calendar.YEAR);
        } else {
            try {
                displayYear = Integer.parseInt(yearText);
                if (displayYear < 2000 || displayYear > 2100) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "请输入有效的年份（如 2024）", 
                    "提示", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        
        // 设置年份字段显示
        yearField.setText(String.valueOf(displayYear));
        
        // 判断是否选择了具体月份
        boolean isSpecificMonth = !"全部".equals(monthStr);
        
        // 构建查询日期范围
        Calendar cal = Calendar.getInstance();
        Date startDate, endDate;
        
        if (isSpecificMonth) {
            // 选择了具体月份，查询该月数据
            int month = Integer.parseInt(monthStr.replace("月", ""));
            cal.set(displayYear, month - 1, 1, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
            startDate = cal.getTime();
            cal.set(displayYear, month - 1, cal.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
            endDate = cal.getTime();
        } else {
            // 选择了"全部"，查询整年数据
            cal.set(displayYear, Calendar.JANUARY, 1, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
            startDate = cal.getTime();
            cal.set(displayYear, Calendar.DECEMBER, 31, 23, 59, 59);
            endDate = cal.getTime();
        }

        // 查询所有 COMPLETED 状态的订单
        List<Reservation> allCompletedReservations = reservationService.searchReservations(
                3, null, "COMPLETED", null, null, 1, Integer.MAX_VALUE);
        
        if (isSpecificMonth) {
            // 按月查询，按天分组显示
            displayDailyData(allCompletedReservations, startDate, endDate, displayYear, 
                            Integer.parseInt(monthStr.replace("月", "")));
        } else {
            // 按年查询，显示12个月的数据
            displayYearlyData(allCompletedReservations, startDate, endDate, displayYear);
        }
    }

    /**
     * 显示年度数据（12个月）
     */
    private void displayYearlyData(List<Reservation> reservations, Date startDate, Date endDate, int year) {
        // 按月分组统计
        Map<Integer, DailyStats> monthlyStats = new HashMap<>();
        
        // 初始化12个月的数据，确保每个月都有记录
        for (int i = 1; i <= 12; i++) {
            monthlyStats.put(i, new DailyStats());
        }
        
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        
        for (Reservation r : reservations) {
            Date timeToUse = r.getCheckOutDate() != null ? r.getCheckOutDate() : r.getCreateTime();
            if (timeToUse != null) {
                // 检查日期是否在指定范围内
                if ((timeToUse.compareTo(startDate) >= 0) && (timeToUse.compareTo(endDate) <= 0)) {
                    String monthStr = monthFormat.format(timeToUse);
                    int month = Integer.parseInt(monthStr);
                    DailyStats stats = monthlyStats.get(month);
                    if (stats != null) {
                        stats.orderCount++;
                        stats.totalIncome += r.getTotalPrice();
                    }
                }
            }
        }

        // 生成12个月的数据列表
        List<String> monthList = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            monthList.add(String.format("%d年%d月", year, i));
        }

        // 计算总页数
        totalPages = (monthList.size() + pageSize - 1) / pageSize;
        if (totalPages < 1) totalPages = 1;

        // 分页显示
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, monthList.size());
        
        double totalIncome = 0;
        
        for (int i = startIndex; i < endIndex; i++) {
            int month = i + 1;
            DailyStats stats = monthlyStats.get(month);
            if (stats == null) {
                stats = new DailyStats();
            }
            tableModel.addRow(new Object[]{
                    monthList.get(i),
                    stats.orderCount,
                    String.format("%.2f", stats.totalIncome),
                    "0.00",
                    String.format("%.2f", stats.totalIncome)
            });
            totalIncome += stats.totalIncome;
        }

        totalIncomeLabel.setText(String.format("总计收入: %.2f 元", totalIncome));
        updatePaginationInfo();
    }

    /**
     * 显示月度数据（按天）
     */
    private void displayDailyData(List<Reservation> reservations, Date startDate, Date endDate, int year, int month) {
        // 按天分组统计
        Map<String, DailyStats> dailyStats = new HashMap<>();
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        for (Reservation r : reservations) {
            Date timeToUse = r.getCheckOutDate() != null ? r.getCheckOutDate() : r.getCreateTime();
            if (timeToUse != null) {
                // 检查日期是否在指定范围内
                if ((timeToUse.compareTo(startDate) >= 0) && (timeToUse.compareTo(endDate) <= 0)) {
                    String dayKey = dayFormat.format(timeToUse);
                    DailyStats stats = dailyStats.computeIfAbsent(dayKey, k -> new DailyStats());
                    stats.orderCount++;
                    stats.totalIncome += r.getTotalPrice();
                }
            }
        }

        // 生成该月的所有日期列表
        List<String> dayList = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1);
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        for (int i = 1; i <= daysInMonth; i++) {
            dayList.add(String.format("%d-%02d-%02d", year, month, i));
        }

        // 计算总页数
        totalPages = (dayList.size() + pageSize - 1) / pageSize;
        if (totalPages < 1) totalPages = 1;

        // 分页显示
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, dayList.size());
        
        double totalIncome = 0;
        
        for (int i = startIndex; i < endIndex; i++) {
            String day = dayList.get(i);
            DailyStats stats = dailyStats.getOrDefault(day, new DailyStats());
            tableModel.addRow(new Object[]{
                    day,
                    stats.orderCount,
                    String.format("%.2f", stats.totalIncome),
                    "0.00",
                    String.format("%.2f", stats.totalIncome)
            });
            totalIncome += stats.totalIncome;
        }

        totalIncomeLabel.setText(String.format("总计收入: %.2f 元", totalIncome));
        updatePaginationInfo();
    }

    // 内部类
    private static class DailyStats {
        int orderCount = 0;
        double totalIncome = 0;
    }

    private void queryStats() {
        currentPage = 1;  // 查询时重置到第一页
        loadData();
    }

    private void exportReport() {
        String year = yearField.getText().trim();
        String month = (String) monthCombo.getSelectedItem();
        
        // 生成报表内容
        StringBuilder report = new StringBuilder();
        report.append("收入统计报表\n");
        report.append("============\n");
        report.append("生成时间: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n");
        report.append("年份: ").append(year).append("\n");
        report.append("月份: ").append(month).append("\n");
        report.append("------------\n");
        
        // 表头
        report.append(String.format("%-12s %-8s %-12s\n", "日期", "订单数", "收入"));
        report.append("------------\n");
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            report.append(String.format("%-12s %-8s %-12s\n",
                tableModel.getValueAt(i, 0),
                tableModel.getValueAt(i, 1),
                tableModel.getValueAt(i, 2)));
        }
        
        report.append("------------\n");
        report.append(totalIncomeLabel.getText());

        // 创建选项面板
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(AppColors.LIGHT_PURPLE);
        
        // 预览区域
        JTextArea textArea = new JTextArea(report.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("宋体", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        scrollPane.setBorder(BorderFactory.createTitledBorder("报表预览"));
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(AppColors.LIGHT_PURPLE);
        
        JButton saveButton = new JButton("保存到文件");
        JButton copyButton = new JButton("复制到剪贴板");
        JButton closeButton = new JButton("关闭");
        
        styleButton(saveButton);
        styleButton(copyButton);
        styleButton(closeButton);
        
        buttonPanel.add(saveButton);
        buttonPanel.add(copyButton);
        buttonPanel.add(closeButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // 创建对话框
        JDialog dialog = new JDialog(this, "导出报表", true);
        dialog.setContentPane(panel);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        
        // 保存按钮事件
        saveButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File("收入统计_" + year + "_" + month + ".txt"));
            
            int result = fileChooser.showSaveDialog(dialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    java.io.File file = fileChooser.getSelectedFile();
                    java.io.FileWriter writer = new java.io.FileWriter(file);
                    writer.write(report.toString());
                    writer.close();
                    JOptionPane.showMessageDialog(dialog, 
                        "报表已保存到: " + file.getAbsolutePath(), 
                        "保存成功", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "保存失败: " + ex.getMessage(), 
                        "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // 复制按钮事件
        copyButton.addActionListener(e -> {
            java.awt.datatransfer.StringSelection selection = 
                new java.awt.datatransfer.StringSelection(report.toString());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
            JOptionPane.showMessageDialog(dialog, 
                "报表内容已复制到剪贴板", 
                "复制成功", JOptionPane.INFORMATION_MESSAGE);
        });
        
        // 关闭按钮事件
        closeButton.addActionListener(e -> dialog.dispose());
        
        dialog.setVisible(true);
    }
    
    // 分页相关方法
    private void updatePaginationInfo() {
        pageInfoLabel.setText("第 " + currentPage + " 页 / 共 " + totalPages + " 页");
        pageInput.setText(String.valueOf(currentPage));
        
        // 更新按钮状态
        firstPageButton.setEnabled(currentPage > 1);
        prevPageButton.setEnabled(currentPage > 1);
        nextPageButton.setEnabled(currentPage < totalPages);
        lastPageButton.setEnabled(currentPage < totalPages);
    }
    
    private void goToFirstPage() {
        currentPage = 1;
        loadData();
    }
    
    private void goToPrevPage() {
        if (currentPage > 1) {
            currentPage--;
            loadData();
        }
    }
    
    private void goToNextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            loadData();
        }
    }
    
    private void goToLastPage() {
        currentPage = totalPages;
        loadData();
    }
    
    private void goToPage() {
        try {
            int page = Integer.parseInt(pageInput.getText().trim());
            if (page >= 1 && page <= totalPages) {
                currentPage = page;
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "页码超出范围", "提示", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效的页码", "提示", JOptionPane.WARNING_MESSAGE);
        }
    }
}