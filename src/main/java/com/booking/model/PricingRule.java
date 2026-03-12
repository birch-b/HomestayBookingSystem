package com.booking.model;

import java.util.Date;

/**
 * 定价规则实体类
 * 对应数据库表：pricing_rules
 */
public class PricingRule {

    private int ruleId;                // 规则ID
    private Integer homestayId;        // 民宿ID（可能为null）
    private String ruleName;           // 规则名称
    private String ruleType;           // 规则类型：WEEKEND,HOLIDAY,SEASON,EARLY_BIRD,LAST_MINUTE
    private Date startDate;            // 适用开始日期
    private Date endDate;              // 适用结束日期
    private String weekdays;           // 适用星期
    private String discountType;       // 折扣类型：PERCENT,FIXED
    private double discountValue;      // 折扣值
    private int priority;              // 优先级
    private int status;                // 状态：1启用 0禁用
    private Date createTime;           // 创建时间

    // 无参构造
    public PricingRule() {
    }

    // Getter和Setter
    public int getRuleId() {
        return ruleId;
    }

    public void setRuleId(int ruleId) {
        this.ruleId = ruleId;
    }

    public Integer getHomestayId() {
        return homestayId;
    }

    public void setHomestayId(Integer homestayId) {
        this.homestayId = homestayId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getWeekdays() {
        return weekdays;
    }

    public void setWeekdays(String weekdays) {
        this.weekdays = weekdays;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(double discountValue) {
        this.discountValue = discountValue;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}