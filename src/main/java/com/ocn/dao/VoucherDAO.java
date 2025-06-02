package com.ocn.dao;

import com.ocn.beans.UserVoucherBean;
import com.ocn.connection.DBConnectionUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VoucherDAO {

    // Add this method to your existing VoucherDAO class
    public boolean createUserVoucher(Connection connection, UserVoucherBean voucher) throws SQLException {
        String sql = "INSERT INTO USER_VOUCHERS (USERS_ID, VOUCHER_ID, VOUCHER_StartDate, VOUCHER_EndDate) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, voucher.getUserId());
            ps.setInt(2, voucher.getVoucherId());
            ps.setDate(3, voucher.getStartDate());
            ps.setDate(4, voucher.getEndDate());
            return ps.executeUpdate() > 0;
        }
    }

    // Get all vouchers for a user
    public List<UserVoucherBean> getVouchersByUserId(int userId) {
        List<UserVoucherBean> vouchers = new ArrayList<>();
        String sql = "SELECT * FROM USER_VOUCHERS WHERE USERS_ID = ?";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    vouchers.add(mapResultSetToVoucherBean(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user vouchers: " + e.getMessage());
        }
        return vouchers;
    }

    // Delete expired vouchers
    public int deleteExpiredVouchers() {
        String sql = "DELETE FROM USER_VOUCHERS WHERE VOUCHER_EndDate < CURDATE()";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting expired vouchers: " + e.getMessage());
            return 0;
        }
    }
    // Additional methods for admin dashboard
    public int getVoucherCountByType(int voucherType) {
        String sql = "SELECT COUNT(*) AS count FROM USER_VOUCHERS WHERE VOUCHER_ID = ?";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, voucherType);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting vouchers by type: " + e.getMessage());
        }
        return 0;
    }


    public UserVoucherBean getVoucherByIdAndUserId(int voucherId, int userId) throws SQLException {
        String sql = "SELECT uv.USERS_ID, uv.VOUCHER_ID, uv.VOUCHER_StartDate, uv.VOUCHER_EndDate, v.VOUCHER_Percentage " +
                "FROM USER_VOUCHERS uv " +
                "JOIN VOUCHER v ON uv.VOUCHER_ID = v.VOUCHER_ID " +
                "WHERE uv.VOUCHER_ID = ? AND uv.USERS_ID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, voucherId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapResultSetToVoucherBean(rs) : null;
            }
        }
    }


    public List<UserVoucherBean> getActiveVouchers(int userId) {
        List<UserVoucherBean> activeVouchers = new ArrayList<>();
        String sql = "SELECT uv.USERS_ID, uv.VOUCHER_ID, uv.VOUCHER_StartDate, uv.VOUCHER_EndDate, v.VOUCHER_Percentage " +
                "FROM USER_VOUCHERS uv " +
                "JOIN VOUCHER v ON uv.VOUCHER_ID = v.VOUCHER_ID " +
                "WHERE uv.USERS_ID = ? " +
                "AND CURDATE() BETWEEN uv.VOUCHER_StartDate AND uv.VOUCHER_EndDate";


        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    activeVouchers.add(mapResultSetToVoucherBean(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching active vouchers: " + e.getMessage());
        }
        return activeVouchers;
    }

    public boolean removeUsedVoucher(int userId, int voucherId) {
        String sql = "DELETE FROM USER_VOUCHERS WHERE USERS_ID = ? AND VOUCHER_ID = ?";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, voucherId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error removing used voucher: " + e.getMessage());
            return false;
        }
    }

    public UserVoucherBean getVoucherByIdUserIdAndStartDate(int voucherId, int userId, Date startDate) throws SQLException {
        String sql = "SELECT uv.USERS_ID, uv.VOUCHER_ID, uv.VOUCHER_StartDate, uv.VOUCHER_EndDate, v.VOUCHER_Percentage " +
                "FROM USER_VOUCHERS uv " +
                "JOIN VOUCHER v ON uv.VOUCHER_ID = v.VOUCHER_ID " +
                "WHERE uv.VOUCHER_ID = ? AND uv.USERS_ID = ? AND uv.VOUCHER_StartDate = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, voucherId);
            ps.setInt(2, userId);
            ps.setDate(3, startDate);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapResultSetToVoucherBean(rs) : null;
            }
        }
    }

    private UserVoucherBean mapResultSetToVoucherBean(ResultSet rs) throws SQLException {
        UserVoucherBean voucher = new UserVoucherBean();
        voucher.setUserId(rs.getInt("USERS_ID"));
        voucher.setVoucherId(rs.getInt("VOUCHER_ID"));
        voucher.setStartDate(rs.getDate("VOUCHER_StartDate"));
        voucher.setEndDate(rs.getDate("VOUCHER_EndDate"));
        voucher.setPercentage(rs.getDouble("VOUCHER_Percentage"));
        return voucher;
    }

}