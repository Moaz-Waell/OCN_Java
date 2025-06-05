<%@ page import="com.ocn.beans.CartBean" %>
<%@ page import="com.ocn.beans.MealBean" %>
<%@ page import="com.ocn.beans.OcnUserBean" %>
<%@ page import="com.ocn.dao.CartDAO" %>
<%@ page import="com.ocn.dao.MealDAO" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  if (session.getAttribute("currentUser") == null) {
    response.sendRedirect(request.getContextPath() + "/aast/UniUserPortal.jsp");
    return;
  }
  OcnUserBean currentUser = (OcnUserBean) session.getAttribute("currentUser");

  CartDAO cartDAO = new CartDAO();
  MealDAO mealDAO = new MealDAO();
  double total = 0.0;

  // Handle decrement action
  if ("decrement".equals(request.getParameter("action"))) {
  String cartIdParam = request.getParameter("cart_id");
  if (cartIdParam != null) {
    try {
      int cartId = Integer.parseInt(cartIdParam);
      CartBean cartItem = cartDAO.getCartItemById(cartId);

      if (cartItem != null && cartItem.getUserId() == currentUser.getUserId()) {
        if (cartItem.getQuantity() > 1) {
          cartDAO.updateCartItemQuantity(cartId, cartItem.getQuantity() - 1);
        } else {
          cartDAO.removeItemFromCart(cartId);
        }
      }
    } catch (NumberFormatException e) {
      // Invalid cart ID format
    }
  }
  response.sendRedirect(request.getContextPath() + "/CartServlet");
  return;
}

  // Get cart items with meal details
  List<CartBean> cartItems = cartDAO.getCartItemsByUserId(currentUser.getUserId());
  for (CartBean item : cartItems) {
    MealBean meal = mealDAO.getMealById(item.getMealId());
    if (meal != null) {
      total += meal.getMealPrice() * item.getQuantity();
    }
  }
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Cart</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pages/user/cart.css">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<div class="container">
  <jsp:include page="/components/sideNav.jsp"/>

  <main class="main-content">
    <form action="${pageContext.request.contextPath}/CheckoutServlet" method="GET">
      <div class="meal-cart-container">
        <% if (cartItems.isEmpty()) { %>
        <div class="empty-cart-message">
          <i class="fas fa-shopping-cart fa-3x"></i>
          <h2 class="heading-secondary">Your Cart is Empty</h2>
          <p class="description">Looks like you haven't added any meals yet!</p>
          <a href="${pageContext.request.contextPath}/HomeServlet" class="btn btn--full margin-top-2rem">
            Browse Meals
          </a>
        </div>
        <% } else {
          for (CartBean item : cartItems) {
            MealBean meal = mealDAO.getMealById(item.getMealId());
            if (meal == null) continue;
        %>
        <div class="meal-item">
          <div class="meal-item__info">
            <img class="meal-item__photo"
                 src="${pageContext.request.contextPath}/img/meals/<%= meal.getCategoryName().toLowerCase() %>/<%= meal.getMealIcon() %>"
                 alt="<%= meal.getMealIcon() %>">
            <div class="meal-item__content">
              <div class="heading-secondary"><%= meal.getMealName() %></div>
              <div class="description">
                <%= item.getNote().isEmpty() ? meal.getMealDescription() : item.getNote() %>
              </div>
              <div class="description">
                <input type="hidden" name="quantity" id="quantityInput" value="1">

                Quantity: <%= item.getQuantity() %>
              </div>
              <div class="meal-item__price">
                <%= String.format("%.2f", meal.getMealPrice()) %> LE
              </div>
            </div>
            <div class="meal-item__quantity-controls">
              <a href="CartServlet?action=decrement&cart_id=<%= item.getCartId() %>"
                 class="meal-item__quantity-btn meal-item__decrement-btn">
                <i class="fas fa-minus"></i>
              </a>
              <a href="CartServlet?action=increment&cart_id=<%= item.getCartId() %>"
                 class="meal-item__quantity-btn meal-item__increment-btn">
                <i class="fas fa-plus"></i>
              </a>
            </div>
          </div>
        </div>
        <% }
        } %>
      </div>

      <% if (!cartItems.isEmpty()) { %>
      <div class="cart-footer-wrapper">
        <div class="cart-footer">
          <div class="total-amount">
            <i class="fas fa-receipt"></i>
            Total Amount: $<span id="total-amount"><%= String.format("%.2f", total) %></span>
          </div>
          <button type="submit" class="btn btn--full btn--checkout">
            <i class="fas fa-credit-card"></i> Proceed to Checkout
          </button>
        </div>
      </div>
      <% } %>
    </form>
  </main>
</div>
</body>
</html>