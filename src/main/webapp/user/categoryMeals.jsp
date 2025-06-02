<%@ page import="com.ocn.beans.MealBean" %>
<%@ page import="java.util.List" %>
<%@ page import="com.ocn.beans.OcnUserBean" %>
<%
    String success = (String) session.getAttribute("mealAddedMessage");
    if (success != null) session.removeAttribute("mealAddedMessage");

    if (session.getAttribute("currentUser") == null) {
        response.sendRedirect(request.getContextPath() + "/aast/UniUserPortal.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${categoryName} Menu</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pages/user/categoryMeals.css">
</head>
<body>
<div class="container">
    <jsp:include page="/components/sideNav.jsp" />
    <main class="main-content">
        <% if (success != null) { %>
        <div class="success-message"><%= success %>
        </div>
        <% } %>
        <div class="menu-category">
            <div class="category-header">
                <h1 class="category-title">${categoryName}</h1>
            </div>
            <div class="menu-grid">
                <%
                    List<MealBean> meals = (List<MealBean>) request.getAttribute("mealsInCategory");
                    if (meals != null && !meals.isEmpty()) {
                        for (MealBean meal : meals) {
                %>
                <div class="menu-item">
                    <div class="menu-image">
                        <img src="${pageContext.request.contextPath}/img/meals/${categoryName.toLowerCase()}/<%= meal.getMealIcon() %>"
                             alt="<%= meal.getMealName() %>">
                    </div>
                    <div class="menu-details">
                        <h3><%= meal.getMealName() %></h3>
                        <p><%= meal.getMealDescription() %></p>
                        <span class="price">$<%= String.format("%.2f", meal.getMealPrice()) %></span>
                        <a href="MealServlet?id=<%= meal.getMealId() %>" class="view-details">View Details</a>
                    </div>
                </div>
                <%
                    }
                } else {
                %>
                <p>No meals available</p>
                <%
                    }
                %>
            </div>
        </div>
    </main>
</div>
</body>
</html>