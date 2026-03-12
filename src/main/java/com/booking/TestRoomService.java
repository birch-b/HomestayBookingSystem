package com.booking;

import com.booking.model.Room;
import com.booking.service.RoomService;
import com.booking.service.impl.RoomServiceImpl;

import java.util.List;

public class TestRoomService {
    public static void main(String[] args) {
        System.out.println("========== 测试房间Service ==========");

        RoomService roomService = new RoomServiceImpl();

        // 1. 查询所有房间
        System.out.println("\n=== 1. 所有房间（第1页，每页5条） ===");
        List<Room> list = roomService.getAllRooms(1, 5);
        for (Room r : list) {
            System.out.println(r.getRoomId() + " | " + r.getRoomNumber() + " | " +
                    r.getRoomType() + " | " + r.getPrice() + "元 | " + r.getStatus());
        }

        // 2. 查询民宿1的房间
        System.out.println("\n=== 2. 民宿1的房间 ===");
        List<Room> homestayRooms = roomService.getRoomsByHomestayId(1);
        for (Room r : homestayRooms) {
            System.out.println("  - " + r.getRoomNumber() + " | " + r.getRoomType() + " | " + r.getPrice() + "元");
        }

        // 3. 查询可用房间
        System.out.println("\n=== 3. 可用房间 ===");
        List<Room> available = roomService.getAvailableRooms();
        System.out.println("可用房间数: " + available.size());

        // 4. 按价格范围查询
        System.out.println("\n=== 4. 300-600元的房间 ===");
        List<Room> priceRange = roomService.getRoomsByPriceRange(300, 600, 1, 5);
        for (Room r : priceRange) {
            System.out.println("  - " + r.getRoomNumber() + " | " + r.getPrice() + "元");
        }

        // 5. 按房型查询
        System.out.println("\n=== 5. 大床房(DOUBLE) ===");
        List<Room> typeRooms = roomService.getRoomsByType("DOUBLE", 1, 5);
        System.out.println("找到 " + typeRooms.size() + " 间");

        // 6. 按人数查询
        System.out.println("\n=== 6. 可住3人以上的房间 ===");
        List<Room> peopleRooms = roomService.getRoomsByPeopleCount(3, 1, 5);
        for (Room r : peopleRooms) {
            System.out.println("  - " + r.getRoomNumber() + " | 可住" + r.getMaxPeople() + "人");
        }

        // 7. 统计民宿1的房间状态
        System.out.println("\n=== 7. 民宿1房间状态统计 ===");
        int[] statusStats = roomService.getStatusStatistics(1);
        System.out.println("可用: " + statusStats[0] + "间");
        System.out.println("已订: " + statusStats[1] + "间");
        System.out.println("维护: " + statusStats[2] + "间");

        // 8. 统计民宿1的房型分布
        System.out.println("\n=== 8. 民宿1房型统计 ===");
        List<Object[]> typeStats = roomService.getTypeStatistics(1);
        for (Object[] row : typeStats) {
            System.out.println("  " + row[0] + ": " + row[1] + "间, 均价:" + row[2] + "元");
        }

        System.out.println("\n========== 测试完成 ==========");
    }
}