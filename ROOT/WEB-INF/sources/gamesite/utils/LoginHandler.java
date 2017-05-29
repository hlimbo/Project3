package gamesite.utils;

import java.sql.*;
import java.net.*;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import gamesite.utils.*;
import recaptcha.VerifyUtils;

public class LoginHandler {
    //ensures static class
    protected LoginHandler () {}

    public static int loginNoCaptcha (HttpServletRequest request, HttpServletResponse response, String table) 
        throws SQLException, Exception {
        Connection conn = null;
        try {
            conn = DBConnection.create();
            HashMap<String,String> params = ParameterParse.getQueryParameters(request.getQueryString());
            String email = params.get("email");
            email=email.trim();
            String password= params.get("password");
            ResultSet rs = SQLQuery.getLogin(conn,email,password,table);

            boolean exists = rs.next();
            if (exists) {
			    HttpSession session = request.getSession();
				Cookie cookie = new Cookie("login-cookie", session.getId());
				cookie.setComment("Cook on client side used to identify the current user login");
				response.addCookie(cookie);
				session.setAttribute("first_name",rs.getString("first_name"));
                return 1;
            } else {
                return -2;
            }
        } catch (SQLException ex) {
            throw ex;
        } finally {
            DBConnection.close(conn);
        }
    }

    public static int login(HttpServletRequest request, HttpServletResponse response, String table) throws SQLException, Exception {
        Connection conn = null;
        try {
            conn = DBConnection.create();
            HashMap<String,String> params = ParameterParse.getQueryParameters(request.getQueryString());
            String email = params.get("email");
            email=email.trim();
            String password= params.get("password");
            ResultSet rs = SQLQuery.getLogin(conn,email,password,table);
			HttpSession session = request.getSession();
            boolean exists = rs.next();
			//Validate Recaptcha	
			String captcha = request.getParameter("g-recaptcha-response");
			boolean human = VerifyUtils.verify(captcha);
            if (exists && human) {
                //success
				Cookie cookie = new Cookie("employee-cookie", session.getId());
				cookie.setComment("Employee");
				response.addCookie(cookie);
				session.setAttribute("employee",rs.getString("fullname"));
                return 1;
            } else if (!human) {
                return -1;
            } else {
                return -2;
            }
        } catch (SQLException ex) {
            throw ex;
        } finally {
            DBConnection.close(conn);
        }
    }
}
