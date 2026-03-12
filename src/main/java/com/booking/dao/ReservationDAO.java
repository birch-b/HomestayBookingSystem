package com.booking.dao;

import com.booking.model.Reservation;
import java.util.Date;
import java.util.List;

/**
 * 预订数据访问接口
 * 核心业务：房间预订、冲突检测、状态管理
 */
public interface ReservationDAO extends BaseDAO<Reservation> {

    // ==================== 核心业务方法 ====================

    /**
     * 创建预订（需要事务处理）
     * 涉及：插入订单、更新房间状态
     */
    int createReservation(Reservation reservation);

    /**
     * 检查房间在指定时间段是否可用
     * @return true=可用，false=已被预订
     */
    boolean checkRoomAvailable(int roomId, Date checkIn, Date checkOut);

    /**
     * 取消订单（需要事务处理）
     * 涉及：更新订单状态、释放房间
     */
    int cancelReservation(int reservationId);

    // ==================== 查询方法 ====================

    /**
     * 根据客人ID查询订单
     */
    List<Reservation> selectByGuestId(int guestId);

    /**
     * 根据房间ID查询订单
     */
    List<Reservation> selectByRoomId(int roomId);

    /**
     * 根据民宿ID查询订单（关联查询）
     */
    List<Reservation> selectByHomestayId(int homestayId);

    /**
     * 根据日期范围查询
     */
    List<Reservation> selectByDateRange(Date start, Date end);

    /**
     * 根据状态查询
     */
    List<Reservation> selectByStatus(String status);

    /**
     * 更新订单状态
     */
    int updateStatus(int reservationId, String status);

    /**
     * 确认入住
     */
    int checkIn(int reservationId);

    /**
     * 完成退房
     */
    int checkOut(int reservationId);

    // ==================== 分页+复合查询 ====================

    /**
     * 复合查询（分页+多条件）
     * @param keyword 关键词（订单号/客人姓名/电话）
     * @param status 订单状态
     * @param start 开始日期
     * @param end 结束日期
     * @param pageNum 页码
     * @param pageSize 每页条数
     */
    List<Reservation> searchReservations(String keyword, String status,
                                         Date start, Date end,
                                         int pageNum, int pageSize);

    /**
     * 统计复合查询结果总数
     */
    long countSearch(String keyword, String status, Date start, Date end);
}