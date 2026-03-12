package com.booking;

import com.booking.model.Homestay;
import com.booking.service.HomestayService;
import com.booking.service.impl.HomestayServiceImpl;

import java.util.List;

public class TestHomestayService {
    public static void main(String[] args) {
        System.out.println("========== 测试民宿Service ==========");

        HomestayService homestayService = new HomestayServiceImpl();

        // 1. 查询所有民宿
        System.out.println("\n=== 1. 所有民宿（第1页，每页5条） ===");
        List<Homestay> list = homestayService.getAllHomestays(1, 5);
        for (Homestay h : list) {
            System.out.println(h.getHomestayId() + " | " + h.getName() + " | " +
                    h.getCity() + " | 评分:" + h.getRating());
        }

        // 2. 按城市查询
        System.out.println("\n=== 2. 北京的民宿 ===");
        List<Homestay> beijingList = homestayService.getHomestaysByCity("北京", 1, 5);
        for (Homestay h : beijingList) {
            System.out.println("  - " + h.getName());
        }

        // 3. 搜索功能
        System.out.println("\n=== 3. 搜索关键词 '山' ===");
        List<Homestay> searchList = homestayService.searchHomestays("山", 1, 5);
        for (Homestay h : searchList) {
            System.out.println("  - " + h.getName());
        }

        // 4. 评分最高的民宿
        System.out.println("\n=== 4. 评分最高的3家民宿 ===");
        List<Homestay> topList = homestayService.getTopRatedHomestays(3);
        for (Homestay h : topList) {
            System.out.println("  - " + h.getName() + " (评分:" + h.getRating() + ")");
        }

        // 5. 城市统计
        System.out.println("\n=== 5. 城市统计 ===");
        List<Object[]> stats = homestayService.getCityStatistics();
        for (Object[] row : stats) {
            System.out.println("  " + row[0] + ": " + row[1] + "家民宿, 平均评分:" + row[2]);
        }

        System.out.println("\n========== 测试完成 ==========");
    }
}