<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<aside class="sidebar">
  <div class="logo">
    <img src="${pageContext.request.contextPath}/img/logo/onCloudNine-white.svg" alt="OCN Logo" />
  </div>

  <nav class="navigation">
    <a href="<%= request.getContextPath() %>/AdminDashboardServlet" class="nav-item">
      <i class="fas fa-home"></i>
      <span>Homepage</span>
    </a>
    <a href="<%= request.getContextPath() %>/AdminMealServlet" class="nav-item">
      <i class="fa-solid fa-utensils"></i>
      <span>Meal</span>
    </a>
    <a href="<%= request.getContextPath() %>/AdminCategoryServlet" class="nav-item">
      <i class="fas fa-list"></i>
      <span>Category</span>
    </a>
    <a href="<%= request.getContextPath() %>/AdminLoginServlet?action=logout" class="nav-item">
      <i class="fas fa-sign-out-alt"></i>
      <span>Logout</span>
    </a>
  </nav>
</aside>