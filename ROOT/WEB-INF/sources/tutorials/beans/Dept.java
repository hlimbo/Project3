package tutorials.beans;

import java.util.HashSet;
import java.util.Set;


//http://o7planning.org/en/10429/java-jsp-standard-tag-library-jstl-tutorial
//A bean java class is an object that can be instantiated via JSP/JSTL


//3 rules of writing a Bean/Model class (Supposedly good programming java conventions)
//1. All bean objects must have a zero-argument default constructor
//2. All fields declared in bean class must be private
//3. All fields are accessed via Getters and Setters

public class Dept
{
	//each variable that is private needs to have getters and setters in order for JSP
	//to access these values using the jsp:setProperty or jsp:getProperty tags
	private int deptNo;
	private String deptName;
	private String location;
	
	private Set<Emp> employees;
	
	//All bean objects need to have a default constructor!
	public Dept()
	{		
	}
	
	public Dept(int deptNo, String deptName, String location)
	{
		this.deptNo = deptNo;
		this.deptName = deptName;
		this.location = location;
	}
	
	public int getDeptNo() { return deptNo; }
	public void setDeptNo(int deptNo){ this.deptNo = deptNo; }
	
	public String getDeptName() { return deptName; }
	public void setDeptName(String deptName) { this.deptName = deptName; }
	
	public String getLocation() { return location; }
	public void setLocation(String location) { this.location = location; }
	
	public Set<Emp> getEmployees() { return employees; }
	
	//must be careful when using this method as it can be used as:
	//1. setEmployees(new Set<Emp>()); to instantiate a new copy employee set.
	//2. setEmployees(otherEmployeeSet); to change employee set reference, which can result in memory leaks
	//since the old reference of employees is lost after assigning employees new reference to otherEmployeeSet.
	public void setEmployees(Set<Emp> employees) { this.employees = employees; }
	
	public void addEmployee(Emp employee)
	{
		if(this.employees == null) { this.employees = new HashSet<Emp>(); }
		this.employees.add(employee);
	}
	
}