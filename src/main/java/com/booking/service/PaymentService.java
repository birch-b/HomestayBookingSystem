package com.booking.service;

import com.booking.model.Payment;
import java.util.Date;
import java.util.List;

/**
 * 支付业务逻辑接口
 */
public interface PaymentService {

    /**
     * 创建支付记录
     * @return 1成功，0失败
     */
    int createPayment(Payment payment);

    /**
     * 支付成功回调
     * @param paymentId 支付ID
     * @param transactionId 第三方支付流水号
     * @return true成功，false失败
     */
    boolean paymentSuccess(int paymentId, String transactionId);

    /**
     * 支付失败
     */
    boolean paymentFail(int paymentId);

    /**
     * 退款
     */
    boolean refund(int paymentId, String refundReason);

    /**
     * 根据ID查询支付记录
     */
    Payment getPaymentById(int paymentId);

    /**
     * 根据支付单号查询
     */
    Payment getPaymentByNo(String paymentNo);

    /**
     * 查询订单的所有支付记录
     */
    List<Payment> getPaymentsByReservationId(int reservationId);

    /**
     * 查询订单已支付总额
     */
    double getTotalPaidByReservation(int reservationId);

    /**
     * 查询订单是否已支付成功
     */
    boolean isReservationPaid(int reservationId);

    /**
     * 根据状态查询支付记录（分页）
     */
    List<Payment> getPaymentsByStatus(String status, int pageNum, int pageSize);

    /**
     * 根据支付方式查询（分页）
     */
    List<Payment> getPaymentsByMethod(String method, int pageNum, int pageSize);

    /**
     * 查询时间范围内的支付记录（分页）
     */
    List<Payment> getPaymentsByDateRange(Date start, Date end, int pageNum, int pageSize);

    /**
     * 查询用户的所有支付记录
     */
    List<Payment> getPaymentsByUserId(int userId, int pageNum, int pageSize);

    /**
     * 查询民宿的所有支付记录
     */
    List<Payment> getPaymentsByHomestayId(int homestayId, int pageNum, int pageSize);

    /**
     * 统计今日收入
     */
    double getTodayIncome();

    /**
     * 统计本月收入
     */
    double getMonthIncome();

    /**
     * 统计指定民宿的月度收入
     */
    List<Object[]> getMonthlyIncomeByHomestay(int homestayId, int year);

    /**
     * 统计所有民宿的月度收入
     */
    List<Object[]> getMonthlyIncomeAll(int year);

    /**
     * 统计支付方式占比
     */
    List<Object[]> getPaymentMethodStatistics(Date start, Date end);

    /**
     * 统计总支付金额
     */
    double getTotalAmount();

    /**
     * 统计成功支付数量
     */
    long getSuccessCount();
}