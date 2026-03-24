package com.booking.dao.impl;

import com.booking.dao.ReviewDAO;
import com.booking.model.Review;
import com.booking.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 评价数据访问实现类
 */
public class ReviewDAOImpl implements ReviewDAO {

    // ==================== 基础CRUD ====================

    @Override
    public int insert(Review review) {
        String sql = "INSERT INTO reviews (reservation_id, guest_id, rating,  comment, host_reply, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setInt(1, review.getReservationId());
            pstmt.setInt(2, review.getGuestId());
            pstmt.setInt(3, review.getRating());
            pstmt.setString(4, review.getComment());
            pstmt.setString(5, review.getHostReply());
            pstmt.setInt(6, review.getStatus());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    review.setReviewId(rs.getInt(1));
                }
            }
            return affectedRows;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.closeResultSet(rs);
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public int deleteById(int id) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
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
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public int update(Review review) {
        String sql = "UPDATE reviews SET reservation_id=?, guest_id=?, rating=?, comment=?, host_reply=?, status=? " +
                "WHERE review_id=?";
        Connection conn;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, review.getReservationId());
            pstmt.setInt(2, review.getGuestId());
            pstmt.setInt(3, review.getRating());
            pstmt.setString(4, review.getComment());
            pstmt.setString(5, review.getHostReply());
            pstmt.setInt(6, review.getStatus());
            pstmt.setInt(7, review.getReviewId());

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public Review selectById(int id) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractReviewFromResultSet(rs);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            DBUtil.closeResultSet(rs);
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public List<Review> selectAll() {
        String sql = "SELECT * FROM reviews ORDER BY review_id ASC";
        List<Review> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractReviewFromResultSet(rs));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        } finally {
            DBUtil.closeResultSet(rs);
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public List<Review> selectByPage(int pageNum, int pageSize) {
        String sql = "SELECT * FROM reviews ORDER BY review_id ASC LIMIT ?, ?";
        List<Review> list = new ArrayList<>();
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
                list.add(extractReviewFromResultSet(rs));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        } finally {
            DBUtil.closeResultSet(rs);
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM reviews";
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
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }

    // ==================== 自定义方法 ====================

    @Override
    public Review selectByReservationId(int reservationId) {
        String sql = "SELECT * FROM reviews WHERE reservation_id = ?";
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, reservationId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractReviewFromResultSet(rs);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            DBUtil.closeResultSet(rs);
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public List<Review> selectByHomestayId(int homestayId) {
        String sql = "SELECT r.* FROM reviews r " +
                "JOIN reservations res ON r.reservation_id = res.reservation_id " +
                "JOIN rooms rm ON res.room_id = rm.room_id " +
                "WHERE rm.homestay_id = ? " +
                "ORDER BY r.review_id ASC";
        List<Review> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, homestayId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractReviewFromResultSet(rs));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        } finally {
            DBUtil.closeResultSet(rs);
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public List<Review> selectByGuestId(int guestId) {
        String sql = "SELECT * FROM reviews WHERE guest_id = ? ORDER BY review_id ASC";
        List<Review> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, guestId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractReviewFromResultSet(rs));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        } finally {
            DBUtil.closeResultSet(rs);
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public List<Review> selectByRatingRange(int minRating, int maxRating) {
        String sql = "SELECT * FROM reviews WHERE rating BETWEEN ? AND ? ORDER BY rating DESC";
        List<Review> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, minRating);
            pstmt.setInt(2, maxRating);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractReviewFromResultSet(rs));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        } finally {
            DBUtil.closeResultSet(rs);
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public double getAverageRatingByHomestay(int homestayId) {
        String sql = "SELECT AVG(r.rating) FROM reviews r " +
                "JOIN reservations res ON r.reservation_id = res.reservation_id " +
                "JOIN rooms rm ON res.room_id = rm.room_id " +
                "WHERE rm.homestay_id = ? AND r.status = 1";
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, homestayId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0.0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        } finally {
            DBUtil.closeResultSet(rs);
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public int[] getRatingDistribution(int homestayId) {
        int[] distribution = new int[5]; // 索引0=1星, 1=2星, 2=3星, 3=4星, 4=5星
        String sql = "SELECT rating, COUNT(*) FROM reviews r " +
                "JOIN reservations res ON r.reservation_id = res.reservation_id " +
                "JOIN rooms rm ON res.room_id = rm.room_id " +
                "WHERE rm.homestay_id = ? AND r.status = 1 " +
                "GROUP BY rating";
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, homestayId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int rating = rs.getInt(1);
                int count = rs.getInt(2);
                if (rating >= 1 && rating <= 5) {
                    distribution[rating - 1] = count;
                }
            }
            return distribution;
        } catch (SQLException e) {
            e.printStackTrace();
            return distribution;
        } finally {
            DBUtil.closeResultSet(rs);
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public int replyReview(int reviewId, String reply) {
        String sql = "UPDATE reviews SET host_reply = ?, reply_time = NOW() WHERE review_id = ?";
        Connection conn;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, reply);
            pstmt.setInt(2, reviewId);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public List<Review> selectLatestReviews(int pageNum, int pageSize) {
        String sql = "SELECT * FROM reviews WHERE status = 1 ORDER BY review_id ASC LIMIT ?, ?";
        List<Review> list = new ArrayList<>();
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
                list.add(extractReviewFromResultSet(rs));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        } finally {
            DBUtil.closeResultSet(rs);
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public List<Review> selectPendingReplies(int homestayId) {
        String sql = "SELECT r.* FROM reviews r " +
                "JOIN reservations res ON r.reservation_id = res.reservation_id " +
                "JOIN rooms rm ON res.room_id = rm.room_id " +
                "WHERE rm.homestay_id = ? AND r.host_reply IS NULL AND r.status = 1 " +
                "ORDER BY r.review_id ASC";
        List<Review> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, homestayId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractReviewFromResultSet(rs));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        } finally {
            DBUtil.closeResultSet(rs);
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }

    // ==================== 工具方法 ====================

    private Review extractReviewFromResultSet(ResultSet rs) throws SQLException {
        Review review = new Review();
        review.setReviewId(rs.getInt("review_id"));
        review.setReservationId(rs.getInt("reservation_id"));
        review.setGuestId(rs.getInt("guest_id"));
        review.setRating(rs.getInt("rating"));
        review.setComment(rs.getString("comment"));
        review.setHostReply(rs.getString("host_reply"));
        review.setCreateTime(rs.getTimestamp("create_time"));
        review.setReplyTime(rs.getTimestamp("reply_time"));
        review.setStatus(rs.getInt("status"));
        return review;
    }
}