package gamesite.servlet;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class SearchServlet extends HttpServlet
{
    public String getServletInfo()
    {
        return "Servlet connects to MySQL database and displays result of a SELECT";
    }

    private String addSearchTerm (HttpServletRequest request, String term, boolean useSubMatch) {
            String value = (String) request.getParameter(term);
            String searchTerm = "";
            if (value != null && value.trim() != "") {
                if (!useSubMatch) {
                    for (String subvalue : value.split(" ")) {
                        searchTerm+=" AND ";
                        searchTerm+=term+" LIKE ?";
                    }
                } else {
                    searchTerm+=" AND ";
                    searchTerm+=term+" LIKE ?";
                }
            }
            return searchTerm;
    }

    private int setSearchTerm (HttpServletRequest request, String term, PreparedStatement statement, 
            int offset, boolean useSubMatch) throws SQLException {
            String value = (String) request.getParameter(term);
            String searchTerm = "";
            if (value != null && value.trim() != "") {
                if (!useSubMatch) {
                    for (String subvalue : value.split(" ")) {
                        statement.setString(offset,"%"+subvalue+"%");
                        offset+=1;
                    }
                } else {
                    statement.setString(offset,value);
                    offset+=1;
                }
            }
            return offset;
    }

    private String cartButton (String id, String quantity, HttpServletRequest request) {
        String button = "<tr><td><form action=\"/ShoppingCart/view-shopping-cart\" method=\"GET\">";
        button+="<input type=\"HIDDEN\" name=id value=\""+id+"\" \\>";
        button+="<input type=\"HIDDEN\" name=\"quantity\" value=\""+quantity+"\" \\>";
        try {
            button+="<input type=\"HIDDEN\" name=\"previousPage\" value=\""
                +URLEncoder.encode(request.getRequestURI(), "UTF-8")+"%3F"
                +URLEncoder.encode(request.getQueryString(), "UTF-8")+"\" \\>";
        } catch (UnsupportedEncodingException e){
        }
        button+="<input type=\"SUBMIT\" value=\"Add to Shopping Cart\" \\>";
        button+="</form></td></tr>";
        return button;
    }

	private static String tableRow (ResultSet result, Hashtable<String,Boolean> link,
            Hashtable<String,Boolean> images, Hashtable<String,Boolean> externalLinks, Hashtable<String,Boolean> ignores) throws SQLException {
		ResultSetMetaData meta = result.getMetaData();
	    String resString = "";
		//resString+="<tr>";
		for (int i=1;i<=meta.getColumnCount();++i) {
			int type = meta.getColumnType(i);
            String table = meta.getTableName(i);
			String typeName = meta.getColumnTypeName(i);
            boolean handled = false;
            String value="";
			switch(typeName.toUpperCase()) {
			case "YEAR":
                if (result.getString(i) != null) {
				    value+=result.getString(i).substring(0,4);
                    handled=true;
                }
				break;
			}
			if (!handled) {
				switch(type) {
				case Types.INTEGER:
                    if (result.getString(i) != null) {
					    value+=result.getInt(i);
                    }
					break;
				default:
                    if (result.getString(i) != null) {
					    value+=result.getString(i);
                    }
					break;
				}
			}
            //handle nulls and empty values here
			String colName = meta.getColumnName(i);
            if (ignores.containsKey(colName) && ignores.get(colName)) {
                continue;
            }
            if (value==null || value.trim().compareTo("") == 0) {
                if (images.containsKey(colName) && images.get(colName)){
                    /*resString+="<td><img src=\""
                        +"http://upload.wikimedia.org/wikipedia/"
                        +"commons/thumb/5/51/"
                        +"Star_full.svg/11px-Star_full.svg.png\" /></td>"*/;
                    resString+="<td><img src=\"/art/game_nologo.svg\"/></td>";
                }
                continue;
            }
            if  (link.containsKey(colName) && link.get(colName)) {
                try {
			        resString+="<td><a href=\"/display/query?table="+table+"&columnName="+colName+
                        "&"+colName+"="+URLEncoder.encode(value,"UTF-8")+"\">";
                } catch (UnsupportedEncodingException error) {
			        resString+="<td><a href=\"/display/query?table="+table+"&columnName="+colName+
                        "&"+colName+"="+value.replaceAll("[^\\w]","_")+"\">";
                }
                resString+=value;
                resString+="</a></td>";
            } else if  (externalLinks.containsKey(colName) && externalLinks.get(colName)) {
			    resString+="<td><a href=\"http://"+value+"\">"+value+"</a></td>";
            } else if (images.containsKey(colName) && images.get(colName)){
                resString+="<td><img src=\"http://"+value+"\" /></td>";
            } else {
			    resString+="<td>";
                resString+=value;
			    resString+="</td>";
            }
		}
		//resString+="</tr>";
        return resString;
	}

    // Use http GET

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
        String loginUser = "user";
        String loginPasswd = "password";
        String loginUrl = "jdbc:mysql://localhost:3306/gamedb";

        response.setContentType("text/html");    // Response mime type

        String query = "";
        String returnLink = "<a href=\"/\"> Return to home </a>";
        try
        {
            //Class.forName("org.gjt.mm.mysql.Driver");
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            // Declare our statement
            //Statement statement = dbcon.createStatement();
            PreparedStatement statement; 

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

            String name = (String) request.getParameter("name");
            String year = (String) request.getParameter("year");
            String publisher = (String) request.getParameter("publisher");
            String genre = (String) request.getParameter("genre");
            String platform = (String) request.getParameter("platform");
            boolean searchAll = false;
            if ((name==null || name.trim().compareTo("")==0) && (year==null || year.trim().compareTo("")==0)
                    && (publisher==null || publisher.trim().compareTo("")==0) 
                    && (genre==null || genre.trim().compareTo("")==0) 
                    && (platform==null || platform.trim().compareTo("")==0)) {
                searchAll = true;
            }
            if (!searchAll) {
                String masterTable = "platforms_of_games NATURAL JOIN genres_of_games NATURAL JOIN publishers_of_games";
                //duplicates due to games on multiple platforms, with multiple genres, or etc...
                query = "SELECT DISTINCT "+table+".* FROM games, publishers, platforms, genres, "+masterTable+" WHERE "
                    +"games.id=game_id AND publishers.id=publisher_id AND platforms.id=platform_id AND genres.id=genre_id";
                query+=addSearchTerm(request,"name",useSubMatch);
                query+=addSearchTerm(request,"year",useSubMatch);
                query+=addSearchTerm(request,"publisher",useSubMatch);
                query+=addSearchTerm(request,"genre",useSubMatch);
                query+=addSearchTerm(request,"platform",useSubMatch);
            } else {
                query = "SELECT "+table+".* FROM "+table;
            }
            String originalQuery = query;
            //query = query.replaceFirst("DISTINCT "+table+"\\.\\*","COUNT(DISTINCT "+table+".*)");
            query = "SELECT COUNT(*) FROM ("+query+") AS countable";
            statement = dbcon.prepareStatement(query);
            query = originalQuery;
            if (descend) {
                query+=" ORDER BY ISNULL( "+table+"."+order+"), "+table+"."+order+" DESC, "
                    +table+".id LIMIT "+limit+" OFFSET "+offset;
            } else {
                query+=" ORDER BY ISNULL( "+table+"."+order+"), "+table+"."+order+" ASC, "
                    +table+".id LIMIT "+limit+" OFFSET "+offset;
            }

            int statementOffset = 1;
            statementOffset = setSearchTerm(request,"name",statement,statementOffset,useSubMatch);
            statementOffset = setSearchTerm(request,"year",statement,statementOffset,useSubMatch);
            statementOffset = setSearchTerm(request,"publisher",statement,statementOffset,useSubMatch);
            statementOffset = setSearchTerm(request,"genre",statement,statementOffset,useSubMatch);
            statementOffset = setSearchTerm(request,"platform",statement,statementOffset,useSubMatch);
            ResultSet rs = statement.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            request.setAttribute("searchCount",count);

            // Perform the query
            statement = dbcon.prepareStatement(query);
            statementOffset = 1;
            statementOffset = setSearchTerm(request,"name",statement,statementOffset,useSubMatch);
            statementOffset = setSearchTerm(request,"year",statement,statementOffset,useSubMatch);
            statementOffset = setSearchTerm(request,"publisher",statement,statementOffset,useSubMatch);
            statementOffset = setSearchTerm(request,"genre",statement,statementOffset,useSubMatch);
            statementOffset = setSearchTerm(request,"platform",statement,statementOffset,useSubMatch);
            rs = statement.executeQuery();

            String results = "";
            results+="<TABLE border>";

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

            ResultSetMetaData meta = rs.getMetaData();
            boolean noResults = false;
            if (!rs.next()) {
                noResults=true;
            } else {
                rs.beforeFirst();
            }
            results+="<tr>";
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
            for (int i=1;i<=meta.getColumnCount();++i) {
                String column = meta.getColumnName(i);
                if (ignores.containsKey(column) && ignores.get(column)) {
                    continue;
                }
                results+="<td><a class=\"sortColumn\" href=\"/search/query"+requestUrl+"order="+column
                    +requestUrlEnd+"\">"+column+"</a></td>\n";
            }
            results+="</tr>";
            if (table.trim().compareToIgnoreCase("games")==0) {
                ArrayList<String> records= new ArrayList<String>();
                ArrayList<Integer> gameIDs = new ArrayList<Integer>();
                //get game fields
                while (rs.next())
                {
                    records.add("<tr>"+tableRow(rs,links,images,externalLinks,ignores)+"</tr>");
                    gameIDs.add(rs.getInt(1));
                }
                //get publishers
                for (int i=0;i<gameIDs.size();++i) {
                    //query="SELECT DISTINCT publisher FROM publishers JOIN publishers_of_games ON id=publisher_id WHERE game_id=?";
                    query="SELECT DISTINCT publisher, platform FROM publishers "
                        +"JOIN publishers_of_games ON publishers.id=publisher_id "
                        +"JOIN platforms ON platforms.id=platform_id WHERE game_id=?";
                    PreparedStatement pubStatement =dbcon.prepareStatement(query);
                    pubStatement.setInt(1,gameIDs.get(i));
                    rs = pubStatement.executeQuery();
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
                }
                //get genres
                for (int i=0;i<gameIDs.size();++i) {
                    query="SELECT genre FROM genres JOIN genres_of_games ON id=genre_id WHERE game_id=?";
                    PreparedStatement genStatement =dbcon.prepareStatement(query);
                    genStatement.setInt(1,gameIDs.get(i));
                    rs = genStatement.executeQuery();
                    records.set(i,records.get(i)+"<tr><td>genres: </td><td><ul>");
                    while (rs.next()) {
                        records.set(i,records.get(i)+tableRow(rs,links,
                                    images,externalLinks,
                                    ignores).replaceAll("<td>","<li>").replaceAll("</td>","</li>"));
                    }
                    records.set(i,records.get(i)+"</ul></td></tr>");
                }
                int i=0;
                for (String record : records) {
                    results+=record;
                    results+=cartButton(Integer.toString(gameIDs.get(i)),"1",request);
                    ++i;
                }
            } else {
                while (rs.next())
                {
                    results+="<tr>"+tableRow(rs,links,images,externalLinks,ignores)+"</tr>";
                    //results+=cartButton(Integer.toString(rs.getInt(1)),"1",request);
                }
            }
            results+="</TABLE>";

            rs.close();
            statement.close();
            dbcon.close();
            String nextJSP = request.getParameter("nextPage");
            if (nextJSP == null) {
                nextJSP = "/search/index.jsp";
            } else {
                nextJSP="/"+nextJSP;
            }
            if (noResults) {
                results="";
            }
            request.setAttribute("searchResults",results);
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
            out.println(" in sql expression "+query+"<br />\n"+returnLink);
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
