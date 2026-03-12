package com.booking.dao.impl;

import com.booking.dao.PricingRuleDAO;
import com.booking.model.PricingRule;
import com.booking.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
public class PricingRuleDAOImpl implements PricingRuleDAO{
    @Override
    public int insert(PricingRule rule) {
        String sql = "INSERT INTO pricing_rules (homestay_id, rule_name, rule_type, start_date, end_date, " +
                "weekdays, discount_type, discount_value, priority, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setObject(1, rule.getHomestayId() != null ? rule.getHomestayId() : null, Types.INTEGER);
            pstmt.setString(2, rule.getRuleName());
            pstmt.setString(3, rule.getRuleType());
            pstmt.setDate(4, rule.getStartDate() != null ? new java.sql.Date(rule.getStartDate().getTime()) : null);
            pstmt.setDate(5, rule.getEndDate() != null ? new java.sql.Date(rule.getEndDate().getTime()) : null);
            pstmt.setString(6, rule.getWeekdays());
            pstmt.setString(7, rule.getDiscountType());
            pstmt.setDouble(8, rule.getDiscountValue());
            pstmt.setInt(9, rule.getPriority());
            pstmt.setInt(10, rule.getStatus());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    rule.setRuleId(rs.getInt(1));
                }
            }
            return affectedRows;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.closeResultSet(rs);
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }
    @Override
    public int deleteById(int id) {
        String sql = "DELETE FROM pricing_rules WHERE rule_id = ?";
        Connection conn;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }
    @Override
    public int update(PricingRule rule) {
        String sql = "UPDATE pricing_rules SET homestay_id=?, rule_name=?, rule_type=?, start_date=?, " +
                "end_date=?, weekdays=?, discount_type=?, discount_value=?, priority=?, status=? " +
                "WHERE rule_id=?";
        Connection conn;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setObject(1, rule.getHomestayId() != null ? rule.getHomestayId() : null, Types.INTEGER);
            pstmt.setString(2, rule.getRuleName());
            pstmt.setString(3, rule.getRuleType());
            pstmt.setDate(4, rule.getStartDate() != null ? new java.sql.Date(rule.getStartDate().getTime()) : null);
            pstmt.setDate(5, rule.getEndDate() != null ? new java.sql.Date(rule.getEndDate().getTime()) : null);
            pstmt.setString(6, rule.getWeekdays());
            pstmt.setString(7, rule.getDiscountType());
            pstmt.setDouble(8, rule.getDiscountValue());
            pstmt.setInt(9, rule.getPriority());
            pstmt.setInt(10, rule.getStatus());
            pstmt.setInt(11, rule.getRuleId());

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }
    @Override
    public PricingRule selectById(int id) {
        String sql = "SELECT * FROM pricing_rules WHERE rule_id = ?";
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractPricingRuleFromResultSet(rs);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            DBUtil.closeResultSet(rs);
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }
    @Override
    public List<PricingRule> selectAll() {
        String sql = "SELECT * FROM pricing_rules ORDER BY priority, rule_id";
        List<PricingRule> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractPricingRuleFromResultSet(rs));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        } finally {
            DBUtil.closeResultSet(rs);
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }
    @Override
    public List<PricingRule> selectByPage(int pageNum, int pageSize) {
        String sql = "SELECT * FROM pricing_rules ORDER BY priority, rule_id LIMIT ?, ?";
        List<PricingRule> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, (pageNum - 1) * pageSize);
            pstmt.setInt(2, pageSize);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractPricingRuleFromResultSet(rs));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        } finally {
            DBUtil.closeResultSet(rs);
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }
    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM pricing_rules";
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.closeResultSet(rs);
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }

    // ==================== 自定义方法 ====================

    @Override
    public List<PricingRule> selectByHomestayId(int homestayId) {
        String sql = "SELECT * FROM pricing_rules WHERE homestay_id = ? OR homestay_id IS NULL " +
                "ORDER BY priority, rule_id";
        List<PricingRule> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, homestayId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractPricingRuleFromResultSet(rs));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        } finally {
            DBUtil.closeResultSet(rs);
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public List<PricingRule> selectGlobalRules() {
        String sql = "SELECT * FROM pricing_rules WHERE homestay_id IS NULL ORDER BY priority";
        List<PricingRule> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractPricingRuleFromResultSet(rs));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        } finally {
            DBUtil.closeResultSet(rs);
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public List<PricingRule> selectByType(String ruleType) {
        String sql = "SELECT * FROM pricing_rules WHERE rule_type = ? ORDER BY priority";
        List<PricingRule> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, ruleType);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractPricingRuleFromResultSet(rs));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        } finally {
            DBUtil.closeResultSet(rs);
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public List<PricingRule> selectActiveRules(Date date) {
        String sql = "SELECT * FROM pricing_rules WHERE status = 1 " +
                "AND (start_date IS NULL OR start_date <= ?) " +
                "AND (end_date IS NULL OR end_date >= ?) " +
                "ORDER BY priority";
        List<PricingRule> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setDate(1, new java.sql.Date(date.getTime()));
            pstmt.setDate(2, new java.sql.Date(date.getTime()));
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractPricingRuleFromResultSet(rs));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        } finally {
            DBUtil.closeResultSet(rs);
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public List<PricingRule> selectActiveRulesByHomestay(int homestayId, Date date) {
        String sql = "SELECT * FROM pricing_rules WHERE status = 1 " +
                "AND (homestay_id = ? OR homestay_id IS NULL) " +
                "AND (start_date IS NULL OR start_date <= ?) " +
                "AND (end_date IS NULL OR end_date >= ?) " +
                "ORDER BY priority";
        List<PricingRule> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, homestayId);
            pstmt.setDate(2, new java.sql.Date(date.getTime()));
            pstmt.setDate(3, new java.sql.Date(date.getTime()));
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractPricingRuleFromResultSet(rs));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        } finally {
            DBUtil.closeResultSet(rs);
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public List<PricingRule> selectOrderByPriority() {
        String sql = "SELECT * FROM pricing_rules ORDER BY priority, rule_id";
        List<PricingRule> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractPricingRuleFromResultSet(rs));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        } finally {
            DBUtil.closeResultSet(rs);
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public int updateStatus(int ruleId, int status) {
        String sql = "UPDATE pricing_rules SET status = ? WHERE rule_id = ?";
        Connection conn;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, status);
            pstmt.setInt(2, ruleId);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public boolean hasConflict(PricingRule rule) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM pricing_rules WHERE status = 1 ");
        sql.append("AND rule_id != ? ");
        sql.append("AND (");
        sql.append("    (homestay_id = ? OR (homestay_id IS NULL AND ? IS NULL)) ");
        sql.append("    AND rule_type = ? ");
        sql.append("    AND (");
        sql.append("        (start_date IS NULL AND end_date IS NULL) ");
        sql.append("        OR (start_date <= ? AND end_date >= ?) ");
        sql.append("        OR (start_date <= ? AND end_date >= ?) ");
        sql.append("    )");
        sql.append(")");

        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql.toString());

            pstmt.setInt(1, rule.getRuleId());
            pstmt.setObject(2, rule.getHomestayId());
            pstmt.setObject(3, rule.getHomestayId());
            pstmt.setString(4, rule.getRuleType());
            pstmt.setDate(5, rule.getEndDate() != null ? new java.sql.Date(rule.getEndDate().getTime()) : null);
            pstmt.setDate(6, rule.getStartDate() != null ? new java.sql.Date(rule.getStartDate().getTime()) : null);
            pstmt.setDate(7, rule.getStartDate() != null ? new java.sql.Date(rule.getStartDate().getTime()) : null);
            pstmt.setDate(8, rule.getEndDate() != null ? new java.sql.Date(rule.getEndDate().getTime()) : null);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.closeResultSet(rs);
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }

    @Override
    public List<PricingRule> selectByHomestayAndType(int homestayId, String ruleType) {
        String sql = "SELECT * FROM pricing_rules WHERE (homestay_id = ? OR homestay_id IS NULL) " +
                "AND rule_type = ? ORDER BY priority";
        List<PricingRule> list = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, homestayId);
            pstmt.setString(2, ruleType);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractPricingRuleFromResultSet(rs));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        } finally {
            DBUtil.closeResultSet(rs);
            if (pstmt != null) DBUtil.closeStatement(pstmt);
        }
    }

    // ==================== 工具方法 ====================

    private PricingRule extractPricingRuleFromResultSet(ResultSet rs) throws SQLException {
        PricingRule rule = new PricingRule();
        rule.setRuleId(rs.getInt("rule_id"));

        int homestayId = rs.getInt("homestay_id");
        if (rs.wasNull()) {
            rule.setHomestayId(null);
        } else {
            rule.setHomestayId(homestayId);
        }

        rule.setRuleName(rs.getString("rule_name"));
        rule.setRuleType(rs.getString("rule_type"));
        rule.setStartDate(rs.getDate("start_date"));
        rule.setEndDate(rs.getDate("end_date"));
        rule.setWeekdays(rs.getString("weekdays"));
        rule.setDiscountType(rs.getString("discount_type"));
        rule.setDiscountValue(rs.getDouble("discount_value"));
        rule.setPriority(rs.getInt("priority"));
        rule.setStatus(rs.getInt("status"));
        rule.setCreateTime(rs.getTimestamp("create_time"));
        return rule;
    }
}
