import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;


//TODO(HARVEY): Find a way to import a separate java file in the same directory via packages.
public class ShoppingCartDisplay extends HttpServlet
{	
	public String getServletInfo()
	{
		return "Servlet displays/adds shopping cart contents";
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException
	{		
		HttpSession session = request.getSession();
		ArrayList<String> previousItems = null;
		//critical section ~ retrieve the list of items in cart if it already exists
		//if not, create a new cart with the item added to it.
		synchronized(session)
		{
			ArrayList<?> attrs = (ArrayList<?>)session.getAttribute("previousItems");
            if (attrs != null) {
                previousItems = new ArrayList<String>();
                for (Object attr : attrs) {
                    previousItems.add((String)attr);
                }
            }
			//if previousItems list from the cart does not already exist, create one
			if(previousItems == null)
			{
				previousItems = new ArrayList<String>();
				session.setAttribute("previousItems",previousItems);
			}
		}
		
		//retrieve the newItem to be added to cart from HttpServletRequest
		String newItem = request.getParameter("newItem");
		
		//have java generate the HTML code for the user to see.
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();	
		String title = "Items Purchased";	
		String docType = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 " +
	      "Transitional//EN\">\n";
		  
		 out.println(docType +
	                "<HTML>\n" +
	                "<HEAD><TITLE>" + title + "</TITLE></HEAD>\n" +
                "<BODY BGCOLOR=\"#FDF5E6\">\n" +
	                "<H1>" + title + "</H1>");
		
		//critical section
		synchronized(previousItems) {
			//if newItem added is valid
	      if (newItem != null) {
	        previousItems.add(newItem);
	      }
	      if (previousItems.size() == 0) {
	        out.println("<I>No items</I>");
	      } else {
			  //display a list of items added in cart  via html
	        out.println("<UL>");
	        for(int i=0; i<previousItems.size(); i++) {
	          out.println("<LI>" + (String)previousItems.get(i));
	        }
	        out.println("</UL>");
	      }  
		}
		
	   // The following two statements show how this thread can access an
	   // object created by a thread of the ShowSession servlet
	   //Integer accessCount = (Integer)session.getAttribute("accessCount");
	   //out.println("<p>accessCount = " + accessCount);
	   
	   out.println("</BODY></HTML>");
	   
	   out.close();
	}
	
	/* public void doPost(HttpServletRequest request, HttpServletResponse response)
	{
		
	} */
}
