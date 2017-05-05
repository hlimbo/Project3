package gamesite.model;

public class QueryUtils {

    //Class not meant to be instantiated
    private QueryUtils () {};

    public static Connection createConn () {
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            String loginUser = "user";
            String loginPasswd = "password";
            String loginUrl = "jdbc:mysql://localhost:3306/gamedb";

            Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            return dbcon;
    }

	public static HashMap<String, String> tableRow (ResultSet result) throws SQLException {
		ResultSetMetaData meta = result.getMetaData();
        HashMap<String,String> row = new HashMap<String,String>();
		for (int i=1;i<=meta.getColumnCount();++i) {
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
            //handle nulls and empty values here
			String colName = meta.getColumnName(i);
            row.put[colName]=value;
		}
        return row;
	}
}
