package gamesite.model;

import java.sql.*;
import java.util.*;

import gamesite.model.QueryUtils;
import gamesite.utils.DBConnection;

public class DashBoardCommands {

    public String addMovie () {
        String commandResult = null;
        return commandResult;
    }

    public LinkedHashMap<String,HashMap<String,String>> getMeta () throws SQLException, java.lang.Exception {
        LinkedHashMap<String,HashMap<String,String>> types = new LinkedHashMap<String,HashMap<String,String>>();
        Connection conn = null;
        try {
            conn = DBConnection.create();
            ArrayList<String> tables = QueryUtils.getTables(conn);
            for (String table : tables) {
                types.put(table,QueryUtils.getColumns(conn,table));
            }
        } catch(SQLException ex) {
            throw ex;
        } catch(java.lang.Exception ex) {
            throw ex;
        }finally {
            DBConnection.close(conn);
        }
        return types;
    }

    public String insertStar () {
        String commandResult = null;
        return commandResult;
    }
}
