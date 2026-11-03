-- 如果存在lab_equipment数据库，先删除，避免旧表冲突
DROP DATABASE IF EXISTS lab_equipment;

-- 创建实验室设备管理数据库，字符集utf8mb4支持中文、emoji，排序大小写不敏感
CREATE DATABASE IF NOT EXISTS lab_equipment CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 切换到刚创建的数据库，后续建表、插数据都在此库执行
USE lab_equipment;

-- ====================== 用户表 ======================
-- 存储系统登录用户，区分学生、管理员
CREATE TABLE user(
    -- 用户唯一编号，主键，自动自增
    uid INT PRIMARY KEY AUTO_INCREMENT,
    -- 登录账号，最长30字符，必填，全表账号不能重复
    username VARCHAR(30) NOT NULL UNIQUE,
    -- 登录密码，最长30字符，不能为空
    password VARCHAR(30) NOT NULL,
    -- 用户角色：0学生 1管理员，极小整数节省空间，必填
    role TINYINT NOT NULL COMMENT '0学生，1管理员',
    -- 用户真实姓名，最长20字符，必填
    name VARCHAR(20) NOT NULL
);

-- ====================== 设备信息表 ======================
-- 存放所有实验室仪器基础信息
CREATE TABLE equipment(
    -- 设备唯一编号，主键自增
    eid INT PRIMARY KEY AUTO_INCREMENT,
    -- 设备名称，必填，最多50字符
    eq_name VARCHAR(50) NOT NULL,
    -- 设备型号，最多50字符，允许为空
    eq_model VARCHAR(50),
    -- 设备规格参数，最多100字符，允许为空
    eq_spec VARCHAR(100),
    -- 设备存放实验室位置，最多50字符，允许为空
    eq_location VARCHAR(50),
    -- 设备状态，必填，新建设备默认空闲(0)
    eq_status TINYINT NOT NULL DEFAULT 0 COMMENT '0空闲 1借出 2维修 3报废'
);

-- ====================== 设备借用申请表 ======================
-- 核心业务表：保存学生借设备的申请、审批、归还全流程记录
CREATE TABLE borrow_apply(
    -- 借用申请单编号，主键自增
    bid INT PRIMARY KEY AUTO_INCREMENT,
    -- 申请人用户ID，必填，关联user表uid
    uid INT NOT NULL COMMENT '申请人id',
    -- 要借用的设备ID，必填，关联equipment表eid
    eid INT NOT NULL COMMENT '设备id',
    -- 计划借用起始日期，仅年月日，必填
    borrow_date DATE NOT NULL,
    -- 计划归还日期，仅年月日，必填
    return_date DATE NOT NULL,
    -- 实际归还日期，未归还时为空，归还后填充日期
    actual_return_date DATE NULL,
    -- 申请单据状态，新建申请默认待审批
    apply_status TINYINT NOT NULL DEFAULT 0 COMMENT '0待审批 1借出 2驳回 3已归还',
    -- 审批这条申请的管理员ID，未审批时为空
    approve_uid INT NULL COMMENT '审批管理员id',
    -- 管理员审批备注/驳回理由，最多200字符
    approve_msg VARCHAR(200),
    -- 外键约束：申请人uid必须存在于user表，防止无效用户ID
    FOREIGN KEY(uid) REFERENCES user(uid),
    -- 外键约束：借用设备eid必须存在于equipment表
    FOREIGN KEY(eid) REFERENCES equipment(eid),
    -- 外键约束：审批人ID必须是系统内合法用户
    FOREIGN KEY(approve_uid) REFERENCES user(uid)
);

-- ====================== 设备报修记录表 ======================
-- 设备损坏后提交的维修单据
CREATE TABLE repair(
    -- 报修单编号，主键自增
    rid INT PRIMARY KEY AUTO_INCREMENT,
    -- 故障设备ID，必填，关联设备表
    eid INT NOT NULL,
    -- 提交报修的用户ID，必填
    report_uid INT NOT NULL,
    -- 故障详细描述，长文本类型，必填
    repair_desc TEXT NOT NULL,
    -- 报修提交时间，创建记录时自动填充当前时间
    report_time DATETIME DEFAULT NOW(),
    -- 维修状态，默认待维修
    repair_status TINYINT DEFAULT 0 COMMENT '0待维修 1维修完成',
    -- 外键：报修的设备必须真实存在
    FOREIGN KEY(eid) REFERENCES equipment(eid),
    -- 外键：提交报修的用户必须存在
    FOREIGN KEY(report_uid) REFERENCES user(uid)
);

