<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<HTML>
<HEAD>
  <TITLE>GameDB Login</TITLE>
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
  <script src='https://www.google.com/recaptcha/api.js'></script>
</HEAD>

<BODY BGCOLOR="#FDF5E6">

<H1 ALIGN="CENTER">GameDB Login Page</H1>

<c:if test="${not empty invalidLoginFlag && invalidLoginFlag != 'null'}" >
	<p><c:out value="${invalidLoginFlag}" /></p>
	<c:set var="invalidLoginFlag" scope="session" value="null" />
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
