<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="userBean" class="com.ocn.beans.UniUserBean" scope="request">
  <jsp:setProperty name="userBean" property="uniId" param="ID"/>
  <jsp:setProperty name="userBean" property="uniPin" param="password"/>
</jsp:useBean>

<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>AASTMT Student Portal</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pages/aast/uniUserLogin.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mediaqueries/general.css" />
</head>

<body>
<div class="portal-container">
  <div class="background-section">
    <div class="overlay">
      <h1>AASTMT Student Portal</h1>
      <p>
        AASTMT Student Portal is an online gateway where students can log in
        to access important program information. Student Portal contains
        information on courses, transcripts, timetables, exam schedules and
        department contact numbers.
      </p>
    </div>
  </div>
  <div class="uni-login-section">
    <div class="logo-container">
      <img src="${pageContext.request.contextPath}/img/aast_imgs/AAST-LOGO-BLUE.png"
           alt="College Logo" class="college-logo" />
      <h2>Student Portal</h2>
    </div>

    <div class="form-section">
      <h3>Registration</h3>
      <button class="register-btn">Open Registration</button>

      <h3>Login</h3>

      <!-- Error Message Display -->
      <div class="error-message" style="${not empty error ? 'display: block;' : 'display: none;'}">
        ${error}
      </div>

      <form action="${pageContext.request.contextPath}/UniUserLoginServlet" method="post">
        <input type="text"
               name="ID"
               placeholder="Registration Number"
               value="<%= userBean.getUniId() == null ? "" : userBean.getUniId() %>"
               required />

        <input type="password"
               name="password"
               placeholder="Pin Code"
               value="<%= userBean.getUniPin() == null ? "" : userBean.getUniPin() %>"
               required />

        <div class="remember-me">
          <input type="checkbox" id="remember" />
          <label for="remember">Remember Me</label>
        </div>

        <button type="submit" class="login-btn">Login</button>
      </form>

      <p class="forgot-password">
        Can't log in or Forgot Password? Click <a href="#">here</a> to send
        your password.
      </p>
    </div>
  </div>
</div>
</body>

</html>