<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- Redirect to a login page or a home servlet --%>
<% response.sendRedirect("aast/UniUserLogin.jsp"); // Or a servlet e.g., request.getContextPath() + "/UserLoginServlet" %>
<html>
<head>
    <title>AAST - Welcome</title>
</head>
<body>
    <p>Loading application...</p>
</body>
</html>

