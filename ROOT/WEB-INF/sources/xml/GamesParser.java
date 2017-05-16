package xml;

import xml.model.SimpleGame;
import gamesite.utils.DBConnection;

import java.sql.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;

import org.xml.sax.helpers.DefaultHandler;


//the parser's job is to return a collection of games in some data structure
//that will be used to insert into the games database.
public class GamesParser extends DefaultHandler
{	
	//tag names in games.xml
	public static final String REQUESTBLOCK = "RequestBlock";
	public static final String GAME = "Game";
	public static final String GAMETITLE = "GameTitle";
	public static final String ID = "id";
	public static final String RELEASEDATE = "ReleaseDate";
	public static final String PRICE = "Price";
	public static final String GENRES = "Genres";
	public static final String GENRE = "Genre";
	//ignore this tag!
	public static final String SIMILAR = "Similar";
	
	private List<SimpleGame> games;
	
	//required to maintain context
	private SimpleGame tempGame;
	private String tempVal;
	
	//ignore tag flag
	private boolean isIgnored = false;
	
	//debug
	private List<String> values;
	
	public GamesParser()
	{
		games = new ArrayList<SimpleGame>();
		values = new ArrayList<String>();
	}
	
	public void parseDocument(String filename)
	{
		//get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

			//set encoding to for ISO-8859-1
			File file = new File(filename);
			InputStream inputStream = new FileInputStream(file);
			Reader reader = new InputStreamReader(inputStream, "ISO-8859-1");	
			InputSource is = new InputSource(reader);
			is.setEncoding("ISO-8859-1");			
			
            //parse the file and also register this class for call backs
            sp.parse(is, this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
	}
	
	public void printData()
	{
		System.out.println("Number of Games: " + games.size());
		Iterator<SimpleGame> it = games.iterator();
		
		int nullGames = 0;
		int validGames = 0;
		
		while(it.hasNext())
		{
			SimpleGame gameRecord = it.next();
			
			/*if(gameRecord.getReleaseDate() == null)
				gameRecord.setReleaseDate("1/1/2017");*/
			
			boolean isNull = gameRecord.getID() == null || gameRecord.getGameTitle() == null || 
			gameRecord.getReleaseDate() == null || gameRecord.getPrice() == null;
			
			if(isNull)
				System.out.println(gameRecord.toString());
			
			if(isNull)
				nullGames++;
			else
				validGames++;
			
			//System.out.println(it.next().toString());
		}
		
		System.out.println("Games with null fields: " + nullGames);
		System.out.println("Games with valid fields: " + validGames);
	}
	
	public void printSize()
	{
		System.out.println("Number of Games: " + games.size());
	}
	
	//Event Handlers
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException 
	{
		if(qName.equalsIgnoreCase(SIMILAR))
			isIgnored = true;
		else if(!isIgnored && qName.equalsIgnoreCase(GAME))
			tempGame = new SimpleGame();
		/*else if(qName.equalsIgnoreCase(GENRES))
			tempGame.initializeGenreList();*/
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		tempVal = new String(ch, start, length);
		values.add(tempVal);
	}
	
	@Override
    public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if(isIgnored && qName.equalsIgnoreCase(SIMILAR))
			isIgnored = false;	
		//add to list when closing xml tag is reached </Game>
		else if(qName.equalsIgnoreCase(GAME))
			games.add(tempGame);
		//otherwise if closing xml game tag has not been reached,
		//set the values to tempGame for the other closing tags e.g. </GameTitle>
		else if(qName.equalsIgnoreCase(ID))
			tempGame.setID(Integer.parseInt(tempVal));
		else if (qName.equalsIgnoreCase(GAMETITLE))
			tempGame.setGameTitle(tempVal);
		else if(qName.equalsIgnoreCase(RELEASEDATE))
			tempGame.setReleaseDate(tempVal);
		else if(qName.equalsIgnoreCase(PRICE))
			tempGame.setPrice(Integer.parseInt(tempVal));
		else if(qName.equalsIgnoreCase(GENRE))
			tempGame.addGenre(tempVal);
	}
	
    public String removeSpecials (String tag, String special) {
        String specialRegex = "[^\\x20-\\x7e]";
        if (special.matches(specialRegex)) {
            System.out.println("ERROR: string "+special+" contains special characters that can not be inserted.");
            if (tag!=null) {
                System.out.println("on tag: "+tag);
            }
            System.out.println("HANDLE: Removing special characters.");
        }
	    return special.replaceAll(specialRegex, " ").replaceAll(" {2,}"," ").trim();
    }

	public HashMap<String,Integer> getMap(String tag, ResultSet set) throws SQLException
	{
		HashMap<String,Integer> map = new HashMap<String,Integer>();
		while(set.next())
		{
			map.put(removeSpecials(tag, set.getString(1)).toLowerCase(), set.getInt(2));
		}
		
		return map;
	}
	
