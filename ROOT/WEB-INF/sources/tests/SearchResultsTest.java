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
            //exact match for Wii Sports
            table = SearchResults.getInstance().search("games","1",
                    "0","Wii Sports","","","","","id",false,2);
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
            Integer count;
            tables = SearchResults.getInstance().masterSearch("publishers","1",
                    "0","","","","","Nintendo","id",false,1);
            assertNotNull(tables.data);
            assertFalse("tables empty",tables.data.isEmpty());
            assertEquals("publishers table is not publishers","publishers",tables.data.name);
            //tables should be games, publishers, genres, and platforms
            assertFalse("number of tables do not match",
                tables.data.size()==4);
            count=0;
            for (HashMap<String,String> row : tables.data) {
                assertEquals("Nintendo not found in publisher table","Nintendo",row.get("publisher"));
                ++count;
            }
            assertEquals("Too many publishers or zero publishers in games table",(long)1,(long)count);
            for (NTreeNode<Table> child : tables.children) {
                Table table = child.data;
                if (table.name.equals("games")) {
                    assertTrue("Wii Sports not found",table.find("name","Wii Sports"));
                } else if (table.name.equals("genres")) {
                    assertTrue("Sports not found",table.find("genre","Sports"));
                } else if (table.name.equals("platforms")) {
                    assertTrue("Wii not found",table.find("platform","Wii"));
                } else {
                    throw new Exception("Unknown table in tables of masterSearch");
                }
            }
            tables.data=null;
            tables = SearchResults.getInstance().masterSearch("games","1",
                    "0","Wii Sports","","","","","id",false,1);
            assertNotNull(tables.data);
            assertFalse("tables empty",tables.data.isEmpty());
            assertEquals("games table is not games","games",tables.data.name);
            //tables should be games, publishers, genres, and platforms
            assertFalse("number of tables do not match",
                tables.data.size()==4);
            count=0;
            for (HashMap<String,String> row : tables.data) {
                assertEquals("Wii Sports not found in game table",row.get("name"),"Wii Sports");
                ++count;
            }
            assertEquals("Too many games or zero games in games table",(long)1,(long)count);
            for (NTreeNode<Table> child : tables.children) {
                Table table = child.data;
                if (table.name.equals("publishers")) {
                    assertTrue("Nintendo not found",table.find("publisher","Nintendo"));
                    assertTrue("Wii not found in publishers of",child.children.get(0).data.find("platform","Wii"));
                } else if (table.name.equals("genres")) {
                    assertTrue("Sports not found",table.find("genre","Sports"));
                } else if (table.name.equals("platforms")) {
                    assertTrue("Wii not found",table.find("platform","Wii"));
                } else {
                    throw new Exception("Unknown table in tables of masterSearch");
                }
            }
            tables.data=null;
            tables = SearchResults.getInstance().masterSearch("games","5",
                    "0","M","","","","","id",false,1);
            assertNotNull(tables.data);
            assertFalse("tables empty",tables.data.isEmpty());
            assertEquals("games table is not games","games",tables.data.name);
            //tables should be games, publishers, genres, and platforms
            assertFalse("number of tables do not match",
                tables.data.size()==4);
            count=0;
            for (HashMap<String,String> row : tables.data) {
                ++count;
            }
            assertTrue("Zero games in games table",count>0);
            tables = SearchResults.getInstance().masterSearch("games","5",
                    "0","Dragon","","","","","id",false,1);
            tables.data.print();
            for (NTreeNode<Table> child : tables.children) {
                child.data.print();
            }
        } catch (SQLExceptionHandler ex) {
            System.out.println(ex.getErrorMessage());
        } catch (java.lang.Exception e) {
			e.printStackTrace();
		}
    }
}
