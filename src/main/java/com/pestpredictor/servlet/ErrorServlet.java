package com.pestpredictor.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Logger;

@WebServlet("/error")
public class ErrorServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(ErrorServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        handleError(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        handleError(request, response);
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
        String errorMessage = (String) request.getAttribute("jakarta.servlet.error.message");
        Throwable throwable = (Throwable) request.getAttribute("jakarta.servlet.error.exception");

        if (statusCode == null) statusCode = 500;

        if (errorMessage == null || errorMessage.isBlank()) {
            errorMessage = switch (statusCode) {
                case 404 -> "The page you are looking for could not be found.";
                case 403 -> "You do not have permission to access this resource.";
                case 401 -> "Authentication is required to access this page.";
                default -> "An unexpected error occurred.";
            };
        }

        if (throwable != null) {
            logger.severe("Error [" + statusCode + "]: " + throwable.getMessage());
        }

        // ✅ Pass error via URL (since static HTML cannot read request attributes)
        String redirectUrl = request.getContextPath()
                + "/error.html?code=" + statusCode
                + "&msg=" + java.net.URLEncoder.encode(errorMessage, "UTF-8");

        response.sendRedirect(redirectUrl);
    }
}