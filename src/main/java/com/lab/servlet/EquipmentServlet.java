package com.lab.servlet;

import com.lab.dao.EquipmentDao;
import com.lab.dao.SysLogDao;
import com.lab.entity.Equipment;
import com.lab.entity.SysLog;
import com.lab.entity.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/equipment")
public class EquipmentServlet extends HttpServlet {
    private final EquipmentDao eqDao = new EquipmentDao();
    private final SysLogDao logDao = new SysLogDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String ctx = req.getContextPath();
        HttpSession session = req.getSession();
        User loginUser = (User) session.getAttribute("loginUser");
        // 过滤器已保证登录，这里再兜底一次
        if (loginUser == null) {
            resp.sendRedirect(ctx + "/login.jsp");
            return;
        }
        Integer uid = loginUser.getUid();
        String act = req.getParameter("act");

        if ("add".equals(act)) {
            String eidStr = req.getParameter("eid");
            String eqName = req.getParameter("eqName");
            // 基础校验：ID为数字且名称非空
            if (eidStr == null || eqName == null || eqName.trim().isEmpty()) {
                req.setAttribute("eqList", eqDao.listAll());
                req.setAttribute("addMsg", "设备ID和设备名称为必填项");
                req.getRequestDispatcher("/pages/equipmentList.jsp").forward(req, resp);
                return;
            }
            Integer eid;
            try {
                eid = Integer.parseInt(eidStr);
            } catch (NumberFormatException e) {
                req.setAttribute("eqList", eqDao.listAll());
                req.setAttribute("addMsg", "设备ID必须为数字");
                req.getRequestDispatcher("/pages/equipmentList.jsp").forward(req, resp);
                return;
            }

            Equipment eq = new Equipment();
            eq.setEid(eid);
            eq.setEqName(eqName.trim());
            eq.setEqModel(req.getParameter("eqModel"));
            eq.setEqSpec(req.getParameter("eqSpec"));
            eq.setEqLocation(req.getParameter("eqLocation"));
            eq.setEqStatus(0); // 新增默认空闲

            if (!eqDao.add(eq)) {
                // 主键冲突或数据库异常
                req.setAttribute("eqList", eqDao.listAll());
                req.setAttribute("addMsg", "新增失败：设备ID " + eid + " 可能已存在");
                req.getRequestDispatcher("/pages/equipmentList.jsp").forward(req, resp);
                return;
            }

            SysLog log = new SysLog();
            log.setOperateUid(uid);
            log.setOperateType("新增设备");
            log.setOperateContent("设备ID：" + eid + "，设备名称：" + eqName);
            logDao.addLog(log);
            resp.sendRedirect(ctx + "/equipment");
        } else if ("del".equals(act)) {
            String eidStr = req.getParameter("eid");
            if (eidStr != null) {
                try {
                    Integer eid = Integer.parseInt(eidStr);
                    eqDao.delete(eid);
                    SysLog log = new SysLog();
                    log.setOperateUid(uid);
                    log.setOperateType("删除设备");
                    log.setOperateContent("设备ID：" + eid);
                    logDao.addLog(log);
                } catch (NumberFormatException ignored) {
                }
            }
            resp.sendRedirect(ctx + "/equipment");
        } else {
            resp.sendRedirect(ctx + "/equipment");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        req.setAttribute("eqList", eqDao.listAll());
        req.getRequestDispatcher("/pages/equipmentList.jsp").forward(req, resp);
    }
}
