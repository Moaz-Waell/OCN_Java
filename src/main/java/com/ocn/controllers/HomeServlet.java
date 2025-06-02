package com.ocn.controllers;
import com.ocn.beans.*;
import com.ocn.dao.AllergyDAO;
import com.ocn.dao.CategoryDAO;
import com.ocn.dao.MealDAO;
import com.ocn.dao.OcnUserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet("/HomeServlet")
public class HomeServlet extends HttpServlet {
    private CategoryDAO categoryDAO;
    private MealDAO mealDAO;
    private AllergyDAO AllergyDAO;

    public void init() {
        categoryDAO = new CategoryDAO();
        mealDAO = new MealDAO();
        AllergyDAO = new AllergyDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String contextPath = request.getContextPath();
        if (session == null || session.getAttribute("uniUser") == null) {
            System.out.println("session is null");
            response.sendRedirect(contextPath + "/aast/UniUserPortal.jsp");
            return;
        }

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxies

        OcnUserBean currentUser = null;

        //1. Get cookies and initialize variables
        Cookie[] cookies = request.getCookies();
        int userId = -1;
        String userName = null;
        String userPhone = null;
        int userAttendance = -1;
        String userEmail = null;

        //2. Extract and decode cookie values
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                System.out.println("Checking cookie: " + cookie.getName() + " -> " + cookie.getValue());
                try {
                    switch (cookie.getName()) {
                        case "userId":
                            userId = Integer.parseInt(URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8));
                            break;
                        case "userName":
                            userName = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                            break;
                        case "userPhone":
                            userPhone = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                            break;
                        case "userAttendance":
                            userAttendance = Integer.parseInt(URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8));
                            break;
                        case "userEmail":
                            userEmail = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                            break;
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Error decoding cookie: " + e.getMessage());
                    response.sendRedirect(contextPath + "/aast/UniUserPortal.jsp");
                    return;
                }
            }
        }

        //3. Validate all required cookies
        if (userId == -1 || userName == null || userPhone == null || userAttendance == -1 || userEmail == null) {
            response.sendRedirect(contextPath + "/aast/UniUserPortal.jsp");
            return;
        }

        //4. Create OcnUserBean from cookies
        currentUser = new OcnUserBean();
        currentUser.setUserId(userId);
        currentUser.setUserName(userName);
        currentUser.setUserPhone(userPhone);
        currentUser.setUserAttendance(userAttendance);
        currentUser.setUserEmail(userEmail);

        //5. Insert/Update user in database
        if (!OcnUserDAO.newOcnUser(currentUser)) {
            request.setAttribute("error", "Failed to initialize user profile");
            response.sendRedirect(contextPath + "/aast/UniUserPortal.jsp");
            return;
        }

        //6. Update session with OcnUserBean
        // HttpSession session = request.getSession();
        session.setAttribute("currentUser", currentUser);

        //7. Proceed with normal page setup
        List<CategoryBean> categories = categoryDAO.getAllCategories();
        List<MealBean> bestSellerMeals = mealDAO.getBestSellerMeals();
        List<AllergyBean> allAllergies = AllergyDAO.getAllAllergies();

        // Check if user has existing allergies
        boolean hasAllergies = new OcnUserDAO().hasUserAllergies(currentUser.getUserId());
        request.setAttribute("showAllergyForm", !hasAllergies);

        request.setAttribute("currentUser", currentUser);
        request.setAttribute("allAllergies", allAllergies);
        request.setAttribute("categories", categories);
        request.setAttribute("bestSellerMeals", bestSellerMeals);

        request.getRequestDispatcher("/user/home.jsp").forward(request, response);
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}

