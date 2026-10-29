package com.lab.servlet;

import com.lab.dao.BorrowApplyDao;
import com.lab.dao.EquipmentDao;
import com.lab.dao.SysLogDao;
import com.lab.entity.BorrowApply;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@WebServlet("/borrowApply")
public class BorrowApplyServlet extends HttpServlet {
    private final BorrowApplyDao borrowDao = new BorrowApplyDao();
    private final EquipmentDao eqDao = new EquipmentDao();
    private final SysLogDao logDao = new SysLogDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String act = req.getParameter("act");
        HttpSession session = req.getSession();
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        Integer uid = loginUser.getUid();

        // 我的借用列表
        if ("myList".equals(act)) {
            List<BorrowApply> borrowList = borrowDao.listByUid(uid);
            req.setAttribute("borrowList", borrowList);
            req.getRequestDispatcher("/pages/stuApplyList.jsp").forward(req, resp);
        }
        // 跳转新建借用申请页面，查询空闲设备传给页面
        else if ("add".equals(act)) {
            List<Equipment> freeEqList = eqDao.listFree();
            req.setAttribute("freeEqList", freeEqList);
            req.getRequestDispatcher("/pages/stuBorrow.jsp").forward(req, resp);
        }
        // 无效act默认回我的列表
        else {
            resp.sendRedirect(req.getContextPath() + "/borrowApply?act=myList");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String ctx = req.getContextPath();
        String act = req.getParameter("act");
        HttpSession session = req.getSession();
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            resp.sendRedirect(ctx + "/login.jsp");
            return;
        }
        Integer uid = loginUser.getUid();

        // 提交借用申请（含完整日历限制校验）
        if ("add".equals(act)) {
            List<String> errors = new ArrayList<>();
            Integer eid = null;
            Date borrowDate = null;
            Date returnDate = null;

            // --- 1. 参数解析与容错 ---
            String eidStr = req.getParameter("eid");
            String borrowDateStr = req.getParameter("borrowDate");
            String returnDateStr = req.getParameter("returnDate");

            // 设备ID校验
            try {
                eid = Integer.parseInt(eidStr);
                if (eid <= 0) {
                    errors.add("设备ID必须为正整数");
                }
            } catch (NumberFormatException e) {
                errors.add("设备ID格式不正确，请输入数字");
            }

            // 借用日期格式校验
            try {
                borrowDate = java.sql.Date.valueOf(borrowDateStr);
            } catch (IllegalArgumentException e) {
                errors.add("借用日期格式不正确，请选择有效日期");
            }

            // 归还日期格式校验
            try {
                returnDate = java.sql.Date.valueOf(returnDateStr);
            } catch (IllegalArgumentException e) {
                errors.add("归还日期格式不正确，请选择有效日期");
            }

            // --- 2. 日历限制校验 ---
            if (borrowDate != null && returnDate != null) {
                // 今天 00:00:00
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                Date today = cal.getTime();

                if (borrowDate.before(today)) {
                    errors.add("借用日期不能早于今天");
                }
                if (!returnDate.after(borrowDate)) {
                    errors.add("计划归还日期必须晚于借用日期");
                }
                // 最长借用期限 30 天
                long diffDays = (returnDate.getTime() - borrowDate.getTime()) / (1000L * 60 * 60 * 24);
                if (diffDays > 30) {
                    errors.add("借用期限不能超过30天，当前选择了 " + diffDays + " 天");
                }
            }

            // --- 3. 设备存在性与状态校验 ---
            if (eid != null && eid > 0 && errors.isEmpty()) {
                Equipment targetEq = eqDao.getById(eid);
                if (targetEq == null) {
                    errors.add("设备ID不存在，请从右侧空闲列表中选择");
                } else if (targetEq.getEqStatus() != 0) {
                    errors.add("设备「" + targetEq.getEqName() + "」当前状态不可借用");
                }
            }

            // --- 4. 时间段冲突检测 ---
            if (eid != null && eid > 0 && borrowDate != null && returnDate != null && errors.isEmpty()) {
                if (borrowDao.checkOverlap(eid, borrowDate, returnDate)) {
                    errors.add("该设备在所选时间段内已被其他申请占用，请更换设备或调整日期");
                }
            }

            // --- 校验失败：回到表单页并回填 ---
            if (!errors.isEmpty()) {
                req.setAttribute("error", String.join("；", errors));
                req.setAttribute("eidInput", eidStr);
                req.setAttribute("borrowDateInput", borrowDateStr);
                req.setAttribute("returnDateInput", returnDateStr);
                req.setAttribute("freeEqList", eqDao.listFree());
                req.getRequestDispatcher("/pages/stuBorrow.jsp").forward(req, resp);
                return;
            }

            // --- 校验通过：提交申请 ---
            BorrowApply apply = new BorrowApply();
            apply.setUid(uid);
            apply.setEid(eid);
            apply.setBorrowDate(borrowDate);
            apply.setReturnDate(returnDate);
            borrowDao.addApply(apply);

            SysLog log = new SysLog();
            log.setOperateUid(uid);
            log.setOperateType("提交借用申请");
            log.setOperateContent("设备ID：" + eid + "，借用日期：" + borrowDate + "，计划归还：" + returnDate);
            logDao.addLog(log);

            resp.sendRedirect(ctx + "/borrowApply?act=myList&msg=applySuccess");
        }
        // 归还设备【事务在Dao内，含状态校验，防止重复归还】
        else if ("return".equals(act)) {
            String bidStr = req.getParameter("bid");
            String eidStr = req.getParameter("eid");
            if (bidStr == null || eidStr == null) {
                resp.sendRedirect(ctx + "/borrowApply?act=myList");
                return;
            }
            try {
                Integer bid = Integer.parseInt(bidStr);
                Integer eid = Integer.parseInt(eidStr);
                borrowDao.returnEquip(bid, eid);

                SysLog log = new SysLog();
                log.setOperateUid(uid);
                log.setOperateType("设备归还登记");
                log.setOperateContent("申请单ID：" + bid + "，设备ID：" + eid);
                logDao.addLog(log);
            } catch (NumberFormatException ignored) {
            }
            resp.sendRedirect(ctx + "/borrowApply?act=myList");
        }
        // 未知act回列表
        else {
            resp.sendRedirect(ctx + "/borrowApply?act=myList");
        }
    }
}
