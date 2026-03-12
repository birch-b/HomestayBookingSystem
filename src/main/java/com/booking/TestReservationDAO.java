package com.booking;

import com.booking.dao.ReservationDAO;
import com.booking.dao.impl.ReservationDAOImpl;
import com.booking.model.Reservation;
import com.booking.model.Room;
import com.booking.dao.RoomDAO;
import com.booking.dao.impl.RoomDAOImpl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Calendar;

public class TestReservationDAO {
    public static void main(String[] args) {
        System.out.println("========== 测试预订DAO ==========");

        ReservationDAO reservationDAO = new ReservationDAOImpl();
        RoomDAO roomDAO = new RoomDAOImpl();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // ==================== 1. 基础统计 ====================
        System.out.println("\n=== 1. 基础统计 ===");
        long totalCount = reservationDAO.count();
        System.out.println("订单总数: " + totalCount);

        // ==================== 2. 查询所有可用房间 ====================
        System.out.println("\n=== 2. 所有可用房间 ===");
        List<Room> rooms = roomDAO.selectAvailable();
        for (Room r : rooms) {
            System.out.println(r.getRoomId() + " | " + r.getRoomNumber() + " | " + r.getPrice() + "元");
        }

        // ==================== 3. 测试创建预订 ====================
        System.out.println("\n=== 3. 测试创建预订 ===");
        if (rooms.size() > 0) {
            int roomId = rooms.get(0).getRoomId();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 1);
            Date checkIn = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, 1);
            Date checkOut = cal.getTime();

            Reservation reservation = new Reservation();
            reservation.setRoomId(roomId);
            reservation.setGuestId(3);
            reservation.setCheckInDate(checkIn);
            reservation.setCheckOutDate(checkOut);
            reservation.setGuestsCount(2);
            reservation.setTotalPrice(rooms.get(0).getPrice() * 2);
            reservation.setGuestName("张三");
            reservation.setGuestPhone("13800138000");
            reservation.setSpecialRequests("需要加床");

            System.out.println("房间ID: " + roomId);
            System.out.println("入住: " + checkIn);
            System.out.println("离店: " + checkOut);

