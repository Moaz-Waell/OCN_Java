<%@ page import="com.ocn.beans.UserVoucherBean" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.LocalDate" %>
<%
    // Redirect if user is not logged in
    if (session.getAttribute("currentUser") == null) {
        response.sendRedirect(request.getContextPath() + "/user/UniUserPortal.jsp"); // Assuming this is the login/portal page
        return;
    }

    // Retrieve data passed from servlet
    Double subtotal = (Double) request.getAttribute("subtotal");
    Double discount = (Double) request.getAttribute("discount");
    List<UserVoucherBean> vouchers = (List<UserVoucherBean>) request.getAttribute("vouchers");
    String errorMessage = (String) request.getAttribute("error"); // Use request attribute for errors from servlet
    String successMessage = (String) request.getAttribute("message"); // Use request attribute for success messages
    String appliedVoucherKey = (String) session.getAttribute("applied_voucher_key"); // Get key of applied voucher from session

    // Default values if attributes are null
    if (subtotal == null) subtotal = 0.0;
    if (discount == null) discount = 0.0;
    double totalPrice = subtotal - discount;

    // Get today's date for min attribute in date input
    String todayDate = LocalDate.now().toString();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Checkout</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pages/user/checkout.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<div class="container">
    <jsp:include page="/components/sideNav.jsp"/>
    <main class="main-content">
        <div class="checkout container">
            <!-- Display Error/Success Messages -->
            <% if (errorMessage != null) { %>
            <div class="error-message"><%= errorMessage %></div>
            <% } %>
            <% if (successMessage != null) { %>
            <div class="success-message"><%= successMessage %></div>
            <% } %>

            <!-- *** Single Checkout Form *** -->
            <form id="checkoutForm" action="${pageContext.request.contextPath}/CheckoutServlet" method="post">

                <div class="left_side">
                    <h2 class="heading-primary">Checkout</h2>

                    <!-- Delivery Options -->
                    <label class="checkbox-container margin-top-2rem">
                        <input type="checkbox" name="deliverNow" id="deliverNow" value="true">
                        <span class="checkmark"></span>
                        Deliver Now
                    </label>
                    <div class="heading-secondary">
                        <label for="scheduleDate">Schedule Delivery Date</label>
                        <input type="date" name="scheduleDate" id="scheduleDate"
                               min="<%= todayDate %>" required>
                    </div>
                    <div class="heading-secondary">
                        <label for="scheduleTime">Schedule Delivery Time</label>
                        <input type="time" name="scheduleTime" id="scheduleTime" required>
                    </div>

                    <!-- Vouchers Section - Refactored -->
                    <div class="coupon_section">
                        <h3 class="heading-secondary">Available Vouchers</h3>
                        <% if (vouchers == null || vouchers.isEmpty()) { %>
                        <div class="no-vouchers">
                            <p>No vouchers available at the moment.</p>
                        </div>
                        <% } else { %>
                        <% for (UserVoucherBean voucher : vouchers) { %>
                        <% String currentVoucherKey = voucher.getVoucherId() + "_" + voucher.getStartDate(); %>
                        <% boolean isApplied = currentVoucherKey.equals(appliedVoucherKey); %>
                        <div class="coupon <%= isApplied ? "applied" : "" %>">
                            <div class="coupon-details">
                                <span class="badge"><%= voucher.getPercentage() %>% Off</span>
                                <span class="end-date">Valid until <%= voucher.getEndDate() %></span>
                            </div>
                            <!-- Apply Button submits the main form with specific parameters -->
                            <button type="submit" name="applyVoucherBtn" value="<%= currentVoucherKey %>" class="apply-btn">
                                <%= isApplied ? "Applied" : "Apply" %>
                            </button>
                        </div>
                        <% } %>
                        <!-- Optional: Add a button/link to remove applied voucher -->
                        <% if (appliedVoucherKey != null) { %>
                        <button type="submit" name="action" value="removeVoucher" class="remove-voucher-btn" style="margin-top: 10px; margin-bottom: 10px">Remove Applied Voucher</button>
                        <% } %>
                        <% } %>
                    </div>

                    <!-- Order Summary -->
                    <div class="total_price">
                        <h3 class="description">Subtotal: <%= String.format("%.2f", subtotal) %> LE</h3>
                        <h3 class="description">Discount: <%= String.format("%.2f", discount) %> LE</h3>
                        <h3 class="heading-secondary">Total Price: <%= String.format("%.2f", totalPrice) %> LE</h3>
                    </div>
                </div>

                <!-- Payment Section -->
                <div class="left_right_wrapper">
                    <div class="right_side">
                        <h2 class="heading-primary">Payment Method</h2>
                        <div class="form form-inline">
                            <input type="radio" name="paymentMethod" id="cash" value="Cash" checked>
                            <label for="cash">Cash</label>
                        </div>
                        <div class="form form-inline">
                            <input type="radio" name="paymentMethod" id="card" value="Card">
                            <label for="card">Credit Card</label>
                        </div>

                        <div id="card-details" style="display: none;">
                            <div class="form">
                                <label for="cardholder_name">Cardholder Name</label>
                                <input type="text" name="cardholder_name" id="cardholder_name">
                            </div>
                            <div class="form">
                                <label for="card_number">Card Number</label>
                                <input type="text" name="card_number" id="card_number" pattern="[0-9]{13,16}" title="Enter 13 to 16 digit card number">
                            </div>
                            <div class="form">
                                <label for="expiry_date">Expiry Date (MM/YYYY)</label>
                                <!-- Using text input with pattern for broader compatibility -->
                                <input type="text" name="expiry_date" id="expiry_date" placeholder="MM/YYYY" pattern="(0[1-9]|1[0-2])\/\d{4}" title="Enter date as MM/YYYY">
                            </div>
                            <div class="form">
                                <label for="cvv">CVV</label>
                                <input type="text" name="cvv" id="cvv" pattern="\d{3,4}" title="Enter 3 or 4 digit CVV">
                            </div>
                        </div>
                        <!-- Place Order Button submits the main form -->
                        <button type="submit" name="action" value="placeOrder"
                                class="btn btn--full place_order_btn">
                            Place Order
                        </button>
                    </div>
                </div>
            </form>
        </div>
    </main>
