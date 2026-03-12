package com.booking;

import com.booking.model.CheckinRecord;
import com.booking.service.CheckinRecordService;
import com.booking.service.impl.CheckinRecordServiceImpl;

import java.util.List;

public class TestCheckinRecordService {
    public static void main(String[] args) {
        System.out.println("========== 测试入住记录Service ==========");

        CheckinRecordService checkinService = new CheckinRecordServiceImpl();

        // 1. 查询所有入住记录
        System.out.println("\n=== 1. 所有入住记录 ===");
        List<CheckinRecord> allRecords = checkinService.getRecordsByGuestId(0, 1, 10);
        System.out.println("找到 " + allRecords.size() + " 条记录");

        // 2. 测试办理入住
        System.out.println("\n=== 2. 办理入住 ===");
        int result = checkinService.checkIn(21, 200.0, 2, "客人要求安静房间");
        if (result == 1) {
            System.out.println("✅ 入住办理成功");
        } else if (result == -1) {
            System.out.println("❌ 入住失败：订单状态错误或已入住");
        } else {
            System.out.println("❌ 入住失败：系统错误");
        }

        // 3. 查询今日入住
        System.out.println("\n=== 3. 今日入住 ===");
        List<CheckinRecord> todayIn = checkinService.getTodayCheckIn();
        System.out.println("今日入住: " + todayIn.size() + " 条");
        for (CheckinRecord r : todayIn) {
            System.out.println("  " + r.getRecordId() + " | 预订ID:" + r.getReservationId() +
                    " | 押金:" + r.getDeposit());
        }

        // 4. 根据预订ID查询
        System.out.println("\n=== 4. 查询预订21的入住记录 ===");
        CheckinRecord record = checkinService.getRecordByReservationId(21);
        if (record != null) {
            System.out.println("找到记录: ID=" + record.getRecordId() +
                    ", 押金=" + record.getDeposit() +
                    ", 房卡=" + record.getRoomKeysGiven());
        }

        // 5. 测试获取详情
        if (record != null) {
            System.out.println("\n=== 5. 获取入住详情 ID=" + record.getRecordId() + " ===");
            CheckinRecord detail = checkinService.getRecordDetail(record.getRecordId());
            if (detail != null) {
                System.out.println("记录ID: " + detail.getRecordId());

                // 显示关联的订单信息
                if (detail.getReservation() != null) {
                    System.out.println("订单号: " + detail.getReservation().getReservationNo());
                    System.out.println("订单状态: " + detail.getReservation().getStatus());
                }

                // 显示关联的客人信息
                if (detail.getGuest() != null) {
                    System.out.println("客人: " + detail.getGuest().getRealName() +
                            " (" + detail.getGuest().getUsername() + ")");
                }

                // 显示关联的房间信息
                if (detail.getRoom() != null) {
                    System.out.println("房间号: " + detail.getRoom().getRoomNumber());
                    System.out.println("房型: " + detail.getRoom().getRoomType());
                }

                // 显示关联的民宿信息
                if (detail.getHomestay() != null) {
                    System.out.println("民宿: " + detail.getHomestay().getName());
                }
            }
        }

        // 6. 测试退房
        if (record != null) {
            System.out.println("\n=== 6. 办理退房 ===");
            boolean checkout = checkinService.checkOut(record.getRecordId(), 200.0);
            System.out.println("退房结果: " + (checkout ? "✅ 成功" : "❌ 失败"));
        }

        // 7. 统计信息
        System.out.println("\n=== 7. 统计信息 ===");
        int todayInCount = checkinService.getTodayCheckInCount();
        int todayOutCount = checkinService.getTodayCheckOutCount();
        int currentOccupancy = checkinService.getCurrentOccupancy();

        System.out.println("今日入住人数: " + todayInCount);
        System.out.println("今日退房人数: " + todayOutCount);
        System.out.println("当前在住人数: " + currentOccupancy);

        // 8. 查询民宿1的入住率
        System.out.println("\n=== 8. 民宿1今日入住率 ===");
        double occupancyRate = checkinService.getOccupancyRate(1, new java.util.Date());
        System.out.println("入住率: " + occupancyRate + "%");

        System.out.println("\n========== 测试完成 ==========");
    }
}