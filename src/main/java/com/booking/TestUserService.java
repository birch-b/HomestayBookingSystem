package com.booking;

import com.booking.model.User;
import com.booking.service.UserService;
import com.booking.service.impl.UserServiceImpl;

import java.util.List;

public class TestUserService {
    public static void main(String[] args) {
        System.out.println("========== 测试用户Service ==========");

        UserService userService = new UserServiceImpl();

        // 1. 测试注册
        System.out.println("\n=== 1. 测试注册 ===");
        User newUser = new User();
        newUser.setUsername("testuser");
        newUser.setPassword("123456");
        newUser.setRole("GUEST");
        newUser.setRealName("测试用户");
        newUser.setPhone("13800138001");
        newUser.setEmail("test@test.com");

        int regResult = userService.register(newUser);
        if (regResult == 1) {
            System.out.println("✅ 注册成功！用户ID: " + newUser.getUserId());
        } else if (regResult == -1) {
            System.out.println("❌ 注册失败：用户名已存在");
        } else {
            System.out.println("❌ 注册失败：系统错误");
        }

        // 2. 测试登录
        System.out.println("\n=== 2. 测试登录 ===");
        User loginUser = userService.login("testuser", "123456");
        if (loginUser != null) {
            System.out.println("✅ 登录成功！欢迎 " + loginUser.getRealName());
        } else {
            System.out.println("❌ 登录失败：用户名或密码错误");
        }

        // 3. 测试查询所有用户
        System.out.println("\n=== 3. 查询所有用户（第1页，每页5条） ===");
        List<User> userList = userService.getAllUsers(1, 5);
        for (User u : userList) {
            System.out.println(u);
        }

        // 4. 测试同名检测
        System.out.println("\n=== 4. 同名检测 ===");
        String testName = "张三";
        int sameNameCount = userService.countByRealName(testName);
        System.out.println("姓名 '" + testName + "' 有 " + sameNameCount + " 人");

        List<User> sameNameList = userService.findUsersByRealName(testName);
        for (User u : sameNameList) {
            System.out.println("  - " + u.getUsername() + " | " + u.getRealName());
        }

        // 5. 测试角色统计
        System.out.println("\n=== 5. 角色统计 ===");
        int[] roleCounts = userService.getRoleCounts();
        System.out.println("管理员: " + roleCounts[0] + " 人");
        System.out.println("民宿主: " + roleCounts[1] + " 人");
        System.out.println("游客: " + roleCounts[2] + " 人");

        System.out.println("\n========== 测试完成 ==========");
    }
}