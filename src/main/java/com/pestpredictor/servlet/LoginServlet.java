package com.pestpredictor.servlet;

import com.pestpredictor.model.User;
import com.pestpredictor.service.UserService;
import com.pestpredictor.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(LoginServlet.class.getName());
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (SessionUtil.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        // ✅ ALWAYS go to static page
        response.sendRedirect(request.getContextPath() + "/login.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = trim(request.getParameter("username"));
        String password = request.getParameter("password");

        // ❌ NO FORWARDING ANYWHERE

        // Validation
        if (username == null || username.isEmpty() ||
            password == null || password.isEmpty()) {

            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        Optional<User> userOpt = userService.authenticate(username, password);

        // Invalid login
        if (userOpt.isEmpty()) {
            logger.warning("Failed login attempt for: " + username);

            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        // Success
        User user = userOpt.get();
        SessionUtil.setLoggedInUser(request, user);

        logger.info("User logged in: " + username);

        response.sendRedirect(request.getContextPath() + "/dashboard");
    }

    private String trim(String s) {
        return s == null ? null : s.trim();
    }
}