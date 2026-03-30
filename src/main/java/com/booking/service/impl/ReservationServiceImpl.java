package com.booking.service.impl;

import com.booking.dao.*;
import com.booking.dao.impl.*;
import com.booking.model.*;
import com.booking.service.ReservationService;
import com.booking.util.DBUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReservationServiceImpl implements ReservationService{
    private ReservationDAO reservationDAO;
    private RoomDAO roomDAO;
    private PaymentDAO paymentDAO;
    private HomestayDAO homestayDAO;
    private PricingRuleDAO pricingRuleDAO;
    //无参：真实运行时用，创建真实的DAO对象
    public ReservationServiceImpl(){
        reservationDAO = new ReservationDAOImpl();
        roomDAO = new RoomDAOImpl();
        paymentDAO = new PaymentDAOImpl();
        homestayDAO= new HomestayDAOImpl();
        pricingRuleDAO = new PricingRuleDAOImpl();
    }
    //带参：单元测试时用，可以传入模拟对象（Mock)
    public ReservationServiceImpl(ReservationDAO reservationDAO,RoomDAO roomDAO,
                                  PaymentDAO paymentDAO, HomestayDAO homestayDAO,
                                  PricingRuleDAO pricingRuleDAO){
        this.reservationDAO = reservationDAO;
        this.roomDAO = roomDAO;
        this.paymentDAO = paymentDAO;
        this.homestayDAO = homestayDAO;
        this.pricingRuleDAO = pricingRuleDAO;
    }
    @Override
    public int createReservation(Reservation reservation, Payment payment) {
        Connection conn = null;

        try{
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);//开启事务
            // 1. 检查房间是否可用
            boolean available=reservationDAO.checkRoomAvailable(
                    reservation.getRoomId(),
                    reservation.getCheckInDate(),
                    reservation.getCheckOutDate()
            );
            if (!available){
                conn.rollback();
                return -1; // 房间不可用
            }
            // 2. 获取房间信息
            Room room=roomDAO.selectById(reservation.getRoomId());
            if (room==null){
                conn.rollback();
                return 0;
            }
            // 3. 计算总价（如果没有设置的话）
            if(reservation.getTotalPrice()==0){
                double totalPrice=calculateTotalPrice(
                        reservation.getRoomId(),
                        reservation.getCheckInDate(),
                        reservation.getCheckOutDate(),
                        reservation.getGuestsCount()
                );
                reservation.setTotalPrice(totalPrice);
            }
            // 4. 生成订单号（如果DAO没生成）
            if (reservation.getReservationNo()==null){
                reservation.setReservationNo(generateReservationNo());
            }
            // 5. 设置默认状态
            reservation.setStatus("PENDING");
            // 6. 插入订单
            int orderResult = reservationDAO.insert(reservation);
            if (orderResult <= 0) {
                conn.rollback();
                return 0;
            }
            // 7. 更新房间状态
            roomDAO.updateStatus(reservation.getRoomId(), "BOOKED");
            // 8. 如果有支付信息，插入支付记录
            if (payment != null) {
                payment.setReservationId(reservation.getReservationId());
                payment.setPaymentNo(generatePaymentNo());
                payment.setStatus("PENDING");
                paymentDAO.insert(payment);
            }

            conn.commit();  // 提交事务
            return 1;

        }catch (SQLException e){
            try {
                if (conn!=null){
                    conn.rollback();
                }
            }catch (SQLException e2){
                e2.printStackTrace();
            }
            e.printStackTrace();
            return 0;
        }
        finally {
                DBUtil.closeConnection();
        }
    }

    @Override
    public boolean cancelReservation(int reservationId) {
        // 1. 获取订单信息
        Reservation reservation = reservationDAO.selectById(reservationId);
        if (reservation == null) {
            return false;
        }

        // 2. 检查是否可以取消（只有PENDING和PAID可以取消）
        String status = reservation.getStatus();
        if (!"PENDING".equals(status) && !"PAID".equals(status)) {
            return false;
        }

        // 3. 调用DAO的取消方法（DAO内部处理事务）
        int result = reservationDAO.cancelReservation(reservationId);
        return result > 0;
    }

    @Override
    public boolean paymentSuccess(int reservationId, String transactionId) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // 1. 查询订单的支付记录
            List<Payment> payments = paymentDAO.selectByReservationId(reservationId);
            if (payments.isEmpty()) {
                return false;
            }

            // 2. 更新支付状态
            Payment payment = payments.get(0);
            int result = paymentDAO.paymentSuccess(payment.getPaymentId(), transactionId);

            if (result <= 0) {
                conn.rollback();
                return false;
            }

            // 3. 更新订单状态为PAID
            reservationDAO.updateStatus(reservationId, "PAID");

            conn.commit();
            return true;

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.closeConnection();
        }
    }


    @Override
    public boolean checkIn(int reservationId, double deposit) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // 1. 获取订单信息
            Reservation reservation = reservationDAO.selectById(reservationId);
            if (reservation == null) {
                return false;
            }

            // 2. 检查状态（只有PAID和CONFIRMED可以入住）
            String status = reservation.getStatus();
            if (!"PAID".equals(status) && !"CONFIRMED".equals(status)) {
                return false;
            }

            // 3. 更新订单状态为CHECKED_IN
            int result = reservationDAO.checkIn(reservationId);
            if (result <= 0) {
                conn.rollback();
                return false;
            }

            // 4. 创建入住记录
            CheckinRecord record = new CheckinRecord();
            record.setReservationId(reservationId);
            record.setDeposit(deposit);
            record.setRoomKeysGiven(1);
            record.setActualCheckIn(new Date());  // 设置实际入住时间为当前时间

            CheckinRecordDAO checkinDAO = new CheckinRecordDAOImpl();
            checkinDAO.insert(record);

            conn.commit();
            return true;

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.closeConnection();
        }
    }

    @Override
    public boolean checkOut(int reservationId, double depositReturn) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // 1. 获取订单信息
            Reservation reservation = reservationDAO.selectById(reservationId);
            if (reservation == null) {
                return false;
            }

            // 2. 检查状态（只有CHECKED_IN可以退房）
            if (!"CHECKED_IN".equals(reservation.getStatus())) {
                return false;
            }

            // 3. 更新订单状态为COMPLETED
            int result = reservationDAO.checkOut(reservationId);
            if (result <= 0) {
                conn.rollback();
                return false;
            }

            // 4. 更新入住记录（退押金）
            CheckinRecordDAO checkinDAO = new CheckinRecordDAOImpl();
            CheckinRecord record = checkinDAO.selectByReservationId(reservationId);
            if (record != null) {
                checkinDAO.updateCheckOut(record.getRecordId(), depositReturn);
            }

            // 5. 释放房间
            roomDAO.updateStatus(reservation.getRoomId(), "AVAILABLE");

            conn.commit();
            return true;

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.closeConnection();
        }
    }

    @Override
    public Reservation getReservationDetail(int reservationId) {
        Reservation reservation = reservationDAO.selectById(reservationId);
        if (reservation != null) {
            // 可以在这里加载关联信息，比如房间详情、民宿详情等
            Room room = roomDAO.selectById(reservation.getRoomId());
            if (room != null) {
                // 可以设置到reservation中（需要在Reservation类中加字段）
                 reservation.setRoom(room);
            }
        }
        return reservation;
    }

    @Override
    public List<Reservation> getUserReservations(int userId, int pageNum, int pageSize) {
        // 获取所有数据，然后在内存中进行分页
        List<Reservation> allReservations = reservationDAO.selectByGuestId(userId);
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, allReservations.size());
        if (startIndex >= allReservations.size()) {
            return new ArrayList<>();
        }
        return allReservations.subList(startIndex, endIndex);
    }

    @Override
    public List<Reservation> getHomestayReservations(int homestayId, int pageNum, int pageSize) {
        // 获取所有数据，然后在内存中进行分页
        List<Reservation> allReservations = reservationDAO.selectByHomestayId(homestayId);
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, allReservations.size());
        if (startIndex >= allReservations.size()) {
            return new ArrayList<>();
        }
        return allReservations.subList(startIndex, endIndex);
    }

