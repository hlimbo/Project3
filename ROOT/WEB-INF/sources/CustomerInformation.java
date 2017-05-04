
/* A servlet to display the contents of the MySQL movieDB database */

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;


public class CustomerInformation extends HttpServlet
{
	
    public String getServletInfo()
    {
       return "Servlet verifies customer information is in the creditcards table database";
    }	
	
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
        String loginUser = "user";
        String loginPasswd = "password";
        String loginUrl = "jdbc:mysql://localhost:3306/gamedb";

		
		System.out.println("CustomerInformation class is active");
		
        response.setContentType("text/html");    // Response mime type
		
        try
        {
              //Class.forName("org.gjt.mm.mysql.Driver");
              Class.forName("com.mysql.jdbc.Driver").newInstance();
              Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);	

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
					session.setAttribute("invalidFlag","Customer's identity: " + first_name +  " could not be verified in the database");
					response.sendRedirect("/CustomerInformation/confirmationPage.jsp");
					return;
				}
				
				HashMap<String,Integer> cart = (HashMap<String,Integer>)session.getAttribute("cartList");
				String gameIdQuery = "SELECT id FROM games WHERE id = ?";
				String insertQuery = "INSERT INTO sales (customer_id, salesdate, game_id) VALUES( ?, CURDATE(), ?)";
				PreparedStatement gameIDStatement = dbcon.prepareStatement(gameIdQuery);
				PreparedStatement insertStatement = dbcon.prepareStatement(insertQuery);
				//used to verify if the game id is a valid id in the database.
				if(cart != null && !cart.isEmpty())
				{
					for(Map.Entry<String,Integer> item : cart.entrySet())
					{
						gameIDStatement.setInt(1, Integer.valueOf(item.getKey()));
						ResultSet gameIdSet = gameIDStatement.executeQuery();
						if(gameIdSet.next())
						{
							int gameID = gameIdSet.getInt(1);
                            int quantity = item.getValue();
							insertStatement.setInt(1, customerID);
							insertStatement.setInt(2, gameID);
                            for (int qIndex=0;qIndex<quantity;++qIndex) {
							    insertStatement.executeUpdate();
                            }
						}
						else
						{
							System.out.println("Game ID: " + item.getKey() + " does not exist in the games table");
						}
					}
				}
				else
				{
					System.out.println("Cart is empty or has not been initialized");
				}
				
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
			  result.close();
              dbcon.close();
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
