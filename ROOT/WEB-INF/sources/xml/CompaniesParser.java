package xml;

import java.sql.*;
import java.nio.charset.Charset;
import java.nio.ByteBuffer;

import xml.model.Company;
import xml.model.Developer;
import xml.model.Publisher;
import gamesite.utils.DBConnection;

import java.io.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;

import org.xml.sax.helpers.DefaultHandler;

public class CompaniesParser extends DefaultHandler
{
	public static final String DEVELOPER = "developer";
	public static final String PUBLISHER = "publisher";
	public static final String NAME = "name";
	public static final String FOUNDED = "founded";
	public static final String REVENUE = "revenue";
	public static final String PLATFORMS = "platforms";
	public static final String PLATFORM = "platform";
	
	private List<Publisher> publishers;
	private List<Developer> developers;
	
	//to maintain context
	private Company company;
	private String tempVal;
	
	public CompaniesParser()
	{
		publishers = new ArrayList<Publisher>();
		developers = new ArrayList<Developer>();
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
		//int size = developers.size() + publishers.size();
		//System.out.println("Number of Companies: " + size);
		
		//System.out.println("Number of developers: " + developers.size());		
		//Iterator<Developer> it = developers.iterator();
		//while(it.hasNext())
		//	System.out.println(it.next().toString());
		
		System.out.println("Number of publishers: " + publishers.size());
		Iterator<Publisher> it2 = publishers.iterator();
		while(it2.hasNext())
		{
			Publisher publisherRecord = it2.next();
		String convert = publisherRecord.getName().replaceAll("[^\\x20-\\x7e]", " ").replaceAll(" {2,}"," ");
			System.out.println(convert);
//			ByteBuffer buffer = Charset.forName("UTF-8").encode(publisherRecord.getName());
	//		String convertedString = new String(buffer.array(), Charset.forName("UTF-8"));
		//	System.out.println(convertedString);
			//System.out.println(publisherRecord.toString());
		}
	}
	
	public void printSize()
	{
		//int size = developers.size() + publishers.size();
		//System.out.println("Number of Companies: " + size);
		//System.out.println("Number of developers: " + developers.size());
		System.out.println("Number of publishers: " + publishers.size());		
	}
	
	//Event Handlers
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException 
	{
		if(qName.equalsIgnoreCase(PUBLISHER))
		{
			company = new Publisher();
		}
		else if(qName.equalsIgnoreCase(DEVELOPER))
		{
			company = new Developer();
		}
		else if(qName.equalsIgnoreCase(PLATFORMS))
		{
			//initialize a platforms list
			company.initializePlatformList();		
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		tempVal = new String(ch , start, length);//Charset.forName("ISO-8859-1").name());
	}
	
	@Override
    public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if(qName.equalsIgnoreCase(PUBLISHER))
		{
			publishers.add((Publisher)company);
		}
		else if(qName.equalsIgnoreCase(DEVELOPER))
		{
			developers.add((Developer)company);
		}
		else if(qName.equalsIgnoreCase(NAME))
		{
			company.setName(tempVal);
		}
		else if(qName.equalsIgnoreCase(FOUNDED))
		{
			company.setFoundedYear(tempVal);
		}
		else if(qName.equalsIgnoreCase(REVENUE))
		{
			company.setRevenue(tempVal);
		}
		else if(qName.equalsIgnoreCase(PLATFORM))
		{
			//add platform to specified company type
			company.addPlatform(tempVal);
		}
			
	}
	
	//only inserting publishers..
	public void insertIntoDatabase()
	{
		try
		{
			//create a db connection
			Connection dbcon = DBConnection.create();
			
			//turn off autocommit
			dbcon.setAutoCommit(false);
			
			//write insert sql query
			String insertQuery = "INSERT INTO publishers (publisher, founded) VALUES (?,?)";
		
			//Create a preparedStatement via db connection
			PreparedStatement insertStatement = dbcon.prepareStatement(insertQuery);
			//loop through every entry in publishers and set the prepared  statments params accordingly
			Iterator<Publisher> it = publishers.iterator();
			while(it.hasNext())//naive way of inserting items into the games database.
			{
				Publisher publisherRecord = it.next();
				//remove all special characters not supported by mysql database.
				String convert = publisherRecord.getName().replaceAll("[^\\x20-\\x7e]", " ").replaceAll(" {2,}"," ");
				insertStatement.setString(1, convert);
				insertStatement.setString(2, publisherRecord.getFoundedYear());
				insertStatement.addBatch();
				
				//insertStatement.executeUpdate();
			}
			
			//execute batch
			insertStatement.executeBatch();
			dbcon.commit();
			
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
		
		CompaniesParser c = new CompaniesParser();
		long startTime = System.nanoTime();
		c.parseDocument(args[0]);
		//c.parseDocument("newGames/companies.xml");
		c.insertIntoDatabase();
		long endTime = System.nanoTime();	
		long elapsedTime = (endTime - startTime) / 1000000;
		
		System.out.println("Parse Execution Time: " + elapsedTime + " ms");
		c.printSize();
		//c.printData();
	}
	
}