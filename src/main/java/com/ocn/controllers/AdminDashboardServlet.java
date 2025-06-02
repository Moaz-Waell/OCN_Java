package com.ocn.controllers;

import com.ocn.beans.OrderBean;
import com.ocn.beans.OrderDetailBean;
import com.ocn.dao.MealDAO;
import com.ocn.dao.OrderDAO;
import com.ocn.dao.OcnUserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/AdminDashboardServlet")
public class AdminDashboardServlet extends HttpServlet {
    private OrderDAO orderDAO;
    private MealDAO mealDAO;
    private OcnUserDAO ocnUserDAO;
    private OrderDetailBean orderDetailBean;

    public void init() {
        ocnUserDAO = new OcnUserDAO();
        mealDAO = new MealDAO();
        orderDAO = new OrderDAO();
        orderDetailBean = new OrderDetailBean();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String contextPath = request.getContextPath();
        if (session == null || session.getAttribute("currentAdmin") == null) {
            response.sendRedirect(contextPath + "/admin/adminLogin.jsp");
            return;
        }
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxies
        // Initialize lists to empty to avoid null values in JSP
        List<OrderBean> activeOrders = new ArrayList<>();
        List<OrderBean> orderHistory = new ArrayList<>();
        double totalRevenueDelivered = 0.0;
        int deliveredCount = 0;
        double todaysRevenue = 0.0;
        int todaysOrders = 0;
        try {
            // Get total revenue and count
            totalRevenueDelivered = orderDAO.getTotalRevenue();
            deliveredCount = orderDAO.getDeliveredOrderCount();

            // Get today's revenue and count
            todaysRevenue = orderDAO.getTodaysTotalRevenue();
            todaysOrders = orderDAO.getTodaysDeliveredOrderCount();

            // Get orders
            activeOrders = orderDAO.getActiveOrders();
            orderHistory = orderDAO.getOrderHistory();

            for (OrderBean orderBean : activeOrders) {
                System.out.println(orderBean);
            }

        } catch (Exception e) {
            System.err.println("Error in AdminDashboardServlet: " + e.getMessage());
            request.setAttribute("error", "Failed to load dashboard data.");
        }

        // Set attributes regardless of exceptions
        request.setAttribute("totalRevenueDelivered", totalRevenueDelivered);
        request.setAttribute("deliveredCount", deliveredCount);
        request.setAttribute("todaysRevenue", todaysRevenue); // Add today's revenue
        request.setAttribute("todaysOrders", todaysOrders);   // Add today's orders
        request.setAttribute("activeOrders_admin", activeOrders);
        request.setAttribute("OrderHistory", orderHistory);

        request.getRequestDispatcher("/admin/adminDashboard.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}

