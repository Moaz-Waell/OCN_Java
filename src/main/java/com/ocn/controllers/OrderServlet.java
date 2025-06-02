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

@WebServlet("/OrderServlet")
public class OrderServlet extends HttpServlet {
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

        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(contextPath + "/aast/UniUserPortal.jsp");
            return;
        }

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        OcnUserBean currentUser = (OcnUserBean) session.getAttribute("currentUser");
        String action = request.getParameter("action");

        if ("viewDetails".equals(action)) {
            String orderIdStr = request.getParameter("orderId");
            if (orderIdStr == null) {
                request.setAttribute("error", "Order ID is missing.");
                response.sendRedirect(contextPath + "/OrderServlet");
                return;
            }
            try {
                int orderId = Integer.parseInt(orderIdStr);
                OrderBean order = orderDAO.getOrderById(orderId);
                if (order == null || order.getUserId() != currentUser.getUserId()) {
                    request.setAttribute("error", "Order not found or access denied.");
                    response.sendRedirect(contextPath + "/OrderServlet");
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
                request.getRequestDispatcher("/user/viewOrderDetails.jsp").forward(request, response);
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid Order ID format.");
                response.sendRedirect(contextPath + "/OrderServlet");
            }
        } else {
            // Default action: List all orders for the user
            List<OrderBean> allOrders = orderDAO.getOrdersByUserId(currentUser.getUserId());
            List<OrderBean> currentOrders = new ArrayList<>();
            List<OrderBean> pastOrders = new ArrayList<>();

            for (OrderBean order : allOrders) {
                if ("Delivered".equalsIgnoreCase(order.getOrderStatus()) || "Cancelled".equalsIgnoreCase(order.getOrderStatus())) {
                    pastOrders.add(order);
                } else {
                    currentOrders.add(order);
                }
            }
            request.setAttribute("currentOrders", currentOrders);
            request.setAttribute("pastOrders", pastOrders);
            request.getRequestDispatcher("/user/orders.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String contextPath = request.getContextPath();

        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(contextPath + "/aast/UniUserPortal.jsp");
            return;
        }

        OcnUserBean currentUser = (OcnUserBean) session.getAttribute("currentUser");
        String action = request.getParameter("action");
        String orderIdStr = request.getParameter("orderId");
        int orderId = 0;

        if (orderIdStr != null && !orderIdStr.isEmpty()) {
            try {
                orderId = Integer.parseInt(orderIdStr);
            } catch (NumberFormatException e) {
                session.setAttribute("error", "Invalid Order ID for action.");
                response.sendRedirect(contextPath + "/OrderServlet");
                return;
            }
        }

        // Add this block for feedback submission
        if ("submitFeedback".equals(action)) {
            try {
                orderId = Integer.parseInt(request.getParameter("orderId"));
                int rating = Integer.parseInt(request.getParameter("rating"));

                OrderBean order = orderDAO.getOrderById(orderId);
                if (order != null && order.getUserId() == currentUser.getUserId()) {
                    boolean success = orderDAO.updateOrderFeedback(orderId, rating);
                    if (success) {
                        session.setAttribute("message", "Feedback submitted successfully!");
                    } else {
                        session.setAttribute("error", "Failed to submit feedback.");
                    }
                } else {
                    session.setAttribute("error", "Order not found or unauthorized.");
                }
            } catch (NumberFormatException e) {
                session.setAttribute("error", "Invalid rating value.");
            }
            response.sendRedirect(contextPath + "/OrderServlet");
            return; // Exit after handling feedback
        }

        if ("cancelOrder".equals(action)) {
            if (orderId > 0) {
                OrderBean order = orderDAO.getOrderById(orderId);
                // Security check + status check (e.g., can only cancel if 'Pending')
                if (order != null && order.getUserId() == currentUser.getUserId() && ("Pending".equalsIgnoreCase(order.getOrderStatus()) || "Processing".equalsIgnoreCase(order.getOrderStatus()))) {
                    boolean success = orderDAO.updateOrderStatus(orderId, "Cancelled");
                    if (success) {
                        session.setAttribute("message", "Order #" + orderId + " has been cancelled.");
                    } else {
                        session.setAttribute("error", "Failed to cancel order #" + orderId + ".");
                    }
                } else {
                    session.setAttribute("error", "Order cannot be cancelled or access denied.");
                }
            }
            response.sendRedirect(contextPath + "/OrderServlet");
        } else if ("reorder".equals(action)) {
            if (orderId > 0) {
                List<OrderDetailBean> orderDetails = orderDAO.getOrderDetailsByOrderId(orderId);
                if (orderDetails != null && !orderDetails.isEmpty()) {
                    cartDAO.clearCart(currentUser.getUserId());
                    boolean allAdded = true;
                    for (OrderDetailBean detail : orderDetails) {
                        MealBean meal = mealDAO.getMealById(detail.getMealId());
                        if (meal != null) {
                            CartBean cartItem = new CartBean();
                            cartItem.setUserId(currentUser.getUserId());
                            cartItem.setMealId(detail.getMealId());
                            cartItem.setQuantity(detail.getQuantity());
                            cartItem.setNote(detail.getNote()); // Carry over notes from previous order
                            if (!cartDAO.addItemToCart(cartItem)) {
                                allAdded = false;
                                break;
                            }
                        }
                    }
                    if (allAdded) {
                        session.setAttribute("message", "Items from order #" + orderId + " have been added to your cart.");
                        response.sendRedirect(contextPath + "/CartServlet");
                        return;
                    } else {
                        session.setAttribute("error", "Could not reorder all items from order #" + orderId + ". Some items may be unavailable.");
                    }
                } else {
                    session.setAttribute("error", "No details found for order #" + orderId + " to reorder.");
                }
            }
            response.sendRedirect(contextPath + "/OrderServlet");
        } else {
            doGet(request, response);
        }
    }
}

