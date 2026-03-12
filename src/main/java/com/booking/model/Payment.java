package com.booking.model;

import java.util.Date;

/**
 * 支付记录实体类
 * 对应数据库表：payments
 */
public class Payment {

    private int paymentId;             // 支付ID
    private String paymentNo;          // 支付单号
    private int reservationId;         // 预订ID
    private double amount;             // 支付金额
    private String paymentMethod;      // 支付方式：CASH,WECHAT,ALIPAY,BANK_CARD
    private String status;             // 支付状态：PENDING,SUCCESS,FAILED,REFUNDED
    private String transactionId;      // 第三方支付流水号
    private Date payTime;              // 支付时间
    private Date createTime;           // 创建时间

    // 无参构造
    public Payment() {
    }

    // Getter和Setter
    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentNo() {
        return paymentNo;
    }

    public void setPaymentNo(String paymentNo) {
        this.paymentNo = paymentNo;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}