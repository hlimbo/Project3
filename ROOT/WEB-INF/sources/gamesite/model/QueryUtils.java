package gamesite.model;

import java.sql.*;
import java.util.*;

public class QueryUtils {

    //Class not meant to be instantiated
    private QueryUtils () {};

    public static Connection createConn () throws InstantiationException, 
           SQLException, IllegalAccessException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver").newInstance();

        String loginUser = "user";
        String loginPasswd = "password";
        String loginUrl = "jdbc:mysql://localhost:3306/gamedb";

        Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        return dbcon;
    }

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

    public static ArrayList<String> getTables () throws SQLExceptionHandler, java.lang.Exception {
        Connection dbcon = null;
        try {
            ArrayList<String> tables = new ArrayList<String>();
            dbcon = QueryUtils.createConn();
            DatabaseMetaData dbmeta = dbcon.getMetaData();
            ResultSet tableMeta = dbmeta.getTables(dbcon.getCatalog(),null,"%",null);
            while (tableMeta.next()) {
                tables.add(tableMeta.getString("TABLE_NAME"));
            } 
            return tables;
        } finally {
            dbcon.close();
        }
    }

    /*public static ArrayList<String> getSiblings (String firstTable) throws SQLExceptionHandler, java.lang.Exception {
        ArrayList<String> siblings = new ArrayList<string>();
        HashMap<String,boolean> visited = new HashMap<String,boolean> ();
        LinkedList<String> tableQueue = new LinkedList<String> ();
        visited.put(firstTable,true);
        Connection dbcon = null;
        ArrayList<String> potentialSiblings = getTables();
        try {
            dbcon = QueryUtils.createConn();
            while (!tableQueue.isEmpty()) {
                String table = tableQueue.remove().trim().toLowerCase();
                for (String sibling : potentialSiblings) {
                    if (sibling.indexOf(table) != -1) {
                        //find sibling table by SQL schema convention of relationship tables
                        String next = sibling.replace(table,"").replace("_of_");
                        if (potentialSiblings.contains(next) && !visited.containsKey(next)) {
                            tableQueue.add(next);
                            visited.put(next);
                            siblings.add(next);
                        }
                    }
                }
            }
            return siblings;
        } finally {
            dbcon.close();
        }
    }*/
}
