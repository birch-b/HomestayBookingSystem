package com.booking.dao;

import com.booking.model.Homestay;
import java.util.List;

/**
 * 民宿数据访问接口
 */
public interface HomestayDAO extends BaseDAO<Homestay> {

    /**
     * 根据民宿主ID查询
     */
    List<Homestay> selectByHostId(int hostId);

    /**
     * 根据城市查询
     */
    List<Homestay> selectByCity(String city);

    /**
     * 模糊搜索民宿（按名称、地址）
     */
    List<Homestay> search(String keyword);

    /**
     * 根据评分范围查询
     */
    List<Homestay> selectByRatingRange(double minRating, double maxRating);

    /**
     * 更新民宿评分
     */
    int updateRating(int homestayId, double newRating);
}