package model;

//see Company.java for more implementation details
public class Publisher extends Company
{
	public static final String TYPE = "publisher";
	
	public Publisher() { super(); }
	
	@Override
	public String toString()
	{
		return "Company type: " + TYPE + "\n" + super.toString();	
	}
}