package com.booking.dao;

import com.booking.model.Room;
import java.util.List;

/**
 * 房间数据访问接口
 */
public interface RoomDAO extends BaseDAO<Room> {

    /**
     * 根据民宿ID查询房间
     */
    List<Room> selectByHomestayId(int homestayId);

    /**
     * 查询可用房间
     */
    List<Room> selectAvailable();

    /**
     * 根据价格范围查询
     */
    List<Room> selectByPriceRange(double minPrice, double maxPrice);

    /**
     * 根据房型查询
     */
    List<Room> selectByType(String roomType);

    /**
     * 根据人数查询（房间可容纳人数 >= 指定人数）
     */
    List<Room> selectByPeopleCount(int peopleCount);

    /**
     * 更新房间状态
     */
    int updateStatus(int roomId, String status);

    /**
     * 复合查询：根据城市、日期、人数查询可用房间（核心业务）
     */
    List<Room> searchAvailableRooms(String city, int peopleCount);
}