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
import gamesite.model.*;
import gamesite.utils.*;

public class QueryUtilsTest
{	
	@Test
	public void test_for_size_GetTables() throws SQLException
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
		expectedTables.add("employees");
		
        Connection dbconn=null;
		try
		{
            dbconn = DBConnection.create();
			ArrayList<String> tables = QueryUtils.getTables(dbconn);
			assertEquals("failure - tables are not the same size", expectedTables.size(), tables.size());
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		catch (java.lang.Exception e)
		{
			e.printStackTrace();
		} finally {
            DBConnection.close(dbconn);
        }
	
	}
	
	@Test 
	public void test_for_equality_GetTables() throws SQLException
	{
		//Assumption getting the tables from gamedb
		HashSet<String> expectedTables = new HashSet<String>();
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
		expectedTables.add("employees");
		
        Connection dbconn=null;
		try
		{
            dbconn = DBConnection.create();
			HashSet<String> tables = new HashSet<String> (QueryUtils.getTables(dbconn));
			assertEquals("failure - tables do not contain the same table names", tables, expectedTables);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		catch (java.lang.Exception e)
		{
			e.printStackTrace();
		} finally {
            DBConnection.close(dbconn);
        }
		
	}
	
	@Test
	public void testGetSiblings() throws SQLException
	{
		NTreeNode<String> expectedTables = new NTreeNode<String>("games");
		expectedTables.addChild(new NTreeNode<String>("publishers"));
        NTreeNode<String> platformNode = new NTreeNode<String>("platforms");
		expectedTables.addChild(platformNode);
		expectedTables.addChild(new NTreeNode<String>("genres"));
		NTreeNode<String> publisherExpectedTables = new NTreeNode<String>("publishers");
        publisherExpectedTables.addChild(platformNode);

        Connection dbconn=null;
		try 
        {
            dbconn = DBConnection.create();
			NTreeNode<String> tables = QueryUtils.getSiblings(dbconn,"games");
			assertEquals("failure - sibling roots do not contain the same data", tables.data, expectedTables.data);		
            for (NTreeNode<String> sibling : expectedTables.children) {
                assertTrue(sibling.data+" not found in tables children",tables.children.contains(sibling));
            }
            for (NTreeNode<String> child : tables.children) {
                if (child.data.equals("publishers")) {
                    for (NTreeNode<String> pubSib : publisherExpectedTables.children) {
                        assertTrue("publishers expected child "+pubSib.data+" not found",child.children.contains(pubSib));
                    }
                    break;
                }
            }
        } 
        catch (SQLException e) 
        {
			e.printStackTrace();
		} 
        catch (java.lang.Exception e) 
        {
			e.printStackTrace();
		} finally {
            DBConnection.close(dbconn);
        }
    }
	
}
