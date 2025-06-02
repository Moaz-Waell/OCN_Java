package com.ocn.dao;

import com.ocn.beans.MealBean;
import com.ocn.beans.OrderBean;
import com.ocn.beans.OrderDetailBean;
import com.ocn.connection.DBConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    public boolean createOrder(OrderBean order, List<OrderDetailBean> orderDetails) {
        Connection connection = null;
        String sqlInsertOrder = "INSERT INTO orders (ORDER_Status, ORDER_ScheduleDate, ORDER_ScheduleTime, ORDER_Amount, ORDER_PaymentType, USERS_ID, ORDER_Feedback) VALUES (?, ?, ?, ?, ?, ?, ?)";
        // Fixed column name from QUANTITY to M_Quantity to match database schema
        String sqlInsertOrderDetail = "INSERT INTO order_details (ORDER_ID, MEAL_ID, M_Quantity, NOTE) VALUES (?, ?, ?, ?)";

        try {
            connection = DBConnectionUtil.getConnection();
            connection.setAutoCommit(false);

            // Insert into orders table
            try (PreparedStatement psOrder = connection.prepareStatement(sqlInsertOrder, Statement.RETURN_GENERATED_KEYS)) {
                psOrder.setString(1, order.getOrderStatus());
                psOrder.setString(2, order.getOrderScheduleDate());
                psOrder.setString(3, order.getOrderScheduleTime());
                psOrder.setDouble(4, order.getOrderAmount());
                psOrder.setString(5, order.getOrderPaymentType());
                psOrder.setInt(6, order.getUserId());
                if (order.getOrderFeedback() != null) {
                    psOrder.setInt(7, order.getOrderFeedback());
                } else {
                    psOrder.setNull(7, java.sql.Types.INTEGER);
                }


                int affectedRows = psOrder.executeUpdate();
                if (affectedRows == 0) {
                    connection.rollback();
                    return false;
                }

                try (ResultSet generatedKeys = psOrder.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        order.setOrderId(generatedKeys.getInt(1));
                    } else {
                        connection.rollback();
                        return false;
                    }
                }
            }

            // Insert into order_details table
            try (PreparedStatement psOrderDetail = connection.prepareStatement(sqlInsertOrderDetail)) {
                for (OrderDetailBean detail : orderDetails) {
                    psOrderDetail.setInt(1, order.getOrderId());
                    psOrderDetail.setInt(2, detail.getMealId());
                    psOrderDetail.setInt(3, detail.getQuantity());
                    psOrderDetail.setString(4, detail.getNote());
                    psOrderDetail.addBatch();
                }
                psOrderDetail.executeBatch();
            }

            connection.commit(); // Commit transaction
            return true;

        } catch (SQLException e) {
            System.err.println("Error creating order: " + e.getMessage());
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


    public List<OrderBean> getOrdersByUserId(int userId) {
        List<OrderBean> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE USERS_ID = ? ORDER BY ORDER_ID DESC"; // Or by date

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrderBean(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching orders by user ID: " + e.getMessage());
        }
        return orders;
    }

    // Update order feedback by user
    public boolean updateOrderFeedback(int orderId, int feedback) {
        String sql = "UPDATE orders SET ORDER_Feedback = ? WHERE ORDER_ID = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, feedback);
            preparedStatement.setInt(2, orderId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating order feedback: " + e.getMessage());
            return false;
        }
    }

    public List<OrderBean> getOrdersByStatus_ocn_user(String status) {
        List<OrderBean> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE ORDER_Status = ?";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, status);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrderBean(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching orders by status: " + e.getMessage());
        }
        return orders;
    }

    public List<OrderDetailBean> getOrderDetailsByOrderId(int orderId) {
        List<OrderDetailBean> details = new ArrayList<>();
        String sql = "SELECT od.*, m.MEAL_Name, m.MEAL_Icon, m.MEAL_Price, c.CATEGORY_Name " +
                "FROM ORDER_DETAILS od " +
                "JOIN MEAL m ON od.MEAL_ID = m.MEAL_ID " +
                "JOIN CATEGORY c ON m.CATEGORY_ID = c.CATEGORY_ID " +
                "WHERE od.ORDER_ID = ?";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, orderId);
            System.out.println("[DEBUG] Fetching details for orderId: " + orderId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                System.out.println("[DEBUG] SQL Query Executed: " + sql);

                while (rs.next()) {
                    System.out.println("[DEBUG] Found item: " + rs.getInt("MEAL_ID"));
                    OrderDetailBean detail = new OrderDetailBean();
                    detail.setOrderId(rs.getInt("ORDER_ID"));
                    detail.setMealId(rs.getInt("MEAL_ID"));
                    detail.setQuantity(rs.getInt("M_Quantity"));
                    detail.setNote(rs.getString("NOTE"));

                    MealBean meal = new MealBean();
                    meal.setMealId(rs.getInt("MEAL_ID"));
                    meal.setMealName(rs.getString("MEAL_Name"));
                    meal.setMealIcon(rs.getString("MEAL_Icon"));
                    meal.setMealPrice(rs.getDouble("MEAL_Price"));
                    meal.setCategoryName(rs.getString("CATEGORY_Name"));
                    detail.setMeal(meal);

                    details.add(detail);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching order details by order ID: " + e.getMessage());
        }
        return details;
    }

    //get the orders by status to load in the required table for the admin & the user
    public List<OrderBean> getActiveOrders() {
        List<OrderBean> orders = new ArrayList<>();
        String sql = "SELECT o.*, u.USERS_Name " +
                "FROM ORDERS o " +
                "JOIN USERS u ON o.USERS_ID = u.USERS_ID " +
                "WHERE o.ORDER_Status NOT IN ('Delivered', 'Cancelled') " +
                "ORDER BY o.ORDER_ScheduleDate DESC";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) {
                orders.add(mapResultSetToOrderBean(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching active orders: " + e.getMessage());
        }

        return orders;
    }


    public List<OrderBean> getOrdersByStatus(String status) {
        List<OrderBean> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE ORDER_Status = ?";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, status);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrderBean(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching orders by status: " + e.getMessage());
        }
        return orders;
    }

    public List<OrderBean> getOrderHistory() {
        List<OrderBean> orders = new ArrayList<>();
        String sql = "SELECT o.*, u.USERS_Name " +
                "FROM ORDERS o " +
                "JOIN USERS u ON o.USERS_ID = u.USERS_ID " +
                "WHERE o.ORDER_Status IN ('Delivered', 'Cancelled') " +
                "ORDER BY o.ORDER_ScheduleDate DESC";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) {
                orders.add(mapResultSetToOrderBean(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching order history: " + e.getMessage());
        }

        return orders;
    }

    //for the admin
    // analysis section
    // get the total number of delivered orders
    public int getDeliveredOrderCount() {
        int count = 0;
        String sql = "SELECT COUNT(*) AS order_count FROM orders WHERE ORDER_Status = 'Delivered'";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt("order_count");
            }
        } catch (SQLException e) {
            System.err.println("Error counting delivered orders: " + e.getMessage());
        }
        return count;
    }

    // get the total revenue for the admin
    public double getTotalRevenue() {
        double total = 0.0;
        String sql = "SELECT SUM(ORDER_Amount) AS total FROM orders WHERE ORDER_Status = ?";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, "Delivered");
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    total = rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error calculating total revenue: " + e.getMessage());
        }
        return total;
    }


    //table section
    // Update order status from pending to preparing to out of delivery
    public boolean updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE orders SET ORDER_Status = ? WHERE ORDER_ID = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, status);
            preparedStatement.setInt(2, orderId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating order status: " + e.getMessage());
            return false;
        }
    }


    //for the kitchen
    // get the current orders
    public List<OrderBean> getTodaysOrders() {
        List<OrderBean> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE ORDER_ScheduleDate = CURDATE()";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) {
                orders.add(mapResultSetToOrderBean(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching today's orders: " + e.getMessage());
        }
        return orders;
    }


    // Get a specific order by its ID
    public OrderBean getOrderById(int orderId) {
        OrderBean order = null;
        String sql = "SELECT * FROM ORDERS WHERE ORDER_ID = ?";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, orderId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    order = mapResultSetToOrderBean(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching order by ID: " + e.getMessage());
        }
        return order;
    }


    private OrderBean mapResultSetToOrderBean(ResultSet rs) throws SQLException {
        OrderBean order = new OrderBean();
        order.setOrderId(rs.getInt("ORDER_ID"));
        order.setOrderStatus(rs.getString("ORDER_Status"));
        order.setOrderScheduleDate(rs.getString("ORDER_ScheduleDate"));
        order.setOrderScheduleTime(rs.getString("ORDER_ScheduleTime"));
        order.setOrderAmount(rs.getDouble("ORDER_Amount"));
        order.setOrderPaymentType(rs.getString("ORDER_PaymentType"));
        order.setUserId(rs.getInt("USERS_ID"));
        order.setOrderFeedback(rs.getObject("ORDER_Feedback") != null ? rs.getInt("ORDER_Feedback") : null);
        return order;
    }


    // Get today's delivered order count
    public int getTodaysDeliveredOrderCount() {
        int count = 0;
        String sql = "SELECT COUNT(*) AS order_count FROM orders WHERE ORDER_Status = 'Delivered' AND ORDER_ScheduleDate = CURDATE()";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt("order_count");
            }
        } catch (SQLException e) {
            System.err.println("Error counting today's delivered orders: " + e.getMessage());
        }
        return count;
    }

    // Get today's total revenue from delivered orders
    public double getTodaysTotalRevenue() {
        double total = 0.0;
        String sql = "SELECT SUM(ORDER_Amount) AS total FROM orders WHERE ORDER_Status = 'Delivered' AND ORDER_ScheduleDate = CURDATE()";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {

            if (rs.next()) {
                total = rs.getDouble("total"); // getDouble handles NULL by returning 0.0
            }
        } catch (SQLException e) {
            System.err.println("Error calculating today's total revenue: " + e.getMessage());
        }
        return total;
    }

    public List<OrderBean> getKitchenOrders() {
        List<OrderBean> orders = new ArrayList<>();
        String sql = "SELECT o.ORDER_ID, o.ORDER_Status, u.USERS_Name AS customer_name, "
                + "o.ORDER_ScheduleDate, o.ORDER_ScheduleTime, m.MEAL_Name, m.MEAL_Description, "
                + "od.M_Quantity, od.NOTE "
                + "FROM ORDERS o "
                + "JOIN USERS u ON o.USERS_ID = u.USERS_ID "
                + "JOIN ORDER_DETAILS od ON o.ORDER_ID = od.ORDER_ID "
                + "JOIN MEAL m ON od.MEAL_ID = m.MEAL_ID "
                + "WHERE o.ORDER_Status IN ('Pending', 'Preparing') " // Status filter
                + "AND o.ORDER_ScheduleDate = CURDATE() " // Today's orders
                + "ORDER BY "
                + "STR_TO_DATE(CONCAT(o.ORDER_ScheduleDate, ' ', o.ORDER_ScheduleTime), '%Y-%m-%d %h:%i %p') ASC"; // Corrected sort by datetime
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                OrderBean order = new OrderBean();
                order.setOrderId(rs.getInt("ORDER_ID"));
                order.setOrderStatus(rs.getString("ORDER_Status"));
                order.setCustomerName(rs.getString("customer_name"));
                order.setOrderScheduleDate(rs.getString("ORDER_ScheduleDate"));
                order.setOrderScheduleTime(rs.getString("ORDER_ScheduleTime"));

                MealBean meal = new MealBean();
                meal.setMealName(rs.getString("MEAL_Name"));
                meal.setMealDescription(rs.getString("MEAL_Description"));

                OrderDetailBean detail = new OrderDetailBean();
                detail.setMeal(meal);
                detail.setQuantity(rs.getInt("M_Quantity"));
                detail.setNote(rs.getString("NOTE"));

                order.setMealDetails(detail);
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching kitchen orders: " + e.getMessage());
            e.printStackTrace();
        }
        return orders;
    }

}

