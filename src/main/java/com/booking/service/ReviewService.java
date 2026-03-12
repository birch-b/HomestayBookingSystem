package com.booking.service;

import com.booking.model.Review;
import java.util.List;

/**
 * 评价业务逻辑接口
 */
public interface ReviewService {

    /**
     * 发表评价
     * @return 1成功，-1已评价过，0失败
     */
    int addReview(Review review);

    /**
     * 房东回复评价
     */
    boolean replyReview(int reviewId, String reply);

    /**
     * 删除评价（软删除）
     */
    boolean deleteReview(int reviewId);

    /**
     * 根据ID查询评价
     */
    Review getReviewById(int reviewId);

    /**
     * 根据预订ID查询评价
     */
    Review getReviewByReservationId(int reservationId);

    /**
     * 查询民宿的所有评价（分页）
     */
    List<Review> getReviewsByHomestayId(int homestayId, int pageNum, int pageSize);

    /**
     * 查询用户的所有评价
     */
    List<Review> getReviewsByGuestId(int guestId, int pageNum, int pageSize);

    /**
     * 查询最新评价
     */
    List<Review> getLatestReviews(int limit);

    /**
     * 查询高分评价（评分>=4）
     */
    List<Review> getHighRatingReviews(int homestayId, int pageNum, int pageSize);

    /**
     * 查询待回复的评价
     */
    List<Review> getPendingReplies(int homestayId);

    /**
     * 统计民宿的评价总数
     */
    long getReviewCountByHomestay(int homestayId);

    /**
     * 统计民宿的平均评分
     */
    double getAverageRatingByHomestay(int homestayId);

    /**
     * 获取民宿的评分分布
     * @return 数组 [5星,4星,3星,2星,1星]
     */
    int[] getRatingDistribution(int homestayId);

    /**
     * 检查订单是否已评价
     */
    boolean hasReviewed(int reservationId);

    /**
     * 获取用户的最新评价
     */
    Review getLatestReviewByGuest(int guestId);
}