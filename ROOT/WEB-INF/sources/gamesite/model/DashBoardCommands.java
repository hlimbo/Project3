package gamesite.model;

import java.sql.*;
import java.util.*;
import java.util.regex.*;

import gamesite.model.QueryUtils;
import gamesite.utils.DBConnection;

public class DashBoardCommands {

    public static boolean addGame (String name, String year, String price, String platform,
            String publisher, String genre) 
            throws SQLExceptionHandler, SQLException, java.lang.Exception {
        Connection conn = null;
        try {
            conn = DBConnection.create();
            PreparedStatement addStmt = conn.prepareStatement("CALL add_game(?,?,?,?,?,?)");
            addStmt.setString(1,name.trim());
            addStmt.setString(2,year.trim());
            addStmt.setString(3,price.trim());
            addStmt.setString(4,platform.trim());
            addStmt.setString(5,publisher.trim());
            addStmt.setString(6,genre.trim());
            addStmt.executeUpdate();
        } catch(SQLExceptionHandler ex) {
            throw ex;
        } catch(SQLException ex) {
            throw ex;
        } catch(java.lang.Exception ex) {
            throw ex;
        }finally {
            DBConnection.close(conn);
        }
        return true;
    }

    public static LinkedHashMap<String,HashMap<String,String>> getMeta () 
            throws SQLExceptionHandler, SQLException, java.lang.Exception {
        LinkedHashMap<String,HashMap<String,String>> types = new LinkedHashMap<String,HashMap<String,String>>();
        Connection conn = null;
        try {
            conn = DBConnection.create();
            ArrayList<String> tables = QueryUtils.getTables(conn);
            for (String table : tables) {
                types.put(table,QueryUtils.getColumns(conn,table));
            }
        } catch(SQLExceptionHandler ex) {
            throw ex;
        } catch(SQLException ex) {
            throw ex;
        } catch(java.lang.Exception ex) {
            throw ex;
        }finally {
            DBConnection.close(conn);
        }
        return types;
    }

    public static int insertPublisher(String pubAndYear) throws SQLExceptionHandler, SQLException, java.lang.Exception {
        if (pubAndYear == null) {
            return -1;
        }
        pubAndYear = pubAndYear.trim();
        if (pubAndYear.equals("")) {
            return -1;
        }
        String publisher = "";
        String year = null;
        Pattern yearPattern = Pattern.compile("\\d+$");
        Matcher yearMatch = yearPattern.matcher(pubAndYear);
        //if pubAndYear ends in numbers
        if (yearMatch.matches()) {
            year = yearMatch.group();
            publisher = pubAndYear.substring(0,yearMatch.start());
        } else {
            publisher = pubAndYear;
        }
        Connection conn = null;
        String query ="";
        try {
            conn = DBConnection.create();
            PreparedStatement insert;
            if (year != null) {
                query = "INSERT INTO publishers (publisher, founded) VALUES (?,?)";
                insert = conn.prepareStatement(query);
                insert.setString(2,year);
            } else {
                query = "INSERT INTO publishers (publisher, founded) VALUES (?,null)";
                insert = conn.prepareStatement(query);
            }
            insert.setString(1,publisher);
            insert.executeUpdate();
        } catch(SQLExceptionHandler ex) {
            throw ex;
        } catch(SQLException ex) {
            throw new SQLExceptionHandler(ex,query);
        } catch(java.lang.Exception ex) {
            throw ex;
        }finally {
            DBConnection.close(conn);
        }
        return 1;
    }
}
