<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
  <TITLE>Credit Card Information</TITLE>

  <!-- <style>
	input:invalid
	{
		border: 3px solid red;
	}
	input,
	input:validity
	{
		border: 1px solid #ccc;
	}
	.error
	{
		color: red;
	}
  </style> -->
  
  <script type="text/javascript">
  
	window.onload = function()
	{
		var input = document.getElementById("first_name");
		
		input.oninvalid = function(event)
		{
			event.target.setCustomValidity("First Name should only contain lowercase or uppercase letters. e.g. John");
		}
		
		//clears the custom validity
		input.oninput = function(event)
		{
			event.target.setCustomValidity("");
		}
		
		var input2 = document.getElementById("last_name");
		
		input2.oninvalid = function(event)
		{
			event.target.setCustomValidity("Last Name should only contain lowercase or uppercase letters. e.g. Doe");
		}
		
		input2.oninput = function(event)
		{
			event.target.setCustomValidity("");
		}
		
		var input3 = document.getElementById("cc_id");
		
		input3.oninvalid = function(event)
		{
			event.target.setCustomValidity("Credit Card number must have exactly 16 digits. i.e. 0000123456780000");
		}
		
		input3.oninput = function(event)
		{
			event.target.setCustomValidity("");
		}
		
		var input4 = document.getElementById("expiration");
		
		input4.oninvalid = function(event)
		{
			event.target.setCustomValidity("Exp Date Format: YYYY-MM-DD e.g. 2013-04-27");
		}
		
		input4.oninput = function(event)
		{
			event.target.setCustomValidity("");
		}
		
	}
	
  </script>
  
</HEAD>

<BODY BGCOLOR="#FDF5E6">

<H1 ALIGN="CENTER">Credit Card Information</H1>
<p>Confirm your purchase with credit card information.</p>



<% String errorMsg = (String)session.getAttribute("invalidFlag"); %>
<%	if (errorMsg != null) { %>
		<p class="error"> <%= errorMsg %> </p>
		<% session.setAttribute("invalidFlag", null); %>
<% } %>


<FORM ACTION="/CustomerInformation/customer-info-confirmation" METHOD="POST">
	
	<div>
		<label>First Name:</label>
		<input type="text" id="first_name" name="first_name" pattern="[A-Za-z]*" required>
	</div>
	<div>
		<label>Last Name:</label>
		<input type="text" id="last_name" name="last_name" pattern="[A-Za-z]*" required>
	</div>
	<div>
		<label>Credit Card Number:</label>
		<input name="cc_id" id="cc_id" type="text" pattern="[0-9]{16}" required>	
	</div>
	<div>
		<label>Expiration Date:</label>
		<input name="expiration" id="expiration" type="text" pattern="[0-9]{4}-(0[1-9]|1[012])-(0[1-9]|1[0-9]|2[0-9]|3[01])" required>
	</div>
	<input type="submit" value="Confirm Purchase">
</FORM>


</BODY>
</HTML>
