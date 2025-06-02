package com.ocn.controllers;


import com.ocn.beans.*;
import com.ocn.dao.CategoryDAO;
import com.ocn.dao.MealDAO;
import com.ocn.dao.OcnUserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/MealServlet")
public class MealServlet extends HttpServlet {
    private MealDAO mealDAO;
    private OcnUserDAO OcnUserDAO;
    private CategoryDAO categoryDAO; // For category name for image path

    public void init() {
        mealDAO = new MealDAO();
        OcnUserDAO = new OcnUserDAO();
        categoryDAO = new CategoryDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String contextPath = request.getContextPath();

        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(contextPath + "/aast/UniUserPortal.jsp");
            return;
        }

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxies

        String mealIdStr = request.getParameter("id");
        if (mealIdStr == null || mealIdStr.trim().isEmpty()) {
            response.sendRedirect(contextPath + "/HomeServlet"); // Redirect if no meal ID
            return;
        }

        int mealId;
        try {
            mealId = Integer.parseInt(mealIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(contextPath + "/HomeServlet"); // Redirect if ID is not a number
            return;
        }

        MealBean meal = mealDAO.getMealById(mealId);
        if (meal == null) {
            // Meal not found, redirect to home or an error page
            response.sendRedirect(contextPath + "/HomeServlet");
            return;
        }

        List<IngredientBean> mealIngredients = mealDAO.getIngredientsByMealId(mealId);
        OcnUserBean currentUser = (OcnUserBean) session.getAttribute("currentUser");
        List<AllergyBean> userAllergies = OcnUserDAO.getUserAllergies(currentUser.getUserId());

        // Check for allergens
        List<AllergyBean> detectedAllergensInMeal = new ArrayList<>();
        if (!userAllergies.isEmpty() && !mealIngredients.isEmpty()) {
            // Case-insensitive comparison
            List<String> userAllergyNamesLower = userAllergies.stream()
                    .map(a -> a.getAllergyName().toLowerCase())
                    .toList();

            for (IngredientBean ingredient : mealIngredients) {
                String ingNameLower = ingredient.getIngredientName().toLowerCase();
                if (userAllergyNamesLower.contains(ingNameLower)) {
                    // Find matching allergy (case-sensitive name)
                    userAllergies.stream()
                            .filter(a -> a.getAllergyName().equalsIgnoreCase(ingredient.getIngredientName())) // Fix here
                            .findFirst()
                            .ifPresent(detectedAllergensInMeal::add);
                }
            }
        }
        
        // Get category name for image path construction
        CategoryBean category = categoryDAO.getCategoryById(meal.getCategoryId());
        String categoryUrlName = "default"; // Default if category not found or name is problematic
        if (category != null && category.getCategoryName() != null) {
            categoryUrlName = category.getCategoryName().toLowerCase().replaceAll("[^a-z0-9]+", "");
        }

        request.setAttribute("meal", meal);
        request.setAttribute("mealIngredients", mealIngredients);
        request.setAttribute("userAllergies", userAllergies); // All allergies of the user
        request.setAttribute("detectedAllergensInMeal", detectedAllergensInMeal); // Allergens from user's list found in this meal
        request.setAttribute("currentUser", currentUser);
        request.setAttribute("categoryUrlName", categoryUrlName);

        request.getRequestDispatcher("/user/viewMealDetails.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Could be used for actions like adding to cart from meal details page, but CartServlet handles that
        doGet(request, response); 
    }
}

