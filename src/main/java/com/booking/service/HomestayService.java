package com.booking.service;

import com.booking.model.Homestay;
import java.util.List;

/**
 * 民宿业务逻辑接口
 */
public interface HomestayService {

    /**
     * 新增民宿
     * @return 1成功，0失败
     */
    int addHomestay(Homestay homestay);

    /**
     * 更新民宿信息
     */
    boolean updateHomestay(Homestay homestay);

    /**
     * 删除民宿（软删除）
     */
    boolean deleteHomestay(int homestayId);

    /**
     * 根据ID查询民宿
     */
    Homestay getHomestayById(int homestayId);

    /**
     * 查询民宿主的所有民宿
     */
    List<Homestay> getHomestaysByHostId(int hostId);

    /**
     * 查询所有民宿（分页）
     */
    List<Homestay> getAllHomestays(int pageNum, int pageSize);

    /**
     * 按城市查询民宿
     */
    List<Homestay> getHomestaysByCity(String city, int pageNum, int pageSize);

    /**
     * 搜索民宿（按名称、地址、城市）
     */
    List<Homestay> searchHomestays(String keyword, int pageNum, int pageSize);

    /**
     * 按评分范围查询
     */
    List<Homestay> getHomestaysByRating(double minRating, double maxRating, int pageNum, int pageSize);

    /**
     * 获取评分最高的民宿
     */
    List<Homestay> getTopRatedHomestays(int limit);

    /**
     * 更新民宿评分（评价后自动调用）
     */
    void updateHomestayRating(int homestayId);

    /**
     * 统计民宿总数
     */
    long getTotalCount();

    /**
     * 统计各城市民宿数量
     */
    List<Object[]> getCityStatistics();
}