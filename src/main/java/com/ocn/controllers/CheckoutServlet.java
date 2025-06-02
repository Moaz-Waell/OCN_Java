package com.ocn.controllers;

import com.ocn.beans.*;
import com.ocn.connection.DBConnectionUtil;
import com.ocn.dao.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

@WebServlet("/CheckoutServlet")
public class CheckoutServlet extends HttpServlet {
    private CartDAO cartDAO;
    private MealDAO mealDAO;
    private OrderDAO orderDAO;
    private VoucherDAO voucherDAO;



    public void init() {
        cartDAO = new CartDAO();
        mealDAO = new MealDAO();
        orderDAO = new OrderDAO();
        voucherDAO = new VoucherDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String contextPath = request.getContextPath();

        // Authentication Check
        OcnUserBean currentUser = (OcnUserBean) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.sendRedirect(contextPath + "/user/UniUserPortal.jsp"); // Redirect to login/portal
            return;
        }

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxies

        // Retrieve Cart Items
        List<CartBean> cartItems = cartDAO.getCartItemsByUserId(currentUser.getUserId());


        double subtotal = 0;
        Map<CartBean, MealBean> cartItemsDetails = new LinkedHashMap<>();
        if (cartItems != null && !cartItems.isEmpty()) {
            for (CartBean item : cartItems) {
                MealBean meal = mealDAO.getMealById(item.getMealId());
                if (meal != null) {
                    cartItemsDetails.put(item, meal);
                    subtotal += meal.getMealPrice() * item.getQuantity();
                } else {

                    request.setAttribute("error", "An item in your cart (ID: " + item.getMealId() + ") is no longer available. Please remove it from your cart.");
                }
            }
        } else {
            // If cart is empty, redirect to cart page with a message
            session.setAttribute("message", "Your cart is empty."); // Use session for redirect message
            response.sendRedirect(contextPath + "/CartServlet");
            return;
        }

        // Update subtotal in session (used by POST potentially)
        session.setAttribute("subtotal", subtotal);

        // Retrieve Available Vouchers
        List<UserVoucherBean> vouchers = voucherDAO.getActiveVouchers(currentUser.getUserId());

        // Handle Applied Voucher and Discount
        double discount = 0.0;
        String appliedVoucherKey = (String) session.getAttribute("applied_voucher_key");
        Integer appliedVoucherId = (Integer) session.getAttribute("applied_voucher_id");

        if (appliedVoucherKey != null && appliedVoucherId != null) {
            // Verify if the applied voucher is still valid and available
            UserVoucherBean appliedVoucher = null;
            try {
                appliedVoucher = voucherDAO.getVoucherByIdAndUserId(appliedVoucherId, currentUser.getUserId());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            boolean stillValid = false;
            if (appliedVoucher != null) {
                String currentKey = appliedVoucher.getVoucherId() + "_" + appliedVoucher.getStartDate();
                // Check key match and date validity
                if (appliedVoucherKey.equals(currentKey) &&
                        !LocalDate.now().isBefore(appliedVoucher.getStartDate().toLocalDate()) &&
                        LocalDate.now().isBefore(appliedVoucher.getEndDate().toLocalDate())) {
                    stillValid = true;
                    // Recalculate discount based on current subtotal
                    discount = subtotal * (appliedVoucher.getPercentage() / 100.0);
                } else {
                    System.out.println("Applied voucher date no longer valid or key mismatch.");
                }
            } else {
                System.out.println("Applied voucher not found for user.");
            }

            if (!stillValid) {
                // If voucher is no longer valid, remove it from session
                session.removeAttribute("applied_voucher_key");
                session.removeAttribute("applied_voucher_id");
                session.removeAttribute("applied_discount");
                appliedVoucherKey = null; // Reset local variable
                appliedVoucherId = null;  // Reset local variable
                discount = 0.0;         // Reset discount
                request.setAttribute("error", "The previously applied voucher is no longer valid and has been removed.");
            } else {
                // Update session discount if recalculated
                session.setAttribute("applied_discount", discount);
            }
        } else {
            // Ensure discount is zero if no voucher key/id in session
            discount = 0.0;
            session.setAttribute("applied_discount", discount); // Ensure session reflects 0
        }


        // Set attributes for JSP
        request.setAttribute("cartItemsDetails", cartItemsDetails);
        request.setAttribute("subtotal", subtotal);
        request.setAttribute("discount", discount);
        request.setAttribute("vouchers", vouchers);
        // No need to set currentUser, it's in session, JSP can access if needed

        // Forward to JSP
        request.getRequestDispatcher("/user/checkout.jsp").forward(request, response); // Use the refactored JSP
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String contextPath = request.getContextPath();

        // Authentication Check
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(contextPath + "/aast/UniUserPortal.jsp"); // Redirect to login
            return;
        }
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxies
        OcnUserBean currentUser = (OcnUserBean) session.getAttribute("currentUser");

