package com.pestpredictor.servlet;

import com.pestpredictor.util.SessionUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebFilter("/*")
public class AuthFilter implements Filter {

    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/login.html",
            "/register.html",
            "/css/",
            "/js/",
            "/images/",
            "/favicon.ico",
            "/error.html"
    );

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getServletPath();

        // ✅ Allow static + public pages
        boolean isPublic = PUBLIC_PATHS.stream().anyMatch(path::startsWith);

        if (isPublic || path.equals("/") || path.isEmpty()) {
            chain.doFilter(req, res);
            return;
        }

        // ✅ Allow login & register servlets too
        if (path.equals("/login") || path.equals("/register")) {
            chain.doFilter(req, res);
            return;
        }

        // 🔒 Protect everything else
        if (!SessionUtil.isLoggedIn(request)) {
            SessionUtil.setFlashError(request, "Please log in first.");
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        chain.doFilter(req, res);
    }
}