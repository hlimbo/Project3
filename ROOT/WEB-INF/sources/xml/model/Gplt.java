package xml.model;

//platform of games
public class Gplt
{
	public String gameTitle;//<g>
	public String platform;//<plt>
	
	public Gplt() {}
	public Gplt(String gameTitle, String platform)
	{
		this.gameTitle = gameTitle;
		this.platform = platform;
	}
	
	@Override
	public String toString()
	{
		return "[ gameTitle: " + gameTitle +  ", platform: " + platform + " ]";
	}
}