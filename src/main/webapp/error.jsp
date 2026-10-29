<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>出错了 | 实验室设备借用系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style.css">
</head>
<body>
    <div class="error-page">
        <h1>:(</h1>
        <p>页面不存在或服务出了点问题，请返回重试。</p>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/login.jsp">返回登录</a>
    </div>
</body>
</html>
