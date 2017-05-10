<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
  <TITLE>GameDB Login</TITLE>
</HEAD>

<BODY BGCOLOR="#FDF5E6">

<H1 ALIGN="CENTER">GameDB Login Page</H1>


<!-- can only obtain values from httprequest and httpresponse -->
<% if ( session.getAttribute("invalidLoginFlag") != null){ %>
	<p><%= session.getAttribute("invalidLoginFlag") %></p>
	 <% session.setAttribute("invalidLoginFlag", null); %>
<% } %>

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
