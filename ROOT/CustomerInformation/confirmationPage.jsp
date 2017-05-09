<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<HTML>
	<HEAD>
		<TITLE>Confirmation Page</TITLE>
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
	</HEAD>
	
	<BODY>
		
		<H1>Confirmation Page</H1>
		<c:if test="${empty cart || cart.isEmpty()}">
			<p> Shopping cart is empty!!!!!! </p>
		</c:if>
		<c:if test="${not empty cart && !cart.isEmpty()}">
			<p> <c:out value="${first_name}" />, You bought something special </p>
			
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
		</c:if>
		
		<!-- clear the cart after successful purchase TODO(HARVEY): On the next project, implement this using ajax -->
		${cart.clear()}
		
		
		<!-- back to home page -->
		<form action="/index.jsp" method="GET">
			<button name="backToHome">Back to Home</button>
		</form>
		
	</BODY>
</HTML>