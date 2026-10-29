<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="activeNav" value="add" scope="request" />
<%@ include file="/WEB-INF/common/top.jspf" %>

<h3 class="page-title">提交设备借用申请</h3>

<%-- 后端校验失败的错误提示 --%>
<c:if test="${not empty requestScope.error}">
    <div class="alert alert-error">⚠ ${requestScope.error}</div>
</c:if>

<div class="wrap" style="display:flex; gap:24px; align-items:flex-start; flex-wrap:wrap;">
    <%-- 左侧：申请表单 --%>
    <div class="card" style="width:400px; flex-shrink:0;">
        <h4>填写申请信息</h4>
        <form action="${ctx}/borrowApply" method="post" id="applyForm">
            <input type="hidden" name="act" value="add">
            <div class="form-item">
                <label>借用设备ID</label>
                <input type="number" name="eid" id="eidInput" required
                       value="${fn:escapeXml(requestScope.eidInput)}" placeholder="点击右侧「选用」自动填入">
            </div>
            <div class="form-item">
                <label>借用日期</label>
                <input type="date" name="borrowDate" required
                       value="${fn:escapeXml(requestScope.borrowDateInput)}">
            </div>
            <div class="form-item">
                <label>计划归还日期</label>
                <input type="date" name="returnDate" required
                       value="${fn:escapeXml(requestScope.returnDateInput)}">
            </div>
            <button class="btn btn-primary" type="submit" style="width:100%">提交申请</button>
        </form>
        <p style="color:#94a3b8; font-size:13px; margin-top:14px;">
            借用日期不能早于今天，最长借用 30 天，同一设备同一时间段仅允许一人借用。
        </p>
    </div>

    <%-- 右侧：空闲设备列表 --%>
    <div class="card" style="flex:1; min-width:480px;">
        <h4>当前可借用的空闲设备</h4>
        <table>
            <thead>
            <tr>
                <th>设备ID</th>
                <th>设备名称</th>
                <th>规格参数</th>
                <th>存放实验室</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <c:choose>
                <c:when test="${empty requestScope.freeEqList}">
                    <tr><td colspan="5" class="empty-tip">暂无空闲可借用设备</td></tr>
                </c:when>
                <c:otherwise>
                    <c:forEach items="${requestScope.freeEqList}" var="item">
                        <tr>
                            <td>${item.eid}</td>
                            <td>${item.eqName}</td>
                            <td>${item.eqSpec}</td>
                            <td>${item.eqLocation}</td>
                            <td>
                                <button class="btn btn-outline btn-sm" type="button"
                                        onclick="document.getElementById('eidInput').value='${item.eid}'">选用</button>
                            </td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
            </tbody>
        </table>
    </div>
</div>

<%@ include file="/WEB-INF/common/bottom.jspf" %>
