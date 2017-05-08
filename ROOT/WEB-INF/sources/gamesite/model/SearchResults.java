package gamesite.model;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;

import gamesite.model.QueryUtils;
import gamesite.model.SQLExceptionHandler;
import gamesite.utils.*;
import gamesite.datastruct.*;

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
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            dbconn=DBConnection.create();
            statement = dbconn.prepareStatement(query);
            setSearchTerms(statement,game,year,publisher,genre,platform,match);
            rs = statement.executeQuery();
            while (rs.next()) {
                results.add(QueryUtils.tableRow(rs));
            }
        } catch (SQLException ex) {
            throw ex;
        } catch (java.lang.Exception ex) {
            throw ex;
        } finally {
            if (rs!=null) {
                rs.close();
            }
            if (statement!=null) {
                statement.close();
            }
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

    private Table siblingData (NTreeNode<Table> root, NTreeNode<Table> child, 
            String rootId) throws SQLExceptionHandler, java.lang.Exception {
        Connection conn = null;
        ResultSet result = null;
        String queryId = "null";
        String itemQuery = null;
        Table sibData = new Table(child.data.name,"id");
        try {
            ArrayList<String> tables = QueryUtils.getTables();
            conn = DBConnection.create();
            //By SQL schema convention, relationship tables are named by
            //table1_of_table2
            String relationName = root.data.name+"_of_"+child.data.name;
            if (!tables.contains(relationName)) {
                relationName = child.data.name+"_of_"+root.data.name;
                assert(tables.contains(relationName));
            }
            ArrayList<Integer> sibIds = new ArrayList<Integer>();
            //By SQL schema convention, values.id = value_id in relation table
            String parentIdField = QueryUtils.getRelationIdName(root.data.name);
		    itemQuery = "SELECT * FROM "+relationName+" JOIN "+root.data.name
                +" ON "+relationName+"."+parentIdField+"="+root.data.name+".id WHERE id=?";
		    PreparedStatement statement = conn.prepareStatement(itemQuery);
		    statement.setInt(1,Integer.parseInt(rootId));
            result = statement.executeQuery();
            while (result.next()) {
                String sibIdField = QueryUtils.getRelationIdName(child.data.name);
		        ResultSetMetaData meta = result.getMetaData();
		        for (int i=1;i<=meta.getColumnCount();++i) {
                    if (meta.getColumnName(i).equals(sibIdField)) {
                        sibIds.add(result.getInt(i));
                        break;
                    }
                }
            }
            result.close();
            statement.close();
            itemQuery=null;
            for (Integer sibId : sibIds) {
                queryId = sibId.toString();
                result = SQLQuery.getTableInfo(conn,
                        sibId,root.data.name);
                while (result.next()) {
                    sibData.addRow(QueryUtils.tableRow(result));
                }
            }
        } catch (SQLException ex) {
            if (itemQuery!=null) {
                throw new SQLExceptionHandler(ex,itemQuery);
            } else {
                throw new SQLExceptionHandler(ex," SQLQuery.getTableInfo(conn, "
                    +queryId+", "+root.data.name+")");
            }
        } finally {
            if (result!=null) {
                result.close();
            }
            DBConnection.close(conn);
        }
        return sibData;
    }

    private NTreeNode<Table> rootSearch (NTreeNode<Table> root,
            NTreeNode<String> siblings) throws SQLExceptionHandler, java.lang.Exception {
        for (NTreeNode<String> sib : siblings.children) {
            root.addChild(new NTreeNode<Table>(new Table(sib.data,"id")));
        }
        for (HashMap<String,String> row : root.data) {
            for (NTreeNode<Table> child : root.children) {
                Table newData = siblingData(root,child,row.get("id"));
                for (HashMap<String, String> newRow : newData) {
                    child.data.addRow(newRow);
                }
            }
        }
        for (NTreeNode<String> sibling : siblings.children) {
            for (NTreeNode<Table> child : root.children) {
                if (child.data.name.equals(sibling.data)) {
                    rootSearch(child,sibling);
                    break;
                }
            }
        }
        return root;
    }

    public NTreeNode<Table> masterSearch (String table, String limit, String offset,
            String game, String year, String genre, String platform, String publisher, String order, 
            boolean descend, int match) throws SQLExceptionHandler, java.lang.Exception {
        NTreeNode<Table> root = new NTreeNode<Table>(new Table(table,"id"));
        ArrayList<HashMap<String,String>> rootTable = search(table,limit,offset,game,
                year,genre,platform,publisher,order,descend,match);
        for (HashMap<String,String> row : rootTable) {
            root.data.addRow(row);
        }
        NTreeNode<String> siblings = QueryUtils.getSiblings(table);
        rootSearch(root,siblings);
        return root;
    }

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
