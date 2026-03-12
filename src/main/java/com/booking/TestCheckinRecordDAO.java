package com.booking;

import com.booking.dao.CheckinRecordDAO;
import com.booking.dao.ReservationDAO;
import com.booking.dao.impl.CheckinRecordDAOImpl;
import com.booking.dao.impl.ReservationDAOImpl;
import com.booking.model.CheckinRecord;
import com.booking.model.Reservation;

import java.util.List;

public class TestCheckinRecordDAO {
    public static void main(String[] args) {
        System.out.println("========== 测试入住记录DAO ==========");

        CheckinRecordDAO checkinDAO = new CheckinRecordDAOImpl();
        ReservationDAO reservationDAO = new ReservationDAOImpl();

        // 1. 查询当前入住记录总数
        System.out.println("\n=== 1. 当前入住记录总数 ===");
        long count = checkinDAO.count();
        System.out.println("入住记录数: " + count);

        // 2. 找一个已入住的订单来测试
        System.out.println("\n=== 2. 查找已入住的订单 ===");
        List<Reservation> checkedInList = reservationDAO.selectByStatus("CHECKED_IN");
        System.out.println("已入住订单数: " + checkedInList.size());

        if (checkedInList.size() > 0) {
            Reservation res = checkedInList.get(0);
            System.out.println("订单: " + res.getReservationId() + " | " +
                    res.getReservationNo() + " | " + res.getGuestName());

            // 3. 创建入住记录
            System.out.println("\n=== 3. 创建入住记录 ===");
            CheckinRecord record = new CheckinRecord();
            record.setReservationId(res.getReservationId());
            record.setDeposit(200.0);
            record.setRoomKeysGiven(2);
            record.setRemarks("客人要求安静房间");

            int result = checkinDAO.insert(record);
            System.out.println("插入结果: " + (result > 0 ? "✅ 成功" : "❌ 失败"));
            if (result > 0) {
                System.out.println("记录ID: " + record.getRecordId());
            }

            // 4. 根据预订ID查询
            System.out.println("\n=== 4. 根据预订ID查询 ===");
            CheckinRecord found = checkinDAO.selectByReservationId(res.getReservationId());
            if (found != null) {
                System.out.println("找到记录: ID=" + found.getRecordId() +
                        ", 押金=" + found.getDeposit() +
                        ", 房卡=" + found.getRoomKeysGiven());
            }

            // 5. 查询所有入住记录
            System.out.println("\n=== 5. 所有入住记录 ===");
            List<CheckinRecord> allList = checkinDAO.selectAll();
            System.out.println("总数: " + allList.size());
            for (CheckinRecord r : allList) {
                System.out.println(r.getRecordId() + " | 预订ID=" + r.getReservationId() +
                        " | 押金=" + r.getDeposit());
            }

            // 6. 测试退房
            System.out.println("\n=== 6. 测试退房 ===");
            if (found != null) {
                int updateResult = checkinDAO.updateCheckOut(found.getRecordId(), 200.0);
                System.out.println("退房更新: " + (updateResult > 0 ? "✅ 成功" : "❌ 失败"));

                // 验证更新
                CheckinRecord updated = checkinDAO.selectById(found.getRecordId());
                if (updated != null) {
                    System.out.println("退房时间: " + updated.getActualCheckOut());
                    System.out.println("退还押金: " + updated.getDepositReturn());
                }
            }
        }

        // 7. 测试今日入住
        System.out.println("\n=== 7. 今日入住 ===");
        List<CheckinRecord> todayIn = checkinDAO.selectTodayCheckIn();
        System.out.println("今日入住: " + todayIn.size() + " 条");

        // 8. 测试今日退房
        System.out.println("\n=== 8. 今日退房 ===");
        List<CheckinRecord> todayOut = checkinDAO.selectTodayCheckOut();
        System.out.println("今日退房: " + todayOut.size() + " 条");

        // 9. 测试分页
        System.out.println("\n=== 9. 分页测试 ===");
        List<CheckinRecord> page1 = checkinDAO.selectByPage(1, 3);
        System.out.println("第1页(3条): " + page1.size() + " 条");
        for (CheckinRecord r : page1) {
            System.out.println("  " + r.getRecordId() + " | 预订ID=" + r.getReservationId());
        }

        System.out.println("\n========== 测试完成 ==========");
    }
}