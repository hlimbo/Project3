package gamesite.utils;

import java.sql.*;
import java.io.*;

import gamesite.model.SQLExceptionHandler;
import gamesite.utils.ExceptionFormat;

public class SQLExceptionFormat extends ExceptionFormat {
    //Ensures static class
    protected SQLExceptionFormat () {}

    public static String toHtml (SQLExceptionHandler ex) {
        return htmlHeader()+"\n"+ex.getErrorMessage()+getHtmlTrace(ex)+"\n"+htmlFooter();
    }

    public static String toXml (SQLExceptionHandler ex) {
        return xmlHeader()+"<exception_class>SQLExceptionHandler</exception_class>\n"
            +"<msg>"+ex.getErrorMessage()+"</msg>\n"+getXmlTrace(ex)+"\n"+xmlFooter();
    }

    public static String toHtml (SQLException ex) {
        return htmlHeader()+"\n"+getHtmlTrace(ex)+"\n"+htmlFooter();
    }

    public static String toXml (SQLException ex) {
        return xmlHeader()+"<exception_class>SQLException</exception_class>\n"
            +getXmlTrace(ex)+"\n"+xmlFooter();
    }
}
