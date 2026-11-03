# 实验室设备借用管理系统（endlab）

一个基于 **JSP + Servlet + MySQL** 的实验室设备借用管理课程设计项目，使用 Maven 构建、Tomcat 9 部署，实现「设备台账 → 借用申请 → 审批 → 归还 → 报修」的完整业务闭环。

> 技术路线：Java EE（Servlet 4.0）+ JSP/JSTL/EL + JDBC + MySQL 8，经典 MVC 分层，不依赖 Spring 等框架。

---

## 一、功能清单

### 学生端

| 功能 | 说明 |
| --- | --- |
| 登录 / 退出 | 会话保存登录态，未登录访问内部页面自动跳回登录页 |
| 浏览设备 | 查看设备台账与实时状态（空闲 / 借出中 / 维修中） |
| 提交借用申请 | 选择设备与借用时间段，**校验归还日期必须晚于借用日期**，设备状态与时间冲突自动拦截 |
| 我的借用 | 查看申请列表与状态（待审批 / 已借出 / 已驳回 / 已归还），对已借出设备发起归还 |
| 设备报修 | 对故障设备提交报修单，附故障描述，设备自动转为「维修中」 |

### 管理员端

| 功能 | 说明 |
| --- | --- |
| 设备管理 | 新增设备、删除设备、查看全部设备台账 |
| 借用审批 | 通过 / 驳回学生申请，填写审批意见；通过后设备自动置为「借出中」 |
| 报修管理 | 查看全部报修单，标记维修完成，设备恢复「空闲」 |
| 操作日志 | 全流程关键操作（新增 / 借用 / 审批 / 归还 / 报修 / 维修）落库到 `sys_log`，可追溯 |

---

## 二、技术点对照

| 课程考查点 | 本项目实现位置 |
| --- | --- |
| Servlet 编程 | `com.lab.servlet` 下 6 个 Servlet，覆盖登录、设备、借用、审批、报修、日志 |
| Filter 过滤器 | `LoginFilter`：登录拦截 + 角色权限控制（学生不能访问管理端接口） |
| JDBC 数据库访问 | `DBUtil` 统一获取连接，DAO 层使用 `PreparedStatement` 参数绑定，防 SQL 注入 |
| 事务控制 | 报修提交（插单 + 改设备状态）、审批、归还等涉及多表更新处使用 `setAutoCommit(false)` 显式事务 |
| JSP + JSTL + EL | 视图层全部使用 `<c:forEach>` / `<c:choose>` 与 EL 表达式，不写 scriptlet，输出自动转义防 XSS |
| MVC 分层 | entity（实体）/ dao（数据访问）/ servlet（控制）/ jsp（视图）四层分离 |
| 会话与权限 | `HttpSession` 保存用户与角色，`web.xml` 配置会话超时与友好错误页 |
| 配置外置 | 数据库连接参数放在 `db.properties`，敏感信息不入库 |

---

## 三、环境要求

| 组件 | 版本要求 |
| --- | --- |
| JDK | 11（项目 `maven.compiler.source/target` 为 1.8，JDK 8/11 均可编译运行） |
| Tomcat | **9.x**（Servlet 4.0 / `javax.servlet`；Tomcat 10+ 已迁移到 `jakarta`，跑不起来） |
| MySQL | 8.x |
| Maven | 3.6+（IDEA 自带即可） |
| IDE | IntelliJ IDEA（推荐安装 Smart Tomcat 插件） |

---

## 四、快速开始

### 步骤 1：初始化数据库

打开命令行（或 MySQL 客户端），执行根目录下的建库脚本。脚本会自动创建数据库 `lab_equipment`、5 张表，并插入演示数据：

```bash
mysql -u root -p < 项目路径/lab_equipment.sql
```

已经进入 `mysql>` 命令行的话，也可以用：

```sql
source 项目路径/lab_equipment.sql;
```

> 路径用正斜杠 `/`，结尾必须带分号 `;`。脚本开头会 `DROP DATABASE IF EXISTS lab_equipment`，重复执行会清空重建。

### 步骤 2：配置数据库账号

把配置模板复制一份，改名为 `db.properties`（放在同一目录），填入你本机 MySQL 的账号密码：

```
src/main/resources/db.properties.example  →  src/main/resources/db.properties
```

```properties
jdbc.user=root
jdbc.password=你的MySQL密码
```

> `db.properties` 已被 `.gitignore` 忽略，是**本地私有配置**，不会进仓库；提交到 GitHub 的只有 `db.properties.example` 模板。

### 步骤 3：IDEA 运行（Smart Tomcat）

1. IDEA 打开项目根目录，等待 Maven 依赖下载完成
2. 安装 **Smart Tomcat** 插件（Settings → Plugins → 搜索 Smart Tomcat）
3. Run → Edit Configurations → `+` → Smart Tomcat：

   | 配置项 | 值 |
   | --- | --- |
   | Name | endlab |
   | Tomcat Server | 本机 Tomcat 9 目录 |
   | Deployment Directory | `项目路径/src/main/webapp` |
   | Context Path | `/endlab`（改成别的名字也能跑） |
   | Server Port | 8080 |
   | Before launch | Maven `clean compile`（可选，保证源码最新） |

4. 点击运行，浏览器访问 `http://localhost:8080/endlab/`

> 项目中所有页面链接与重定向均通过 `${pageContext.request.contextPath}` / `request.getContextPath()` 动态生成，没有写死 `/endlab`，因此修改 Context Path 不会出现 404。

### 步骤 4：测试账号

| 角色 | 账号 | 密码 | 登录后页面 |
| --- | --- | --- | --- |
| 管理员 | `admin` | `123456` | 设备管理 |
| 学生 | `stu01` | `123456` | 我的借用 |

