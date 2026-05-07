package com.booking.dao.impl;

import com.booking.dao.CheckinRecordDAO;
import com.booking.model.CheckinRecord;
import com.booking.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 入住记录数据访问实现类
 */
public class CheckinRecordDAOImpl implements CheckinRecordDAO {

    // ==================== 基础CRUD ====================

    @Override
    public int insert(CheckinRecord record) {
        String sql = "INSERT INTO checkin_records (reservation_id, actual_check_in, actual_check_out, " +
                "deposit, deposit_return, room_keys_given, remarks) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setInt(1, record.getReservationId());
            pstmt.setTimestamp(2, record.getActualCheckIn() != null ?
                    new Timestamp(record.getActualCheckIn().getTime()) : null);
            pstmt.setTimestamp(3, record.getActualCheckOut() != null ?
                    new Timestamp(record.getActualCheckOut().getTime()) : null);
            pstmt.setDouble(4, record.getDeposit());
            pstmt.setDouble(5, record.getDepositReturn());
            pstmt.setInt(6, record.getRoomKeysGiven());
            pstmt.setString(7, record.getRemarks());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    record.setRecordId(rs.getInt(1));
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
        String sql = "DELETE FROM checkin_records WHERE record_id = ?";
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
    public int update(CheckinRecord record) {
        String sql = "UPDATE checkin_records SET reservation_id=?, actual_check_in=?, actual_check_out=?, " +
                "deposit=?, deposit_return=?, room_keys_given=?, remarks=? WHERE record_id=?";
        Connection conn;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, record.getReservationId());
            pstmt.setTimestamp(2, record.getActualCheckIn() != null ?
                    new Timestamp(record.getActualCheckIn().getTime()) : null);
            pstmt.setTimestamp(3, record.getActualCheckOut() != null ?
                    new Timestamp(record.getActualCheckOut().getTime()) : null);
            pstmt.setDouble(4, record.getDeposit());
            pstmt.setDouble(5, record.getDepositReturn());
            pstmt.setInt(6, record.getRoomKeysGiven());
            pstmt.setString(7, record.getRemarks());
            pstmt.setInt(8, record.getRecordId());

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public CheckinRecord selectById(int id) {
        String sql = "SELECT * FROM checkin_records WHERE record_id = ?";
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractCheckinRecordFromResultSet(rs);
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
    public List<CheckinRecord> selectAll() {
        String sql = "SELECT * FROM checkin_records ORDER BY record_id DESC";
        List<CheckinRecord> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractCheckinRecordFromResultSet(rs));
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
    public List<CheckinRecord> selectByPage(int pageNum, int pageSize) {
        String sql = "SELECT * FROM checkin_records ORDER BY record_id DESC LIMIT ?, ?";
        List<CheckinRecord> list = new ArrayList<>();
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
                list.add(extractCheckinRecordFromResultSet(rs));
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
        String sql = "SELECT COUNT(*) FROM checkin_records";
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
    public CheckinRecord selectByReservationId(int reservationId) {
        String sql = "SELECT * FROM checkin_records WHERE reservation_id = ?";
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, reservationId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractCheckinRecordFromResultSet(rs);
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
    public List<CheckinRecord> selectByGuestId(int guestId) {
        String sql = "SELECT cr.* FROM checkin_records cr " +
                "JOIN reservations r ON cr.reservation_id = r.reservation_id " +
                "WHERE r.guest_id = ? ORDER BY cr.actual_check_in DESC";
        List<CheckinRecord> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, guestId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractCheckinRecordFromResultSet(rs));
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
    public List<CheckinRecord> selectByHomestayId(int homestayId) {
        String sql = "SELECT cr.* FROM checkin_records cr " +
                "JOIN reservations r ON cr.reservation_id = r.reservation_id " +
                "JOIN rooms rm ON r.room_id = rm.room_id " +
                "WHERE rm.homestay_id = ? ORDER BY cr.actual_check_in DESC";
        List<CheckinRecord> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, homestayId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractCheckinRecordFromResultSet(rs));
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
    public List<CheckinRecord> selectTodayCheckIn() {
        String sql = "SELECT * FROM checkin_records WHERE DATE(actual_check_in) = CURDATE()";
        List<CheckinRecord> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractCheckinRecordFromResultSet(rs));
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
    public List<CheckinRecord> selectTodayCheckOut() {
        String sql = "SELECT * FROM checkin_records WHERE DATE(actual_check_out) = CURDATE()";
        List<CheckinRecord> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractCheckinRecordFromResultSet(rs));
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
    public int updateCheckOut(int recordId, double depositReturn) {
        String sql = "UPDATE checkin_records SET actual_check_out = NOW(), deposit_return = ? " +
                "WHERE record_id = ?";
        Connection conn;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setDouble(1, depositReturn);
            pstmt.setInt(2, recordId);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public List<CheckinRecord> selectCheckedInRecords() {
        // 查询所有已入住（actual_check_in不为空）但未退房（actual_check_out为空）的记录
        String sql = "SELECT * FROM checkin_records WHERE actual_check_in IS NOT NULL AND actual_check_out IS NULL ORDER BY actual_check_in DESC";
        List<CheckinRecord> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractCheckinRecordFromResultSet(rs));
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

    private CheckinRecord extractCheckinRecordFromResultSet(ResultSet rs) throws SQLException {
        CheckinRecord record = new CheckinRecord();
        record.setRecordId(rs.getInt("record_id"));
        record.setReservationId(rs.getInt("reservation_id"));
        record.setActualCheckIn(rs.getTimestamp("actual_check_in"));
        record.setActualCheckOut(rs.getTimestamp("actual_check_out"));
        record.setDeposit(rs.getDouble("deposit"));
        record.setDepositReturn(rs.getDouble("deposit_return"));
        record.setRoomKeysGiven(rs.getInt("room_keys_given"));
        record.setRemarks(rs.getString("remarks"));
        record.setCreateTime(rs.getTimestamp("create_time"));
        return record;
    }
}