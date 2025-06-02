package com.ocn.filters;

import com.ocn.beans.OcnUserBean;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {
        "/CartServlet",
        "/OrderServlet",
        "/ProfileServlet",
        "/MealServlet",
        "/CategoryServlet",
        "/CheckoutServlet",
        "/SubmitAllergiesServlet",
        "/KitchenServlet"
})
public class OcnAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Check if user is logged into OCN
        HttpSession session = req.getSession(false);
        OcnUserBean user = (session != null) ? (OcnUserBean) session.getAttribute("currentUser") : null;
        if (user == null) {
            // Redirect to university login if not authenticated
            res.sendRedirect(req.getContextPath() + "/aast/UniUserLogin.jsp");
            return;
        }

        // Prevent caching of sensitive pages
        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setDateHeader("Expires", 0);

        // Pass request to the next filter/servlet
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}