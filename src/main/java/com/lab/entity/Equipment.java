package com.lab.entity;

public class Equipment {
    private Integer eid;
    private String eqName;
    private String eqModel;
    private String eqSpec;
    private String eqLocation;
    private Integer eqStatus;

    public Integer getEid() {
        return eid;
    }
    public void setEid(Integer eid) {
        this.eid = eid;
    }

    public String getEqName() {
        return eqName;
    }
    public void setEqName(String eqName) {
        this.eqName = eqName;
    }

    public String getEqModel() {
        return eqModel;
    }
    public void setEqModel(String eqModel) {
        this.eqModel = eqModel;
    }

    public String getEqSpec() {
        return eqSpec;
    }
    public void setEqSpec(String eqSpec) {
        this.eqSpec = eqSpec;
    }

    public String getEqLocation() {
        return eqLocation;
    }
    public void setEqLocation(String eqLocation) {
        this.eqLocation = eqLocation;
    }

    public Integer getEqStatus() {
        return eqStatus;
    }
    public void setEqStatus(Integer eqStatus) {
        this.eqStatus = eqStatus;
    }
}