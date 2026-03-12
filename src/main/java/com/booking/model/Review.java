package com.booking.model;

import java.util.Date;

/**
 * 评价实体类
 * 对应数据库表：reviews
 */
public class Review {

    private int reviewId;              // 评价ID
    private int reservationId;         // 预订ID
    private int guestId;               // 客人ID
    private int rating;                // 评分（1-5）
    private String comment;            // 评价内容
    private String hostReply;          // 房东回复
    private Date createTime;           // 评价时间
    private Date replyTime;            // 回复时间
    private int status;                // 状态：1显示 0隐藏

    // 无参构造
    public Review() {
    }

    // Getter和Setter
    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getHostReply() {
        return hostReply;
    }

    public void setHostReply(String hostReply) {
        this.hostReply = hostReply;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(Date replyTime) {
        this.replyTime = replyTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}