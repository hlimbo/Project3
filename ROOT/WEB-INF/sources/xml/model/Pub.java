package xml.model;

import java.util.List;
import java.util.ArrayList;

public class Pub
{
	private String p_id;//id retrieved from pubs.xml file Note: not the same id in the database
	private String p;//publisher name
	private String fnd;//foundedYear
	private ArrayList<Gplt> platform_of_game;
	
	public Pub() {}
	public Pub(String p_id, String p, String fnd)
	{
		this.p_id = p_id;
		this.p = p;
		this.fnd = fnd;
	}
	
	public String getPubID() { return p_id; }
	public String getPublisher() { return p; }
	public String getFoundedYear() { return fnd; }
	
	public void setPubID(String p_id) { this.p_id = p_id; }
	public void setPublisher(String p) { this.p = p; }
	public void setFoundedYear(String fnd) { this.fnd = fnd; }
	
	public void initializePlatformGameList()
	{
		platform_of_game = new ArrayList<Gplt>();
	}
	
	public boolean isPlatformGameListNull()
	{
		return platform_of_game == null;
	}
	
	public void addGamePlatformRecord(Gplt record)
	{
		if(platform_of_game != null)
			platform_of_game.add(record);
		else
			System.out.println("Warning: PlatformGameList not initialized! Record not added");
	}
	
	public void addGamePlatformRecord(String gameTitle, String platform)
	{
		if(platform_of_game != null)
			platform_of_game.add(new Gplt(gameTitle, platform));
		else
			System.out.println("Warning: PlatformGameList not initialized! Record not added");
	}
	
	public ArrayList<Gplt> getList() { return platform_of_game; }
	
	@Override
	public String toString()
	{
		String returnString =  p_id + ": " + p + " | " + fnd + " | ";
		
		for(Gplt record : platform_of_game)
		{
			returnString += "\n\t" + record.toString();
		}
		
		return returnString;
	}
}