package com.booking.model;

import java.util.Date;

/**
 * 房间实体类
 * 对应数据库表：rooms
 */
public class Room {

    private int roomId;              // 房间ID
    private int homestayId;          // 所属民宿ID
    private String roomNumber;       // 房间号
    private String roomType;         // 房型：SINGLE,DOUBLE,TWIN,SUITE,FAMILY
    private String bedType;          // 床型
    private int area;                // 面积
    private int maxPeople;           // 最多入住人数
    private double price;            // 价格
    private String status;           // 状态：AVAILABLE,BOOKED,MAINTENANCE
    private String description;      // 房间描述
    private Date createTime;         // 创建时间

    // 无参构造
    public Room() {
    }

    // Getter和Setter
    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getHomestayId() {
        return homestayId;
    }

    public void setHomestayId(int homestayId) {
        this.homestayId = homestayId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getBedType() {
        return bedType;
    }

    public void setBedType(String bedType) {
        this.bedType = bedType;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public int getMaxPeople() {
        return maxPeople;
    }

    public void setMaxPeople(int maxPeople) {
        this.maxPeople = maxPeople;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}