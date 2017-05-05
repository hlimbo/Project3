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

    public ArrayList<HashMap<String,String>> search (String table, String limit, String offset,
            String game, String year, String genre, String platform, String order, 
            boolean descend, boolean match) throws SQLException, Java.lang.Exception {
        ArrayList<HashMap<String,String>> table = new ArrayList<HashMap<String,String>> ();
        String query;
        try {
            Connection dbconn=QueryUtils.createConn();
            boolean searchAll = false;
            if ((name==null || name.trim().compareTo("")==0) && (year==null || year.trim().compareTo("")==0)
                    && (publisher==null || publisher.trim().compareTo("")==0) 
                    && (genre==null || genre.trim().compareTo("")==0) 
                    && (platform==null || platform.trim().compareTo("")==0)) {
                searchAll = true;
            }
            if (!searchAll) {
                //duplicates due to games on multiple platforms, with multiple genres, or etc...
                query = "SELECT DISTINCT "+table+".* FROM games, publishers, platforms, genres, "+masterTable+" WHERE "
                    +"games.id=game_id AND publishers.id=publisher_id AND platforms.id=platform_id";
                query+=addSearchTerm(request,"name",useSubMatch);
                query+=addSearchTerm(request,"year",useSubMatch);
                query+=addSearchTerm(request,"publisher",useSubMatch);
                query+=addSearchTerm(request,"genre",useSubMatch);
                query+=addSearchTerm(request,"platform",useSubMatch);
            } else {
                query = "SELECT "+table+".* FROM "+table;
            }
            ResultSet rs = statement.executeQuery();
            rs.next();
        } catch (SQLException ex) {
            throw ex;
        } catch (java.lang.Exception ex) {
            throw ex;
        } finally {
            conn.close();
        }
    }

    public int getCount () {
        String query;
        String originalQuery = query;
        query = "SELECT COUNT(*) FROM ("+query+") AS countable";
        int count = rs.getInt(1);
    }

}
