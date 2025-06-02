<%@ page import="com.ocn.beans.OcnUserBean, com.ocn.beans.CategoryBean, com.ocn.beans.MealBean, java.util.List" %>
<%@ page import="com.ocn.beans.AllergyBean" %>
<%
  OcnUserBean currentUser = (OcnUserBean) session.getAttribute("currentUser");
  if (session.getAttribute("currentUser") == null) {
    response.sendRedirect(request.getContextPath() + "/aast/UniUserPortal.jsp");
    return;
  }

  response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
  response.setHeader("Pragma", "no-cache");
  response.setDateHeader("Expires", 0);
%>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>OCN Food Dashboard</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pages/user/home.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pages/user/allergies.css">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
</head>
<body>
<%
  boolean showAllergyForm = (Boolean) request.getAttribute("showAllergyForm");
  List<AllergyBean> allAllergies = (List<AllergyBean>) request.getAttribute("allAllergies");
%>

<% if(showAllergyForm) { %>
<div class="allergy-overlay">
  <div class="basic-container">
    <form method="POST" action="SubmitAllergiesServlet" class="form-container">
      <h1 class="h1 heading-primary head-allergies">Allergies Form</h1>
      <div class="checkbox-group label">
        <% for(AllergyBean allergy : allAllergies) { %>
        <label class="checkbox-container">
          <input type="checkbox" name="allergies" value="<%= allergy.getAllergyId() %>">
          <%= allergy.getAllergyName() %>
          <span class="checkmark"></span>
        </label>
        <% } %>
      </div>
      <div class="button-group">
        <button type="submit" name="no_allergies" class="btn btn--full">No Allergies</button>
        <button type="submit" name="submit" class="btn btn--full">Submit</button>
      </div>
    </form>
  </div>
</div>
<% } %>

<div class="container">
  <jsp:include page="/components/sideNav.jsp" />
  <main class="main-content">
    <header class="header">
      <div class="header-title">
        <h1>Menu</h1>
      </div>
      <div class="search-bar">
        <i class="fas fa-search"></i>
        <input type="search" placeholder="Search" />
      </div>
      <div class="header-actions">
        <div class="user-greeting">
          <p>Hi, <%= currentUser.getUserName() %></p>
        </div>
      </div>
    </header>

    <!-- Category Section -->
    <section class="category-section">
      <div class="section-header">
        <h2>Category</h2>
      </div>
      <div class="category-flex">
        <%
          List<CategoryBean> categories = (List<CategoryBean>) request.getAttribute("categories");
          for(CategoryBean category : categories) {
        %>
        <div class="category-item">
          <a href="CategoryServlet?id=<%= category.getCategoryId() %>">
            <img src="${pageContext.request.contextPath}/img/category/<%= category.getCategoryIcon() %>"
                 alt="<%= category.getCategoryName() %>">
            <p><%= category.getCategoryName() %></p>
          </a>
        </div>
        <% } %>
      </div>
    </section>

    <!-- Best Seller Section -->
    <section class="meal-section">
      <div class="section-header">
        <h2>Best Seller</h2>
      </div>
      <div class="menu-grid">
        <%
          List<MealBean> bestSellerMeals = (List<MealBean>) request.getAttribute("bestSellerMeals");
          for(MealBean meal : bestSellerMeals) {
        %>
        <div class="menu-item">
          <div class="menu-image">
            <img src="${pageContext.request.contextPath}/img/meals/<%= meal.getCategoryName().toLowerCase() %>/<%= meal.getMealIcon() %>"
                 alt="<%= meal.getMealName() %>">
          </div>
          <div class="menu-details">
            <h3><%= meal.getMealName() %></h3>
            <p><%= meal.getMealDescription() %></p>
            <span class="price">$<%= String.format("%.2f", meal.getMealPrice()) %></span>
            <a href="${pageContext.request.contextPath}/MealServlet?id=<%= meal.getMealId() %>" class="view-details">View Details</a>
          </div>
        </div>
        <% } %>
      </div>
    </section>
  </main>
</div>
</body>
</html>