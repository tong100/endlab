# 实验室设备借用管理系统（endlab）

基于 **JSP + Servlet + MySQL** 的实验室设备借用管理课程设计项目，采用 Maven 管理，运行在 Tomcat 9 上。

支持两种角色：

- **学生**：浏览空闲设备、提交借用申请（含日期校验与冲突检测）、归还设备、提交设备报修
- **管理员**：设备增删、借用申请审批（通过/驳回）、报修单管理、查看操作日志

## 技术栈

| 组件 | 说明 |
| --- | --- |
| 后端 | Servlet 4.0（Tomcat 9）、Filter 登录拦截 |
| 前端 | JSP + JSTL/EL，单份公共 CSS（`static/style.css`） |
| 数据库 | MySQL 8，连接参数外置于 `db.properties` |
| 构建 | Maven，`war` 打包 |

## 快速开始

### 1. 初始化数据库

用 MySQL 执行根目录下的 `lab_equipment.sql`（自动建库 `lab_equipment`、建表并插入测试数据）：

```sql
source D:/project/endlab/lab_equipment.sql;
```

### 2. 配置数据库账号

复制配置模板并填入你本地的 MySQL 账号密码（**真实密码文件已被 .gitignore 忽略，不会上传 GitHub**）：

```
src/main/resources/db.properties.example  →  src/main/resources/db.properties
```

```properties
jdbc.user=你的账号
jdbc.password=你的密码
```

### 3. IDEA 中运行（Smart Tomcat 插件）

1. 用 IDEA 打开项目根目录，等待 Maven 依赖下载完成（项目 JDK 使用 **JDK 11**）
2. 安装 **Smart Tomcat** 插件，配置 Tomcat 主目录（如 `D:/Tomcat9`）
3. Run Configuration → Smart Tomcat：
   - **Tomcat Server**：选择你的 Tomcat 9
   - **Deployment Directory**：`D:/project/endlab/src/main/webapp`
   - **Context Path**：`/endlab`（改成任何名字都可以，页面路径已全部动态生成，不再写死）
   - **Server Port**：8080
4. 点击运行，浏览器访问 `http://localhost:8080/endlab/`

> 本项目所有 JSP/Servlet 中的路径都通过 `${pageContext.request.contextPath}` / `request.getContextPath()` 动态生成，
> 不存在写死的 `/endlab`，因此 Smart Tomcat 修改 Context Path 也不会出现 404。

### 4. 测试账号

| 角色 | 账号 | 密码 |
| --- | --- | --- |
| 管理员 | admin | 123456 |
| 学生 | stu01 | 123456 |

## 项目结构

```
endlab
├── lab_equipment.sql              # 建库建表脚本（含测试数据）
├── pom.xml
└── src/main
    ├── java/com/lab
    │   ├── dao/                   # 数据访问层（DAO）
    │   ├── entity/                # 实体类
    │   ├── filter/                # LoginFilter 登录与角色拦截
    │   ├── servlet/               # 控制器（登录/设备/借用/审批/报修/日志）
    │   └── util/                  # DBUtil、状态常量
    ├── resources
    │   ├── db.properties          # 个人数据库配置（不入库）
    │   └── db.properties.example  # 配置模板（入库）
    └── webapp
        ├── static/style.css       # 全站公共样式
        ├── WEB-INF
        │   ├── common/top.jspf    # 顶部导航公共片段
        │   └── web.xml            # Servlet 4.0 规范
        ├── login.jsp / index.jsp / error.jsp
        └── pages/                 # 各业务页面（JSTL + EL）
```

## 主要业务流程

```
学生提交借用申请 → 时间段冲突/日历校验 → 管理员审批
    ├─ 通过 → 设备标记「借出中」→ 学生归还 → 设备恢复「空闲」
    └─ 驳回 → 通知学生调整
设备损坏 → 学生提交报修 → 设备标记「维修中」→ 管理员完成维修 → 恢复「空闲」
```

关键操作（提交申请、审批、归还、维修完成等）均记录到 `sys_log` 操作日志表。

## 常见问题

- **登录后 404**：检查 Smart Tomcat 的 Context Path 与浏览器访问路径是否一致
- **数据库连接失败**：确认 MySQL 已启动、`db.properties` 账号密码正确、已执行建库脚本
- **JSTL 标签报错**：确认使用 Tomcat 9（javax 命名空间），Tomcat 10+ 需要迁移到 jakarta
