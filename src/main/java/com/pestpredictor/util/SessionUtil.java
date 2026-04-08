package com.pestpredictor.util;

import com.pestpredictor.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Utility class for servlet-based session management.
 * Provides consistent access to session attributes across servlets.
 */
public class SessionUtil {

    public static final String SESSION_USER = "loggedInUser";
    public static final String SESSION_USER_ID = "userId";
    public static final String SESSION_USERNAME = "username";
    public static final String SESSION_MESSAGE = "flashMessage";
    public static final String SESSION_ERROR = "flashError";

    private SessionUtil() {}

    /**
     * Stores the logged-in user in the HTTP session.
     */
    public static void setLoggedInUser(HttpServletRequest request, User user) {
        HttpSession session = request.getSession(true);
        session.setAttribute(SESSION_USER, user);
        session.setAttribute(SESSION_USER_ID, user.getId());
        session.setAttribute(SESSION_USERNAME, user.getUsername());
        session.setMaxInactiveInterval(30 * 60); // 30 minutes
    }

    /**
     * Retrieves the logged-in user from the session.
     */
    public static User getLoggedInUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        return (User) session.getAttribute(SESSION_USER);
    }

    /**
     * Retrieves the logged-in user ID from the session.
     */
    public static Long getLoggedInUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        return (Long) session.getAttribute(SESSION_USER_ID);
    }

    /**
     * Checks if a user is currently logged in.
     */
    public static boolean isLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute(SESSION_USER) != null;
    }

    /**
     * Invalidates the current session (logout).
     */
    public static void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    /**
     * Sets a flash message (shown once, then cleared).
     */
    public static void setFlashMessage(HttpServletRequest request, String message) {
        request.getSession(true).setAttribute(SESSION_MESSAGE, message);
    }

    /**
     * Gets and clears a flash message.
     */
    public static String consumeFlashMessage(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        String msg = (String) session.getAttribute(SESSION_MESSAGE);
        session.removeAttribute(SESSION_MESSAGE);
        return msg;
    }

    /**
     * Sets a flash error message.
     */
    public static void setFlashError(HttpServletRequest request, String error) {
        request.getSession(true).setAttribute(SESSION_ERROR, error);
    }

    /**
     * Gets and clears a flash error.
     */
    public static String consumeFlashError(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        String err = (String) session.getAttribute(SESSION_ERROR);
        session.removeAttribute(SESSION_ERROR);
        return err;
    }
}
