package gamesite2.utils;

import java.sql.*;
import java.util.*;

/*
	SQLQuery is a static class that returns ResultSets of common sql queries
	e.g. retrieving a games information via id
*/

public class SQLQuery
{
	//Note: can return a null ResultSet if game_id not found in database
	public static ResultSet getGameInfo(Connection dbcon, int game_id) throws SQLException
	{
		String itemQuery = "SELECT * FROM games WHERE id=?";
		PreparedStatement statement = dbcon.prepareStatement(itemQuery);
		statement.setInt(1, game_id);
		return statement.executeQuery();
	}
	
	//Find and return customer login information if email and password exist in database, otherwise return null
	public static ResultSet getCustomerLogin(Connection dbcon, String email, String password) throws SQLException
	{
		String query = "Select * from customers where email=? and password=?";
		PreparedStatement statement = dbcon.prepareStatement(query);
		statement.setString(1, email);
		statement.setString(2, password);
		return statement.executeQuery();
	}
	
	
}