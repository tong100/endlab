package com.lab.dao;

import com.lab.entity.Equipment;
import com.lab.util.DBUtil;
import com.lab.util.StateConstant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EquipmentDao {
    /**
     * 新增设备（管理员自定义设备ID）
     */
    public boolean add(Equipment eq) {
        String sql = "INSERT INTO equipment(eid,eq_name,eq_model,eq_spec,eq_location,eq_status) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DBUtil.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eq.getEid());
            pstmt.setString(2, eq.getEqName());
            pstmt.setString(3, eq.getEqModel());
            pstmt.setString(4, eq.getEqSpec());
            pstmt.setString(5, eq.getEqLocation());
            pstmt.setInt(6, StateConstant.EQ_FREE);
            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 修改设备信息
     */
    public void update(Equipment eq) {
        String sql = "UPDATE equipment SET eq_name=?,eq_model=?,eq_spec=?,eq_location=?,eq_status=? WHERE eid=?";
        try (Connection conn = DBUtil.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, eq.getEqName());
            pstmt.setString(2, eq.getEqModel());
            pstmt.setString(3, eq.getEqSpec());
            pstmt.setString(4, eq.getEqLocation());
            pstmt.setInt(5, eq.getEqStatus());
            pstmt.setInt(6, eq.getEid());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据ID删除设备
     */
    public void delete(Integer eid) {
        String sql = "DELETE FROM equipment WHERE eid=?";
        try (Connection conn = DBUtil.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eid);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据ID查询单个设备
     */
    public Equipment getById(Integer eid) {
        String sql = "SELECT * FROM equipment WHERE eid=?";
        try (Connection conn = DBUtil.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eid);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询全部设备
     */
    public List<Equipment> listAll() {
        List<Equipment> list = new ArrayList<>();
        String sql = "SELECT * FROM equipment ORDER BY eid";
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

    /**
     * 查询所有空闲可借用设备
     */
    public List<Equipment> listFree() {
        List<Equipment> list = new ArrayList<>();
        String sql = "SELECT * FROM equipment WHERE eq_status = ? ORDER BY eid";
        try (Connection conn = DBUtil.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, StateConstant.EQ_FREE);
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

    // 结果集 -> 实体对象 的公共映射
    private Equipment mapRow(ResultSet rs) throws Exception {
        Equipment eq = new Equipment();
        eq.setEid(rs.getInt("eid"));
        eq.setEqName(rs.getString("eq_name"));
        eq.setEqModel(rs.getString("eq_model"));
        eq.setEqSpec(rs.getString("eq_spec"));
        eq.setEqLocation(rs.getString("eq_location"));
        eq.setEqStatus(rs.getInt("eq_status"));
        return eq;
    }
}
