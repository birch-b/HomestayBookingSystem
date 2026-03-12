package com.booking.service;

import com.booking.model.CheckinRecord;
import java.util.Date;
import java.util.List;

/**
 * 入住记录业务逻辑接口
 */
public interface CheckinRecordService {

    /**
     * 办理入住
     * @return 1成功，-1订单状态错误，0失败
     */
    int checkIn(int reservationId, double deposit, int roomKeys, String remarks);

    /**
     * 办理退房
     * @return true成功，false失败
     */
    boolean checkOut(int recordId, double depositReturn);

    /**
     * 根据ID查询入住记录
     */
    CheckinRecord getRecordById(int recordId);

    /**
     * 根据预订ID查询入住记录
     */
    CheckinRecord getRecordByReservationId(int reservationId);

    /**
     * 查询用户的所有入住记录
     */
    List<CheckinRecord> getRecordsByGuestId(int guestId, int pageNum, int pageSize);

    /**
     * 查询民宿的所有入住记录
     */
    List<CheckinRecord> getRecordsByHomestayId(int homestayId, int pageNum, int pageSize);

    /**
     * 查询今日入住
     */
    List<CheckinRecord> getTodayCheckIn();

    /**
     * 查询今日退房
     */
    List<CheckinRecord> getTodayCheckOut();

    /**
     * 查询日期范围内的入住记录
     */
    List<CheckinRecord> getRecordsByDateRange(Date start, Date end, int pageNum, int pageSize);

    /**
     * 统计今日入住人数
     */
    int getTodayCheckInCount();

    /**
     * 统计今日退房人数
     */
    int getTodayCheckOutCount();

    /**
     * 统计当前在住人数
     */
    int getCurrentOccupancy();

    /**
     * 统计民宿的入住率
     */
    double getOccupancyRate(int homestayId, Date date);

    /**
     * 获取入住记录详情（包含订单、房间、客人信息）
     */
    CheckinRecord getRecordDetail(int recordId);
}