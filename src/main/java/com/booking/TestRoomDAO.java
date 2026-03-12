package com.booking;

import com.booking.dao.RoomDAO;
import com.booking.dao.impl.RoomDAOImpl;
import com.booking.model.Room;
import java.util.List;

public class TestRoomDAO {
    public static void main(String[] args) {
        System.out.println("========== 测试房间DAO ==========");

        RoomDAO roomDAO = new RoomDAOImpl();

        // 1. 查询当前房间总数
        System.out.println("\n=== 1. 当前房间总数 ===");
        long count = roomDAO.count();
        System.out.println("房间总数: " + count);

        // 2. 查询所有房间
        System.out.println("\n=== 2. 查询所有房间 ===");
        List<Room> allList = roomDAO.selectAll();
        System.out.println("房间总数: " + allList.size());
        for (Room r : allList) {
            System.out.println(r.getRoomId() + " | " + r.getRoomNumber() + " | " +
                    r.getRoomType() + " | " + r.getPrice() + "元 | " + r.getStatus());
        }

        // 3. 根据民宿ID查询（假设民宿ID=1）
        System.out.println("\n=== 3. 查询民宿ID=1的房间 ===");
        List<Room> homestayList = roomDAO.selectByHomestayId(1);
        System.out.println("找到 " + homestayList.size() + " 个房间");
        for (Room r : homestayList) {
            System.out.println("  - " + r.getRoomNumber() + " | " + r.getRoomType() + " | " + r.getPrice() + "元");
        }

        // 4. 查询可用房间
        System.out.println("\n=== 4. 查询可用房间 ===");
        List<Room> availableList = roomDAO.selectAvailable();
        System.out.println("可用房间: " + availableList.size() + " 个");

        // 5. 根据价格范围查询
        System.out.println("\n=== 5. 查询300-600元的房间 ===");
        List<Room> priceList = roomDAO.selectByPriceRange(300, 600);
        System.out.println("找到 " + priceList.size() + " 个房间");
        for (Room r : priceList) {
            System.out.println("  - " + r.getRoomNumber() + " | " + r.getPrice() + "元");
        }

        // 6. 根据房型查询
        System.out.println("\n=== 6. 查询大床房(DOUBLE) ===");
        List<Room> typeList = roomDAO.selectByType("DOUBLE");
        System.out.println("找到 " + typeList.size() + " 个房间");

        // 7. 根据人数查询
        System.out.println("\n=== 7. 查询可住3人以上的房间 ===");
        List<Room> peopleList = roomDAO.selectByPeopleCount(3);
        System.out.println("找到 " + peopleList.size() + " 个房间");

        // 8. 复合查询：搜索北京的可用房间（核心功能）
        System.out.println("\n=== 8. 搜索北京可住2人的可用房间 ===");
        List<Room> searchList = roomDAO.searchAvailableRooms("北京", 2);
        System.out.println("找到 " + searchList.size() + " 个房间");
        for (Room r : searchList) {
            System.out.println("  - " + r.getRoomNumber() + " | " + r.getPrice() + "元 | " + r.getStatus());
        }

        System.out.println("\n========== 测试完成 ==========");
    }
}