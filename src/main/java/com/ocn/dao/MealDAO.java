package com.ocn.dao;
import com.ocn.beans.IngredientBean;
import com.ocn.beans.MealBean;
import com.ocn.connection.DBConnectionUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MealDAO {

    // Get meal by ID
    public MealBean getMealById(int mealId) {
        MealBean meal = null;
        String sql = "SELECT m.*, c.CATEGORY_Name " +
                "FROM meal m " +
                "JOIN category c ON m.CATEGORY_ID = c.CATEGORY_ID " +
                "WHERE m.MEAL_ID = ?";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, mealId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    meal = mapResultSetToMealBean(rs);
                    meal.setCategoryName(rs.getString("CATEGORY_Name")); // Add this line
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching meal by ID: " + e.getMessage());
        }
        return meal;
    }

    // Get meals by category ID
    public List<MealBean> getMealsByCategoryId(int categoryId) {
        List<MealBean> meals = new ArrayList<>();
        String sql = "SELECT * FROM meal WHERE CATEGORY_ID = ? ORDER BY MEAL_Name";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, categoryId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    MealBean meal = mapResultSetToMealBean(rs);
                    meals.add(meal);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching meals by category ID: " + e.getMessage());
        }
        return meals;
    }

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
            System.err.println("Error fetching ingredients for meal: " + e.getMessage());
        }
        return ingredients;
    }

    public List<MealBean> getBestSellerMeals() {
        List<MealBean> meals = new ArrayList<>();
        String sql = "SELECT m.*, c.CATEGORY_Name, SUM(od.M_Quantity) AS total_ordered " +
                "FROM MEAL m " +
                "JOIN ORDER_DETAILS od ON m.MEAL_ID = od.MEAL_ID " +
                "JOIN CATEGORY c ON m.CATEGORY_ID = c.CATEGORY_ID " +
                "GROUP BY m.MEAL_ID " +
                "HAVING total_ordered >= 25 " +
                "ORDER BY total_ordered DESC " +
                "LIMIT 6";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                MealBean meal = new MealBean();
                // Set basic meal properties
                meal.setMealId(rs.getInt("MEAL_ID"));
                meal.setMealName(rs.getString("MEAL_Name"));
                meal.setMealDescription(rs.getString("MEAL_Description"));
                meal.setMealPrice(rs.getDouble("MEAL_Price"));
                meal.setMealIcon(rs.getString("MEAL_Icon"));
                meal.setCategoryId(rs.getInt("CATEGORY_ID"));
                meal.setCategoryName(rs.getString("CATEGORY_Name"));
                meals.add(meal);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching best seller meals: " + e.getMessage());
            e.printStackTrace();
        }
        return meals;
    }

    public boolean addMeal(MealBean meal, List<IngredientBean> ingredients) {
        Connection connection = null;
        try {
            connection = DBConnectionUtil.getConnection();
            connection.setAutoCommit(false); // Start transaction

            // 1. Build description from ingredients
            StringBuilder description = new StringBuilder();
            for (IngredientBean ingredient : ingredients) {
                if (!description.isEmpty()) description.append(", ");
                description.append(ingredient.getIngredientName());
            }
            // 2. Insert meal with generated description
            String mealSql = "INSERT INTO meal (MEAL_Name, MEAL_Description, MEAL_Price, MEAL_Icon, CATEGORY_ID) " +
                    "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement mealStmt = connection.prepareStatement(mealSql, Statement.RETURN_GENERATED_KEYS)) {
                mealStmt.setString(1, meal.getMealName());

                mealStmt.setString(2, description.toString());

                mealStmt.setDouble(3, meal.getMealPrice());
                mealStmt.setString(4, meal.getMealIcon());
                mealStmt.setInt(5, meal.getCategoryId());

                int affectedRows = mealStmt.executeUpdate();
                if (affectedRows == 0) {
                    connection.rollback();
                    return false;
                }

                // Get generated meal ID
                try (ResultSet generatedKeys = mealStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        meal.setMealId(generatedKeys.getInt(1));
                    } else {
                        connection.rollback();
                        return false;
                    }
                }
            }


            // 3. Insert ingredients into MEAL_INGREDIENTS
            String ingredientSql = "INSERT INTO MEAL_INGREDIENTS (MEAL_ID, INGREDIENT_ID) VALUES (?, ?)";
            try (PreparedStatement ingredientStmt = connection.prepareStatement(ingredientSql)) {
                for (IngredientBean ingredient : ingredients) {
                    ingredientStmt.setInt(1, meal.getMealId());
                    ingredientStmt.setInt(2, ingredient.getIngredientId());
                    ingredientStmt.addBatch();
                }
                ingredientStmt.executeBatch();
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Rollback failed: " + ex.getMessage());
            }
            System.err.println("Error adding meal with ingredients: " + e.getMessage());
            return false;
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Connection close failed: " + e.getMessage());
            }
        }
    }




    public boolean updateMeal(MealBean meal, List<IngredientBean> ingredients) {
        Connection connection = null;
        try {
            connection = DBConnectionUtil.getConnection();
            connection.setAutoCommit(false); // Start transaction

            // 1. Build description from ingredients
            StringBuilder description = new StringBuilder();
            for (IngredientBean ingredient : ingredients) {
                if (!description.isEmpty()) description.append(", ");
                description.append(ingredient.getIngredientName());
            }

            // 2. Update the meal with the new description and details
            String mealSql = "UPDATE meal SET MEAL_Name = ?, MEAL_Description = ?, MEAL_Price = ?, MEAL_Icon = ?, CATEGORY_ID = ? WHERE MEAL_ID = ?";
            try (PreparedStatement mealStmt = connection.prepareStatement(mealSql)) {
                mealStmt.setString(1, meal.getMealName());
                mealStmt.setString(2, description.toString());
                mealStmt.setDouble(3, meal.getMealPrice());
                mealStmt.setString(4, meal.getMealIcon());
                mealStmt.setInt(5, meal.getCategoryId());
                mealStmt.setInt(6, meal.getMealId());

                int affectedRows = mealStmt.executeUpdate();
                if (affectedRows == 0) {
                    connection.rollback();
                    return false; // No meal found to update
                }
            }

            // 3. Delete existing ingredients for the meal
            String deleteSql = "DELETE FROM MEAL_INGREDIENTS WHERE MEAL_ID = ?";
            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, meal.getMealId());
                deleteStmt.executeUpdate();
            }

            // 4. Insert the new ingredients into MEAL_INGREDIENTS
            String ingredientSql = "INSERT INTO MEAL_INGREDIENTS (MEAL_ID, INGREDIENT_ID) VALUES (?, ?)";
            try (PreparedStatement ingredientStmt = connection.prepareStatement(ingredientSql)) {
                for (IngredientBean ingredient : ingredients) {
                    ingredientStmt.setInt(1, meal.getMealId());
                    ingredientStmt.setInt(2, ingredient.getIngredientId());
                    ingredientStmt.addBatch();
                }
                ingredientStmt.executeBatch();
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Rollback failed: " + ex.getMessage());
            }
            System.err.println("Error updating meal: " + e.getMessage());
            return false;
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Connection close failed: " + e.getMessage());
            }
        }
    }

    public List<MealBean> getAllMeals() {
        List<MealBean> meals = new ArrayList<>();
        String sql = "SELECT m.*, c.CATEGORY_Name " +
                "FROM MEAL m " +
                "JOIN CATEGORY c ON m.CATEGORY_ID = c.CATEGORY_ID " + // Include category name
                "ORDER BY m.MEAL_ID";

        try (Connection connection = DBConnectionUtil.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                MealBean meal = mapResultSetToMealBean(rs);
                meal.setCategoryName(rs.getString("CATEGORY_Name")); // Set category name
                meals.add(meal);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching meals: " + e.getMessage());
        }
        return meals;
    }


    // Delete a meal
    public boolean deleteMeal(int mealId) {
        String sqlDeleteMealIngredients = "DELETE FROM meal_ingredients WHERE MEAL_ID = ?";
        String sqlDeleteMeal = "DELETE FROM meal WHERE MEAL_ID = ?";
        
        Connection connection = null;
        try {
            connection = DBConnectionUtil.getConnection();
            connection.setAutoCommit(false); // Start transaction

            // Delete from meal_ingredients first
            try (PreparedStatement psMealIngredients = connection.prepareStatement(sqlDeleteMealIngredients)) {
                psMealIngredients.setInt(1, mealId);
                psMealIngredients.executeUpdate(); 
            }
            
            // Then delete from meal
            try (PreparedStatement psMeal = connection.prepareStatement(sqlDeleteMeal)) {
                psMeal.setInt(1, mealId);
                int affectedRows = psMeal.executeUpdate();
                if (affectedRows > 0) {
                    connection.commit(); // Commit transaction
                    return true;
                }
            }
            connection.rollback(); // Rollback if meal deletion failed
            return false;
        } catch (SQLException e) {
            System.err.println("Error deleting meal: " + e.getMessage());
            if (connection != null) {
                try {
                    connection.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    System.err.println("Error rolling back transaction: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true); // Reset auto-commit
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    // Helper method to map ResultSet to MealBean
    private MealBean mapResultSetToMealBean(ResultSet rs) throws SQLException {
        MealBean meal = new MealBean();
        meal.setMealId(rs.getInt("MEAL_ID"));
        meal.setMealName(rs.getString("MEAL_Name"));
        meal.setMealDescription(rs.getString("MEAL_Description"));
        meal.setMealPrice(rs.getDouble("MEAL_Price"));
        meal.setMealIcon(rs.getString("MEAL_Icon"));
        meal.setCategoryId(rs.getInt("CATEGORY_ID"));
        return meal;
    }

    // --- Meal Ingredients Linkage ---

    public boolean addIngredientToMeal(int mealId, int ingredientId) {
        String sql = "INSERT INTO meal_ingredients (MEAL_ID, INGREDIENT_ID) VALUES (?, ?)";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, mealId);
            preparedStatement.setInt(2, ingredientId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding ingredient to meal: " + e.getMessage());
            return false;
        }
    }

    public boolean removeIngredientFromMeal(int mealId, int ingredientId) {
        String sql = "DELETE FROM meal_ingredients WHERE MEAL_ID = ? AND INGREDIENT_ID = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, mealId);
            preparedStatement.setInt(2, ingredientId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error removing ingredient from meal: " + e.getMessage());
            return false;
        }
    }


    // In MealDAO.java
    public boolean deleteMealIngredients(int mealId) {
        String sql = "DELETE FROM meal_ingredients WHERE MEAL_ID = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, mealId);
            return preparedStatement.executeUpdate() >= 0; // Returns true even if 0 rows deleted
        } catch (SQLException e) {
            System.err.println("Error deleting meal ingredients: " + e.getMessage());
            return false;
        }
    }

    public boolean updateMealIngredient(int mealId, int oldIngredientId, int newIngredientId) {
        // First remove the old ingredient
        if (!removeIngredientFromMeal(mealId, oldIngredientId)) {
            return false; // Failed to remove
        }
        // Then add the new one
        return addIngredientToMeal(mealId, newIngredientId);
    }

}

