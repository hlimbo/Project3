import model.Employee;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class SAXParserExample extends DefaultHandler {

    List<Employee> myEmpls;

    private String tempVal;

    //to maintain context
    private Employee tempEmp;
	
	//total number of times characters event is called
	private int charactersEventCount = 0;
	private int startElementEventCount = 0;
	private int endElementEventCount = 0;
	
	private List<String> values;
	private int charCount = 0;

    public SAXParserExample() {
        myEmpls = new ArrayList<Employee>();
		values = new ArrayList<String>();
    }

    public void runExample() {
        parseDocument();
        printData();
		
		/*System.out.println("characters callback count: " + charactersEventCount);
		System.out.println("startElement callback count: " + startElementEventCount);
		System.out.println("endElement callback count: " + endElementEventCount);
    
		int newlineCount = 0;
		int actualCharCount = 0;
		for(String value : values)
		{
			if(value.contains("\t"))
				System.out.println("tab");
			if(value.contains("\n"))
				System.out.println("newline: " + (++newlineCount) + " value.length(): " + value.length());
			else if(value.trim().length() == 0)
				System.out.println("whitespace: " + value.length());
			
			actualCharCount += value.trim().length();
		}
		
		
		System.out.println("values size: " + values.size());
		System.out.println("char count: " + charCount);
		System.out.println("actual char count: " + actualCharCount);
		*/
		for(int i = 0;i < values.size(); ++i)
		{
			System.out.println( (i+1) + "\t" + values.get(i));
		}
	}

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("employees.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Iterate through the list and print
     * the contents
     */
    private void printData() {

        System.out.println("No of Employees '" + myEmpls.size() + "'.");

        Iterator<Employee> it = myEmpls.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

    //Event Handlers
	@Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        
		System.out.println("startElement: " + uri + ", " + localName + ", " + qName);
		++startElementEventCount;
		//reset
        tempVal = "";
        if (qName.equalsIgnoreCase("Employee")) {
            //create a new instance of employee
            tempEmp = new Employee();
            tempEmp.setType(attributes.getValue("type"));
        }
    }

	@Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
		charCount += length;
		values.add(tempVal);
		++charactersEventCount;
    }

	@Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
		
		++endElementEventCount;
        if (qName.equalsIgnoreCase("Employee")) {
            //add it to the list
            myEmpls.add(tempEmp);

        } else if (qName.equalsIgnoreCase("Name")) {
            tempEmp.setName(tempVal);
        } else if (qName.equalsIgnoreCase("Id")) {
            tempEmp.setID(Integer.parseInt(tempVal));
        } else if (qName.equalsIgnoreCase("Age")) {
            tempEmp.setAge(Integer.parseInt(tempVal));
        }

    }
	
    public static void main(String[] args) {
        SAXParserExample spe = new SAXParserExample();
        spe.runExample();
    }

}