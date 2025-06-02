package com.ocn.dao;

import com.ocn.beans.UniUserBean;
import com.ocn.beans.OcnUserBean;
import com.ocn.connection.DBConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UniUserDAO {

    public static UniUserBean validateUniUser(int username, int pincode) {
        UniUserBean user = null;
        String sql = "SELECT * FROM UNI_USERS WHERE USERS_ID = ? AND USERS_Pincode = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, username);
            preparedStatement.setInt(2, pincode);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUniUserBean(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error validating uni_user: " + e.getMessage());
            e.printStackTrace();
        }
        return user;
    }

    static UniUserBean mapResultSetToUniUserBean(ResultSet rs) throws SQLException {
        UniUserBean user = new UniUserBean();
        user.setUniId(rs.getString("USERS_ID"));
        user.setUniPin(rs.getString("USERS_Pincode"));
        user.setUniName(rs.getString("USERS_Name"));
        user.setUniPhone(rs.getString("USERS_Phnumber"));
        user.setUniAttendance(rs.getInt("USERS_Attendance"));
        user.setUniAttendance(rs.getInt("USERS_Attendance"));
        user.setUniEmail(rs.getString("USERS_Email"));
        return user;
    }
}

