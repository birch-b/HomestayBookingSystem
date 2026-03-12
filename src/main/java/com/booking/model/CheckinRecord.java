package com.booking.model;

import java.util.Date;

/**
 * 入住记录实体类
 * 对应数据库表：checkin_records
 */
public class CheckinRecord {

    private int recordId;              // 记录ID
    private int reservationId;         // 预订ID
    private Date actualCheckIn;        // 实际入住时间
    private Date actualCheckOut;       // 实际退房时间
    private double deposit;            // 押金
    private double depositReturn;       // 退还押金
    private int roomKeysGiven;          // 房卡数量
    private String remarks;             // 备注
    private Date createTime;            // 创建时间

    // 无参构造
    public CheckinRecord() {
    }

    // Getter和Setter
    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public Date getActualCheckIn() {
        return actualCheckIn;
    }

    public void setActualCheckIn(Date actualCheckIn) {
        this.actualCheckIn = actualCheckIn;
    }

    public Date getActualCheckOut() {
        return actualCheckOut;
    }

    public void setActualCheckOut(Date actualCheckOut) {
        this.actualCheckOut = actualCheckOut;
    }

    public double getDeposit() {
        return deposit;
    }

    public void setDeposit(double deposit) {
        this.deposit = deposit;
    }

    public double getDepositReturn() {
        return depositReturn;
    }

    public void setDepositReturn(double depositReturn) {
        this.depositReturn = depositReturn;
    }

    public int getRoomKeysGiven() {
        return roomKeysGiven;
    }

    public void setRoomKeysGiven(int roomKeysGiven) {
        this.roomKeysGiven = roomKeysGiven;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}