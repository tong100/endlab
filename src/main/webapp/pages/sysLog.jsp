<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activeNav" value="sysLog" scope="request" />
<%@ include file="/WEB-INF/common/top.jspf" %>

<h3 class="page-title">系统操作日志</h3>

<div class="card">
    <table>
        <thead>
        <tr>
            <th>日志ID</th>
            <th>操作人ID</th>
            <th>操作类型</th>
            <th>操作详情</th>
            <th>操作时间</th>
        </tr>
        </thead>
        <tbody>
        <c:choose>
            <c:when test="${empty requestScope.logList}">
                <tr><td colspan="5" class="empty-tip">暂无操作日志</td></tr>
            </c:when>
            <c:otherwise>
                <c:forEach items="${requestScope.logList}" var="log">
                    <tr>
                        <td>${log.lid}</td>
                        <td>${log.operateUid}</td>
                        <td>${log.operateType}</td>
                        <td style="text-align:left;">${log.operateContent}</td>
                        <td><fmt:formatDate value="${log.operateTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    </tr>
                </c:forEach>
            </c:otherwise>
        </c:choose>
        </tbody>
    </table>
</div>

<%@ include file="/WEB-INF/common/bottom.jspf" %>
