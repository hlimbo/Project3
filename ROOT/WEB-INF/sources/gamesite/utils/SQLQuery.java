package gamesite.utils;

import java.sql.*;
import java.util.*;

/*
	SQLQuery is a static class that returns ResultSets of common sql queries
	e.g. retrieving a games information via id
*/

public class SQLQuery
{

	public static ResultSet getTableInfo(Connection dbcon, int table_id, String table) throws SQLException
    {
		String itemQuery = "SELECT * FROM "+table+" WHERE id='"+Integer.toString(table_id)+"'";
		Statement statement = dbcon.createStatement();
		return statement.executeQuery(itemQuery);
    }

	//Note: can return a null ResultSet if game_id not found in database
	public static ResultSet getGameInfo(Connection dbcon, int game_id) throws SQLException
	{
        return getTableInfo(dbcon,game_id,"games");
	}

	//Find and return login information if email and password exist in the table of the database, otherwise return null
	public static ResultSet getLogin (Connection dbcon, String email, String password, String table) throws SQLException
	{
		String query = "Select * from "+table+" where email='"+email.trim()+"' and password='"+password+"'";
		Statement statement = dbcon.createStatement();
		return statement.executeQuery(query);
	}
	
	//Find and return customer login information if email and password exist in database, otherwise return null
	public static ResultSet getCustomerLogin(Connection dbcon, String email, String password) throws SQLException
	{
        return getLogin(dbcon,email,password,"customers");
	}
	
	//Find customer creditcard information and verify if it exists in database.
	public static boolean isValidCreditCard(Connection dbcon, String cc_id, String expiration)
	{
		return false;
	}
	
	//helper function for isValidCreditCard
	private static boolean isCreditCardExpired(String expiration)
	{
		return false;
	}
	
	//Verifies that the creditcard is owned by exactly 1 person in the database.
	public static boolean isUniqueCreditCardAccount(Connection dbcon, String first_name, String last_name, String cc_id, String expiration)
	throws SQLException
	{
		return false;
	}
	
	
}
