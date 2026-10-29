<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="activeNav" value="repairAdd" scope="request" />
<%@ include file="/WEB-INF/common/top.jspf" %>

<h3 class="page-title">设备损坏报修</h3>

<div class="card" style="max-width:520px;">
    <h4>填写报修信息</h4>
    <form action="${ctx}/repair" method="post">
        <input type="hidden" name="act" value="add">
        <div class="form-item">
            <label>损坏设备ID</label>
            <input type="number" name="eid" required placeholder="请输入报修的设备编号">
        </div>
        <div class="form-item" style="align-items:flex-start;">
            <label style="padding-top:10px;">损坏描述</label>
            <textarea name="repairDesc" rows="5" required placeholder="描述设备损坏情况，例如：示波器开机无显示、按键失灵等"></textarea>
        </div>
        <button class="btn btn-primary" type="submit" style="width:100%">提交报修</button>
    </form>
    <p style="color:#94a3b8; font-size:13px; margin-top:14px;">
        提交后设备会立即标记为「维修中」，维修完成后管理员恢复设备为可用状态。
    </p>
</div>

<%@ include file="/WEB-INF/common/bottom.jspf" %>
