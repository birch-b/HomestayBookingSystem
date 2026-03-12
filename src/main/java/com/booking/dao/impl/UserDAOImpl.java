package com.booking.dao.impl;

import com.booking.dao.UserDAO;
import com.booking.model.User;
import com.booking.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户数据访问实现类
 */
public class UserDAOImpl implements UserDAO {

    // ==================== 基础CRUD ====================

    @Override
    public int insert(User user) {
        String sql = "INSERT INTO users (username, password, role, real_name, phone, email, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getRealName());
            stmt.setString(5, user.getPhone());
            stmt.setString(6, user.getEmail());
            stmt.setInt(7, user.getStatus());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    user.setUserId(rs.getInt(1));
                }
            }
            return affectedRows;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.closeResultSet(rs);
            DBUtil.closeStatement(stmt);
            // 注意：不关闭Connection，由Service层管理事务
        }
    }

    @Override
    public int deleteById(int id) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        Connection conn;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public int update(User user) {
        String sql = "UPDATE users SET username=?, password=?, role=?, real_name=?, " +
                "phone=?, email=?, status=? WHERE user_id=?";
        Connection conn;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getRealName());
            pstmt.setString(5, user.getPhone());
            pstmt.setString(6, user.getEmail());
            pstmt.setInt(7, user.getStatus());
            pstmt.setInt(8, user.getUserId());

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public User selectById(int id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            DBUtil.closeResultSet(rs);
            DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public List<User> selectAll() {
        String sql = "SELECT * FROM users ORDER BY user_id";
        List<User> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractUserFromResultSet(rs));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        } finally {
            DBUtil.closeResultSet(rs);
            DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public List<User> selectByPage(int pageNum, int pageSize) {
        String sql = "SELECT * FROM users ORDER BY user_id LIMIT ?, ?";
        List<User> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, (pageNum - 1) * pageSize);
            pstmt.setInt(2, pageSize);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractUserFromResultSet(rs));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        } finally {
            DBUtil.closeResultSet(rs);
            DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM users";
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.closeResultSet(rs);
            DBUtil.closeStatement(pstmt);
        }
    }

    // ==================== 自定义方法 ====================

    @Override
    public User selectByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            DBUtil.closeResultSet(rs);
            DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND status = 1";
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            DBUtil.closeResultSet(rs);
            DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public List<User> selectByRole(String role) {
        String sql = "SELECT * FROM users WHERE role = ? ORDER BY user_id";
        List<User> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, role);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractUserFromResultSet(rs));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        } finally {
            DBUtil.closeResultSet(rs);
            DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public List<User> search(String keyword) {
        String sql = "SELECT * FROM users WHERE username LIKE ? OR real_name LIKE ? OR phone LIKE ?";
        List<User> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            String pattern = "%" + keyword + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            pstmt.setString(3, pattern);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractUserFromResultSet(rs));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        } finally {
            DBUtil.closeResultSet(rs);
            DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public int updateLastLoginTime(int userId) {
        String sql = "UPDATE users SET last_login_time = NOW() WHERE user_id = ?";
        Connection conn;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public List<User> selectByRealName(String realName) {
        String sql = "SELECT * FROM users WHERE real_name = ? ORDER BY user_id";
        List<User> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, realName);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractUserFromResultSet(rs));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        } finally {
            DBUtil.closeResultSet(rs);
            if (pstmt != null) {
                DBUtil.closeStatement(pstmt);
            }
        }
    }

    @Override
    public int countByRealName(String realName) {
        String sql = "SELECT COUNT(*) FROM users WHERE real_name = ?";
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, realName);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.closeResultSet(rs);
            if (pstmt != null) {
                DBUtil.closeStatement(pstmt);
            }
        }
    }

    @Override
    public boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.closeResultSet(rs);
            if (pstmt != null) {
                DBUtil.closeStatement(pstmt);
            }
        }
    }

    @Override
    public boolean isUsernameExistsExcludeSelf(String username, int userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ? AND user_id != ?";
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setInt(2, userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.closeResultSet(rs);
            if (pstmt != null) {
                DBUtil.closeStatement(pstmt);
            }
        }
    }

    // ==================== 工具方法 ====================

    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setRealName(rs.getString("real_name"));
        user.setPhone(rs.getString("phone"));
        user.setEmail(rs.getString("email"));
        user.setCreateTime(rs.getTimestamp("create_time"));
        user.setLastLoginTime(rs.getTimestamp("last_login_time"));
        user.setStatus(rs.getInt("status"));
        return user;
    }
}