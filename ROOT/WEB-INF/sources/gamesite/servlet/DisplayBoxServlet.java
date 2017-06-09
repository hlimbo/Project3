package gamesite.servlet;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class DisplayBoxServlet extends HttpServlet
{
    public String getServletInfo()
    {
        return "Servlet connects to MySQL database and displays a single record within the database";
    }

    private String cartButton (String id, String name,String price, String quantity, HttpServletRequest request) {
        String button = "<div><span><form action=\"/ShoppingCart/view-shopping-cart\" method=\"GET\">";
        button+="<input type=\"HIDDEN\" name=id value=\""+id+"\" \\>";
        button+="<input type=\"HIDDEN\" name=\"quantity\" value=\""+quantity+"\" \\>";
        try {
            button+="<input type=\"HIDDEN\" name=\"previousPage\" value=\""
                +URLEncoder.encode(request.getRequestURI(), "UTF-8")+"%3F"
                +URLEncoder.encode(request.getQueryString(), "UTF-8")+"\" \\>";
        } catch (UnsupportedEncodingException e){
        }
        button+="<input type=\"SUBMIT\" value=\"Add to Shopping Cart\" \\>";
        button+="</form></span></div>";
        return button;
    }

    private String getValue (String table, String column, String fieldValue, 
            Hashtable<String,Boolean> fieldIgnores, Hashtable<String,Boolean> links,
            Hashtable<String,Boolean> images, Hashtable<String,Boolean> externalLinks) throws UnsupportedEncodingException {
        if (fieldIgnores.containsKey(column) && fieldIgnores.get(column)
                && (table.compareToIgnoreCase("games") != 0 || column.compareToIgnoreCase("id") != 0)
                && (table.compareToIgnoreCase("publishers") !=0 || column.compareToIgnoreCase("logo") !=0)) {
            return null;
        } else if (links.containsKey(column) && links.get(column)) {
            String fieldUrl = "/display/query?table="+table+
                "&columnName="+column+"&"+column+"="+
                URLEncoder.encode(fieldValue,"UTF-8");
            return("<span>"+column+": <a href=\""+fieldUrl+"\">"+fieldValue+"</a></span><br />");
        } else if (images.containsKey(column) && images.get(column)){
            if (fieldValue.startsWith("/art")) {
                return ("<span><img src=\""+fieldValue+"\" /></span><br />");
            } else {
                return ("<span><img src=\"http://"+fieldValue+"\" /></span><br />");
            }
        } else if (externalLinks.containsKey(column) && externalLinks.get(column)) {
            return("<span>"+column+": <a href=\"http://"+fieldValue+"\">"+fieldValue+"</a></span><br />");
        }else {
            return ("<span>"+column+": "+fieldValue+"</span><br />");
        }
    }

    // Use http GET

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
        String loginUser = "user";
        String loginPasswd = "password";
        String loginUrl = "jdbc:mysql://localhost:3306/gamedb";
        String returnLink = "<a href=\"/\"> Return to home </a>";

        response.setContentType("text/html");    // Response mime type

        try
        {
            //Class.forName("org.gjt.mm.mysql.Driver");
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            // Declare our statement
            DatabaseMetaData dbmeta = dbcon.getMetaData();
            ResultSet tableMeta = dbmeta.getTables(dbcon.getCatalog(),null,"%",null);
            ArrayList<String> tables = new ArrayList<String>();
            while (tableMeta.next()) {
                tables.add(tableMeta.getString("TABLE_NAME"));
            }
            tableMeta.close();
            Statement statement;

            String table = "games";
            table=table.replaceAll("[^\\w]","_");
            String column = "id";
            column=column.replaceAll("[^\\w]","_");
            String columnValue= (String) request.getParameter(column);
            String offset="0";
            String limit = "1";
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
            } catch (NumberFormatException ex) {
                limit = limitMax.toString();
            }

            String masterTable = "platforms_of_games NATURAL JOIN genres_of_games NATURAL JOIN publishers_of_games";
            //duplicates due to games on multiple platforms, with multiple genres, or etc...
            String query = "SELECT DISTINCT "+table+".* FROM games, publishers, platforms, genres, "+masterTable+" WHERE "
                +"games.id=game_id AND publishers.id=publisher_id AND platforms.id=platform_id AND genres.id=genre_id AND "+table+"."+column+"='"+columnValue+"'";
            statement = dbcon.createStatement();

            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            String id;
            String gameID = "";
            String gameName = "";
            String gamePrice = "";
            if (rs.next()) {
                id = rs.getString(1);
                if (table.trim().compareToIgnoreCase("games") == 0) {
                    gameID=id;
                    gameName=rs.getString(3);
                    gamePrice=rs.getString(6);
                }
            } else {
                rs.close();
                statement.close();
                dbcon.close();
                return;
            }
            //Convert to column name of a relation table by naming convention of database. 
            //For example, publishers.id -> publisher_id
            String tableIDField = table.substring(0,table.length()-1)+"_id";
            String tableIDCond = tableIDField+"="+id;

            statement.close();
            rs.close();
            String results = ""; //"<TABLE border>";
            //Sets which fields to not display to client. Configuration option
            Hashtable<String,Boolean> fieldIgnores = new Hashtable<String,Boolean>();
            fieldIgnores.put("id",true);
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

            //results+="<div><span>"+columnValue+"</span></div><div>";
            //results+="<div>";
            query="SELECT DISTINCT * FROM "+table+" WHERE id="+id;
            statement=dbcon.createStatement();
            rs = statement.executeQuery(query);
            while (rs.next()) {
                results+="<div><span>";
                ResultSetMetaData meta = rs.getMetaData();
                for (int i=1;i<=meta.getColumnCount();++i) {
                    String columnName = meta.getColumnName(i);
                    String fieldValue = rs.getString(i);
                    if (fieldValue == null) {
                        if (images.containsKey(columnName) && images.get(columnName)) {
                            /*fieldValue="upload.wikimedia.org/wikipedia/"
                                +"commons/thumb/5/51/"
                                +"Star_full.svg/11px-Star_full.svg.png";*/
                            fieldValue="/art/game_nologo.svg";
                        } else {
                            continue;
                        }   
                    }
                    if (table.trim().compareToIgnoreCase("games")==0 && columnName.trim().compareToIgnoreCase("url")==0) {
                        fieldValue = getValue(table,"trailer",fieldValue,fieldIgnores,links,images,externalLinks);
                    } else {
                        fieldValue = getValue(table,columnName,fieldValue,fieldIgnores,links,images,externalLinks);
                    }
                    if (fieldValue == null) {
                        continue;
                    }
                    if (i==1) {
                        //fieldValue=fieldValue.substring(4,fieldValue.length()-5);
                        results+=fieldValue+"</span><span>"; //<TABLE border>";
                    } else {
                        results+=fieldValue;
                    }
                }
                results+="</span></div>\n"; //</TABLE>\n";
            }
            int gameCount = -1;
            //request.setAttribute("displayGameCount",gameCount);
            for (String tbl : tables) {
                //by convention of SQL schema, relation tables contains name
                //of entity tables. For example, publishers_of_games contains both
                //publishers and games within its name
                if (tbl.indexOf(table) != -1 && tbl.trim().compareToIgnoreCase(table.trim()) != 0) {
                    results+="<div>";
                    results+="<span>"+tbl.trim().replace("_of_","").replace(table,"")+":</span>";
                    if (table.compareToIgnoreCase("games")!=0 && tbl.indexOf("games") != -1) {
                        query="SELECT DISTINCT * FROM "+tbl+" WHERE "
                            +tableIDCond;
                        String originalQuery = query;
                        query = "SELECT COUNT(*) FROM ("+query+") AS countable";
                        Statement gameCountStatement = dbcon.createStatement();
                        ResultSet gameCountRs= gameCountStatement.executeQuery(query);
                        gameCountRs.next();
                        gameCount=gameCountRs.getInt(1);
                        gameCountStatement.close();
                        gameCountRs.close();
                        query=originalQuery+" LIMIT "+limit+" OFFSET "+offset;;
                    } else {
                        query="SELECT DISTINCT * FROM "+tbl+" WHERE "+tableIDCond;
                    }
                    //request.setAttribute("displayGameCount",gameCount);
                    statement=dbcon.createStatement();
                    rs = statement.executeQuery(query);
                    results += "<span>"; //<TABLE border>";
                    ArrayList<String> fields = new ArrayList<String>();
                    ArrayList<String> checkouts = new ArrayList<String>();

                    int row = 0;
                    while (rs.next()) {
                        ResultSetMetaData meta = rs.getMetaData();
                        for (int i=1;i<=meta.getColumnCount();++i) {
                            String columnName = meta.getColumnName(i);
                            if (columnName.compareToIgnoreCase(tableIDField) != 0) {
                                //By naming convention of database
                                String parentTable = columnName.substring(0,columnName.length()-3)+"s";
                                query= "SELECT DISTINCT * FROM "+parentTable+" WHERE id='"+rs.getString(i)+"'";
                                Statement parentStatement=dbcon.createStatement();
                                ResultSet parentResult = parentStatement.executeQuery(query);
                                ResultSetMetaData parentMeta = parentResult.getMetaData();
                                while (parentResult.next()) {
                                    for (int j=1;j<=parentMeta.getColumnCount();++j) {
                                        String parentColumn = parentMeta.getColumnName(j);
                                        if (parentColumn.compareToIgnoreCase("id")==0 && parentTable.compareToIgnoreCase("games")==0) {
                                            gameID=Integer.toString(parentResult.getInt(1));
                                            gameName=parentResult.getString(3);
                                            gamePrice=parentResult.getString(6);
                                            checkouts.add(cartButton(gameID,gameName,gamePrice,"1",request));
                                        }
                                        String fieldValue = parentResult.getString(j);
                                        if (fieldValue == null) {
                                            continue;
                                        }
                                        if (parentColumn.trim().compareToIgnoreCase("url") == 0) {
                                            fieldValue = getValue(parentTable,"trailer",fieldValue,fieldIgnores,links,images,externalLinks);
                                        } else {
                                            fieldValue = getValue(parentTable,parentColumn,fieldValue,fieldIgnores,links,images,externalLinks);
                                        }
                                        if (fieldValue == null) {
                                            continue;
                                        }
                                        //if (fieldIgnores.containsKey(parentColumn) && fieldIgnores.get(parentColumn)
                                        //        && (parentTable.compareToIgnoreCase("games") != 0 || parentColumn.compareToIgnoreCase("id") != 0)) {
                                        //    continue;
                                        //} else if (links.containsKey(parentColumn) && links.get(parentColumn)) {
                                        //    String fieldUrl = "/display/query?table="+parentTable+
                                        //        "&columnName="+parentColumn+"&"+parentColumn+"="+
                                        //        URLEncoder.encode(fieldValue,"UTF-8");
                                        //    fieldValue="<span>"+parentColumn+": <a href=\""+fieldUrl+"\">"+fieldValue+"</a></span>";
                                        //} else {
                                        //    fieldValue="<span>"+parentColumn+": "+fieldValue+"</span>";
                                        //}
                                        if (fields.size() > row) {
                                            fields.set(row,fields.get(row)+fieldValue);
                                        } else {
                                            fields.add(row,fieldValue);
                                        }
                                        //if (parentTable.trim().compareToIgnoreCase("games")==0) {
                                            //checkouts.add(cartButton(gameID,gameName,gamePrice,"1",request));
                                        //}
                                    }
                                    //if (parentTable.trim().compareToIgnoreCase("games")==0) {
                                        //fields.set(row,fields.get(row)+cartButton(gameID,gameName,gamePrice,"1",request));
                                        //fields.set(row,fields.get(row));
                                        //checkouts.add(cartButton(gameID,gameName,gamePrice,"1",request));
                                    //}
                                }
                                parentResult.close();
                                parentStatement.close();
                            }
                        }
                        ++row;
                    }
                    for (int i=0;i<fields.size();++i) {
                        results+="<div>"+fields.get(i);
                        if (table.compareToIgnoreCase("games") != 0) {
                            results+=checkouts.get(i);
                        }
                        results+="</div>\n";
                    }
                    results += "</span>"; //</TABLE>";
                    results+="</div>\n";
                }
            }
            if (table.compareToIgnoreCase("games") == 0) {
                results+=cartButton(gameID,gameName,gamePrice,"1",request);
            }
            //results+="</TABLE>";

            statement.close();
            rs.close();
            dbcon.close();

            PrintWriter out = response.getWriter();
            out.println(results);
            out.close();
            //request.setAttribute("displayResults",results);
            //RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP); 
            //dispatcher.forward(request,response);

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
