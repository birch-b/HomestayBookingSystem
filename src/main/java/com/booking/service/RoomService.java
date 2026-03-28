package com.booking.service;

import com.booking.model.Room;
import java.util.Date;
import java.util.List;

/**
 * 房间业务逻辑接口
 */
public interface RoomService {

    /**
     * 新增房间
     * @return 1成功，0失败
     */
    int addRoom(Room room);

    /**
     * 更新房间信息
     */
    boolean updateRoom(Room room);

    /**
     * 删除房间
     */
    boolean deleteRoom(int roomId);

    /**
     * 根据ID查询房间
     */
    Room getRoomById(int roomId);

    /**
     * 查询民宿的所有房间
     */
    List<Room> getRoomsByHomestayId(int homestayId);

    /**
     * 查询所有房间（分页）
     */
    List<Room> getAllRooms(int pageNum, int pageSize);

    /**
     * 查询可用房间
     */
    List<Room> getAvailableRooms();

    /**
     * 按价格范围查询
     */
    List<Room> getRoomsByPriceRange(double minPrice, double maxPrice, int pageNum, int pageSize);

    /**
     * 按房型查询
     */
    List<Room> getRoomsByType(String roomType, int pageNum, int pageSize);

    /**
     * 按人数查询（房间可容纳人数 >= 指定人数）
     */
    List<Room> getRoomsByPeopleCount(int peopleCount, int pageNum, int pageSize);

    /**
     * 更新房间状态
     */
    boolean updateRoomStatus(int roomId, String status);

    /**
     * 搜索可用房间（按城市、日期、人数）
     * 核心业务：用于游客预订
     */
    List<Room> searchAvailableRooms(String city, Date checkIn, Date checkOut,
                                    int peopleCount, int pageNum, int pageSize);

    /**
     * 根据民宿ID获取可用房间
     */
    List<Room> getAvailableRoomsByHomestayId(int homestayId);

    /**
     * 根据民宿ID搜索可用房间（按日期、人数）
     */
    List<Room> searchAvailableRoomsByHomestayId(int homestayId, Date checkIn, Date checkOut,
                                    int peopleCount, int pageNum, int pageSize);

    /**
     * 统计房间总数
     */
    long getTotalCount();

    /**
     * 统计各状态房间数量
     */
    int[] getStatusStatistics(int homestayId);

    /**
     * 统计各房型房间数量
     */
    List<Object[]> getTypeStatistics(int homestayId);
}