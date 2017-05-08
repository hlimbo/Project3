package gamesite.servlet;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import gamesite.datastruct.*;
import gamesite.model.*;

public class SearchServlet extends HttpServlet
{
    public String getServletInfo()
    {
        return "Servlet connects to MySQL database and displays result of a SELECT";
    }


    private String cartButton (String id, String quantity, HttpServletRequest request) {
        String button = "<form class=\"cartButton\" action=\"/ShoppingCart/view-shopping-cart\" method=\"GET\">";
        button+="<input type=\"HIDDEN\" name=id value=\""+id+"\" \\>";
        button+="<input type=\"HIDDEN\" name=\"quantity\" value=\""+quantity+"\" \\>";
        try {
            button+="<input type=\"HIDDEN\" name=\"previousPage\" value=\""
                +URLEncoder.encode(request.getRequestURI(), "UTF-8")+"%3F"
                +URLEncoder.encode(request.getQueryString(), "UTF-8")+"\" \\>";
        } catch (UnsupportedEncodingException e){
        }
        button+="<input class=\"cartButtonCheckout\" type=\"SUBMIT\" value=\"Add to Shopping Cart\" \\>";
        button+="</form>";
        return button;
    }

	private static String fieldValue (String colName, String value, String table,
            Hashtable<String,Boolean> link, Hashtable<String,Boolean> images, 
            Hashtable<String,Boolean> externalLinks, Hashtable<String,Boolean> ignores) {
	    String resString = "";
        //for (Map.Entry<String,String> field : results.get(i).entrySet()) {
            //String colName = field.getKey();
            //String value = field.getValue();
            //handle nulls and empty values here
            if (ignores.containsKey(colName) && ignores.get(colName)) {
                return "";
            }
			resString+=" <span class=\""+table+"_"+colName+"\">";
            if (value==null || value.trim().compareTo("") == 0) {
                if (images.containsKey(colName) && images.get(colName)){
                    resString+="<img src=\""
                        +"http://upload.wikimedia.org/wikipedia/"
                        +"commons/thumb/5/51/"
                        +"Star_full.svg/11px-Star_full.svg.png\" />";
                }
                return "";
            }
            if  (link.containsKey(colName) && link.get(colName)) {
                try {
			        resString+="<a href=\"/display/query?table="+table+"&columnName="+colName+
                        "&"+colName+"="+URLEncoder.encode(value,"UTF-8")+"\">";
                } catch (UnsupportedEncodingException error) {
			        resString+="<a href=\"/display/query?table="+table+"&columnName="+colName+
                        "&"+colName+"="+value.replaceAll("[^\\w]","_")+"\">";
                }
                resString+=value;
                resString+="</a>";
            } else if  (externalLinks.containsKey(colName) && externalLinks.get(colName)) {
			    resString+="<a href=\"http://"+value+"\">"+value+"</a>";
            } else if (images.containsKey(colName) && images.get(colName)){
                resString+="<img src=\"http://"+value+"\" />";
            } else {
                resString+=value;
            }
			resString+="</span> ";
        //}
        return resString;
	}

    // Use http GET

