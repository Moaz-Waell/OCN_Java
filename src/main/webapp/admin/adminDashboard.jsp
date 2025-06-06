<%@ page import="java.util.*, com.ocn.beans.OrderBean" %>
<%@ page import="com.ocn.beans.AdminBean" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    AdminBean currentAdmin = (AdminBean) session.getAttribute("currentAdmin");
    String error = (String) session.getAttribute("error");
    String success = (String) session.getAttribute("success");
    if (error != null) session.removeAttribute("error");
    if (success != null) session.removeAttribute("success");
// Safely retrieve and cast attributes
    Double totalRevenueDeliveredAttr = (Double) request.getAttribute("totalRevenueDelivered");
    double totalRevenue = totalRevenueDeliveredAttr != null ? totalRevenueDeliveredAttr : 0.0;

    Integer deliveredCountAttr = (Integer) request.getAttribute("deliveredCount");
    int totalOrders = deliveredCountAttr != null ? deliveredCountAttr : 0;


    Double todayRevenueDeliveredAttr = (Double) request.getAttribute("todaysRevenue");
    double todayRevenue = todayRevenueDeliveredAttr != null ? todayRevenueDeliveredAttr : 0.0;

    Integer todaydeliveredCountAttr = (Integer) request.getAttribute("todaysOrders");
    int todayOrders = todaydeliveredCountAttr != null ? todaydeliveredCountAttr : 0;


    List<OrderBean> activeOrders = (List<OrderBean>) request.getAttribute("activeOrders_admin");
    if (activeOrders == null) activeOrders = new ArrayList<>();


    List<OrderBean> orderHistory = (List<OrderBean>) request.getAttribute("OrderHistory");
    if (orderHistory == null) orderHistory = new ArrayList<>();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <!-- Add cache-control meta tags here -->
    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Expires" content="0">
    <title>OCN Admin Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pages/admin/admin_landing.css"/>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<div class="container">
    <jsp:include page="/components/admin_sideNav.jsp"/>
    <main class="main-content">
        <header class="header flex-space-between">
            <div class="header-title">
                <h1>Dashboard</h1>
            </div>
            <div class="header-title">
                <h1>Welcome, <%= currentAdmin.getAdminName() %>
                </h1>
            </div>
        </header>

        <% if (error != null) { %>
        <div class="error-message"><%= error %>
        </div>
        <% } %>

        <% if (success != null) { %>
        <div class="success-message"><%= success %>
        </div>
        <% } %>

        <div class="top">
            <section class="analysis-section">
                <h2 class="heading-secondary text-center">Analysis</h2>
                <div class="analysis-flex">
                    <div class="analysis-item">
                        <p class="margin-bottom-1rem"><b>Total Orders:</b></p>
                        <p><%= totalOrders %>
                        </p>
                    </div>
                    <div class="analysis-item">
                        <p class="margin-bottom-1rem"><b>Total Revenue:</b></p>
                        <p><%= String.format("%.2f", totalRevenue) %> LE
                        </p>
                    </div>
                    <div class="analysis-item">
                        <p class="margin-bottom-1rem"><b>Today Revenue:</b></p>
                        <p><%= String.format("%.2f", todayRevenue) %> LE
                        </p>
                    </div>
                    <div class="analysis-item">
                        <p class="margin-bottom-1rem"><b>Today Orders:</b></p>
                        <p><%=todayOrders %>
                        </p>
                    </div>
                </div>
            </section>

            <section class="analysis-section">
                <h2 class="heading-secondary text-center">Vouchers</h2>
                <div class="analysis-flex">
                    <form method="POST" action="<%= request.getContextPath() %>/SendVoucherServlet">
                        <button type="submit" class="btn btn--order-again analysis-item">Send Vouchers</button>
                    </form>
                </div>
            </section>
        </div>
        <hr/>

        <!-- Current Orders -->
        <section class="margin-top-2rem">
            <h2 class="heading-secondary text-center">Current Orders</h2>
            <table class="orders-table">
                <thead>
                <tr class="heading-secondary">
                    <th>Order ID</th>
                    <th>Customer</th>
                    <th>Date</th>
                    <th>Time</th>
                    <th>Total</th>
                    <th>Details</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <% for (OrderBean order : activeOrders) { %>
                <tr>
                    <td>#<%= order.getOrderId() %>
                    </td>
                    <td><%= order.getUserId() %>
                    </td>
                    <td><%= order.getOrderScheduleDate() %>
                    </td>
                    <td><%= order.getOrderScheduleTime() %>
                    </td>
                    <td><%= String.format("%.2f", order.getOrderAmount()) %> LE
                    </td>
                    <td>
                        <a href="${pageContext.request.contextPath}/AdminOrderServlet?action=viewDetails&orderId=<%= order.getOrderId() %>"
                           class="view-details">View Details</a>
                    </td>
                    <td>
                        <span class="btn btn--status ordersstatus <%= order.getOrderStatus().toLowerCase() %>">
                            <%= order.getOrderStatus() %>
                        </span>
                    </td>
                    <td>
                        <%-- Check if the order status allows for cancellation or delivery --%>
                        <% if (Arrays.asList("Pending", "In Progress", "Out For Delivery", "Preparing").contains(order.getOrderStatus())) { %>
                        <%-- Cancel Button Form --%>
                        <form method="POST" action="<%= request.getContextPath() %>/AdminOrderServlet" class="inline-form">
                            <input type="hidden" name="action" value="cancelOrder">
                            <input type="hidden" name="orderId" value="<%= order.getOrderId() %>">
                            <button type="submit" class="btn btn--order-again">Cancel</button>
                        </form>

                        <%-- Deliver Button Form with conditional logic --%>
                        <form method="POST" action="<%= request.getContextPath() %>/AdminOrderServlet" class="inline-form">
                            <input type="hidden" name="action" value="deliverOrder">
                            <input type="hidden" name="orderId" value="<%= order.getOrderId() %>">
                            <%-- Check if the order status is 'Out For Delivery' --%>
                            <% if ("Out For Delivery".equalsIgnoreCase(order.getOrderStatus())) { %>
                            <%-- Enable the Deliver button if status is 'Out For Delivery' --%>
                            <button type="submit" class="btn btn--order-again">Deliver</button>
                            <% } else { %>
                            <%-- Disable the Deliver button and show lock icon otherwise --%>
                            <button type="submit" class="btn btn--order-again" disabled>
                                <i class="fas fa-lock"></i> Deliver
                            </button>
                            <% } %>
                        </form>
                        <% } %>
                    </td>

                </tr>
                <% } %>
                </tbody>
            </table>
        </section>

        <!-- Order History with Feedback -->
        <section class="margin-top-2rem">
            <h2 class="heading-secondary text-center">Order History</h2>
            <table class="orders-table">
                <thead>
                <tr class="heading-secondary">
                    <th>Order ID</th>
                    <th>Customer</th>
                    <th>Date</th>
                    <th>Total</th>
                    <th>Status</th>
                    <th>Details</th>
                    <th>Feedback</th>
                </tr>
                </thead>
                <tbody>
                <% for (OrderBean order : orderHistory) { %>
                <tr>
                    <td>#<%= order.getOrderId() %>
                    </td>
                    <td><%= order.getUserId() %>
                    </td> <!-- Replace with user name if needed -->
                    <td><%= order.getOrderScheduleDate() %>
                    </td>
                    <td>$<%= String.format("%.2f", order.getOrderAmount()) %>
                    </td>
                    <td>
                        <span class="btn btn--status ordersstatus <%= order.getOrderStatus().toLowerCase() %>">
                            <%= order.getOrderStatus() %>
                        </span>
                    </td>
                    <td>
                        <a href="${pageContext.request.contextPath}/AdminOrderServlet?action=viewDetails&orderId=<%= order.getOrderId() %>"
                           class="view-details">View Details</a>
                    </td>
                    <td>
                        <% if ("Delivered".equals(order.getOrderStatus()) && order.getOrderFeedback() != null) { %>
                        <div class="rating-stars">
                            <% for (int i = 0; i < 5; i++) { %>
                            <span class="star <%= i < order.getOrderFeedback() ? "filled" : "" %>">★</span>
                            <% } %>
                        </div>
                        <% } else { %>
                        <span class="no-feedback">N/A</span>
                        <% } %>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </section>
    </main>
</div>
</body>
</html>