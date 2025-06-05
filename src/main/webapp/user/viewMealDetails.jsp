<jsp:useBean id="meal" scope="request" type="com.ocn.beans.MealBean"/>
<%@ page import="java.util.List" %>
<%@ page import="com.ocn.beans.IngredientBean" %>
<%@ page import="com.ocn.beans.AllergyBean" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
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
    <title>${meal.mealName} Details</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pages/user/viewMealDetails.css">
</head>
<body>
<div>
    <a href="CategoryServlet?id=${meal.categoryId}" class="btn-back">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M20.3284 11.0001V13.0001L7.50011 13.0001L10.7426 16.2426L9.32842 17.6568L3.67157 12L9.32842 6.34314L10.7426 7.75735L7.49988 11.0001L20.3284 11.0001Z" fill="currentColor"/>
        </svg>
    </a>
</div>
<div class="wave">
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1440 320">
        <path fill="#143f23" fill-opacity="1" d="M0,288L40,282.7C80,277,160,267,240,229.3C320,192,400,128,480,101.3C560,75,640,85,720,106.7C800,128,880,160,960,144C1040,128,1120,64,1200,80C1280,96,1360,192,1400,240L1440,288L1440,0L1400,0C1360,0,1280,0,1200,0C1120,0,1040,0,960,0C880,0,800,0,720,0C640,0,560,0,480,0C400,0,320,0,240,0C160,0,80,0,40,0L0,0Z"></path>
    </svg>
</div>

<section class="meal_details">
    <div class="container">
        <div class="caption">
            <div class="heading-primary meal_name">${meal.mealName}</div>

            <div class="description meal_description">
                ${meal.mealDescription}
            </div>

            <%
                List<AllergyBean> allergens = (List<AllergyBean>) request.getAttribute("detectedAllergensInMeal");
                if (allergens != null && !allergens.isEmpty()) {
            %>
            <div class="description allergy_warning">
                <p>Allergy detected:
                    <% for (int i=0; i<allergens.size(); i++) {
                        AllergyBean allergen = allergens.get(i);
                    %>
                    <%= allergen.getAllergyName() %><% if (i != allergens.size()-1) { %>, <% } %>
                    <% } %>
                </p>
            </div>
            <% } %>

            <div class="ingredient">
                <p class="description"><i>select to remove from the meal</i></p>
                <form action="CartServlet" method="post">
                    <input type="hidden" name="mealId" value="${meal.mealId}">
                    <div class="grid grid-3-cols ingredients_list">
                        <% for (IngredientBean ingredient : (List<IngredientBean>) request.getAttribute("mealIngredients")) { %>
                        <label class="checkbox-container margin-bottom-2rem">
                            <input type="checkbox"
                                   name="ingredients"
                                   value="<%= ingredient.getIngredientName() %>"
                                   checked>
                            <span class="checkmark"></span>
                            <%= ingredient.getIngredientName() %>
                        </label>
                        <% } %>
                    </div>

                    <div class="heading-secondary meal_price">${String.format("%.2f", meal.mealPrice)} LE</div>

                    <div class="buttons">
                        <div class="quantity-selector">
                            <button type="button" class="quantity__btn" onclick="changeQuantity(-1)">-</button>
                            <div class="quantity__display" id="quantity">1</div>
                            <button type="button" class="quantity__btn" onclick="changeQuantity(1)">+</button>
                        </div>
                        <input type="hidden" name="quantity" id="quantityInput" value="1">
                        <button type="submit" class="btn btn--full add_to_cart" name="action" value="add">Add to Cart</button>
                    </div>
                </form>
            </div>
        </div>
        <img src="${pageContext.request.contextPath}/img/meals/${categoryUrlName}/${meal.mealIcon}"
             alt="${meal.mealName}">
    </div>
</section>

<script src="${pageContext.request.contextPath}/js/quantity_counter.js"></script>
</body>
</html>