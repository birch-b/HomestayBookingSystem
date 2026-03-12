package com.booking;

import com.booking.dao.UserDAO;
import com.booking.dao.impl.UserDAOImpl;
import com.booking.model.User;
import java.util.List;

public class TestUserDAO {
    public static void main(String[] args) {
        System.out.println("========== 测试用户DAO ==========");

        UserDAO userDAO = new UserDAOImpl();

        // 1. 测试统计总数
        long count = userDAO.count();
        System.out.println("用户总数：" + count);

        // 2. 测试查询所有用户
        System.out.println("\n所有用户列表：");
        List<User> userList = userDAO.selectAll();
        if (userList.isEmpty()) {
            System.out.println("暂无用户数据（需要先插入测试数据）");
        } else {
            for (User user : userList) {
                System.out.println(user);
            }
        }

        // 3. 测试分页查询
        System.out.println("\n分页查询（第1页，每页5条）：");
        List<User> pageList = userDAO.selectByPage(1, 5);
        for (User user : pageList) {
            System.out.println(user);
        }
    }
}