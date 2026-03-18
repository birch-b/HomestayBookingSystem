package com.booking.view;

import com.booking.model.Room;
import com.booking.model.User;
import com.booking.util.AppColors;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
// import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
// import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 预订界面 - 游客预订房间
 */
public class ReservationView extends JFrame {

    private User currentUser;
    // private List<Room> availableRooms;

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
    private com.booking.service.RoomService roomService;
    private com.booking.service.ReservationService reservationService;
    private int selectedRoomId = -1;
    private String selectedRoomInfo = "";

    public ReservationView(User user) {
        this.currentUser = user;
        // this.availableRooms = new ArrayList<>();
        this.roomService = new com.booking.service.impl.RoomServiceImpl();
        this.reservationService = new com.booking.service.impl.ReservationServiceImpl();
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
        loadAvailableRooms();
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

  private void loadAvailableRooms() {
    tableModel.setRowCount(0);
    
    // 获取所有可用房间
    List<Room> availableRooms = roomService.getAvailableRooms();
    
    for (Room r : availableRooms) {
        // 获取民宿名称（这里需要从HomestayService获取，暂时用ID代替）
        String homestayName = "民宿" + r.getHomestayId();
        
        Object[] row = {
            false,
            r.getRoomId(),
            homestayName,
            r.getRoomNumber(),
            getRoomTypeName(r.getRoomType()),
            r.getMaxPeople(),
            r.getPrice(),
            getStatusName(r.getStatus())
        };
        tableModel.addRow(row);
    }
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
    private void searchRooms() {
    String city = cityField.getText().trim();
    String checkInStr = checkInField.getText().trim();
    String checkOutStr = checkOutField.getText().trim();
    String peopleStr = peopleField.getText().trim();
        // TODO: 调用Service搜索可用房间
        // 验证输入
        if (city.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入城市", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int peopleCount;
        try {
            peopleCount = Integer.parseInt(peopleStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入正确的人数", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
     // 解析日期
        Date checkIn, checkOut;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            checkIn = sdf.parse(checkInStr);
            checkOut = sdf.parse(checkOutStr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "日期格式错误，请使用 yyyy-MM-dd 格式", 
                                        "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

    // 调用Service搜索可用房间
        List<Room> searchResult = roomService.searchAvailableRooms(
            city, checkIn, checkOut, peopleCount, 1, 100);
    
    // 更新表格
         tableModel.setRowCount(0);
        for (Room r : searchResult) {
            String homestayName = "民宿" + r.getHomestayId();
            
            Object[] row = {
                false,
                r.getRoomId(),
                homestayName,
                r.getRoomNumber(),
                getRoomTypeName(r.getRoomType()),
                r.getMaxPeople(),
                r.getPrice(),
                getStatusName(r.getStatus())
            };
            tableModel.addRow(row);
        }
        if (searchResult.isEmpty()) {
            JOptionPane.showMessageDialog(this, "没有找到符合条件的房间", "提示", 
                                        JOptionPane.INFORMATION_MESSAGE);
        }
    }

  private void reserveRoom() {
    if (selectedRoomId == -1) {
        JOptionPane.showMessageDialog(this, "请先选择要预订的房间", "提示", 
                                    JOptionPane.WARNING_MESSAGE);
        return;
    }

    String checkInStr = checkInField.getText().trim();
    String checkOutStr = checkOutField.getText().trim();
    String peopleStr = peopleField.getText().trim();

    if (checkInStr.isEmpty() || checkOutStr.isEmpty() || peopleStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, "请填写完整的日期和人数", "提示", 
                                    JOptionPane.WARNING_MESSAGE);
        return;
    }

    try {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date checkIn = sdf.parse(checkInStr);
        Date checkOut = sdf.parse(checkOutStr);
        int guestsCount = Integer.parseInt(peopleStr);

        // 获取选中房间的价格
        double roomPrice = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if ((int) tableModel.getValueAt(i, 1) == selectedRoomId) {
                roomPrice = (double) tableModel.getValueAt(i, 6);
                break;
            }
        }

        // 计算总价
        long days = (checkOut.getTime() - checkIn.getTime()) / (1000 * 60 * 60 * 24);
        if (days <= 0) days = 1;
        double totalPrice = roomPrice * days;

        // 创建预订对象
        com.booking.model.Reservation reservation = new com.booking.model.Reservation();
        reservation.setRoomId(selectedRoomId);
        reservation.setGuestId(currentUser.getUserId());
        reservation.setCheckInDate(checkIn);
        reservation.setCheckOutDate(checkOut);
        reservation.setGuestsCount(guestsCount);
        reservation.setTotalPrice(totalPrice);
        reservation.setGuestName(currentUser.getRealName());
        reservation.setGuestPhone(currentUser.getPhone());

        // 创建支付对象（可选）
        com.booking.model.Payment payment = new com.booking.model.Payment();
        payment.setAmount(totalPrice);
        payment.setPaymentMethod("WECHAT");

        // 确认预订
        String message = String.format(
            "确认预订以下房间？\n\n" +
            "房间: %s\n" +
            "入住: %s\n" +
            "离店: %s\n" +
            "人数: %d\n" +
            "天数: %d晚\n" +
            "总价: %.2f元",
            selectedRoomInfo, checkInStr, checkOutStr, guestsCount, days, totalPrice);

        int confirm = JOptionPane.showConfirmDialog(this, message, "确认预订", 
                                                  JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int result = reservationService.createReservation(reservation, payment);
            
            if (result == 1) {
                JOptionPane.showMessageDialog(this, 
                    "预订成功！\n订单号: " + reservation.getReservationNo(), 
                    "成功", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else if (result == -1) {
                JOptionPane.showMessageDialog(this, 
                    "预订失败：房间已被预订", 
                    "失败", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "预订失败：系统错误", 
                    "失败", JOptionPane.ERROR_MESSAGE);
            }
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "输入数据错误: " + e.getMessage(), 
                                    "错误", JOptionPane.ERROR_MESSAGE);
    }
    }
}