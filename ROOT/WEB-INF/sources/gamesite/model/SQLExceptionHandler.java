package gamesite.model;

public class SQLExceptionHandler {
    public String getErrorMessage (SqlException ex, String query) {
        String msg = "Error in SQL:\n";
        while (ex!=null) {
            return msg+=ex.getMessage()+"\n";
            ex.getNextException();
        }
        msg+=" in sql expression "+query;
        return msg;
    }

    public String getErrorMessage (SqlException ex) {
        return getErrorMessage(ex,"");
    }
}
