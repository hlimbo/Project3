package model;

import java.util.List;
import java.util.ArrayList;

//Company can either be a developer or a publisher
public class Company
{
	protected String name;
	protected String foundedYear;
	protected String revenue;
	protected List<String> platforms;

	public Company() {}
	
	public String getName() { return name; }
	public String getFoundedYear() { return foundedYear; }
	public String getRevenue() { return revenue; }
		
	public void setName(String name) { this.name = name; }
	public void setFoundedYear(String foundedYear) { this.foundedYear = foundedYear; }
	public void setRevenue(String revenue) { this.revenue = revenue; }
	
	public void initializePlatformList()
	{
		platforms = new ArrayList<String>();
	}
	
	public void addPlatform(String platform)
	{
		platforms.add(platform);
	}
	
	@Override
	public String toString()
	{
		String returnString = name + " | " + foundedYear + " | " + revenue;
		
		returnString += "\nPlatforms: ";
		for(String platform : platforms)
			returnString += platform + " | ";
		
		return returnString;
		
	}
	
}