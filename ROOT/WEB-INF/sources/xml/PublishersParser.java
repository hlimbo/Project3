package xml;

import xml.model.Pub;
import xml.model.Gplt;
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
		System.out.println("Publishers Size: " + publishers.size());
		Iterator<Pub> it = publishers.iterator();
		while(it.hasNext())
			System.out.println(it.next().toString());
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
	
	@Override
    public void endElement(String uri, String localName, String qName) throws SAXException
	{
			if(qName.equalsIgnoreCase(PUB))
				publishers.add(tempPub);
			else if(qName.equalsIgnoreCase(P_ID))
				tempPub.setPubID(tempVal);
			else if(qName.equalsIgnoreCase(P))
				tempPub.setPublisher(tempVal);
			else if(qName.equalsIgnoreCase(FND))
				tempPub.setFoundedYear(tempVal);
			else if(qName.equalsIgnoreCase(GPLT))
				tempPub.addGamePlatformRecord(tempRecord);
			else if(qName.equalsIgnoreCase(G))
				tempRecord.gameTitle = tempVal;
			else if(qName.equalsIgnoreCase(PLT))
				tempRecord.platform = tempVal;				
	}

	public void insertIntoDatabase()
	{
		try
		{
			//create a db connection
			Connection dbcon = DBConnection.create();

			//write insert sql query ~ WRONG
			String insertQuery = "INSERT INTO publishers (publisher, founded) VALUES (?,?)";
		
			//Create a preparedStatement via db connection ~ WRONG
			PreparedStatement insertStatement = dbcon.prepareStatement(insertQuery);
			//loop through every entry in publishers and set the prepared  statments params accordingly
	
			//close db connection
			DBConnection.close(dbcon);
		}
		catch(SQLException e)
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
		pr.parseDocument(args[0] + "/pubs.xml");
		//pr.parseDocument("newGames/pubs.xml");
		long endTime = System.currentTimeMillis();	
		long elapsedTime = endTime - startTime;	
		pr.printData();
		System.out.println("Execution parse time: " + elapsedTime + " ms");
	}
	
}