@Override
public List<Reservation> searchReservations(
        Integer userId,
        String keyword,
        String status,
        Date start,
        Date end,
        int pageNum,
        int pageSize) {

    // 统一处理空字符串
    if (keyword != null && keyword.trim().isEmpty()) {
        keyword = null;
    }
    if (status != null && status.trim().isEmpty()) {
        status = null;
    }

    List<Reservation> allReservations;

    // ✅ 如果传了userId → 只查该用户（游客）
    if (userId != null) {
        allReservations = reservationDAO.selectByGuestId(userId);
    } else {
        // 管理员 or 其他情况
        allReservations = reservationDAO.selectAll();
    }

    List<Reservation> filtered = new ArrayList<>();

    for (Reservation r : allReservations) {

        // 状态过滤
        if (status != null && !status.equals(r.getStatus())) continue;

        // 日期过滤
        if (start != null && r.getCheckInDate().before(start)) continue;
        if (end != null && r.getCheckOutDate().after(end)) continue;

        filtered.add(r);
    }

    // 分页
    int startIndex = (pageNum - 1) * pageSize;
    int endIndex = Math.min(startIndex + pageSize, filtered.size());

    if (startIndex >= filtered.size()) {
        return new ArrayList<>();
    }

    return filtered.subList(startIndex, endIndex);
}
    @Override
    public List<Reservation> searchReservationsByDateRange(Integer homestayId, String status, Date startDate, Date endDate) {
        // 实现按日期范围查询订单的逻辑
        // 可以复用 DAO 层的方法
        List<Reservation> allReservations;
        
        if (homestayId != null && homestayId > 0) {
            // 如果指定了民宿ID，查询该民宿的订单
            allReservations = reservationDAO.selectByHomestayId(homestayId);
        } else {
            // 否则查询所有订单
            allReservations = reservationDAO.selectAll();
        }
        
        // 过滤符合条件的订单
        List<Reservation> filteredReservations = new ArrayList<>();
        for (Reservation r : allReservations) {
            // 状态过滤
            if (status != null && !status.trim().isEmpty() && !status.equals(r.getStatus())) {
                continue;
            }
            
            // 日期范围过滤
            Date timeToUse = r.getCheckOutDate() != null ? r.getCheckOutDate() : r.getCreateTime();
            if (timeToUse != null) {
                if (startDate != null && timeToUse.before(startDate)) {
                    continue;
                }
                if (endDate != null && timeToUse.after(endDate)) {
                    continue;
                }
            }
            
            filteredReservations.add(r);
        }
        
        return filteredReservations;
    }

    @Override
    public double calculateTotalPrice(int roomId, Date checkIn, Date checkOut, int guestsCount) {
        // 1. 获取房间信息
        Room room = roomDAO.selectById(roomId);
        if (room == null) {
            return 0;
        }
        // 2. 计算入住天数
        long diff = checkOut.getTime() - checkIn.getTime();
        int days = (int) (diff / (1000 * 60 * 60 * 24));
        if (days <= 0) {
            days = 1;
        }
        //3.基础价格
        double basePrice = days * room.getPrice();
        // 4. 获取民宿的定价规则
        Homestay homestay = homestayDAO.selectById(room.getHomestayId());
        if (homestay != null) {
            List<PricingRule> rules = pricingRuleDAO.selectActiveRulesByHomestay(
                    homestay.getHomestayId(), new Date()
            );
            // 5. 应用定价规则（简单实现：取优先级最高的）
            if (!rules.isEmpty()) {
                PricingRule rule = rules.get(0);
                if ("PERCENT".equals(rule.getDiscountType())) {
                    basePrice *= rule.getDiscountValue();
                } else if ("FIXED".equals(rule.getDiscountType())) {
                    basePrice += rule.getDiscountValue();
                }
            }
            }
            return basePrice;
        
    }

    /**
     * 更新订单状态
     * @param reservationId 订单ID
     * @param status 新状态
     * @return 是否成功
     */
    public boolean updateReservationStatus(int reservationId, String status) {
        int result = reservationDAO.updateStatus(reservationId, status);
        return result > 0;
    }
    // ==================== 工具方法 ====================
//获取订单号
    private String generateReservationNo() {
        return String.format("%tY%<tm%<td%06d",
                new Date(),
                (int) (Math.random() * 1000000));
    }
    private String generatePaymentNo() {
        return "PAY" + System.currentTimeMillis();
    }
    @Override
public int countTodayOrders(int homestayId) {
    return reservationDAO.countTodayOrders(homestayId);
}
}