package tests;

//---------------- JUnit 4 testing imports -------------------------//
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.hamcrest.core.CombinableMatcher;
import org.junit.Test;
//-------------------------------------------------------

import java.sql.*;
import java.util.*;

import gamesite.datastruct.NTreeNode;

//testing class
import gamesite.model.QueryUtils;

public class QueryUtilsTest
{
	@Test
	public void testDBConnection()
	{
		try
		{
			Connection dbcon = QueryUtils.createConn();
			assertNotNull("Database Connection should not be null", dbcon);
			dbcon.close();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetTables()
	{
		//Assumption getting the tables from gamedb
		ArrayList<String> expectedTables = new ArrayList<String>();
		expectedTables.add("creditcards");
		expectedTables.add("customers");
		expectedTables.add("games");
		expectedTables.add("genres");
		expectedTables.add("genres_of_games");
		expectedTables.add("platforms");
		expectedTables.add("platforms_of_games");
		expectedTables.add("publishers");
		expectedTables.add("publishers_of_games");
		expectedTables.add("sales");
		
		try
		{
			ArrayList<String> tables = QueryUtils.getTables();
			assertEquals("failure - tables are not the same size", tables.size(), expectedTables.size());		
			assertEquals("failure - tables do not contain the same table names", tables, expectedTables);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		catch (java.lang.Exception e)
		{
			e.printStackTrace();
		}
	
	}
	
	@Test
	public void testGetSiblings()
	{
		NTreeNode<String> expectedTables = new NTreeNode<String>("games");
		expectedTables.addChild(new NTreeNode<String>("publishers"));
		expectedTables.addChild(new NTreeNode<String>("platforms"));
		expectedTables.addChild(new NTreeNode<String>("genres"));

		try 
        {
			NTreeNode<String> tables = QueryUtils.getSiblings("games");
			assertEquals("failure - sibling roots do not contain the same data", tables.data, expectedTables.data);		
            for (NTreeNode<String> sibling : expectedTables.children) {
                assertTrue(tables.children.contains(sibling));
            }
        } 
        catch (SQLException e) 
        {
			e.printStackTrace();
		} 
        catch (java.lang.Exception e) 
        {
			e.printStackTrace();
		}
    }
	
}
