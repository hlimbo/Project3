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

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        HashMap<String,String> params = ParameterParse.getQueryParameters(request.getQueryString());
        if (params.containsKey("command")) {
            String command = params.get("command");
            PrintWriter writer=null;
            try {
                writer = response.getWriter();
                switch (command) {
                    case "add_game":
                        DashBoardCommands.addGame(params.get("name"),
                                params.get("year"),params.get("price"),params.get("platform"),
                                params.get("publisher"),params.get("genre"));
                        writer.println("<p>Successfully added game.</p>");
                        break;
                    case "insert_publisher":
                        DashBoardCommands.insertPublisher(params.get("publisher"));
                        writer.println("<p>Successfully added publisher.</p>");
                        break;
                    case "meta":
                        LinkedHashMap<String, HashMap<String, String>> meta = DashBoardCommands.getMeta();
                        for (String table : meta.keySet()) {
                            writer.write("<h3>);
                            writer.write(table);
                            writer.write("</h3>");
                            for (HashMap<String, String> column : meta.get(table)) {
                                HtmlFormat.printHtmlRow (writer,column);
                            }
                        }
                        break;
                    default:
                        break;
                }
            } catch (SQLExceptionHandler ex) {
                writer.println(SQLExceptionFormat.toHtml(ex));
            } catch (SQLException ex) {
                writer.println(SQLExceptionFormat.toHtml(ex));
            } catch (java.lang.Exception ex)  {
                writer.println(ExceptionFormat.toHtml(ex));
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
