<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="adminBean" class="com.ocn.beans.AdminBean" scope="request">
    <jsp:setProperty name="adminBean" property="adminId" param="ID"/>
    <jsp:setProperty name="adminBean" property="adminPin" param="password"/>
</jsp:useBean>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <!-- Add cache-control meta tags here -->
    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Expires" content="0">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/components/login.css" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
<main>
    <section class="section-login">
        <div class="container">
            <div class="grid grid-2-cols">
                <img src="${pageContext.request.contextPath}/img/logo/onCloudNine.svg" class="login-image">
                <div class="form-container">
                    <h2 class="heading-secondary">Admin Login</h2>
                    <% if (request.getAttribute("error") != null) { %>
                    <div class="error-message" style="${not empty error ? 'display: block;' : 'display: none;'}">
                        <p><%= request.getAttribute("error") %></p>
                    </div>
                    <% } %>
                    <form action="${pageContext.request.contextPath}/AdminLoginServlet" method="post" class="login-form">
                        <div class="input-group">
                        <input type="text"
                               name="ID"
                               placeholder="Admin ID"
                               value="<%=  adminBean.getAdminId()== null ? "" : adminBean.getAdminId() %>"
                               required />
                        </div>
                        <div class="input-group">
                        <input type="password"
                               name="password"
                               placeholder="Pin Code"
                               value="<%= adminBean.getAdminPin() == null ? "" : adminBean.getAdminPin() %>"
                               required />
                        </div>
                        <button type="submit" class="btn btn-primary">Login</button>
                    </form>
                </div>
            </div>
        </div>
    </section>
</main>
</body>
</html>