-- ====================== 系统操作日志表 ======================
-- 记录所有用户操作，方便追溯、审计
CREATE TABLE sys_log(
    -- 日志记录编号，主键自增
    lid INT PRIMARY KEY AUTO_INCREMENT,
    -- 操作人的用户ID，必填
    operate_uid INT NOT NULL,
    -- 操作类型（如：新增设备、提交借用、审批报修）最长50字符，必填
    operate_type VARCHAR(50) NOT NULL,
    -- 操作详细内容，长文本存储完整记录，必填
    operate_content TEXT NOT NULL,
    -- 操作发生时间，自动填充当前时间
    operate_time DATETIME DEFAULT NOW(),
    -- 外键：操作人必须是系统内合法用户
    FOREIGN KEY(operate_uid) REFERENCES user(uid)
);

-- ====================== 插入测试用户数据 ======================
-- 向user表插入2个账号：管理员admin、学生stu01
INSERT INTO user(username,password,role,name) VALUES
-- 第一条：管理员账号，密码123456，角色1
('admin','123456',1,'实验室管理员'),
-- 第二条：学生账号，密码123456，角色0
('stu01','123456',0,'学生张三');

-- ====================== 插入测试设备数据 ======================
-- 设备状态：0空闲 1借出中 2维修中（状态与下方借用单/报修单保持一致）
INSERT INTO equipment(eq_name,eq_model,eq_spec,eq_location,eq_status) VALUES
-- 1：数字示波器，已被学生借出中
('数字示波器','DS1054Z','50MHz四通道','物理实验室101',1),
-- 2：电子分析天平，空闲（有一条已完成的报修记录）
('电子分析天平','FA2004','精度0.1mg','化学实验室202',0),
-- 3：函数信号发生器，空闲（有一条待审批申请）
('函数信号发生器','DG1022Z','25MHz双通道','物理实验室101',0),
-- 4：离心机，空闲（有一条被驳回的申请）
('离心机','TG16-WS','最高16000rpm','生物实验室303',0),
-- 5：紫外分光光度计，维修中
('紫外分光光度计','UV-1800','波长190-1100nm','化学实验室202',2),
-- 6：数字万用表，空闲
('数字万用表','Fluke 17B+','真有效值','电子实验室105',0);

-- ====================== 插入测试借用申请数据 ======================
-- 申请状态：0待审批 1借出 2驳回 3已归还；日期用CURDATE()相对生成，任何时候执行都有效
INSERT INTO borrow_apply(uid,eid,borrow_date,return_date,actual_return_date,apply_status,approve_uid,approve_msg) VALUES
-- 1：借出中（设备1）
(2,1,CURDATE() - INTERVAL 5 DAY,CURDATE() + INTERVAL 2 DAY,NULL,1,1,'设备状态良好，同意借用'),
-- 2：已归还（设备2，历史记录）
(2,2,CURDATE() - INTERVAL 12 DAY,CURDATE() - INTERVAL 6 DAY,CURDATE() - INTERVAL 7 DAY,3,1,'已按时归还'),
-- 3：待审批（设备3）
(2,3,CURDATE() + INTERVAL 1 DAY,CURDATE() + INTERVAL 5 DAY,NULL,0,NULL,NULL),
-- 4：已驳回（设备4）
(2,4,CURDATE() - INTERVAL 10 DAY,CURDATE() - INTERVAL 8 DAY,NULL,2,1,'该设备本周已排满，请改约下周');

-- ====================== 插入测试报修数据 ======================
-- 报修状态：0待维修 1维修完成
INSERT INTO repair(eid,report_uid,repair_desc,report_time,repair_status) VALUES
-- 1：待维修（设备5，与设备状态「维修中」对应）
(5,2,'开机后读数漂移严重，无法完成校准',NOW() - INTERVAL 1 DAY,0),
-- 2：已维修完成（设备2，历史记录）
(2,2,'称量时显示Err-02，疑似传感器故障',NOW() - INTERVAL 8 DAY,1);

-- ====================== 插入测试操作日志数据 ======================
INSERT INTO sys_log(operate_uid,operate_type,operate_content,operate_time) VALUES
(1,'新增设备','管理员新增设备：函数信号发生器 DG1022Z',NOW() - INTERVAL 15 DAY),
(2,'提交借用','学生张三提交借用申请：数字示波器',NOW() - INTERVAL 5 DAY),
(1,'审批通过','管理员审批通过借用申请 #1',NOW() - INTERVAL 5 DAY),
(2,'提交报修','学生张三提交报修：紫外分光光度计',NOW() - INTERVAL 1 DAY);