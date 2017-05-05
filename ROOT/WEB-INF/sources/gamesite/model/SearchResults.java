package gamesite.model;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;

public class SearchResults {
    private static String masterTable = "platforms_of_games NATURAL JOIN genres_of_games NATURAL JOIN publishers_of_games";

    private static String addSearchTerm (String value, String term, boolean useSubMatch) {
            String searchTerm = "";
            if (value != null && value.trim() != "") {
                if (!useSubMatch) {
                    for (String subvalue : value.split(" ")) {
                        searchTerm+=" AND ";
                        searchTerm+=term+" LIKE ?";
                    }
                } else {
                    searchTerm+=" AND ";
                    searchTerm+=term+" LIKE ?";
                }
            }
            return searchTerm;
    }

    private static int setSearchTerm (String value, String term, PreparedStatement statement, 
            int offset, boolean useSubMatch) throws SQLException {
            Connection dbcon = QueryUtils.makeConn();
            PreparedStatement statement; 

            String searchTerm = "";
            if (value != null && value.trim() != "") {
                if (!useSubMatch) {
                    for (String subvalue : value.split(" ")) {
                        statement.setString(offset,"%"+subvalue+"%");
                        offset+=1;
                    }
                } else {
                    statement.setString(offset,value);
                    offset+=1;
                }
            }
            return offset;
    }

    public ArrayList<HashMap<String,String>> search () {
        ArrayList<HashMap<String,String>> table = new ArrayList<HashMap<String,String>> ();
        ResultSet rs = statement.executeQuery();
        rs.next();
    }

    public int getCount () {
        int count = rs.getInt(1);
    }

}
