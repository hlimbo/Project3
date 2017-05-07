package tutorials.unit;

//Note: Have bash script place tests in a test folder after compiling!
//Note: can't package Test classes I don't know why that is

//To run the associated java class file:
//https://github.com/junit-team/junit4/wiki/Getting-started
//java -cp .;junit-4.XX.jar;hamcrest-core-1.3.jar org.junit.runner.JUnitCore CalculatorTest


//the class I want to test ~ nvm
//import tutorials.unit.Calculator;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class CalculatorTest
{
	@Test
	public void evaluatesExpression()
	{
		Calculator calculator = new Calculator();
		int sum = calculator.evaluate("1+2+3");
		assertEquals(6,sum);
	}
}