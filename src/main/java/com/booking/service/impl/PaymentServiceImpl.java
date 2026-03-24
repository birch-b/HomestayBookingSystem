package com.booking.service.impl;

import com.booking.dao.PaymentDAO;
import com.booking.dao.ReservationDAO;
import com.booking.dao.UserDAO;
import com.booking.dao.HomestayDAO;
import com.booking.dao.RoomDAO;
import com.booking.dao.impl.PaymentDAOImpl;
import com.booking.dao.impl.ReservationDAOImpl;
import com.booking.dao.impl.UserDAOImpl;
import com.booking.dao.impl.HomestayDAOImpl;
import com.booking.dao.impl.RoomDAOImpl;
import com.booking.model.Payment;
import com.booking.model.Reservation;
import com.booking.model.Room;
import com.booking.service.PaymentService;
import com.booking.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 支付业务逻辑实现类
 */
public class PaymentServiceImpl implements PaymentService {

    private PaymentDAO paymentDAO;
    private ReservationDAO reservationDAO;
    private UserDAO userDAO;
    private HomestayDAO homestayDAO;
    private RoomDAO roomDAO;

    // 无参构造
    public PaymentServiceImpl() {
        this.paymentDAO = new PaymentDAOImpl();
        this.reservationDAO = new ReservationDAOImpl();
        this.userDAO = new UserDAOImpl();
        this.homestayDAO = new HomestayDAOImpl();
        this.roomDAO = new RoomDAOImpl();
    }

    // 带参构造（用于测试）
    public PaymentServiceImpl(PaymentDAO paymentDAO, ReservationDAO reservationDAO,
                              UserDAO userDAO, HomestayDAO homestayDAO, RoomDAO roomDAO) {
        this.paymentDAO = paymentDAO;
        this.reservationDAO = reservationDAO;
        this.userDAO = userDAO;
        this.homestayDAO = homestayDAO;
        this.roomDAO = roomDAO;
    }

    /**
     * 插入数据,创建订单
     */
    @Override
    public int createPayment(Payment payment) {
        // 1. 生成支付单号
        payment.setPaymentNo(generatePaymentNo());

        // 2. 设置默认状态
        if (payment.getStatus() == null || payment.getStatus().isEmpty()) {
            payment.setStatus("PENDING");
        }

        // 3. 插入支付记录
        int result = paymentDAO.insert(payment);
        return result > 0 ? 1 : 0;
    }

    @Override
    public boolean paymentSuccess(int paymentId, String transactionId) {
        int result = paymentDAO.paymentSuccess(paymentId, transactionId);

        if (result > 0) {
            // 获取支付记录
            Payment payment = paymentDAO.selectById(paymentId);
            if (payment != null) {
                // 更新订单状态为已支付
                reservationDAO.updateStatus(payment.getReservationId(), "PAID");
            }
        }
        return result > 0;
    }

    @Override
    public boolean paymentFail(int paymentId) {
        int result = paymentDAO.updateStatus(paymentId, "FAILED");
        return result > 0;
    }

    @Override
    public boolean refund(int paymentId, String refundReason) {
        Payment payment = paymentDAO.selectById(paymentId);
        if (payment == null) {
            return false;
        }

        // 更新支付状态为已退款
        int result = paymentDAO.updateStatus(paymentId, "REFUNDED");

        if (result > 0) {
            // 更新订单状态
            reservationDAO.updateStatus(payment.getReservationId(), "CANCELLED");
        }
        return result > 0;
    }

    @Override
    public Payment getPaymentById(int paymentId) {
        return paymentDAO.selectById(paymentId);
    }

    @Override
    public Payment getPaymentByNo(String paymentNo) {
        return paymentDAO.selectByPaymentNo(paymentNo);
    }

    @Override
    public List<Payment> getPaymentsByReservationId(int reservationId) {
        return paymentDAO.selectByReservationId(reservationId);
    }

    @Override
    public double getTotalPaidByReservation(int reservationId) {
        return paymentDAO.getTotalPaidByReservation(reservationId);
    }

