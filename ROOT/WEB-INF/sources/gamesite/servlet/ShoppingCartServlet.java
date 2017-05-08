package gamesite.servlet;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;


import gamesite.model.ShoppingCart;
import gamesite.model.ShoppingCartItem;
import gamesite.utils.DBConnection;
import gamesite.utils.SQLQuery;

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
	
		ShoppingCart cart = null;
		HttpSession session = request.getSession();
		
		//critical section ~ retrieve the list of items in cart if it already exists
		//if not, create a new cart with the item added to it.
		synchronized(session)
		{
			//http://stackoverflow.com/questions/509076/how-do-i-address-unchecked-cast-warnings
			//Note: This will always be a <String,Integer> typed hashmap
			//@SuppressWarnings("unchecked")
			//Map<String,Integer> temp = (HashMap<String,Integer>)session.getAttribute("cartList");
			
			cart = (ShoppingCart)session.getAttribute("cart");
			
			//if  cartList from the cart does not already exist, create one
			if(cart == null)
			{
				cart = new ShoppingCart();
				session.setAttribute("cart",cart);
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
			else if(cart != null)
			{
				try
				{
					Connection dbcon = DBConnection.create();
					
					//query for item to add in cart
					Integer game_id = Integer.valueOf(id);
					//check if supplied game_id is in the database;
					ResultSet set = SQLQuery.getGameInfo(dbcon, game_id);
					if(set.next())
					{
						//add new shopping cart item.
						ShoppingCartItem item = new ShoppingCartItem(set.getString("name"), set.getInt("price"), 1);
						//Note: this function also internally updates the quantity of the item if the item is already in the cart.
						cart.put(game_id.toString(), item);
						
						System.out.println("Successfully placed: " + item.getGameName() + " into cart");
					}
				
					
					DBConnection.close(dbcon);
				}
				catch (SQLException ex)
				{
					PrintWriter out = response.getWriter();
					String returnLink = "<a href=\"/\"> Return to home </a>";
					out.println("<HTML>" +
							"<HEAD><TITLE>" +
							"gamedb: Error" +
							"</TITLE></HEAD>\n<BODY>" +
							"<P>Error in SQL: ");
							
					while (ex != null)
					{
						out.println ("SQL Exception:  " + ex.getMessage ());
						ex = ex.getNextException ();
					}
				    out.println("<br />\n"+returnLink+"</P></BODY></HTML>");
					out.close();	
				}
				
				//System.out.println("id: " + id);
				//Integer quantity = cartList.get(id) == null ? 0 : (Integer) cartList.get(id);
				//cart.put(id, ++quantity);
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
