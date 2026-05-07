package com.booking.service.impl;

import com.booking.dao.CheckinRecordDAO;
import com.booking.dao.ReservationDAO;
import com.booking.dao.RoomDAO;
import com.booking.dao.HomestayDAO;
import com.booking.dao.UserDAO;
import com.booking.dao.impl.CheckinRecordDAOImpl;
import com.booking.dao.impl.ReservationDAOImpl;
import com.booking.dao.impl.RoomDAOImpl;
import com.booking.dao.impl.HomestayDAOImpl;
import com.booking.dao.impl.UserDAOImpl;
import com.booking.model.CheckinRecord;
import com.booking.model.Reservation;
import com.booking.model.Room;
import com.booking.model.Homestay;
import com.booking.model.User;
import com.booking.service.CheckinRecordService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 入住记录业务逻辑实现类
 */
public class CheckinRecordServiceImpl implements CheckinRecordService {

    private CheckinRecordDAO checkinRecordDAO;
    private ReservationDAO reservationDAO;
    private RoomDAO roomDAO;
    private HomestayDAO homestayDAO;
    private UserDAO userDAO;

    // 无参构造
    public CheckinRecordServiceImpl() {
        this.checkinRecordDAO = new CheckinRecordDAOImpl();
        this.reservationDAO = new ReservationDAOImpl();
        this.roomDAO = new RoomDAOImpl();
        this.homestayDAO = new HomestayDAOImpl();
        this.userDAO = new UserDAOImpl();
    }

    // 带参构造（用于测试）
    public CheckinRecordServiceImpl(CheckinRecordDAO checkinRecordDAO, ReservationDAO reservationDAO,
                                    RoomDAO roomDAO, HomestayDAO homestayDAO, UserDAO userDAO) {
        this.checkinRecordDAO = checkinRecordDAO;
        this.reservationDAO = reservationDAO;
        this.roomDAO = roomDAO;
        this.homestayDAO = homestayDAO;
        this.userDAO = userDAO;
    }

    @Override
    public int checkIn(int reservationId, double deposit, int roomKeys, String remarks) {
        // 1. 获取订单信息
        Reservation reservation = reservationDAO.selectById(reservationId);
        if (reservation == null) {
            return 0;  // 订单不存在
        }

        // 2. 检查订单状态（只有PAID和CONFIRMED可以入住）
        String status = reservation.getStatus();
        if (!"PAID".equals(status) && !"CONFIRMED".equals(status)) {
            return -1;  // 订单状态错误
        }

        // 3. 检查是否已有入住记录
        CheckinRecord existing = checkinRecordDAO.selectByReservationId(reservationId);
        if (existing != null) {
            return -1;  // 已办理入住
        }

        // 4. 创建入住记录
        CheckinRecord record = new CheckinRecord();
        record.setReservationId(reservationId);
        record.setDeposit(deposit);
        record.setRoomKeysGiven(roomKeys);
        record.setRemarks(remarks);
        record.setActualCheckIn(new Date());  // 设置实际入住时间为当前时间

        int result = checkinRecordDAO.insert(record);

        if (result > 0) {
            // 5. 更新订单状态为已入住
            reservationDAO.checkIn(reservationId);

            // 6. 更新房间状态（如果需要）
            roomDAO.updateStatus(reservation.getRoomId(), "BOOKED");
        }

        return result > 0 ? 1 : 0;
    }

    @Override
    public boolean checkOut(int recordId, double depositReturn) {
        // 1. 获取入住记录
        CheckinRecord record = checkinRecordDAO.selectById(recordId);
        if (record == null) {
            return false;
        }

        // 2. 获取订单信息
        Reservation reservation = reservationDAO.selectById(record.getReservationId());
        if (reservation == null) {
            return false;
        }

        // 3. 更新退房信息
        int result = checkinRecordDAO.updateCheckOut(recordId, depositReturn);

        if (result > 0) {
            // 4. 更新订单状态为已完成
            reservationDAO.checkOut(reservation.getReservationId());

            // 5. 释放房间
            roomDAO.updateStatus(reservation.getRoomId(), "AVAILABLE");
        }

        return result > 0;
    }
    /**
     * 根据ID查询
     */
    @Override
    public CheckinRecord getRecordById(int recordId) {
        return checkinRecordDAO.selectById(recordId);
    }
    /**
     * 根据预订ID查询入住记录（一对一关系）
     */
    @Override
    public CheckinRecord getRecordByReservationId(int reservationId) {
        return checkinRecordDAO.selectByReservationId(reservationId);
    }
    /**
     * 根据客人ID查询入住记录
     */
    @Override
    public List<CheckinRecord> getRecordsByGuestId(int guestId, int pageNum, int pageSize) {
        List<CheckinRecord> allRecords = checkinRecordDAO.selectByGuestId(guestId);

        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, allRecords.size());

        if (start >= allRecords.size()) {
            return new ArrayList<>();
        }

        return allRecords.subList(start, end);
    }
    /**
     * 根据民宿ID查询入住记录
     */
    @Override
    public List<CheckinRecord> getRecordsByHomestayId(int homestayId, int pageNum, int pageSize) {
        List<CheckinRecord> allRecords = checkinRecordDAO.selectByHomestayId(homestayId);

        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, allRecords.size());

        if (start >= allRecords.size()) {
            return new ArrayList<>();
        }

        return allRecords.subList(start, end);
    }
   @Override
