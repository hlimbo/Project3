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

public class LoginServletXml extends HttpServlet {

    public String getServletInfo() {
        return "Servlet for employee interface";
    }

    private static String xmlHeader="<?xml version=\"1.0\" encoding=\"UTF-8\"?><login_status>";
    private static String xmlFooter="</login_status>";

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
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            if (params.containsKey("email") && params.containsKey("password")) {
                //writer = response.getWriter();
		        int login = LoginHandler.loginNoCaptcha(request,response,"customers");
                writer.println(xmlHeader);
                switch (login) {
                    case 1:
                        writeSuccess(writer);
                        break;
                    case -1:
                        //This case should never be reached, but included for
                        //completeness and error checking
                        writeFailure(writer,"-1","Please complete the ReCaptcha");
                        break;
                    case -2:
                        writeFailure(writer,"-2","Invalid email or password");
                        break;
                }
                writer.println(xmlFooter);
            } else if (params.containsKey("email")) {
                writeFailure(writer,"-2","Password required as a parameter");
            } else if (params.containsKey("password")) {
                writeFailure(writer,"-2","Email required as a parameter");
            } else {
                writeFailure(writer,"-2","Parameters email and password required");
            }
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

    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
        doGet(request, response);
    }
}
