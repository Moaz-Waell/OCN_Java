package com.ocn.controllers;


import com.ocn.beans.*;
import com.ocn.dao.CartDAO;
import com.ocn.dao.CategoryDAO;
import com.ocn.dao.MealDAO;
import com.ocn.dao.OrderDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/AdminOrderServlet" )
public class AdminOrderServlet extends HttpServlet {
    private OrderDAO orderDAO;
    private MealDAO mealDAO; // For reorder and order details
    private CartDAO cartDAO; // For reorder

    public void init() {
        orderDAO = new OrderDAO();
        mealDAO = new MealDAO();
        cartDAO = new CartDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String contextPath = request.getContextPath();

        if (session == null || session.getAttribute("currentAdmin") == null) {
            response.sendRedirect(contextPath + "/admin/adminLogin.jsp");
            return;
        }

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxies

        OcnUserBean currentUser = (OcnUserBean) session.getAttribute("currentUser");
        AdminBean admin = (AdminBean) session.getAttribute("currentAdmin");
        String action = request.getParameter("action");

        if ("viewDetails".equals(action)) {
            String orderIdStr = request.getParameter("orderId");
            if (orderIdStr == null) {
                request.setAttribute("error", "Order ID is missing.");
                response.sendRedirect(contextPath + "/AdminOrderServlet");
                return;
            }
            try {
                int orderId = Integer.parseInt(orderIdStr);
                OrderBean order = orderDAO.getOrderById(orderId);
                if (order == null) {
                    request.setAttribute("error", "Order not found or access denied.");
                    response.sendRedirect(contextPath + "/AdminOrderServlet");
                    return;
                }
                List<OrderDetailBean> orderDetails = orderDAO.getOrderDetailsByOrderId(orderId);
                List<OrderDetailDTO> orderDetailDTOs = new ArrayList<>();

                for (OrderDetailBean detail : orderDetails) {
                    orderDetailDTOs.add(new OrderDetailDTO(detail, detail.getMeal()));
                }
                System.out.println("Order Details DTOs count: " + orderDetailDTOs.size()); // Check if list is populated
// In           OrderServlet.doGet()
                System.out.println("[DEBUG] Viewing details for orderId: " + orderId);
                order = orderDAO.getOrderById(orderId);
                System.out.println("[DEBUG] Order found: " + (order != null));

                request.setAttribute("order", order);
                request.setAttribute("orderDetails", orderDetailDTOs); // Rename attribute
                request.getRequestDispatcher("/admin/viewAdminOrderDetails.jsp").forward(request, response);
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid Order ID format.");
                response.sendRedirect(contextPath + "/AdminOrderServlet");
            }
        }
//        else {
//            String orderIdStr = request.getParameter("orderId");
//            int orderId = Integer.parseInt(orderIdStr);
//            int orderUserId = orderDAO.getOrderById(orderId).getUserId();
//            List<OrderBean> allOrders = orderDAO.getOrdersByUserId(orderUserId);
//            List<OrderBean> currentOrders = new ArrayList<>();
//            List<OrderBean> pastOrders = new ArrayList<>();
//
//            for (OrderBean order : allOrders) {
//                if ("Delivered".equalsIgnoreCase(order.getOrderStatus()) || "Cancelled".equalsIgnoreCase(order.getOrderStatus())) {
//                    pastOrders.add(order);
//                } else {
//                    currentOrders.add(order);
//                }
//            }
//            request.setAttribute("currentOrders", currentOrders);
//            request.setAttribute("pastOrders", pastOrders);
//            request.getRequestDispatcher("/user/orders.jsp").forward(request, response);
//        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String contextPath = request.getContextPath();

        if (session == null || session.getAttribute("currentAdmin") == null) {
            response.sendRedirect(contextPath + "/admin/adminLogin.jsp");
            return;
        }

//        AdminBean currentUser = (AdminBean) session.getAttribute("currentAdmin");
        String action = request.getParameter("action");
        String orderIdStr = request.getParameter("orderId");
        int orderId = 0;

        if (orderIdStr != null && !orderIdStr.isEmpty()) {
            try {
                orderId = Integer.parseInt(orderIdStr);
            } catch (NumberFormatException e) {
                session.setAttribute("error", "Invalid Order ID for action.");
                response.sendRedirect(contextPath + "/admin/adminLogin.jsp");
                return;
            }
        }

        if ("cancelOrder".equals(action)) {
            if (orderId > 0) {
                OrderBean order = orderDAO.getOrderById(orderId);
                // Security check + status check (e.g., can only cancel if 'Pending')
                if (order != null  && ("Pending".equalsIgnoreCase(order.getOrderStatus()) ||
                        "Processing".equalsIgnoreCase(order.getOrderStatus()) ||
                        "In Progress".equalsIgnoreCase(order.getOrderStatus()) ||
                        "Out For Delivery".equalsIgnoreCase(order.getOrderStatus()) ||
                        "Preparing".equalsIgnoreCase(order.getOrderStatus()))) {
                    boolean success = orderDAO.updateOrderStatus(orderId, "Cancelled");
                    if (success) {
                        session.setAttribute("success", "Order #" + orderId + " has been cancelled.");
                    } else {
                        session.setAttribute("error", "Failed to cancel order #" + orderId + ".");
                    }
                } else {
                    session.setAttribute("error", "Order cannot be cancelled or access denied.");
                }
            }
            response.sendRedirect(contextPath + "/AdminDashboardServlet");
        }
        else if ("deliverOrder".equals(action)) {
            if (orderId > 0) {
                OrderBean order = orderDAO.getOrderById(orderId);
                // Check if order exists and can be delivered
                if (order != null && ("Pending".equalsIgnoreCase(order.getOrderStatus()) ||
                        "Processing".equalsIgnoreCase(order.getOrderStatus()) ||
                        "In Progress".equalsIgnoreCase(order.getOrderStatus()) ||
                        "Out For Delivery".equalsIgnoreCase(order.getOrderStatus()) ||
                        "Preparing".equalsIgnoreCase(order.getOrderStatus()))) {
                    boolean success = orderDAO.updateOrderStatus(orderId, "Delivered");
                    if (success) {
                        session.setAttribute("success", "Order #" + orderId + " has been marked as delivered.");
                    } else {
                        session.setAttribute("error", "Failed to mark order #" + orderId + " as delivered.");
                    }
                } else {
                    session.setAttribute("error", "Order cannot be marked as delivered or access denied.");
                }
            }
            response.sendRedirect(contextPath + "/AdminDashboardServlet");
        }
        else {
            doGet(request, response);
        }
    }
}
