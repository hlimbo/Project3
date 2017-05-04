import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;


//The servlet's job should be to:
// 1. redirect the user to the proper jsp webpage.
// 2. authenticate user credentials
// 3. query for certain items to be found in the database. i.e. game id, genre id.

public class ClearCart extends HttpServlet
{	
	public String getServletInfo()
	{
		return "Servlet clears shopping cart contents";
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException
	{	
		System.out.println("Clearcart.java");
		HttpSession session = request.getSession(false);	
		HashMap<String,Integer> cartList = (HashMap<String,Integer>)session.getAttribute("cartList");
		if(cartList == null)
		{
			System.out.println("Cart is already empty");
			session.setAttribute("errorString", "Cart is already empty");
		}
		else
		{
			cartList.clear();
			cartList = null;
			session.setAttribute("cartList", null);
		}
		
		if(request.getParameter("previousPage") != null)
			response.sendRedirect("/ShoppingCart/AddToCartDisplay.jsp?previousPage=" + (String)request.getParameter("previousPage"));
		else
			response.sendRedirect("/ShoppingCart/AddToCartDisplay.jsp");//ShoppingCart/AddToCartDisplay.jsp"
		
	}
	
	/* public void doPost(HttpServletRequest request, HttpServletResponse response)
	{
		
	} */
}
