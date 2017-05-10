package recaptcha;
/* A servlet to display the contents of the MySQL movieDB database */

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TomcatFormReCaptcha extends HttpServlet
{
    public String getServletInfo()
    {
       return "Servlet connects to MySQL database and displays result of a SELECT";
    }

    // Use http GET

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

	String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
	out.println("gRecaptchaResponse=" + gRecaptchaResponse);
	// Verify CAPTCHA.
	boolean valid = VerifyUtils.verify(gRecaptchaResponse);
	if (!valid) {
	    //errorString = "Captcha invalid!";
	    out.println("<HTML>" +
			"<HEAD><TITLE>" +
			"MovieDB: Error" +
			"</TITLE></HEAD>\n<BODY>" +
			"<P>Recaptcha WRONG!!!! </P></BODY></HTML>");
	    return;
	}
	  
        String loginUser = "user";
        String loginPasswd = "password";
        String loginUrl = "jdbc:mysql://localhost:3306/gamedb";

        response.setContentType("text/html");    // Response mime type


        out.println("<HTML><HEAD><TITLE>GameDB</TITLE></HEAD>");
        out.println("<BODY><H1>GameDB</H1>");

        try
           {
              //Class.forName("org.gjt.mm.mysql.Driver");
              Class.forName("com.mysql.jdbc.Driver").newInstance();

              Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
              // Declare our statement
              Statement statement = dbcon.createStatement();

	      String lastname = request.getParameter("lastname");
              String query = "SELECT * from games where name = '" + lastname + "'";
		

		out.println(query);
              // Perform the query
              ResultSet rs = statement.executeQuery(query);

              out.println("<TABLE>");
		out.println("HELLO");
              // Iterate through each row of rs
              while (rs.next())
              {
		 out.println("stuff");
                  Integer m_ID = rs.getInt("id");
                  String m_FN = rs.getString("name");
                  String m_LN = rs.getString("url");
                  out.println("<tr>" +
                              "<td>" + m_ID + "</td>" +
                              "<td>" + m_FN + "</td>" +
                              "<td>" + m_LN + "</td>" +
                              "</tr>");
              }

              out.println("</TABLE>");

              rs.close();
              statement.close();
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
                out.println("<HTML>" +
                            "<HEAD><TITLE>" +
                            "MovieDB: Error" +
                            "</TITLE></HEAD>\n<BODY>" +
                            "<P>SQL error in doGet: " +
                            ex.getMessage() + "</P></BODY></HTML>");
                return;
            }
         out.close();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
		doGet(request, response);
    }
}