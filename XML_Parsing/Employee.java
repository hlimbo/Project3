
public class Employee
{
	private String type;
	private String name;
	private int id;
	private int age;
	
	public Employee() {}
	
	public Employee(String type, String name, int id, int age)
	{
		this.type = type;
		this.name = name;
		this.id = id;
		this.age = age;
	}
	
	public String getType() { return type; }
	public String getName() { return name; }
	public int getID() { return id; }
	public int getAge() { return age; }
	
	public void setType(String type) { this.type = type; }
	public void setName(String name) { this.name = name; }
	public void setID(int id) { this.id = id; }
	public void setAge(int age) { this.age = age; }
		
}