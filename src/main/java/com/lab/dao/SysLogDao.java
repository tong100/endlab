package com.lab.dao;

import com.lab.entity.SysLog;
import com.lab.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SysLogDao {
    // 新增操作日志（各Servlet调用记录操作）
    public void addLog(SysLog log) {
        String sql = "INSERT INTO sys_log(operate_uid,operate_type,operate_content) VALUES (?,?,?)";
        try (Connection conn = DBUtil.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, log.getOperateUid());
            pstmt.setString(2, log.getOperateType());
            pstmt.setString(3, log.getOperateContent());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 查询全部日志（按时间倒序，最新在前）
    public List<SysLog> listAllLog() {
        List<SysLog> list = new ArrayList<>();
        String sql = "SELECT * FROM sys_log ORDER BY operate_time DESC";
        try (Connection conn = DBUtil.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                SysLog log = new SysLog();
                log.setLid(rs.getInt("lid"));
                log.setOperateUid(rs.getInt("operate_uid"));
                log.setOperateType(rs.getString("operate_type"));
                log.setOperateContent(rs.getString("operate_content"));
                log.setOperateTime(rs.getTimestamp("operate_time"));
                list.add(log);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
