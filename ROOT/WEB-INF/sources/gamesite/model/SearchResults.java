package gamesite.model;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;

import gamesite.model.QueryUtils;
import gamesite.model.SQLExceptionHandler;
import gamesite.utils.DBConnection;

public class SearchResults {
    private static String masterTable = "platforms_of_games NATURAL JOIN genres_of_games NATURAL JOIN publishers_of_games";

    //Singleton class, so constructor not permitted outside of class
    private SearchResults () {
        cache = new ConcurrentHashMap<String,ArrayList<HashMap<String,String>>> ();
    }

    //Initialization-on-demand holder idiom
    //https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
    private static class SearchCache {
        static final SearchResults INSTANCE = new SearchResults();
    }
    //TODO implement size limit on caching

    public static SearchResults getInstance() {
        return SearchCache.INSTANCE;
    }

    private static ArrayList<HashMap<String,String>> callSearchQuery (String query, String table, 
            String limit, String offset, String game, String year, String genre, 
            String platform, String publisher, String order, 
            boolean descend, int match) throws SQLException, java.lang.Exception {
        ArrayList<HashMap<String,String>> results = new ArrayList<HashMap<String,String>> ();
        Connection dbconn = null;
        try {
            dbconn=DBConnection.create();
            PreparedStatement statement = dbconn.prepareStatement(query);
            setSearchTerms(statement,game,year,publisher,genre,platform,match);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                results.add(QueryUtils.tableRow(rs));
            }
        } catch (SQLException ex) {
            throw ex;
        } catch (java.lang.Exception ex) {
            throw ex;
        } finally {
			DBConnection.close(dbconn);
        }
        return results;
    }

    public static String buildQuery (String table, String limit, String offset,
            String game, String year, String genre, String platform, String publisher, 
            String order, boolean descend, 
            int match) throws SQLException, java.lang.Exception {
        String query="";
        boolean searchAll = false;
        if ((game==null || game.trim().compareTo("")==0) && (year==null || year.trim().compareTo("")==0)
                && (publisher==null || publisher.trim().compareTo("")==0) 
                && (genre==null || genre.trim().compareTo("")==0) 
                && (platform==null || platform.trim().compareTo("")==0)) {
            searchAll = true;
        }
        if (!searchAll) {
            //duplicates due to games on multiple platforms, with multiple genres, or etc...
            query = "SELECT DISTINCT "+table+".* FROM games, publishers, platforms, genres, "+masterTable+" WHERE "
                +"games.id=game_id AND publishers.id=publisher_id AND platforms.id=platform_id AND genres.id=genre_id";
            query+=QueryUtils.addSearchTerm(game,"name",match);
            query+=QueryUtils.addSearchTerm(year,"year",match);
            query+=QueryUtils.addSearchTerm(publisher,"publisher",match);
            query+=QueryUtils.addSearchTerm(genre,"genre",match);
            query+=QueryUtils.addSearchTerm(platform,"platform",match);
        } else {
            query = "SELECT "+table+".* FROM "+table;
        }
        return query;
    }

    private static void setSearchTerms (PreparedStatement statement,String name, String year, String publisher,
            String genre, String platform,int useSubMatch) throws SQLException {
        int statementOffset = 1;
        statementOffset = QueryUtils.setSearchTerm(name,"name",statement,statementOffset,useSubMatch);
        statementOffset = QueryUtils.setSearchTerm(year,"year",statement,statementOffset,useSubMatch);
        statementOffset = QueryUtils.setSearchTerm(publisher,"publisher",statement,statementOffset,useSubMatch);
        statementOffset = QueryUtils.setSearchTerm(genre,"genre",statement,statementOffset,useSubMatch);
        QueryUtils.setSearchTerm(platform,"platform",statement,statementOffset,useSubMatch);
    }

    public ArrayList<HashMap<String,String>> search (String table, String limit, String offset,
            String game, String year, String genre, String platform, String publisher, String order, 
            boolean descend, int match) throws SQLExceptionHandler, java.lang.Exception {
        String query = buildQuery(table,limit,offset,game,year,genre,platform,publisher,order,descend,match);
        if (descend) {
            query+=" ORDER BY ISNULL( "+table+"."+order+"), "+table+"."+order+" DESC, "
                +table+".id LIMIT "+limit+" OFFSET "+offset;
        } else {
            query+=" ORDER BY ISNULL( "+table+"."+order+"), "+table+"."+order+" ASC, "
                +table+".id LIMIT "+limit+" OFFSET "+offset;
        }
        ArrayList<HashMap<String,String>> results = cache.get(query);
        if (results == null) {
            try {
                results = callSearchQuery(query,
                    table,limit,offset,game,year,genre,
                    platform,publisher,order,descend,match);
                if (results != null) {
                    cache.putIfAbsent(query,results);
                    return results;
                } else {
                    results = new ArrayList<HashMap<String,String>>();
                }
            } catch (SQLException ex) {
                throw new SQLExceptionHandler(ex,query);
            }
        }
        return results;
    }

    /*private NTreeNode<ArrayList<HashMap<String,String>>> searchNode (Connection conn, NTreeNode<String> parent, String key) 
        throws SQLExceptionHandler, java.lang.Exception {
        String query = "";
        try {
            //TODO
            String joinTable;
            String parentTable;
            query = "SELECT DISTINCT "+parent.data+".* FROM "+parent.data+" JOIN "+joinTable
                +" ON id="+key+" WHERE "+parent.data+"="+key;
                +"";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            ArrayList<HashMap<String,String>> rows;
            while (rs.next()) {
                rows.add(tableRow(rs));
            }
            NTreeNode<ArrayList<HashMap<String,String>>> root = new NTreeNode<ArrayList<HashMap<String,String>>>(rows);
            for (NTreeNode<String> child : parent) {
                root.addChild(searchNode(conn,child));
            }
            return root;
        } catch (SQLException ex) {
            throw new SQLExceptionHandler(ex,query);
        }
    }*/

    /*public NTreeNode<ArrayList<HashMap<String,String>>> masterSearch (String table, String limit, String offset,
            String game, String year, String genre, String platform, String publisher, String order, 
            boolean descend, int match) throws SQLExceptionHandler, java.lang.Exception {
        NTreeNode<String> siblings = QueryUtils.getSiblings(table);
        NTreeNode<ArrayList<HashMap<String,String>>> resultsRoot = new NTreeNode<Array<HashMap<String,String>>>(
                search(table,limit,offset,game,year,genre,platform,publisher,order,descend,match));
        Connection dbcon = null;
        //NTreeNode<ArrayList<HashMap<String,String>>> parent = resultsRoot;
        try {
            dbcon = QueryUtils.createConn();
            for
            searchNode(dbcon,siblings);
        } finally {
            if (dbcon!=null) {
                dbcon.close();
            }
        }
        return resultsRoot;
    }*/

    public int getCount(String table, String limit, String offset,
            String game, String year, String genre, String platform, String publisher,
            String order, boolean descend, int match) throws SQLException, java.lang.Exception {
        String countQuery = "SELECT COUNT(*) FROM ("+
            buildQuery(table,limit,offset,game,year,genre,platform,publisher,order,descend,match)
            +") AS countable";
        int count = -1;
        Connection dbconn=null;
        try {
            dbconn=DBConnection.create();
            PreparedStatement statement = dbconn.prepareStatement(countQuery);
            setSearchTerms(statement,game,year,publisher,genre,platform,match);
            ResultSet rs = statement.executeQuery();
            rs.next();
            count = rs.getInt(1);
        } finally {
            DBConnection.close(dbconn);
        }
        return count;
    }

    private ConcurrentMap<String,ArrayList<HashMap<String,String>>> cache;
}
