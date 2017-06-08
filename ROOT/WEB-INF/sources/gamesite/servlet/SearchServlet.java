package gamesite.servlet;

import static java.nio.file.StandardOpenOption.*;
import java.io.*;
import java.nio.*;
import java.nio.file.*;
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

public class SearchServlet extends SearchBase 
{
    public String getServletInfo()
    {
        return "Servlet displays search results";
    }


    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
        long startTime = System.nanoTime();
        response.setContentType("text/html");    // Response mime type

        String returnLink = "<a href=\"/\"> Return to home </a>";
        try
        {
            String table = (String) request.getParameter("table");
            if (table==null) {
                table="games";
            } else {
                table = table.replaceAll("[^\\w]","_");
            }
            String limit = (String) request.getParameter("limit");
            Integer limitMax = SearchResults.limitMax;
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
                request.setAttribute("searchLimit",lim);
            } catch (NumberFormatException ex) {
                limit = limitMax.toString();
                request.setAttribute("searchLimit",limitMax);
            }

            String offset = (String) request.getParameter("offset");
            if (offset==null) {
                offset="0";
            } else {
                offset = offset.replaceAll("[\\D]","");
            }
            try {
                request.setAttribute("searchOffset",Integer.parseInt(offset));
            } catch (NumberFormatException ex) {
                request.setAttribute("searchOffset",-1);
            }

            String order = (String) request.getParameter("order");
            if (order==null) {
                order="id";
            } else {
                order = order.replaceAll("[^\\w]","_");
            }
            String descParam = (String) request.getParameter("descend");
            boolean descend = false;
            if (descParam != null && descParam.trim().compareToIgnoreCase("true")==0) {
                descend=true;
            }

            String matchParameter = (String) request.getParameter("match");
            int useSubMatch = 1;
            if (matchParameter != null) { //&& matchParameter.toLowerCase().trim().equals("true")) {
                try {
                    useSubMatch = Integer.parseInt(matchParameter);
                } catch (NumberFormatException ex) {
                    useSubMatch = 1;
                }
            }

            String ledaStr = (String) request.getParameter("leda");
            Integer ledaMax = 15;
            Integer leda = 0;
            if (ledaStr==null) {
                ledaStr="0";
            } else {
                ledaStr = ledaStr.replaceAll("[\\D]","");
            }
            if (ledaStr.trim().compareTo("")==0) {
                ledaStr="0";
            }
            try {
                leda = Integer.parseInt(ledaStr);
                if (leda > ledaMax) {
                    leda=ledaMax;
                } else if (leda < 1) {
                    leda=0;
                }
                ledaStr = leda.toString();
            } catch (NumberFormatException ex) {
                ledaStr = "0";
                leda=0;
            }
            if (leda > 0) {
                useSubMatch=4;
            }
            request.setAttribute("leda",leda);

            //column configurations
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
            Hashtable<String,Boolean> ignores = new Hashtable<String,Boolean>();
            ignores.put("url",true);
            ignores.put("logo",true);
            ignores.put("trailer",true);
            ignores.put("rank",true);
            ignores.put("globalsales",true);

            String requestUrl = "?"+request.getQueryString();
            String requestUrlEnd = "";
            int orderStart = requestUrl.indexOf("order=");
            if (orderStart > -1) {
                int orderEnd = requestUrl.substring(orderStart).indexOf("&");
                if (orderEnd <= -1) {
                    requestUrl=requestUrl.substring(0,orderStart);
                } else {
                    requestUrlEnd=requestUrl.substring(orderStart+orderEnd);
                    requestUrl=requestUrl.substring(0,orderStart);
                }
            } else {
                requestUrl+="&";
            }

            String game = (String) request.getParameter("name");
            String year = (String) request.getParameter("year");
            String publisher = (String) request.getParameter("publisher");
            String genre = (String) request.getParameter("genre");
            String platform = (String) request.getParameter("platform");

