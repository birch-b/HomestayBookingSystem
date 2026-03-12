package com.booking;

import com.booking.dao.PaymentDAO;
import com.booking.dao.impl.PaymentDAOImpl;
import com.booking.model.Payment;

import java.util.List;
import java.util.UUID;

public class TestPaymentDAO {
    public static void main(String[] args) {
        System.out.println("========== 测试支付DAO ==========");

        PaymentDAO paymentDAO = new PaymentDAOImpl();

        // 1. 当前支付记录总数
        System.out.println("\n=== 1. 当前支付记录总数 ===");
        long count = paymentDAO.count();
        System.out.println("支付记录数: " + count);

        // 2. 插入支付记录
        System.out.println("\n=== 2. 插入支付记录 ===");
        Payment payment = new Payment();
        payment.setPaymentNo(generatePaymentNo());
        payment.setReservationId(21);  // 订单ID=21
        payment.setAmount(1288.00);
        payment.setPaymentMethod("WECHAT");
        payment.setStatus("PENDING");

        int result = paymentDAO.insert(payment);
        System.out.println("插入结果: " + (result > 0 ? "✅ 成功" : "❌ 失败"));
        if (result > 0) {
            System.out.println("支付ID: " + payment.getPaymentId());
            System.out.println("支付单号: " + payment.getPaymentNo());
        }

        // 3. 根据ID查询
        System.out.println("\n=== 3. 根据ID查询 ===");
        if (payment.getPaymentId() > 0) {
            Payment found = paymentDAO.selectById(payment.getPaymentId());
            if (found != null) {
                System.out.println("支付ID: " + found.getPaymentId());
                System.out.println("金额: " + found.getAmount());
                System.out.println("方式: " + found.getPaymentMethod());
                System.out.println("状态: " + found.getStatus());
            }
        }

        // 4. 根据预订ID查询
        System.out.println("\n=== 4. 查询订单21的支付记录 ===");
        List<Payment> reservationList = paymentDAO.selectByReservationId(21);
        System.out.println("找到 " + reservationList.size() + " 条");
        for (Payment p : reservationList) {
            System.out.println("  " + p.getPaymentId() + " | " + p.getPaymentNo() + " | " + p.getAmount() + "元");
        }

        // 5. 测试支付成功回调
        System.out.println("\n=== 5. 测试支付成功回调 ===");
        if (payment.getPaymentId() > 0) {
            int successResult = paymentDAO.paymentSuccess(payment.getPaymentId(), "wx_" + UUID.randomUUID().toString());
            System.out.println("支付成功更新: " + (successResult > 0 ? "✅ 成功" : "❌ 失败"));

            // 验证更新
            Payment updated = paymentDAO.selectById(payment.getPaymentId());
            if (updated != null) {
                System.out.println("更新后状态: " + updated.getStatus());
                System.out.println("支付时间: " + updated.getPayTime());
                System.out.println("交易号: " + updated.getTransactionId());
            }
        }

        // 6. 根据状态查询
        System.out.println("\n=== 6. 查询状态为SUCCESS的支付 ===");
        List<Payment> successList = paymentDAO.selectByStatus("SUCCESS");
        System.out.println("找到 " + successList.size() + " 条");

        // 7. 根据支付方式查询
        System.out.println("\n=== 7. 查询微信支付 ===");
        List<Payment> wechatList = paymentDAO.selectByMethod("WECHAT");
        System.out.println("找到 " + wechatList.size() + " 条");

        // 8. 统计订单已支付总额
        System.out.println("\n=== 8. 订单21已支付总额 ===");
        double totalPaid = paymentDAO.getTotalPaidByReservation(21);
        System.out.println("已支付: " + totalPaid + "元");

        // 9. 所有支付记录
        System.out.println("\n=== 9. 所有支付记录 ===");
        List<Payment> allList = paymentDAO.selectAll();
        System.out.println("总数: " + allList.size());
        for (Payment p : allList) {
            System.out.println(p.getPaymentId() + " | " + p.getPaymentNo() + " | " +
                    p.getAmount() + "元 | " + p.getStatus());
        }

        System.out.println("\n========== 测试完成 ==========");
    }

    private static String generatePaymentNo() {
        return "PAY" + System.currentTimeMillis();
    }
}