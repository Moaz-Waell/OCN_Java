package com.ocn.dao;
import com.ocn.beans.CategoryBean;
import com.ocn.connection.DBConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public List<CategoryBean> getAllCategories() {
        List<CategoryBean> categories = new ArrayList<>();
        String sql = "SELECT * FROM category ORDER BY CATEGORY_ID"; // Assuming CATEGORY_ID or some order is desired

        try (Connection connection = DBConnectionUtil.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                CategoryBean category = new CategoryBean();
                category.setCategoryId(rs.getInt("CATEGORY_ID"));
                category.setCategoryName(rs.getString("CATEGORY_Name"));
                category.setCategoryIcon(rs.getString("CATEGORY_Icon"));
                categories.add(category);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all categories: " + e.getMessage());
        }
        return categories;
    }

    public CategoryBean getCategoryById(int categoryId) {
        CategoryBean category = null;
        String sql = "SELECT * FROM category WHERE CATEGORY_ID = ?";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, categoryId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    category = new CategoryBean();
                    category.setCategoryId(rs.getInt("CATEGORY_ID"));
                    category.setCategoryName(rs.getString("CATEGORY_Name"));
                    category.setCategoryIcon(rs.getString("CATEGORY_Icon"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching category by ID: " + e.getMessage());
        }
        return category;
    }

    // CRUD operations for admin
    public boolean addCategory(CategoryBean category) {
        String sql = "INSERT INTO category (CATEGORY_Name, CATEGORY_Icon) VALUES (?, ?)";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, category.getCategoryName());
            preparedStatement.setString(2, category.getCategoryIcon());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        category.setCategoryId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error adding category: " + e.getMessage());
            return false;
        }
    }

    public boolean updateCategory(CategoryBean category) {
        String sql = "UPDATE category SET CATEGORY_Name = ?, CATEGORY_Icon = ? WHERE CATEGORY_ID = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, category.getCategoryName());
            preparedStatement.setString(2, category.getCategoryIcon());
            preparedStatement.setInt(3, category.getCategoryId());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating category: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCategory(int categoryId) {
        // First, delete all meals linked to this category
        String deleteMealsSql = "DELETE FROM MEAL WHERE CATEGORY_ID = ?";
        // Then, delete the category
        String deleteCategorySql = "DELETE FROM CATEGORY WHERE CATEGORY_ID = ?";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement deleteMealsStmt = connection.prepareStatement(deleteMealsSql);
             PreparedStatement deleteCategoryStmt = connection.prepareStatement(deleteCategorySql)) {

            // Delete meals first
            deleteMealsStmt.setInt(1, categoryId);
            deleteMealsStmt.executeUpdate(); // Execute even if no meals exist

            // Then delete the category
            deleteCategoryStmt.setInt(1, categoryId);
            return deleteCategoryStmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting category: " + e.getMessage());
            return false;
        }
    }
}

