package com.ocn.controllers;

import com.ocn.beans.CartBean;
import com.ocn.beans.IngredientBean;
import com.ocn.beans.MealBean;
import com.ocn.beans.OcnUserBean;
import com.ocn.dao.CartDAO;
import com.ocn.dao.MealDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/CartServlet")
public class CartServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private CartDAO cartDAO;
    private MealDAO mealDAO;

    public void init() {
        cartDAO = new CartDAO();
        mealDAO = new MealDAO();
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

        OcnUserBean currentUser = (OcnUserBean) session.getAttribute("currentUser");

        // Handle increment action
        String action = request.getParameter("action");
        if ("increment".equals(action)) {
            handleIncrementCart(request, response, currentUser);
            return;
        }

        request.setAttribute("cartItems", cartDAO.getCartItemsByUserId(currentUser.getUserId()));
        request.getRequestDispatcher("/user/cart.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String contextPath = request.getContextPath();

        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(contextPath + "/aast/UniUserPortal.jsp");
            return;
        }
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxies

        OcnUserBean currentUser = (OcnUserBean) session.getAttribute("currentUser");
        String action = request.getParameter("action");

        if ("add".equals(action)) {
            handleAddToCart(request, response, currentUser);
        } else if ("update".equals(action)) {
            handleUpdateCart(request, response);
        } else if ("remove".equals(action)) {
            handleRemoveFromCart(request, response);
        } else if ("clear".equals(action)) {
            cartDAO.clearCart(currentUser.getUserId());
            response.sendRedirect(contextPath + "/CartServlet");
        } else {
            doGet(request, response);
        }
    }

    private void handleAddToCart(HttpServletRequest request, HttpServletResponse response, OcnUserBean currentUser)
            throws IOException {
        String mealIdStr = request.getParameter("mealId");
        String quantityStr = request.getParameter("quantity");
        String[] selectedIngredients = request.getParameterValues("ingredients");
        String contextPath = request.getContextPath();

        if (mealIdStr != null && quantityStr != null) {
            try {
                int mealId = Integer.parseInt(mealIdStr);
                int quantity = Integer.parseInt(quantityStr);
                MealBean meal = mealDAO.getMealById(mealId);

                if (meal != null && quantity > 0) {
                    List<IngredientBean> allIngredients = mealDAO.getIngredientsByMealId(mealId);
                    List<String> selected = selectedIngredients != null ?
                            Arrays.asList(selectedIngredients) : new ArrayList<>();

                    // Calculate excluded ingredients
                    List<String> excluded = new ArrayList<>();
                    for (IngredientBean ing : allIngredients) {
                        if (!selected.contains(ing.getIngredientName())) {
                            excluded.add(ing.getIngredientName());
                        }
                    }

                    // Build note
                    String note = excluded.isEmpty() ? "" : "Exclude: " + String.join(", ", excluded);

                    // Check for existing cart items with same meal and note
                    List<CartBean> userCart = cartDAO.getCartItemsByUserId(currentUser.getUserId());
                    boolean itemExists = false;

                    for (CartBean existingItem : userCart) {
                        if (existingItem.getMealId() == mealId && existingItem.getNote().equals(note)) {
                            // Update existing item's quantity
                            cartDAO.updateCartItemQuantity(
                                    existingItem.getCartId(),
                                    existingItem.getQuantity() + quantity
                            );
                            itemExists = true;
                            break;
                        }
                    }

                    if (!itemExists) {
                        // Create new cart item
                        CartBean newItem = new CartBean();
                        newItem.setUserId(currentUser.getUserId());
                        newItem.setMealId(mealId);
                        newItem.setQuantity(quantity);
                        newItem.setNote(note);
                        cartDAO.addItemToCart(newItem);
                    }

                    // Redirect to meal's category after adding to cart
                    int categoryId = meal.getCategoryId();
                    response.sendRedirect(contextPath + "/CategoryServlet?id=" + categoryId + "&mealAdded=true");
                    return;
                }
            } catch (NumberFormatException e) {
                //
            }
        }
        // Fallback redirect if any error occurs
        response.sendRedirect(contextPath + "/HomeServlet");
    }

    private void handleUpdateCart(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String cartIdStr = request.getParameter("cartId");
        String quantityStr = request.getParameter("quantity");

        if (cartIdStr != null && quantityStr != null) {
            try {
                int cartId = Integer.parseInt(cartIdStr);
                int quantity = Integer.parseInt(quantityStr);

                if (quantity > 0) {
                    cartDAO.updateCartItemQuantity(cartId, quantity);
                } else {
                    cartDAO.removeItemFromCart(cartId);
                }
            } catch (NumberFormatException e) {

            }
        }
        response.sendRedirect(request.getContextPath() + "/CartServlet");
    }

    private void handleRemoveFromCart(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String cartIdStr = request.getParameter("cartId");

        if (cartIdStr != null) {
            try {
                int cartId = Integer.parseInt(cartIdStr);
                cartDAO.removeItemFromCart(cartId);
            } catch (NumberFormatException e) {
                // Handle invalid input
            }
        }
        response.sendRedirect(request.getContextPath() + "/CartServlet");
    }

    private void handleIncrementCart(HttpServletRequest request, HttpServletResponse response, OcnUserBean currentUser)
            throws IOException {
        String cartIdStr = request.getParameter("cart_id");
        String contextPath = request.getContextPath();

        if (cartIdStr != null) {
            try {
                int cartId = Integer.parseInt(cartIdStr);
                CartBean cartItem = cartDAO.getCartItemById(cartId);

                if (cartItem != null && cartItem.getUserId() == currentUser.getUserId()) {
                    // Increment the quantity by 1
                    cartDAO.updateCartItemQuantity(cartId, cartItem.getQuantity() + 1);
                }
            } catch (NumberFormatException e) {
                // Invalid cart ID format
            }
        }
        response.sendRedirect(contextPath + "/CartServlet");
    }
}