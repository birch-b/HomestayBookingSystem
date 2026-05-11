package com.booking.model;
import java.util.Date;
public class User {
/**
 * 用户实体类
 * 对应数据库表：users
 */
    private int userId;           // 用户ID
    private String username;       // 用户名
    private String password;       // 密码（加密后）
    private String salt;           // 盐值（用于密码加密）
    private String role;           // 角色：ADMIN/HOST/GUEST
    private String realName;       // 真实姓名
    private String phone;          // 手机号
    private String email;          // 邮箱
    private Date createTime;       // 创建时间
    private Date lastLoginTime;    // 最后登录时间
    private int status;            // 状态：1正常 0禁用
    // 无参构造方法（必须）
    public User() {
    }
    public User(int userId, String username, String password, String role,
                String realName, String phone, String email,
                Date createTime, Date lastLoginTime, int status) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.realName = realName;
        this.phone = phone;
        this.email = email;
        this.createTime = createTime;
        this.lastLoginTime = lastLoginTime;
        this.status = status;
    }
    // Getter和Setter方法
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getSalt() {
        return salt;
    }
    public void setSalt(String salt) {
        this.salt = salt;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public String getRealName() {
        return realName;
    }
    public void setRealName(String realName) {
        this.realName = realName;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", realName='" + realName + '\'' +
                ", phone='" + phone + '\'' +
                ", status=" + status +
                '}';
    }
}
