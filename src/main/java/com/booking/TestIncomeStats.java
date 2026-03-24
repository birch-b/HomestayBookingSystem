package com.booking;

import com.booking.model.User;
import com.booking.view.IncomeStatsView;

import javax.swing.*;

public class TestIncomeStats {
    public static void main(String[] args) {
        // 创建测试用户
        User testUser = new User();
        testUser.setUserId(1);
        testUser.setUsername("admin");
        testUser.setRealName("管理员");
        testUser.setRole("ADMIN");
        
        // 显示收入统计页面
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                IncomeStatsView incomeStatsView = new IncomeStatsView(testUser);
                incomeStatsView.setVisible(true);
            }
        });
    }
}