            int searchCount=-1;
            String results = "";
            if (table.equals("games")) {
                NTreeNode<Table> rows=null;
                if (useSubMatch==1) {
                    rows = SearchResults.getInstance().masterSearch(table,limit,offset,game,
                        year,genre,platform,publisher,order,descend,1);
                    searchCount = SearchResults.getInstance().getCount(table,limit,offset,game,
                        year,genre,platform,publisher,order,descend,1);
                } else if (useSubMatch==3) {
                    rows = SearchResults.getInstance().masterSearch(table,limit,offset,game,
                        year,genre,platform,publisher,order,descend,3);
                    searchCount = SearchResults.getInstance().getCount(table,limit,offset,game,
                        year,genre,platform,publisher,order,descend,3);
                } else if (useSubMatch==4) {
                    rows = SearchResults.getInstance().masterSearch(table,limit,offset,game,
                        year,genre,platform,publisher,order,descend,4,leda);
                    searchCount = SearchResults.getInstance().getCount(table,limit,offset,game,
                        year,genre,platform,publisher,order,descend,4,leda);
                } else {
                    rows = SearchResults.getInstance().masterSearch(table,limit,offset,game,
                        year,genre,platform,publisher,order,descend,2);
                    searchCount = SearchResults.getInstance().getCount(table,limit,offset,game,
                        year,genre,platform,publisher,order,descend,2);
                }

                results+=ntreeToHtml(rows,request,null,links,images,externalLinks,ignores);
            } else {
                ArrayList<HashMap<String,String>> rows=null;
                if (useSubMatch==1) {
                    rows = SearchResults.getInstance().search(table,limit,offset,game,
                        year,genre,platform,publisher,order,descend,1);
                    searchCount = SearchResults.getInstance().getCount(table,limit,offset,game,
                        year,genre,platform,publisher,order,descend,1);
                } else if (useSubMatch==3) {
                    rows = SearchResults.getInstance().search(table,limit,offset,game,
                        year,genre,platform,publisher,order,descend,3);
                    searchCount = SearchResults.getInstance().getCount(table,limit,offset,game,
                        year,genre,platform,publisher,order,descend,3);
                } else if (useSubMatch==4) {
                    rows = SearchResults.getInstance().search(table,limit,offset,game,
                        year,genre,platform,publisher,order,descend,4,leda);
                    searchCount = SearchResults.getInstance().getCount(table,limit,offset,game,
                        year,genre,platform,publisher,order,descend,4,leda);
                } else {
                    rows = SearchResults.getInstance().search(table,limit,offset,game,
                        year,genre,platform,publisher,order,descend,2);
                    searchCount = SearchResults.getInstance().getCount(table,limit,offset,game,
                        year,genre,platform,publisher,order,descend,2);
                }

                for (HashMap<String,String> row : rows) {
                    results+=rowToHtml(row,request,table,links,images,externalLinks,ignores);
                }
            }
            request.setAttribute("searchCount",searchCount);

            String nextJSP = request.getParameter("nextPage");
            if (nextJSP == null) {
                nextJSP = "/search/index.jsp";
            } else {
                nextJSP="/"+nextJSP;
            }

            request.setAttribute("searchResults",results);
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP); 
            dispatcher.forward(request,response);
            long endTime = System.nanoTime();
            Long elapsedTime = endTime - startTime;
            OutputStream out = new BufferedOutputStream(Files.newOutputStream(
                        Paths.get("./ts.txt"), CREATE, APPEND));
            byte data[] = (elapsedTime.toString()+"\n").getBytes();
            out.write(data);
            out.flush();
            out.close();
        }
        catch (SQLExceptionHandler ex) {
            PrintWriter out = response.getWriter();
            out.println("<HTML>" +
                    "<HEAD><TITLE>" +
                    "gamedb: Error" +
                    "</TITLE></HEAD>\n<BODY>" +
                    "<P>");
            out.println(ex.getErrorMessage()+"<br />\n"+returnLink);
            out.println("</P></BODY></HTML>");
            out.close();
        }  // end catch SQLException

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
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
        doGet(request, response);
    }
}
