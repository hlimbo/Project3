package gamesite.model;

import java.sql.*;
import java.util.*;
import java.util.regex.*;

import gamesite.model.QueryUtils;
import gamesite.utils.DBConnection;

public class DashBoardCommands {

    public static int addGame (String name, String year, String price, String platform,
            String publisher, String genre) 
            throws SQLExceptionHandler, SQLException, java.lang.Exception {
        Connection conn = null;
        String founded = "";
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
        return 1;
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
        int returnCode = 1;
        String publisher = "";
        String year = null;
        Pattern yearPattern = Pattern.compile("\\d{1,2}|\\d{4}");
        String[] parts = pubAndYear.split(";");
        if (parts.length > 1) {
            Matcher yearMatch = yearPattern.matcher(parts[parts.length-1].trim());
            //if pubAndYear ends in numbers
            if (yearMatch.matches()) {
                year = yearMatch.group();
                publisher = pubAndYear.substring(0,pubAndYear.lastIndexOf(';'));
            } else {
                publisher=pubAndYear;
            }
        } else {
            publisher = pubAndYear;
        }
        Connection conn = null;
        String query ="";
        try {
            conn = DBConnection.create();
            PreparedStatement count;
            query = "SELECT COUNT(*) FROM publishers WHERE publisher = ?";
            count = conn.prepareStatement(query);
            count.setString(1,publisher);
            ResultSet rs = count.executeQuery();
            rs.next();
            int exists = rs.getInt(1);
            rs.close();
            count.close();
            if (exists == 0) {
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
                try {
                    insert.executeUpdate();
                } catch (SQLException ex) {
                    //Invalid year
                    if (ex!=null && ex.getErrorCode()==1264) {
                        returnCode=-2;
                    } else {
                        throw ex;
                    }
                } finally {
                    insert.close();
                }
            } else {
                returnCode = 2;
            }
        } catch(SQLExceptionHandler ex) {
            throw ex;
        } catch(SQLException ex) {
            throw new SQLExceptionHandler(ex,query);
        } catch(java.lang.Exception ex) {
            throw ex;
        }finally {
            DBConnection.close(conn);
        }
        return returnCode;
    }
}
