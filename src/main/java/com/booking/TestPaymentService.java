package com.booking;

import com.booking.model.Payment;
import com.booking.service.PaymentService;
import com.booking.service.impl.PaymentServiceImpl;

import java.util.List;

public class TestPaymentService {
    public static void main(String[] args) {
        System.out.println("========== 测试支付Service ==========");

        PaymentService paymentService = new PaymentServiceImpl();

        // 1. 查询所有支付记录
        System.out.println("\n=== 1. 所有支付记录 ===");
        List<Payment> allPayments = paymentService.getPaymentsByStatus(null, 1, 10);
        System.out.println("找到 " + allPayments.size() + " 条支付记录");
        for (Payment p : allPayments) {
            System.out.println("  " + p.getPaymentId() + " | " + p.getPaymentNo() +
                    " | " + p.getAmount() + "元 | " + p.getStatus());
        }

        // 2. 创建新支付
        System.out.println("\n=== 2. 创建新支付 ===");
        Payment newPayment = new Payment();
        newPayment.setReservationId(22);  // 订单ID
        newPayment.setAmount(1288.00);
        newPayment.setPaymentMethod("WECHAT");

        int result = paymentService.createPayment(newPayment);
        if (result == 1) {
            System.out.println("✅ 支付创建成功！支付ID: " + newPayment.getPaymentId());
            System.out.println("   支付单号: " + newPayment.getPaymentNo());
        } else {
            System.out.println("❌ 支付创建失败");
        }

        // 3. 支付成功回调
        if (result == 1) {
            System.out.println("\n=== 3. 支付成功回调 ===");
            boolean success = paymentService.paymentSuccess(newPayment.getPaymentId(), "wx_test_123456789");
            System.out.println("支付成功结果: " + (success ? "✅ 成功" : "❌ 失败"));
        }

        // 4. 查询订单22的支付记录
        System.out.println("\n=== 4. 查询订单22的支付记录 ===");
        List<Payment> orderPayments = paymentService.getPaymentsByReservationId(22);
        System.out.println("找到 " + orderPayments.size() + " 条");
        for (Payment p : orderPayments) {
            System.out.println("  " + p.getPaymentId() + " | " + p.getPaymentNo() +
                    " | " + p.getAmount() + "元 | " + p.getStatus());
        }

        // 5. 查询订单22已支付总额
        System.out.println("\n=== 5. 订单22已支付总额 ===");
        double totalPaid = paymentService.getTotalPaidByReservation(22);
        System.out.println("已支付总额: " + totalPaid + "元");

        boolean isPaid = paymentService.isReservationPaid(22);
        System.out.println("订单是否已支付: " + (isPaid ? "✅ 是" : "❌ 否"));

        // 6. 根据状态查询
        System.out.println("\n=== 6. 查询SUCCESS状态的支付 ===");
        List<Payment> successPayments = paymentService.getPaymentsByStatus("SUCCESS", 1, 5);
        System.out.println("找到 " + successPayments.size() + " 条");

        // 7. 统计信息
        System.out.println("\n=== 7. 统计信息 ===");
        double todayIncome = paymentService.getTodayIncome();
        double monthIncome = paymentService.getMonthIncome();
        double totalAmount = paymentService.getTotalAmount();
        long successCount = paymentService.getSuccessCount();

        System.out.println("今日收入: " + todayIncome + "元");
        System.out.println("本月收入: " + monthIncome + "元");
        System.out.println("总收入: " + totalAmount + "元");
        System.out.println("成功支付笔数: " + successCount + "笔");

        System.out.println("\n========== 测试完成 ==========");
    }
}