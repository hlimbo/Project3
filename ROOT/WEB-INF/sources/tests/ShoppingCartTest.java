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
	
	@Test
	public void testShoppingCartForNull()
	{
		ShoppingCart cart = new ShoppingCart();
		cart.put("1", new ShoppingCartItem("Wii Sports", 18, 1));
		cart.put("2", new ShoppingCartItem("Super Mario Bros.", 69, 1));
		cart.put("3", new ShoppingCartItem("Mario Kart Wii", 8, 4));
		cart.put("4", new ShoppingCartItem("Wii Sports Resort", 30, 2));
		
		for(Map.Entry<String,ShoppingCartItem> entry : cart.getItems().entrySet())
		{
			assertNotNull(entry);
		}
	}
	
	@Test
	public void testShoppingCartPrice()
	{
		ShoppingCart cart = new ShoppingCart();
		cart.put("1", new ShoppingCartItem("Wii Sports", 18, 1));
		cart.put("2", new ShoppingCartItem("Super Mario Bros.", 69, 1));
		cart.put("3", new ShoppingCartItem("Mario Kart Wii", 8, 4));
		cart.put("4", new ShoppingCartItem("Wii Sports Resort", 30, 2));
		
		assertEquals(18, (int)cart.getItem("1").getPrice());
		assertEquals(69, (int)cart.getItem("2").getPrice());
		assertEquals(8, (int)cart.getItem("3").getPrice());
		assertEquals(30, (int)cart.getItem("4").getPrice());
	}
	
	@Test
	public void testShoppingCartQuantity()
	{
		ShoppingCart cart = new ShoppingCart();
		cart.put("1", new ShoppingCartItem("Wii Sports", 18, 1));
		cart.put("2", new ShoppingCartItem("Super Mario Bros.", 69, 1));
		cart.put("3", new ShoppingCartItem("Mario Kart Wii", 8, 4));
		cart.put("4", new ShoppingCartItem("Wii Sports Resort", 30, 2));
		
		assertEquals(1, (int)cart.getItem("1").getQuantity());
		assertEquals(1, (int)cart.getItem("2").getQuantity());
		assertEquals(4, (int)cart.getItem("3").getQuantity());
		assertEquals(2, (int)cart.getItem("4").getQuantity());
	}
	
	@Test
	public void testShoppingCartTotalPrice()
	{
		ShoppingCart cart = new ShoppingCart();
		cart.put("1", new ShoppingCartItem("Wii Sports", 18, 1));
		cart.put("2", new ShoppingCartItem("Super Mario Bros.", 69, 1));
		cart.put("3", new ShoppingCartItem("Mario Kart Wii", 8, 4));
		cart.put("4", new ShoppingCartItem("Wii Sports Resort", 30, 2));
		
		Integer actualPrice = cart.getTotalPrice();
		Integer expectedPrice = 0;
		for(Map.Entry<String,ShoppingCartItem> entry : cart.getItems().entrySet())
			expectedPrice += entry.getValue().getTotalPrice();
		
		assertEquals((int)expectedPrice, (int)actualPrice);
	}
}