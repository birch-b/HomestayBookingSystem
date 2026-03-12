package com.booking;

import com.booking.dao.HomestayDAO;
import com.booking.dao.impl.HomestayDAOImpl;
import com.booking.model.Homestay;
import java.util.List;

public class TestHomestayDAO {
    public static void main(String[] args) {
        System.out.println("========== 测试民宿DAO ==========");

        HomestayDAO homestayDAO = new HomestayDAOImpl();

        // 1. 查询当前民宿总数
        System.out.println("\n=== 1. 当前民宿总数 ===");
        long count = homestayDAO.count();
        System.out.println("民宿总数: " + count);

        // 2. 查询所有民宿
        System.out.println("\n=== 2. 查询所有民宿 ===");
        List<Homestay> allList = homestayDAO.selectAll();
        System.out.println("民宿总数: " + allList.size());
        for (Homestay h : allList) {
            System.out.println(h.getHomestayId() + " | " + h.getName() + " | " + h.getCity() + " | " + h.getPhone() + " | 评分:" + h.getRating());
        }

        // 3. 根据ID查询（如果存在）
        if (allList.size() > 0) {
            int firstId = allList.get(0).getHomestayId();
            System.out.println("\n=== 3. 查询ID=" + firstId + "的民宿 ===");
            Homestay found = homestayDAO.selectById(firstId);
            if (found != null) {
                System.out.println("找到: " + found.getName());
                System.out.println("地址: " + found.getAddress());
                System.out.println("电话: " + found.getPhone());
                System.out.println("评分: " + found.getRating());
            }
        }

        // 4. 根据城市查询
        System.out.println("\n=== 4. 查询北京的民宿 ===");
        List<Homestay> beijingList = homestayDAO.selectByCity("北京");
        System.out.println("找到 " + beijingList.size() + " 家");
        for (Homestay h : beijingList) {
            System.out.println("  - " + h.getName() + " (评分:" + h.getRating() + ")");
        }

        // 5. 根据民宿主ID查询
        System.out.println("\n=== 5. 查询民宿主ID=2的民宿 ===");
        List<Homestay> hostList = homestayDAO.selectByHostId(2);
        System.out.println("找到 " + hostList.size() + " 家");
        for (Homestay h : hostList) {
            System.out.println("  - " + h.getName());
        }

        // 6. 测试搜索功能
        System.out.println("\n=== 6. 搜索关键词 '山' ===");
        List<Homestay> searchList = homestayDAO.search("山");
        System.out.println("找到 " + searchList.size() + " 家");
        for (Homestay h : searchList) {
            System.out.println("  - " + h.getName());
        }

        // 7. 搜索关键词 '海'
        System.out.println("\n=== 7. 搜索关键词 '海' ===");
        searchList = homestayDAO.search("海");
        System.out.println("找到 " + searchList.size() + " 家");
        for (Homestay h : searchList) {
            System.out.println("  - " + h.getName());
        }

        // 8. 根据评分范围查询
        System.out.println("\n=== 8. 查询评分4.5以上的民宿 ===");
        List<Homestay> ratingList = homestayDAO.selectByRatingRange(4.5, 5.0);
        System.out.println("找到 " + ratingList.size() + " 家");
        for (Homestay h : ratingList) {
            System.out.println("  - " + h.getName() + " (评分:" + h.getRating() + ")");
        }

        System.out.println("\n========== 测试完成 ==========");
    }
}