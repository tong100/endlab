package com.lab.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录过滤器：未登录用户一律打回登录页
 * 放行范围：静态资源、登录页、登录/退出接口
 * 管理员接口（审批、日志、报修管理）额外校验角色
 */
@WebFilter("/*")
public class LoginFilter implements Filter {
    @Override
    public void doFilter(javax.servlet.ServletRequest req, javax.servlet.ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        String ctx = request.getContextPath();
        String path = request.getRequestURI().substring(ctx.length());

        // 静态资源与登录相关接口放行
        if (path.startsWith("/static/") || path.equals("/login") || path.equals("/login.jsp")
                || path.equals("/index.jsp") || path.equals("/") || path.isEmpty()) {
            chain.doFilter(req, resp);
            return;
        }

        Object loginUser = request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            response.sendRedirect(ctx + "/login.jsp");
            return;
        }

        // 管理员专属接口的角色校验
        boolean adminOnly = path.startsWith("/audit") || path.startsWith("/sysLog")
                || path.startsWith("/equipment");
        if (adminOnly) {
            Integer role = (Integer) request.getSession().getAttribute("role");
            if (role == null || role != 1) {
                // 学生没有权限，打回自己的借用列表
                response.sendRedirect(ctx + "/borrowApply?act=myList");
                return;
            }
        }

        chain.doFilter(req, resp);
    }
}
