<HTML>
	<HEAD>
		<TITLE>Games Station | Main</TITLE>
        <link href="/tomcat.css" rel="stylesheet" type="text/css" />
        <style>
        .letterList {
            display: inline;
            list-style-type: none;
        }
        </style>
	</HEAD>
	
	<BODY>
		<h1>Games Station Main Page</h1>
		<% if ( session.getAttribute("first_name") == null) { %>
			name is null
        <% } else {%>
		<p> Welcome, <%= (String)session.getAttribute("first_name") %>! </p>
	<div>
        <jsp:include page="/headerLinks.jsp" />
        <jsp:include page="/browse/browse.jsp" />
        <jsp:include page="/search/search.jsp" />
	</div>
        <% }  %>
				
	</BODY>
</HTML>
