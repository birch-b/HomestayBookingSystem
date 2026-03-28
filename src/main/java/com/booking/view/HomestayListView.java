package com.booking.view;

import com.booking.model.User;
import com.booking.model.Homestay;
import com.booking.service.HomestayService;
import com.booking.service.impl.HomestayServiceImpl;
import com.booking.util.AppColors;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * 民宿列表页面
 */
public class HomestayListView extends MainView {

    private HomestayService homestayService;
    private DefaultListModel<String> listModel;
    private JList<String> homestayList;
    private List<Homestay> currentHomestays;
    private int[] currentPage;
    private int pageSize;
    private int totalPages;
    private JLabel pageLabel;
    private JButton prevBtn;
    private JButton nextBtn;

    public HomestayListView(User user) {
        super(user, "浏览民宿");
        // 设置关闭操作，只关闭当前窗口，不退出整个应用
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    protected void initMenu() {}

    @Override
    protected void initContent() {
        homestayService = new HomestayServiceImpl();
        
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(AppColors.LIGHT_PURPLE);
        
        // 移除退出登录按钮
        JPanel topPanel = (JPanel) getContentPane().getComponent(0);
        if (topPanel != null && topPanel.getComponentCount() > 1) {
            Component eastComponent = topPanel.getComponent(1);
            if (eastComponent instanceof JButton) {
                JButton logoutButton = (JButton) eastComponent;
                if (logoutButton.getText().equals("退出登录")) {
                    topPanel.remove(logoutButton);
                    topPanel.revalidate();
                    topPanel.repaint();
                }
            }
        }
        
        // 初始化分页参数
        pageSize = 10;
        currentPage = new int[]{1};
        long totalCount = homestayService.getTotalCount();
        totalPages = (int) Math.ceil((double) totalCount / pageSize);
        
        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 民宿列表
        listModel = new DefaultListModel<>();
        homestayList = new JList<>(listModel);
        homestayList.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        homestayList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        homestayList.setFixedCellHeight(40);
        
        // 查看详情按钮
        JButton detailBtn = new JButton("查看详情");
        detailBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        detailBtn.setPreferredSize(new Dimension(100, 30));
        
        // 分页控件
        prevBtn = new JButton("上一页");
        nextBtn = new JButton("下一页");
        pageLabel = new JLabel("第 " + currentPage[0] + " 页，共 " + totalPages + " 页");
        pageLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        
        // 加载数据
        currentHomestays = new java.util.ArrayList<>();
        loadData();
        
        // 查看详情按钮事件
        detailBtn.addActionListener(e -> {
            int selectedIndex = homestayList.getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < currentHomestays.size()) {
                Homestay selectedHomestay = currentHomestays.get(selectedIndex);
                openHomestayDetail(selectedHomestay.getHomestayId());
            }
        });
        
        // 上一页按钮事件
        prevBtn.addActionListener(e -> {
            if (currentPage[0] > 1) {
                currentPage[0]--;
                loadData();
            }
        });
        
        // 下一页按钮事件
        nextBtn.addActionListener(e -> {
            if (currentPage[0] < totalPages) {
                currentPage[0]++;
                loadData();
            }
        });
        
        // 返回主页面按钮
        JButton backBtn = new JButton("返回");
        backBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        backBtn.setPreferredSize(new Dimension(120, 30));
        backBtn.addActionListener(e -> {
            dispose();
            new GuestView(currentUser).setVisible(true);
        });
        
        // 组装按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(AppColors.LIGHT_PURPLE);
        buttonPanel.add(backBtn);
        buttonPanel.add(prevBtn);
        buttonPanel.add(pageLabel);
        buttonPanel.add(nextBtn);
        buttonPanel.add(detailBtn);
        
        // 添加组件
        mainPanel.add(new JScrollPane(homestayList), BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        contentPanel.add(mainPanel, BorderLayout.CENTER);
    }
    
    private void loadData() {
        currentHomestays.clear();
        listModel.clear();
        List<Homestay> homestays = homestayService.getAllHomestays(currentPage[0], pageSize);
        for (Homestay homestay : homestays) {
            currentHomestays.add(homestay);
            listModel.addElement(homestay.getName() + " - " + homestay.getCity());
        }
        pageLabel.setText("第 " + currentPage[0] + " 页，共 " + totalPages + " 页");
        prevBtn.setEnabled(currentPage[0] > 1);
        nextBtn.setEnabled(currentPage[0] < totalPages);
    }
    
    private void openHomestayDetail(int id) {
        // 使用统一的民宿详情对话框
        HomestayDetailDialog.show(this, currentUser, id);
    }
}