public List<CheckinRecord> getTodayCheckIn() {

    List<CheckinRecord> result = new ArrayList<>();

    // 1. 获取今天所有订单（核心！）
    List<Reservation> todayReservations = reservationDAO.selectByCheckInDate(new Date());

    for (Reservation reservation : todayReservations) {

        // 2. 查是否已有入住记录
        CheckinRecord record = checkinRecordDAO.selectByReservationId(reservation.getReservationId());

        if (record == null) {
            // ===== 未入住（创建临时记录）=====
            record = new CheckinRecord();
            record.setReservationId(reservation.getReservationId());
            record.setReservation(reservation);
        } else {
            // ===== 已入住（补充 reservation）=====
            record.setReservation(reservation);
        }

        // ===== 统一补全信息（关键！）=====

        // 客人
        User guest = userDAO.selectById(reservation.getGuestId());
        record.setGuest(guest);

        // 房间
        Room room = roomDAO.selectById(reservation.getRoomId());
        if (room != null) {
            record.setRoom(room);

            // 民宿
            Homestay homestay = homestayDAO.selectById(room.getHomestayId());
            record.setHomestay(homestay);
        }

        result.add(record);
    }

    return result;
}
    /**
     * 查询今日退房
     */
    @Override
    public List<CheckinRecord> getTodayCheckOut() {
        return checkinRecordDAO.selectTodayCheckOut();
    }
    /**
     * 范围时间记录
     */
    @Override
    public List<CheckinRecord> getRecordsByDateRange(Date start, Date end, int pageNum, int pageSize) {
        // 由于DAO层没有这个方法，我们手动筛选
        List<CheckinRecord> allRecords = checkinRecordDAO.selectAll();
        List<CheckinRecord> rangeRecords = new ArrayList<>();

        for (CheckinRecord r : allRecords) {
            if (r.getActualCheckIn() != null &&
                    r.getActualCheckIn().after(start) &&
                    r.getActualCheckIn().before(end)) {
                rangeRecords.add(r);
            }
        }

        int startIdx = (pageNum - 1) * pageSize;
        int endIdx = Math.min(startIdx + pageSize, rangeRecords.size());

        if (startIdx >= rangeRecords.size()) {
            return new ArrayList<>();
        }

        return rangeRecords.subList(startIdx, endIdx);
    }
    /**
     * 查询今日入住数量
     */
    @Override
    public int getTodayCheckInCount() {
        return checkinRecordDAO.selectTodayCheckIn().size();
    }
    /**
     * 查询今日退房数量
     */
    @Override
    public int getTodayCheckOutCount() {
        return checkinRecordDAO.selectTodayCheckOut().size();
    }
    /**
     * 查询所有在住名单
     */
    @Override
    public int getCurrentOccupancy() {
        // 查询所有在住的订单（状态为CHECKED_IN）
        List<Reservation> checkedIn = reservationDAO.selectByStatus("CHECKED_IN");
        return checkedIn.size();
    }
    /**
     * 查询入住率
     */
    @Override
    public double getOccupancyRate(int homestayId, Date date) {
        // 1. 获取民宿的总房间数
        List<Room> rooms = roomDAO.selectByHomestayId(homestayId);
        int totalRooms = rooms.size();
        if (totalRooms == 0) {
            return 0.0;
        }

        // 2. 获取当天入住的房间数
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date start = cal.getTime();

        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date end = cal.getTime();

        // 3. 统计当天入住的记录
        List<CheckinRecord> todayCheckIn = checkinRecordDAO.selectTodayCheckIn();
        int occupiedRooms = 0;

        for (CheckinRecord r : todayCheckIn) {
            Reservation res = reservationDAO.selectById(r.getReservationId());
            if (res != null) {
                Room room = roomDAO.selectById(res.getRoomId());
                if (room != null && room.getHomestayId() == homestayId) {
                    occupiedRooms++;
                }
            }
        }

        return (double) occupiedRooms / totalRooms * 100;
    }
    /**
     * 查询详细记录
     */
    @Override
    public CheckinRecord getRecordDetail(int recordId) {
        CheckinRecord record = checkinRecordDAO.selectById(recordId);

        if (record != null) {
            // 获取关联的订单信息
            Reservation reservation = reservationDAO.selectById(record.getReservationId());
            if (reservation != null) {
                // 设置订单信息
                record.setReservation(reservation);

                // 获取客人信息
                User guest = userDAO.selectById(reservation.getGuestId());
                record.setGuest(guest);

                // 获取房间信息
                Room room = roomDAO.selectById(reservation.getRoomId());
                if (room != null) {
                    record.setRoom(room);

                    // 获取民宿信息
                    Homestay homestay = homestayDAO.selectById(room.getHomestayId());
                    record.setHomestay(homestay);
                }
            }
        }

        return record;
    }

    @Override
    public int deleteRecord(int recordId) {
        return checkinRecordDAO.deleteById(recordId);
    }

    @Override
    public List<CheckinRecord> getCheckedInRecords() {
        // 查询所有已入住但未退房的记录
        List<CheckinRecord> records = checkinRecordDAO.selectCheckedInRecords();
        List<CheckinRecord> result = new ArrayList<>();

        for (CheckinRecord record : records) {
            // 获取预订信息
            Reservation reservation = reservationDAO.selectById(record.getReservationId());
            if (reservation != null) {
                record.setReservation(reservation);

                // 获取客人信息
                User guest = userDAO.selectById(reservation.getGuestId());
                record.setGuest(guest);

                // 获取房间信息
                Room room = roomDAO.selectById(reservation.getRoomId());
                if (room != null) {
                    record.setRoom(room);

                    // 获取民宿信息
                    Homestay homestay = homestayDAO.selectById(room.getHomestayId());
                    record.setHomestay(homestay);
                }
            }
            result.add(record);
        }

        return result;
    }
}