package xml;

import xml.model.Pub;
import xml.model.Gplt;

import java.sql.*;
import gamesite.utils.DBConnection;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.util.Map;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;

import org.xml.sax.helpers.DefaultHandler;

public class PublishersParser extends DefaultHandler
{
	public static final String PUB = "pub";
	public static final String P_ID = "p_id";
	public static final String P = "p";
	public static final String FND = "fnd";
	public static final String GPLT = "gplt";
	public static final String G = "g";
	public static final String PLT = "plt";
	
	private List<Pub> publishers;
	
	//to maintain context
	private Pub tempPub;
	private Gplt tempRecord;
	private String tempVal;
	
	public PublishersParser()
	{
		publishers = new ArrayList<Pub>();
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
		Iterator<Pub> it = publishers.iterator();
		while(it.hasNext())
			System.out.println(it.next().toString());
		System.out.println("Publishers Size: " + publishers.size());
	}
	
	//Event Handlers
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException 
	{
		if(qName.equalsIgnoreCase(PUB))
			tempPub = new Pub();
		else if(qName.equalsIgnoreCase(GPLT))
		{	//initialize a platform game list on the first gplt tag encountered within a <pub> tag
			if(tempPub != null && tempPub.isPlatformGameListNull())
				tempPub.initializePlatformGameList();
			
			//intialize a gplt record to store into the platform game list
			tempRecord = new Gplt();
		}
		
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		tempVal = new String(ch, start, length);
	}

    public String removeSpecials (String tag, String special) {
        String specialRegex = "[^\\x20-\\x7e]";
        if (special.matches(".*"+specialRegex+"+.*")) {
            System.out.println("ERROR: string "+special+" contains special characters that can not be inserted.");
            if (tag!=null) {
                System.out.println("on tag: "+tag);
            }
            System.out.println("HANDLE: Removing special characters.");
        }
	    return special.replaceAll(specialRegex, " ").replaceAll(" {2,}"," ").trim();
    }
	
	@Override
    public void endElement(String uri, String localName, String qName) throws SAXException
	{
			if(qName.equalsIgnoreCase(PUB))
				publishers.add(tempPub);
			else if(qName.equalsIgnoreCase(P_ID))
				tempPub.setPubID(tempVal);
			else if(qName.equalsIgnoreCase(P))
			{
				String convert = removeSpecials(P,tempVal);
				tempPub.setPublisher(convert);
			}
			else if(qName.equalsIgnoreCase(FND))
				tempPub.setFoundedYear(tempVal);
			else if(qName.equalsIgnoreCase(GPLT))
				tempPub.addGamePlatformRecord(tempRecord);
			else if(qName.equalsIgnoreCase(G))
			{
				String convert = removeSpecials(G,tempVal);
				tempRecord.gameTitle = convert;
			}
			else if(qName.equalsIgnoreCase(PLT))
			{
				String convert = removeSpecials(PLT,tempVal);
				tempRecord.platform = convert;	
			}
	}
	
	public ResultSet getPublishers(Statement statement) throws SQLException
	{
		String selectQuery = "SELECT publisher, id FROM publishers";
		return statement.executeQuery(selectQuery);
	}
	
	public ResultSet getGames(Statement statement) throws SQLException
	{
		String selectQuery = "SELECT name, id FROM games";
		return statement.executeQuery(selectQuery);
	}
	
	public ResultSet getPlatforms(Statement statement) throws SQLException
	{
		String selectQuery = "SELECT platform, id FROM platforms";
		return statement.executeQuery(selectQuery);
	}

	public ResultSet getPogs(Statement statement) throws SQLException
	{
		String selectQuery = "SELECT game_id, publisher_id, platform_id FROM publishers_of_games";
		return statement.executeQuery(selectQuery);
    }
	
	public HashMap<String,Integer> getMap(ResultSet set) throws SQLException
	{
		HashMap<String,Integer> map = new HashMap<String,Integer>();
		while(set.next())
		{
			map.put(set.getString(1), set.getInt(2));
		}
		
		return map;
	}
	
	//return -1 on error
	public Integer getQuery(Statement statement, String query) throws SQLException
	{
		ResultSet r1 = statement.executeQuery(query);
		if(r1.next())
			return r1.getInt(1);
		
		return -1;
	}

