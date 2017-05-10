package gamesite.utils;

import java.sql.*;
import java.io.*;

import gamesite.model.SQLExceptionHandler;
import gamesite.utils.ExceptionFormat;

public class SQLExceptionFormat extends ExceptionFormat {
    //Ensures static class
    protected SQLExceptionFormat () {}

    public static String toHtml (SQLExceptionHandler ex) {
        return htmlHeader()+"\n"+ex.getErrorMessage()+getTrace(ex)+"\n"+htmlFooter();
    }

    public static String toHtml (SQLException ex) {
        return htmlHeader()+"\n"+getTrace(ex)+"\n"+htmlFooter();
    }
}
