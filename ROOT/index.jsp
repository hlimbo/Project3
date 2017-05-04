<%--
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
--%>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
		<TITLE>Games Station | Main</TITLE>
        <link href="/tomcat.css" rel="stylesheet" type="text/css" />
        <style>
        .letterList {
            display: inline;
            list-style-type: none;
        }
        </style>
    </head>

    <body>
		<h1>Games Station Main Page</h1>
        <% if (session.getAttribute("first_name") != null) { %>
            <jsp:include page="/headerLinks.jsp" />
            <jsp:include page="/browse/browse.jsp" />
            <jsp:include page="/search/search.jsp" />
        <% } else { %>
            <a href="/LoginPage">Login</a> 
        <% } %>
    </body>
</html>
