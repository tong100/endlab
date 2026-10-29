package com.lab.entity;

import java.util.Date;

public class SysLog {
    private Integer lid;
    private Integer operateUid;
    private String operateType;
    private String operateContent;
    private Date operateTime;

    public Integer getLid() {
        return lid;
    }
    public void setLid(Integer lid) {
        this.lid = lid;
    }
    public Integer getOperateUid() {
        return operateUid;
    }
    public void setOperateUid(Integer operateUid) {
        this.operateUid = operateUid;
    }
    public String getOperateType() {
        return operateType;
    }
    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }
    public String getOperateContent() {
        return operateContent;
    }
    public void setOperateContent(String operateContent) {
        this.operateContent = operateContent;
    }
    public Date getOperateTime() {
        return operateTime;
    }
    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }
}