    @Override
    public boolean isReservationPaid(int reservationId) {
        double totalPaid = paymentDAO.getTotalPaidByReservation(reservationId);

        // 获取订单总金额
        Reservation reservation = reservationDAO.selectById(reservationId);
        if (reservation == null) {
            return false;
        }

        // 如果已支付金额 >= 订单总金额，则认为已支付
        return totalPaid >= reservation.getTotalPrice();
    }

    @Override
    public List<Payment> getPaymentsByStatus(String status, int pageNum, int pageSize) {
        List<Payment> allByStatus;
        if (status == null || status.isEmpty()) {
            allByStatus = paymentDAO.selectAll();  // 状态为空时查询所有
        } else {
            allByStatus = paymentDAO.selectByStatus(status);
        }

        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, allByStatus.size());

        if (start >= allByStatus.size()) {
            return new ArrayList<>();
        }

        return allByStatus.subList(start, end);
    }

    @Override
    public List<Payment> getPaymentsByMethod(String method, int pageNum, int pageSize) {
        List<Payment> allByMethod = paymentDAO.selectByMethod(method);

        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, allByMethod.size());

        if (start >= allByMethod.size()) {
            return new ArrayList<>();
        }

        return allByMethod.subList(start, end);
    }

    @Override
    public List<Payment> getPaymentsByDateRange(Date start, Date end, int pageNum, int pageSize) {
        List<Payment> allByDate = paymentDAO.selectByDateRange(start, end);

        int startIdx = (pageNum - 1) * pageSize;
        int endIdx = Math.min(startIdx + pageSize, allByDate.size());

        if (startIdx >= allByDate.size()) {
            return new ArrayList<>();
        }

        return allByDate.subList(startIdx, endIdx);
    }

    @Override
    public List<Payment> getPaymentsByUserId(int userId, int pageNum, int pageSize) {
        // 获取用户的所有订单
        List<Reservation> reservations = reservationDAO.selectByGuestId(userId);

        // 获取这些订单的所有支付记录
        List<Payment> allPayments = new ArrayList<>();
        for (Reservation r : reservations) {
            allPayments.addAll(paymentDAO.selectByReservationId(r.getReservationId()));
        }

        // 按时间排序
        allPayments.sort((p1, p2) -> {
            if (p1.getCreateTime() == null) return -1;
            if (p2.getCreateTime() == null) return 1;
            return p2.getCreateTime().compareTo(p1.getCreateTime());
        });

        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, allPayments.size());

        if (start >= allPayments.size()) {
            return new ArrayList<>();
        }

        return allPayments.subList(start, end);
    }

    @Override
    public List<Payment> getPaymentsByHomestayId(int homestayId, int pageNum, int pageSize) {
        // 获取民宿的所有房间
        List<Room> rooms = roomDAO.selectByHomestayId(homestayId);

        // 获取这些房间的所有订单
        List<Payment> allPayments = new ArrayList<>();
        for (Room room : rooms) {
            List<Reservation> reservations = reservationDAO.selectByRoomId(room.getRoomId());
            for (Reservation r : reservations) {
                allPayments.addAll(paymentDAO.selectByReservationId(r.getReservationId()));
            }
        }

        // 去重
        List<Payment> uniquePayments = new ArrayList<>();
        for (Payment p : allPayments) {
            if (!uniquePayments.contains(p)) {
                uniquePayments.add(p);
            }
        }

        // 按时间排序
        uniquePayments.sort((p1, p2) -> {
            if (p1.getCreateTime() == null) return -1;
            if (p2.getCreateTime() == null) return 1;
            return p2.getCreateTime().compareTo(p1.getCreateTime());
        });

        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, uniquePayments.size());

        if (start >= uniquePayments.size()) {
            return new ArrayList<>();
        }

        return uniquePayments.subList(start, end);
    }

    @Override
    public double getTodayIncome() {
        Date today = new Date();
        Date tomorrow = new Date(today.getTime() + 24 * 60 * 60 * 1000);

        List<Payment> todayPayments = paymentDAO.selectByDateRange(today, tomorrow);

        double total = 0;
        for (Payment p : todayPayments) {
            total += p.getAmount();
        }
        return total;
    }

    @Override
    public double getMonthIncome() {
        Date now = new Date();
        Date firstDay = getFirstDayOfMonth(now);
        Date lastDay = getLastDayOfMonth(now);

        List<Payment> monthPayments = paymentDAO.selectByDateRange(firstDay, lastDay);

        double total = 0;
        for (Payment p : monthPayments) {
            total += p.getAmount();
        }
        return total;
    }

    @Override
    public List<Object[]> getMonthlyIncomeByHomestay(int homestayId, int year) {
        return paymentDAO.getMonthlyIncome(homestayId, year);
    }

    @Override
    public List<Object[]> getMonthlyIncomeAll(int year) {
        List<Object[]> monthlyIncome = new ArrayList<>();
        
        // 初始化12个月，默认金额为0
        for (int i = 1; i <= 12; i++) {
            Object[] month = new Object[2];
            month[0] = i;
            month[1] = 0.0;
            monthlyIncome.add(month);
        }
        
        String sql = "SELECT MONTH(COALESCE(pay_time, create_time)) as month, " +
                     "SUM(amount) as total " +
                     "FROM payments " +
                     "WHERE status = 'SUCCESS' " +
                     "AND YEAR(COALESCE(pay_time, create_time)) = ? " +
                     "GROUP BY MONTH(COALESCE(pay_time, create_time)) " +
                     "ORDER BY month";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, year);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                int month = rs.getInt("month");
                double amount = rs.getDouble("total");
                if (month >= 1 && month <= 12) {
                    monthlyIncome.get(month - 1)[1] = amount;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.closeResultSet(rs);
            if (pstmt != null) DBUtil.closeStatement(pstmt);
            DBUtil.closeConnection();  // 修改这里：不要传参数
        }
        
        return monthlyIncome;
    }

    @Override
    public List<Object[]> getPaymentMethodStatistics(Date start, Date end) {
        List<Payment> payments = paymentDAO.selectByDateRange(start, end);

        // 统计各支付方式的金额
        java.util.Map<String, Double> methodTotal = new java.util.HashMap<>();

        for (Payment p : payments) {
            String method = p.getPaymentMethod();
            methodTotal.put(method, methodTotal.getOrDefault(method, 0.0) + p.getAmount());
        }

        List<Object[]> stats = new ArrayList<>();
        for (java.util.Map.Entry<String, Double> entry : methodTotal.entrySet()) {
            Object[] row = new Object[2];
            row[0] = entry.getKey();
            row[1] = entry.getValue();
            stats.add(row);
        }

        return stats;
    }

    @Override
    public double getTotalAmount() {
        String sql = "SELECT SUM(amount) as total FROM payments WHERE status = 'SUCCESS'";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
            return 0.0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        } finally {
            DBUtil.closeResultSet(rs);
            if (pstmt != null) DBUtil.closeStatement(pstmt);
            DBUtil.closeConnection();  // 修改这里：不要传参数
        }
    }

    @Override
    public long getSuccessCount() {
        String sql = "SELECT COUNT(*) FROM payments WHERE status = 'SUCCESS'";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.closeResultSet(rs);
            if (pstmt != null) DBUtil.closeStatement(pstmt);
            DBUtil.closeConnection();  // 修改这里：不要传参数
        }
    }

    // ==================== 私有工具方法 ====================

    private String generatePaymentNo() {
        return "PAY" + System.currentTimeMillis();
    }

    private Date getFirstDayOfMonth(Date date) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(date);
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private Date getLastDayOfMonth(Date date) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(date);
        cal.set(java.util.Calendar.DAY_OF_MONTH, cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
        cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
        cal.set(java.util.Calendar.MINUTE, 59);
        cal.set(java.util.Calendar.SECOND, 59);
        cal.set(java.util.Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
}