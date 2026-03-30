import com.booking.service.ReservationService;
import com.booking.service.impl.ReservationServiceImpl;
import com.booking.model.Reservation;
import java.util.Date;

public class test_order32 {
    public static void main(String[] args) {
        // 创建订单服务实例
        ReservationService reservationService = new ReservationServiceImpl();
        
        // 查询订单ID=32的详细信息
        Reservation reservation = reservationService.getReservationDetail(32);
        
        if (reservation != null) {
            System.out.println("订单ID: " + reservation.getReservationId());
            System.out.println("订单号: " + reservation.getReservationNo());
            System.out.println("状态: " + reservation.getStatus());
            System.out.println("创建时间: " + reservation.getCreateTime());
            System.out.println("入住时间: " + reservation.getCheckInDate());
            System.out.println("退房时间: " + reservation.getCheckOutDate());
            System.out.println("客人ID: " + reservation.getGuestId());
            System.out.println("房间ID: " + reservation.getRoomId());
        } else {
            System.out.println("订单ID=32不存在");
        }
        
        // 获取当前日期
        Date today = new Date();
        System.out.println("当前日期: " + today);
    }
}