        // Determine action based on parameters from the single form
        String action = request.getParameter("action"); // Primarily for placeOrder, removeVoucher
        String applyVoucherBtnValue = request.getParameter("applyVoucherBtn"); // Value is voucherKey

        String redirectTarget = contextPath + "/CheckoutServlet"; // Default redirect back to checkout GET
        boolean redirect = true; // Most actions redirect back to GET

        Connection connection = null; // For transaction management in placeOrder

        try {
            if (applyVoucherBtnValue != null && !applyVoucherBtnValue.isEmpty()) {
                // --- Apply Voucher Action --- (Triggered by applyVoucherBtn parameter)
                handleApplyVoucher(request, session, currentUser, applyVoucherBtnValue);

            } else if ("removeVoucher".equals(action)) {
                // --- Remove Voucher Action ---
                handleRemoveVoucher(request, session);

            } else if ("placeOrder".equals(action)) {
                // --- Place Order Action ---
                connection = DBConnectionUtil.getConnection();
                connection.setAutoCommit(false); // Start transaction

                boolean orderPlaced = handlePlaceOrder(request, session, currentUser, connection);

                if (orderPlaced) {
                    connection.commit(); // Commit transaction
                    // Clear session attributes AFTER successful commit
                    session.removeAttribute("subtotal");
                    session.removeAttribute("applied_discount");
                    session.removeAttribute("applied_voucher_id");
                    session.removeAttribute("applied_voucher_key");
                    session.setAttribute("message", "Order placed successfully!"); // Message for next page
                    redirectTarget = contextPath + "/OrderServlet"; // Redirect to order history/confirmation
                } else {
                    // Error occurred, rollback (should happen within handlePlaceOrder)
                    // Error message should be set in request attribute by handlePlaceOrder
                    // Just ensure rollback happened if connection is not null
                    if (connection != null && !connection.getAutoCommit()) { // Check if still in transaction
                        try {
                            connection.rollback();
                        } catch (SQLException ex) {
                            System.err.println("Rollback failed: " + ex.getMessage());
                        }
                    }
                }
            } else {
                // Unknown action
                request.setAttribute("error", "Invalid checkout action specified.");
            }

        } catch (SQLException dbEx) {
            // Handle unexpected DB errors during connection or transaction management
            request.setAttribute("error", "A database error occurred: " + dbEx.getMessage());
            System.err.println("Checkout Servlet DB Error: " + dbEx.getMessage());
            dbEx.printStackTrace();
            // Ensure rollback if transaction was started
            if (connection != null) {
                try {
                    if (!connection.getAutoCommit()) connection.rollback();
                } catch (SQLException ex) {
                    System.err.println("Rollback failed: " + ex.getMessage());
                }
            }
        } catch (Exception e) {
            // Handle other unexpected errors
            request.setAttribute("error", "An unexpected error occurred: " + e.getMessage());
            System.err.println("Checkout Servlet Error: " + e.getMessage());
            e.printStackTrace();
            // Ensure rollback if transaction was started
            if (connection != null) {
                try {
                    if (!connection.getAutoCommit()) connection.rollback();
                } catch (SQLException ex) {
                    System.err.println("Rollback failed: " + ex.getMessage());
                }
            }
        } finally {
            // Close connection if it was opened (primarily for placeOrder)
            if (connection != null) {
                try {
                    if (!connection.getAutoCommit()) connection.setAutoCommit(true); // Reset auto-commit before closing
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }

        // Redirect or Forward
        if (redirect) {
            response.sendRedirect(redirectTarget);
        } else {
            // If any action needed to forward instead of redirect (unlikely here)
            // request.getRequestDispatcher(forwardTarget).forward(request, response);
        }
    }

    // --- Helper Methods for Actions --- //

    private void handleApplyVoucher(HttpServletRequest request, HttpSession session, OcnUserBean currentUser, String voucherKey) {
        try {
            // Extract voucherId and startDate from the key (e.g., "123_2025-05-20")
            String[] parts = voucherKey.split("_");
            if (parts.length != 2) {
                request.setAttribute("error", "Invalid voucher format.");
                return;
            }
            int voucherId = Integer.parseInt(parts[0]);
            Date startDate = Date.valueOf(parts[1]); // Assumes YYYY-MM-DD format

            UserVoucherBean voucher = voucherDAO.getVoucherByIdUserIdAndStartDate(
                    voucherId, currentUser.getUserId(), startDate);

            // Validate voucher existence and date range
            if (voucher != null &&
                    !LocalDate.now().isBefore(voucher.getStartDate().toLocalDate()) &&
                    LocalDate.now().isBefore(voucher.getEndDate().toLocalDate())) {
                // Calculate discount based on current subtotal from session
                Double subtotalObj = (Double) session.getAttribute("subtotal");
                double subtotal = (subtotalObj != null) ? subtotalObj : 0.0; // Recalculate if needed?
                double discount = subtotal * (voucher.getPercentage() / 100.0);

                // Store details in session
                session.setAttribute("applied_voucher_key", voucherKey);
                session.setAttribute("applied_discount", discount);
                session.setAttribute("applied_voucher_id", voucherId);
                request.setAttribute("message", "Voucher applied successfully!"); // Use request for immediate feedback

            } else {
                // Invalid voucher, remove any existing applied voucher info
                session.removeAttribute("applied_voucher_key");
                session.removeAttribute("applied_discount");
                session.removeAttribute("applied_voucher_id");
                request.setAttribute("error", "Selected voucher is invalid or expired.");
            }
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            // Handle errors parsing the voucher key
            session.removeAttribute("applied_voucher_key");
            session.removeAttribute("applied_discount");
            session.removeAttribute("applied_voucher_id");
            request.setAttribute("error", "Error applying voucher: Invalid format.");
        } catch (Exception e) {
            // General error
            session.removeAttribute("applied_voucher_key");
            session.removeAttribute("applied_discount");
            session.removeAttribute("applied_voucher_id");
            request.setAttribute("error", "Error applying voucher: " + e.getMessage());
            System.err.println("Apply Voucher Error: " + e.getMessage());
        }
    }

    private void handleRemoveVoucher(HttpServletRequest request, HttpSession session) {
        session.removeAttribute("applied_voucher_key");
        session.removeAttribute("applied_discount");
        session.removeAttribute("applied_voucher_id");
        request.setAttribute("message", "Applied voucher removed."); // Use request for immediate feedback
    }

    private boolean handlePlaceOrder(HttpServletRequest request, HttpSession session, OcnUserBean currentUser, Connection connection) throws SQLException {
        // Re-verify cart is not empty (within transaction)
        List<CartBean> cartItems = cartDAO.getCartItemsByUserId(currentUser.getUserId()); // Pass connection if DAO refactored
        if (cartItems == null || cartItems.isEmpty()) {
            request.setAttribute("error", "Your cart is empty. Cannot place order.");
            connection.rollback();
            return false;
        }

        // Recalculate subtotal and validate meals (within transaction)
        double subtotal = 0;
        List<OrderDetailBean> orderDetails = new ArrayList<>();
        for (CartBean item : cartItems) {
            MealBean meal = mealDAO.getMealById(item.getMealId()); // Pass connection if DAO refactored
            if (meal != null) {
                subtotal += meal.getMealPrice() * item.getQuantity();
                OrderDetailBean detail = new OrderDetailBean();
                detail.setMealId(item.getMealId());
                detail.setQuantity(item.getQuantity());
                detail.setNote(item.getNote());
                orderDetails.add(detail);
            } else {
                request.setAttribute("error", "Item ID " + item.getMealId() + " in your cart is invalid.");
                connection.rollback();
                return false;
            }
        }

        // Re-validate applied voucher and calculate final discount (within transaction)
        double discount = 0.0;
        Integer appliedVoucherId = (Integer) session.getAttribute("applied_voucher_id");
        UserVoucherBean appliedVoucher = null; // Keep track of the voucher object if applied

        if (appliedVoucherId != null) {
            appliedVoucher = voucherDAO.getVoucherByIdAndUserId(appliedVoucherId, currentUser.getUserId()); // Pass connection if DAO refactored
            if (appliedVoucher != null &&
                    !LocalDate.now().isBefore(appliedVoucher.getStartDate().toLocalDate()) &&
                    LocalDate.now().isBefore(appliedVoucher.getEndDate().toLocalDate())) {
                discount = subtotal * (appliedVoucher.getPercentage() / 100.0);
            } else {
                // Voucher became invalid - stop order placement
                request.setAttribute("error", "Applied voucher is no longer valid. Please review your order.");
                session.removeAttribute("applied_voucher_key");
                session.removeAttribute("applied_voucher_id");
                session.removeAttribute("applied_discount");
                connection.rollback();
                return false;
            }
        }
        double total = subtotal - discount;

        // Get and Validate Delivery Details
        String scheduleDateStr = request.getParameter("scheduleDate");
        String scheduleTimeStr = request.getParameter("scheduleTime");
        boolean deliverNow = "true".equals(request.getParameter("deliverNow"));
        String orderScheduleDate, orderScheduleTime;


        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        if (deliverNow) {
            LocalDateTime now = LocalDateTime.now();
            orderScheduleDate = now.format(dateFormatter);  // Format date
            orderScheduleTime = now.format(timeFormatter);  // Format time
        } else {
            try {
                // Validate AND format date
                LocalDate.parse(scheduleDateStr, dateFormatter);
                orderScheduleDate = scheduleDateStr;  // Assign validated date

                // Parse and format time
                LocalTime time = LocalTime.parse(scheduleTimeStr);
                orderScheduleTime = time.format(timeFormatter);
            } catch (DateTimeParseException e) {
                request.setAttribute("error", "Invalid date/time format");
                connection.rollback();
                return false;
            }
        }

//        if (deliverNow) {
//            LocalDateTime now = LocalDateTime.now();
//            orderScheduleDate = now.format(DateTimeFormatter.ISO_LOCAL_DATE);
//            orderScheduleTime = now.format(DateTimeFormatter.ofPattern("HH:mm")); // Use HH:mm format
//        } else {
//            if (scheduleDateStr == null || scheduleDateStr.isEmpty() || scheduleTimeStr == null || scheduleTimeStr.isEmpty()) {
//                request.setAttribute("error", "Please select a delivery date and time, or choose 'Deliver Now'.");
//                connection.rollback();
//                return false;
//            }
//            // Basic validation for date/time format (more robust validation could be added)
//            try {
//                LocalDate.parse(scheduleDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
//                // LocalTime.parse(scheduleTimeStr); // No standard ISO format, HH:mm is common
//                orderScheduleDate = scheduleDateStr;
//                orderScheduleTime = scheduleTimeStr;
//            } catch (DateTimeParseException e) {
//                request.setAttribute("error", "Invalid schedule date or time format.");
//                connection.rollback();
//                return false;
//            }
//        }

        // Get and Validate Payment Details
        String paymentMethod = request.getParameter("paymentMethod");
        if (paymentMethod == null || (!paymentMethod.equals("Cash") && !paymentMethod.equals("Card"))) {
            request.setAttribute("error", "Please select a valid payment method.");
            connection.rollback();
            return false;
        }
        if (paymentMethod.equals("Card")) {
            // Basic validation for card fields (presence check)
            if (request.getParameter("cardholder_name") == null || request.getParameter("cardholder_name").isEmpty() ||
                    request.getParameter("card_number") == null || request.getParameter("card_number").isEmpty() ||
                    request.getParameter("expiry_date") == null || request.getParameter("expiry_date").isEmpty() ||
                    request.getParameter("cvv") == null || request.getParameter("cvv").isEmpty()) {
                request.setAttribute("error", "Please fill in all required credit card details.");
                connection.rollback();
                return false;
            }
            // Add more specific validation (regex patterns) if needed here or on frontend
        }

        // Create Order Bean
        OrderBean order = new OrderBean();
        order.setUserId(currentUser.getUserId());
        order.setOrderAmount(total);
        order.setOrderPaymentType(paymentMethod);
        order.setOrderStatus("Pending"); // Initial status
        order.setOrderScheduleDate(orderScheduleDate);
        order.setOrderScheduleTime(orderScheduleTime);
        // order.setOrderFeedback(null); // Default feedback is null

        // --- Database Operations within Transaction --- //

        // 1. Save Order and Details
        // Assuming OrderDAO.createOrder handles both order and details insertion
        // AND crucially, accepts the connection to participate in the transaction.
        // If OrderDAO manages its own transaction, this whole structure needs rethinking.
        // boolean orderSaved = orderDAO.createOrder(connection, order, orderDetails); // Ideal DAO signature
        boolean orderSaved = orderDAO.createOrder(order, orderDetails); // Using provided DAO signature
        if (!orderSaved) {
            request.setAttribute("error", "Failed to save the order details.");
            connection.rollback(); // Rollback if order saving fails
            return false;
        }

        // 2. Clear Cart
        // boolean cartCleared = cartDAO.clearCart(connection, currentUser.getUserId()); // Ideal DAO signature
        boolean cartCleared = cartDAO.clearCart(currentUser.getUserId()); // Using provided DAO signature
        if (!cartCleared) {
            request.setAttribute("error", "Order saved, but failed to clear the cart. Please contact support.");
            // This is tricky - order is saved but cart isn't cleared. Commit or Rollback?
            // Rolling back might be safer to avoid inconsistent state, but loses the order.
            // Committing leaves the cart dirty.
            // Let's rollback for consistency for now.
            connection.rollback();
            return false;
        }

        // 3. Remove Used Voucher (if applicable)
        if (appliedVoucher != null) {
            // boolean voucherRemoved = voucherDAO.removeUsedVoucher(connection, currentUser.getUserId(), appliedVoucher.getVoucherId()); // Ideal
            boolean voucherRemoved = voucherDAO.removeUsedVoucher(currentUser.getUserId(), appliedVoucher.getVoucherId()); // Provided
            if (!voucherRemoved) {
                request.setAttribute("error", "Order saved and cart cleared, but failed to mark voucher as used. Please contact support.");
                // Similar dilemma to cart clearing. Rollback for consistency.
                connection.rollback();
                return false;
            }
        }

        // Collect meal quantities for email
        Map<MealBean, Integer> mealQuantities = new LinkedHashMap<>();
        for (CartBean item : cartItems) {
            MealBean meal = mealDAO.getMealById(item.getMealId());
            if (meal != null) {
                mealQuantities.put(meal, item.getQuantity());
            }
        }

        // Send confirmation email
        sendOrderConfirmationEmail(
                currentUser.getUserEmail(),
                order.getOrderId(),
                mealQuantities,
                subtotal,
                discount,
                total,
                order.getOrderScheduleDate() + " " + order.getOrderScheduleTime(),
                order.getOrderPaymentType(),
                appliedVoucher
        );

        // If all steps succeeded up to here
        return true; // Signal success to the main try block for commit
    }


    private void sendOrderConfirmationEmail(String userEmail, int orderId,
                                            Map<MealBean, Integer> mealQuantities,
                                            double subtotal, double discount,
                                            double total, String deliverySchedule,
                                            String paymentMethod,
                                            UserVoucherBean voucher) {
        final String username = "oncloudnine2025@gmail.com"; // Your Gmail
        final String password = "olym uiiw haxr scfk"; // App password from step 2

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(userEmail));
            message.setSubject("Order Confirmation #" + orderId);

            // Build email content
            StringBuilder content = new StringBuilder();
            content.append("Thank you for your order!\n\n");
            content.append("Order ID: ").append(orderId).append("\n");
            content.append("Delivery Time: ").append(deliverySchedule).append("\n");
            content.append("Payment Method: ").append(paymentMethod).append("\n\n");
            content.append("Order Details:\n");

            for (Map.Entry<MealBean, Integer> entry : mealQuantities.entrySet()) {
                MealBean meal = entry.getKey();
                int quantity = entry.getValue();
                content.append("- ")
                        .append(meal.getMealName())
                        .append(" x").append(quantity)
                        .append(" ($").append(meal.getMealPrice() * quantity)
                        .append(")\n");
            }

            content.append("\nSubtotal: $").append(String.format("%.2f", subtotal)).append("\n");
            if (voucher != null) {
                content.append("Discount (").append(voucher.getPercentage())
                        .append("%): $").append(String.format("%.2f", discount)).append("\n");
            }
            content.append("Total: $").append(String.format("%.2f", total)).append("\n");

            message.setText(content.toString());
            Transport.send(message);
        } catch (MessagingException e) {
            System.err.println("Email sending failed: " + e.getMessage());
        }
    }


}

