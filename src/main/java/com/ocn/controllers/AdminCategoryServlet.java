package com.ocn.controllers;

import com.ocn.beans.CategoryBean;
import com.ocn.dao.CategoryDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/AdminCategoryServlet")
public class AdminCategoryServlet extends HttpServlet {
    private CategoryDAO categoryDAO;

    public void init() {
        categoryDAO = new CategoryDAO();
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

        try {
            String action = request.getParameter("action");
            if ("create".equals(action)) {
                handleCreate(request);
            } else if ("update".equals(action)) {
                handleUpdate(request);
            }

            List<CategoryBean> categories = categoryDAO.getAllCategories();
            request.setAttribute("categories", categories);
            request.getRequestDispatcher("/admin/AdminCategory.jsp").forward(request, response);

        } catch (Exception e) {
            handleError(response, e);
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
        response.setDateHeader("Expires", 0); // Proxies3
        try {
            String action = request.getParameter("action");
            if ("delete".equals(action)) {
                handleDelete(request);
            }

            List<CategoryBean> categories = categoryDAO.getAllCategories();
            request.setAttribute("categories", categories);
            request.getRequestDispatcher("/admin/AdminCategory.jsp").forward(request, response);

        } catch (Exception e) {
            handleError(response, e);
        }
    }

    private void handleCreate(HttpServletRequest request) {
        CategoryBean category = new CategoryBean();
        category.setCategoryName(request.getParameter("name"));
        category.setCategoryIcon(request.getParameter("icon"));
        categoryDAO.addCategory(category);
    }

    private void handleUpdate(HttpServletRequest request) {
        CategoryBean category = new CategoryBean();
        category.setCategoryId(Integer.parseInt(request.getParameter("categoryId")));
        category.setCategoryName(request.getParameter("name"));
        category.setCategoryIcon(request.getParameter("icon"));
        categoryDAO.updateCategory(category);
    }

    private void handleDelete(HttpServletRequest request) {
        int categoryId = Integer.parseInt(request.getParameter("categoryId"));
        categoryDAO.deleteCategory(categoryId);
    }

    private void handleError(HttpServletResponse response, Exception e) throws IOException {
        e.printStackTrace();
        response.sendRedirect("AdminCategoryServlet?error=1");
    }
}