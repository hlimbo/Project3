package gamesite.datastruct;

import java.util.*;

public class Table implements Iterable<HashMap<String,String>> {

    public Table (String newName,String keyName) {
        name = newName;
        rows = new HashMap<String,HashMap<String,String>>();
        key = keyName;
    }

    public HashMap<String,String> getRow(String keyValue) {
        return rows.get(keyValue);
    }

    public HashMap<String,String> addRow(HashMap<String,String> row) {
        return rows.put(row.get(key),row);
    }

    public Iterator<HashMap<String,String>> iterator () {
            return rows.values().iterator();
    }

    public boolean isEmpty () {
        return rows.isEmpty();
    }

    public int size () {
        return rows.size();
    }

    public boolean find (String fieldName, String fieldValue) {
        for (HashMap<String,String> row : this) {
            if (row.containsKey(fieldName) && row.get(fieldName).equals(fieldValue)) {
                return true;
            }
        }
        return false;
    }

    //String key in outer HashMap is a primary key to find a particular
    //row by.
    public HashMap<String,HashMap<String,String>> rows;
    public final String name;
    //primary key of table for quick searching of particular
    //row
    public final String key;
}
