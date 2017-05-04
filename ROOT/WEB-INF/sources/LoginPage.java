
/* A servlet to display the contents of the MySQL movieDB database */

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class LoginPage extends HttpServlet
{
    public String getServletInfo()
    {
       return "Servlet connects to database to verify login credentials user entered in";
    }

    // Use http GET

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
        String loginUser = "user";
        String loginPasswd = "password";
        String loginUrl = "jdbc:mysql://localhost:3306/gamedb";

        response.setContentType("text/html");    // Response mime type
		
        try
           {
              //Class.forName("org.gjt.mm.mysql.Driver");
              Class.forName("com.mysql.jdbc.Driver").newInstance();

              Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
				
			  Statement statement = dbcon.createStatement();	
				
			  String email = request.getParameter("email");
			  email.trim();
			  String password = request.getParameter("password");
              String query = "SELECT * from customers where email = '" + email + "'" + " and password = '" + password + "'";
			  ResultSet result = statement.executeQuery(query);
			
			//use a session key for the client.
			HttpSession session = request.getSession();
			
			boolean rnext = result.next();
			if(rnext)
			{
				System.out.println("Success!");
				Cookie cookie = new Cookie("login-cookie", session.getId());
				cookie.setComment("Cook on client side used to identify the current user login");
				//TODO(HARVEY):cookie.setDomain("");
				//TODO(HARVEY): review cookie.setPath()
				response.addCookie(cookie);
				session.setAttribute("first_name",result.getString("first_name"));
				response.sendRedirect("/LoginPage/LoginSuccess.jsp");
			}
			else
			{
				try
				{
					System.out.println("Invalid username or password");
					session.setAttribute("invalidLoginFlag", "Invalid email or password");
					response.sendRedirect("/LoginPage");
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			
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
