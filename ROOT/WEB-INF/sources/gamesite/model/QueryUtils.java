package gamesite.model;

import java.sql.*;
import java.util.*;

import gamesite.datastruct.NTreeNode;
import gamesite.utils.DBConnection;

public class QueryUtils {

    //Class not meant to be instantiated
    private QueryUtils () {};

    public static String getValue (ResultSet result, ResultSetMetaData meta, int i) 
        throws SQLException{
        int type = meta.getColumnType(i);
        String table = meta.getTableName(i);
        String typeName = meta.getColumnTypeName(i);
        boolean handled = false;
        String value="";
        switch(typeName.toUpperCase()) {
            case "YEAR":
                if (result.getString(i) != null) {
                    value+=result.getString(i).substring(0,4);
                    handled=true;
                }
                break;
        }
        if (!handled) {
            switch(type) {
                case Types.INTEGER:
                    if (result.getString(i) != null) {
                        value+=result.getInt(i);
                    }
                    break;
                default:
                    if (result.getString(i) != null) {
                        value+=result.getString(i);
                    }
                    break;
            }
        }
        return value;
    }

	public static HashMap<String, String> tableRow (ResultSet result) throws SQLException {
		ResultSetMetaData meta = result.getMetaData();
        HashMap<String,String> row = new HashMap<String,String>();
		for (int i=1;i<=meta.getColumnCount();++i) {
            row.put(meta.getColumnName(i),getValue(result,meta,i));
		}
        return row;
	}

    public static String addSearchTerm (String value, String term, int useSubMatch) {
            String searchTerm = "";
            if (value != null && value.trim() != "") {
                if (useSubMatch==1) {
                    for (String subvalue : value.split(" ")) {
                        searchTerm+=" AND ";
                        searchTerm+=term+" LIKE ?";
                    }
                } else if (useSubMatch==2) {
                    searchTerm+=" AND ";
                    searchTerm+=term+" LIKE ?";
                } else {
                    searchTerm+=" AND ";
                    searchTerm+=term+" = ?";
                }
            }
            return searchTerm;
    }

    public static int setSearchTerm (String value, String term, PreparedStatement statement, 
            int offset, int useSubMatch) throws SQLException {

            String searchTerm = "";
            if (value != null && value.trim() != "") {
                if (useSubMatch==1) {
                    for (String subvalue : value.split(" ")) {
                        statement.setString(offset,"%"+subvalue+"%");
                        offset+=1;
                    }
                } else {
                    statement.setString(offset,value);
                    offset+=1;
                }
            }
            return offset;
    }

    public static String getRelationIdName (String table) {
        return table.substring(0,table.length()-1)+"_id";
    }

    public static String getTableFromRelationIdName (String idName) {
        return idName.substring(0,idName.length()-3)+"s";
    }

    public static ArrayList<String> getTables (Connection dbcon) throws SQLExceptionHandler, java.lang.Exception {
        ArrayList<String> tables = new ArrayList<String>();
        DatabaseMetaData dbmeta = dbcon.getMetaData();
        ResultSet tableMeta = dbmeta.getTables(dbcon.getCatalog(),null,"%",null);
        while (tableMeta.next()) {
            tables.add(tableMeta.getString("TABLE_NAME"));
        } 
        return tables;
    }

    public static NTreeNode<String> getSiblings (Connection dbcon, String firstTable) throws SQLExceptionHandler, java.lang.Exception {
        NTreeNode<String> siblings = new NTreeNode<String>(firstTable);
        HashMap<String,Boolean> visited = new HashMap<String,Boolean> ();
        LinkedList<NTreeNode<String>> tableQueue = new LinkedList<NTreeNode<String>> ();
        tableQueue.add(siblings);
        visited.put(firstTable,true);
        ArrayList<String> potentialSiblings = getTables(dbcon);
        while (!tableQueue.isEmpty()) {
            NTreeNode<String> node = tableQueue.remove();
            String table = node.data.trim().toLowerCase();
            for (String sibling : potentialSiblings) {
                if (sibling.indexOf(table) != -1) {
                    //find sibling table by SQL schema convention of relationship tables
                    String next = sibling.replaceFirst(table,"").replaceFirst("_of_","");
                    if (potentialSiblings.contains(next) && !visited.containsKey(next)) {
                        NTreeNode<String> nextNode = new NTreeNode<String>(next);
                        tableQueue.add(nextNode);
                        visited.put(next,true);
                        node.addChild(nextNode);
                    }
                }
            }
        }
        return siblings;
    }
}
