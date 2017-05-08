package tests;

//---------------- JUnit 4 testing imports -------------------------//
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.hamcrest.core.CombinableMatcher;
import org.junit.Test;
//-------------------------------------------------------

import java.sql.*;
import java.util.*;

import gamesite.model.*;
import gamesite.datastruct.*;

public class SearchResultsTest {

    @Test
    public void searchTest () {
        ArrayList<HashMap<String, String>> table;
        try {
            table = SearchResults.getInstance().search("publishers","1",
                    "0","","","","","Nintendo","id",false,1);
            assertNotNull(table);
            assertFalse(table.isEmpty());
            assertEquals(table.get(0).get("publisher"),"Nintendo");
            table = SearchResults.getInstance().search("games","1",
                    "0","Wii Sports","","","","","id",false,1);
            assertNotNull("table null",table);
            assertFalse("table empty",table.isEmpty());
            assertEquals("table entry not equal to Wii Sports",table.get(0).get("name"),"Wii Sports");
            assertNotEquals("table entry check not failing",table.get(0).get("name"),"TESTABLE_INCORRECT_VALUE");
        } catch (SQLExceptionHandler ex) {
            System.out.println(ex.getErrorMessage());
        } catch (java.lang.Exception e) {
			e.printStackTrace();
		}
    }

    @Test
    public void getCountTest () {
        Integer count; 
        try {
            count = SearchResults.getInstance().getCount("publishers","1",
                    "0","","","","","Nintendo","id",false,1);
            assertEquals((long)count,(long)1);
        } catch (SQLExceptionHandler ex) {
            System.out.println(ex.getErrorMessage());
        } catch (java.lang.Exception e) {
			e.printStackTrace();
		}
    }

    @Test
    public void masterSearchTest () {
        NTreeNode<Table> tables;
        try {
            tables = SearchResults.getInstance().masterSearch("publishers","1",
                    "0","","","","","Nintendo","id",false,1);
            assertNotNull(tables.data);
            assertFalse("tables empty",tables.data.isEmpty());
            //tables should be games, publishers, genres, and platforms
            assertFalse("number of tables do not match",
                tables.data.size()==4);
            for (HashMap<String,String> row : tables.data) {
                assertEquals("Nintendo not found in publisher table",row.get("publisher"),"Nintendo");
                assertFalse("publisher table contains more than 1 row",
                    row.size()==1);
            }
        } catch (SQLExceptionHandler ex) {
            System.out.println(ex.getErrorMessage());
        } catch (java.lang.Exception e) {
			e.printStackTrace();
		}
    }
}
