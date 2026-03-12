package com.booking;

import com.booking.model.Review;
import com.booking.service.ReviewService;
import com.booking.service.impl.ReviewServiceImpl;

import java.util.List;

public class TestReviewService {
    public static void main(String[] args) {
        System.out.println("========== 测试评价Service ==========");

        ReviewService reviewService = new ReviewServiceImpl();

        // 1. 查询民宿1的评价
        System.out.println("\n=== 1. 民宿1的评价（第1页，每页5条） ===");
        List<Review> list = reviewService.getReviewsByHomestayId(1, 1, 5);
        System.out.println("找到 " + list.size() + " 条评价");
        for (Review r : list) {
            System.out.println("  " + r.getReviewId() + " | 评分:" + r.getRating() +
                    "星 | " + r.getComment());
            if (r.getHostReply() != null) {
                System.out.println("    房东回复: " + r.getHostReply());
            }
        }

        // 2. 测试发表评价
        System.out.println("\n=== 2. 发表评价 ===");
        Review newReview = new Review();
        newReview.setReservationId(22);  // 找个已完成的订单ID
        newReview.setGuestId(3);         // 张三
        newReview.setRating(5);
        newReview.setComment("非常棒的住宿体验！房间干净，风景优美！");

        int result = reviewService.addReview(newReview);
        if (result == 1) {
            System.out.println("✅ 评价发表成功！评价ID: " + newReview.getReviewId());

            // 验证民宿评分是否更新
            double avgRating = reviewService.getAverageRatingByHomestay(1);
            System.out.println("民宿1当前平均评分: " + avgRating);
        } else if (result == -1) {
            System.out.println("❌ 评价失败：该订单已评价过");
        } else {
            System.out.println("❌ 评价失败：订单未完成或系统错误");
        }

        // 3. 查询最新评价
        System.out.println("\n=== 3. 最新3条评价 ===");
        List<Review> latest = reviewService.getLatestReviews(3);
        for (Review r : latest) {
            System.out.println("  " + r.getReviewId() + " | 评分:" + r.getRating() +
                    "星 | " + r.getComment());
        }

        // 4. 测试房东回复
        if (!latest.isEmpty()) {
            int reviewId = latest.get(0).getReviewId();
            System.out.println("\n=== 4. 回复评价 ID=" + reviewId + " ===");
            boolean replied = reviewService.replyReview(reviewId, "感谢您的评价！欢迎下次光临！");
            System.out.println("回复结果: " + (replied ? "✅ 成功" : "❌ 失败"));
        }

        // 5. 查询高分评价
        System.out.println("\n=== 5. 民宿1的高分评价（4星以上） ===");
        List<Review> highRating = reviewService.getHighRatingReviews(1, 1, 5);
        System.out.println("找到 " + highRating.size() + " 条");
        for (Review r : highRating) {
            System.out.println("  " + r.getReviewId() + " | " + r.getRating() + "星 | " + r.getComment());
        }

        // 6. 统计民宿1的评分
        System.out.println("\n=== 6. 民宿1评分统计 ===");
        double avgRating = reviewService.getAverageRatingByHomestay(1);
        System.out.println("平均评分: " + avgRating + " 星");

        long totalCount = reviewService.getReviewCountByHomestay(1);
        System.out.println("评价总数: " + totalCount);

        // 7. 评分分布
        System.out.println("\n=== 7. 民宿1评分分布 ===");
        int[] distribution = reviewService.getRatingDistribution(1);
        System.out.println("5星: " + distribution[4] + " 条");
        System.out.println("4星: " + distribution[3] + " 条");
        System.out.println("3星: " + distribution[2] + " 条");
        System.out.println("2星: " + distribution[1] + " 条");
        System.out.println("1星: " + distribution[0] + " 条");

        // 8. 检查订单是否已评价
        System.out.println("\n=== 8. 检查订单22是否已评价 ===");
        boolean hasReviewed = reviewService.hasReviewed(22);
        System.out.println("订单22评价状态: " + (hasReviewed ? "✅ 已评价" : "❌ 未评价"));

        System.out.println("\n========== 测试完成 ==========");
    }
}