<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activeNav" value="audit" scope="request" />
<%@ include file="/WEB-INF/common/top.jspf" %>

<h3 class="page-title">借用申请审批</h3>

<div class="card">
    <table>
        <thead>
        <tr>
            <th>申请单号</th>
            <th>申请人ID</th>
            <th>借用设备ID</th>
            <th>借用日期</th>
            <th>计划归还</th>
            <th style="min-width:280px;">审批操作</th>
        </tr>
        </thead>
        <tbody>
        <c:choose>
            <c:when test="${empty requestScope.borrowList}">
                <tr><td colspan="6" class="empty-tip">没有待审批的申请，干得漂亮 ☕</td></tr>
            </c:when>
            <c:otherwise>
                <c:forEach items="${requestScope.borrowList}" var="item">
                    <tr>
                        <td>${item.bid}</td>
                        <td>${item.uid}</td>
                        <td>${item.eid}</td>
                        <td><fmt:formatDate value="${item.borrowDate}" pattern="yyyy-MM-dd"/></td>
                        <td><fmt:formatDate value="${item.returnDate}" pattern="yyyy-MM-dd"/></td>
                        <td>
                            <div class="row-actions">
                                <form action="${ctx}/audit" method="post" style="display:inline; display:flex; gap:6px;">
                                    <input type="hidden" name="bid" value="${item.bid}">
                                    <input type="hidden" name="eid" value="${item.eid}">
                                    <input type="hidden" name="opt" value="pass">
                                    <input type="text" name="auditMsg" placeholder="审批意见（可空）"
                                           style="height:34px; padding:0 8px; border:1px solid #d1d9e6; border-radius:5px;">
                                    <button class="btn btn-success btn-sm" type="submit"
                                            onclick="return confirm('确认通过该借用申请吗？通过后设备将标记为借出。')">通过</button>
                                </form>
                                <form action="${ctx}/audit" method="post" style="display:inline; display:flex; gap:6px;">
                                    <input type="hidden" name="bid" value="${item.bid}">
                                    <input type="hidden" name="opt" value="reject">
                                    <input type="text" name="auditMsg" placeholder="驳回理由（可空）"
                                           style="height:34px; padding:0 8px; border:1px solid #d1d9e6; border-radius:5px;">
                                    <button class="btn btn-danger btn-sm" type="submit"
                                            onclick="return confirm('确认驳回该申请吗？')">驳回</button>
                                </form>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
            </c:otherwise>
        </c:choose>
        </tbody>
    </table>
</div>

</main>
