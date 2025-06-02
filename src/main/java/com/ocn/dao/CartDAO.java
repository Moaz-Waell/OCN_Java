package com.ocn.dao;



import com.ocn.beans.CartBean;
import com.ocn.connection.DBConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {

    // Get all cart items for a specific user
    public List<CartBean> getCartItemsByUserId(int userId) {
        List<CartBean> cartItems = new ArrayList<>();
        String sql = "SELECT * FROM cart WHERE USERS_ID = ?";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    CartBean item = new CartBean();
                    item.setCartId(rs.getInt("CART_ID"));
                    item.setUserId(rs.getInt("USERS_ID"));
                    item.setMealId(rs.getInt("MEAL_ID"));
                    item.setNote(rs.getString("NOTE"));
                    item.setQuantity(rs.getInt("QUANTITY"));
                    cartItems.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching cart items by user ID: " + e.getMessage());
        }
        return cartItems;
    }

    // Add an item to the cart
    public boolean addItemToCart(CartBean cartItem) {
        // Check if item already exists for user, if so, update quantity?
        // For now, simple add. Logic for update/merge can be in servlet or here.
        String sql = "INSERT INTO cart (USERS_ID, MEAL_ID, NOTE, QUANTITY) VALUES (?, ?, ?, ?)";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, cartItem.getUserId());
            preparedStatement.setInt(2, cartItem.getMealId());
            preparedStatement.setString(3, cartItem.getNote());
            preparedStatement.setInt(4, cartItem.getQuantity());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        cartItem.setCartId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error adding item to cart: " + e.getMessage());
            return false;
        }
    }

    // Update quantity of an item in the cart
    public boolean updateCartItemQuantity(int cartId, int quantity) {
        String sql = "UPDATE cart SET QUANTITY = ? WHERE CART_ID = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, quantity);
            preparedStatement.setInt(2, cartId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating cart item quantity: " + e.getMessage());
            return false;
        }
    }

    // Remove an item from the cart
    public boolean removeItemFromCart(int cartId) {
        String sql = "DELETE FROM cart WHERE CART_ID = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, cartId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error removing item from cart: " + e.getMessage());
            return false;
        }
    }

    // Clear all items from a user's cart (e.g., after checkout)
    public boolean clearCart(int userId) {
        String sql = "DELETE FROM cart WHERE USERS_ID = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            return preparedStatement.executeUpdate() >= 0; // Return true even if cart was already empty
        } catch (SQLException e) {
            System.err.println("Error clearing cart: " + e.getMessage());
            return false;
        }
    }

    // Get a specific cart item by its ID (might be useful for updates)
    public CartBean getCartItemById(int cartId) {
        CartBean item = null;
        String sql = "SELECT * FROM cart WHERE CART_ID = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, cartId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    item = new CartBean();
                    item.setCartId(rs.getInt("CART_ID"));
                    item.setUserId(rs.getInt("USERS_ID"));
                    item.setMealId(rs.getInt("MEAL_ID"));
                    item.setNote(rs.getString("NOTE"));
                    item.setQuantity(rs.getInt("QUANTITY"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching cart item by ID: " + e.getMessage());
        }
        return item;
    }
}

