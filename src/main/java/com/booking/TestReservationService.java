package com.booking;

import com.booking.model.*;
import com.booking.service.ReservationService;
import com.booking.service.impl.ReservationServiceImpl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TestReservationService {
    public static void main(String[] args) {
        System.out.println("========== 测试 ReservationService ==========");

        ReservationService service = new ReservationServiceImpl();

        // ==================== 1. 测试创建预订 ====================
        System.out.println("\n=== 1. 测试创建预订 ===");

        // 创建预订对象
        Reservation reservation = new Reservation();
        reservation.setRoomId(1);           // 房间ID=1
        reservation.setGuestId(3);           // 客人ID=3 (张三)

        // 设置入住日期：明天
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date checkIn = cal.getTime();

        // 设置离店日期：后天
        cal.add(Calendar.DAY_OF_MONTH, 2);
        Date checkOut = cal.getTime();

        reservation.setCheckInDate(checkIn);
        reservation.setCheckOutDate(checkOut);
        reservation.setGuestsCount(2);
        reservation.setGuestName("张三");
        reservation.setGuestPhone("13800138000");
        reservation.setSpecialRequests("需要安静的房间");

        // 创建支付对象
        Payment payment = new Payment();
        payment.setAmount(776.00);           // 房费
        payment.setPaymentMethod("WECHAT");

        System.out.println("房间ID: " + reservation.getRoomId());
        System.out.println("入住: " + checkIn);
        System.out.println("离店: " + checkOut);
        System.out.println("人数: " + reservation.getGuestsCount());

        int result = service.createReservation(reservation, payment);
        if (result == 1) {
            System.out.println("✅ 预订成功！");
            System.out.println("订单号: " + reservation.getReservationNo());
            System.out.println("订单ID: " + reservation.getReservationId());
            System.out.println("总价: " + reservation.getTotalPrice());
        } else if (result == -1) {
            System.out.println("❌ 预订失败：房间已被预订");
        } else {
            System.out.println("❌ 预订失败：系统错误");
        }

        // ==================== 2. 测试获取订单详情 ====================
        System.out.println("\n=== 2. 测试获取订单详情 ===");
        if (reservation.getReservationId() > 0) {
            Reservation detail = service.getReservationDetail(reservation.getReservationId());
            if (detail != null) {
                System.out.println("订单ID: " + detail.getReservationId());
                System.out.println("订单号: " + detail.getReservationNo());
                System.out.println("客人: " + detail.getGuestName());
                System.out.println("状态: " + detail.getStatus());

                // 如果设置了关联对象
                if (detail.getRoom() != null) {
                    System.out.println("房间号: " + detail.getRoom().getRoomNumber());
                }
            }
        }

        // ==================== 3. 测试查询用户订单 ====================
        System.out.println("\n=== 3. 测试查询用户订单 (用户ID=3) ===");
        List<Reservation> userOrders = service.getUserReservations(3, 1, 5);
        System.out.println("找到 " + userOrders.size() + " 个订单");
        for (Reservation r : userOrders) {
            System.out.println("  " + r.getReservationId() + " | " +
                    r.getReservationNo() + " | " +
                    r.getStatus());
        }

        // ==================== 4. 测试支付成功回调 ====================
        System.out.println("\n=== 4. 测试支付成功回调 ===");
        if (reservation.getReservationId() > 0) {
            boolean paid = service.paymentSuccess(reservation.getReservationId(), "wx_test_123456");
            System.out.println("支付结果: " + (paid ? "✅ 成功" : "❌ 失败"));

            // 验证状态
            Reservation afterPay = service.getReservationDetail(reservation.getReservationId());
            if (afterPay != null) {
                System.out.println("订单状态: " + afterPay.getStatus());
            }
        }

        // ==================== 5. 测试取消预订 ====================
        System.out.println("\n=== 5. 测试取消预订 ===");
        if (reservation.getReservationId() > 0) {
            boolean cancelled = service.cancelReservation(reservation.getReservationId());
            System.out.println("取消结果: " + (cancelled ? "✅ 成功" : "❌ 失败"));

            // 验证状态
            Reservation afterCancel = service.getReservationDetail(reservation.getReservationId());
            if (afterCancel != null) {
                System.out.println("订单状态: " + afterCancel.getStatus());
            }
        }

        // ==================== 6. 测试价格计算 ====================
        System.out.println("\n=== 6. 测试价格计算 ===");
        double price = service.calculateTotalPrice(1, checkIn, checkOut, 2);
        System.out.println("房间1的价格: " + price + "元");

        // ==================== 7. 测试搜索功能 ====================
        System.out.println("\n=== 7. 测试搜索功能 (关键词'张三') ===");
        List<Reservation> searchResult = service.searchReservations(3,"张三", null, null, null, 1, 5);
        System.out.println("找到 " + searchResult.size() + " 个订单");
        for (Reservation r : searchResult) {
            System.out.println("  " + r.getReservationId() + " | " +
                    r.getReservationNo() + " | " +
                    r.getGuestName() + " | " +
                    r.getStatus());
        }

        System.out.println("\n========== 测试完成 ==========");
    }
}