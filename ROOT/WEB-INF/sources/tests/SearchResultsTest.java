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
            assertNotNull(table);
            assertFalse(table.isEmpty());
            assertEquals(table.get(0).get("name"),"Wii Sports");
            assertNotEquals(table.get(0).get("name"),"TESTABLE_INCORRECT_VALUE");
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
}
