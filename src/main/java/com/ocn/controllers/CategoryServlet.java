package com.ocn.controllers;


import com.ocn.beans.CategoryBean;
import com.ocn.beans.MealBean;
import com.ocn.beans.OcnUserBean;
import com.ocn.dao.CategoryDAO;
import com.ocn.dao.MealDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/CategoryServlet")
public class CategoryServlet extends HttpServlet {
    private CategoryDAO categoryDAO;
    private MealDAO mealDAO;

    public void init() {
        categoryDAO = new CategoryDAO();
        mealDAO = new MealDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String contextPath = request.getContextPath();

        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(contextPath + "/aast/UniUserPortal.jsp");
            return;
        }

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxies

        String categoryIdStr = request.getParameter("id");
        if (categoryIdStr == null || categoryIdStr.trim().isEmpty()) {
            response.sendRedirect(contextPath + "/HomeServlet"); // Redirect to home if no category ID
            return;
        }

        int categoryId;
        try {
            categoryId = Integer.parseInt(categoryIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(contextPath + "/HomeServlet"); // Redirect if ID is not a number
            return;
        }

        CategoryBean category = categoryDAO.getCategoryById(categoryId);
        if (category == null) {
            // Category not found, redirect to home or an error page
            response.sendRedirect(contextPath + "/HomeServlet");
            return;
        }

        // Check if meal was just added to cart
        String mealAdded = request.getParameter("mealAdded");
        if (mealAdded != null && mealAdded.equals("true")) {
            session.setAttribute("mealAddedMessage", "Meal successfully added to cart!");
        }


        List<MealBean> mealsInCategory = mealDAO.getMealsByCategoryId(categoryId);

        request.setAttribute("categoryID", category.getCategoryId());
        request.setAttribute("categoryName", category.getCategoryName());
        request.setAttribute("mealsInCategory", mealsInCategory);
        request.setAttribute("currentUser", session.getAttribute("currentUser"));

        request.getRequestDispatcher("/user/categoryMeals.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}

