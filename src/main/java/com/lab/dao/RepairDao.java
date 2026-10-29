package com.lab.dao;

import com.lab.entity.Repair;
import com.lab.util.DBUtil;
import com.lab.util.StateConstant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class RepairDao {
    /**
     * 学生提交报修单
     * 1. 插入报修记录
     * 2. 将对应设备状态改为维修中 EQ_REPAIR
     * 两条SQL放同一个事务，要么都成功要么都不生效
     */
    public void addRepair(Repair repair) {
        String sql = "INSERT INTO repair(eid,report_uid,repair_desc,report_time,repair_status) VALUES (?,?,?,?,?)";
        String sql2 = "UPDATE equipment SET eq_status=? WHERE eid=?";
        try (Connection conn = DBUtil.getConn()) {
            DBUtil.beginTrans(conn);
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 PreparedStatement pstmt2 = conn.prepareStatement(sql2)) {
                pstmt.setInt(1, repair.getEid());
                pstmt.setInt(2, repair.getReportUid());
                pstmt.setString(3, repair.getRepairDesc());
                pstmt.setTimestamp(4, new Timestamp(repair.getReportTime().getTime()));
                pstmt.setInt(5, StateConstant.REPAIR_WAIT);
                pstmt.executeUpdate();

                pstmt2.setInt(1, StateConstant.EQ_REPAIR);
                pstmt2.setInt(2, repair.getEid());
                pstmt2.executeUpdate();

                DBUtil.commit(conn);
            } catch (Exception e) {
                DBUtil.rollback(conn);
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 管理员标记维修完成
     * 1. 修改报修单状态为 REPAIR_FINISH
     * 2. 设备恢复空闲 EQ_FREE
     */
    public void finishRepair(Integer rid, Integer eid) {
        String sql1 = "UPDATE repair SET repair_status=? WHERE rid=?";
        String sql2 = "UPDATE equipment SET eq_status=? WHERE eid=?";
        try (Connection conn = DBUtil.getConn()) {
            DBUtil.beginTrans(conn);
            try (PreparedStatement p1 = conn.prepareStatement(sql1);
                 PreparedStatement p2 = conn.prepareStatement(sql2)) {
                p1.setInt(1, StateConstant.REPAIR_FINISH);
                p1.setInt(2, rid);
                p1.executeUpdate();

                p2.setInt(1, StateConstant.EQ_FREE);
                p2.setInt(2, eid);
                p2.executeUpdate();

                DBUtil.commit(conn);
            } catch (Exception e) {
                DBUtil.rollback(conn);
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询全部报修记录（管理员后台列表）
     */
    public List<Repair> listAll() {
        List<Repair> list = new ArrayList<>();
        String sql = "SELECT * FROM repair ORDER BY rid DESC";
        try (Connection conn = DBUtil.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Repair r = new Repair();
                r.setRid(rs.getInt("rid"));
                r.setEid(rs.getInt("eid"));
                r.setReportUid(rs.getInt("report_uid"));
                r.setRepairDesc(rs.getString("repair_desc"));
                r.setReportTime(rs.getTimestamp("report_time"));
                r.setRepairStatus(rs.getInt("repair_status"));
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
