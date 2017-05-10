package gamesite.utils;

import java.io.*;
import java.util.*;

public class HtmlFormat {
    //Ensures this is a static class
    protected HtmlFormat () {}

    public String htmlRow (HashMap<String,String> row, String rowClass, String columnClass) {
        String htmlRow = "";
        String spanTag = null;
        if (rowClass!=null) {
	        htmlRow+="<div class=\""+rowClass+"\" >\n   ";
        } else {
	        htmlRow+="<div>";
        }
        if (columnClass!=null) {
            spanTag="<span class=\""+columnClass+"\">";
        } else {
            spanTag="<span>";
        }
        for (Map.Entry<String,String> column : row.entrySet()) {
                htmlRow+=" "+spanTag+column.getKey()+column.getValue()+"</span> ";
        }
        htmlRow+="</div>\n";
        return htmlRow;
    }

    public String htmlRow (HashMap<String,String> row, String rowClass) {
        return htmlRow(row, rowClass, null);
    }

    public String htmlRow (HashMap<String,String> row) {
        return htmlRow(row, null, null);
    }

    public void printHtmlRow (PrintWriter writer, HashMap<String,String> row,
            String rowClass, String columnClass) {
        writer.println(htmlRow(row, rowClass, columnClass));
    }

    public void printHtmlRow (PrintWriter writer, HashMap<String,String> row, String rowClass) {
        printHtmlRow(writer, row, rowClass, null);
    }

    public void printHtmlRow (PrintWriter writer, HashMap<String,String> row) {
        printHtmlRow(writer, row, null, null);
    }
}
