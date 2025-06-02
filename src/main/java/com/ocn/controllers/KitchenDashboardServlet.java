package com.ocn.controllers;

import com.ocn.beans.OrderBean;
import com.ocn.dao.OrderDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/KitchenDashboardServlet")
public class KitchenDashboardServlet extends HttpServlet {
    private OrderDAO orderDAO;

    public void init() {
        orderDAO = new OrderDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<Integer, OrderBean> groupedOrders = new HashMap<>();

        try {
            // Get kitchen orders with priority
            List<OrderBean> orders = orderDAO.getKitchenOrders();

            groupedOrders = groupOrders(orders);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading orders: " + e.getMessage());
        } finally {
            // Always set attributes and handle messages
            request.setAttribute("orders", groupedOrders);
            request.setAttribute("currentTime", System.currentTimeMillis());

            // Handle session messages
            String error = (String) request.getSession().getAttribute("error");
            String success = (String) request.getSession().getAttribute("success");
            if(error != null) request.setAttribute("error", error);
            if(success != null) request.setAttribute("success", success);
            request.getSession().removeAttribute("error");
            request.getSession().removeAttribute("success");

            try {
                request.getRequestDispatcher("/admin/kitchenDashboard.jsp").forward(request, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Map<Integer, OrderBean> groupOrders(List<OrderBean> orders) {
        Map<Integer, OrderBean> grouped = new HashMap<>();
        for(OrderBean order : orders) {
            if(!grouped.containsKey(order.getOrderId())) {
                // Initialize meals list
                order.setMeals(new ArrayList<>());
                order.getMeals().add(order.getMealDetails()); // Add first meal
                grouped.put(order.getOrderId(), order);
            } else {
                // Add subsequent meals to existing order
                grouped.get(order.getOrderId()).getMeals().add(order.getMealDetails());
            }
        }
        return grouped;
    }
}