package com.lab.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 数据库连接工具类
 * 连接参数从 classpath 下的 db.properties 读取，
 * 真实密码只保存在本地（该文件已被 .gitignore 忽略），不会提交到 GitHub。
 */
public class DBUtil {
    private static final String DEFAULT_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/lab_equipment?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PWD = "";

    private static String URL = DEFAULT_URL;
    private static String USER = DEFAULT_USER;
    private static String PWD = DEFAULT_PWD;

    // 显式加载驱动：Tomcat 的 WEB-INF/lib 虚拟挂载下，SPI 自动发现偶尔失效，显式加载最稳
    static {
        try {
            Class.forName(DEFAULT_DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("【DBUtil提示】MySQL驱动加载失败，请确认 mysql-connector-j 依赖已正确引入：" + e);
        }
    }

    // 类加载时读取一次配置文件
    static {
        try (InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                System.err.println("【DBUtil提示】未找到 db.properties 配置文件。"
                        + "请复制 src/main/resources/db.properties.example 为 db.properties 并填写你的MySQL账号密码。");
            } else {
                Properties props = new Properties();
                props.load(in);
                URL = props.getProperty("jdbc.url", DEFAULT_URL);
                USER = props.getProperty("jdbc.user", DEFAULT_USER);
                PWD = props.getProperty("jdbc.password", DEFAULT_PWD);
            }
        } catch (Exception e) {
            System.err.println("【DBUtil提示】读取 db.properties 失败，将使用默认配置：" + e.getMessage());
        }
    }

    // 获取连接
    public static Connection getConn() throws SQLException {
        return DriverManager.getConnection(URL, USER, PWD);
    }

    // 事务控制
    public static void beginTrans(Connection conn) throws SQLException {
        conn.setAutoCommit(false);
    }
    public static void commit(Connection conn) throws SQLException {
        conn.commit();
    }
    public static void rollback(Connection conn) throws SQLException {
        conn.rollback();
    }

    // 关闭资源重载方法
    public static void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
        try { if (pstmt != null) pstmt.close(); } catch (SQLException ignored) {}
        try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
    }
    public static void close(Connection conn, PreparedStatement pstmt) {
        close(conn, pstmt, null);
    }
}
