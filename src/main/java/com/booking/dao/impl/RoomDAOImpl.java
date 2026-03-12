package com.booking.dao.impl;

import com.booking.dao.RoomDAO;
import com.booking.model.Room;
import com.booking.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 房间数据访问实现类
 */
public class RoomDAOImpl implements RoomDAO {

    // ==================== 基础CRUD ====================

    @Override
    public int insert(Room room) {
        String sql = "INSERT INTO rooms (homestay_id, room_number, room_type, bed_type, area, " +
                "max_people, price, status, description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setInt(1, room.getHomestayId());
            stmt.setString(2, room.getRoomNumber());
            stmt.setString(3, room.getRoomType());
            stmt.setString(4, room.getBedType());
            stmt.setInt(5, room.getArea());
            stmt.setInt(6, room.getMaxPeople());
            stmt.setDouble(7, room.getPrice());
            stmt.setString(8, room.getStatus());
            stmt.setString(9, room.getDescription());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    room.setRoomId(rs.getInt(1));
                }
            }
            return affectedRows;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.closeResultSet(rs);
            if (stmt != null) {
                DBUtil.closeStatement(stmt);
            }
        }
    }

    @Override
    public int deleteById(int id) {
        String sql = "DELETE FROM rooms WHERE room_id = ?";
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
            if (stmt != null) {
                DBUtil.closeStatement(stmt);
            }
        }
    }

    @Override
    public int update(Room room) {
        String sql = "UPDATE rooms SET homestay_id=?, room_number=?, room_type=?, bed_type=?, " +
                "area=?, max_people=?, price=?, status=?, description=? WHERE room_id=?";
        Connection conn;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, room.getHomestayId());
            pstmt.setString(2, room.getRoomNumber());
            pstmt.setString(3, room.getRoomType());
            pstmt.setString(4, room.getBedType());
            pstmt.setInt(5, room.getArea());
            pstmt.setInt(6, room.getMaxPeople());
            pstmt.setDouble(7, room.getPrice());
            pstmt.setString(8, room.getStatus());
            pstmt.setString(9, room.getDescription());
            pstmt.setInt(10, room.getRoomId());

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
    public Room selectById(int id) {
        String sql = "SELECT * FROM rooms WHERE room_id = ?";
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractRoomFromResultSet(rs);
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
    public List<Room> selectAll() {
        String sql = "SELECT * FROM rooms ORDER BY room_id";
        List<Room> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractRoomFromResultSet(rs));
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
    public List<Room> selectByPage(int pageNum, int pageSize) {
        String sql = "SELECT * FROM rooms ORDER BY room_id LIMIT ?, ?";
        List<Room> list = new ArrayList<>();
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
                list.add(extractRoomFromResultSet(rs));
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
        String sql = "SELECT COUNT(*) FROM rooms";
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

    // ==================== 自定义方法 ====================

    @Override
    public List<Room> selectByHomestayId(int homestayId) {
        String sql = "SELECT * FROM rooms WHERE homestay_id = ? ORDER BY room_number";
        List<Room> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, homestayId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractRoomFromResultSet(rs));
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
    public List<Room> selectAvailable() {
        String sql = "SELECT * FROM rooms WHERE status = 'AVAILABLE' ORDER BY price";
        List<Room> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractRoomFromResultSet(rs));
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
    public List<Room> selectByPriceRange(double minPrice, double maxPrice) {
        String sql = "SELECT * FROM rooms WHERE price BETWEEN ? AND ? ORDER BY price";
        List<Room> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setDouble(1, minPrice);
            pstmt.setDouble(2, maxPrice);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractRoomFromResultSet(rs));
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
    public List<Room> selectByType(String roomType) {
        String sql = "SELECT * FROM rooms WHERE room_type = ? ORDER BY price";
        List<Room> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, roomType);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractRoomFromResultSet(rs));
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
    public List<Room> selectByPeopleCount(int peopleCount) {
        String sql = "SELECT * FROM rooms WHERE max_people >= ? ORDER BY max_people, price";
        List<Room> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, peopleCount);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractRoomFromResultSet(rs));
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
    public int updateStatus(int roomId, String status) {
        String sql = "UPDATE rooms SET status = ? WHERE room_id = ?";
        Connection conn;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setInt(2, roomId);
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
    public List<Room> searchAvailableRooms(String city, int peopleCount) {
        String sql = "SELECT r.*, h.name as homestay_name, h.address, h.city " +
                "FROM rooms r " +
                "JOIN homestays h ON r.homestay_id = h.homestay_id " +
                "WHERE h.city LIKE ? " +
                "AND r.max_people >= ? " +
                "AND r.status = 'AVAILABLE' " +
                "ORDER BY r.price";
        List<Room> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + city + "%");
            pstmt.setInt(2, peopleCount);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Room room = extractRoomFromResultSet(rs);
                list.add(room);
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

    // ==================== 工具方法 ====================

    private Room extractRoomFromResultSet(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setRoomId(rs.getInt("room_id"));
        room.setHomestayId(rs.getInt("homestay_id"));
        room.setRoomNumber(rs.getString("room_number"));
        room.setRoomType(rs.getString("room_type"));
        room.setBedType(rs.getString("bed_type"));
        room.setArea(rs.getInt("area"));
        room.setMaxPeople(rs.getInt("max_people"));
        room.setPrice(rs.getDouble("price"));
        room.setStatus(rs.getString("status"));
        room.setDescription(rs.getString("description"));
        room.setCreateTime(rs.getTimestamp("create_time"));
        return room;
    }
}