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

    // ============== 新增关联字段 ==============
    private Reservation reservation;    // 关联的订单信息
    private User guest;                 // 关联的客人信息
    private Room room;                   // 关联的房间信息
    private Homestay homestay;           // 关联的民宿信息

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

    // ============== 新增关联字段的Getter/Setter ==============

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public User getGuest() {
        return guest;
    }

    public void setGuest(User guest) {
        this.guest = guest;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Homestay getHomestay() {
        return homestay;
    }

    public void setHomestay(Homestay homestay) {
        this.homestay = homestay;
    }

    @Override
    public String toString() {
        return "CheckinRecord{" +
                "recordId=" + recordId +
                ", reservationId=" + reservationId +
                ", deposit=" + deposit +
                ", depositReturn=" + depositReturn +
                '}';
    }
}