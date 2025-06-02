<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map,com.ocn.beans.OrderBean,com.ocn.beans.OrderDetailBean,java.util.HashMap" %>
<!DOCTYPE html>
<html>
<head>
    <title>Kitchen Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pages/kitchenDashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
<header class="dashboard-header">
    <h1 class="dashboard-title">Kitchen Orders</h1>
    <div class="dashboard-info">
        <div class="current-time">
            <i class="fas fa-clock"></i>
            <span id="live-clock"></span>
        </div>
    </div>
</header>

<%-- Error/Success Messages --%>
<% if(request.getAttribute("error") != null) { %>
<div class="error-message"><%= request.getAttribute("error") %></div>
<% } %>

<% if(request.getAttribute("success") != null) { %>
<div class="success-message"><%= request.getAttribute("success") %></div>
<% } %>

<main class="order-list">
    <%
        // Null-safe orders initialization
        Map<Integer, OrderBean> orders = (Map<Integer, OrderBean>) request.getAttribute("orders");
        if(orders == null) {
            orders = new HashMap<>();
        }

        if(orders.isEmpty()) {
    %>
    <div class="no-orders">No pending orders found for today</div>
    <% } else {
        for(OrderBean order : orders.values()) {
    %>
    <div class="order-card <%= order.isPriority() ? "priority" : "" %>">
        <div class="order-header">
            <div class="order-meta">
                <h2 class="order-id">Order #<%= order.getOrderId() %></h2>
                <div class="customer-info">
                    <i class="fas fa-user"></i>
                    <span><%= order.getCustomerName() %></span>
                </div>
            </div>
            <div class="order-timing">
                <div class="order-date">
                    <i class="fas fa-calendar-day"></i>
                    <%= order.getFormattedDate() %>
                </div>
                <div class="order-time">
                    <i class="fas fa-clock"></i>
                    <%= order.getFormattedTime() %>
                </div>
            </div>
        </div>

        <div class="meal-list">
            <% for(OrderDetailBean meal : order.getMeals()) { %>
            <div class="meal-item">
                <div class="meal-info">
                    <h3 class="meal-name">
                        <%= meal.getMeal().getMealName() %>
                        <span class="meal-quantity">x<%= meal.getQuantity() %></span>
                    </h3>
                    <% if(meal.getMeal().getMealDescription() != null && !meal.getMeal().getMealDescription().isEmpty()) { %>
                    <p class="meal-description"><%= meal.getMeal().getMealDescription() %></p>
                    <% } %>
                    <% if(meal.getNote() != null && !meal.getNote().isEmpty()) { %>
                    <div class="meal-note">
                        <strong>Special Note:</strong>
                        <%= meal.getNote() %>
                    </div>
                    <% } %>
                </div>
            </div>
            <% } %>
        </div>

        <div class="order-actions">
            <form method="POST" action="UpdateOrderStatus" class="status-form">
                <input type="hidden" name="orderId" value="<%= order.getOrderId() %>">
                <input type="hidden" name="status" value="Preparing">
                <button type="submit"
                        class="status-btn <%= "Preparing".equals(order.getOrderStatus()) ? "Preparing" : "in-progress" %>"
                        <%= "Preparing".equals(order.getOrderStatus()) ? "disabled" : "" %>>
                    <i class="fas fa-spinner"></i>
                    <%= "Preparing".equals(order.getOrderStatus()) ? "Preparing" : "Mark as Preparing" %>
                </button>
            </form>

            <form method="POST" action="UpdateOrderStatus" class="status-form">
                <input type="hidden" name="orderId" value="<%= order.getOrderId() %>">
                <input type="hidden" name="status" value="Out For Delivery">
                <button type="submit"
                        class="status-btn out-delivery"
                        <%= !"Preparing".equals(order.getOrderStatus()) ? "disabled" : "" %>>
                    <i class="fas fa-truck"></i> Out for Delivery
                </button>
            </form>
        </div>
    </div>
    <%  }
    } %>
</main>

<script>
    // Live clock initialization
    function updateClock() {
        const options = {
            weekday: 'short',
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit'
        };
        document.getElementById('live-clock').textContent =
            new Date().toLocaleDateString('en-US', options);
    }

    // Initial clock update and interval setup
    updateClock();
    setInterval(updateClock, 1000);

    // Auto-refresh every 5 seconds
    setInterval(() => window.location.reload(), 5000);
</script>
</body>
</html>