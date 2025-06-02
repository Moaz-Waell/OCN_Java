package com.ocn.controllers;

import com.ocn.beans.CategoryBean;
import com.ocn.beans.IngredientBean;
import com.ocn.beans.MealBean;
import com.ocn.dao.CategoryDAO;
import com.ocn.dao.IngredientDAO;
import com.ocn.dao.MealDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/AdminMealServlet")
public class AdminMealServlet extends HttpServlet {
    private MealDAO mealDAO;
    private IngredientDAO ingredientDAO;
    private ArrayList<IngredientBean> ingredientBeans;

    public void init() {
        mealDAO = new MealDAO();
        ingredientDAO = new IngredientDAO();
        ingredientBeans = new ArrayList<>();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentAdmin") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/adminLogin.jsp");
            return;
        }

        // Prevent caching of admin pages
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxies
        List<IngredientBean> ingredientBeans = ingredientDAO.getAllIngredients();
        request.setAttribute("ingredientBeans", ingredientBeans);

        String action = request.getParameter("action");
        try {
            if ("add".equals(action)) {
                // Add meal
                IngredientDAO tempDAO = new IngredientDAO();
                IngredientBean temp = new IngredientBean();
                MealBean meal = new MealBean();
                meal.setMealName(request.getParameter("meal_name"));
                meal.setMealPrice(Double.parseDouble(request.getParameter("price")));
                meal.setCategoryId(Integer.parseInt(request.getParameter("category")));
                meal.setMealIcon(request.getParameter("image"));

                // Get selected ingredients
                String[] ingredientIds = request.getParameterValues("ingredients");
                List<IngredientBean> ingredients = new ArrayList<>();
                if (ingredientIds != null) {

                    for (int i = 0; i < ingredientIds.length; i++) {
                        String id = ingredientIds[i];
                        IngredientBean ingredient = new IngredientBean();
                        ingredient.setIngredientId(Integer.parseInt(id));
                        temp = tempDAO.getIngredientById(Integer.parseInt(id));
                        ingredient.setIngredientName(temp.getIngredientName());
                        System.out.println(ingredient.toString());
                        ingredients.add(ingredient);
                    }
                }

                mealDAO.addMeal(meal, ingredients);

            } else if ("update".equals(action)) {
                // Update meal
                MealBean meal = new MealBean();
                meal.setMealId(Integer.parseInt(request.getParameter("meal_id")));
                meal.setMealName(request.getParameter("meal_name"));
                meal.setMealPrice(Double.parseDouble(request.getParameter("price")));
                meal.setCategoryId(Integer.parseInt(request.getParameter("category")));
                meal.setMealIcon(request.getParameter("image"));

                // Get selected ingredients
                String[] ingredientIds = request.getParameterValues("ingredients");
                List<IngredientBean> ingredients = new ArrayList<>();
                if (ingredientIds != null) {
                    for (String id : ingredientIds) {
                        IngredientBean ingredient = new IngredientBean();
                        ingredient.setIngredientId(Integer.parseInt(id));
                        ingredients.add(ingredient);
                    }
                }

//                mealDAO.deleteMealIngredients(meal.getMealId()); // Clear existing ingredients
                mealDAO.updateMeal(meal, ingredients); // Re-add selected ingredients
            }

            response.sendRedirect("AdminMealServlet"); // Redirect to refresh data
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("AdminMealServlet?error=1");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentAdmin") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/adminLogin.jsp");
            return;
        }

        // Prevent caching of admin pages
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxies

        String action = request.getParameter("action");
        try {
            // Fetch all meals, categories, and ingredients
            List<MealBean> meals = mealDAO.getAllMeals();
            List<CategoryBean> categories = new CategoryDAO().getAllCategories();
            List<IngredientBean> ingredients = new IngredientDAO().getAllIngredients();

            // Fetch ingredients for each meal and attach to meal object (or use a Map)
            Map<Integer, List<IngredientBean>> mealIngredientsMap = new HashMap<>();
            for (MealBean meal : meals) {
                List<IngredientBean> mealIngredients = mealDAO.getIngredientsByMealId(meal.getMealId());
                mealIngredientsMap.put(meal.getMealId(), mealIngredients);
            }
            if ("delete".equals(action)) {
                int mealId = Integer.parseInt(request.getParameter("meal_id"));
                mealDAO.deleteMeal(mealId);
                response.sendRedirect("AdminMealServlet");
                return; // Exit after redirect
            }
            // Set attributes for JSP
            request.setAttribute("meals", meals);
            request.setAttribute("categories", categories);
            request.setAttribute("ingredients", ingredients);
            request.setAttribute("mealIngredientsMap", mealIngredientsMap);
            request.getRequestDispatcher("/admin/AdminMeal.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}