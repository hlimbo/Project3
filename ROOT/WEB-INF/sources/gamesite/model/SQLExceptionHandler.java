package gamesite.model;

import java.sql.*;

public class SQLExceptionHandler {
    public static String getErrorMessage (SQLException ex, String query) {
        String msg = "Error in SQL:\n";
        while (ex!=null) {
            msg+=ex.getMessage()+"\n";
            ex.getNextException();
        }
        msg+=" in sql expression "+query;
        return msg;
    }

    public static String getErrorMessage (SQLException ex) {
        return getErrorMessage(ex,"");
    }
}
