package com.lab.util;

// 业务状态统一管理：申请-审批-借用-归还 完整状态机
public class StateConstant {
    // 借用单 apply_status
    public static final int APPLY_WAIT_AUDIT = 0;    // 待审批
    public static final int APPLY_PASS_BORROW = 1;  // 审批通过 借出中
    public static final int APPLY_REJECT = 2;       // 审批驳回
    public static final int APPLY_RETURNED = 3;      // 已归还

    // 设备 eq_status
    public static final int EQ_FREE = 0;    // 空闲
    public static final int EQ_BORROW = 1;  // 借出
    public static final int EQ_REPAIR = 2;  // 维修
    public static final int EQ_SCRAP = 3;   // 报废

    // 报修 repair_status
    public static final int REPAIR_WAIT = 0;
    public static final int REPAIR_FINISH = 1;
}