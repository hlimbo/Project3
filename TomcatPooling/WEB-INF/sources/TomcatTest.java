
/* A servlet to display the contents of the MySQL movieDB database */

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import javax.naming.InitialContext;
import javax.naming.Context;
import javax.sql.DataSource;

public class TomcatTest extends HttpServlet {
    public String getServletInfo() {
        return "Servlet connects to MySQL database and displays result of a SELECT";
    }

    // Use http GET

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        response.setContentType("text/html"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        out.println("<HTML><HEAD><TITLE>GameDB</TITLE></HEAD>");
        out.println("<BODY><H1>GameDB (with some changes)</H1>");

        try {
            // the following few lines are for connection pooling
            // Obtain our environment naming context

            Context initCtx = new InitialContext();
            if (initCtx == null)
                out.println("initCtx is NULL");

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                out.println("envCtx is NULL");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/TestDB");

            // the following commented lines are direct connections without pooling
            //Class.forName("org.gjt.mm.mysql.Driver");
            //Class.forName("com.mysql.jdbc.Driver").newInstance();
            //Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

            if (ds == null)
                out.println("ds is null.");

            Connection dbcon = ds.getConnection();
            if (dbcon == null)
                out.println("dbcon is null.");

            // Declare our statement
            Statement statement = dbcon.createStatement();
            String query = "SELECT * from stars";

            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            out.println("<TABLE border>");

            // Iterate through each row of rs
            while (rs.next()) {
                String m_FN = rs.getString("first_name");
                String m_LN = rs.getString("last_name");
                String m_dob = rs.getString("dob");
                out.println("<tr>" + "<td>" + m_FN + "</td>" + "<td>" + m_LN + "</td>" + "<td>" + m_dob + "</td>"
                        + "</tr>");
            }

            out.println("</TABLE>");

            rs.close();
            statement.close();
            dbcon.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            while (ex != null) {
                System.out.println("SQL Exception:  " + ex.getMessage());
                ex = ex.getNextException();
            } // end while
        } // end catch SQLException

        catch (java.lang.Exception ex) {
            out.println("<HTML>" + "<HEAD><TITLE>" + "GameDB: Error" + "</TITLE></HEAD>\n<BODY>"
                    + "<P>SQL error in doGet: " + ex.getMessage() + "</P></BODY></HTML>");
            return;
        }
        out.close();
    }
}
