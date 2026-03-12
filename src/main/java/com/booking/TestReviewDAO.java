package com.booking;

import com.booking.dao.ReviewDAO;
import com.booking.dao.impl.ReviewDAOImpl;
import com.booking.model.Review;

import java.util.List;
import java.util.Arrays;

public class TestReviewDAO {
    public static void main(String[] args) {
        System.out.println("========== 测试评价DAO ==========");

        ReviewDAO reviewDAO = new ReviewDAOImpl();

        // 1. 当前评价总数
        System.out.println("\n=== 1. 当前评价总数 ===");
        long count = reviewDAO.count();
        System.out.println("评价总数: " + count);

        // 2. 插入测试评价
        System.out.println("\n=== 2. 插入评价数据 ===");
        Review review = new Review();
        review.setReservationId(21);  // 订单ID=21
        review.setGuestId(4);          // 赵六
        review.setRating(5);
        review.setComment("非常满意！房间干净，海景很棒！");
        review.setStatus(1);

        int result = reviewDAO.insert(review);
        System.out.println("插入结果: " + (result > 0 ? "✅ 成功" : "❌ 失败"));
        if (result > 0) {
            System.out.println("评价ID: " + review.getReviewId());
        }

        // 3. 根据预订ID查询
        System.out.println("\n=== 3. 根据预订ID查询 ===");
        Review found = reviewDAO.selectByReservationId(21);
        if (found != null) {
            System.out.println("评价: " + found.getComment());
            System.out.println("评分: " + found.getRating() + "星");
        }

        // 4. 查询所有评价
        System.out.println("\n=== 4. 所有评价 ===");
        List<Review> allList = reviewDAO.selectAll();
        System.out.println("总数: " + allList.size());
        for (Review r : allList) {
            System.out.println(r.getReviewId() + " | 订单" + r.getReservationId() +
                    " | " + r.getRating() + "星 | " + r.getComment());
        }

        // 5. 测试房东回复
        System.out.println("\n=== 5. 房东回复 ===");
        if (found != null) {
            int replyResult = reviewDAO.replyReview(found.getReviewId(),
                    "感谢您的评价！欢迎下次再来！");
            System.out.println("回复结果: " + (replyResult > 0 ? "✅ 成功" : "❌ 失败"));

            // 验证回复
            Review withReply = reviewDAO.selectById(found.getReviewId());
            if (withReply != null) {
                System.out.println("房东回复: " + withReply.getHostReply());
                System.out.println("回复时间: " + withReply.getReplyTime());
            }
        }

        // 6. 根据民宿查询评价
        System.out.println("\n=== 6. 查询民宿1的评价 ===");
        List<Review> homestayList = reviewDAO.selectByHomestayId(1);
        System.out.println("找到 " + homestayList.size() + " 条");

        // 7. 计算平均分
        System.out.println("\n=== 7. 民宿1的平均评分 ===");
        double avgRating = reviewDAO.getAverageRatingByHomestay(1);
        System.out.println("平均分: " + avgRating);

        // 8. 评分分布
        System.out.println("\n=== 8. 民宿1的评分分布 ===");
        int[] distribution = reviewDAO.getRatingDistribution(1);
        System.out.println("5星: " + distribution[4] + " 条");
        System.out.println("4星: " + distribution[3] + " 条");
        System.out.println("3星: " + distribution[2] + " 条");
        System.out.println("2星: " + distribution[1] + " 条");
        System.out.println("1星: " + distribution[0] + " 条");

        // 9. 查询最新评价
        System.out.println("\n=== 9. 最新评价 ===");
        List<Review> latest = reviewDAO.selectLatestReviews(1, 3);
        for (Review r : latest) {
            System.out.println(r.getReviewId() + " | " + r.getRating() + "星 | " + r.getComment());
        }

        // 10. 查询待回复评价
        System.out.println("\n=== 10. 待回复评价 ===");
        List<Review> pending = reviewDAO.selectPendingReplies(1);
        System.out.println("待回复: " + pending.size() + " 条");

        System.out.println("\n========== 测试完成 ==========");
    }
}