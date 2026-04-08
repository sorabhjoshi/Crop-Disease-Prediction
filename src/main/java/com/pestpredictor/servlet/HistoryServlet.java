package com.pestpredictor.servlet;

import com.pestpredictor.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Servlet rendering the prediction history page.
 */
@WebServlet("/history")
public class HistoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ✅ Check login
        if (!SessionUtil.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        // ❌ Removed backend data forwarding (not usable with static HTML)

        // ✅ Redirect to static page
        response.sendRedirect(request.getContextPath() + "/history.html");
    }
}