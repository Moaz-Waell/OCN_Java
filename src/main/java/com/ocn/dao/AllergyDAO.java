package com.ocn.dao;


import com.ocn.beans.AllergyBean;
import com.ocn.connection.DBConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AllergyDAO {

    public List<AllergyBean> getAllAllergies() {
        List<AllergyBean> allergies = new ArrayList<>();
        String sql = "SELECT * FROM allergy WHERE ALLERGY_Name != 'no allergies' ORDER BY ALLERGY_ID";

        try (Connection connection = DBConnectionUtil.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                AllergyBean allergy = new AllergyBean();
                allergy.setAllergyId(rs.getInt("ALLERGY_ID"));
                allergy.setAllergyName(rs.getString("ALLERGY_Name"));
                allergies.add(allergy);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all allergies: " + e.getMessage());
        }
        return allergies;
    }

    public AllergyBean getAllergyById(int allergyId) {
        AllergyBean allergy = null;
        String sql = "SELECT * FROM allergy WHERE ALLERGY_ID = ?";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, allergyId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    allergy = new AllergyBean();
                    allergy.setAllergyId(rs.getInt("ALLERGY_ID"));
                    allergy.setAllergyName(rs.getString("ALLERGY_Name"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching allergy by ID: " + e.getMessage());
        }
        return allergy;
    }

}

