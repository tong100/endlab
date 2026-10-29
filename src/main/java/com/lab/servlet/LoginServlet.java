package com.lab.servlet;

import com.lab.dao.UserDao;
import com.lab.entity.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String ctx = req.getContextPath();
        // 退出登录：销毁会话回到登录页
        if ("logout".equals(req.getParameter("act"))) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            resp.sendRedirect(ctx + "/login.jsp");
            return;
        }
        // 直接访问登录接口时回到登录页
        resp.sendRedirect(ctx + "/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String ctx = req.getContextPath();
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        // 参数为空直接回登录页提示，不发无效SQL
        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            req.setAttribute("msg", "账号和密码不能为空");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        User loginUser;
        try {
            loginUser = userDao.login(username.trim(), password);
        } catch (Exception e) {
            // 数据库连不上等情况，给用户看真实原因而不是误导性的"密码错误"
            req.setAttribute("msg", "数据库连接失败：" + e.getMessage());
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }
        if (loginUser != null) {
            HttpSession session = req.getSession();
            session.setAttribute("loginUser", loginUser);
            session.setAttribute("uid", loginUser.getUid());
            session.setAttribute("role", loginUser.getRole());
            // 管理员进设备管理，学生进我的借用列表
            if (loginUser.getRole() == 1) {
                resp.sendRedirect(ctx + "/equipment");
            } else {
                resp.sendRedirect(ctx + "/borrowApply?act=myList");
            }
        } else {
            req.setAttribute("msg", "账号或密码错误");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }
}
