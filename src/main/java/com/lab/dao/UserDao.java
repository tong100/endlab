package com.lab.dao;

import com.lab.entity.User;
import com.lab.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDao {
    public User login(String username, String password) {
        User user = null;
        String sql = "SELECT * FROM user WHERE username=? AND password=?";
        try (Connection conn = DBUtil.getConn()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        user = new User();
                        user.setUid(rs.getInt("uid"));
                        user.setUsername(rs.getString("username"));
                        user.setPassword(rs.getString("password"));
                        user.setRole(rs.getInt("role"));
                        user.setName(rs.getString("name"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 数据库层面失败（连接不上、驱动缺失等）不能伪装成"账号密码错误"，抛给上层给用户看真实原因
            throw new RuntimeException("数据库访问失败：" + e.getMessage(), e);
        }
        return user;
    }
}
