package com.lab.dao;

import com.lab.entity.BorrowApply;
import com.lab.util.DBUtil;
import com.lab.util.StateConstant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BorrowApplyDao {
    // 学生提交借用申请
    public void addApply(BorrowApply apply) {
        String sql = "INSERT INTO borrow_apply(uid,eid,borrow_date,return_date,apply_status) VALUES (?,?,?,?,?)";
        try (Connection conn = DBUtil.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, apply.getUid());
            pstmt.setInt(2, apply.getEid());
            pstmt.setDate(3, new java.sql.Date(apply.getBorrowDate().getTime()));
            pstmt.setDate(4, new java.sql.Date(apply.getReturnDate().getTime()));
            pstmt.setInt(5, StateConstant.APPLY_WAIT_AUDIT);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 审批通过【事务：更新借用单 + 修改设备为借出】
    public boolean auditPass(Integer bid, Integer adminUid, String msg, Integer eid) {
        String sql1 = "UPDATE borrow_apply SET apply_status=?,approve_uid=?,approve_msg=? WHERE bid=?";
        String sql2 = "UPDATE equipment SET eq_status=? WHERE eid=?";
        try (Connection conn = DBUtil.getConn()) {
            DBUtil.beginTrans(conn);
            try (PreparedStatement p1 = conn.prepareStatement(sql1);
                 PreparedStatement p2 = conn.prepareStatement(sql2)) {
                p1.setInt(1, StateConstant.APPLY_PASS_BORROW);
                p1.setInt(2, adminUid);
                p1.setString(3, msg);
                p1.setInt(4, bid);
                p1.executeUpdate();

                p2.setInt(1, StateConstant.EQ_BORROW);
                p2.setInt(2, eid);
                p2.executeUpdate();

                DBUtil.commit(conn);
                return true;
            } catch (Exception e) {
                DBUtil.rollback(conn);
                e.printStackTrace();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 审批驳回（仅更新申请单，无事务）
    public void auditReject(Integer bid, Integer adminUid, String msg) {
        String sql = "UPDATE borrow_apply SET apply_status=?,approve_uid=?,approve_msg=? WHERE bid=?";
        try (Connection conn = DBUtil.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, StateConstant.APPLY_REJECT);
            pstmt.setInt(2, adminUid);
            pstmt.setString(3, msg);
            pstmt.setInt(4, bid);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 归还登记【事务：借用单标记已归还 + 设备恢复空闲】
    public boolean returnEquip(Integer bid, Integer eid) {
        String sql1 = "UPDATE borrow_apply SET apply_status=?,actual_return_date=NOW() WHERE bid=? AND apply_status=?";
        String sql2 = "UPDATE equipment SET eq_status=? WHERE eid=?";
        try (Connection conn = DBUtil.getConn()) {
            DBUtil.beginTrans(conn);
            try (PreparedStatement p1 = conn.prepareStatement(sql1);
                 PreparedStatement p2 = conn.prepareStatement(sql2)) {
                p1.setInt(1, StateConstant.APPLY_RETURNED);
                p1.setInt(2, bid);
                p1.setInt(3, StateConstant.APPLY_PASS_BORROW);
                p1.executeUpdate();

                p2.setInt(1, StateConstant.EQ_FREE);
                p2.setInt(2, eid);
                p2.executeUpdate();

                DBUtil.commit(conn);
                return true;
            } catch (Exception e) {
                DBUtil.rollback(conn);
                e.printStackTrace();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 管理员查询待审批列表
    public List<BorrowApply> listWaitAudit() {
        List<BorrowApply> list = new ArrayList<>();
        String sql = "SELECT * FROM borrow_apply WHERE apply_status=0 ORDER BY bid";
        try (Connection conn = DBUtil.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 检查设备在目标时间段是否已有借用冲突（待审批或借出中）
    public boolean checkOverlap(Integer eid, java.util.Date borrowDate, java.util.Date returnDate) {
        String sql = "SELECT COUNT(*) FROM borrow_apply WHERE eid=? AND apply_status IN (0,1) AND borrow_date <= ? AND return_date >= ?";
        try (Connection conn = DBUtil.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eid);
            pstmt.setDate(2, new java.sql.Date(returnDate.getTime()));
            pstmt.setDate(3, new java.sql.Date(borrowDate.getTime()));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 学生查询本人全部申请记录
    public List<BorrowApply> listByUid(Integer uid) {
        List<BorrowApply> list = new ArrayList<>();
        String sql = "SELECT * FROM borrow_apply WHERE uid=? ORDER BY bid DESC";
        try (Connection conn = DBUtil.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, uid);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 结果集 -> 实体对象 的公共映射，避免每个查询方法重复写
    private BorrowApply mapRow(ResultSet rs) throws Exception {
        BorrowApply ba = new BorrowApply();
        ba.setBid(rs.getInt("bid"));
        ba.setUid(rs.getInt("uid"));
        ba.setEid(rs.getInt("eid"));
        ba.setBorrowDate(rs.getDate("borrow_date"));
        ba.setReturnDate(rs.getDate("return_date"));
        ba.setActualReturnDate(rs.getDate("actual_return_date"));
        ba.setApplyStatus(rs.getInt("apply_status"));
        return ba;
    }
}
