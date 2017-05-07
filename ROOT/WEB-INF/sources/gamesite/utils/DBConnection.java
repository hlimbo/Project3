package gamesite.utils;

import java.sql.*;

/*
	DBConnection is a static class responsible for 2 things:
		1. Creating a database connection to gamedb
		2. Closing a database connection to gamedb
*/

public class DBConnection
{
	public static Connection create() throws SQLException
	{
		//since we have jdbc 5.0, we don't need to create a new instance
		//Class.forName("com.mysql.jdbc.Driver").newInstance();
		
		String user = "user";
		String password = "password";
		//disable and suppress SSL errors; if SSL=true, it is required to verify that the database connection is secure
		String loginUrl = "jdbc:mysql://localhost:3306/gamedb?useSSL=false";
		
		return DriverManager.getConnection(loginUrl, user, password);
	}
	
	public static void close(Connection dbcon) throws SQLException
	{
		dbcon.close();
	}
}