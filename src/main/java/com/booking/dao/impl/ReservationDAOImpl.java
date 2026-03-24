package com.booking.dao.impl;

import com.booking.dao.ReservationDAO;
import com.booking.model.Reservation;
import com.booking.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 预订数据访问实现类
 * 核心业务：房间预订、冲突检测、状态管理
 */
public class ReservationDAOImpl implements ReservationDAO {

    // ==================== 基础CRUD ====================

    @Override
    //插入
    public int insert(Reservation reservation) {
        String sql = "INSERT INTO reservations (reservation_no, room_id, guest_id, check_in_date, " +
                "check_out_date, guests_count, total_price, status, guest_name, guest_phone, special_requests) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, reservation.getReservationNo());
            pstmt.setInt(2, reservation.getRoomId());
            pstmt.setInt(3, reservation.getGuestId());
            pstmt.setDate(4, new java.sql.Date(reservation.getCheckInDate().getTime()));
            pstmt.setDate(5, new java.sql.Date(reservation.getCheckOutDate().getTime()));
            pstmt.setInt(6, reservation.getGuestsCount());
            pstmt.setDouble(7, reservation.getTotalPrice());
            pstmt.setString(8, reservation.getStatus());
            pstmt.setString(9, reservation.getGuestName());
            pstmt.setString(10, reservation.getGuestPhone());
            pstmt.setString(11, reservation.getSpecialRequests());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    reservation.setReservationId(rs.getInt(1));
                }
            }
            return affectedRows;
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
    //删除
    public int deleteById(int id) {
        String sql = "DELETE FROM reservations WHERE reservation_id = ?";
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
            if (pstmt != null) {
                DBUtil.closeStatement(pstmt);
            }
        }
    }

    @Override
    //更新
    public int update(Reservation reservation) {
        String sql = "UPDATE reservations SET reservation_no=?, room_id=?, guest_id=?, " +
                "check_in_date=?, check_out_date=?, guests_count=?, total_price=?, status=?, " +
                "guest_name=?, guest_phone=?, special_requests=? WHERE reservation_id=?";
        Connection conn;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, reservation.getReservationNo());
            pstmt.setInt(2, reservation.getRoomId());
            pstmt.setInt(3, reservation.getGuestId());
            pstmt.setDate(4, new java.sql.Date(reservation.getCheckInDate().getTime()));
            pstmt.setDate(5, new java.sql.Date(reservation.getCheckOutDate().getTime()));
            pstmt.setInt(6, reservation.getGuestsCount());
            pstmt.setDouble(7, reservation.getTotalPrice());
            pstmt.setString(8, reservation.getStatus());
            pstmt.setString(9, reservation.getGuestName());
            pstmt.setString(10, reservation.getGuestPhone());
            pstmt.setString(11, reservation.getSpecialRequests());
            pstmt.setInt(12, reservation.getReservationId());

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (pstmt != null) {
                DBUtil.closeStatement(pstmt);
            }
        }
    }

    @Override
    public Reservation selectById(int id) {
        String sql = "SELECT * FROM reservations WHERE reservation_id = ?";
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractReservationFromResultSet(rs);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            DBUtil.closeResultSet(rs);
            if (pstmt != null) {
                DBUtil.closeStatement(pstmt);
            }
        }
    }

    @Override
    public List<Reservation> selectAll() {
        String sql = "SELECT * FROM reservations ORDER BY reservation_id ASC";
        List<Reservation> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractReservationFromResultSet(rs));
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
    public List<Reservation> selectByPage(int pageNum, int pageSize) {
        String sql = "SELECT * FROM reservations ORDER BY reservation_id ASC LIMIT ?, ?";
        List<Reservation> list = new ArrayList<>();
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
                list.add(extractReservationFromResultSet(rs));
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
    public long count() {
        String sql = "SELECT COUNT(*) FROM reservations";
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
            if (pstmt != null) {
                DBUtil.closeStatement(pstmt);
            }
        }
    }

    // ==================== 核心业务方法 ====================

    @Override
    public int createReservation(Reservation reservation) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // 1. 检查房间是否可用
            if (!checkRoomAvailable(reservation.getRoomId(),
                    reservation.getCheckInDate(),
                    reservation.getCheckOutDate())) {
                conn.rollback();
                return -1;  // 房间不可用
            }

            // 2. 生成订单号
            reservation.setReservationNo(generateReservationNo());
            reservation.setStatus("PENDING");  // 默认状态

            // 3. 插入订单
            int result = insert(reservation);
            if (result <= 0) {
                conn.rollback();
                return 0;
            }

            // 4. 更新房间状态为BOOKED
            String updateRoomSql = "UPDATE rooms SET status = 'BOOKED' WHERE room_id = ?";
            PreparedStatement roomStmt = conn.prepareStatement(updateRoomSql);
            roomStmt.setInt(1, reservation.getRoomId());
            roomStmt.executeUpdate();
            roomStmt.close();

            conn.commit();
            return 1;
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.closeConnection();
        }
    }

    @Override
    public boolean checkRoomAvailable(int roomId, Date checkIn, Date checkOut) {
        String sql = "SELECT COUNT(*) FROM reservations " +
                "WHERE room_id = ? " +
                "AND status IN ('PAID', 'CONFIRMED', 'CHECKED_IN') " +
                "AND (check_in_date < ? AND check_out_date > ?)";
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, roomId);
            pstmt.setDate(2, new java.sql.Date(checkOut.getTime()));
            pstmt.setDate(3, new java.sql.Date(checkIn.getTime()));

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
            return true;
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
    public int cancelReservation(int reservationId) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // 1. 获取订单信息
            Reservation reservation = selectById(reservationId);
            if (reservation == null) return 0;

            // 2. 更新订单状态为CANCELLED
            String updateOrderSql = "UPDATE reservations SET status = 'CANCELLED' WHERE reservation_id = ?";
            PreparedStatement orderStmt = conn.prepareStatement(updateOrderSql);
            orderStmt.setInt(1, reservationId);
            orderStmt.executeUpdate();
            orderStmt.close();

            // 3. 释放房间
            String updateRoomSql = "UPDATE rooms SET status = 'AVAILABLE' WHERE room_id = ?";
            PreparedStatement roomStmt = conn.prepareStatement(updateRoomSql);
            roomStmt.setInt(1, reservation.getRoomId());
            roomStmt.executeUpdate();
            roomStmt.close();

            conn.commit();
            return 1;
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.closeConnection();
        }
    }

    // ==================== 查询方法 ====================

    @Override
    public List<Reservation> selectByGuestId(int guestId) {
        String sql = "SELECT * FROM reservations WHERE guest_id = ? ORDER BY reservation_id ASC";
        List<Reservation> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, guestId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractReservationFromResultSet(rs));
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
    public List<Reservation> selectByRoomId(int roomId) {
        String sql = "SELECT * FROM reservations WHERE room_id = ? ORDER BY reservation_id ASC";
        List<Reservation> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, roomId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractReservationFromResultSet(rs));
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
    public List<Reservation> selectByHomestayId(int homestayId) {
        String sql = "SELECT r.* FROM reservations r " +
                "JOIN rooms rm ON r.room_id = rm.room_id " +
                "WHERE rm.homestay_id = ? " +
                "ORDER BY r.reservation_id ASC";
        List<Reservation> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, homestayId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractReservationFromResultSet(rs));
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
    public List<Reservation> selectByDateRange(Date start, Date end) {
        String sql = "SELECT * FROM reservations " +
                "WHERE check_in_date >= ? AND check_out_date <= ? " +
                "ORDER BY check_in_date";
        List<Reservation> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setDate(1, new java.sql.Date(start.getTime()));
            pstmt.setDate(2, new java.sql.Date(end.getTime()));
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractReservationFromResultSet(rs));
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
    public List<Reservation> selectByStatus(String status) {
        String sql = "SELECT * FROM reservations WHERE status = ? ORDER BY create_time DESC";
        List<Reservation> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractReservationFromResultSet(rs));
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
    public int updateStatus(int reservationId, String status) {
        String sql = "UPDATE reservations SET status = ? WHERE reservation_id = ?";
        Connection conn;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setInt(2, reservationId);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (pstmt != null) {
                DBUtil.closeStatement(pstmt);
            }
        }
    }

    @Override
    public int checkIn(int reservationId) {
        return updateStatus(reservationId, "CHECKED_IN");
    }

    @Override
    public int checkOut(int reservationId) {
        return updateStatus(reservationId, "COMPLETED");
    }

    // ==================== 分页+复合查询 ====================

    @Override
    public List<Reservation> searchReservations(String keyword, String status,
                                                Date start, Date end,
                                                int pageNum, int pageSize) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT r.*, rm.room_number, h.name as homestay_name, u.username as guest_username ");
        sql.append("FROM reservations r ");
        sql.append("JOIN rooms rm ON r.room_id = rm.room_id ");
        sql.append("JOIN homestays h ON rm.homestay_id = h.homestay_id ");
        sql.append("JOIN users u ON r.guest_id = u.user_id ");
        sql.append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (r.reservation_no LIKE ? OR r.guest_name LIKE ? OR r.guest_phone LIKE ?) ");
            String pattern = "%" + keyword + "%";
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
        }

        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND r.status = ? ");
            params.add(status);
        }

        if (start != null) {
            sql.append("AND r.check_in_date >= ? ");
            params.add(new java.sql.Date(start.getTime()));
        }
        if (end != null) {
            sql.append("AND r.check_out_date <= ? ");
            params.add(new java.sql.Date(end.getTime()));
        }

        sql.append("ORDER BY r.reservation_id ASC LIMIT ?, ?");

        params.add((pageNum - 1) * pageSize);
        params.add(pageSize);

        List<Reservation> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql.toString());

            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    pstmt.setString(i + 1, (String) param);
                } else if (param instanceof Integer) {
                    pstmt.setInt(i + 1, (Integer) param);
                } else if (param instanceof java.sql.Date) {
                    pstmt.setDate(i + 1, (java.sql.Date) param);
                }
            }

            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractReservationFromResultSet(rs));
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
    public long countSearch(String keyword, String status, Date start, Date end) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM reservations r ");
        sql.append("JOIN rooms rm ON r.room_id = rm.room_id ");
        sql.append("JOIN homestays h ON rm.homestay_id = h.homestay_id ");
        sql.append("JOIN users u ON r.guest_id = u.user_id ");
        sql.append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (r.reservation_no LIKE ? OR r.guest_name LIKE ? OR r.guest_phone LIKE ?) ");
            String pattern = "%" + keyword + "%";
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
        }

        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND r.status = ? ");
            params.add(status);
        }

        if (start != null) {
            sql.append("AND r.check_in_date >= ? ");
            params.add(new java.sql.Date(start.getTime()));
        }
        if (end != null) {
            sql.append("AND r.check_out_date <= ? ");
            params.add(new java.sql.Date(end.getTime()));
        }

        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql.toString());

            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    pstmt.setString(i + 1, (String) param);
                } else if (param instanceof java.sql.Date) {
                    pstmt.setDate(i + 1, (java.sql.Date) param);
                }
            }

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
            if (pstmt != null) {
                DBUtil.closeStatement(pstmt);
            }
        }
    }

    // ==================== 工具方法 ====================

    private String generateReservationNo() {
        return String.format("%tY%<tm%<td%06d",
                new java.util.Date(),
                (int) (Math.random() * 1000000));
    }

    private Reservation extractReservationFromResultSet(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setReservationId(rs.getInt("reservation_id"));
        reservation.setReservationNo(rs.getString("reservation_no"));
        reservation.setRoomId(rs.getInt("room_id"));
        reservation.setGuestId(rs.getInt("guest_id"));
        reservation.setCheckInDate(rs.getDate("check_in_date"));
        reservation.setCheckOutDate(rs.getDate("check_out_date"));
        reservation.setGuestsCount(rs.getInt("guests_count"));
        reservation.setTotalPrice(rs.getDouble("total_price"));
        reservation.setStatus(rs.getString("status"));
        reservation.setGuestName(rs.getString("guest_name"));
        reservation.setGuestPhone(rs.getString("guest_phone"));
        reservation.setSpecialRequests(rs.getString("special_requests"));
        reservation.setCreateTime(rs.getTimestamp("create_time"));
        reservation.setUpdateTime(rs.getTimestamp("update_time"));
        return reservation;
    }
}