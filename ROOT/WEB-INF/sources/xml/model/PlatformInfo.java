package xml.model;

//this class is used in Game.java
public class PlatformInfo
{
	private String platform;
	private String releaseDate;
	
	public PlatformInfo() {}
	public PlatformInfo(String platform, String releaseDate)
	{
		this.platform = platform;
		this.releaseDate = releaseDate;
	}
	
	public String getPlatform() { return platform; }
	//TODO(HARVEY): write a getter method that returns the year only.
	public String getReleaseDate() { return releaseDate; }
	
	public void setPlatform(String platform) { this.platform = platform; }
	public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
		
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null)
			return false;
		
		if(!(obj instanceof PlatformInfo))
			return false;
		
		PlatformInfo other = (PlatformInfo)obj;
		if(this.platform.equals(other.platform) && this.releaseDate.equals(other.releaseDate))
			return true;
		
		return false;
	}
	
	@Override
	public String toString()
	{
		return "platform: " + platform + "| releaseDate: " + releaseDate;
	}
}