    private String ntreeToHtml (NTreeNode<Table> root, 
            Hashtable<String,Boolean> link, Hashtable<String,Boolean> images, 
            Hashtable<String,Boolean> externalLinks, Hashtable<String,Boolean> ignores) {
        String result = "";
        for (HashMap<String,String> row : root.data) {
            result+="<div class=\""+root.data.name+"_row\">";
            for (String field : row.keySet()) {
                if (!field.endsWith("_id")) {
                    result+=fieldValue(field,row.get(field),root.data.name,
                            link,images,externalLinks,ignores);
                }
            }
            for (String field : row.keySet()) {
                if (field.endsWith("_id")) {
                    String childTable = QueryUtils.getTableFromRelationIdName(field);
                    for (NTreeNode<Table> child : root.children) {
                        if (child.data.name.equals(childTable)) {
                            result+=ntreeToHtml(child,link,images,externalLinks,ignores);
                        }
                    }
                }
            }
            result+="</div>\n";
        }
        return result;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
        response.setContentType("text/html");    // Response mime type

        String returnLink = "<a href=\"/\"> Return to home </a>";
        try
        {
            // Declare our statement
            //Statement statement = dbcon.createStatement();
            //PreparedStatement statement; 

            String table = (String) request.getParameter("table");
            if (table==null) {
                table="games";
            } else {
                table = table.replaceAll("[^\\w]","_");
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
            boolean useSubMatch = false;
            if (matchParameter != null && matchParameter.compareToIgnoreCase("true") == 0) {
                useSubMatch = true;
            }

            String game = (String) request.getParameter("name");
            String year = (String) request.getParameter("year");
            String publisher = (String) request.getParameter("publisher");
            String genre = (String) request.getParameter("genre");
            String platform = (String) request.getParameter("platform");

            NTreeNode<Table> rows;
            int searchCount=-1;
            if (useSubMatch) {
                rows = SearchResults.getInstance().masterSearch(table,limit,offset,game,
                    year,genre,platform,publisher,order,descend,1);
                searchCount = SearchResults.getInstance().getCount(table,limit,offset,game,
                    year,genre,platform,publisher,order,descend,1);
            } else {
                rows = SearchResults.getInstance().masterSearch(table,limit,offset,game,
                    year,genre,platform,publisher,order,descend,2);
                searchCount = SearchResults.getInstance().getCount(table,limit,offset,game,
                    year,genre,platform,publisher,order,descend,2);
            }
            request.setAttribute("searchCount",searchCount);

            String results = "";
            //results+="<TABLE border>";

            // Iterate through each row of rs
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
            results+=ntreeToHtml(rows,links,images,externalLinks,ignores);
            /* 
            results+="<tr>";
            if (!rows.data.isEmpty()) {
                //TODO get column names in table
                for (String column : rows.data.rows.values()) {
                    if (ignores.containsKey(column) && ignores.get(column)) {
                        continue;
                    }
                    results+="<td><a class=\"sortColumn\" href=\"/search/query"+requestUrl+"order="+column
                        +requestUrlEnd+"\">"+column+"</a></td>\n";
                }
            }
            results+="</tr>";*/
            /*if (table.trim().compareToIgnoreCase("games")==0) {
                ArrayList<String> records= new ArrayList<String>();
                ArrayList<Integer> gameIDs = new ArrayList<Integer>();
                //get game fields
                for (int i=0;i<rows.size();++i) {
                    records.add("<tr>"+tableRow(rows,i,"games",links,images,externalLinks,ignores)+"</tr>");
                    gameIDs.add(Integer.parseInt(rows.get(i).get("id")));
                }*/
                //get publishers
                /*Connection dbcon = QueryUtils.createConn();
                for (int i=0;i<gameIDs.size();++i) {
                    //query="SELECT DISTINCT publisher FROM publishers JOIN publishers_of_games ON id=publisher_id WHERE game_id=?";
                    query="SELECT DISTINCT publisher, platform FROM publishers "
                        +"JOIN publishers_of_games ON publishers.id=publisher_id "
                        +"JOIN platforms ON platforms.id=platform_id WHERE game_id=?";
                    PreparedStatement pubStatement =dbcon.prepareStatement(query);
                    pubStatement.setInt(1,gameIDs.get(i));
                    ResultSet rs = pubStatement.executeQuery();
                    records.set(i,records.get(i)+"<tr><td>publishers: </td><td>");
                    while (rs.next()) {
                        records.set(i,records.get(i)+"\n<ul style=\""
                                +"list-style-type:none;\">"
                                +tableRow(rs,links,
                                    images,externalLinks,
                                    ignores).replaceAll("<td>","<li style=\"display:inline;margin 20px\">"
                                        ).replaceAll("</td>","</li> ")+"</ul>");
                    }
                    records.set(i,records.get(i)+"</td></tr>");
                    rs.close()
                }
                //get genres
                for (int i=0;i<gameIDs.size();++i) {
                    query="SELECT genre FROM genres JOIN genres_of_games ON id=genre_id WHERE game_id=?";
                    PreparedStatement genStatement =dbcon.prepareStatement(query);
                    genStatement.setInt(1,gameIDs.get(i));
                    ResultSet rs = genStatement.executeQuery();
                    records.set(i,records.get(i)+"<tr><td>genres: </td><td><ul>");
                    while (rs.next()) {
                        records.set(i,records.get(i)+tableRow(rs,links,
                                    images,externalLinks,
                                    ignores).replaceAll("<td>","<li>").replaceAll("</td>","</li>"));
                    }
                    records.set(i,records.get(i)+"</ul></td></tr>");
                    rs.close()
                }
                dbcon.close()*/
                /*int i=0;
                for (String record : records) {
                    results+=record;
                    results+=cartButton(Integer.toString(gameIDs.get(i)),"1",request);
                    ++i;
                }*/
            /*} else {
                for (int i=0;i<rows.size();++i) {
                    results+="<tr>"+tableRow(rows,i,table,links,images,externalLinks,ignores)+"</tr>";
                }
            }*/
            //results+="</TABLE>";

            String nextJSP = request.getParameter("nextPage");
            if (nextJSP == null) {
                nextJSP = "/search/index.jsp";
            } else {
                nextJSP="/"+nextJSP;
            }
            if (rows.data.isEmpty()) {
                results="";
            }
            request.setAttribute("searchResults",results);
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP); 
            dispatcher.forward(request,response);

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
            return;
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
        doGet(request, response);
    }
}
