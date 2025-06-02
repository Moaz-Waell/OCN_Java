<%@ page import="com.ocn.beans.OrderBean" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%

  if (session.getAttribute("currentUser") == null) {
    response.sendRedirect(request.getContextPath() + "/aast/UniUserPortal.jsp");
    return;
  }

  List<OrderBean> currentOrders = (List<OrderBean>) request.getAttribute("currentOrders");
  List<OrderBean> pastOrders = (List<OrderBean>) request.getAttribute("pastOrders");

  String success = (String) session.getAttribute("message");
  if (success != null) session.removeAttribute("message");
  String error = (String) session.getAttribute("error");
  if (error != null) session.removeAttribute("error");
%>
<html>
<head>
  <title>Orders</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pages/user/orders.css">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css ">
</head>
<body>
<div class="container">
  <jsp:include page="/components/sideNav.jsp"/>
  <main class="main-content">
    <% if (success != null) { %>
    <div class="error-message"><%= success %></div>
    <% } %>
    <% if (error != null) { %>
    <div class="error-message"><%= error %></div>
    <% } %>
    <h2 class="heading-secondary text-center">Current Orders</h2>

    <!-- Active Orders Table -->
    <table class="orders-table">
      <thead>
      <tr class="heading-secondary">
        <th>Order ID</th>
        <th>Date</th>
        <th>Time</th>
        <th>Total</th>
        <th>Details</th>
        <th>Status</th>
        <th>Action</th>
      </tr>
      </thead>
      <tbody>
      <%
        if (currentOrders != null) {
          for (OrderBean order : currentOrders) {
      %>
      <tr>
        <td>#<%=order.getOrderId() %></td>
        <td><%= order.getOrderScheduleDate() %></td>
        <td><%= order.getOrderScheduleTime() %></td>
        <td><%= order.getOrderAmount() %></td>
        <td>
          <a href="${pageContext.request.contextPath}/OrderServlet?action=viewDetails&orderId=<%= order.getOrderId() %>"
             class="view-details">View Details</a>
        </td>
        <td>
          <button class="btn btn--status ordersstatus <%= order.getOrderStatus().toLowerCase() %>">
            <%= order.getOrderStatus() %>
          </button>
        </td>
        <td>
          <%
            String status = order.getOrderStatus();
            if ("Pending".equals(status) || "In Progress".equals(status) ||
                    "Out For Delivery".equals(status) || "Preparing".equals(status)) {
          %>
          <form method="POST" action="${pageContext.request.contextPath}/OrderServlet">
            <input type="hidden" name="action" value="cancelOrder">
            <input type="hidden" name="orderId" value="<%= order.getOrderId() %>">
            <button type="submit" class="btn btn--order-again">Cancel Order</button>
          </form>
          <%
            }
          %>
        </td>
      </tr>
      <%
          }
        }
      %>
      </tbody>
    </table>

    <h2 class="heading-secondary text-center">Order History</h2>

    <!-- Order History Table -->
    <table class="orders-table">
      <thead>
      <tr class="heading-secondary">
        <th>Order ID</th>
        <th>Date</th>
        <th>Total</th>
        <th>Details</th>
        <th>Status</th>
        <th>Action</th>
        <th>Feedback</th>
      </tr>
      </thead>
      <tbody>
      <%
        if (pastOrders != null) {
          for (OrderBean order : pastOrders) {
      %>
      <tr>
        <td>#<%= order.getOrderId() %></td>
        <td><%= order.getOrderScheduleDate() %></td>
        <td><%= order.getOrderAmount() %></td>
        <td>
          <a href="${pageContext.request.contextPath}/OrderServlet?action=viewDetails&orderId=<%= order.getOrderId() %>"
             class="view-details">View Details</a>
        </td>
        <td>
          <button class="btn btn--status ordersstatus <%= order.getOrderStatus().toLowerCase() %>">
            <%= order.getOrderStatus() %>
          </button>
        </td>
        <td>
          <form method="POST" action="${pageContext.request.contextPath}/OrderServlet">
            <input type="hidden" name="action" value="reorder">
            <input type="hidden" name="orderId" value="<%= order.getOrderId() %>">
            <button type="submit" class="btn btn--order-again">Reorder</button>
          </form>
        </td>
        <td>
          <%
            if ("Delivered".equals(order.getOrderStatus()) && order.getOrderFeedback() == null) {
          %>
          <button class="btn btn--order-again open-feedback" data-order-id="<%= order.getOrderId() %>">
            Give Feedback
          </button>
          <%
          } else if ("Delivered".equals(order.getOrderStatus()) && order.getOrderFeedback() != null) {
          %>
          <div class="rating-stars">
            <%
              for (int i = 1; i <= 5; i++) {
                String starClass = (i <= order.getOrderFeedback()) ? "star filled" : "star";
            %>
            <span class="<%= starClass %>">★</span>
            <%
              }
            %>
          </div>
          <%
            }
          %>
        </td>
      </tr>
      <%
          }
        }
      %>
      </tbody>
    </table>

    <!-- Feedback Popup -->
    <div id="feedbackPopup" class="popup">
      <div class="popup-content">
        <span class="close-btn">&times;</span>
        <form method="POST" action="${pageContext.request.contextPath}/OrderServlet">
          <input type="hidden" name="action" value="submitFeedback">
          <input type="hidden" name="orderId" id="feedbackOrderId">
          <p class="question">How do you feel about your order?</p>
          <div class="rating">
            <%
              for (int i = 1; i <= 5; i++) {
            %>
            <label>
              <input type="radio" name="rating" value="<%= i %>">
              <span class="rating-number"><%= i %></span>
            </label>
            <%
              }
            %>
          </div>
          <button type="submit" class="next-btn">Submit</button>
        </form>
      </div>
    </div>
  </main>
</div>

<script>
  // Feedback popup handling
  document.querySelectorAll('.open-feedback').forEach(btn => {
    btn.addEventListener('click', (e) => {
      e.preventDefault();
      document.getElementById('feedbackOrderId').value = btn.dataset.orderId;
      document.getElementById('feedbackPopup').style.display = 'block';
    });
  });

  document.querySelector('.close-btn').addEventListener('click', (e) => {
    e.preventDefault();
    document.getElementById('feedbackPopup').style.display = 'none';
  });
</script>
</body>
</html>