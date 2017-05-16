package xml.model;

//class will be used as a model to contain all parsed information from
//games.xml ~ does not worry about duplicate games e.g. same game but different platform

public class SimpleGame
{
	private Integer id;
	private String gameTitle;
	private String releaseDate;
	private Integer price;
	
	public SimpleGame() {}
	public SimpleGame(Integer id, String gameTitle, String releaseDate, Integer price)
	{
		this.id = id;
		this.gameTitle = gameTitle;
		this.releaseDate = releaseDate;
		this.price = price;
	}
	
	public Integer getID() { return id; }
	public String getGameTitle() { return gameTitle; }
	public String getReleaseDate() { return releaseDate == null ? "1/1/2017" : releaseDate; }
	public Integer getPrice() { return price; }
	
	public void setID(Integer id) { this.id = id; }
	public void setGameTitle(String gameTitle) { this.gameTitle = gameTitle; }
	public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
	public void setPrice(Integer price) { this.price = price; }

	@Override
	public String toString()
	{
		return id.toString() + ". " + gameTitle + " | " + releaseDate + " | " +  price;
	}
	
	//Return null if releaseDate is null or empty.
	public String getReleaseYear()
	{
		if(releaseDate != null && !releaseDate.isEmpty())
		{
			int lastIndex = releaseDate.lastIndexOf('/');
			if(lastIndex == -1)
				return "2017";
			
			String year = releaseDate.substring(lastIndex + 1);
			return year;
		}
		
		return "2017";
	}
}