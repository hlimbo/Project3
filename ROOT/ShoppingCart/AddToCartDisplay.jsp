<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page import="java.net.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="java.text.*" %>
<%@ page import="javax.servlet.*" %>
<%@ page import="javax.servlet.http.*" %>

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
	
		<!-- HARDCODING... -->
		<% String loginUser = "user"; %>
		<% String loginPasswd = "password"; %>
		<% String loginUrl = "jdbc:mysql://localhost:3306/gamedb"; %>
		<% Connection dbcon = null; %>
		<% try { %>
		
		<% 
			/* Any JDBC 4.0 drivers that are found in your class are automatically loaded
			Must manually load any drivers prior to JDBC 4.0 with the method below
			https://docs.oracle.com/javase/tutorial/jdbc/basics/connecting.html 
		    Note: we shouldn't need this line below if we are running JDBC 4.0 or above.*/
		%>
		
		<% //Class.forName("com.mysql.jdbc.Driver").newInstance(); %>
		<% dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd); %>
		<% } catch (SQLException e) { %>
		<% e.printStackTrace(); } %>
	
		<H1>Shopping Cart</H1>
		
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
		
		<% HashMap<String,Integer> cart = (HashMap<String,Integer>)session.getAttribute("cartList"); %>	
		<% int totalCost = 0; %>
		<tbody>
				<% // sql querying should be done in a java controller class instead (This is the view!) %>
				<% if ( cart != null && !cart.isEmpty() ) { %>
				<% for (Map.Entry<String,Integer> item : cart.entrySet()){ %>
				<% String itemQuery = "SELECT * FROM games WHERE id=?"; %>
				<% PreparedStatement statement = dbcon.prepareStatement(itemQuery); %>
				<% statement.setInt(1,Integer.valueOf(item.getKey())); %>
				<% ResultSet set = statement.executeQuery(); %>
					<% if(set.next()) { %>
					<tr>
						<td><%= item.getKey() %></td>
						<td> <%= set.getString("name") %> </td>
						<td> <%= set.getInt("price") %> </td>
						<td> 
							<span>
								<input class="qtextbox" disabled="disabled" type="text" name="quantity" value=<%= item.getValue() %>>
								
								<form class="qControlBox" name="updateForm" action="/ShoppingCart/update-quantity" method="GET">
									<input type="hidden" name="itemID" value=<%= item.getKey() %> >
									<input type="hidden" name="updateFlag" value="increment" >
                                    <% for (Map.Entry<String,String> parsedParam : parsedParams.entrySet()) { %>
                                    <%= "<input type=\"hidden\" name=\""+parsedParam.getKey()+"\" value=\""+parsedParam.getValue()+"\" />"%>
                                    <% } %>
									<button name="quantity ">+</button>
								</form>
								<form class="qControlBox" name="updateForm" action="/ShoppingCart/update-quantity" method="GET">
									<input type="hidden" name="itemID" value=<%= item.getKey() %> >
									<input type="hidden" name="updateFlag" value="decrement" >
                                    <% for (Map.Entry<String,String> parsedParam : parsedParams.entrySet()) { %>
                                    <%= "<input type=\"hidden\" name=\""+parsedParam.getKey()+"\" value=\""+parsedParam.getValue()+"\" />"%>
                                    <% } %>
									<button id="q2" name="quantity ">-</button>
								</form>
							</span>
						</td>
						<td> 
							<form name="deleteForm" action="/ShoppingCart/delete-item" method="GET">
								<input type="hidden" name="itemID" value=<%= item.getKey() %> >
                                <% for (Map.Entry<String,String> parsedParam : parsedParams.entrySet()) { %>
                                <%= "<input type=\"hidden\" name=\""+parsedParam.getKey()+"\" value=\""+parsedParam.getValue()+"\" />"%>
                                <% } %>
								<button name="deleteItem"> Delete </button>
							</form>
						</td>
					</tr>
					
					<!-- calculate total cost here -->
					<% totalCost += set.getInt("price") * item.getValue(); %>
					<% } %>
				<% } } else { %>
				<tr>
					<p> Cart is Empty </p>
				</tr>
				<% } %>
				
				<% dbcon.close(); %>
		</tbody>
		</table>
		
		<hr>
		
		<!-- only display the checkout button if the cart is not empty -->
		<% if (cart != null && !cart.isEmpty() ) { %>
			<span>
				<form action="/CustomerInformation/index.jsp" method="GET">
					<button name="checkout">Continue To Checkout</button>
				</form>
				<!-- display total cost -->
				<p class="total">Total Cost: $<%= totalCost %>.00</p>
			</span>
			
			<!-- clearing the cart contents -->
			<form action="/ShoppingCart/clear-cart" method="GET">
                <% for (Map.Entry<String,String> parsedParam : parsedParams.entrySet()) { %>
                <%= "<input type=\"hidden\" name=\""+parsedParam.getKey()+"\" value=\""+parsedParam.getValue()+"\" />"%>
                <% } %>
				<button name="clearCart">Clear Cart</button>
			</form>
		<% } %>
		
		
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
