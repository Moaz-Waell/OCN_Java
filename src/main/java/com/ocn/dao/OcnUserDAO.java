package com.ocn.dao;

//import com.ocn.beans.AllergyBean;
import com.ocn.beans.AllergyBean;
import com.ocn.beans.OcnUserBean;
import com.ocn.beans.UniUserBean;
import com.ocn.connection.DBConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OcnUserDAO {


    // Add a new user in OCN
    public static boolean newOcnUser(OcnUserBean user) {
        String sql = "INSERT INTO users (USERS_ID, USERS_Phnumber, USERS_Name, USERS_Attendance, USERS_Email) "
                + "VALUES (?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE "
                + "USERS_Phnumber = VALUES(USERS_Phnumber), "
                + "USERS_Name = VALUES(USERS_Name), "
                + "USERS_Attendance = VALUES(USERS_Attendance),"
                + "USERS_Email = VALUES(USERS_Email)";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, user.getUserId());
            ps.setString(2, user.getUserPhone());
            ps.setString(3, user.getUserName());
            ps.setInt(4, user.getUserAttendance());
            ps.setString(5, user.getUserEmail());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error upserting user: " + e.getMessage());
            return false;
        }
    }

    // Get user by ID
    public OcnUserBean getUserById(int userId) {
        OcnUserBean user = null;
        String sql = "SELECT * FROM users WHERE USERS_ID = ?";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new OcnUserBean();
                    user.setUserId(rs.getInt("USERS_ID"));
                    user.setUserName(rs.getString("USERS_Name"));
                    user.setUserPhone(rs.getString("USERS_Phnumber"));
                    user.setUserAttendance(rs.getInt("USERS_Attendance"));
                    user.setUserEmail(rs.getString("USERS_Email"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user: " + e.getMessage());
        }
        return user;
    }

    // --- User Allergy Management ---

    //set new user allergies in OCN
    public boolean setUserAllergy(int userId, int allergyId, String has_allergy) {
        String sql = "INSERT INTO user_allergies (USERS_ID, ALLERGY_ID,Has_Allergy) VALUES (?, ?, ?)";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, allergyId);
            preparedStatement.setString(3, has_allergy);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            // Handle potential duplicate entry if primary key constraint is violated
            System.err.println("Error adding user allergy: " + e.getMessage());
            return false;
        }
    }

    public boolean removeUserAllergy(int userId, int allergyId, String has_allergy) {
        String sql = "DELETE FROM user_allergies WHERE USERS_ID = ? AND ALLERGY_ID = ? AND Has_Allergy = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, allergyId);
            preparedStatement.setString(3, has_allergy);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error removing user allergy: " + e.getMessage());
            return false;
        }
    }

    // Update user phone
    public boolean updateUserPhone(OcnUserBean user) {
        String sql = "UPDATE users SET  USERS_Phnumber = ? WHERE USERS_ID = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getUserPhone());
            preparedStatement.setInt(2, user.getUserId());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user profile: " + e.getMessage());
            return false;
        }
    }

    public List<AllergyBean> getUserAllergies(int userId) {
        List<AllergyBean> userAllergies = new ArrayList<>();
        String sql = "SELECT a.ALLERGY_ID, a.ALLERGY_Name FROM allergy a " +
                     "JOIN user_allergies ua ON a.ALLERGY_ID = ua.ALLERGY_ID " +
                     "WHERE ua.USERS_ID = ? ORDER BY a.ALLERGY_Name";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    AllergyBean allergy = new AllergyBean();
                    allergy.setAllergyId(rs.getInt("ALLERGY_ID"));
                    allergy.setAllergyName(rs.getString("ALLERGY_Name"));
                    userAllergies.add(allergy);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user allergies: " + e.getMessage());
        }
        return userAllergies;
    }

    public boolean hasUserAllergies(int userId) {
        String sql = "SELECT COUNT(*) FROM user_allergies WHERE USERS_ID = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking user allergies: " + e.getMessage());
        }
        return false;
    }

    public boolean clearUserAllergies(int userId) {
        String sql = "DELETE FROM user_allergies WHERE USERS_ID = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() >= 0;
        } catch (SQLException e) {
            System.err.println("Error clearing allergies: " + e.getMessage());
            return false;
        }
    }


//    public OcnUserBean mapResultSetToOcnUserBean(ResultSet rs) throws SQLException {
//        OcnUserBean user = new OcnUserBean();
//        user.setUserId(rs.getInt("USERS_ID"));
//        user.setUserName(rs.getString("USERS_Name"));
//        user.setUserPhone(rs.getString("USERS_Phnumber"));
//        user.setUserAttendance(rs.getInt("USERS_Attendance"));
//        return user;
//    }
}