	public void insertIntoDatabase()
	{
		try
		{
			//create a db connection
			Connection dbcon = DBConnection.create();
			
			Statement statement = dbcon.createStatement();
			
			ResultSet pubSet = getPublishers(statement);
			HashMap<String,Integer> pubMap = getMap(pubSet);
			ResultSet gameSet = getGames(statement);
			HashMap<String,Integer> gameMap = getMap(gameSet);
			ResultSet platformSet = getPlatforms(statement);	
			HashMap<String,Integer> platformMap = getMap(platformSet);

		
			Statement iStatement1 = dbcon.createStatement();

			Integer pubOffset = getQuery(iStatement1,"SELECT COUNT(*) FROM publishers");
			Integer gameOffset = getQuery(iStatement1, "SELECT COUNT(*) FROM games");
			Integer platformOffset = getQuery(iStatement1, "SELECT COUNT(*) FROM platforms");
					
			//turn off autocommit
			dbcon.setAutoCommit(false);
			
			String iQuery1 = "INSERT INTO publishers (publisher, founded) VALUES(?, YEAR(CURDATE()))";
			String iQuery2 = "INSERT INTO games (name, price, year) VALUES (?, 12, YEAR(CURDATE()))";
			String iQuery3 = "INSERT INTO platforms (platform) VALUES(?)";
			
			PreparedStatement pStatement1 = dbcon.prepareStatement(iQuery1);
			PreparedStatement pStatement2 = dbcon.prepareStatement(iQuery2);
			PreparedStatement pStatement3 = dbcon.prepareStatement(iQuery3);
			
			Map<String,ArrayList<Gplt>> masterList = new HashMap<String,ArrayList<Gplt>>();
			//entities
			//2 batch inserts
			for(Pub pub1 : publishers)
			{
				if(!pubMap.containsKey(pub1.getPublisher()))
				{
					//insert into publishers table
					pStatement1.setString(1, pub1.getPublisher());
					pStatement1.addBatch();
					
					//add pending insert to publisher map.
					pubMap.put(pub1.getPublisher(), -1);
					
					if (masterList.containsKey(pub1.getPublisher())) {
						masterList.get(pub1.getPublisher()).addAll(pub1.getList());
					} else {
						masterList.put(pub1.getPublisher(),pub1.getList());
					}
				}
			}
			
			pStatement1.executeBatch();
			dbcon.commit();
			
			for (List<Gplt> gplist : masterList.values()) {
				for(Gplt gboy : gplist)
				{
					if(!gameMap.containsKey(gboy.gameTitle))
					{
						//insert into games table.
						//insert gametitle and year
						pStatement2.setString(1,gboy.gameTitle);
						pStatement2.addBatch();
						
						gameMap.put(gboy.gameTitle, -1);
					}
					if(!platformMap.containsKey(gboy.platform))
					{
						//insert platform name into platforms table.
						pStatement3.setString(1, gboy.platform);
						pStatement3.addBatch();
						
						platformMap.put(gboy.platform, -1);
					}
				}
			}
			pStatement2.executeBatch();
			pStatement3.executeBatch();
			dbcon.commit();
			
			//query for the updated maps
			ResultSet pubSet2 = getPublishers(statement);
			pubMap = getMap(pubSet2);
			ResultSet gameSet2 = getGames(statement);
			gameMap = getMap(gameSet2);
			ResultSet platformSet2 = getPlatforms(statement);			
			platformMap = getMap(platformSet2);
			
			ResultSet pogSet = getPogs(statement);	
		    HashMap<String,Integer> pogs = new HashMap<String,Integer>();
		    while(pogSet.next())
		    {
		    	pogs.put(pogSet.getInt(1)+","+pogSet.getInt(2)+","+pogSet.getInt(3),-1);
		    }
			
			
			//adding all 3 ids into the database.
			//Map<String,List<Gplt>> master = new HashMap<String,ArrayList<Gplt>>();
			String insertQuery = "INSERT INTO publishers_of_games (game_id, publisher_id, platform_id) VALUES (?,?,?)";
			//Create a preparedStatement via db connection
			PreparedStatement insertStatement = dbcon.prepareStatement(insertQuery);
			for (Map.Entry<String,ArrayList<Gplt>> record : masterList.entrySet()) {
				Integer pID = pubMap.get(record.getKey());
				for (Gplt gp2 : record.getValue()) {
					Integer gaID = gameMap.get(gp2.gameTitle);
					Integer plID = platformMap.get(gp2.platform);
                    if (!pogs.containsKey(gaID+","+pID+","+plID)) {
					    insertStatement.setInt(1, gaID);
					    insertStatement.setInt(2, pID);
					    insertStatement.setInt(3, plID);
					    insertStatement.addBatch();
                        pogs.put(gaID+","+pID+","+plID,-1);
                    }
				}
			}
			
			//execute batch;
			insertStatement.executeBatch();
			dbcon.commit();
			
			
			pStatement3.close();
			pStatement2.close();
			pStatement1.close();
			iStatement1.close();
			statement.close();
			
			
			insertStatement.close();
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

	public static void main(String[] args)
	{
		PublishersParser pr = new PublishersParser();
		long startTime = System.currentTimeMillis();
		pr.parseDocument(args[0]);
		pr.insertIntoDatabase();
		long endTime = System.currentTimeMillis();	
		long elapsedTime = endTime - startTime;	
		//pr.printData();
		System.out.println("Execution parse time: " + elapsedTime + " ms");
	}
	
}
