package model;

//see Company.java for more implementation details
public class Developer extends Company
{
	public static final String TYPE = "developer";
	
	public Developer() { super(); }
	
	@Override
	public String toString()
	{
		return "Company type: " + TYPE + "\n" + super.toString();	
	}
}