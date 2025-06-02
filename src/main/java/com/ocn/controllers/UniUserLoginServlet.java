package com.ocn.controllers;

import com.ocn.beans.UniUserBean;
import com.ocn.dao.OcnUserDAO;
import com.ocn.dao.UniUserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/UniUserLoginServlet")
public class UniUserLoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int UniUserId = Integer.parseInt(request.getParameter("ID"));
        int UniUserPin = Integer.parseInt(request.getParameter("password"));
        String contextPath = request.getContextPath();

        UniUserBean user = UniUserDAO.validateUniUser(UniUserId, UniUserPin);

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("uniUser", user);
            response.sendRedirect(contextPath + "/aast/UniUserPortal.jsp");
        } else {
            request.setAttribute("error", "Invalid User ID or PIN.");
            request.setAttribute("ID", UniUserId);
            request.setAttribute("pincode", UniUserPin);
            request.getRequestDispatcher("/aast/UniUserLogin.jsp").forward(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String contextPath = request.getContextPath();
        if ("logout".equals(action)) {
            // 1. Invalidate the session
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            // 2. Delete cookies
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("userId") ||
                            cookie.getName().equals("userName") ||
                            cookie.getName().equals("userPhone") ||
                            cookie.getName().equals("userAttendance")) {

                        cookie.setMaxAge(0); // Delete the cookie
                        cookie.setPath(contextPath + "/"); // Match the cookie path
                        response.addCookie(cookie);
                    }
                }
            }

            // 3. Redirect AFTER deleting cookies
            response.sendRedirect(contextPath + "/aast/UniUserLogin.jsp");
        }
    }
}

