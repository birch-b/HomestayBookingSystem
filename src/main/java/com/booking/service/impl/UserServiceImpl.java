package com.booking.service.impl;

import com.booking.dao.UserDAO;
import com.booking.dao.impl.UserDAOImpl;
import com.booking.model.User;
import com.booking.service.UserService;
import com.booking.util.MD5Util;
import com.booking.util.PasswordUtil;

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

        // 2. 生成盐值并进行二次哈希加密
        String salt = PasswordUtil.generateSalt();
        String hashedPassword = PasswordUtil.hashPassword(user.getPassword(), salt);
        
        user.setPassword(hashedPassword);
        user.setSalt(salt);

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
        // 先获取用户（包含盐值）
        User user = userDAO.selectByUsername(username);
        
        // 用户不存在或已禁用
        if (user == null || user.getStatus() != 1) {
            return null;
        }

        // 使用盐值验证密码（支持新旧两种加密方式）
        boolean passwordValid = false;
        String storedPassword = user.getPassword();
        String salt = user.getSalt();
        
        if (salt != null && !salt.isEmpty()) {
            // 新的带盐值加密方式
            passwordValid = PasswordUtil.verifyPassword(password, storedPassword, salt);
        } else {
            // 兼容旧的MD5加密方式
            passwordValid = MD5Util.verify(password, storedPassword);
        }

        // 登录成功，更新最后登录时间
        if (passwordValid) {
            userDAO.updateLastLoginTime(user.getUserId());
            return user;
        }
        
        return null;
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
        int result = userDAO.update(user);
        return result > 0;
    }

    @Override
    public boolean deleteUser(int userId) {
        // 硬删除：从数据库中彻底删除用户
        int result = userDAO.deleteById(userId);
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

        // 2. 验证旧密码（支持新旧两种加密方式）
        boolean oldPasswordValid = false;
        String storedPassword = user.getPassword();
        String salt = user.getSalt();
        
        if (salt != null && !salt.isEmpty()) {
            oldPasswordValid = PasswordUtil.verifyPassword(oldPassword, storedPassword, salt);
        } else {
            oldPasswordValid = MD5Util.verify(oldPassword, storedPassword);
        }
        
        if (!oldPasswordValid) {
            return false;  // 旧密码错误
        }

        // 3. 设置新密码（使用新的带盐值加密方式）
        String newSalt = PasswordUtil.generateSalt();
        String hashedNewPassword = PasswordUtil.hashPassword(newPassword, newSalt);
        user.setPassword(hashedNewPassword);
        user.setSalt(newSalt);
        
        int result = userDAO.update(user);
        return result > 0;
    }

    @Override
    public boolean resetPassword(int userId, String newPassword) {
        User user = userDAO.selectById(userId);
        if (user == null) {
            return false;
        }

        // 使用带盐值的二次哈希加密
        String salt = PasswordUtil.generateSalt();
        String hashedPassword = PasswordUtil.hashPassword(newPassword, salt);
        user.setPassword(hashedPassword);
        user.setSalt(salt);
        
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
    @Override
public boolean updateUserStatus(int userId, int status) {
    User user = userDAO.selectById(userId);
    if (user == null) {
        return false;
    }
    user.setStatus(status);
    int result = userDAO.update(user);
    return result > 0;
}
}