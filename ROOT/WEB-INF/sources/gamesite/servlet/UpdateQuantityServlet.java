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

//The servlet's job should be to:
// 1. redirect the user to the proper jsp webpage.
// 2. authenticate user credentials
// 3. query for certain items to be found in the database. i.e. game id, genre id.

public class UpdateQuantityServlet extends HttpServlet
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
		ShoppingCart cart = (ShoppingCart)session.getAttribute("cart");
		if(cart == null|| cart.isEmpty())
		{
			System.out.println("Cannot update item quantity. Cart is already empty");
			session.setAttribute("errorString", "Cannot update item. quantity Cart is already empty");
		}
		else
		{
			String itemID = (String)request.getParameter("itemID");
			if(cart.containsKey(itemID))
			{				
				String updateFlag = (String)request.getParameter("updateFlag");
				Integer newQuantity =  null;
				
				if(updateFlag.equals("increment"))
					newQuantity = cart.getItem(itemID).getQuantity() + 1;
				else if(updateFlag.equals("decrement"))
					newQuantity = cart.getItem(itemID).getQuantity() - 1;
				else
				{
					System.out.println("Error: updateFlag=" + (String)request.getParameter("updateFlag"));
					return;
				}
				
				if(newQuantity != null)
				{
					if(newQuantity <= 0)
						cart.remove(itemID);
					else
						cart.getItem(itemID).setQuantity(newQuantity);
					
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
