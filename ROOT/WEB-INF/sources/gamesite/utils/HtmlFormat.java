package gamesite.utils;

import java.io.*;
import java.util.*;

public class HtmlFormat {
    //Ensures this is a static class
    protected HtmlFormat () {}

    public static String htmlRows (HashMap<String,String> row, String rowClass, String columnClass) {
        String html = "";
        String spanTag = null;
        if (rowClass!=null) {
	        html+="<div class=\""+rowClass+"\" >\n   ";
        } else {
	        html+="<div>";
        }
        if (columnClass!=null) {
            spanTag="<span class=\""+columnClass+"\">";
        } else {
            spanTag="<span>";
        }
        for (Map.Entry<String,String> column : row.entrySet()) {
                html+=spanTag+"<span class=\"key\">"+column.getKey()+"</span> "
                    +"<span class=\"value\">"+column.getValue()+"</span></span> ";
        }
        html+="</div>\n";
        return html;
    }

    public static String htmlRows (HashMap<String,String> row, String rowClass) {
        return htmlRows(row, rowClass, null);
    }

    public static String htmlRows (HashMap<String,String> row) {
        return htmlRows(row, null, null);
    }

    public static void printHtmlRows (PrintWriter writer, HashMap<String,String> row,
            String rowClass, String columnClass) {
        writer.println(htmlRows(row, rowClass, columnClass));
    }

    public static void printHtmlRows (PrintWriter writer, HashMap<String,String> row, String rowClass) {
        printHtmlRows(writer, row, rowClass, null);
    }

    public static void printHtmlRows (PrintWriter writer, HashMap<String,String> row) {
        printHtmlRows(writer, row, null, null);
    }
}
