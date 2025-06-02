package com.ocn.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/OcnLogoutServlet")
public class OcnLogoutServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String contextPath = request.getContextPath();

        if (session != null) {
            session.invalidate();
        }
        // Prevent caching
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // Inside OcnLogoutServlet's doGet method
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("userId") ||
                        cookie.getName().equals("userName") ||
                        cookie.getName().equals("userPhone") ||
                        cookie.getName().equals("userAttendance") ||
                        cookie.getName().equals("userEmail")) { // Add userEmail
                    cookie.setMaxAge(0);
                    cookie.setPath(contextPath + "/");
                    response.addCookie(cookie);
                }
            }
        }
        response.sendRedirect(request.getContextPath() + "/aast/UniUserLogin.jsp");

    }
}