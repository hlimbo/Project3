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

import gamesite.utils.DBConnection;

public class DBConnectionTest
{
	@Test
	public void testDBConnection()
	{
		Connection dbcon = null;
		boolean isValidConnection = false;
		int timeoutInSeconds = 1;
		try
		{
			dbcon = DBConnection.create();
			isValidConnection = dbcon.isValid(timeoutInSeconds);
			dbcon.close();
		}
		catch (SQLException e)
		{
			assertNotNull("Failure - Database Connection should not be null", dbcon);
		}
		finally
		{
			assertTrue("Failure - Database Connection was not created", isValidConnection);
		}
	}
	
	@Test
	public void testDBConnectionClose()
	{	
		Connection dbcon = null;
		boolean isClosed = false;
		try
		{
			dbcon = DBConnection.create();	
			DBConnection.close(dbcon);
			isClosed = dbcon.isClosed();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			assertTrue("Failure - Database Connection should be closed", isClosed);
		}
	}
}