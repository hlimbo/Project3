package gamesite.model;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

import gamesite.model.QueryUtils;
import gamesite.model.SQLExceptionHandler;
import gamesite.utils.*;
import gamesite.datastruct.*;

public class SearchResults {
    private static String masterTable = "platforms_of_games NATURAL JOIN genres_of_games NATURAL JOIN publishers_of_games";
    public static Integer limitMax = 50;

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
        //TODO
        //ArrayList<HashMap<String,String>> results = cache.get(query);
        ArrayList<HashMap<String,String>> results = null;
        if (results == null) {
            try {
                results = callSearchQuery(query,
                    table,limit,offset,game,year,genre,
                    platform,publisher,order,descend,match);
                if (results != null) {
                    //TODO
                    //cache.putIfAbsent(query,results);
                    return results;
                } 
            } catch (SQLException ex) {
                throw new SQLExceptionHandler(ex,query);
            }
        }
        return results;
    }

    private Table siblingData (NTreeNode<Table> root, 
            NTreeNode<Table> child, String rootId, HashMap<String,
            HashMap<String,String>>relations) throws SQLExceptionHandler, java.lang.Exception {
        Connection conn = null;
        ResultSet result = null;
        String queryId = "null";
        String itemQuery = null;
        Table sibData = new Table(child.data.name,"id");
        try {
            conn = DBConnection.create();
            ArrayList<String> tables = QueryUtils.getTables(conn);
            String relationName = relations.get(root.data.name).get(child.data.name);
            ArrayList<Integer> sibIds = new ArrayList<Integer>();
            ArrayList<HashMap<String,Integer>> childSibIds = new ArrayList<HashMap<String,Integer>>();
            //By SQL schema convention, values.id = value_id in relation table
            String parentIdField = QueryUtils.getRelationIdName(root.data.name);
		    itemQuery = "SELECT "+relationName+".* FROM "+relationName+" JOIN "+root.data.name
                +" ON "+relationName+"."+parentIdField+"="+root.data.name+".id WHERE id=?";
		    PreparedStatement statement = conn.prepareStatement(itemQuery);
		    statement.setInt(1,Integer.parseInt(rootId));
            result = statement.executeQuery();
            while (result.next()) {
                String sibIdField = QueryUtils.getRelationIdName(child.data.name);
		        ResultSetMetaData meta = result.getMetaData();
                childSibIds.add(new HashMap<String, Integer> ());
		        for (int i=1;i<=meta.getColumnCount();++i) {
                    if (meta.getColumnName(i).equals(sibIdField)) {
                        sibIds.add(result.getInt(i));
                    } else {
                        childSibIds.get(childSibIds.size()-1).put(meta.getColumnName(i),result.getInt(i));
                    }
                }
            }
            result.close();
            statement.close();
            itemQuery=null;
            assert(sibIds.size() == childSibIds.size());
            for (int sibIndex = 0; sibIndex < sibIds.size(); ++sibIndex) {
                    HashMap<String, String> row;
                    boolean append = false;
                    if (sibData.getRow(sibIds.get(sibIndex).toString())==null) {
                        queryId = sibIds.get(sibIndex).toString();
                        result = SQLQuery.getTableInfo(conn,
                            sibIds.get(sibIndex),child.data.name);
                        if (result.next()) {
                            row = QueryUtils.tableRow(result);
                            sibData.addRow(row);
                            assert(result.next()==false);
                        } else {
                            continue;
                        }
                    }
                    row = sibData.getRow(sibIds.get(sibIndex).toString());
                    if (row.containsKey("child_id")) {
                        append=true;
                    }
                    for (HashMap.Entry<String,Integer> childSib : childSibIds.get(sibIndex).entrySet()) {
                        if (!row.containsKey("child_id")) {
                            row.put("child_id",childSib.getKey()+"="+childSib.getValue().toString());
                        } else {
                            if (append) {
                                row.put("child_id",row.get("child_id")
                                    +","+childSib.getKey()+"="+childSib.getValue().toString());
                                append=false;
                            } else {
                                row.put("child_id",row.get("child_id")
                                    +"&"+childSib.getKey()+"="+childSib.getValue().toString());
                            }
                        }
                    }
                    sibData.addRow(row);
            }
        } catch (SQLException ex) {
            if (itemQuery!=null) {
                throw new SQLExceptionHandler(ex,itemQuery);
            } else {
                throw new SQLExceptionHandler(ex," SQLQuery.getTableInfo(conn, "
                    +queryId+", "+child.data.name+")");
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
            NTreeNode<String> siblings, HashMap<String,Boolean> visited,
            HashMap<String,NTreeNode<Table>> processed,
            HashMap<String,HashMap<String,String>> relations
            ) throws SQLExceptionHandler, java.lang.Exception {
        visited.put(root.data.name,true);
        for (NTreeNode<String> sib : siblings.children) {
            if (!visited.containsKey(sib.data)) {
                if (!processed.containsKey(sib.data)) {
                    root.addChild(new NTreeNode<Table>(new Table(sib.data,"id")));
                } else {
                    root.addChild(processed.get(sib.data));
                }
            }
        }
        //process children nodes
        for (HashMap<String,String> row : root.data) {
            for (NTreeNode<Table> child : root.children) {
                String siblingIdField = QueryUtils.getRelationIdName(child.data.name);
                Table newData = siblingData(root,child,row.get("id"),relations);
                if (!processed.containsKey(child.data.name)) {
                    for (HashMap<String, String> newRow : newData) {
                        if (child.data.getRow(newRow.get("id"))==null) {
                            child.data.addRow(newRow);
                        } else if (newRow.get("child_id")!=null){
                            HashMap<String,String> childRow = child.data.getRow(newRow.get("id"));
                            if (childRow.get("child_id")!=null) {
                                childRow.put("child_id",childRow.get("child_id")+","+newRow.get("child_id"));
                            } else {
                                childRow.put("child_id",newRow.get("child_id"));
                            }
                        }
                    }
                }
                for (HashMap<String, String> newRow : newData) {
                    if (row.containsKey(siblingIdField)) {
                        row.put(siblingIdField,row.get(siblingIdField)+","+newRow.get("id"));
                    } else {
                        row.put(siblingIdField,newRow.get("id"));
                    }
                }
            }
        }
        //add processed children now that processing is done
        for (HashMap<String,String> row : root.data) {
            for (NTreeNode<Table> child : root.children) {
                if (!processed.containsKey(child.data.name)) {
                    processed.put(child.data.name,child);
                }
            }
        }
        for (NTreeNode<String> sibling : siblings.children) {
            for (NTreeNode<Table> child : root.children) {
                if (child.data.name.equals(sibling.data)) {
                    if (!visited.containsKey(child.data.name)) {
                        rootSearch(child,sibling,visited,processed,relations);
                    }
                    break;
                }
            }
        }
        visited.remove(root.data.name);
        return root;
    }

    public NTreeNode<Table> masterSearch (String table, String limit, String offset,
            String game, String year, String genre, String platform, String publisher, String order, 
            boolean descend, int match) throws SQLExceptionHandler, java.lang.Exception {
        NTreeNode<Table> root = new NTreeNode<Table>(new Table(table,"id"));
        ArrayList<HashMap<String,String>> rootTable;
        //if (table.equalsIgnoreCase("games")) {
            rootTable = search(table,limit,offset,game,
                year,genre,platform,publisher,order,descend,match);
        /*} else {
            rootTable = search(table,"18446744073709551615","0",game,
                year,genre,platform,publisher,order,descend,match);
        }*/
        for (HashMap<String,String> row : rootTable) {
            root.data.addRow(row);
        }
        Connection dbconn = null;
        try {
            dbconn = DBConnection.create();
            NTreeNode<String> siblings = QueryUtils.getSiblings(dbconn,table);
            rootSearch(root,siblings, new HashMap<String,Boolean>(),
                    new HashMap<String,NTreeNode<Table>>(), QueryUtils.getRelations(dbconn));
        } catch (SQLException ex) {
            throw ex;
        } finally {
            dbconn.close();
        }
        return root;
    }

    public class ntreeVisitAllTables implements Consumer<Table> {
        public ntreeVisitAllTables () {
            visited = new HashMap<String,Table> ();
        }
        public void accept (Table data) {
            if (!visited.containsKey(data.name)) {
                visited.put(data.name,data);
            }
        }
        public HashMap<String,Table> visited;
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