---

## 五、演示数据说明

执行 `lab_equipment.sql` 后，数据库中预置了可直接演示的数据：

| 表 | 数据量 | 内容 |
| --- | --- | --- |
| `user` | 2 | 管理员 admin、学生 stu01 |
| `equipment` | 6 | 示波器（借出中）、天平、信号发生器、离心机、分光光度计（维修中）、万用表 |
| `borrow_apply` | 4 | 覆盖**待审批 / 借出中 / 已驳回 / 已归还**四种状态 |
| `repair` | 2 | 待维修 1 条、维修完成 1 条 |
| `sys_log` | 4 | 新增设备、提交借用、审批通过、提交报修 |

借用日期用 `CURDATE()` 相对生成，**任何时间执行脚本数据都不会过期**。

---

## 六、数据库设计

### 表结构

| 表名 | 说明 | 关键字段 |
| --- | --- | --- |
| `user` | 用户表 | `uid` 主键、`username` 唯一、`password`、`role`（1 管理员 / 0 学生）、`name` |
| `equipment` | 设备台账 | `eid` 主键、`eq_name`、`eq_model`、`eq_spec`、`eq_location`、`eq_status`（0 空闲 / 1 借出中 / 2 维修中） |
| `borrow_apply` | 借用申请单 | `bid` 主键、`uid` 申请人、`eid` 设备、`borrow_date`、`return_date`、`actual_return_date`、`apply_status`（0 待审批 / 1 借出 / 2 驳回 / 3 已归还）、`approve_uid`、`approve_msg` |
| `repair` | 报修单 | `rid` 主键、`eid`、`report_uid`、`repair_desc`、`report_time`、`repair_status`（0 待维修 / 1 维修完成） |
| `sys_log` | 操作日志 | `lid` 主键、`operate_uid`、`operate_type`、`operate_content`、`operate_time` |

### 实体关系

```
user(1) ──< borrow_apply >── (1)equipment     一个学生可提交多条申请，一台设备可被多次借用
user(1) ──< repair        >── (1)equipment     报修单关联设备与提交人
user(1) ──< sys_log                           操作日志记录操作人
```

`borrow_apply`、`repair`、`sys_log` 均建有外键约束，保证引用完整性。

### 业务状态流转

```
学生提交申请 → [待审批]
      ├─ 管理员通过 → [已借出] → 学生归还 → [已归还]
      └─ 管理员驳回 → [已驳回]

学生提交报修 → 设备[维修中] + 报修单[待维修] → 管理员完成维修 → 设备[空闲] + 报修单[维修完成]
```

---

## 七、项目结构

```
endlab
├── lab_equipment.sql                  # 建库建表 + 演示数据
├── pom.xml                            # Maven 配置（war 打包）
├── README.md
└── src/main
    ├── java/com/lab
    │   ├── dao/                       # 数据访问层：UserDao / EquipmentDao / BorrowApplyDao / RepairDao / SysLogDao
    │   ├── entity/                    # 实体类：User / Equipment / BorrowApply / Repair / SysLog
    │   ├── filter/LoginFilter.java    # 登录拦截 + 角色权限控制
    │   ├── servlet/                   # 控制层：Login / Equipment / BorrowApply / Audit / Repair / Log
    │   └── util/                      # DBUtil（连接与事务）、StateConstant（状态常量）
    ├── resources
    │   ├── db.properties              # 本地数据库配置（不入库）
    │   └── db.properties.example      # 配置模板（入库）
    └── webapp
        ├── static/style.css           # 全站公共样式
        ├── WEB-INF
        │   ├── web.xml                # Servlet 4.0 配置
        │   └── common/                # top.jspf（文档骨架+导航）、bottom.jspf（页脚收尾）
        ├── login.jsp / index.jsp / error.jsp
        └── pages/                     # 业务页面：设备管理、借用、审批、报修、日志
```

---

## 八、部署为 WAR 包（可选）

不想用 IDEA 插件时，可以打包后丢进独立 Tomcat：

```bash
mvn clean package
```

生成的 `target/endlab.war` 复制到 `Tomcat/webapps/` 目录，启动 Tomcat 后访问：

```
http://localhost:8080/endlab/
```

> 使用 war 部署时，`db.properties` 已随编译进入 `WEB-INF/classes`，无需额外配置。

---

## 九、常见问题

| 现象 | 排查方向 |
| --- | --- |
| 登录提示「账号或密码错误」 | 先确认 `user` 表有数据：`SELECT * FROM user;`，脚本可能只执行了前半段 |
| 登录提示「数据库连接失败」 | `db.properties` 账号密码是否正确、MySQL 服务是否启动、库名是否为 `lab_equipment` |
| 页面 404 | Smart Tomcat 的 Context Path 与浏览器地址是否一致 |
| 页面 500 / 空白 | 看 Tomcat 控制台首个 `Caused by`；本项目已配置 `error.jsp` 友好错误页 |
| JSTL 标签报错 | 必须是 Tomcat 9（`javax` 命名空间），Tomcat 10+ 需迁移 `jakarta` |
| 中文乱码 | 数据库/表已用 `utf8mb4`；命令行执行脚本前可先 `chcp 65001` |

---

## 十、界面预览

> 截图存放目录建议：`docs/screenshots/`。上传后在此处用 Markdown 引用，例如
> `![设备管理](docs/screenshots/equipment.png)`

- 登录页：蓝色渐变背景 + 居中登录卡片
- 设备管理（管理员）：新增设备表单 + 设备台账表格，状态彩色徽章
- 借用审批（管理员）：待审批列表，通过 / 驳回操作
- 我的借用（学生）：申请状态跟踪与归还入口
