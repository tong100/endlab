package com.lab.entity;

import java.util.Date;

public class BorrowApply {
    private Integer bid;
    private Integer uid;
    private Integer eid;
    private Date borrowDate;
    private Date returnDate;
    private Date actualReturnDate;
    private Integer applyStatus;
    private Integer approveUid;
    private String approveMsg;

    public Integer getBid() {
        return bid;
    }
    public void setBid(Integer bid) {
        this.bid = bid;
    }
    public Integer getUid() {
        return uid;
    }
    public void setUid(Integer uid) {
        this.uid = uid;
    }
    public Integer getEid() {
        return eid;
    }
    public void setEid(Integer eid) {
        this.eid = eid;
    }
    public Date getBorrowDate() {
        return borrowDate;
    }
    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }
    public Date getReturnDate() {
        return returnDate;
    }
    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }
    public Date getActualReturnDate() {
        return actualReturnDate;
    }
    public void setActualReturnDate(Date actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }
    public Integer getApplyStatus() {
        return applyStatus;
    }
    public void setApplyStatus(Integer applyStatus) {
        this.applyStatus = applyStatus;
    }
    public Integer getApproveUid() {
        return approveUid;
    }
    public void setApproveUid(Integer approveUid) {
        this.approveUid = approveUid;
    }
    public String getApproveMsg() {
        return approveMsg;
    }
    public void setApproveMsg(String approveMsg) {
        this.approveMsg = approveMsg;
    }
}