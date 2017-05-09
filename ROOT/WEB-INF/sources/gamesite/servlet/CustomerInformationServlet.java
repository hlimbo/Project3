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

public class CustomerInformationServlet extends HttpServlet
{
	
    public String getServletInfo()
    {
       return "Servlet verifies customer information is in the creditcards table database";
    }	
	
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {	
	
		System.out.println("CustomerInformation class is active");	
        response.setContentType("text/html");    // Response mime type
		
        try
        {
			Connection dbcon = DBConnection.create();

			HttpSession session = request.getSession();
			String first_name = (String)request.getParameter("first_name");
			String last_name = (String)request.getParameter("last_name");
			String cc_id = (String)request.getParameter("cc_id");
			String expiration = (String)request.getParameter("expiration");
			java.sql.Date expDate = java.sql.Date.valueOf(expiration);
			
			Statement statement = dbcon.createStatement();	
             String query = "SELECT * FROM creditcards WHERE id='" + cc_id 
			 + "' and first_name='" + first_name + "' and last_name='" + last_name + "' and expiration='" + expDate + "';";
			  
			ResultSet result = statement.executeQuery(query);
			//get the number of rows in the set executed by query
			result.last();
			int rowCount = result.getRow();
			System.out.println(query);
			System.out.println(rowCount);
						
			if(rowCount == 1)
			{
				System.out.println(first_name + " " + last_name + " was found in the creditcards table");
				
				//for every item in cart,insert each successful purchased item into sales table.
				String customerIdQuery = "SELECT id FROM customers WHERE first_name='" + first_name + "' and last_name='" + last_name + "'";//"' and cc_id='" + cc_id + "';";	
				
				ResultSet custIdSet = statement.executeQuery(customerIdQuery);
				Integer customerID = null;
				if(custIdSet.next())
				{
					customerID = custIdSet.getInt("id");
				}
				else
				{
					System.out.println("Customer's identity could not be verified in the database");
					
					statement.close();
					DBConnection.close(dbcon);		
					
					session.setAttribute("invalidFlag","Customer's identity: " + first_name +  " could not be verified in the database");
					response.sendRedirect("/CustomerInformation/confirmationPage.jsp");
					return;
				}
				
				ShoppingCart cart = (ShoppingCart)session.getAttribute("cart");
				//String gameIdQuery = "SELECT id FROM games WHERE id = ?";
				String insertQuery = "INSERT INTO sales (customer_id, salesdate, game_id) VALUES( ?, CURDATE(), ?)";
				PreparedStatement insertStatement = dbcon.prepareStatement(insertQuery);
				//used to verify if the game id is a valid id in the database.
				if(cart != null && !cart.isEmpty())
				{
					//insert every game bought in cart into the sales table.
					for(Map.Entry<String,ShoppingCartItem> entry : cart.itemSet())
					{
						Integer gameID = Integer.valueOf(entry.getKey());						
						insertStatement.setInt(1, customerID);
						insertStatement.setInt(2, gameID);
						
						//insert game into sales table x times where x = quantity bought
						for(int i = 0;i < entry.getValue().getQuantity(); ++i)
						{
							insertStatement.executeUpdate();
						}
						
					}
				}
				else
				{
					System.out.println("Cart is empty or has not been initialized");
				}
				
				insertStatement.close();
				session.setAttribute("first_name",first_name);				
				response.sendRedirect("/CustomerInformation/confirmationPage.jsp");
			}
			else if(rowCount > 1)
			{
				System.out.println("There are multiple records in the database with the same information");
				session.setAttribute("invalidFlag","There are multiple records in the database with the same information");
				response.sendRedirect("/CustomerInformation/confirmationPage.jsp");
			}
			else//if rowCount == 0
			{
				try
				{
					System.out.println("Supplied information not found or does not match in creditcards table");
					session.setAttribute("invalidFlag", "Supplied information not found or does not match in creditcards table");
					response.sendRedirect("/CustomerInformation/index.jsp");
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			
				statement.close();
				DBConnection.close(dbcon);
			}
        catch (SQLException ex) {
              while (ex != null) {
                    System.out.println ("SQL Exception:  " + ex.getMessage ());
                    ex = ex.getNextException ();
                }  // end while
            }  // end catch SQLException

        catch(java.lang.Exception ex)
            {
                System.out.println("<HTML>" +
                            "<HEAD><TITLE>" +
                            "MovieDB: Error" +
                            "</TITLE></HEAD>\n<BODY>" +
                            "<P>SQL error in doGet: " +
                            ex.getMessage() + "</P></BODY></HTML>");
                return;
            }
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
		doGet(request, response);
	}
}
