<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<HTML>
<HEAD>
  <TITLE>GameDB Login</TITLE>
  <script src='https://www.google.com/recaptcha/api.js'></script>
</HEAD>

<BODY BGCOLOR="#FDF5E6">

<H1 ALIGN="CENTER">GameDB Login Page</H1>

<c:if test="${not empty invalidLoginFlag && invalidLoginFlag != 'null'}" >
	<p><c:out value="${invalidLoginFlag}" /></p>
	<c:set var="invalidLoginFlag" scope="request" value="null" />
</c:if>

<FORM ACTION="/servlet/loginSuccess" METHOD="POST">
  Email: <INPUT type="email" name="email" required><BR>
  Password: <INPUT type="password" name="password" required><BR>
  <CENTER>
    <INPUT TYPE="SUBMIT" VALUE="Login">
  </CENTER>
  <div class="g-recaptcha" data-sitekey="6LeuvCAUAAAAAB68t-UTWj7SyL02eIYRd3MOswzm"></div>
</FORM>

</BODY>
</HTML>
