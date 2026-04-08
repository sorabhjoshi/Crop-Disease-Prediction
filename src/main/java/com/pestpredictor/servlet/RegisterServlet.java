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
import java.util.logging.Logger;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(RegisterServlet.class.getName());
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (SessionUtil.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        // ✅ redirect to static page
        response.sendRedirect(request.getContextPath() + "/register.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = trim(request.getParameter("username"));
        String email = trim(request.getParameter("email"));
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String fullName = trim(request.getParameter("fullName"));

        String validationError = validateRegistration(username, email, password, confirmPassword);

        if (validationError != null) {
            response.sendRedirect(request.getContextPath() + "/register.html");
            return;
        }

        try {
            User newUser = userService.register(username, email, password, fullName);
            logger.info("New user registered: " + username);

            SessionUtil.setFlashMessage(request, "Registration successful!");
            response.sendRedirect(request.getContextPath() + "/login.html");

        } catch (IllegalArgumentException e) {
            response.sendRedirect(request.getContextPath() + "/register.html");
        }
    }

    private String validateRegistration(String username, String email, String password, String confirm) {
        if (username == null || username.length() < 3) return "Invalid username";
        if (email == null || !email.contains("@")) return "Invalid email";
        if (password == null || password.length() < 6) return "Weak password";
        if (!password.equals(confirm)) return "Passwords mismatch";
        return null;
    }

    private String trim(String s) {
        return s == null ? null : s.trim();
    }
}