package gamesite.servlet;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import gamesite.datastruct.*;
import gamesite.utils.*;
import gamesite.model.*;
import gamesite.servlet.SearchBase;

public class DisplayServlet extends SearchBase
{
    public String getServletInfo()
    {
        return "Servlet connects to MySQL database and displays a single record within the database";
    }

    // Use http GET

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
        String returnLink = "<a href=\"/\"> Return to home </a>";

        response.setContentType("text/html");    // Response mime type

        try
        {

            String table = (String) request.getParameter("table");
            table=table.replaceAll("[^\\w]","_");
            String column = (String) request.getParameter("columnName");
            column=column.replaceAll("[^\\w]","_");
            String columnValue= (String) request.getParameter(column);
            if (table==null) {
                table="games";
            }
            String offset = (String) request.getParameter("offset");
            if (offset==null) {
                offset="0";
            } else {
                offset = offset.replaceAll("[\\D]","");
            }
            try {
                request.setAttribute("displayOffset",Integer.parseInt(offset));
            } catch (NumberFormatException ex) {
                request.setAttribute("displayOffset",-1);
            }
            String limit = (String) request.getParameter("limit");
            Integer limitMax = 50;
            if (limit==null) {
                limit=limitMax.toString();
            } else {
                limit = limit.replaceAll("[\\D]","");
            }
            if (limit.trim().compareTo("")==0) {
                limit=limitMax.toString();
            }
            try {
                Integer lim = Integer.parseInt(limit);
                if (lim > limitMax) {
                    lim=limitMax;
                } else if (lim < 1) {
                    lim=1;
                }
                limit = lim.toString();
                request.setAttribute("displayLimit",lim);
            } catch (NumberFormatException ex) {
                limit = limitMax.toString();
                request.setAttribute("displayLimit",limitMax);
            }

            //Sets which fields to not display to client. Configuration option
            Hashtable<String,Boolean> fieldIgnores = new Hashtable<String,Boolean>();
            //fieldIgnores.put("id",true);
            fieldIgnores.put("platform_id",true);
            fieldIgnores.put("game_id",true);
            fieldIgnores.put("genre_id",true);
            fieldIgnores.put("publisher_id",true);
            fieldIgnores.put("globalsales",true);
            fieldIgnores.put("rank",true);
            if (table.compareToIgnoreCase("games")!=0) {
                fieldIgnores.put("url",true);
                fieldIgnores.put("trailer",true);
                fieldIgnores.put("logo",true);
            }
            //Sets which fields to hyperlink. Configuration option
            Hashtable<String,Boolean> links = new Hashtable<String,Boolean>();
            links.put("name",true);
            links.put("publisher",true);
            links.put("genre",true);
            links.put("platform",true);
            Hashtable<String,Boolean> externalLinks = new Hashtable<String,Boolean>();
            externalLinks.put("url",true);
            externalLinks.put("trailer",true);
            Hashtable<String,Boolean> images = new Hashtable<String,Boolean>();
            images.put("logo",true);

            int gameCount = -1;
            NTreeNode<Table> rows = null;
            switch (table) {
                case "games":
                    rows = SearchResults.getInstance().masterSearch(table,limit,offset,columnValue,
                        null,null,null,null,column,false,2);
                    gameCount = SearchResults.getInstance().getCount("games",limit,offset,columnValue,
                        null,null,null,null,column,false,2);
                    break;
                case "publishers":
                    rows = SearchResults.getInstance().masterSearch(table,limit,offset,null,
                        null,null,null,columnValue,column,false,2);
                    gameCount = SearchResults.getInstance().getCount("games",limit,offset,null,
                        null,null,null,columnValue,column,false,2);
                    break;
                case "platforms":
                    rows = SearchResults.getInstance().masterSearch(table,limit,offset,null,
                        null,null,columnValue,null,null,false,2);
                    gameCount = SearchResults.getInstance().getCount("games",limit,offset,null,
                        null,null,columnValue,null,column,false,2);
                    break;
                case "genres":
                    rows = SearchResults.getInstance().masterSearch(table,limit,offset,null,
                        null,columnValue,null,null,column,false,2);
                    gameCount = SearchResults.getInstance().getCount("games",limit,offset,null,
                        null,columnValue,null,null,column,false,2);
                    break;
            }
            request.setAttribute("displayGameCount",gameCount);
            String results = "";
            results+=ntreeToHtml(rows,request,null,links,images,externalLinks,fieldIgnores);

            String nextJSP = request.getParameter("nextPage");
            if (nextJSP == null) {
                nextJSP = "/display/index.jsp";
            } else {
                nextJSP="/"+nextJSP;
            }
            request.setAttribute("displayResults",results);
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP); 
            dispatcher.forward(request,response);

        }
        catch (SQLException ex) {
            PrintWriter out = response.getWriter();
            out.println("<HTML>" +
                    "<HEAD><TITLE>" +
                    "gamedb: Error" +
                    "</TITLE></HEAD>\n<BODY>" +
                    "<P>Error in SQL: ");
            while (ex != null) {
                out.println ("SQL Exception:  " + ex.getMessage ());
                ex = ex.getNextException ();
            }  // end while
            out.println("<br />\n"+returnLink+"</P></BODY></HTML>");
            out.close();
        }  // end catch SQLException
        catch (UnsupportedEncodingException ex) {
            PrintWriter out = response.getWriter();
            out.println("<HTML>" +
                    "<HEAD><TITLE>" +
                    "gamedb: Error" +
                    "</TITLE></HEAD>\n<BODY>" +
                    "<P>UnsupportedEncodingException in doGet: " +
                    ex.getMessage() + "<br />\n"+returnLink+"</P></BODY></HTML>");
            out.close();
            return;
        }
        catch(java.lang.Exception ex)
        {
            PrintWriter out = response.getWriter();
            out.println("<HTML>" +
                    "<HEAD><TITLE>" +
                    "gamedb: Error" +
                    "</TITLE></HEAD>\n<BODY>" +
                    "<P>Error in doGet: ");
            ex.printStackTrace(out);
            out.println(ex.getMessage() + "<br />\n"+returnLink+"</P></BODY></HTML>");
            out.close();
            return;
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
        doGet(request, response);
    }
}
