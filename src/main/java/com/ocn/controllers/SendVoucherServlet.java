package com.ocn.controllers;

import com.ocn.beans.OcnUserBean;
import com.ocn.beans.UserVoucherBean;
import com.ocn.dao.VoucherDAO;
import com.ocn.connection.DBConnectionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/SendVoucherServlet")
public class SendVoucherServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String contextPath = request.getContextPath();

        // Check admin session
        if (session == null || session.getAttribute("currentAdmin") == null) {
            response.sendRedirect(contextPath + "/admin/adminLogin.jsp");
            return;
        }

        Connection connection = null;
        try {
            connection = DBConnectionUtil.getConnection();
            connection.setAutoCommit(false); // Start transaction

            // 1. Get eligible users
            List<OcnUserBean> eligibleUsers = getEligibleUsers(connection);

            // 2. Create vouchers
            int createdVouchers = createVouchers(connection, eligibleUsers);

            connection.commit();
            session.setAttribute("success", createdVouchers + " vouchers distributed successfully");
        } catch (SQLException e) {
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException ex) {}
            session.setAttribute("error", "Error sending vouchers: " + e.getMessage());
        } finally {
            DBConnectionUtil.closeConnection(connection);
        }

        response.sendRedirect(contextPath + "/AdminDashboardServlet");
    }

    private List<OcnUserBean> getEligibleUsers(Connection connection) throws SQLException {
        List<OcnUserBean> users = new ArrayList<>();
        String sql = "SELECT USERS_ID, USERS_Attendance FROM USERS WHERE USERS_Attendance > 0";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                OcnUserBean user = new OcnUserBean();
                user.setUserId(rs.getInt("USERS_ID"));
                user.setUserAttendance(rs.getInt("USERS_Attendance"));
                users.add(user);
            }
        }
        return users;
    }

    private int createVouchers(Connection connection, List<OcnUserBean> users) throws SQLException {
        int count = 0;
        VoucherDAO voucherDAO = new VoucherDAO();
        LocalDate today = LocalDate.now();

        for (OcnUserBean user : users) {
            Integer voucherType = determineVoucherType(user.getUserAttendance());
            if (voucherType != null) {
                UserVoucherBean voucher = new UserVoucherBean();
                voucher.setUserId(user.getUserId());
                voucher.setVoucherId(voucherType);
                voucher.setStartDate(Date.valueOf(today));
                voucher.setEndDate(Date.valueOf(today.plusDays(5)));

                if (voucherDAO.createUserVoucher(connection, voucher)) {
                    count++;
                }
            }
        }
        return count;
    }

    private Integer determineVoucherType(int attendance) {
        if (attendance >= 90) return 1; // 30% off
        if (attendance >= 80) return 2; // 15% off
        if (attendance >= 70) return 3; // 5% off
        return null;
    }
}