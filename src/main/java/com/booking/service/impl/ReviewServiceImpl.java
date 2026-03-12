package com.booking.service.impl;

import com.booking.dao.ReviewDAO;
import com.booking.dao.HomestayDAO;
import com.booking.dao.ReservationDAO;
import com.booking.dao.RoomDAO;
import com.booking.dao.impl.ReviewDAOImpl;
import com.booking.dao.impl.HomestayDAOImpl;
import com.booking.dao.impl.ReservationDAOImpl;
import com.booking.dao.impl.RoomDAOImpl;
import com.booking.model.Review;
import com.booking.model.Reservation;
import com.booking.model.Room;
import com.booking.service.ReviewService;

import java.util.ArrayList;
import java.util.List;

/**
 * 评价业务逻辑实现类
 */
public class ReviewServiceImpl implements ReviewService {

    private ReviewDAO reviewDAO;
    private HomestayDAO homestayDAO;
    private ReservationDAO reservationDAO;
    private RoomDAO roomDAO;  // 新增

    // 无参构造
    public ReviewServiceImpl() {
        this.reviewDAO = new ReviewDAOImpl();
        this.homestayDAO = new HomestayDAOImpl();
        this.reservationDAO = new ReservationDAOImpl();
        this.roomDAO = new RoomDAOImpl();
    }

    // 带参构造（用于测试）
    public ReviewServiceImpl(ReviewDAO reviewDAO, HomestayDAO homestayDAO, ReservationDAO reservationDAO, RoomDAO roomDAO) {
        this.reviewDAO = reviewDAO;
        this.homestayDAO = homestayDAO;
        this.reservationDAO = reservationDAO;
        this.roomDAO = new RoomDAOImpl();
    }

    @Override
    public int addReview(Review review) {
        Review existing=reviewDAO.selectByReservationId(review.getReservationId());
        if (existing != null) {
            return -1;  // 已评价过
        }

        // 2. 检查订单状态（必须是已完成才能评价）
        Reservation reservation=reservationDAO.selectById(review.getReservationId());
        if (reservation == null || !"COMPLETED".equals(reservation.getStatus())) {
            return 0;  // 订单不存在或未完成
        }
        // 3. 设置默认值
        review.setStatus(1);  // 默认显示
        if (review.getHostReply() == null) {
            review.setHostReply(null);
        }

        // 4. 插入评价
        int result = reviewDAO.insert(review);
        if (result > 0) {
            // 5. 更新民宿评分
            updateHomestayRating(reservation.getRoomId());
        }
        return result > 0 ? 1 : 0;
    }
    /**
     * 房东回复评价
     */
    @Override
    public boolean replyReview(int reviewId, String reply) {
        int result = reviewDAO.replyReview(reviewId, reply);
        return result > 0;
    }

    @Override
    public boolean deleteReview(int reviewId) {
        // 软删除：将状态设为0
        Review review = reviewDAO.selectById(reviewId);
        if (review == null) {
            return false;
        }
        review.setStatus(0);
        int result = reviewDAO.update(review);

        if (result > 0) {
            // 更新民宿评分
            Reservation reservation = reservationDAO.selectById(review.getReservationId());
            if (reservation != null) {
                updateHomestayRating(reservation.getRoomId());
            }
        }
        return result > 0;
    }

    @Override
    public Review getReviewById(int reviewId) {
        return reviewDAO.selectById(reviewId);
    }

    @Override
    public Review getReviewByReservationId(int reservationId) {
        return reviewDAO.selectByReservationId(reservationId);
    }
    /**
     * 根据民宿ID查询所有评价
     */
    @Override
    public List<Review> getReviewsByHomestayId(int homestayId, int pageNum, int pageSize) {
        List<Review> allReviews = reviewDAO.selectByHomestayId(homestayId);

        // 只显示状态为1（显示）的评价
        List<Review> visibleReviews = new ArrayList<>();
        for (Review r : allReviews) {
            if (r.getStatus() == 1) {
                visibleReviews.add(r);
            }
        }

        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, visibleReviews.size());

        if (start >= visibleReviews.size()) {
            return new ArrayList<>();
        }

        return visibleReviews.subList(start, end);
    }
    /**
     * 根据客人ID查询评价
     */
    @Override
    public List<Review> getReviewsByGuestId(int guestId, int pageNum, int pageSize) {
        List<Review> allReviews = reviewDAO.selectByGuestId(guestId);

        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, allReviews.size());

        if (start >= allReviews.size()) {
            return new ArrayList<>();
        }

        return allReviews.subList(start, end);
    }
    /**
     * 查询最新评价（分页）
     */
    @Override
    public List<Review> getLatestReviews(int limit) {
        return reviewDAO.selectLatestReviews(1, limit);
    }

    @Override
    public List<Review> getHighRatingReviews(int homestayId, int pageNum, int pageSize) {
        List<Review> allReviews = reviewDAO.selectByHomestayId(homestayId);

        // 筛选评分>=4且状态为1的评价
        List<Review> highRating = new ArrayList<>();
        for (Review r : allReviews) {
            if (r.getRating() >= 4 && r.getStatus() == 1) {
                highRating.add(r);
            }
        }

        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, highRating.size());

        if (start >= highRating.size()) {
            return new ArrayList<>();
        }

        return highRating.subList(start, end);
    }
    /**
     * 查询待回复的评价（房东还没回复的）
     */
    @Override
    public List<Review> getPendingReplies(int homestayId) {
        return reviewDAO.selectPendingReplies(homestayId);
    }

    @Override
    public long getReviewCountByHomestay(int homestayId) {
        List<Review> reviews = reviewDAO.selectByHomestayId(homestayId);
        long count = 0;
        for (Review r : reviews) {
            if (r.getStatus() == 1) {
                count++;
            }
        }
        return count;
    }
    @Override
    public double getAverageRatingByHomestay(int homestayId) {
        return reviewDAO.getAverageRatingByHomestay(homestayId);
    }

    @Override
    public int[] getRatingDistribution(int homestayId) {
        return reviewDAO.getRatingDistribution(homestayId);
    }

    @Override
    public boolean hasReviewed(int reservationId) {
        return reviewDAO.selectByReservationId(reservationId) != null;
    }

    @Override
    public Review getLatestReviewByGuest(int guestId) {
        List<Review> reviews = reviewDAO.selectByGuestId(guestId);
        if (reviews.isEmpty()) {
            return null;
        }
        // 按时间排序，取最新的
        reviews.sort((r1, r2) -> r2.getCreateTime().compareTo(r1.getCreateTime()));
        return reviews.get(0);
    }
    // ==================== 私有方法 ====================

    /**
     * 更新民宿评分
     */
    private void updateHomestayRating(int roomId) {
        Room room=roomDAO.selectById(roomId);
        if (room == null) {
            return;  // 房间不存在
        }
        int homestayId=room.getHomestayId();
        // 2. 获取该民宿所有评价
        List<Review> reviews = reviewDAO.selectByHomestayId(homestayId);
        // 3. 计算平均分（只计算状态为1的显示评价）
        double sum = 0;
        int validCount = 0;
        for (Review r : reviews) {
            if (r.getStatus() == 1) {
                sum += r.getRating();
                validCount++;
            }
        }

        double newRating = validCount > 0 ? sum / validCount : 0.0;

        // 4. 更新民宿评分
        homestayDAO.updateRating(homestayId, newRating);

        System.out.println("✅ 民宿 " + homestayId + " 评分已更新为: " + newRating);
    }
}
