package com.lab.servlet;

import com.lab.dao.RepairDao;
import com.lab.dao.SysLogDao;
import com.lab.entity.Repair;
import com.lab.entity.SysLog;
import com.lab.entity.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@WebServlet("/repair")
public class RepairServlet extends HttpServlet {
    private final RepairDao repairDao = new RepairDao();
    private final SysLogDao logDao = new SysLogDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String ctx = req.getContextPath();
        HttpSession session = req.getSession();
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            resp.sendRedirect(ctx + "/login.jsp");
            return;
        }
        Integer uid = loginUser.getUid();

        String act = req.getParameter("act");
        if (act == null) {
            resp.sendRedirect(ctx + "/repair?act=list");
            return;
        }

        // 管理员：标记维修完成
        if ("finish".equals(act)) {
            if (!isAdmin(session)) {
                resp.sendRedirect(ctx + "/borrowApply?act=myList");
                return;
            }
            String ridStr = req.getParameter("rid");
            String eidStr = req.getParameter("eid");
            if (ridStr == null || eidStr == null) {
                resp.sendRedirect(ctx + "/repair?act=list");
                return;
            }
            try {
                Integer rid = Integer.parseInt(ridStr);
                Integer eid = Integer.parseInt(eidStr);
                repairDao.finishRepair(rid, eid);

                SysLog log = new SysLog();
                log.setOperateUid(uid);
                log.setOperateType("标记报修维修完成");
                log.setOperateContent("报修单ID：" + rid + "，设备ID：" + eid);
                logDao.addLog(log);
            } catch (NumberFormatException ignored) {
            }
            resp.sendRedirect(ctx + "/repair?act=list");
        }
        // 学生提交报修
        else if ("add".equals(act)) {
            String eidStr = req.getParameter("eid");
            String repairDesc = req.getParameter("repairDesc");
            if (eidStr == null || repairDesc == null || repairDesc.trim().isEmpty()) {
                resp.sendRedirect(ctx + "/repair?act=add");
                return;
            }
            try {
                Repair repair = new Repair();
                repair.setEid(Integer.parseInt(eidStr));
                repair.setReportUid(uid);
                repair.setRepairDesc(repairDesc.trim());
                repair.setReportTime(new Date());
                repair.setRepairStatus(0); // 0=待维修
                repairDao.addRepair(repair);

                SysLog log = new SysLog();
                log.setOperateUid(uid);
                log.setOperateType("提交报修");
                log.setOperateContent("设备ID：" + eidStr);
                logDao.addLog(log);
            } catch (NumberFormatException ignored) {
            }
            resp.sendRedirect(ctx + "/borrowApply?act=myList&msg=repairSuccess");
        }
        // 其他未知act默认跳转列表
        else {
            resp.sendRedirect(ctx + "/repair?act=list");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String ctx = req.getContextPath();
        HttpSession session = req.getSession();
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            resp.sendRedirect(ctx + "/login.jsp");
            return;
        }
        String act = req.getParameter("act");
        if ("list".equals(act)) {
            // 报修列表是管理员页面
            if (!isAdmin(session)) {
                resp.sendRedirect(ctx + "/borrowApply?act=myList");
                return;
            }
            List<Repair> list = repairDao.listAll();
            req.setAttribute("repairList", list);
            req.getRequestDispatcher("/pages/repairList.jsp").forward(req, resp);
        } else if ("add".equals(act)) {
            req.getRequestDispatcher("/pages/stuRepair.jsp").forward(req, resp);
        } else {
            resp.sendRedirect(ctx + "/repair?act=list");
        }
    }

    // 角色校验小工具
    private boolean isAdmin(HttpSession session) {
        Integer role = (Integer) session.getAttribute("role");
        return role != null && role == 1;
    }
}
