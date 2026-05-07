package com.booking.view;

import com.booking.model.Homestay;
import com.booking.model.Reservation;
import com.booking.model.User;
import com.booking.util.AppColors;
import com.booking.service.HomestayService;
import com.booking.service.ReservationService;
import com.booking.service.impl.HomestayServiceImpl;
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
 * 收入统计界面 - 增加了[已完成]订单提示
 */
public class IncomeStatsView extends JFrame {

    private User currentUser;
    private ReservationService reservationService;
    private HomestayService homestayService;
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
        this.homestayService = new HomestayServiceImpl();
        initUI();
        loadData();
    }

    private void initUI() {
        setTitle("收入统计 - " + currentUser.getRealName());
        setSize(900, 650); // 稍微增加高度以容纳提示文字
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(AppColors.LIGHT_PURPLE);

        // --- 标题区域 ---
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setBackground(AppColors.LIGHT_PURPLE);
        
        JLabel titleLabel = new JLabel("收入统计", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        
        // 提示1：副标题
        JLabel subTitleLabel = new JLabel("( 数据来源：仅限状态为 [已完成] 的订单 )", JLabel.CENTER);
        subTitleLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        subTitleLabel.setForeground(new Color(180, 0, 0)); // 使用深红色引起注意
        
        titlePanel.add(titleLabel);
        titlePanel.add(subTitleLabel);

        // --- 查询面板 ---
        JPanel queryAndHintPanel = new JPanel(new BorderLayout());
        queryAndHintPanel.setBackground(AppColors.LIGHT_PURPLE);

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
        
        // 提示2：查询栏下方的详细说明
        JLabel detailHintLabel = new JLabel("注意：待支付、已支付、已入住或已取消的订单不计入本统计表。", JLabel.CENTER);
        detailHintLabel.setFont(new Font("微软雅黑", Font.ITALIC, 12));
        detailHintLabel.setForeground(Color.GRAY);
        detailHintLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        queryAndHintPanel.add(queryPanel, BorderLayout.CENTER);
        queryAndHintPanel.add(detailHintLabel, BorderLayout.SOUTH);

        // --- 表格 ---
        String[] columns = {"日期", "订单数", "房间收入", "其他收入", "总收入"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        statsTable = new JTable(tableModel);
        statsTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        statsTable.setRowHeight(25);
        statsTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 13));
        statsTable.getTableHeader().setBackground(AppColors.PRIMARY_PURPLE);
        
        JScrollPane scrollPane = new JScrollPane(statsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppColors.PRIMARY_PURPLE));

        // --- 分页面板 ---
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

        paginationPanel.add(firstPageButton);
        paginationPanel.add(prevPageButton);
        paginationPanel.add(new JLabel("跳转到:"));
        paginationPanel.add(pageInput);
        paginationPanel.add(new JLabel("页"));
        paginationPanel.add(pageInfoLabel);
        paginationPanel.add(nextPageButton);
        paginationPanel.add(lastPageButton);

        // --- 底部统计 ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(AppColors.LIGHT_PURPLE);
        
        // 提示3：底部状态标注
        JLabel statusFlagLabel = new JLabel("  当前统计范围：已完成 (COMPLETED)");
        statusFlagLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        statusFlagLabel.setForeground(new Color(0, 100, 0)); // 绿色表示正常完成状态

        totalIncomeLabel = new JLabel("总计收入: 0.00 元  ");
        totalIncomeLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        totalIncomeLabel.setForeground(Color.BLACK);
        
        bottomPanel.add(statusFlagLabel, BorderLayout.WEST);
        bottomPanel.add(totalIncomeLabel, BorderLayout.EAST);

        // 组装界面
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(AppColors.LIGHT_PURPLE);
        topContainer.add(titlePanel, BorderLayout.NORTH);
        topContainer.add(queryAndHintPanel, BorderLayout.SOUTH);
        
        JPanel southContainer = new JPanel(new BorderLayout());
        southContainer.setBackground(AppColors.LIGHT_PURPLE);
        southContainer.add(paginationPanel, BorderLayout.NORTH);
        southContainer.add(bottomPanel, BorderLayout.SOUTH);
        
        mainPanel.add(topContainer, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(southContainer, BorderLayout.SOUTH);

        add(mainPanel);

        // 事件监听
        queryButton.addActionListener(e -> queryStats());
        exportButton.addActionListener(e -> exportReport());
        backButton.addActionListener(e -> dispose());
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
    }

    private void loadData() {
        tableModel.setRowCount(0);

        String yearText = yearField.getText().trim();
        String monthStr = (String) monthCombo.getSelectedItem();
        
        int displayYear;
        if (yearText.isEmpty()) {
            displayYear = Calendar.getInstance().get(Calendar.YEAR);
        } else {
            try {
                displayYear = Integer.parseInt(yearText);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "请输入有效年份", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        
        yearField.setText(String.valueOf(displayYear));
        boolean isSpecificMonth = !"全部".equals(monthStr);
        
        Calendar cal = Calendar.getInstance();
        Date startDate, endDate;
        
        if (isSpecificMonth) {
            int month = Integer.parseInt(monthStr.replace("月", ""));
            cal.set(displayYear, month - 1, 1, 0, 0, 0);
            startDate = cal.getTime();
            cal.set(displayYear, month - 1, cal.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
            endDate = cal.getTime();
        } else {
            cal.set(displayYear, Calendar.JANUARY, 1, 0, 0, 0);
            startDate = cal.getTime();
            cal.set(displayYear, Calendar.DECEMBER, 31, 23, 59, 59);
            endDate = cal.getTime();
        }

        // 根据角色决定查询范围
        List<Reservation> allCompletedReservations;
        if ("ADMIN".equals(currentUser.getRole())) {
            // 管理员查所有已完成订单
            allCompletedReservations = reservationService.searchReservations(
                    null, null, "COMPLETED", null, null, 1, Integer.MAX_VALUE);
        } else {
            // 房东只查自己名下所有民宿的已完成订单
            List<Homestay> myHomestays = homestayService.getHomestaysByHostId(currentUser.getUserId());
            allCompletedReservations = new ArrayList<>();
            for (Homestay h : myHomestays) {
                List<Reservation> homestayReservations = reservationService.getHomestayReservations(
                        h.getHomestayId(), 1, Integer.MAX_VALUE);
                for (Reservation r : homestayReservations) {
                    if ("COMPLETED".equals(r.getStatus())) {
                        allCompletedReservations.add(r);
                    }
                }
            }
        }

        if (isSpecificMonth) {
            displayDailyData(allCompletedReservations, startDate, endDate, displayYear,
                            Integer.parseInt(monthStr.replace("月", "")));
        } else {
            displayYearlyData(allCompletedReservations, startDate, endDate, displayYear);
        }
    }

    private void displayYearlyData(List<Reservation> reservations, Date startDate, Date endDate, int year) {
        Map<Integer, DailyStats> monthlyStats = new HashMap<>();
        for (int i = 1; i <= 12; i++) monthlyStats.put(i, new DailyStats());
        
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        for (Reservation r : reservations) {
            Date timeToUse = r.getCheckOutDate() != null ? r.getCheckOutDate() : r.getCreateTime();
            if (timeToUse != null && !timeToUse.before(startDate) && !timeToUse.after(endDate)) {
                int month = Integer.parseInt(monthFormat.format(timeToUse));
                DailyStats stats = monthlyStats.get(month);
                stats.orderCount++;
                stats.totalIncome += r.getTotalPrice();
            }
        }

        List<String> monthList = new ArrayList<>();
        for (int i = 1; i <= 12; i++) monthList.add(String.format("%d年%d月", year, i));

        totalPages = (monthList.size() + pageSize - 1) / pageSize;
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, monthList.size());
        
        double totalSum = 0;
        // 注意：计算总计应当遍历所有数据而不仅仅是当前页
        for (DailyStats ds : monthlyStats.values()) totalSum += ds.totalIncome;

        for (int i = startIndex; i < endIndex; i++) {
            int month = i + 1;
            DailyStats stats = monthlyStats.get(month);
            tableModel.addRow(new Object[]{
                    monthList.get(i), stats.orderCount, String.format("%.2f", stats.totalIncome),
                    "0.00", String.format("%.2f", stats.totalIncome)
            });
        }
        totalIncomeLabel.setText(String.format("总计收入: %.2f 元  ", totalSum));
        updatePaginationInfo();
    }

    private void displayDailyData(List<Reservation> reservations, Date startDate, Date endDate, int year, int month) {
        Map<String, DailyStats> dailyStats = new HashMap<>();
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        double totalSum = 0;
        for (Reservation r : reservations) {
            Date timeToUse = r.getCheckOutDate() != null ? r.getCheckOutDate() : r.getCreateTime();
            if (timeToUse != null && !timeToUse.before(startDate) && !timeToUse.after(endDate)) {
                String dayKey = dayFormat.format(timeToUse);
                DailyStats stats = dailyStats.computeIfAbsent(dayKey, k -> new DailyStats());
                stats.orderCount++;
                stats.totalIncome += r.getTotalPrice();
                totalSum += r.getTotalPrice();
            }
        }

        List<String> dayList = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1);
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 1; i <= daysInMonth; i++) dayList.add(String.format("%d-%02d-%02d", year, month, i));

        totalPages = (dayList.size() + pageSize - 1) / pageSize;
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, dayList.size());
        
        for (int i = startIndex; i < endIndex; i++) {
            String day = dayList.get(i);
            DailyStats stats = dailyStats.getOrDefault(day, new DailyStats());
            tableModel.addRow(new Object[]{
                    day, stats.orderCount, String.format("%.2f", stats.totalIncome),
                    "0.00", String.format("%.2f", stats.totalIncome)
            });
        }
        totalIncomeLabel.setText(String.format("总计收入: %.2f 元  ", totalSum));
        updatePaginationInfo();
    }

    private static class DailyStats {
        int orderCount = 0;
        double totalIncome = 0;
    }

    private void queryStats() { currentPage = 1; loadData(); }

    private void exportReport() {
        String year = yearField.getText().trim();
        String month = (String) monthCombo.getSelectedItem();
        StringBuilder report = new StringBuilder();
        report.append("收入统计报表 (仅限已完成订单)\n");
        report.append("==============================\n");
        report.append("生成时间: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n");
        report.append("统计范围: ").append(year).append("年").append(month).append("\n");
        report.append("------------------------------\n");
        report.append(String.format("%-15s %-10s %-10s\n", "日期", "订单数", "收入"));
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            report.append(String.format("%-15s %-10s %-10s\n",
                tableModel.getValueAt(i, 0), tableModel.getValueAt(i, 1), tableModel.getValueAt(i, 2)));
        }
        report.append("------------------------------\n");
        report.append(totalIncomeLabel.getText());

        JTextArea textArea = new JTextArea(report.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "报表预览", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void updatePaginationInfo() {
        pageInfoLabel.setText("第 " + currentPage + " 页 / 共 " + totalPages + " 页");
        pageInput.setText(String.valueOf(currentPage));
        firstPageButton.setEnabled(currentPage > 1);
        prevPageButton.setEnabled(currentPage > 1);
        nextPageButton.setEnabled(currentPage < totalPages);
        lastPageButton.setEnabled(currentPage < totalPages);
    }
    
    private void goToFirstPage() { currentPage = 1; loadData(); }
    private void goToPrevPage() { if (currentPage > 1) { currentPage--; loadData(); } }
    private void goToNextPage() { if (currentPage < totalPages) { currentPage++; loadData(); } }
    private void goToLastPage() { currentPage = totalPages; loadData(); }
    private void goToPage() {
        try {
            int page = Integer.parseInt(pageInput.getText().trim());
            if (page >= 1 && page <= totalPages) { currentPage = page; loadData(); }
        } catch (Exception e) { /* ignore */ }
    }
}