<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>登录 | 实验室设备借用系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style.css">
</head>
<body class="login-page">
    <div class="login-box">
        <div class="sys-name">实验室设备借用系统</div>
        <div class="sys-sub">Laboratory Equipment Management</div>

        <c:if test="${not empty msg}">
            <div class="alert alert-error">${msg}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="post">
            <div class="form-item">
                <label>账号</label>
                <input name="username" placeholder="请输入账号" autofocus>
            </div>
            <div class="form-item">
                <label>密码</label>
                <input type="password" name="password" placeholder="请输入密码">
            </div>
            <button class="btn btn-primary" type="submit">登录系统</button>
        </form>
    </div>

    <p class="tip-text">测试账号：管理员 admin / 123456 &nbsp;|&nbsp; 学生 stu01 / 123456</p>
</body>
</html>
