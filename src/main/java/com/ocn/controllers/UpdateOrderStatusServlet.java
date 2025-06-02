package com.ocn.controllers;

import com.ocn.dao.OrderDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/UpdateOrderStatus")
public class UpdateOrderStatusServlet extends HttpServlet {
    private OrderDAO orderDAO;

    public void init() {
        orderDAO = new OrderDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int orderId = Integer.parseInt(request.getParameter("orderId"));
        String status = request.getParameter("status");

        try {
            String currentStatus = orderDAO.getOrderById(orderId).getOrderStatus();

            if(isValidTransition(currentStatus, status)) {
                boolean updated = orderDAO.updateOrderStatus(orderId, status);
                if(updated) {
                    request.getSession().setAttribute("success", "Order #" + orderId + " status updated to " + status);
                } else {
                    request.getSession().setAttribute("error", "Failed to update order status");
                }
            } else {
                request.getSession().setAttribute("error", "Invalid status transition from " + currentStatus + " to " + status);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "Error updating status: " + e.getMessage());
        }

        response.sendRedirect("KitchenDashboardServlet");
    }

    private boolean isValidTransition(String current, String newStatus) {
        if("Preparing".equalsIgnoreCase(newStatus)) return "pending".equalsIgnoreCase(current);
        if("Out For Delivery".equalsIgnoreCase(newStatus)) return "preparing".equalsIgnoreCase(current);
        return false;
    }
}