            int result = reservationDAO.createReservation(reservation);
            if (result > 0) {
                System.out.println("✅ 预订成功！订单号: " + reservation.getReservationNo());
                System.out.println("订单ID: " + reservation.getReservationId());
            } else if (result == -1) {
                System.out.println("❌ 预订失败：房间已被预订");
            } else {
                System.out.println("❌ 预订失败：系统错误");
            }
        }

        // ==================== 4. 测试查询所有订单（分页） ====================
        System.out.println("\n=== 4. 测试分页查询 ===");
        int pageSize = 5;
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        System.out.println("每页显示: " + pageSize + " 条");
        System.out.println("总页数: " + (totalPages + 1));  // +1 因为刚插了一条

        for (int page = 1; page <= 3; page++) {  // 只测前3页
            System.out.println("\n--- 第 " + page + " 页 ---");
            List<Reservation> pageList = reservationDAO.selectByPage(page, pageSize);
            for (Reservation r : pageList) {
                System.out.println(r.getReservationId() + " | " +
                        r.getReservationNo() + " | " +
                        r.getGuestName() + " | " +
                        r.getStatus() + " | " +
                        sdf.format(r.getCreateTime()));
            }
        }

        // ==================== 5. 测试按客人ID查询 ====================
        System.out.println("\n=== 5. 查询客人ID=3的订单 ===");
        List<Reservation> guestList = reservationDAO.selectByGuestId(3);
        System.out.println("找到 " + guestList.size() + " 个订单");
        for (Reservation r : guestList) {
            System.out.println("  - " + r.getReservationNo() + " | " + r.getStatus() + " | " + r.getGuestName());
        }

        // ==================== 6. 测试按状态查询 ====================
        System.out.println("\n=== 6. 按状态查询 ===");
        String[] statuses = {"PENDING", "PAID", "CONFIRMED", "CHECKED_IN", "COMPLETED", "CANCELLED"};
        for (String status : statuses) {
            List<Reservation> statusList = reservationDAO.selectByStatus(status);
            System.out.println(status + ": " + statusList.size() + " 个订单");
        }

        // ==================== 7. 测试按房间ID查询 ====================
        System.out.println("\n=== 7. 按房间ID查询 ===");
        if (rooms.size() > 0) {
            int roomId = rooms.get(0).getRoomId();
            List<Reservation> roomList = reservationDAO.selectByRoomId(roomId);
            System.out.println("房间 " + roomId + " 的订单: " + roomList.size() + " 个");
        }

        // ==================== 8. 测试按民宿ID查询 ====================
        System.out.println("\n=== 8. 按民宿ID查询 ===");
        List<Reservation> homestayList = reservationDAO.selectByHomestayId(1);
        System.out.println("民宿1的订单: " + homestayList.size() + " 个");

        // ==================== 9. 测试日期范围查询 ====================
        System.out.println("\n=== 9. 日期范围查询 ===");
        Calendar cal = Calendar.getInstance();
        cal.set(2026, Calendar.MARCH, 1);
        Date start = cal.getTime();
        cal.set(2026, Calendar.MARCH, 31);
        Date end = cal.getTime();
        System.out.println("2026-03-01 到 2026-03-31");
        List<Reservation> dateList = reservationDAO.selectByDateRange(start, end);
        System.out.println("找到 " + dateList.size() + " 个订单");

        // ==================== 10. 测试复合查询（关键词） ====================
        System.out.println("\n=== 10. 复合查询（关键词） ===");
        String[] keywords = {"张三", "李四", "202603"};
        for (String keyword : keywords) {
            long matchCount = reservationDAO.countSearch(keyword, null, null, null);
            System.out.println("关键词 '" + keyword + "' 匹配: " + matchCount + " 个");

            List<Reservation> searchList = reservationDAO.searchReservations(
                    keyword, null, null, null, 1, 3);
            System.out.println("  第1页前3条:");
            for (Reservation r : searchList) {
                System.out.println("    " + r.getReservationId() + " | " +
                        r.getReservationNo() + " | " +
                        r.getGuestName());
            }
        }

        // ==================== 11. 测试复合查询（组合条件） ====================
        System.out.println("\n=== 11. 复合查询（组合条件） ===");
        System.out.println("条件: 状态=COMPLETED, 关键词=张");
        List<Reservation> comboList = reservationDAO.searchReservations(
                "张", "COMPLETED", null, null, 1, 5);
        System.out.println("找到 " + comboList.size() + " 个订单");
        for (Reservation r : comboList) {
            System.out.println("  " + r.getReservationNo() + " | " +
                    r.getGuestName() + " | " + r.getStatus());
        }

        // ==================== 12. 测试取消订单 ====================
        System.out.println("\n=== 12. 测试取消订单 ===");
        List<Reservation> allList = reservationDAO.selectAll();
        if (allList.size() > 0) {
            // 找一个PENDING状态的订单来取消
            Reservation toCancel = null;
            for (Reservation r : allList) {
                if ("PENDING".equals(r.getStatus())) {
                    toCancel = r;
                    break;
                }
            }

            if (toCancel != null) {
                System.out.println("取消订单 ID=" + toCancel.getReservationId() +
                        ", 状态=" + toCancel.getStatus());
                int cancelResult = reservationDAO.cancelReservation(toCancel.getReservationId());
                System.out.println("取消结果: " + (cancelResult > 0 ? "✅ 成功" : "❌ 失败"));

                // 验证状态
                Reservation cancelled = reservationDAO.selectById(toCancel.getReservationId());
                if (cancelled != null) {
                    System.out.println("取消后状态: " + cancelled.getStatus());
                }
            } else {
                System.out.println("没有找到PENDING状态的订单可取消");
            }
        }

        // ==================== 13. 测试更新状态 ====================
        System.out.println("\n=== 13. 测试更新状态 ===");
        List<Reservation> pendingList = reservationDAO.selectByStatus("PENDING");
        if (pendingList.size() > 0) {
            int id = pendingList.get(0).getReservationId();
            System.out.println("将订单 " + id + " 从 PENDING 改为 PAID");
            int updateResult = reservationDAO.updateStatus(id, "PAID");
            System.out.println("更新结果: " + (updateResult > 0 ? "✅ 成功" : "❌ 失败"));

            Reservation updated = reservationDAO.selectById(id);
            System.out.println("更新后状态: " + updated.getStatus());
        }

        // ==================== 14. 测试入住/退房 ====================
        System.out.println("\n=== 14. 测试入住/退房 ===");
        List<Reservation> paidList = reservationDAO.selectByStatus("PAID");
        if (paidList.size() > 0) {
            int id = paidList.get(0).getReservationId();
            System.out.println("订单 " + id + " 办理入住");
            int checkInResult = reservationDAO.checkIn(id);
            System.out.println("入住结果: " + (checkInResult > 0 ? "✅ 成功" : "❌ 失败"));

            System.out.println("订单 " + id + " 办理退房");
            int checkOutResult = reservationDAO.checkOut(id);
            System.out.println("退房结果: " + (checkOutResult > 0 ? "✅ 成功" : "❌ 失败"));
        }

        // ==================== 15. 测试无效条件 ====================
        System.out.println("\n=== 15. 测试无效条件 ===");
        List<Reservation> emptyList = reservationDAO.searchReservations(
                "abc", null, null, null, 1, 5);
        System.out.println("搜索不存在的关键词 'abc': " + emptyList.size() + " 条");

        emptyList = reservationDAO.searchReservations(
                null, "XXX", null, null, 1, 5);
        System.out.println("搜索不存在的状态 'XXX': " + emptyList.size() + " 条");

        System.out.println("\n========== 所有测试完成 ==========");
    }
}