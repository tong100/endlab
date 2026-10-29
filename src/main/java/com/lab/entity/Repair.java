package com.lab.entity;

import java.util.Date;

public class Repair {
    private Integer rid;
    private Integer eid;
    private Integer reportUid;
    private String repairDesc;
    private Date reportTime;
    private Integer repairStatus;

    public Integer getRid() {
        return rid;
    }
    public void setRid(Integer rid) {
        this.rid = rid;
    }
    public Integer getEid() {
        return eid;
    }
    public void setEid(Integer eid) {
        this.eid = eid;
    }
    public Integer getReportUid() {
        return reportUid;
    }
    public void setReportUid(Integer reportUid) {
        this.reportUid = reportUid;
    }
    public String getRepairDesc() {
        return repairDesc;
    }
    public void setRepairDesc(String repairDesc) {
        this.repairDesc = repairDesc;
    }
    public Date getReportTime() {
        return reportTime;
    }
    public void setReportTime(Date reportTime) {
        this.reportTime = reportTime;
    }
    public Integer getRepairStatus() {
        return repairStatus;
    }
    public void setRepairStatus(Integer repairStatus) {
        this.repairStatus = repairStatus;
    }
}