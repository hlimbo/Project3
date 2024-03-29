package gamesite.servlet;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import gamesite.utils.*;
import gamesite.utils.LoginHandler;
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

    public void writeSuccess(PrintWriter writer) {
        writer.println("<status>success</status>");
        writer.println("<status_code>1</status_code>");
    }

    public void writeFailure(PrintWriter writer, String code, String msg) {
        writer.println("<status>failure</status>");
        writer.println("<status_code>"+code+"</status_code>");
        writer.println("<message>"+msg+"</message>");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        HashMap<String,String> params = ParameterParse.getQueryParameters(request.getQueryString());
        if (params.containsKey("command")) {
            String command = params.get("command");
            PrintWriter writer=null;
            try {
                writer = response.getWriter();
                writer.println(xmlHeader);
                switch (command) {
                    case "add_game":
                        Integer status = DashBoardCommands.addGame(params.get("name"),
                                params.get("year"),params.get("price"),params.get("platform"),
                                params.get("publisher"),params.get("genre"));
                        switch(status) {
                            case 1:
                                writeSuccess(writer);
                                break;
                            case -2:
                                writeFailure(writer,status.toString()
                                        ,"Either invalid year or price value");
                                break;
                        }
                        break;
                    case "insert_publisher":
                        Integer inserted = DashBoardCommands.insertPublisher(params.get("publisher"));
                        if (inserted==1) {
                            writeSuccess(writer);
                        } else {
                            switch(inserted) {
                                case -2:
                                    writeFailure(writer,inserted.toString()
                                        ,"Invalid year value");
                                    break;
                                case -1:
                                    writeFailure(writer,inserted.toString()
                                        ,"Publisher can not be empty");
                                    break;
                                case 2:
                                    writeFailure(writer,inserted.toString()
                                        ,"Publisher already exists");
                                    break;
                                default:
                                    writeFailure(writer,inserted.toString()
                                        ,"Unexpected insertion error");
                                    break;
                            }
                        }
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
                        writeSuccess(writer);
                        break;
                    default:
                        writer.println("<status>unknown command</status>");
                        writer.println("<status_code>-1</status_code>");
                        break;
                }
            } catch (SQLExceptionHandler ex) {
                writer.println(SQLExceptionFormat.toXml(ex));
            } catch (SQLException ex) {
                writer.println(SQLExceptionFormat.toXml(ex));
            } catch (java.lang.Exception ex)  {
                writer.println(SQLExceptionFormat.toXml(ex));
            } finally {
                writer.println(xmlFooter);
                writer.close();
            }
        } else if (params.containsKey("email") && params.containsKey("password")) {
            PrintWriter writer = null;
            try {
                writer = response.getWriter();
			    int login = LoginHandler.login(request,response,"employees");
                writer.println(xmlHeader);
                switch (login) {
                    case 1:
                        writeSuccess(writer);
                        break;
                    case -1:
                        writeFailure(writer,"-1","Please complete the ReCaptcha");
                        break;
                    case -2:
                        writeFailure(writer,"-2","Invalid email or password");
                        break;
                }
                writer.println(xmlFooter);
            } catch (SQLExceptionHandler ex) {
                writer.println(SQLExceptionFormat.toXml(ex));
            } catch (SQLException ex) {
                writer.println(SQLExceptionFormat.toXml(ex));
            } catch (java.lang.Exception ex)  {
                writer.println(SQLExceptionFormat.toXml(ex));
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
        doGet(request, response);
    }
}
