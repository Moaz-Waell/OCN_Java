package com.ocn.dao;

import com.ocn.beans.AdminBean;
import com.ocn.connection.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {

    public static AdminBean validateAdmin(String adminId, String pin) {
        AdminBean admin = null;
        String sql = "SELECT * FROM admins WHERE ADMIN_ID = ? AND ADMIN_Pin = ?";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, adminId);
            preparedStatement.setString(2, pin);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    admin = new AdminBean();
                    admin.setAdminId(rs.getString("ADMIN_ID"));
                    admin.setAdminName(rs.getString("ADMIN_Name"));
                    admin.setAdminPin(rs.getString("ADMIN_Pin"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error validating admin: " + e.getMessage());
        }
        return admin;
    }
    // public AdminBean getAdminById(int adminId) { ... }
}

