<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ocn.beans.UniUserBean" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.nio.charset.StandardCharsets" %>
<%@ page import="com.ocn.beans.UniUserBean" %>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    UniUserBean uniUser = (UniUserBean) session.getAttribute("uniUser");
    if (uniUser == null) {
        response.sendRedirect(request.getContextPath() + "/aast/UniUserLogin.jsp");
        return;
    }
%>

<%
    // Cookie creation logic with encoding
    if (uniUser != null) {
        int cookieAge = 24 * 60 * 60; // 24 hours

        // Encode all values using URLEncoder
        Cookie idCookie = new Cookie("userId",
                URLEncoder.encode(String.valueOf(uniUser.getUniId()), StandardCharsets.UTF_8));
        idCookie.setPath(request.getContextPath() + "/");
        idCookie.setMaxAge(cookieAge);
        response.addCookie(idCookie);

        Cookie nameCookie = new Cookie("userName",
                URLEncoder.encode(uniUser.getUniName(), StandardCharsets.UTF_8));
        nameCookie.setPath(request.getContextPath() + "/");
        nameCookie.setMaxAge(cookieAge);
        response.addCookie(nameCookie);

        Cookie phoneCookie = new Cookie("userPhone",
                URLEncoder.encode(uniUser.getUniPhone(), StandardCharsets.UTF_8));
        phoneCookie.setPath(request.getContextPath() + "/");
        phoneCookie.setMaxAge(cookieAge);
        response.addCookie(phoneCookie);

        Cookie attendanceCookie = new Cookie("userAttendance",
                URLEncoder.encode(String.valueOf(uniUser.getUniAttendance()), StandardCharsets.UTF_8));
        attendanceCookie.setPath(request.getContextPath() + "/");
        attendanceCookie.setMaxAge(cookieAge);
        response.addCookie(attendanceCookie);

        Cookie emailCookie = new Cookie("userEmail",
                URLEncoder.encode(String.valueOf(uniUser.getUniEmail()), StandardCharsets.UTF_8));
        emailCookie.setPath(request.getContextPath() + "/");
        emailCookie.setMaxAge(cookieAge);
        response.addCookie(emailCookie);

    }
    else {
        response.sendRedirect(request.getContextPath() + "/aast/UniUserLogin.jsp");
    }
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Student Portal</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pages/aast/uniUserPortal.css" />
</head>

<body>
<div class="student-portal">
    <header>
        <div class="logo-container">
            <img src="${pageContext.request.contextPath}/img/aast_imgs/AAST-LOGO-BLUE.png"
                 alt="Student Portal" class="logo-img" />
            <h1>Student Portal</h1>
        </div>
        <nav>
            <ul class="nav-links">
                <li class="user-profile">
                    <a href="#">
                        <span class="user-icon">👤</span>
                        ${uniUser.uniName} - ${uniUser.uniId}
                        <span class="dropdown-arrow">▼</span>
                    </a>
                </li>
                <li class="logout-btn">
                    <a href="${pageContext.request.contextPath}/UniUserLoginServlet?action=logout">Logout</a>
                </li>
            </ul>
        </nav>
    </header>
    <div class="title-section">
        <h2>Average Attendance: ${uniUser.uniAttendance}%</h2>
    </div>
    <main>
        <div class="grid-container">
            <div class="card">
                <img src="https://placehold.co/300x200/9df5ff/333333?text=Student"
                     alt="Student Results" />
                <h3>Student Results</h3>
            </div>
            <div class="card">
                <img src="https://placehold.co/300x200/ffffff/33aa33?text=Calendar"
                     alt="Student Schedule" />
                <h3>Student Schedule</h3>
            </div>
            <div class="card">
                <a href="${pageContext.request.contextPath}/HomeServlet">
                    <img src="${pageContext.request.contextPath}/img/logo/onCloudNine.svg"
                         alt="On Cloud Nine" />
                    <h3>On Cloud Nine</h3>
                </a>
            </div>
            <div class="card">
                <img src="https://placehold.co/300x200/e0f0ff/333333?text=Clinic"
                     alt="Clinic Reservation" />
                <h3>Clinic Reservation</h3>
            </div>
            <div class="card">
                <img src="https://placehold.co/300x200/9df5ff/333333?text=Training"
                     alt="Student Training" />
                <h3>Student Training</h3>
            </div>
            <div class="card">
                <img src="https://placehold.co/300x200/ffaa00/ffffff?text=Moodle"
                     alt="Old Moodle" />
                <h3>Old Moodle</h3>
            </div>
            <div class="card">
                <img src="https://placehold.co/300x200/ffaa00/ffffff?text=Moodle"
                     alt="New Moodle" />
                <h3>New Moodle</h3>
            </div>
            <div class="card">
                <img src="https://placehold.co/300x200/fff5f5/333333?text=Unofficial"
                     alt="Unofficial Transcript" />
                <h3>Unofficial Transcript</h3>
            </div>
        </div>
    </main>
</div>
</body>

</html>