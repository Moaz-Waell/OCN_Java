<%@ page import="com.ocn.beans.*" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    if (session.getAttribute("currentUser") == null) {
        response.sendRedirect(request.getContextPath() + "/aast/UniUserPortal.jsp");
        return;
    }
    OrderBean order = (OrderBean) request.getAttribute("order");
    List<OrderDetailDTO> orderDetails = (List<OrderDetailDTO>) request.getAttribute("orderDetails");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Order Details</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pages/user/viewOrderDetails.css">
</head>
<body>
<a href="${pageContext.request.contextPath}/OrderServlet" class="btn-back" aria-label="Back to Orders">
    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M20.3284 11.0001V13.0001L7.50011 13.0001L10.7426 16.2426L9.32842 17.6568L3.67157 12L9.32842 6.34314L10.7426 7.75735L7.49988 11.0001L20.3284 11.0001Z" fill="currentColor"/>
    </svg>
</a>

<div class="order-header">
    <h1 class="category-title">Order #<%= order.getOrderId() %></h1>
    <span class="btn btn--status order-status ordersstatus <%= order.getOrderStatus().toLowerCase().replace(" ", "-") %>">
            <%= order.getOrderStatus() %>
        </span>
</div>

<div class="order-summary">
    <div class="summary-item">
        <span class="label">Order Date:</span>
        <span class="value">
                <%= order.getOrderScheduleDate() %>
            </span>
    </div>
    <div class="summary-item">
        <span class="label">Customer:</span>
        <span class="value"><%= ((OcnUserBean) session.getAttribute("currentUser")).getUserName() %></span>
    </div>
</div>

<div class="order-items">
    <h2 class="section-title">Items</h2>
    <% if (orderDetails != null && !orderDetails.isEmpty()) { %>
    <% for (OrderDetailDTO dto : orderDetails) {
        OrderDetailBean item = dto.getOrderDetail();
        MealBean meal = dto.getMeal();
    %>
    <div class="order-item">
        <div class="item-image">
            <img src="${pageContext.request.contextPath}/img/meals/<%= meal.getCategoryName().toLowerCase() %>/<%= meal.getMealIcon() %>"
                 alt="<%= meal.getMealName() %>">
        </div>
        <div class="item-details">
            <h3><%= meal.getMealName() %></h3>
            <% if (item.getNote() != null && !item.getNote().isEmpty()) { %>
            <p class="item-note">Note: <%= item.getNote() %></p>
            <% } %>
        </div>
        <div class="item-price">
            <span class="quantity">x<%= item.getQuantity() %></span>
            <span class="unit-price">$<%= String.format("%.2f", meal.getMealPrice()) %></span>
        </div>
    </div>
    <% } %>
    <% } else { %>
    <p>No items found in this order.</p>
    <% } %>
</div>
<div class="order-total">
    <div class="total-row final-total">
        <span class="label">Total Amount:</span>
        <span class="value">$<%= String.format("%.2f", order.getOrderAmount()) %></span>
    </div>
</div>
</body>
</html>