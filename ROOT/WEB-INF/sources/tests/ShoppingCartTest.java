package tests;

//---------------- JUnit 4 testing imports -------------------------//
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.hamcrest.core.CombinableMatcher;
import org.junit.Test;
//-------------------------------------------------------

import java.util.*;

import gamesite.model.ShoppingCartItem;
import gamesite.model.ShoppingCart;

public class ShoppingCartTest
{
	@Test
	public void testShoppingCartItem()
	{
		ShoppingCartItem item = new ShoppingCartItem("Legends", 10, 1);
		
		assertEquals("Failure - item name not equal: ", "Legends", item.getGameName());
		assertEquals("Failure - item price not equal: ", 10, (int)item.getPrice());
		assertEquals("Failure - item quantity not equal: ", 1, (int)item.getQuantity());
		
	}
	
	@Test
	public void testShoppingCartItem2()
	{
		ShoppingCartItem item = new ShoppingCartItem("Breath of the Wild", 60, 5);
		assertEquals("Failure - item total price not equal: ", 300, (int)item.getTotalPrice());
	}
}