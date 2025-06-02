<%@ page import="java.util.List, com.ocn.beans.CategoryBean" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <!-- Add cache-control meta tags here -->
  <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
  <meta http-equiv="Pragma" content="no-cache">
  <meta http-equiv="Expires" content="0">
  <title>Category Management</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pages/admin/CRUD_Category.css">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<%
  if (session.getAttribute("currentAdmin") == null) {
    response.sendRedirect(request.getContextPath() + "/admin/adminLogin.jsp");
    return;
  }

  List<CategoryBean> categories = (List<CategoryBean>) request.getAttribute("categories");
%>

<div class="container">
  <jsp:include page="/components/admin_sideNav.jsp"/>

  <main class="main-content">
    <div class="category-container">
      <div class="category-header">
        <h2>Category</h2>
        <a href="#popupForm" class="CRUD-btn">Add category</a>
      </div>

      <table class="category-table">
        <thead>
        <tr>
          <th>Icon</th>
          <th>Category Name</th>
          <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <% if (categories != null) {
          for (CategoryBean category : categories) { %>
        <tr>
          <td>
            <img src="${pageContext.request.contextPath}/img/category/<%= category.getCategoryIcon() %>"
                 alt="<%= category.getCategoryName() %>" class="icon-img">
          </td>
          <td><%= category.getCategoryName() %></td>
          <td>
            <a href="AdminCategoryServlet?action=delete&categoryId=<%= category.getCategoryId() %>"
               class="CRUD-btn">Remove</a>
            <a href="#popup_<%= category.getCategoryId() %>" class="CRUD-btn">Update</a>

            <!-- Update Popup -->
            <div id="popup_<%= category.getCategoryId() %>" class="popup">
              <div class="popup-content">
                <div class="popup-header">
                  <a href="#" class="close-btn">&times;</a>
                </div>
                <div class="popup-body">
                  <h4 class="popheader">Update Category</h4>
                  <form method="POST" action="AdminCategoryServlet">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="categoryId"
                           value="<%= category.getCategoryId() %>">
                    <div class="form-group">
                      <label class="header-lable">New name:</label>
                      <input type="text" name="name"
                             value="<%= category.getCategoryName() %>" required>
                    </div>
                    <div class="form-group">
                      <label class="header-lable">New icon:</label>
                      <input type="text" name="icon"
                             value="<%= category.getCategoryIcon() %>" required>
                    </div>
                    <button type="submit">Submit</button>
                  </form>
                </div>
              </div>
            </div>
          </td>
        </tr>
        <% }
        } %>
        </tbody>
      </table>
    </div>

    <!-- Add Category Popup -->
    <div id="popupForm" class="popup">
      <div class="popup-content">
        <h3 class="popheader">Add Category</h3>
        <a href="#" class="close-btn">&times;</a>
        <form method="POST" action="AdminCategoryServlet">
          <input type="hidden" name="action" value="create">
          <div class="form-group">
            <label class="header-lable">Category name:</label>
            <input type="text" name="name" placeholder="Enter name" required>
          </div>
          <div class="form-group">
            <label class="header-lable">Icon:</label>
            <input type="text" name="icon" placeholder="Image Name" required>
          </div>
          <button type="submit">Submit</button>
        </form>
      </div>
    </div>
  </main>
</div>
</body>
</html>