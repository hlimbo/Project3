package gamesite.utils;

import java.sql.*;
import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.Context;

/*
	DBConnection is a static class responsible for 2 things:
		1. Creating a database connection to gamedb
		2. Closing a database connection to gamedb
*/

public class DBConnection
{
    protected static int connectionNumber = 0;

	public static Connection create() throws SQLException, java.lang.Exception
	{
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		
		/*String user = "user";
		String password = "password";
		//disable and suppress SSL errors; if SSL=true, it is required to verify that the database connection is secure
		String loginUrl = "jdbc:mysql://localhost:3306/gamedb?useSSL=false";
		
		return DriverManager.getConnection(loginUrl, user, password);*/

        //Update to use connection pooling
        Context initCtx = new InitialContext();

        Context envCtx = (Context) initCtx.lookup("java:comp/env");
        DataSource ds = (DataSource) envCtx.lookup("jdbc/GameReadWrite");

        return ds.getConnection();
	}

	public static Connection createRead() throws SQLException, java.lang.Exception
	{
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		
		/*String user = "user";
		String password = "password";
		//disable and suppress SSL errors; if SSL=true, it is required to verify that the database connection is secure
		String loginUrl = "jdbc:mysql://localhost:3306/gamedb?useSSL=false";
		
		return DriverManager.getConnection(loginUrl, user, password);*/

        //Update to use connection pooling
        Context initCtx = new InitialContext();

        Context envCtx = (Context) initCtx.lookup("java:comp/env");
        DataSource ds = null;
        if (connectionNumber % 2 ==0) {
            ds = (DataSource) envCtx.lookup("jdbc/GameRead");
        } else {
            ds = (DataSource) envCtx.lookup("jdbc/GameReadWrite");
        }
        ++connectionNumber;

        return ds.getConnection();
	}
	
	public static void close(Connection dbcon) throws SQLException
	{
		if(dbcon != null)
			dbcon.close();
	}
}
