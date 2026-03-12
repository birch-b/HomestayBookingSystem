package com.booking.dao;

import com.booking.model.CheckinRecord;
import java.util.List;

/**
 * 入住记录数据访问接口
 */
public interface CheckinRecordDAO extends BaseDAO<CheckinRecord> {

    /**
     * 根据预订ID查询入住记录（一对一关系）
     */
    CheckinRecord selectByReservationId(int reservationId);

    /**
     * 根据客人ID查询入住记录
     */
    List<CheckinRecord> selectByGuestId(int guestId);

    /**
     * 根据民宿ID查询入住记录
     */
    List<CheckinRecord> selectByHomestayId(int homestayId);

    /**
     * 查询今日入住
     */
    List<CheckinRecord> selectTodayCheckIn();

    /**
     * 查询今日退房
     */
    List<CheckinRecord> selectTodayCheckOut();

    /**
     * 更新退房信息（退押金等）
     */
    int updateCheckOut(int recordId, double depositReturn);
}