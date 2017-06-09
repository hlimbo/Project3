package gamesite.servlet;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import recaptcha.VerifyUtils;
import gamesite.utils.DBConnection;

public class LoginPageServlet extends HttpServlet
{
    public String getServletInfo()
    {
       return "Servlet connects to database to verify login credentials user entered in";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
		
       try
	   {
				
			Connection dbcon = DBConnection.createRead();

			//Query for email and password in database
			String email = request.getParameter("email");
			email = email.trim();
			String password = request.getParameter("password");
			String query = "SELECT * from customers where email = '"+email+"' and password = '"+password+"'";
			Statement statement = dbcon.createStatement();	
			ResultSet result = statement.executeQuery(query);
			
			//Validate Recaptcha	
			String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
			boolean valid = VerifyUtils.verify(gRecaptchaResponse);
			
			//use a session key for the client.
			HttpSession session = request.getSession();
			
			boolean rnext = result.next();
			if(rnext && valid)
			{
				Cookie cookie = new Cookie("login-cookie", session.getId());
				cookie.setComment("Cook on client side used to identify the current user login");
				//TODO(HARVEY):cookie.setDomain("");
				//TODO(HARVEY): review cookie.setPath()
				response.addCookie(cookie);
				session.setAttribute("first_name",result.getString("first_name"));
				
				RequestDispatcher dispatcher = request.getRequestDispatcher("/LoginPage/LoginSuccess.jsp");//this.getServletContext().getRequestDispatcher("/LoginPage/LoginSuccess.jsp");
				dispatcher.forward(request,response);
				
				//response.sendRedirect("/LoginPage/LoginSuccess.jsp");
			}
			else
			{
				if(!rnext)
				{
					System.out.println("Invalid email or password");
					session.setAttribute("invalidLoginFlag","Invalid email or password");
				}
				else //if recaptcha not valid
				{
					System.out.println("Please complete the recaptcha");
					session.setAttribute("invalidLoginFlag", "Please complete the ReCaptcha");
				}
				
				response.sendRedirect("/LoginPage");
			}

			result.close();
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
