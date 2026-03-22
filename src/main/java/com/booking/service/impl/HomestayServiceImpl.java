package com.booking.service.impl;
import com.booking.dao.HomestayDAO;
import com.booking.dao.ReviewDAO;
import com.booking.dao.RoomDAO;
import com.booking.dao.impl.HomestayDAOImpl;
import com.booking.dao.impl.ReviewDAOImpl;
import com.booking.dao.impl.RoomDAOImpl;
import com.booking.model.Homestay;
import com.booking.model.Review;
import com.booking.service.HomestayService;

import java.util.ArrayList;
import java.util.List;
public class HomestayServiceImpl implements HomestayService{
    /**
     * 民宿业务逻辑实现类
     */

        private HomestayDAO homestayDAO;
        private ReviewDAO reviewDAO;
        private RoomDAO roomDAO;

        // 无参构造
        public HomestayServiceImpl() {
            this.homestayDAO = new HomestayDAOImpl();
            this.reviewDAO = new ReviewDAOImpl();
            this.roomDAO = new RoomDAOImpl();
        }

        // 带参构造（用于测试）
        public HomestayServiceImpl(HomestayDAO homestayDAO, ReviewDAO reviewDAO, RoomDAO roomDAO) {
            this.homestayDAO = homestayDAO;
            this.reviewDAO = reviewDAO;
            this.roomDAO = roomDAO;
        }
    @Override
    public int addHomestay(Homestay homestay) {
        // 设置默认值
        if (homestay.getRating() == 0) {
            homestay.setRating(0.0);
        }
        if (homestay.getReviewCount() == 0) {
            homestay.setReviewCount(0);
        }
        if (homestay.getStatus() == 0) {
            homestay.setStatus(1);  // 默认营业
        }

        int result = homestayDAO.insert(homestay);
        return result > 0 ? 1 : 0;
    }

    @Override
    public boolean updateHomestay(Homestay homestay) {
            int result = homestayDAO.update(homestay);
            return result>0;
    }

    @Override
    public boolean deleteHomestay(int homestayId) {
    Homestay homestay=homestayDAO.selectById(homestayId);
    if (homestay==null) {
        return false;
    }
    int result = homestayDAO.deleteById(homestayId);
    return result>0;
    }

    @Override
    public Homestay getHomestayById(int homestayId) {
       return homestayDAO.selectById(homestayId);
    }

    @Override
    public List<Homestay> getHomestaysByHostId(int hostId) {
        return homestayDAO.selectByHostId(hostId);
    }

    @Override
    public List<Homestay> getAllHomestays(int pageNum, int pageSize) {
        return homestayDAO.selectByPage(pageNum, pageSize);
    }

    @Override
    public List<Homestay> getHomestaysByCity(String city, int pageNum, int pageSize) {
      List<Homestay>allByCity=homestayDAO.selectByCity(city);
      int start=(pageNum-1)*pageSize;
      int end=Math.min(start + pageSize, allByCity.size());
      if(start>=allByCity.size()) {
          return new ArrayList<>();
      }
        return  allByCity.subList(start, end);
    }

    @Override
    public List<Homestay> searchHomestays(String keyword, int pageNum, int pageSize) {
        List<Homestay> allMatches = homestayDAO.search(keyword);
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, allMatches.size());
        if (start >= allMatches.size()) {
            return new ArrayList<>();
        }
        return allMatches.subList(start, end);
    }

    @Override
    public List<Homestay> getHomestaysByRating(double minRating, double maxRating, int pageNum, int pageSize) {
        List<Homestay> allMatches = homestayDAO.selectByRatingRange(minRating, maxRating);

        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, allMatches.size());

        if (start >= allMatches.size()) {
            return new ArrayList<>();
        }

        return allMatches.subList(start, end);
    }

    @Override
    public List<Homestay> getTopRatedHomestays(int limit) {
        List<Homestay> all = homestayDAO.selectAll();
        // 按评分排序
        all.sort((h1, h2) -> Double.compare(h2.getRating(), h1.getRating()));

        // 取前limit个
        return all.subList(0, Math.min(limit, all.size()));
    }

    @Override
    public void updateHomestayRating(int homestayId) {
        // 查询该民宿所有评价
        List<Review> reviews = reviewDAO.selectByHomestayId(homestayId);

        if (reviews.isEmpty()) {
            homestayDAO.updateRating(homestayId, 0.0);
            return;
        }

        // 计算平均分
        double sum = 0;
        for (Review r : reviews) {
            sum += r.getRating();
        }
        double avg = sum / reviews.size();

        // 更新民宿评分
        homestayDAO.updateRating(homestayId, avg);
    }

    @Override
    public long getTotalCount() {
        return homestayDAO.count();
    }

    @Override
    public List<Object[]> getCityStatistics() {
        List<Object[]> stats = new ArrayList<>();
        List<Homestay> all = homestayDAO.selectAll();
        // 按城市分组统计
        java.util.Map<String, Integer> cityCount = new java.util.HashMap<>();
        java.util.Map<String, Double> cityRating = new java.util.HashMap<>();

        for (Homestay h : all) {
            String city = h.getCity();
            cityCount.put(city, cityCount.getOrDefault(city, 0) + 1);
            cityRating.put(city, cityRating.getOrDefault(city, 0.0) + h.getRating());
        }

        // 计算平均分
        for (String city : cityCount.keySet()) {
            Object[] row = new Object[3];
            row[0] = city;
            row[1] = cityCount.get(city);
            row[2] = cityRating.get(city) / cityCount.get(city);
            stats.add(row);
        }

        return stats;
    }
}
