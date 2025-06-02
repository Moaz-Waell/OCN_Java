<%@ page import="java.util.List, com.ocn.beans.*" %>
<%@ page import="java.util.Map" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <!-- Add cache-control meta tags here -->
    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Expires" content="0">
    <title>Meal Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pages/admin/CRUD_Meal.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const checkLists = document.querySelectorAll('.dropdown-check-list');
            checkLists.forEach(list => {
                list.addEventListener('click', function(evt) {
                    this.classList.toggle('visible');
                    evt.stopPropagation();
                });
            });

            window.addEventListener('click', function() {
                checkLists.forEach(list => list.classList.remove('visible'));
            });
        });
    </script>
</head>
<body>
<div class="container">
    <jsp:include page="/components/admin_sideNav.jsp"/>
    <main class="main-content">
        <div class="category-container">
            <div class="category-header">
                <h2>Meals</h2>
                <a href="#addMealForm" class="CRUD-btn add-meal">Add Meal</a>
            </div>

            <table class="category-table">
                <thead>
                <tr>
                    <th>Icon</th>
                    <th>Meal Name</th>
                    <th>Price</th>
                    <th>Category</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <% List<MealBean> meals = (List<MealBean>) request.getAttribute("meals");
                    for (MealBean meal : meals) { %>
                <tr>
                    <td>
                        <img src="${pageContext.request.contextPath}/img/meals/<%= meal.getCategoryName().toLowerCase() %>/<%= meal.getMealIcon() %>"
                             alt="<%= meal.getMealName() %>"
                             class="icon-img">
                    </td>
                    <td><%= meal.getMealName() %></td>
                    <td>$<%= String.format("%.2f", meal.getMealPrice()) %></td>
                    <td><%= meal.getCategoryName() %></td>
                    <td>
                        <a href="AdminMealServlet?action=delete&meal_id=<%= meal.getMealId() %>"
                           class="remove_btn">Remove</a>
                        <a href="#updateMealForm-<%= meal.getMealId() %>" class="CRUD-btn">Update</a>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>

        <!-- Add Meal Popup -->
        <div id="addMealForm" class="popup">
            <div class="popup-content">
                <h3>Add Meal</h3>
                <a href="#" class="close-btn">&times;</a>
                <form method="POST" action="AdminMealServlet">
                    <input type="hidden" name="action" value="add">

                    <label class="header-lable">Meal name:</label>
                    <input type="text" name="meal_name" placeholder="Enter meal name" required>

                    <label class="header-lable">Price:</label>
                    <input type="number" min="0" name="price" placeholder="Enter price">
                    <label class="header-lable">Ingredients:</label>
                    <div class="dropdown-check-list" tabindex="1">
                            <span class="dropdown-title">
                                <span>Select Ingredients</span>
                                <svg class="dropdown-arrow" width="10" height="6" viewBox="0 0 10 6">
                                    <path d="M5 6L0 0H10L5 6Z" fill="currentColor" />
                                </svg>
                            </span>
                        <ul class="items">
                            <% List<IngredientBean> ingredients = (List<IngredientBean>) request.getAttribute("ingredients");
                                for (IngredientBean ingredient : ingredients) { %>
                            <li>
                                <label>
                                    <input type="checkbox" name="ingredients"
                                           value="<%= ingredient.getIngredientId() %>">
                                    <%= ingredient.getIngredientName() %>
                                </label>
                            </li>
                            <% } %>
                        </ul>
                    </div>

                    <label class="header-lable">Category:</label>
                    <select class="category-select" name="category" required>
                        <% List<CategoryBean> categories = (List<CategoryBean>) request.getAttribute("categories");
                            for (CategoryBean category : categories) { %>
                        <option value="<%= category.getCategoryId() %>">
                            <%= category.getCategoryName() %>
                        </option>
                        <% } %>
                    </select>

                    <label class="header-lable">Image:</label>
                    <input type="text" name="image" required>

                    <button type="submit" class="CRUD-btn">Submit</button>
                </form>
            </div>
        </div>

        <!-- Update Meal Popups -->
        <% for (MealBean meal : meals) {
        %>
        <div id="updateMealForm-<%= meal.getMealId() %>" class="popup">
            <div class="popup-content">
                <div class="popup-header">
                    <a href="#" class="close-btn">&times;</a>
                </div>
                <div class="popup-body">
                <h4  class="popheader">Update Meal</h4>

                <form method="POST" action="AdminMealServlet">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="meal_id" value="<%= meal.getMealId() %>">

                    <label class="header-lable">Meal name:</label>
                    <input type="text" name="meal_name" value="<%= meal.getMealName() %>" required>

                    <label class="header-lable">Price:</label>
                    <input type="text" name="price" value="<%= meal.getMealPrice() %>" required>

                    <label class="header-lable">Ingredients:</label>
                    <div class="dropdown-check-list" tabindex="1">
                                <span class="dropdown-title">
                                    <span>Select Ingredients</span>
                                    <svg class="dropdown-arrow" width="10" height="6" viewBox="0 0 10 6">
                                        <path d="M5 6L0 0H10L5 6Z" fill="currentColor" />
                                    </svg>
                                </span>
                        <ul class="items">
                            <%
                                Map<Integer, List<IngredientBean>> mealIngredientsMap =
                                        (Map<Integer, List<IngredientBean>>) request.getAttribute("mealIngredientsMap");
                                List<IngredientBean> currentMealIngredients = mealIngredientsMap.get(meal.getMealId());
                            %>

                            <% for (IngredientBean ingredient : ingredients) {
                                boolean checked = currentMealIngredients != null &&
                                        currentMealIngredients.stream()
                                                .anyMatch(mi -> mi.getIngredientId() == ingredient.getIngredientId());
                            %>
                            <li>
                                <label>
                                    <input type="checkbox" name="ingredients"
                                           value="<%= ingredient.getIngredientId() %>"
                                        <%= checked ? "checked" : "" %>>
                                    <%= ingredient.getIngredientName() %>
                                </label>
                            </li>
                            <% } %>
                        </ul>
                    </div>

                    <label class="header-lable">Category:</label>
                    <select class="category-select" name="category" required>
                        <% for (CategoryBean category : categories) { %>
                        <option value="<%= category.getCategoryId() %>"
                                <%= category.getCategoryId() == meal.getCategoryId() ? "selected" : "" %>>
                            <%= category.getCategoryName() %>
                        </option>
                        <% } %>
                    </select>

                    <label class="header-lable">Image:</label>
                    <input type="text" name="image" value="<%= meal.getMealIcon() %>" required>

                    <button type="submit" class="CRUD-btn">Update</button>
                </form>
                </div>
            </div>
        </div>
        <% } %>
    </main>
</div>
</body>
</html>