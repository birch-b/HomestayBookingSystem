package com.booking.dao;

import com.booking.model.Review;
import java.util.List;

/**
 * 评价数据访问接口
 */
public interface ReviewDAO extends BaseDAO<Review> {

    /**
     * 根据预订ID查询评价（一对一关系）
     */
    Review selectByReservationId(int reservationId);

    /**
     * 根据民宿ID查询所有评价
     */
    List<Review> selectByHomestayId(int homestayId);

    /**
     * 根据客人ID查询评价
     */
    List<Review> selectByGuestId(int guestId);

    /**
     * 根据评分范围查询
     */
    List<Review> selectByRatingRange(int minRating, int maxRating);

    /**
     * 查询某个民宿的平均评分
     */
    double getAverageRatingByHomestay(int homestayId);

    /**
     * 查询某个民宿的评分分布
     * 返回：5星几个、4星几个...
     */
    int[] getRatingDistribution(int homestayId);

    /**
     * 房东回复评价
     */
    int replyReview(int reviewId, String reply);

    /**
     * 查询最新评价（分页）
     */
    List<Review> selectLatestReviews(int pageNum, int pageSize);

    /**
     * 查询待回复的评价（房东还没回复的）
     */
    List<Review> selectPendingReplies(int homestayId);
}