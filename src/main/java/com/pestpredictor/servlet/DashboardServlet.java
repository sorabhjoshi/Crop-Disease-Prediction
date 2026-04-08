package com.pestpredictor.servlet;

import com.pestpredictor.model.PredictionHistory;
import com.pestpredictor.model.User;
import com.pestpredictor.service.PredictionService;
import com.pestpredictor.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private final PredictionService predictionService = new PredictionService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!SessionUtil.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        // ❌ remove WEB-INF forward
        // ✅ redirect to static page
        response.sendRedirect(request.getContextPath() + "/dashboard.html");
    }
}