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

        // ==================== 新增：测试同名检测功能 ====================
        System.out.println("\n========== 测试同名检测 ==========");

        // 4. 按真实姓名查询
        System.out.println("\n=== 4. 按真实姓名查询 ===");
        String[] testNames = {"张三", "李四", "王五", "赵六", "管理员"};

        for (String name : testNames) {
            List<User> sameNameList = userDAO.selectByRealName(name);
            System.out.println("姓名 '" + name + "' 有 " + sameNameList.size() + " 个用户");

            if (!sameNameList.isEmpty()) {
                System.out.println("  同名用户详情：");
                for (User u : sameNameList) {
                    System.out.println("    - ID:" + u.getUserId() +
                            ", 用户名:" + u.getUsername() +
                            ", 真实姓名:" + u.getRealName());
                }
            }
        }

        // 5. 统计同名人数
        System.out.println("\n=== 5. 统计同名人数 ===");
        String targetName = "张三";
        int sameNameCount = userDAO.countByRealName(targetName);
        System.out.println("姓名 '" + targetName + "' 共有 " + sameNameCount + " 人");

        // 6. 测试边界情况
        System.out.println("\n=== 6. 测试边界情况 ===");
        String emptyName = "";
        List<User> emptyList = userDAO.selectByRealName(emptyName);
        System.out.println("查询空字符串: " + emptyList.size() + " 条");

        String notExistName = "不存在的人";
        List<User> notExistList = userDAO.selectByRealName(notExistName);
        System.out.println("查询不存在的人: " + notExistList.size() + " 条");
    }
}