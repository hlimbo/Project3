package xml.model;

import java.util.*;

public class Game
{
	//gameTitle will be the key to lookup in the database
	private String gameTitle;
	
	//Key = id retrieved from games.xml Note: this id is not associated in the games database.
	//Value = platformInfo
	private Map<Integer,PlatformInfo> releasePlatforms;
	
	//This is required if this object will be used in a servlet script
	public Game() 
	{
		releasePlatforms = new HashMap<Integer,PlatformInfo>();
	}
	
	public Game(String gameTitle)
	{
		this.gameTitle = gameTitle;
		releasePlatforms = new HashMap<Integer,PlatformInfo>();
	}
	//returns null if id not found or does not match with associated platforminfo
	//lookup may be slow if the same game is released on more than a HUGE AMOUNT OF platforms
	public Integer getID(PlatformInfo info) 
	{
			for(Map.Entry<Integer,PlatformInfo> entry : releasePlatforms.entrySet())
			{
				boolean isEqual = releasePlatforms.get(entry.getKey()).equals(info);
				if(isEqual)
					return entry.getKey();
			}
			
		return null;
	}
	
	public String getGameTitle() { return gameTitle; }
	//Note: may return null if platform string is null
	public String getPlatform(Integer id) { return releasePlatforms.get(id).getPlatform(); }
	//Note: may return null if release date string is null
	public String getReleaseDate(Integer id) { return releasePlatforms.get(id).getReleaseDate(); }
	
	public void setGameTitle(String gameTitle) { this.gameTitle = gameTitle; }
	
	public void put(Integer id, PlatformInfo info) { releasePlatforms.put(id, info); }
	public void put(Integer id, String platform, String releaseDate) { releasePlatforms.put(id, new PlatformInfo(platform, releaseDate)); }
		
	//returns the number of platforms the game was released in.
	public int size(){	return releasePlatforms.size(); }	
	public Set<Map.Entry<Integer,PlatformInfo>> entrySet() { return releasePlatforms.entrySet(); }
}