<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <title>Welcome</title>
        <link href="/tomcat.css" rel="stylesheet" type="text/css" />
        <style>
        .letterList {
            display: inline;
            list-style-type: none;
        }
        </style>
    </head>

    <body>
        <% if (session.getAttribute("first_name") != null) { %>
            <jsp:include page="/headerLinks.jsp" />
            <jsp:include page="/browse/browse.jsp" />
        <% } else { %>
            <a href="/LoginPage">Login</a> 
        <% } %>
    </body>
</html>