	public void insertIntoDatabase()
	{
		try
		{
			//create a db connection
			Connection dbcon = DBConnection.create();
			
			String selectQuery = "SELECT name, id FROM games";
			Statement statement = dbcon.createStatement();
			ResultSet rs = statement.executeQuery(selectQuery);
			/*while(rs.next())
			{
				gamesMap.put(rs.getString(1), true);
			}*/
            HashMap<String, Integer> gamesMap = getMap(null,rs);
			
			ResultSet r73 = statement.executeQuery("SELECT genre, id from genres");			
			HashMap<String,Integer> genreMap = getMap(null,r73);
			
			
			//turn of autocommit
			dbcon.setAutoCommit(false);
			
			//write insert sql query
			String insertQuery = "INSERT INTO games (name, year, price) VALUES (?,?,?)";
		
			//Create a preparedStatement via db connection
			PreparedStatement insertStatement = dbcon.prepareStatement(insertQuery);
			//loop through every entry in games and set the prepared  statments params accordingly
			Iterator<SimpleGame> it = games.iterator();
			while(it.hasNext())
			{
				SimpleGame gameRecord = it.next();
				
				//remove special characters from game title if any exists
				String gameTitle = removeSpecials(GAMETITLE,gameRecord.getGameTitle());
				
				//duplication set
				if(!gamesMap.containsKey(gameTitle.toLowerCase()))
				{
					insertStatement.setString(1, gameTitle);
					insertStatement.setString(2, gameRecord.getReleaseYear());
					insertStatement.setInt(3, gameRecord.getPrice());
					insertStatement.addBatch();
					
					//don't add anymore games with the same title for games that are pending to add in database.
					gamesMap.put(gameTitle.toLowerCase(),-1);
				}
				
			}
			
			//execute batch;
			insertStatement.executeBatch();
			dbcon.commit();
			
			String insertQuery2 = "INSERT INTO genres (genre) VALUES (?)";
			PreparedStatement insertStatement2 = dbcon.prepareStatement(insertQuery2);
			
			HashMap<String,ArrayList<String>> gog = new HashMap<String,ArrayList<String>>();
			//insert into genres table
			for(SimpleGame gameRecord : games)
			{
				ArrayList<String> gens = new ArrayList<String>();
				for (String genre : gameRecord.getGenres())
				{
				    genre = removeSpecials(GENRE,genre);
					if(!genreMap.containsKey(genre.toLowerCase()))
					{
						insertStatement2.setString(1,genre);
						insertStatement2.addBatch();
						genreMap.put(genre.toLowerCase(), -1);
					}
					gens.add(genre);
				}
                //System.out.println(gens);
				gog.put(gameRecord.getGameTitle(),gens);
			}
			
			insertStatement2.executeBatch();
			dbcon.commit();
			
			
			Statement iStatement1  = dbcon.createStatement();
			String sQuery = "SELECT name, id FROM games";
			ResultSet r1 = iStatement1.executeQuery(sQuery);
			HashMap<String,Integer> gamesMap2 = getMap(null,r1);
			
			
			Statement iStatement2 = dbcon.createStatement();
			String sQuery2 = "SELECT genre, id FROM genres";
			ResultSet r2 = iStatement2.executeQuery(sQuery2);
			HashMap<String,Integer> genresMap = getMap(null,r2);
			
			Statement iStatement3 = dbcon.createStatement();
			String sQuery3 = "SELECT game_id, genre_id FROM genres_of_games";
			ResultSet r55 = iStatement3.executeQuery(sQuery3);
			HashMap<String,Integer> gogMap = new HashMap<String,Integer>();

			while(r55.next())
			{
				gogMap.put(r55.getInt(1)+","+r55.getInt(2),-1);
			}
            //System.out.println(gogMap);
			
			String insertQuery3 = "INSERT INTO genres_of_games (game_id, genre_id) VALUES (?, ?)";
			PreparedStatement insertStatement3 = dbcon.prepareStatement(insertQuery3);
			
			//insert into genres_of_games
			for(Map.Entry<String,ArrayList<String>> entry : gog.entrySet())
			{
				for(String g: entry.getValue())
				{
					if(!gogMap.containsKey(gamesMap2.get(removeSpecials(null,entry.getKey()).toLowerCase())+","+genresMap.get(removeSpecials(null,g).toLowerCase()))) 
					{
						if (gamesMap2.get(removeSpecials(null,entry.getKey()).toLowerCase())!=null && genresMap.get(removeSpecials(null,g).toLowerCase())!=null) {
                            //System.out.println(entry.getKey());
                            //System.out.println(gamesMap2.get(entry.getKey()));
                            //System.out.println(genresMap.get(g));
						    insertStatement3.setInt(1,gamesMap2.get(removeSpecials(GAMETITLE,entry.getKey().toLowerCase())));
						    insertStatement3.setInt(2,genresMap.get(removeSpecials(GENRE,g).toLowerCase()));
						    insertStatement3.addBatch();
						} else {
                            System.out.println("Unable to insert KEY:"+entry.getKey()+"\nVALUE:"+g);
                        }
					    gogMap.put(gamesMap2.get(removeSpecials(null,entry.getKey()).toLowerCase())+","+genresMap.get(removeSpecials(null,g).toLowerCase()),-1);
					}
				}
			}
			
			insertStatement3.executeBatch();
			dbcon.commit();
			
			
			insertStatement3.close();
			insertStatement2.close();
			insertStatement.close();						
			statement.close();
			//close db connection
			DBConnection.close(dbcon);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		catch(java.lang.Exception e)
		{
			e.printStackTrace();
		}
		
		
		
	}
	
	public void printStuff()
	{
		int i = 1;
		for(String value : values)
		{
			System.out.println((i++) + ". " + value);
		}
	}
	
	//execution time for 2 million game records ~ 6 seconds
	public static void main(String[] args)
	{
		GamesParser gameParser = new GamesParser();
		long startTime = System.nanoTime();
		gameParser.parseDocument(args[0]);
		gameParser.insertIntoDatabase();
		long endTime = System.nanoTime();		
		long elapsedTime = endTime - startTime;
		long timeInMilli = elapsedTime / 1000000;
		System.out.println("Parse execution time: " + timeInMilli);
		//gameParser.printData();
		
		//gameParser.printStuff();
	}
}
