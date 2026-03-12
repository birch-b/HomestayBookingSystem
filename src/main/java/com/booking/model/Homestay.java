package com.booking.model;

import java.util.Date;

/**
 * 民宿实体类
 * 对应数据库表：homestays
 */
public class Homestay {

    private int homestayId;        // 民宿ID
    private int hostId;            // 民宿主ID
    private String name;           // 民宿名称
    private String address;        // 地址
    private String city;           // 城市
    private String description;    // 介绍
    private String facilities;     // 设施列表
    private String phone;          // 联系电话
    private double rating;         // 综合评分
    private int reviewCount;       // 评价数量
    private int status;            // 状态：1营业 0暂停
    private Date createTime;       // 创建时间
    private Date updateTime;       // 更新时间

    // ==================== 无参构造 ====================

    public Homestay() {
    }

    // ==================== 全参构造 ====================

    public Homestay(int homestayId, int hostId, String name, String address, String city,
                    String description, String facilities, String phone, double rating,
                    int reviewCount, int status, Date createTime, Date updateTime) {
        this.homestayId = homestayId;
        this.hostId = hostId;
        this.name = name;
        this.address = address;
        this.city = city;
        this.description = description;
        this.facilities = facilities;
        this.phone = phone;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.status = status;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    // ==================== Getter和Setter ====================

    public int getHomestayId() {
        return homestayId;
    }

    public void setHomestayId(int homestayId) {
        this.homestayId = homestayId;
    }

    public int getHostId() {
        return hostId;
    }

    public void setHostId(int hostId) {
        this.hostId = hostId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFacilities() {
        return facilities;
    }

    public void setFacilities(String facilities) {
        this.facilities = facilities;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    // ==================== toString方法（方便调试） ====================

    @Override
    public String toString() {
        return "Homestay{" +
                "homestayId=" + homestayId +
                ", hostId=" + hostId +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", phone='" + phone + '\'' +
                ", rating=" + rating +
                ", status=" + status +
                '}';
    }
}