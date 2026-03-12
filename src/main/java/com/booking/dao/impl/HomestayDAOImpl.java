package com.booking.dao.impl;

import com.booking.dao.HomestayDAO;
import com.booking.model.Homestay;
import com.booking.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 民宿数据访问实现类
 */
public class HomestayDAOImpl implements HomestayDAO {

    // ==================== 基础CRUD ====================

    /**
     * 插入民宿数据
     */
    @Override
    public int insert(Homestay homestay) {
        String sql = "INSERT INTO homestays (host_id, name, address, city, description, facilities, phone, rating, review_count, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setInt(1, homestay.getHostId());
            stmt.setString(2, homestay.getName());
            stmt.setString(3, homestay.getAddress());
            stmt.setString(4, homestay.getCity());
            stmt.setString(5, homestay.getDescription());
            stmt.setString(6, homestay.getFacilities());
            stmt.setString(7, homestay.getPhone());
            stmt.setDouble(8, homestay.getRating());
            stmt.setInt(9, homestay.getReviewCount());
            stmt.setInt(10, homestay.getStatus());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    homestay.setHomestayId(rs.getInt(1));
                }
            }
            return affectedRows;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.closeResultSet(rs);
            DBUtil.closeStatement(stmt);
        }
    }

    /**
     * 根据ID删除民宿
     */
    @Override
    public int deleteById(int id) {
        String sql = "DELETE FROM homestays WHERE homestay_id = ?";
        Connection conn;
        PreparedStatement stmt = null;

        try {
            conn = DBUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.closeStatement(stmt);
        }
    }

    /**
     * 更新民宿信息
     */
    @Override
    public int update(Homestay homestay) {
        String sql = "UPDATE homestays SET host_id=?, name=?, address=?, city=?, description=?, " +
                "facilities=?, phone=?, rating=?, review_count=?, status=? WHERE homestay_id=?";
        Connection conn;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, homestay.getHostId());
            pstmt.setString(2, homestay.getName());
            pstmt.setString(3, homestay.getAddress());
            pstmt.setString(4, homestay.getCity());
            pstmt.setString(5, homestay.getDescription());
            pstmt.setString(6, homestay.getFacilities());
            pstmt.setString(7, homestay.getPhone());
            pstmt.setDouble(8, homestay.getRating());
            pstmt.setInt(9, homestay.getReviewCount());
            pstmt.setInt(10, homestay.getStatus());
            pstmt.setInt(11, homestay.getHomestayId());

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.closeStatement(pstmt);
        }
    }

    /**
     * 根据ID查询民宿
     */
    @Override
    public Homestay selectById(int id) {
        String sql = "SELECT * FROM homestays WHERE homestay_id = ?";
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractHomestayFromResultSet(rs);
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

    /**
     * 查询所有民宿
     */
    @Override
    public List<Homestay> selectAll() {
        String sql = "SELECT * FROM homestays ORDER BY homestay_id";
        List<Homestay> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractHomestayFromResultSet(rs));
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

    /**
     * 分页查询民宿
     */
    @Override
    public List<Homestay> selectByPage(int pageNum, int pageSize) {
        String sql = "SELECT * FROM homestays ORDER BY homestay_id LIMIT ?, ?";
        List<Homestay> list = new ArrayList<>();
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
                list.add(extractHomestayFromResultSet(rs));
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

    /**
     * 统计民宿总数
     */
    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM homestays";
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

    /**
     * 根据民宿主ID查询
     */
    @Override
    public List<Homestay> selectByHostId(int hostId) {
        String sql = "SELECT * FROM homestays WHERE host_id = ? ORDER BY homestay_id";
        List<Homestay> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, hostId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractHomestayFromResultSet(rs));
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

    /**
     * 根据城市查询
     */
    @Override
    public List<Homestay> selectByCity(String city) {
        String sql = "SELECT * FROM homestays WHERE city LIKE ? ORDER BY rating DESC";
        List<Homestay> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + city + "%");
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractHomestayFromResultSet(rs));
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

    /**
     * 模糊搜索民宿（按名称、地址、城市）
     */
    @Override
    public List<Homestay> search(String keyword) {
        String sql = "SELECT * FROM homestays WHERE name LIKE ? OR address LIKE ? OR city LIKE ?";
        List<Homestay> list = new ArrayList<>();
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
                list.add(extractHomestayFromResultSet(rs));
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

    /**
     * 根据评分范围查询
     */
    @Override
    public List<Homestay> selectByRatingRange(double minRating, double maxRating) {
        String sql = "SELECT * FROM homestays WHERE rating BETWEEN ? AND ? ORDER BY rating DESC";
        List<Homestay> list = new ArrayList<>();
        Connection conn;
            PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setDouble(1, minRating);
            pstmt.setDouble(2, maxRating);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractHomestayFromResultSet(rs));
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

    /**
     * 更新民宿评分
     */
    @Override
    public int updateRating(int homestayId, double newRating) {
        String sql = "UPDATE homestays SET rating = ? WHERE homestay_id = ?";
        Connection conn;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setDouble(1, newRating);
            pstmt.setInt(2, homestayId);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.closeStatement(pstmt);
        }
    }

    // ==================== 工具方法 ====================

    /**
     * 将ResultSet转换为Homestay对象
     */
    private Homestay extractHomestayFromResultSet(ResultSet rs) throws SQLException {
        Homestay homestay = new Homestay();
        homestay.setHomestayId(rs.getInt("homestay_id"));
        homestay.setHostId(rs.getInt("host_id"));
        homestay.setName(rs.getString("name"));
        homestay.setAddress(rs.getString("address"));
        homestay.setCity(rs.getString("city"));
        homestay.setDescription(rs.getString("description"));
        homestay.setFacilities(rs.getString("facilities"));
        homestay.setPhone(rs.getString("phone"));
        homestay.setRating(rs.getDouble("rating"));
        homestay.setReviewCount(rs.getInt("review_count"));
        homestay.setStatus(rs.getInt("status"));
        homestay.setCreateTime(rs.getTimestamp("create_time"));
        homestay.setUpdateTime(rs.getTimestamp("update_time"));
        return homestay;
    }
}