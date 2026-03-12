package com.booking.dao;

import java.util.List;

/**
 * 基础DAO接口，定义通用CRUD操作
 */
public interface BaseDAO<T> {

    /**
     * 插入数据
     */
    int insert(T entity);

    /**
     * 根据ID删除
     */
    int deleteById(int id);

    /**
     * 更新数据
     */
    int update(T entity);

    /**
     * 根据ID查询
     */
    T selectById(int id);

    /**
     * 查询所有
     */
    List<T> selectAll();

    /**
     * 分页查询
     */
    List<T> selectByPage(int pageNum, int pageSize);

    /**
     * 统计总数
     */
    long count();
}