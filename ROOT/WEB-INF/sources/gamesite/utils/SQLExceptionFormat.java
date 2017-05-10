import java.sql.*;
import java.io.*;

import gamesite.model.SQLExceptionHandler;

class SQLExceptionFormat {
    //Ensures static class
    private SQLExceptionFormat () {}

    public static String htmlHeader () {
        return ("<HTML>\n<HEAD><TITLE>\ngamedb: Error"
            +"</TITLE>\n</HEAD>\n<BODY>\n" +
                    "<P>");
    }

    public static String htmlFooter () {
            return "</P></BODY></HTML>";
    }

    public static String getTrace (SQLException ex) {
        StackTraceElement [] trace = ex.getStackTrace();
        String traceString = "";
        for (StackTraceElement stackTop : trace) {
            traceString+=stackTop.toString();
        }
        return traceString;
    }

    public static String toHtml (SQLExceptionHandler ex) {
        return htmlHeader()+"\n"+ex.getErrorMessage()+getTrace(ex)+"\n"+htmlFooter();
    }

    public static String toHtml (SQLException ex) {
        return htmlHeader()+"\n"+getTrace(ex)+"\n"+htmlFooter();
    }
}
