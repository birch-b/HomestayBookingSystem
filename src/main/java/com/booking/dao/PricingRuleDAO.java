package com.booking.dao;

import com.booking.model.PricingRule;
import java.util.List;
import java.util.Date;

/**
 * 定价规则数据访问接口
 */
public interface PricingRuleDAO extends BaseDAO<PricingRule> {

    /**
     * 根据民宿ID查询定价规则
     */
    List<PricingRule> selectByHomestayId(int homestayId);

    /**
     * 查询全局规则（homestay_id为NULL的规则）
     */
    List<PricingRule> selectGlobalRules();

    /**
     * 根据规则类型查询
     */
    List<PricingRule> selectByType(String ruleType);

    /**
     * 查询当前有效的规则（根据日期判断）
     */
    List<PricingRule> selectActiveRules(Date date);

    /**
     * 查询某民宿在指定日期有效的规则
     */
    List<PricingRule> selectActiveRulesByHomestay(int homestayId, Date date);

    /**
     * 根据优先级排序查询
     */
    List<PricingRule> selectOrderByPriority();

    /**
     * 启用/禁用规则
     */
    int updateStatus(int ruleId, int status);

    /**
     * 检查规则是否冲突（同一民宿同一类型日期重叠）
     */
    boolean hasConflict(PricingRule rule);

    /**
     * 根据民宿和类型查询
     */
    List<PricingRule> selectByHomestayAndType(int homestayId, String ruleType);
}