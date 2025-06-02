package com.ocn.dao;

import com.ocn.beans.IngredientBean;
import com.ocn.connection.DBConnectionUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IngredientDAO {

    public List<IngredientBean> getAllIngredients() {
        List<IngredientBean> ingredients = new ArrayList<>();
        String sql = "SELECT * FROM ingredients ORDER BY INGREDIENT_ID";

        try (Connection connection = DBConnectionUtil.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                IngredientBean ingredient = new IngredientBean();
                ingredient.setIngredientId(rs.getInt("INGREDIENT_ID"));
                ingredient.setIngredientName(rs.getString("INGREDIENT_NAME"));
                ingredients.add(ingredient);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all ingredients: " + e.getMessage());
        }
        return ingredients;
    }



    public IngredientBean getIngredientById(int ingredientId) {
        IngredientBean ingredient = null;
        String sql = "SELECT * FROM ingredients WHERE INGREDIENT_ID = ?";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, ingredientId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    ingredient = new IngredientBean();
                    ingredient.setIngredientId(rs.getInt("INGREDIENT_ID"));
                    ingredient.setIngredientName(rs.getString("INGREDIENT_NAME"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching ingredient by ID: " + e.getMessage());
        }
        return ingredient;
    }

    // Get ingredients for a specific meal (from meal_ingredients table)
    public List<IngredientBean> getIngredientsByMealId(int mealId) {
        List<IngredientBean> ingredients = new ArrayList<>();
        String sql = "SELECT i.INGREDIENT_ID, i.INGREDIENT_NAME FROM ingredients i " +
                "JOIN meal_ingredients mi ON i.INGREDIENT_ID = mi.INGREDIENT_ID " +
                "WHERE mi.MEAL_ID = ? ORDER BY i.INGREDIENT_NAME";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, mealId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    IngredientBean ingredient = new IngredientBean();
                    ingredient.setIngredientId(rs.getInt("INGREDIENT_ID"));
                    ingredient.setIngredientName(rs.getString("INGREDIENT_NAME"));
                    ingredients.add(ingredient);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching ingredients by meal ID: " + e.getMessage());
        }
        return ingredients;
    }

    // CRUD operations for admin (if ingredients are manageable)
    public boolean addIngredient(IngredientBean ingredient) {
        String sql = "INSERT INTO ingredients (INGREDIENT_NAME) VALUES (?)";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, ingredient.getIngredientName());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        ingredient.setIngredientId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error adding ingredient: " + e.getMessage());
            return false;
        }
    }

    public boolean updateIngredient(IngredientBean ingredient) {
        String sql = "UPDATE ingredients SET INGREDIENT_NAME = ? WHERE INGREDIENT_ID = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, ingredient.getIngredientName());
            preparedStatement.setInt(2, ingredient.getIngredientId());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating ingredient: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteIngredient(int ingredientId) {
        // Consider impact on meal_ingredients table (CASCADE or manual cleanup)
        String sql = "DELETE FROM ingredients WHERE INGREDIENT_ID = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, ingredientId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting ingredient: " + e.getMessage());
            return false;
        }
    }
}

