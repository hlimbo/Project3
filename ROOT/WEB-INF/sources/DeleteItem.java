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

public class DeleteItem extends HttpServlet
{	
	public String getServletInfo()
	{
		return "Servlet deletes a shopping cart item";
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException
	{	
		System.out.println("DeleteItem.java");
		HttpSession session = request.getSession(false);	
		HashMap<String,Integer> cartList = (HashMap<String,Integer>)session.getAttribute("cartList");
/* 		ArrayList<?> attrs = (ArrayList<?>)session.getAttribute("cartList");
        if (attrs != null) {
            cartList = new ArrayList<String>();
            for (Object attr : attrs) {
                cartList.add((String)attr);
            }
        } */
		if(cartList == null)
		{
			System.out.println("Cannot remove item. Cart is already empty");
			session.setAttribute("errorString", "Cannot remove item. Cart is already empty");
		}
		else
		{
			String itemID = (String)request.getParameter("itemID");
			if(cartList.containsKey(itemID))
			{
				cartList.remove(itemID);
				System.out.println("Successfully removed game id: " + itemID);
			}
			else
			{
				System.out.println("Game ID not found in cart: " + itemID);
			}
		}
		
		//response.sendRedirect("/ShoppingCart/AddToCartDisplay.jsp");
		if(request.getParameter("previousPage") != null)
			response.sendRedirect("/ShoppingCart/AddToCartDisplay.jsp?previousPage=" + (String)request.getParameter("previousPage"));
		else
			response.sendRedirect("/ShoppingCart/AddToCartDisplay.jsp");//ShoppingCart/AddToCartDisplay.jsp"
		
	}
	
	/* public void doPost(HttpServletRequest request, HttpServletResponse response)
	{
		
	} */
}
