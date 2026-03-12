package com.booking.service;

import com.booking.model.Reservation;
import com.booking.model.Payment;
import java.util.Date;
import java.util.List;

/**
 * 预订业务逻辑接口
 */
public interface ReservationService {

    /**
     * 创建预订（核心业务，包含事务）
     * @return 1成功，-1房间不可用，0失败
     */
    int createReservation(Reservation reservation, Payment payment);

    /**
     * 取消预订
     */
    boolean cancelReservation(int reservationId);

    /**
     * 支付成功回调
     */
    boolean paymentSuccess(int reservationId, String transactionId);

    /**
     * 办理入住
     */
    boolean checkIn(int reservationId, double deposit);

    /**
     * 办理退房
     */
    boolean checkOut(int reservationId, double depositReturn);

    /**
     * 根据ID查询预订（包含关联信息）
     */
    Reservation getReservationDetail(int reservationId);

    /**
     * 查询用户的预订列表
     */
    List<Reservation> getUserReservations(int userId, int pageNum, int pageSize);

    /**
     * 查询民宿的预订列表
     */
    List<Reservation> getHomestayReservations(int homestayId, int pageNum, int pageSize);

    /**
     * 搜索预订（多条件分页）
     */
    List<Reservation> searchReservations(String keyword, String status,
                                         Date start, Date end,
                                         int pageNum, int pageSize);

    /**
     * 计算订单总价（考虑定价规则）
     */
    double calculateTotalPrice(int roomId, Date checkIn, Date checkOut, int guestsCount);
}