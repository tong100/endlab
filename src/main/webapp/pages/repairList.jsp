<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activeNav" value="repair" scope="request" />
<%@ include file="/WEB-INF/common/top.jspf" %>

<h3 class="page-title">报修记录管理</h3>

<div class="card">
    <table>
        <thead>
        <tr>
            <th>报修单号</th>
            <th>设备ID</th>
            <th>报修人ID</th>
            <th>损坏描述</th>
            <th>报修时间</th>
            <th>维修状态</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <c:choose>
            <c:when test="${empty requestScope.repairList}">
                <tr><td colspan="7" class="empty-tip">暂无报修记录</td></tr>
            </c:when>
            <c:otherwise>
                <c:forEach items="${requestScope.repairList}" var="r">
                    <tr>
                        <td>${r.rid}</td>
                        <td>${r.eid}</td>
                        <td>${r.reportUid}</td>
                        <td style="text-align:left;">${r.repairDesc}</td>
                        <td><fmt:formatDate value="${r.reportTime}" pattern="yyyy-MM-dd HH:mm"/></td>
                        <td>
                            <c:choose>
                                <c:when test="${r.repairStatus == 0}"><span class="badge badge-repair">待维修</span></c:when>
                                <c:otherwise><span class="badge badge-pass">维修完成</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:if test="${r.repairStatus == 0}">
                                <form action="${ctx}/repair" method="post" style="display:inline">
                                    <input type="hidden" name="act" value="finish">
                                    <input type="hidden" name="rid" value="${r.rid}">
                                    <input type="hidden" name="eid" value="${r.eid}">
                                    <button class="btn btn-success btn-sm" type="submit"
                                            onclick="return confirm('确认维修完成吗？设备将恢复为空闲可借用状态。')">标记维修完成</button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </c:otherwise>
        </c:choose>
        </tbody>
    </table>
</div>

<%@ include file="/WEB-INF/common/bottom.jspf" %>
