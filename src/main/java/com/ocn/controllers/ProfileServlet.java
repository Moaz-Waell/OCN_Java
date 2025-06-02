package com.ocn.controllers;

import com.ocn.beans.AllergyBean;
import com.ocn.beans.OcnUserBean;
import com.ocn.dao.AllergyDAO;
import com.ocn.dao.OcnUserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/ProfileServlet")
public class ProfileServlet extends HttpServlet {
    private OcnUserDAO userDAO;
    private AllergyDAO allergyDAO;

    public void init() {
        userDAO = new OcnUserDAO();
        allergyDAO = new AllergyDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/aast/UniUserPortal.jsp");
            return;
        }

        OcnUserBean currentUser = (OcnUserBean) session.getAttribute("currentUser");

        // Refresh user data from database
        OcnUserBean freshUser = userDAO.getUserById(currentUser.getUserId());
        List<AllergyBean> currentUserAllergies = userDAO.getUserAllergies(freshUser.getUserId());
        List<AllergyBean> allAllergies = allergyDAO.getAllAllergies();

        request.setAttribute("currentUser", freshUser);
        request.setAttribute("allAllergies", allAllergies);
        request.setAttribute("currentUserAllergies", currentUserAllergies);

        request.getRequestDispatcher("/user/profile.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/aast/UniUserPortal.jsp");
            return;
        }

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxies

        OcnUserBean currentUser = (OcnUserBean) session.getAttribute("currentUser");
        String phone = request.getParameter("phone");
        String[] selectedAllergies = request.getParameterValues("selectedAllergies");

        try {
            // Update phone number
            currentUser.setUserPhone(phone);
            boolean phoneUpdated = userDAO.updateUserPhone(currentUser);

            // Update allergies
            userDAO.clearUserAllergies(currentUser.getUserId());
            if (selectedAllergies != null) {
                for (String allergyIdStr : selectedAllergies) {
                    int allergyId = Integer.parseInt(allergyIdStr);
                    userDAO.setUserAllergy(currentUser.getUserId(), allergyId, "Yes");
                }
            }

            if (phoneUpdated) {
                // Update session with fresh data
                OcnUserBean updatedUser = userDAO.getUserById(currentUser.getUserId());
                session.setAttribute("currentUser", updatedUser);
                request.setAttribute("successMessage", "Profile updated successfully!");
            } else {
                request.setAttribute("errorMessage", "Failed to update profile");
            }

        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid allergy selection");
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error updating profile: " + e.getMessage());
        }

        doGet(request, response); // Refresh the page with updated data
    }
}