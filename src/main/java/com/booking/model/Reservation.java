package com.booking.model;

import java.util.Date;

/**
 * 预订订单实体类
 * 对应数据库表：reservations
 */
public class Reservation {

    private int reservationId;           // 预订ID
    private String reservationNo;        // 订单号
    private int roomId;                  // 房间ID
    private int guestId;                 // 客人ID
    private Date checkInDate;            // 入住日期
    private Date checkOutDate;           // 离店日期
    private int guestsCount;              // 入住人数
    private double totalPrice;            // 总价
    private String status;                // 订单状态
    private String guestName;             // 入住人姓名
    private String guestPhone;            // 入住人电话
    private String specialRequests;       // 特殊要求
    private Date createTime;               // 创建时间
    private Date updateTime;               // 更新时间

    // ============== 新增：关联对象，不是数据库字段 ==============
    private Room room;                    // 房间信息（用于显示）
    private Homestay homestay;             // 民宿信息（用于显示）
    private User guest;                    // 客人信息（用于显示）

    // 无参构造
    public Reservation() {
    }

    // ==================== Getter和Setter ====================

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public String getReservationNo() {
        return reservationNo;
    }

    public void setReservationNo(String reservationNo) {
        this.reservationNo = reservationNo;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public Date getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
    }

    public Date getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(Date checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public int getGuestsCount() {
        return guestsCount;
    }

    public void setGuestsCount(int guestsCount) {
        this.guestsCount = guestsCount;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestPhone() {
        return guestPhone;
    }

    public void setGuestPhone(String guestPhone) {
        this.guestPhone = guestPhone;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    // ============== 新增的Getter/Setter ==============

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

    public User getGuest() {
        return guest;
    }

    public void setGuest(User guest) {
        this.guest = guest;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "reservationId=" + reservationId +
                ", reservationNo='" + reservationNo + '\'' +
                ", guestName='" + guestName + '\'' +
                ", status='" + status + '\'' +
                ", totalPrice=" + totalPrice +
                '}';
    }
}