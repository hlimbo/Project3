package gamesite.servlet;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import gamesite.utils.*;
import gamesite.model.DashBoardCommands;

public class DashBoardServlet extends HttpServlet {

    public String getServletInfo() {
        return "Servlet for employee interface";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        //TODO Parse parameters and then send to another class to handle
        //the processing of the commands
        HashMap<String,String> params = ParameterParse.getQueryParameters(request.getQueryString());
        if (params.containsKey("command") {
            String command = params.get("command");
            PrintWriter writer=null;
            try {
                PrintWriter writer = response.getWriter();
                switch (command) {
                    case "add_game":
                        DashBoardCommands.addGame(params.get("name"),
                                params.get("year"),params.get("price"),params.get("platform"),
                                params.get("publisher"),params.get("genre"));
                        break;
                    case "insert_publisher":
                        DashBoardCommands.insertPublisher(params.get("publisher"));
                        break;
                    case "meta":
                        DashBoardCommands.getMeta();
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
