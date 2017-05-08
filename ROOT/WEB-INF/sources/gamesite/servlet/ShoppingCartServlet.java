package gamesite.servlet;

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
public class ShoppingCartServlet extends HttpServlet
{	
	public String getServletInfo()
	{
		return "Servlet initializes a shopping cart object and stores it to the current session.";
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
			//http://stackoverflow.com/questions/509076/how-do-i-address-unchecked-cast-warnings
			//Note: This will always be a <String,Integer> typed hashmap
			//@SuppressWarnings("unchecked")
			//Map<String,Integer> temp = (HashMap<String,Integer>)session.getAttribute("cartList");
			
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
			else
			{
				System.out.println("Failure!");
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

	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	{
		
	}
}
