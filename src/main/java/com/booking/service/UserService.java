package com.booking.service;

import com.booking.model.User;
import java.util.List;

/**
 * 用户业务逻辑接口
 */
public interface UserService {

    /**
     * 用户注册
     * @return 1成功，-1用户名已存在，0失败
     */
    int register(User user);

    /**
     * 用户登录
     * @return 登录成功返回User对象，失败返回null
     */
    User login(String username, String password);

    /**
     * 根据ID查询用户
     */
    User getUserById(int userId);

    /**
     * 根据用户名查询用户
     */
    User getUserByUsername(String username);

    /**
     * 查询所有用户（分页）
     */
    List<User> getAllUsers(int pageNum, int pageSize);

    /**
     * 更新用户信息
     * @return true成功，false失败
     */
    boolean updateUser(User user);

    /**
     * 删除用户（软删除/禁用）
     */
    boolean deleteUser(int userId);

    /**
     * 批量删除用户
     */
    int batchDeleteUsers(List<Integer> userIds);

    /**
     * 搜索用户（按用户名、真实姓名、电话）
     */
    List<User> searchUsers(String keyword, int pageNum, int pageSize);

    /**
     * 统计搜索总数
     */
    long countSearch(String keyword);

    /**
     * 获取所有管理员
     */
    List<User> getAdminList();

    /**
     * 获取所有民宿主
     */
    List<User> getHostList();

    /**
     * 获取所有游客
     */
    List<User> getGuestList();

    /**
     * 根据角色查询用户
     */
    List<User> getUsersByRole(String role, int pageNum, int pageSize);

    /**
     * 检查用户名是否存在
     */
    boolean isUsernameExists(String username);

    /**
     * 检查用户名是否存在（排除自己）
     */
    boolean isUsernameExistsExcludeSelf(String username, int userId);

    /**
     * 根据真实姓名查询同名用户
     */
    List<User> findUsersByRealName(String realName);

    /**
     * 统计同名用户数量
     */
    int countByRealName(String realName);

    /**
     * 修改密码
     */
    boolean changePassword(int userId, String oldPassword, String newPassword);

    /**
     * 重置密码（管理员功能）
     */
    boolean resetPassword(int userId, String newPassword);

    /**
     * 更新最后登录时间
     */
    void updateLastLoginTime(int userId);

    /**
     * 统计用户总数
     */
    long getTotalUserCount();

    /**
     * 统计各角色用户数量
     */
    int[] getRoleCounts();
    /**
     * 更新用户状态
     * @param userId 用户ID
     * @param status 1正常 0禁用
     * @return true成功，false失败
     */
    boolean updateUserStatus(int userId, int status);
}