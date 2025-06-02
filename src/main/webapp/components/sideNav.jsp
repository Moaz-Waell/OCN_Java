<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<aside class="sidebar">
    <div class="logo">
        <img src="${pageContext.request.contextPath}/img/logo/onCloudNine-white.svg" alt="OCN Logo"/>
    </div>
    <nav class="navigation">

        <a href="${pageContext.request.contextPath}/HomeServlet" class="nav-item">
            <i class="fas fa-home"></i>
            <span>Home</span>
        </a>
        <a href="${pageContext.request.contextPath}/OrderServlet" class="nav-item">
            <i class="fas fa-receipt"></i>
            <span>My Orders</span>
        </a>
        <a href="${pageContext.request.contextPath}/CartServlet" class="nav-item">
            <i class="fas fa-shopping-cart"></i>
            <span>My Cart</span>
        </a>
        <a href="${pageContext.request.contextPath}/ProfileServlet" class="nav-item">
            <i class="fas fa-user"></i>
            <span>My Profile</span>
        </a>
        <a href="${pageContext.request.contextPath}/OcnLogoutServlet" class="nav-item">
            <i class="fas fa-sign-out-alt"></i>
            <span>Logout</span>
        </a>
    </nav>
</aside>

