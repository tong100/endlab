package com.lab.servlet;

import com.lab.dao.SysLogDao;
import com.lab.entity.SysLog;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/sysLog")
public class LogServlet extends HttpServlet {
    private final SysLogDao logDao = new SysLogDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        // 查询全部日志
        List<SysLog> logList = logDao.listAllLog();
        // 存入request域，供JSP读取
        req.setAttribute("logList", logList);
        // 转发到pages下的日志页面
        req.getRequestDispatcher("pages/sysLog.jsp").forward(req, resp);
    }
}
