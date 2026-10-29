package com.lab.servlet;

import com.lab.dao.BorrowApplyDao;
import com.lab.dao.SysLogDao;
import com.lab.entity.BorrowApply;
import com.lab.entity.SysLog;
import com.lab.entity.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/audit")
public class AuditServlet extends HttpServlet {
    private final BorrowApplyDao borrowDao = new BorrowApplyDao();
    private final SysLogDao logDao = new SysLogDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        List<BorrowApply> borrowList = borrowDao.listWaitAudit();
        req.setAttribute("borrowList", borrowList);
        req.getRequestDispatcher("/pages/auditList.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String ctx = req.getContextPath();
        HttpSession session = req.getSession();
        User loginAdmin = (User) session.getAttribute("loginUser");
        if (loginAdmin == null) {
            resp.sendRedirect(ctx + "/login.jsp");
            return;
        }
        Integer adminUid = loginAdmin.getUid();

        // 参数容错：缺参直接回列表
        String bidStr = req.getParameter("bid");
        String opt = req.getParameter("opt");
        String auditMsg = req.getParameter("auditMsg");
        if (bidStr == null || opt == null) {
            resp.sendRedirect(ctx + "/audit");
            return;
        }

        try {
            Integer bid = Integer.parseInt(bidStr);
            if ("pass".equals(opt)) {
                String eidStr = req.getParameter("eid");
                if (eidStr == null) {
                    resp.sendRedirect(ctx + "/audit");
                    return;
                }
                Integer eid = Integer.parseInt(eidStr);
                borrowDao.auditPass(bid, adminUid, auditMsg, eid);

                SysLog log = new SysLog();
                log.setOperateUid(adminUid);
                log.setOperateType("审批通过");
                log.setOperateContent("申请单ID：" + bid + "，设备ID：" + eid);
                logDao.addLog(log);
            } else if ("reject".equals(opt)) {
                borrowDao.auditReject(bid, adminUid, auditMsg);

                SysLog log = new SysLog();
                log.setOperateUid(adminUid);
                log.setOperateType("审批驳回");
                log.setOperateContent("申请单ID：" + bid + "，理由：" + auditMsg);
                logDao.addLog(log);
            }
        } catch (NumberFormatException ignored) {
        }

        resp.sendRedirect(ctx + "/audit");
    }
}
