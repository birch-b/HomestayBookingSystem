package com.booking;

import com.booking.dao.PricingRuleDAO;
import com.booking.dao.impl.PricingRuleDAOImpl;
import com.booking.model.PricingRule;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TestPricingRuleDAO {
    public static void main(String[] args) {
        System.out.println("========== 测试定价规则DAO ==========");

        PricingRuleDAO ruleDAO = new PricingRuleDAOImpl();

        // 1. 当前规则总数
        System.out.println("\n=== 1. 当前规则总数 ===");
        long count = ruleDAO.count();
        System.out.println("规则总数: " + count);

        // 2. 插入测试数据
        System.out.println("\n=== 2. 插入定价规则 ===");

        // 规则1：全局周末规则
        PricingRule rule1 = new PricingRule();
        rule1.setHomestayId(null);  // 全局规则
        rule1.setRuleName("周末通用");
        rule1.setRuleType("WEEKEND");
        rule1.setWeekdays("6,7");  // 周六周日
        rule1.setDiscountType("PERCENT");
        rule1.setDiscountValue(0.8);  // 8折
        rule1.setPriority(1);
        rule1.setStatus(1);

        int result1 = ruleDAO.insert(rule1);
        System.out.println("插入全局周末规则: " + (result1 > 0 ? "✅ 成功, ID=" + rule1.getRuleId() : "❌ 失败"));

        // 规则2：民宿1节假日规则
        PricingRule rule2 = new PricingRule();
        rule2.setHomestayId(1);
        rule2.setRuleName("五一特惠");
        rule2.setRuleType("HOLIDAY");

        Calendar cal = Calendar.getInstance();
        cal.set(2026, Calendar.MAY, 1);
        rule2.setStartDate(cal.getTime());
        cal.set(2026, Calendar.MAY, 5);
        rule2.setEndDate(cal.getTime());

        rule2.setDiscountType("FIXED");
        rule2.setDiscountValue(-100);  // 减100元
        rule2.setPriority(2);
        rule2.setStatus(1);

        int result2 = ruleDAO.insert(rule2);
        System.out.println("插入民宿1五一规则: " + (result2 > 0 ? "✅ 成功, ID=" + rule2.getRuleId() : "❌ 失败"));

        // 规则3：民宿2早鸟优惠
        PricingRule rule3 = new PricingRule();
        rule3.setHomestayId(2);
        rule3.setRuleName("早鸟优惠");
        rule3.setRuleType("EARLY_BIRD");
        rule3.setDiscountType("PERCENT");
        rule3.setDiscountValue(0.9);  // 9折
        rule3.setPriority(3);
        rule3.setStatus(1);

        int result3 = ruleDAO.insert(rule3);
        System.out.println("插入民宿2早鸟优惠: " + (result3 > 0 ? "✅ 成功, ID=" + rule3.getRuleId() : "❌ 失败"));

        // 3. 查询所有规则
        System.out.println("\n=== 3. 查询所有规则 ===");
        List<PricingRule> allList = ruleDAO.selectAll();
        System.out.println("规则总数: " + allList.size());
        for (PricingRule r : allList) {
            System.out.println(r.getRuleId() + " | " + r.getRuleName() + " | " +
                    (r.getHomestayId() == null ? "全局" : "民宿" + r.getHomestayId()) + " | " +
                    r.getRuleType() + " | 优先级:" + r.getPriority());
        }

        // 4. 根据民宿ID查询
        System.out.println("\n=== 4. 查询民宿1的规则 ===");
        List<PricingRule> homestayList = ruleDAO.selectByHomestayId(1);
        System.out.println("找到 " + homestayList.size() + " 条");
        for (PricingRule r : homestayList) {
            System.out.println("  " + r.getRuleId() + " | " + r.getRuleName() + " | " + r.getRuleType());
        }

        // 5. 查询全局规则
        System.out.println("\n=== 5. 查询全局规则 ===");
        List<PricingRule> globalList = ruleDAO.selectGlobalRules();
        System.out.println("全局规则: " + globalList.size() + " 条");

        // 6. 根据类型查询
        System.out.println("\n=== 6. 查询WEEKEND类型规则 ===");
        List<PricingRule> typeList = ruleDAO.selectByType("WEEKEND");
        System.out.println("找到 " + typeList.size() + " 条");

        // 7. 查询当前有效的规则
        System.out.println("\n=== 7. 查询当前有效规则 ===");
        List<PricingRule> activeList = ruleDAO.selectActiveRules(new Date());
        System.out.println("有效规则: " + activeList.size() + " 条");

        // 8. 查询民宿1当前有效规则
        System.out.println("\n=== 8. 查询民宿1当前有效规则 ===");
        List<PricingRule> activeHomestayList = ruleDAO.selectActiveRulesByHomestay(1, new Date());
        System.out.println("找到 " + activeHomestayList.size() + " 条");

        // 9. 按优先级排序查询
        System.out.println("\n=== 9. 按优先级排序 ===");
        List<PricingRule> priorityList = ruleDAO.selectOrderByPriority();
        for (PricingRule r : priorityList) {
            System.out.println(r.getRuleId() + " | " + r.getRuleName() + " | 优先级:" + r.getPriority());
        }

        // 10. 测试更新状态
        if (allList.size() > 0) {
            int firstId = allList.get(0).getRuleId();
            System.out.println("\n=== 10. 禁用规则 ID=" + firstId + " ===");
            int updateResult = ruleDAO.updateStatus(firstId, 0);
            System.out.println("更新结果: " + (updateResult > 0 ? "✅ 成功" : "❌ 失败"));

            // 验证
            PricingRule updated = ruleDAO.selectById(firstId);
            if (updated != null) {
                System.out.println("规则状态: " + (updated.getStatus() == 1 ? "启用" : "禁用"));
            }
        }

        // 11. 测试冲突检测
        System.out.println("\n=== 11. 测试冲突检测 ===");
        PricingRule newRule = new PricingRule();
        newRule.setRuleId(0);  // 新规则
        newRule.setHomestayId(1);
        newRule.setRuleType("HOLIDAY");

        cal.set(2026, Calendar.MAY, 1);
        newRule.setStartDate(cal.getTime());
        cal.set(2026, Calendar.MAY, 3);
        newRule.setEndDate(cal.getTime());

        boolean hasConflict = ruleDAO.hasConflict(newRule);
        System.out.println("与民宿1的五一规则是否冲突: " + (hasConflict ? "✅ 有冲突" : "❌ 无冲突"));

        // 12. 根据民宿和类型查询
        System.out.println("\n=== 12. 查询民宿1的HOLIDAY规则 ===");
        List<PricingRule> homestayTypeList = ruleDAO.selectByHomestayAndType(1, "HOLIDAY");
        System.out.println("找到 " + homestayTypeList.size() + " 条");

        System.out.println("\n========== 测试完成 ==========");
    }
}