package gamesite.servlet;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import gamesite.datastruct.*;
import gamesite.utils.*;
import gamesite.model.*;
import gamesite.servlet.SearchBase;

public abstract class SearchBaseXml extends HttpServlet {
    public static String xmlHeader="<?xml version=\"1.0\" encoding=\"UTF-8\"?><search_results>";
    public static String xmlFooter="</search_results>";

	protected static String fieldValue (String colName, String value, String table,
            Hashtable<String,Boolean> link, Hashtable<String,Boolean> images, 
            Hashtable<String,Boolean> externalLinks, Hashtable<String,Boolean> ignores) {
	    String resString = "";
        if (ignores.containsKey(colName) && ignores.get(colName)) {
            return "";
        }
		resString+=" <field class=\""+table+"_"+colName+"\">";
        if (value==null || value.trim().compareTo("") == 0) {
            if (images.containsKey(colName) && images.get(colName)){
                resString+="<img>"
                    +"http://upload.wikimedia.org/wikipedia/"
                    +"commons/thumb/5/51/"
                    +"Star_full.svg/11px-Star_full.svg.png</img>";
            }
		    resString+="</field> ";
            return resString;
        }
        if  (link.containsKey(colName) && link.get(colName)) {
            try {
		        resString+="<a href=\"/display/query?table="+table+"&columnName="+colName+
                    "&"+colName+"="+URLEncoder.encode(value,"UTF-8")+"\">";
            } catch (UnsupportedEncodingException error) {
		        resString+="<a href=\"/display/query?table="+table+"&columnName="+colName+
                    "&"+colName+"="+value.replaceAll("[^\\w]","_")+"\">";
            }
            resString+=value;
            resString+="</a>";
        } else if  (externalLinks.containsKey(colName) && externalLinks.get(colName)) {
		    resString+="<a href=\"http://"+value+"\">"+value+"</a>";
        } else if (images.containsKey(colName) && images.get(colName)){
            resString+="<img>http://"+value+"</img>";
        } else {
            resString+=value;
        }
		resString+="</field> ";
        return resString;
	}

    protected String rowToHtml (HashMap<String,String> row, HttpServletRequest request, String table,
            Hashtable<String,Boolean> link, Hashtable<String,Boolean> images, 
            Hashtable<String,Boolean> externalLinks, Hashtable<String,Boolean> ignores) {
        String xmlRow = "";
	    xmlRow+="<row class=\""+table+"_row\">";
        for (Map.Entry<String,String> field: row.entrySet()) {
                xmlRow+=fieldValue(field.getKey(), field.getValue(), table, 
                        link, images, externalLinks, ignores);
        }
        xmlRow+="</row>";
        return xmlRow;
    }

    protected String regularFieldsToHtml (HashMap<String,String> row, String table,
            Hashtable<String,Boolean> link, Hashtable<String,Boolean> images, 
            Hashtable<String,Boolean> externalLinks, Hashtable<String,Boolean> ignores) {
        String result = "";
        for (String field : row.keySet()) {
            if (!field.endsWith("_id")) {
                result+=fieldValue(field,row.get(field),table,
                        link,images,externalLinks,ignores);
            }
        }
        return result;
    }

    protected String processChildToHtml (NTreeNode<Table> child, String childId,
            NTreeNode<Table> parent, String parentId,
            HashMap<String,Boolean> visited,
            Hashtable<String,Boolean> link, Hashtable<String,Boolean> images, 
            Hashtable<String,Boolean> externalLinks, Hashtable<String,Boolean> ignores) {
        String result = "";
        String childIdField = QueryUtils.getRelationIdName(child.data.name);
        String parentIdField = QueryUtils.getRelationIdName(parent.data.name);
        visited.put(childIdField,true);
        for (HashMap<String,String> row : child.data) {
            if (childId.equals(row.get("id"))) {
                result+="<row class=\""+child.data.name+"_row\">";
                result+=regularFieldsToHtml(row,child.data.name,link,images,externalLinks,ignores);
                if (row.containsKey("child_id")) {
                    String grandChild = row.get("child_id");
                    for (String parameters : grandChild.split(",")) {
                        HashMap<String,String> parsed = ParameterParse.getQueryParameters(parameters);
                        if (parsed.get(parentIdField).equals(parentId)) {
                            for (String key : parsed.keySet()) {
                                if (!visited.containsKey(key)) {
                                    NTreeNode<Table> nextChild;
                                    for (NTreeNode<Table> nChild : child.children) {
                                        if (nChild.data.name.equals(QueryUtils.getTableFromRelationIdName(key))) {
                                            nextChild = nChild;
                                            result+=processChildToHtml(nextChild,parsed.get(key),
                                                    child, childId,visited,
                                                    link,images,externalLinks,ignores);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                result+="</row>\n";
            }
        }
        visited.remove(childIdField);
        return result;
    }

    protected String ntreeToHtml (NTreeNode<Table> root, HttpServletRequest request, String id,
            Hashtable<String,Boolean> link, Hashtable<String,Boolean> images, 
            Hashtable<String,Boolean> externalLinks, Hashtable<String,Boolean> ignores) {
        String parentId = QueryUtils.getRelationIdName(root.data.name);
        String result = "";
        for (HashMap<String,String> row : root.data) {
            if (id==null || id.equals(row.get("id"))) {
                /*result+="<h3 class=\""+root.data.name.substring(0,root.data.name.length()-1)+"_row\">"
                    +root.data.name.substring(0,root.data.name.length()-1)+"</h3>";*/
                result+="<row class=\""+root.data.name+"_row\">";
                result+=regularFieldsToHtml(row,root.data.name,link,images,externalLinks,ignores);
                for (String field : row.keySet()) {
                    if (field.endsWith("_id")) {
                        String childTable = QueryUtils.getTableFromRelationIdName(field);
                        for (NTreeNode<Table> child : root.children) {
                            if (child.data.name.equals(childTable)) {
                                result+="<h3 class=\""+childTable+"_row\">"+childTable+"</h3>";
                                for (String childId : row.get(field).split(",")) {
                                    HashMap<String,Boolean> visited = new HashMap<String,Boolean>();
                                    visited.put(parentId,true);
                                    result+=processChildToHtml(child,childId.toString(),
                                            root, row.get("id"), visited,
                                            link,images,externalLinks,ignores);
                                }
                                break;
                            }
                        }
                    }
                }
                result+="</row>\n";
            }
        }
        return result;
    }
}