</div>

<script>
    // Toggle Schedule fields based on Deliver Now checkbox
    const deliverNowCheckbox = document.getElementById('deliverNow');
    const dateField = document.getElementById('scheduleDate');
    const timeField = document.getElementById('scheduleTime');

    function toggleScheduleFields() {
        const isDeliverNow = deliverNowCheckbox.checked;
        dateField.disabled = isDeliverNow;
        timeField.disabled = isDeliverNow;
        dateField.required = !isDeliverNow;
        timeField.required = !isDeliverNow;

        if (isDeliverNow) {
            const now = new Date();
            dateField.value = now.toISOString().split('T')[0];
            timeField.value = now.toTimeString().substring(0, 5);
        } else {
            // Ensure fields are cleared if previously set by 'Deliver Now'
            dateField.value = '';
            timeField.value = '';
        }
    }

    deliverNowCheckbox.addEventListener('change', toggleScheduleFields);
    // Initial call to set state on page load
    toggleScheduleFields();

    // Toggle Card Details visibility
    const cardRadio = document.getElementById('card');
    const cashRadio = document.getElementById('cash');
    const cardDetailsDiv = document.getElementById('card-details');
    const cardNameField = document.getElementById('cardholder_name');
    const cardNumberField = document.getElementById('card_number');
    const cardExpiryField = document.getElementById('expiry_date');
    const cardCvvField = document.getElementById('cvv');


    function toggleCardDetails() {
        const showCard = cardRadio.checked;
        cardDetailsDiv.style.display = showCard ? 'block' : 'none';
        // Make card fields required only when card payment is selected
        cardNameField.required = showCard;
        cardNumberField.required = showCard;
        cardExpiryField.required = showCard;
        cardCvvField.required = showCard;
    }

    cardRadio.addEventListener('change', toggleCardDetails);
    cashRadio.addEventListener('change', toggleCardDetails);
    // Initial call to set state on page load
    toggleCardDetails();

    // Optional: Add form validation listener if needed
    // document.getElementById('checkoutForm').addEventListener('submit', function(event) {
    //     // Add any custom validation logic here
    //     // if (!valid) {
    //     //     event.preventDefault(); // Stop submission
    //     // }
    // });

</script>
</body>
</html>

