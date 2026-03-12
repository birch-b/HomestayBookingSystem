package com.booking.dao;
import com.booking.model.User;
import java.util.List;
/**
 * 用户数据访问接口
 */
public interface UserDAO extends BaseDAO<User> {

    /**
     * 根据用户名查询用户
     */
    User selectByUsername(String username);

    /**
     * 用户登录验证
     */
    User login(String username, String password);

    /**
     * 根据角色查询用户
     */
    List<User> selectByRole(String role);

    /**
     * 模糊搜索用户
     */
    List<User> search(String keyword);

    /**
     * 更新最后登录时间
     */
    int updateLastLoginTime(int userId);
    /**
     * 根据真实姓名查询用户
     */
    List<User> selectByRealName(String realName);

    /**
     * 统计同名人数
     */
    int countByRealName(String realName);

    /**
     * 检查用户名是否存在
     */
    boolean isUsernameExists(String username);

    /**
     * 检查用户名是否存在（排除自己）
     */
    boolean isUsernameExistsExcludeSelf(String username, int userId);
}