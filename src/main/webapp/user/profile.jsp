<%@ page import="com.ocn.beans.AllergyBean" %>
<%@ page import="com.ocn.beans.OcnUserBean" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.Set" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  if (session.getAttribute("currentUser") == null) {
    response.sendRedirect(request.getContextPath() + "/aast/UniUserPortal.jsp");
    return;
  }
  OcnUserBean currentUser = (OcnUserBean) request.getAttribute("currentUser");
  List<AllergyBean> allAllergies = (List<AllergyBean>) request.getAttribute("allAllergies");
  List<AllergyBean> currentUserAllergies = (List<AllergyBean>) request.getAttribute("currentUserAllergies");

  Set<Integer> userAllergyIds = new HashSet<>();
  for (AllergyBean allergy : currentUserAllergies) {
    userAllergyIds.add(allergy.getAllergyId());
  }

  String errorMessage = (String) request.getAttribute("errorMessage");
  String successMessage = (String) request.getAttribute("successMessage");
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>User Profile</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pages/user/profile.css">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<div class="container">
  <jsp:include page="/components/sideNav.jsp"/>

  <main class="main-content">
    <div class="profile-card">
      <form method="POST" action="${pageContext.request.contextPath}/ProfileServlet">
        <input type="hidden" name="action" value="updateProfile">
        <div class="profile-header">
          <div class="profile-avatar">
            <img src="${pageContext.request.contextPath}/img/icons/user.svg" alt="User Avatar" class="avatar-image">
            <i class="fas fa-user"></i>
          </div>
          <div class="user-details">
            <h2 class="user-name"><%= currentUser.getUserName() %></h2>
            <p class="user-id">ID: <%= currentUser.getUserId() %></p>
            <div class="input-group">
              <label for="phone-input" class="input-label">Phone:</label>
              <input type="tel" id="phone-input" name="phone" class="input-field"
                     value="<%= currentUser.getUserPhone() != null ? currentUser.getUserPhone() : "" %>">
            </div>
          </div>
        </div>

        <div class="allergies-section">
          <h3 class="section-title">Allergies</h3>
          <div class="allergy-group">
            <% for (AllergyBean allergy : allAllergies) {
              if ("No Allergies".equals(allergy.getAllergyName())) continue; %>
            <label class="checkbox-container">
              <input type="checkbox"
                     name="selectedAllergies"
                     value="<%= allergy.getAllergyId() %>"
                <%= userAllergyIds.contains(allergy.getAllergyId()) ? "checked" : "" %>>
              <span class="checkmark"></span>
              <%= allergy.getAllergyName() %>
            </label>
            <% } %>
          </div>
          <button type="submit" class="btn btn--full save-button">Save Changes</button>
        </div>
      </form>
    </div>

    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
    <div class="error-message"><%= errorMessage %></div>
    <% } %>

    <% if (successMessage != null && !successMessage.isEmpty()) { %>
    <div class="success-message"><%= successMessage %></div>
    <% } %>
  </main>
</div>

<script>
  // Auto-hide messages after 5 seconds
  setTimeout(() => {
    document.querySelectorAll('.success-message, .error-message').forEach(el => {
      el.style.display = 'none';
    });
  }, 5000);
</script>
</body>
</html>