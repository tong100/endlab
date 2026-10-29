<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activeNav" value="myList" scope="request" />
<%@ include file="/WEB-INF/common/top.jspf" %>

<h3 class="page-title">我的设备借用申请</h3>

<%-- 提交成功/失败等来自URL参数的提示 --%>
<c:if test="${param.msg == 'applySuccess'}">
    <div class="alert alert-success">借用申请提交成功，请等待管理员审批。</div>
</c:if>
<c:if test="${param.msg == 'repairSuccess'}">
    <div class="alert alert-success">设备报修提交成功，维修人员会尽快处理。</div>
</c:if>

<div class="card">
    <table>
        <thead>
        <tr>
            <th>申请单号</th>
            <th>设备ID</th>
            <th>借用日期</th>
            <th>计划归还</th>
            <th>实际归还</th>
            <th>申请状态</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <c:choose>
            <c:when test="${empty requestScope.borrowList}">
                <tr><td colspan="7" class="empty-tip">暂无借用申请记录，点击右上角「新建借用」提交第一笔申请</td></tr>
            </c:when>
            <c:otherwise>
                <c:forEach items="${requestScope.borrowList}" var="item">
                    <tr>
                        <td>${item.bid}</td>
                        <td>${item.eid}</td>
                        <td><fmt:formatDate value="${item.borrowDate}" pattern="yyyy-MM-dd"/></td>
                        <td><fmt:formatDate value="${item.returnDate}" pattern="yyyy-MM-dd"/></td>
                        <td>
                            <c:choose>
                                <c:when test="${empty item.actualReturnDate}">—</c:when>
                                <c:otherwise><fmt:formatDate value="${item.actualReturnDate}" pattern="yyyy-MM-dd"/></c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${item.applyStatus == 0}"><span class="badge badge-wait">待审批</span></c:when>
                                <c:when test="${item.applyStatus == 1}"><span class="badge badge-borrow">借出中</span></c:when>
                                <c:when test="${item.applyStatus == 2}"><span class="badge badge-reject">已驳回</span></c:when>
                                <c:otherwise><span class="badge badge-returned">已归还</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:if test="${item.applyStatus == 1}">
                                <form action="${ctx}/borrowApply" method="post" style="display:inline">
                                    <input type="hidden" name="act" value="return">
                                    <input type="hidden" name="bid" value="${item.bid}">
                                    <input type="hidden" name="eid" value="${item.eid}">
                                    <button class="btn btn-success btn-sm" type="submit"
                                            onclick="return confirm('确认归还该设备吗？')">归还设备</button>
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

</main>
