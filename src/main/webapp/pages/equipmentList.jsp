<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="activeNav" value="equipment" scope="request" />
<%@ include file="/WEB-INF/common/top.jspf" %>

<h3 class="page-title">设备管理</h3>

<%-- 新增失败提示 --%>
<c:if test="${not empty requestScope.addMsg}">
    <div class="alert alert-error">⚠ ${requestScope.addMsg}</div>
</c:if>

<%-- 新增设备 --%>
<div class="card">
    <h4>新增设备</h4>
    <form action="${ctx}/equipment" method="post">
        <input type="hidden" name="act" value="add">
        <div style="display:flex; gap:16px; flex-wrap:wrap;">
            <div class="form-item" style="flex:1; min-width:140px;">
                <label style="width:auto;">设备ID</label>
                <input type="number" name="eid" required placeholder="数字编号">
            </div>
            <div class="form-item" style="flex:2; min-width:180px;">
                <label style="width:auto;">设备名称</label>
                <input name="eqName" required placeholder="如：数字示波器">
            </div>
            <div class="form-item" style="flex:2; min-width:180px;">
                <label style="width:auto;">设备型号</label>
                <input name="eqModel" placeholder="如：DS1054Z">
            </div>
            <div class="form-item" style="flex:2; min-width:180px;">
                <label style="width:auto;">规格参数</label>
                <input name="eqSpec" placeholder="如：50MHz四通道">
            </div>
            <div class="form-item" style="flex:2; min-width:180px;">
                <label style="width:auto;">存放实验室</label>
                <input name="eqLocation" placeholder="如：物理实验室101">
            </div>
        </div>
        <button class="btn btn-primary" type="submit">新增设备</button>
    </form>
</div>

<%-- 设备列表 --%>
<div class="card">
    <table>
        <thead>
        <tr>
            <th>设备ID</th>
            <th>名称</th>
            <th>型号</th>
            <th>规格</th>
            <th>存放位置</th>
            <th>状态</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <c:choose>
            <c:when test="${empty requestScope.eqList}">
                <tr><td colspan="7" class="empty-tip">暂无设备数据，请先在上方新增</td></tr>
            </c:when>
            <c:otherwise>
                <c:forEach items="${requestScope.eqList}" var="item">
                    <tr>
                        <td>${item.eid}</td>
                        <td>${item.eqName}</td>
                        <td>${item.eqModel}</td>
                        <td>${item.eqSpec}</td>
                        <td>${item.eqLocation}</td>
                        <td>
                            <c:choose>
                                <c:when test="${item.eqStatus == 0}"><span class="badge badge-free">空闲</span></c:when>
                                <c:when test="${item.eqStatus == 1}"><span class="badge badge-borrow">借出中</span></c:when>
                                <c:when test="${item.eqStatus == 2}"><span class="badge badge-repair">维修中</span></c:when>
                                <c:otherwise><span class="badge badge-scrap">报废</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <form action="${ctx}/equipment" method="post" style="display:inline">
                                <input type="hidden" name="act" value="del">
                                <input type="hidden" name="eid" value="${item.eid}">
                                <button class="btn btn-danger btn-sm" type="submit"
                                        onclick="return confirm('确认删除设备「${item.eqName}」吗？此操作不可恢复。')">删除</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </c:otherwise>
        </c:choose>
        </tbody>
    </table>
</div>

</main>
