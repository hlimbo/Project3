package model;

//class will be used as a model to contain all parsed information from
//games.xml ~ does not worry about duplicate games e.g. same game but different platform

public class SimpleGame
{
	private Integer id;
	private String gameTitle;
	private String releaseDate;
	private String platform;
	
	public SimpleGame() {}
	public SimpleGame(Integer id, String gameTitle, String releaseDate, String platform)
	{
		this.id = id;
		this.gameTitle = gameTitle;
		this.releaseDate = releaseDate;
		this.platform = platform;
	}
	
	public Integer getID() { return id; }
	public String getGameTitle() { return gameTitle; }
	public String getReleaseDate() { return releaseDate; }
	public String getPlatform() { return platform; }
	
	public void setID(Integer id) { this.id = id; }
	public void setGameTitle(String gameTitle) { this.gameTitle = gameTitle; }
	public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
	public void setPlatform(String platform) { this.platform = platform; }

	@Override
	public String toString()
	{
		return id.toString() + ". " + gameTitle + " | " + releaseDate + " | " +  platform;
	}
	
	//Return null if releaseDate is null or empty.
	public String getReleaseYear()
	{
		if(releaseDate != null && !releaseDate.isEmpty())
		{
			int lastIndex = releaseDate.lastIndexOf('/');
			String year = releaseDate.substring(lastIndex);
			return year;
		}
		
		return null;
	}
}