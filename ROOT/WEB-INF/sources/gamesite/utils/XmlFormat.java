package gamesite.utils;

import java.io.*;
import java.util.*;

public class XmlFormat {
    //Ensures this is a static class
    protected XmlFormat () {}

    public static String xmlRows (HashMap<String,String> row, String rowClass, String columnClass) {
        String xml = "";
        String spanTag = null;
	    xml+="<"+rowClass+">\n   ";
        for (Map.Entry<String,String> column : row.entrySet()) {
                xml+=" "+"<"+columnClass+">"
                    +"<key>"+column.getKey()+"</key><value>"
                    +column.getValue()+"</value>"
                    +"</"+columnClass+">";
        }
        xml+="</"+rowClass+">\n";
        return xml;
    }

    public static void printXmlRows (PrintWriter writer, HashMap<String,String> row,
            String rowClass, String columnClass) {
        writer.println(xmlRows(row, rowClass, columnClass));
    }

}
