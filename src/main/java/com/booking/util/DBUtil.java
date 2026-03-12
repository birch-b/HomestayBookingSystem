package com.booking.util;
import java.sql.*;
import java.util.Stack;

/**
 * 数据库连接工具类
 * 负责管理数据库连接和事务
 */
public class DBUtil {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/homestay_booking?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "200631612";
    // ThreadLocal存储当前线程的Connection，用于事务管理
    private static ThreadLocal<Connection> tl = new ThreadLocal<>();
    // 静态代码块，加载驱动
    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("MySQL驱动加载失败！请检查lib目录下是否有驱动jar包");
        }
    }
    /**
     * 获取数据库连接
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = tl.get();
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            tl.set(conn);
        }
        return conn;
    }
    /**
     * 开启事务
     */
    public static void beginTransaction() throws SQLException {
        Connection conn = getConnection();
        conn.setAutoCommit(false);
    }
    /**
     * 提交事务
     */
    public static void commitTransaction() throws SQLException {
        Connection conn = getConnection();
        conn.commit();
        conn.setAutoCommit(true);
    }
    /**
     * 回滚事务
     */
    public static void rollbackTransaction() throws SQLException {
        Connection conn = getConnection();
        conn.rollback();
        conn.setAutoCommit(true);
    }
    /**
     * 关闭连接
     */
    public static void closeConnection() {
        Connection conn = tl.get();
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            tl.remove();
        }
    }

    /**
     * 关闭ResultSet
     */
    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭Statement
     */
    public static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 测试连接
     */
    public static void testConnection() {
        Connection conn = null;
        try {
            conn = getConnection();
            System.out.println("✅ 数据库连接成功！");
        } catch (SQLException e) {
            System.out.println("❌ 数据库连接失败：" + e.getMessage());
        } finally {
            closeConnection();
        }
    }
}
