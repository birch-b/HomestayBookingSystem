package com.booking.service.impl;

import com.booking.dao.UserDAO;
import com.booking.dao.impl.UserDAOImpl;
import com.booking.model.User;
import com.booking.service.UserService;
import com.booking.util.MD5Util;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户业务逻辑实现类
 */
public class UserServiceImpl implements UserService {

    private UserDAO userDAO;

    // 无参构造
    public UserServiceImpl() {
        this.userDAO = new UserDAOImpl();
    }

    // 带参构造（用于测试）
    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public int register(User user) {
        // 1. 检查用户名是否已存在
        if (userDAO.isUsernameExists(user.getUsername())) {
            return -1;  // 用户名已存在
        }

        // 2. 密码加密
        user.setPassword(MD5Util.md5(user.getPassword()));

        // 3. 设置默认值
        if (user.getStatus() == 0) {
            user.setStatus(1);  // 默认正常
        }

        // 4. 插入数据库
        int result = userDAO.insert(user);
        return result > 0 ? 1 : 0;
    }

    @Override
    public User login(String username, String password) {
        // 密码加密后验证
        String encryptedPwd = MD5Util.md5(password);
        User user = userDAO.login(username, encryptedPwd);

        // 登录成功，更新最后登录时间
        if (user != null) {
            userDAO.updateLastLoginTime(user.getUserId());
        }
        return user;
    }

    @Override
    public User getUserById(int userId) {
        return userDAO.selectById(userId);
    }

    @Override
    public User getUserByUsername(String username) {
        return userDAO.selectByUsername(username);
    }

    @Override
    public List<User> getAllUsers(int pageNum, int pageSize) {
        return userDAO.selectByPage(pageNum, pageSize);
    }

    @Override
    public boolean updateUser(User user) {
        // 如果密码不为空，需要加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(MD5Util.md5(user.getPassword()));
        }

        int result = userDAO.update(user);
        return result > 0;
    }

    @Override
    public boolean deleteUser(int userId) {
        // 软删除：将状态设为0
        User user = userDAO.selectById(userId);
        if (user == null) {
            return false;
        }
        user.setStatus(0);
        int result = userDAO.update(user);
        return result > 0;
    }

    @Override
    public int batchDeleteUsers(List<Integer> userIds) {
        int successCount = 0;
        for (Integer userId : userIds) {
            if (deleteUser(userId)) {
                successCount++;
            }
        }
        return successCount;
    }

    @Override
    public List<User> searchUsers(String keyword, int pageNum, int pageSize) {
        // 先获取所有匹配的用户
        List<User> allMatches = userDAO.search(keyword);

        // 手动分页
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, allMatches.size());

        if (start >= allMatches.size()) {
            return new ArrayList<>();
        }

        return allMatches.subList(start, end);
    }

    @Override
    public long countSearch(String keyword) {
        return userDAO.search(keyword).size();
    }

    @Override
    public List<User> getAdminList() {
        return userDAO.selectByRole("ADMIN");
    }

    @Override
    public List<User> getHostList() {
        return userDAO.selectByRole("HOST");
    }

    @Override
    public List<User> getGuestList() {
        return userDAO.selectByRole("GUEST");
    }

    @Override
    public List<User> getUsersByRole(String role, int pageNum, int pageSize) {
        List<User> allByRole = userDAO.selectByRole(role);

        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, allByRole.size());

        if (start >= allByRole.size()) {
            return new ArrayList<>();
        }

        return allByRole.subList(start, end);
    }

    @Override
    public boolean isUsernameExists(String username) {
        return userDAO.isUsernameExists(username);
    }

    @Override
    public boolean isUsernameExistsExcludeSelf(String username, int userId) {
        return userDAO.isUsernameExistsExcludeSelf(username, userId);
    }

    @Override
    public List<User> findUsersByRealName(String realName) {
        if (realName == null || realName.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return userDAO.selectByRealName(realName);
    }

    @Override
    public int countByRealName(String realName) {
        if (realName == null || realName.trim().isEmpty()) {
            return 0;
        }
        return userDAO.countByRealName(realName);
    }

    @Override
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        // 1. 获取用户
        User user = userDAO.selectById(userId);
        if (user == null) {
            return false;
        }

        // 2. 验证旧密码
        String encryptedOld = MD5Util.md5(oldPassword);
        if (!encryptedOld.equals(user.getPassword())) {
            return false;  // 旧密码错误
        }

        // 3. 设置新密码
        user.setPassword(MD5Util.md5(newPassword));
        int result = userDAO.update(user);
        return result > 0;
    }

    @Override
    public boolean resetPassword(int userId, String newPassword) {
        User user = userDAO.selectById(userId);
        if (user == null) {
            return false;
        }

        user.setPassword(MD5Util.md5(newPassword));
        int result = userDAO.update(user);
        return result > 0;
    }

    @Override
    public void updateLastLoginTime(int userId) {
        userDAO.updateLastLoginTime(userId);
    }

    @Override
    public long getTotalUserCount() {
        return userDAO.count();
    }

    @Override
    public int[] getRoleCounts() {
        int[] counts = new int[3];
        counts[0] = userDAO.selectByRole("ADMIN").size();  // 管理员
        counts[1] = userDAO.selectByRole("HOST").size();   // 民宿主
        counts[2] = userDAO.selectByRole("GUEST").size();  // 游客
        return counts;
    }
}