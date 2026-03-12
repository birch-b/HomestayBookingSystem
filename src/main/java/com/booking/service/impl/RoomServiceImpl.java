package com.booking.service.impl;

import com.booking.dao.RoomDAO;
import com.booking.dao.ReservationDAO;
import com.booking.dao.impl.RoomDAOImpl;
import com.booking.dao.impl.ReservationDAOImpl;
import com.booking.model.Room;
import com.booking.service.RoomService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 房间业务逻辑实现类
 */
public class RoomServiceImpl implements RoomService {

    private RoomDAO roomDAO;
    private ReservationDAO reservationDAO;

    // 无参构造
    public RoomServiceImpl() {
        this.roomDAO = new RoomDAOImpl();
        this.reservationDAO = new ReservationDAOImpl();
    }

    // 带参构造（用于测试）
    public RoomServiceImpl(RoomDAO roomDAO, ReservationDAO reservationDAO) {
        this.roomDAO = roomDAO;
        this.reservationDAO = reservationDAO;
    }

    @Override
    public int addRoom(Room room) {
        // 设置默认状态
        if (room.getStatus() == null || room.getStatus().isEmpty()) {
            room.setStatus("AVAILABLE");
        }
        int result = roomDAO.insert(room);
        return result > 0 ? 1 : 0;
    }

    @Override
    public boolean updateRoom(Room room) {
        int result = roomDAO.update(room);
        return result > 0;
    }

    @Override
    public boolean deleteRoom(int roomId) {
        int result = roomDAO.deleteById(roomId);
        return result > 0;
    }

    @Override
    public Room getRoomById(int roomId) {
        return roomDAO.selectById(roomId);
    }

    @Override
    public List<Room> getRoomsByHomestayId(int homestayId) {
        return roomDAO.selectByHomestayId(homestayId);
    }

    @Override
    public List<Room> getAllRooms(int pageNum, int pageSize) {
        return roomDAO.selectByPage(pageNum, pageSize);
    }

    @Override
    public List<Room> getAvailableRooms() {
        return roomDAO.selectAvailable();
    }

    @Override
    public List<Room> getRoomsByPriceRange(double minPrice, double maxPrice, int pageNum, int pageSize) {
        List<Room> allByPrice = roomDAO.selectByPriceRange(minPrice, maxPrice);

        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, allByPrice.size());

        if (start >= allByPrice.size()) {
            return new ArrayList<>();
        }

        return allByPrice.subList(start, end);
    }

    @Override
    public List<Room> getRoomsByType(String roomType, int pageNum, int pageSize) {
        List<Room> allByType = roomDAO.selectByType(roomType);

        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, allByType.size());

        if (start >= allByType.size()) {
            return new ArrayList<>();
        }

        return allByType.subList(start, end);
    }

    @Override
    public List<Room> getRoomsByPeopleCount(int peopleCount, int pageNum, int pageSize) {
        List<Room> allByPeople = roomDAO.selectByPeopleCount(peopleCount);

        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, allByPeople.size());

        if (start >= allByPeople.size()) {
            return new ArrayList<>();
        }

        return allByPeople.subList(start, end);
    }

    @Override
    public boolean updateRoomStatus(int roomId, String status) {
        int result = roomDAO.updateStatus(roomId, status);
        return result > 0;
    }

    @Override
    public List<Room> searchAvailableRooms(String city, Date checkIn, Date checkOut,
                                           int peopleCount, int pageNum, int pageSize) {
        // 先获取所有可能符合条件的房间（不考虑日期）
        List<Room> allCandidates = roomDAO.selectByPeopleCount(peopleCount);
        List<Room> availableRooms = new ArrayList<>();

        // 逐个检查日期冲突
        for (Room room : allCandidates) {
            boolean available = reservationDAO.checkRoomAvailable(
                    room.getRoomId(), checkIn, checkOut
            );
            if (available) {
                availableRooms.add(room);
            }
        }

        // 按城市筛选
        List<Room> result = new ArrayList<>();
        for (Room room : availableRooms) {
            // 需要获取房间所属民宿的城市
            // 这里简化处理，实际应该关联查询
            result.add(room);
        }

        // 分页
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, result.size());

        if (start >= result.size()) {
            return new ArrayList<>();
        }

        return result.subList(start, end);
    }

    @Override
    public long getTotalCount() {
        return roomDAO.count();
    }

    @Override
    public int[] getStatusStatistics(int homestayId) {
        int[] stats = new int[3]; // 0:AVAILABLE, 1:BOOKED, 2:MAINTENANCE
        List<Room> rooms = roomDAO.selectByHomestayId(homestayId);

        for (Room room : rooms) {
            switch (room.getStatus()) {
                case "AVAILABLE":
                    stats[0]++;
                    break;
                case "BOOKED":
                    stats[1]++;
                    break;
                case "MAINTENANCE":
                    stats[2]++;
                    break;
            }
        }
        return stats;
    }

    @Override
    public List<Object[]> getTypeStatistics(int homestayId) {
        List<Object[]> stats = new ArrayList<>();
        List<Room> rooms = roomDAO.selectByHomestayId(homestayId);

        // 按房型分组统计
        java.util.Map<String, Integer> typeCount = new java.util.HashMap<>();
        java.util.Map<String, Double> typePrice = new java.util.HashMap<>();

        for (Room room : rooms) {
            String type = room.getRoomType();
            typeCount.put(type, typeCount.getOrDefault(type, 0) + 1);
            typePrice.put(type, typePrice.getOrDefault(type, 0.0) + room.getPrice());
        }

        // 计算平均价格
        for (String type : typeCount.keySet()) {
            Object[] row = new Object[3];
            row[0] = type;
            row[1] = typeCount.get(type);
            row[2] = typePrice.get(type) / typeCount.get(type);
            stats.add(row);
        }

        return stats;
    }
}