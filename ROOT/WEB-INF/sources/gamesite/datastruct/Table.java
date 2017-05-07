package gamesite.datastruct;

import java.util.*;

public class Table {

    public Table (String newName) {
        name = newName;
        rows = new HashMap<String,String>();
    }

    public HashMap<String,String> rows;
    public final String name;
}
