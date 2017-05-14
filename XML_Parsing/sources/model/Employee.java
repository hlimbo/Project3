package model;

public class Employee
{
	private String type;
	private String name;
	private int id;
	private int age;
	
	public Employee() {}
	
	public Employee(String name, int id, int age, String type)
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
	
	@Override
	public String toString()
	{
		return "Employee Details - Name:" + name + ", Type:" + type + ", Id:" + id + ", Age:" + age;
	}
		
}