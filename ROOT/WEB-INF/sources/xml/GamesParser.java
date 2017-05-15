package xml;

import xml.model.SimpleGame;
import gamesite.utils.DBConnection;

import java.sql.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

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
	public static final String PLATFORM = "Platform";
	
	private List<SimpleGame> games;
	
	//required to maintain context
	private SimpleGame tempGame;
	private String tempVal;
	
	public GamesParser()
	{
		games = new ArrayList<SimpleGame>();
	}
	
	public void parseDocument(String filename)
	{
		//get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse(filename, this);

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
		while(it.hasNext())
			System.out.println(it.next().toString());
	}
	
	public void printSize()
	{
		System.out.println("Number of Games: " + games.size());
	}
	
	//Event Handlers
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException 
	{
		if(qName.equalsIgnoreCase(GAME))
			tempGame = new SimpleGame();
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		tempVal = new String(ch, start, length);
	}
	
	@Override
    public void endElement(String uri, String localName, String qName) throws SAXException
	{
		//add to list when closing xml tag is reached </Game>
		if(qName.equalsIgnoreCase(GAME))
			games.add(tempGame);
		//otherwise if closing xml game tag has not been reached,
		//set the values to tempGame for the other closing tags e.g. </GameTitle>
		else if(qName.equalsIgnoreCase(ID))
			tempGame.setID(Integer.parseInt(tempVal));
		else if (qName.equalsIgnoreCase(GAMETITLE))
			tempGame.setGameTitle(tempVal);
		else if(qName.equalsIgnoreCase(RELEASEDATE))
			tempGame.setReleaseDate(tempVal);
		else if(qName.equalsIgnoreCase(PLATFORM))
			tempGame.setPlatform(tempVal);
	}
	
	public void insertIntoDatabase()
	{
		try
		{
			//create a db connection
			Connection dbcon = DBConnection.create();

			//write insert sql query
			String insertQuery = "INSERT INTO games (name, year, price) VALUES (?,?,?)";
		
			//Create a preparedStatement via db connection
			PreparedStatement insertStatement = dbcon.prepareStatement(insertQuery);
			Integer defaultGamePrice = 12;
			//loop through every entry in games and set the prepared  statments params accordingly
			Iterator<SimpleGame> it = games.iterator();
			while(it.hasNext())//naive way of inserting items into the games database.
			{
				SimpleGame gameRecord = it.next();
				insertStatement.setString(1, gameRecord.getGameTitle());
				insertStatement.setString(2, gameRecord.getReleaseYear());
				insertStatement.setInt(3, defaultGamePrice);
				insertStatement.executeUpdate();
			}
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
	
	//execution time for 2 million game records ~ 6 seconds
	public static void main(String[] args)
	{
		GamesParser gameParser = new GamesParser();
		long startTime = System.nanoTime();
		gameParser.parseDocument(args[0] + "/newGames.xml");
		//gameParser.parseDocument("newGames/newGames.xml");
		long endTime = System.nanoTime();		
		long elapsedTime = endTime - startTime;
		gameParser.insertIntoDatabase();
		long timeInMilli = elapsedTime / 1000000;
		System.out.println("Parse execution time: " + timeInMilli);
		//gameParser.printData();
		
	}
}