package com.ocn.controllers;

import com.ocn.beans.AdminBean;
import com.ocn.dao.AdminDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/AdminLoginServlet")
public class AdminLoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxies
        String AdminId = request.getParameter("ID");
        String AdminPin = request.getParameter("password");
        String contextPath = request.getContextPath();

        AdminBean admin = AdminDAO.validateAdmin(AdminId, AdminPin);

        if (admin != null) {
            HttpSession session = request.getSession();
            session.setAttribute("currentAdmin", admin);
            response.sendRedirect(contextPath + "/AdminDashboardServlet");
        } else {
            request.setAttribute("error", "Invalid Admin ID or PIN.");
            request.setAttribute("ID", AdminId);
            request.setAttribute("pincode", AdminPin);
            request.getRequestDispatcher("/admin/adminLogin.jsp").forward(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String contextPath = request.getContextPath();
        if ("logout".equals(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(contextPath + "/admin/adminLogin.jsp");
        }
    }
}

