package gamesite.servlet;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import gamesite.utils.*;

public class DisplayServletXml extends HttpServlet
{
    public String getServletInfo()
    {
        return "Servlet connects to MySQL database and displays a single record within the database";
    }

    private String getValue (String table, String column, String fieldValue, 
            Hashtable<String,Boolean> fieldIgnores, Hashtable<String,Boolean> links,
            Hashtable<String,Boolean> images, Hashtable<String,Boolean> externalLinks) throws UnsupportedEncodingException {
        if (fieldIgnores.containsKey(column) && fieldIgnores.get(column)
                && (table.compareToIgnoreCase("games") != 0 || column.compareToIgnoreCase("id") != 0)
                && (table.compareToIgnoreCase("publishers") !=0 || column.compareToIgnoreCase("logo") !=0)) {
            return null;
        } else if (links.containsKey(column) && links.get(column)) {
            String fieldUrl = XmlFormat.escapeXml("/display/query?table="+table+
                "&columnName="+column+"&"+column+"="+
                URLEncoder.encode(fieldValue,"UTF-8"));
            return("<field><column>"+column+"</column><a><href>"+fieldUrl+"</href><atext>"+fieldValue+"</atext></a></field>");
        } else if (images.containsKey(column) && images.get(column)){
            if (fieldValue.startsWith("/art")) {
                return ("<field><img>"+fieldValue+"</img></field>");
            } else {
                return ("<field><img>http://"+fieldValue+"</img></field>");
            }
        } else if (externalLinks.containsKey(column) && externalLinks.get(column)) {
            return("<field><column>"+column+"</column><value><a><href>http://"+fieldValue+"</href><atext>"+fieldValue+"</atext></a></value></field>");
        }else {
            return ("<field><column>"+column+"</column><value>"+fieldValue+"</value></field>");
        }
    }

    // Use http GET

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
        String loginUser = "user";
        String loginPasswd = "password";
        String loginUrl = "jdbc:mysql://localhost:3306/gamedb";

        response.setContentType("text/xml");    // Response mime type

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

            String table = (String) request.getParameter("table");
            table=table.replaceAll("[^\\w]","_");
            String column = (String) request.getParameter("columnName");
            column=column.replaceAll("[^\\w]","_");
            String columnValue= (String) request.getParameter(column);
            String results = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><display>";
            if (table==null) {
                table="games";
            }
            String offset = (String) request.getParameter("offset");
            if (offset==null) {
                offset="0";
            } else {
                offset = offset.replaceAll("[\\D]","");
            }
            results+="<offset>";
            try {
                results+=offset;
            } catch (NumberFormatException ex) {
                results+="-1";
            }
            results+="</offset>";
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
            results+="<limit>";
            try {
                Integer lim = Integer.parseInt(limit);
                if (lim > limitMax) {
                    lim=limitMax;
                } else if (lim < 1) {
                    lim=1;
                }
                limit = lim.toString();
                results+=limit;
            } catch (NumberFormatException ex) {
                limit = limitMax.toString();
                results+=limit;
            }
            results+="</limit>";

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

            //results+="<row><field>"+columnValue+"</field></row><row>";
            //results+="<row>";
            query="SELECT DISTINCT * FROM "+table+" WHERE id="+id;
            statement=dbcon.createStatement();
            rs = statement.executeQuery(query);
            while (rs.next()) {
                results+="<row>";
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
                        results+=fieldValue;
                    } else {
                        results+=fieldValue;
                    }
                }
                results+="</row>\n"; //</display>\n";
            }
            int gameCount = -1;
            for (String tbl : tables) {
                //by convention of SQL schema, relation tables contains name
                //of entity tables. For example, publishers_of_games contains both
                //publishers and games within its name
                if (tbl.indexOf(table) != -1 && tbl.trim().compareToIgnoreCase(table.trim()) != 0) {
                    //results+="<table>";
                    results += "<entity>";
                    results+="<table>"+tbl.trim().replace("_of_","").replace(table,"")+"</table>";
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
                    statement=dbcon.createStatement();
                    rs = statement.executeQuery(query);
                    results+="<gameCount>";
                    results+=gameCount;
                    results+="</gameCount>";
                    ArrayList<String> fields = new ArrayList<String>();

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
                                        if (fields.size() > row) {
                                            fields.set(row,fields.get(row)+fieldValue);
                                        } else {
                                            fields.add(row,fieldValue);
                                        }
                                    }
                                }
                                parentResult.close();
                                parentStatement.close();
                            }
                        }
                        ++row;
                    }
                    for (int i=0;i<fields.size();++i) {
                        results+="<row>"+fields.get(i);
                        results+="</row>\n";
                    }
                    results += "</entity>\n";
                }
            }
            results+="</display>";

            statement.close();
            rs.close();
            dbcon.close();

            PrintWriter out = response.getWriter();
            out.println(results);
            out.close();

        }
        catch (SQLException ex) {
            PrintWriter out = response.getWriter();
            out.println(SQLExceptionFormat.toXml(ex));
            out.close();
        }  // end catch SQLException
        catch (UnsupportedEncodingException ex) {
            PrintWriter out = response.getWriter();
            out.println(ExceptionFormat.toXml(ex));
            out.close();
            return;
        }
        catch(java.lang.Exception ex)
        {
            PrintWriter out = response.getWriter();
            out.println(ExceptionFormat.toXml(ex));
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
