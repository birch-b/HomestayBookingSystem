package com.booking.dao.impl;

import com.booking.dao.PaymentDAO;
import com.booking.model.Payment;
import com.booking.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 支付记录数据访问实现类
 */
public class PaymentDAOImpl implements PaymentDAO {

    // ==================== 基础CRUD ====================

    @Override
    public int insert(Payment payment) {
        String sql = "INSERT INTO payments (payment_no, reservation_id, amount, payment_method, " +
                "status, transaction_id, pay_time) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, payment.getPaymentNo());
            pstmt.setInt(2, payment.getReservationId());
            pstmt.setDouble(3, payment.getAmount());
            pstmt.setString(4, payment.getPaymentMethod());
            pstmt.setString(5, payment.getStatus());
            pstmt.setString(6, payment.getTransactionId());
            pstmt.setTimestamp(7, payment.getPayTime() != null ?
                    new Timestamp(payment.getPayTime().getTime()) : null);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    payment.setPaymentId(rs.getInt(1));
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
        String sql = "DELETE FROM payments WHERE payment_id = ?";
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
    public int update(Payment payment) {
        String sql = "UPDATE payments SET payment_no=?, reservation_id=?, amount=?, payment_method=?, " +
                "status=?, transaction_id=?, pay_time=? WHERE payment_id=?";
        Connection conn;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, payment.getPaymentNo());
            pstmt.setInt(2, payment.getReservationId());
            pstmt.setDouble(3, payment.getAmount());
            pstmt.setString(4, payment.getPaymentMethod());
            pstmt.setString(5, payment.getStatus());
            pstmt.setString(6, payment.getTransactionId());
            pstmt.setTimestamp(7, payment.getPayTime() != null ?
                    new Timestamp(payment.getPayTime().getTime()) : null);
            pstmt.setInt(8, payment.getPaymentId());

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public Payment selectById(int id) {
        String sql = "SELECT * FROM payments WHERE payment_id = ?";
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractPaymentFromResultSet(rs);
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
    public List<Payment> selectAll() {
        String sql = "SELECT * FROM payments ORDER BY payment_id DESC";
        List<Payment> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractPaymentFromResultSet(rs));
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
    public List<Payment> selectByPage(int pageNum, int pageSize) {
        String sql = "SELECT * FROM payments ORDER BY payment_id DESC LIMIT ?, ?";
        List<Payment> list = new ArrayList<>();
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
                list.add(extractPaymentFromResultSet(rs));
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
        String sql = "SELECT COUNT(*) FROM payments";
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
    public List<Payment> selectByReservationId(int reservationId) {
        String sql = "SELECT * FROM payments WHERE reservation_id = ? ORDER BY create_time DESC";
        List<Payment> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, reservationId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractPaymentFromResultSet(rs));
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
    public Payment selectByPaymentNo(String paymentNo) {
        String sql = "SELECT * FROM payments WHERE payment_no = ?";
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, paymentNo);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractPaymentFromResultSet(rs);
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
    public List<Payment> selectByStatus(String status) {
        String sql = "SELECT * FROM payments WHERE status = ? ORDER BY create_time DESC";
        List<Payment> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractPaymentFromResultSet(rs));
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
    public List<Payment> selectByMethod(String method) {
        String sql = "SELECT * FROM payments WHERE payment_method = ? ORDER BY create_time DESC";
        List<Payment> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, method);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractPaymentFromResultSet(rs));
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
    public List<Payment> selectByDateRange(Date start, Date end) {
        String sql = "SELECT * FROM payments WHERE create_time BETWEEN ? AND ? ORDER BY create_time DESC";
        List<Payment> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setTimestamp(1, new Timestamp(start.getTime()));
            pstmt.setTimestamp(2, new Timestamp(end.getTime()));
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractPaymentFromResultSet(rs));
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
    public double getTotalPaidByReservation(int reservationId) {
        String sql = "SELECT SUM(amount) FROM payments WHERE reservation_id = ? AND status = 'SUCCESS'";
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, reservationId);
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
    public int updateStatus(int paymentId, String status) {
        String sql = "UPDATE payments SET status = ? WHERE payment_id = ?";
        Connection conn;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setInt(2, paymentId);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public int paymentSuccess(int paymentId, String transactionId) {
        String sql = "UPDATE payments SET status = 'SUCCESS', transaction_id = ?, pay_time = NOW() " +
                "WHERE payment_id = ?";
        Connection conn;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, transactionId);
            pstmt.setInt(2, paymentId);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public List<Object[]> getMonthlyIncome(int homestayId, int year) {
        String sql = "SELECT MONTH(p.pay_time) as month, SUM(p.amount) as total " +
                "FROM payments p " +
                "JOIN reservations r ON p.reservation_id = r.reservation_id " +
                "JOIN rooms rm ON r.room_id = rm.room_id " +
                "WHERE rm.homestay_id = ? AND YEAR(p.pay_time) = ? AND p.status = 'SUCCESS' " +
                "GROUP BY MONTH(p.pay_time) " +
                "ORDER BY month";
        List<Object[]> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, homestayId);
            pstmt.setInt(2, year);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Object[] row = new Object[2];
                row[0] = rs.getInt("month");
                row[1] = rs.getDouble("total");
                list.add(row);
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

    private Payment extractPaymentFromResultSet(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentId(rs.getInt("payment_id"));
        payment.setPaymentNo(rs.getString("payment_no"));
        payment.setReservationId(rs.getInt("reservation_id"));
        payment.setAmount(rs.getDouble("amount"));
        payment.setPaymentMethod(rs.getString("payment_method"));
        payment.setStatus(rs.getString("status"));
        payment.setTransactionId(rs.getString("transaction_id"));
        payment.setPayTime(rs.getTimestamp("pay_time"));
        payment.setCreateTime(rs.getTimestamp("create_time"));
        return payment;
    }
}