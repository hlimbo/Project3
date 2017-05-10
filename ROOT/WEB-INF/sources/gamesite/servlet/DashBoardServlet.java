package gamesite.servlet;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import gamesite.utils.*;
import gamesite.model.DashBoardCommands;
import gamesite.model.SQLExceptionHandler;

public class DashBoardServlet extends HttpServlet {

    public String getServletInfo() {
        return "Servlet for employee interface";
    }

    private static String htmlHeader="<html><head><title>Employee Dashboard</title></head><body>"; 
    private static String htmlFooter="</body></html>";
    private static String xmlHeader="<?xml version=\"1.0\" encoding=\"UTF-8\"?><dashboard_status>";
    private static String xmlFooter="</dashboard_status>";

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        HashMap<String,String> params = ParameterParse.getQueryParameters(request.getQueryString());
        if (params.containsKey("command")) {
            String command = params.get("command");
            PrintWriter writer=null;
            try {
                writer = response.getWriter();
                //writer.println(htmlHeader);
                writer.println(xmlHeader);
                switch (command) {
                    case "add_game":
                        DashBoardCommands.addGame(params.get("name"),
                                params.get("year"),params.get("price"),params.get("platform"),
                                params.get("publisher"),params.get("genre"));
                        //writer.println("<p>Successfully added game.</p>");
                        break;
                    case "insert_publisher":
                        DashBoardCommands.insertPublisher(params.get("publisher"));
                        //writer.println("<p>Successfully added publisher.</p>");
                        break;
                    case "meta":
                        LinkedHashMap<String, HashMap<String, String>> meta = DashBoardCommands.getMeta();
                        for (String table : meta.keySet()) {
                            /*writer.write("<h3 class=\"meta_table\">");
                            writer.write(table);
                            writer.write("</h3>");
                            writer.write("<p class=\"meta_columns\">");
                            HtmlFormat.printHtmlRows (writer,column);
                            writer.write("</p>");*/
                            writer.write("<meta_info>");
                            writer.write("<meta_table>");
                            writer.write(table);
                            writer.write("</meta_table>");
                            XmlFormat.printXmlRows (writer,meta.get(table),"meta_columns","meta_column");
                            writer.write("</meta_info>");
                        }
                        break;
                    default:
                        break;
                }
                //writer.println(htmlFooter);
                writer.println("<status>success</status>");
                writer.println("<status code>1</status code>");
                writer.println(xmlFooter);
            } catch (SQLExceptionHandler ex) {
                //writer.println(SQLExceptionFormat.toHtml(ex));
                writer.println(SQLExceptionFormat.toXml(ex));
            } catch (SQLException ex) {
                //writer.println(SQLExceptionFormat.toHtml(ex));
                writer.println(SQLExceptionFormat.toXml(ex));
            } catch (java.lang.Exception ex)  {
                //writer.println(ExceptionFormat.toHtml(ex));
                writer.println(SQLExceptionFormat.toXml(ex));
            } finally {
                writer.close();
            }
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
        doGet(request, response);
    }
}
