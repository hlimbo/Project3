import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

public class test
{
	public static void main(String[] args)
	{
		//testing db connection
		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			
			String user = "user";
			String password = "password";
			String loginUrl = "jdbc:mysql://localhost:3306/gamedb?useSSL=true";
			Connection dbcon = DriverManager.getConnection(loginUrl, user, password);
			
			System.out.println("Successfully opening gamedb database");
			
			dbcon.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(java.lang.Exception e)
		{
			e.printStackTrace();
		}
	}
	
}