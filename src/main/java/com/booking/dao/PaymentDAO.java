package com.booking.dao;

import com.booking.model.Payment;
import java.util.List;
import java.util.Date;

/**
 * 支付记录数据访问接口
 */
public interface PaymentDAO extends BaseDAO<Payment> {

    /**
     * 根据预订ID查询支付记录（一个订单可能有多次支付）
     */
    List<Payment> selectByReservationId(int reservationId);

    /**
     * 根据支付单号查询
     */
    Payment selectByPaymentNo(String paymentNo);

    /**
     * 根据支付状态查询
     */
    List<Payment> selectByStatus(String status);

    /**
     * 根据支付方式查询
     */
    List<Payment> selectByMethod(String method);

    /**
     * 查询某时间范围内的支付记录
     */
    List<Payment> selectByDateRange(Date start, Date end);

    /**
     * 统计某订单已支付总金额
     */
    double getTotalPaidByReservation(int reservationId);

    /**
     * 更新支付状态
     */
    int updateStatus(int paymentId, String status);

    /**
     * 支付成功回调（更新状态和支付时间）
     */
    int paymentSuccess(int paymentId, String transactionId);

    /**
     * 查询某民宿的收入统计（按月份）
     */
    List<Object[]> getMonthlyIncome(int homestayId, int year);
}