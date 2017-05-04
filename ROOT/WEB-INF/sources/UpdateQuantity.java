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

public class UpdateQuantity extends HttpServlet
{	
	public String getServletInfo()
	{
		return "Servlet updates the quantity of a shopping cart item";
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException
	{	
		System.out.println("UpdateQuantity.java");
		HttpSession session = request.getSession(false);	
		HashMap<String,Integer> cartList = (HashMap<String,Integer>)session.getAttribute("cartList");
		if(cartList == null)
		{
			System.out.println("Cannot update item quantity. Cart is already empty");
			session.setAttribute("errorString", "Cannot update item. quantity Cart is already empty");
		}
		else
		{
			String itemID = (String)request.getParameter("itemID");
			if(cartList.containsKey(itemID))
			{				
				String updateFlag = (String)request.getParameter("updateFlag");
				Integer newQuantity =  null;
				
				if(updateFlag.equals("increment"))
					newQuantity = cartList.get(itemID) + 1;
				else if(updateFlag.equals("decrement"))
					newQuantity = cartList.get(itemID) - 1;
				else
				{
					System.out.println("Error: updateFlag=" + (String)request.getParameter("updateFlag"));
					return;
				}
				
				if(newQuantity != null)
				{
					if(newQuantity <= 0)
						cartList.remove(itemID);
					else
						cartList.put(itemID, newQuantity);
					
				}
				else
					System.out.println("Error: newQuantity is null");
			}
			else
			{
				System.out.println("Game ID not found in cart: " + itemID);
			}
		}
		
		//session.getServletContext().getRequestDispatcher("/ShoppingCart/AddToCartDisplay.jsp").forward(request, response);
		//response.sendRedirect("/ShoppingCart/AddToCartDisplay.jsp");
		if(request.getParameter("previousPage") != null)
			response.sendRedirect("/ShoppingCart/AddToCartDisplay.jsp?previousPage=" + (String)request.getParameter("previousPage"));
		else
			response.sendRedirect("/ShoppingCart/AddToCartDisplay.jsp");//ShoppingCart/AddToCartDisplay.jsp"
		
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException
	{
			doGet(request,response);
	}
}
