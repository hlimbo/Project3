package gamesite.datastruct;

import java.util.*;

public class Table {

    public Table (String newName,String keyName) {
        name = newName;
        rows = new HashMap<String,HashMap<String,String>>();
        key = keyName;
    }

    //String key in outer HashMap is a primary key to find a particular
    //row by.
    public HashMap<String,HashMap<String,String>> rows;
    public final String name;
    //primary key of table for quick searching of particular
    //row
    public final String key;
}
