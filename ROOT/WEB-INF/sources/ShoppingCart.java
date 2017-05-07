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

//TODO(HARVEY): create java beans class ShoppingCartItem
public class ShoppingCart extends HttpServlet
{	
	public String getServletInfo()
	{
		return "Servlet displays/adds shopping cart contents";
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException
	{		
		HttpSession session = request.getSession();
		//key = game_id string
		//value = quantity int
		HashMap<String,Integer> cartList = null;
		//critical section ~ retrieve the list of items in cart if it already exists
		//if not, create a new cart with the item added to it.
		synchronized(session)
		{
			cartList = (HashMap<String,Integer>)session.getAttribute("cartList");
			//if  cartList from the cart does not already exist, create one
			if(cartList == null)
			{
				cartList = new HashMap<String,Integer>();
				session.setAttribute("cartList",cartList);
			}
		}
		
		int parameterCount = request.getParameterMap().size();
		if(parameterCount > 0)
		{	
			String id = request.getParameter("id");
			if(id == null)
			{
				System.out.println("id value is null");
			}
			else if(cartList != null)
			{
				System.out.println("id: " + id);
				Integer quantity = cartList.get(id) == null ? 0 : (Integer) cartList.get(id);
				cartList.put(id, ++quantity);
			}
		}
		else
		{
			System.out.println("Zero parameters were passed!");
		}
		
		//how to have /ShoppingCart/view-shopping-cart be linked to ShoppingCart/AddToCartDisplay.jsp
		if(request.getParameter("previousPage") != null)
			response.sendRedirect("/ShoppingCart/AddToCartDisplay.jsp?previousPage=" + (String)request.getParameter("previousPage"));
		else
			response.sendRedirect("/ShoppingCart/AddToCartDisplay.jsp");//ShoppingCart/AddToCartDisplay.jsp"
		
	/* 	//retrieve the newItem to be added to cart from HttpServletRequest
		String newItem = request.getParameter("newItem");
		
		//have java generate the HTML code for the user to see.
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();	
		String title = "Items Purchased";	
		String docType = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 " +
	      "Transitional//EN\">\n";
		  
		 out.println(docType +
	                "<HTML>\n" +
	                "<HEAD><TITLE>" + title + "</TITLE></HEAD>\n" +
                "<BODY BGCOLOR=\"#FDF5E6\">\n" +
	                "<H1>" + title + "</H1>");
		
		//critical section
		synchronized(previousItems) {
			//if newItem added is valid
	      if (newItem != null) {
	        previousItems.add(newItem);
	      }
	      if (previousItems.size() == 0) {
	        out.println("<I>No items</I>");
	      } else {
			  //display a list of items added in cart  via html
	        out.println("<UL>");
	        for(int i=0; i<previousItems.size(); i++) {
	          out.println("<LI>" + (String)previousItems.get(i));
	        }
	        out.println("</UL>");
	      }  
		}
		
	   // The following two statements show how this thread can access an
	   // object created by a thread of the ShowSession servlet
	   //Integer accessCount = (Integer)session.getAttribute("accessCount");
	   //out.println("<p>accessCount = " + accessCount);
	   
	   out.println("</BODY></HTML>");
	   
	   out.close(); */
	}
	
	/* public void doPost(HttpServletRequest request, HttpServletResponse response)
	{
		
	} */
}
