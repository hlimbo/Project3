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
                if (useSubMatch==1 || useSubMatch==3) {
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
                } else if (useSubMatch==3) {
                    String[] subvalues = value.split(" ");
                    //full search for all words except last word
                    for (int subvalue=0;subvalue<subvalues.length-1;++subvalue) {
                        statement.setString(offset,"%"+subvalues[subvalue]+"%");
                        offset+=1;
                    }
                    //treat last word as a prefix
                    statement.setString(offset,subvalues[subvalues.length-1]+"%");
                    offset+=1;
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
        tableMeta.close();
        return tables;
    }

    public static HashMap<String,String> getColumns (Connection conn,String tableName) throws SQLExceptionHandler, java.lang.Exception {
        HashMap<String,String> result = new HashMap<String,String>();
        conn = DBConnection.create();
        DatabaseMetaData metadataDB = conn.getMetaData();
		ResultSet tableColumns = metadataDB.getColumns(conn.getCatalog(),null,tableName, "%");
        while(tableColumns.next()) {
            result.put(tableColumns.getString("COLUMN_NAME"),tableColumns.getString("TYPE_NAME"));
        }
        return result;
    }

    public static HashMap<String,HashMap<String,String>> getRelations (Connection dbcon) 
        throws SQLException, SQLExceptionHandler, java.lang.Exception {
        HashMap<String,HashMap<String,String>> relations = new HashMap<String,HashMap<String,String>>();
        ArrayList<String> tables = getTables(dbcon);
        for (String table : tables) {
            //by convention of SQL schema all relationship tables have _of_
            //between the entity names
            if (table.indexOf("_of_") != -1) {
                String[] relatedTables = table.split("_of_");
                for (String related : relatedTables) {
                    if (!relations.containsKey(related)) {
                        relations.put(related,new HashMap<String,String> ());
                    }
                    DatabaseMetaData dbmeta = dbcon.getMetaData();
                    ArrayList<String> relation = new ArrayList<String> ();
                    ResultSet columnsMeta = dbmeta.getColumns(null,null,table,null);
                    while (columnsMeta.next()) {
                        String foreignTable = columnsMeta.getString("COLUMN_NAME").replaceFirst("_id","")+"s";
                        if (!related.equals(foreignTable)) {
                            relations.get(related).put(foreignTable,table);
                        }
                    }
                    columnsMeta.close();
                }
            }
        }
        return relations;
    }

    private static void getSiblingsRecur (Connection dbcon, NTreeNode<String> root, 
            HashMap<String,HashMap<String,String>> relations,
            HashMap<String,Boolean> visited, HashMap<String,NTreeNode<String>> processed) {
        visited.put(root.data,true);
        String table = root.data.trim().toLowerCase();
        for (String sibling : relations.get(table).keySet()) {
                if (processed.containsKey(sibling)) {
                    NTreeNode<String> nextNode = processed.get(sibling);
                    root.addChild(nextNode);
                } else {
                    NTreeNode<String> nextNode = new NTreeNode<String>(sibling);
                    processed.put(sibling,nextNode);
                    root.addChild(nextNode);
                    getSiblingsRecur(dbcon,nextNode,relations,visited,processed);
                }
        }
        visited.remove(root.data);
    }

    public static NTreeNode<String> getSiblings (Connection dbcon, String firstTable) throws SQLExceptionHandler, java.lang.Exception {
        NTreeNode<String> siblings = new NTreeNode<String>(firstTable);
        HashMap<String,NTreeNode<String>> processed = new HashMap<String,NTreeNode<String>> ();
        processed.put(firstTable,siblings);
        HashMap<String,HashMap<String,String>> relations = getRelations(dbcon);
        getSiblingsRecur(dbcon, siblings, relations, new HashMap<String, Boolean>(), processed);
        return siblings;
    }
}
