package com.ocn.controllers;

import com.ocn.beans.OcnUserBean;
import com.ocn.dao.OcnUserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/SubmitAllergiesServlet")
public class SubmitAllergiesServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String contextPath = request.getContextPath();

        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(contextPath + "/aast/UniUserLogin.jsp");
            return;
        }

        OcnUserBean user = (OcnUserBean) session.getAttribute("currentUser");
        int userId = user.getUserId();

        try {
            // Handle "No Allergies" button
            if (request.getParameter("no_allergies") != null) {
                // Insert a placeholder record (requires allergy ID 0 in database)
                OcnUserDAO userDAO = new OcnUserDAO();
                userDAO.setUserAllergy(userId, 11, "No");
            }
            // Handle selected allergies
            else {
                String[] allergies = request.getParameterValues("allergies");
                if (allergies != null) {
                    OcnUserDAO userDAO = new OcnUserDAO();
                    for (String allergyId : allergies) {
                        int id = Integer.parseInt(allergyId);
                        userDAO.setUserAllergy(userId, id, "yes");
                    }
                }
            }

            response.sendRedirect(contextPath + "/HomeServlet");

        } catch (Exception e) {
            request.setAttribute("error", "Error saving allergies: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
}