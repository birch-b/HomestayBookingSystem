package com.booking.view;

import com.booking.model.Room;
import com.booking.model.User;
import com.booking.util.AppColors;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
// import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private List<Integer> selectedRoomIds = new ArrayList<>();
    private List<String> selectedRoomInfos = new ArrayList<>();
    private Integer homestayId = null; // 民宿ID，用于过滤房间

    public ReservationView(User user) {
        this.currentUser = user;
        // this.availableRooms = new ArrayList<>();
        this.roomService = new com.booking.service.impl.RoomServiceImpl();
        this.reservationService = new com.booking.service.impl.ReservationServiceImpl();
        initUI();
    }

    public ReservationView(User user, int homestayId) {
        this.currentUser = user;
        this.homestayId = homestayId;
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
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(AppColors.LIGHT_PURPLE);

        // 标题
        JLabel titleLabel = new JLabel("预订房间111", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        titleLabel.setForeground(AppColors.PRIMARY_PURPLE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 搜索面板
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(AppColors.LIGHT_PURPLE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 城市（如果没有指定民宿ID，则显示）
        if (homestayId == null) {
            gbc.gridx = 0;
            gbc.gridy = 0;
            JLabel cityLabel = new JLabel("城市:");
            cityLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
            cityLabel.setForeground(AppColors.DARK_PURPLE);
            searchPanel.add(cityLabel, gbc);

            gbc.gridx = 1;
            cityField = new JTextField(15);
            cityField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
            cityField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));
            searchPanel.add(cityField, gbc);
        }

        // 入住日期
        gbc.gridx = homestayId == null ? 2 : 0;
        JLabel checkInLabel = new JLabel("入住:");
        checkInLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        checkInLabel.setForeground(AppColors.DARK_PURPLE);
        searchPanel.add(checkInLabel, gbc);

        gbc.gridx = homestayId == null ? 3 : 1;
        checkInField = new JTextField(10);
        checkInField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        checkInField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));
        checkInField.setText(getCurrentDate());
        searchPanel.add(checkInField, gbc);

        // 离店日期
        gbc.gridx = homestayId == null ? 4 : 2;
        JLabel checkOutLabel = new JLabel("离店:");
        checkOutLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        checkOutLabel.setForeground(AppColors.DARK_PURPLE);
        searchPanel.add(checkOutLabel, gbc);

        gbc.gridx = homestayId == null ? 5 : 3;
        checkOutField = new JTextField(10);
        checkOutField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        checkOutField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));
        checkOutField.setText(getTomorrowDate());
        searchPanel.add(checkOutField, gbc);

        // 人数
        gbc.gridx = homestayId == null ? 6 : 4;
        JLabel peopleLabel = new JLabel("人数:");
        peopleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        peopleLabel.setForeground(AppColors.DARK_PURPLE);
        searchPanel.add(peopleLabel, gbc);

        gbc.gridx = homestayId == null ? 7 : 5;
        peopleField = new JTextField(5);
        peopleField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        peopleField.setBorder(BorderFactory.createLineBorder(AppColors.DARK_PURPLE));
        peopleField.setText("2");
        searchPanel.add(peopleField, gbc);

        // 搜索按钮
        gbc.gridx = homestayId == null ? 8 : 6;
        searchButton = new JButton("搜索");
        styleButton(searchButton);
        searchPanel.add(searchButton, gbc);

        mainPanel.add(searchPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // 提示标签
        JLabel tipLabel = new JLabel("日期格式: yyyy-MM-dd (例如: 2026-03-20)");
        tipLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        tipLabel.setForeground(AppColors.PRIMARY_PURPLE);
        tipLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(tipLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

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
        roomTable.getTableHeader().setForeground(AppColors.DARK_PURPLE);
        roomTable.setSelectionBackground(AppColors.HOVER_PURPLE);

        // 设置列宽
        roomTable.getColumnModel().getColumn(0).setPreferredWidth(80);
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
                int row = roomTable.rowAtPoint(e.getPoint());
                int col = roomTable.columnAtPoint(e.getPoint());
                if (row != -1) {
                    // 确保点击任何列都能选中行
                    roomTable.setRowSelectionInterval(row, row);
                    
                    Boolean selected = (Boolean) tableModel.getValueAt(row, 0);
                    boolean newSelected = selected == null ? true : !selected;
                    tableModel.setValueAt(newSelected, row, 0);

                    // 更新选中房间列表
                    selectedRoomIds.clear();
                    selectedRoomInfos.clear();
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        Boolean isSelected = (Boolean) tableModel.getValueAt(i, 0);
                        if (isSelected != null && isSelected) {
                            selectedRoomIds.add((int) tableModel.getValueAt(i, 1));
                            String roomInfo = tableModel.getValueAt(i, 2) + " " +
                                    tableModel.getValueAt(i, 3) + " " +
                                    tableModel.getValueAt(i, 4);
                            selectedRoomInfos.add(roomInfo);
                        }
                    }

                    // 更新选中房间标签
                    if (selectedRoomInfos.isEmpty()) {
                        selectedRoomLabel.setText("已选房间: 未选择");
                    } else {
                        StringBuilder info = new StringBuilder("已选房间: ");
                        for (int i = 0; i < selectedRoomInfos.size(); i++) {
                            info.append(selectedRoomInfos.get(i));
                            if (i < selectedRoomInfos.size() - 1) {
                                info.append(" | ");
                            }
                        }
                        selectedRoomLabel.setText(info.toString());
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(roomTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppColors.PRIMARY_PURPLE));
        scrollPane.setPreferredSize(new Dimension(850, 300));
        mainPanel.add(scrollPane);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 底部面板
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(AppColors.LIGHT_PURPLE);

        selectedRoomLabel = new JLabel("已选房间: 未选择");
        selectedRoomLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
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

        mainPanel.add(bottomPanel);

        add(mainPanel);

        // 事件监听
        searchButton.addActionListener(e -> searchRooms());
        reserveButton.addActionListener(e -> reserveRoom());
        backButton.addActionListener(e -> dispose());

        // 初始加载数据
        loadAvailableRooms();
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        button.setBackground(AppColors.BUTTON_PURPLE);
        button.setForeground(AppColors.DARK_PURPLE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));

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
    
    // 获取可用房间
    List<Room> availableRooms;
    if (homestayId != null) {
        // 获取指定民宿的可用房间
        availableRooms = roomService.getAvailableRoomsByHomestayId(homestayId);
    } else {
        // 获取所有可用房间
        availableRooms = roomService.getAvailableRooms();
    }
    
    // 按ID升序排序
    availableRooms.sort((r1, r2) -> Integer.compare(r1.getRoomId(), r2.getRoomId()));
    
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
    String city = homestayId == null ? cityField.getText().trim() : "";
    String checkInStr = checkInField.getText().trim();
    String checkOutStr = checkOutField.getText().trim();
    String peopleStr = peopleField.getText().trim();
        // TODO: 调用Service搜索可用房间
        // 验证输入
        if (homestayId == null && city.isEmpty()) {
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
        List<Room> searchResult;
        if (homestayId != null) {
            // 搜索指定民宿的可用房间
            searchResult = roomService.searchAvailableRoomsByHomestayId(
                homestayId, checkIn, checkOut, peopleCount, 1, 100);
        } else {
            // 搜索所有民宿的可用房间
            searchResult = roomService.searchAvailableRooms(
                city, checkIn, checkOut, peopleCount, 1, 100);
        }
    
    // 按ID升序排序
    searchResult.sort((r1, r2) -> Integer.compare(r1.getRoomId(), r2.getRoomId()));
    
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
    if (selectedRoomIds.isEmpty()) {
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

        // 计算天数
        long days = (checkOut.getTime() - checkIn.getTime()) / (1000 * 60 * 60 * 24);
        if (days <= 0) days = 1;

        // 准备确认消息
        StringBuilder message = new StringBuilder("确认预订以下房间？\n\n");
        double totalPrice = 0;

        for (int i = 0; i < selectedRoomIds.size(); i++) {
            int roomId = selectedRoomIds.get(i);
            String roomInfo = selectedRoomInfos.get(i);
            
            // 获取房间价格
            double roomPrice = 0;
            for (int j = 0; j < tableModel.getRowCount(); j++) {
                if ((int) tableModel.getValueAt(j, 1) == roomId) {
                    roomPrice = (double) tableModel.getValueAt(j, 6);
                    break;
                }
            }
            
            double roomTotal = roomPrice * days;
            totalPrice += roomTotal;
            
            message.append("房间: " + roomInfo + "\n");
        }
        
        message.append("入住: " + checkInStr + "\n");
        message.append("离店: " + checkOutStr + "\n");
        message.append("人数: " + guestsCount + "\n");
        message.append("天数: " + days + "晚\n");
        message.append("总价: " + String.format("%.2f", totalPrice) + "元");

        int confirm = JOptionPane.showConfirmDialog(this, message.toString(), "确认预订", 
                                                  JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean allSuccess = true;
            StringBuilder successMessages = new StringBuilder("预订成功！\n");
            
            for (int i = 0; i < selectedRoomIds.size(); i++) {
                int roomId = selectedRoomIds.get(i);
                
                // 获取房间价格
                double roomPrice = 0;
                for (int j = 0; j < tableModel.getRowCount(); j++) {
                    if ((int) tableModel.getValueAt(j, 1) == roomId) {
                        roomPrice = (double) tableModel.getValueAt(j, 6);
                        break;
                    }
                }
                
                double roomTotal = roomPrice * days;
                
                // 创建预订对象
                com.booking.model.Reservation reservation = new com.booking.model.Reservation();
                reservation.setRoomId(roomId);
                reservation.setGuestId(currentUser.getUserId());
                reservation.setCheckInDate(checkIn);
                reservation.setCheckOutDate(checkOut);
                reservation.setGuestsCount(guestsCount);
                reservation.setTotalPrice(roomTotal);
                reservation.setGuestName(currentUser.getRealName());
                reservation.setGuestPhone(currentUser.getPhone());

                // 创建支付对象
                com.booking.model.Payment payment = new com.booking.model.Payment();
                payment.setAmount(roomTotal);
                payment.setPaymentMethod("WECHAT");

                int result = reservationService.createReservation(reservation, payment);
                
                if (result == 1) {
                    successMessages.append("房间 " + selectedRoomInfos.get(i) + " 预订成功！订单号: " + reservation.getReservationNo() + "\n");
                } else if (result == -1) {
                    successMessages.append("房间 " + selectedRoomInfos.get(i) + " 预订失败：房间已被预订\n");
                    allSuccess = false;
                } else {
                    successMessages.append("房间 " + selectedRoomInfos.get(i) + " 预订失败：系统错误\n");
                    allSuccess = false;
                }
            }
            
            JOptionPane.showMessageDialog(this, successMessages.toString(), 
                allSuccess ? "成功" : "部分成功", 
                allSuccess ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
            
            if (allSuccess) {
                dispose();
            }
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "输入数据错误: " + e.getMessage(), 
                                    "错误", JOptionPane.ERROR_MESSAGE);
    }
    }
}