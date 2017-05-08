<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page import="java.net.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="java.text.*" %>
<%@ page import="javax.servlet.*" %>
<%@ page import="javax.servlet.http.*" %>

<!-- jstl include -->
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<!-- TODO(HARVEY): move back to previous page query string parsing logic to a java file -->
<%  String paramQueryString = request.getQueryString();
    HashMap<String,String> parsedParams= new HashMap<String,String>();
    if (paramQueryString!=null && paramQueryString.trim().compareTo("")!=0) {
        for (String param : paramQueryString.split("&")) {
            if (param.split("=").length > 1) { 
                String codedValue = "";
                //if (param.split("=")[0].trim().compareToIgnoreCase("previousPage")==0) {
                    //%3F is the URI encoding of %
					/*try { 		
                        decodedValue = URLDecoder.decode(
                        param.split("=")[1].substring(param.indexOf("%3F")+3), "UTF-8"); 
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
                    }*/
                //} else {
				    /*try {
                     codedValue = URLEncoder.encode(param.split("=")[1], "UTF-8");
				    } catch (UnsupportedEncodingException e) { 
				        e.printStackTrace(); 
				    }*/
                //}
			    parsedParams.put(param.split("=")[0],param.split("=")[1]);
            }
        }
    } %>


<HTML>
	<HEAD>
		<TITLE>Add To Cart</TITLE>
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
		<style>
			.error
			{
				color: red;
			}
			
			.total
			{
				text-align: right;
				font-weight: bold;
				font-size: 24;
				padding-right: 12;
			}
			.qtextbox
			{
				width: 30px;
				text-align: center;
			}
			.qControlBox
			{
				display: inline;
			}
		</style>
	
	</HEAD>
	
	<BODY>	
	
		<H1>Shopping Cart</H1>
		
		<!-- might remove this later -->
		<% if ( session.getAttribute("errorString") != null ) { %>	
		<p class="error"> <%= session.getAttribute("errorString") %> </p>
		<% session.setAttribute("errorString", null); } %>
		
		<table class="table">
		<thead>
			<th>ID</th>
			<th>Game Name</th>
			<th>Price</th>
			<th>Quantity</th>
		</thead>
		
		<tbody>
		<c:forEach var="entry" items="${cart.getItems()}">
			<tr>	
				<td> <c:out value="${entry.key}" /> </td>
				<td> <c:out value="${entry.value.getGameName()}" /> </td>
				<td> <c:out value="${entry.value.getPrice()}" /> </td>
				<td> <c:out value="${entry.value.getQuantity()}" /> </td>
			</tr>
		</c:forEach>
		</tbody>
		
		</table>
		
		<hr>
		
		<!-- back to previous page -->
		<% if( request.getParameter("previousPage") != null ){%>
			<% String previousPage = (String)request.getParameter("previousPage"); %>
			<% if(previousPage.indexOf("?") != -1) { %>
				<form action=<%= previousPage.split("\\?")[0] %> method="GET">	
					<% String queryString = previousPage.split("\\?")[1]; %> 
					<% for(String param : queryString.split("&")){ %>
					<% if( param.split("=").length > 1) { %>
				    <% String parsedValue = "name=\'" + param.split("=")[0] + "\'" + " value=\'"; %>  
					<% try { 		String decodedValue = URLDecoder.decode(param.split("=")[1], "UTF-8"); %>
						 <% parsedValue += decodedValue + "\'"; %>
						  <input type="hidden" <%= parsedValue  %> >
					<% } catch (UnsupportedEncodingException e) { %>
					<%	e.printStackTrace(); %>
					<% } %>
					<% }  } %>
		
			
		<% } else {  %>
			<form action=<%= request.getParameter("previousPage") %> method="GET">	
		<% }  %>
				<button name="backToPrev">Back to Previous Page</button>
		</form>
		<% } %>
		
		
		<!-- back to home page -->
		<form action="/index.jsp" method="GET">
			<button name="backToHome">Back to Home</button>
		</form>
		
	</BODY>
</HTML>
