package gamesite.model;

/* Container model class that will be set as 
   an attribute during a user's session on the website.
*/

import java.util.HashMap;

public class ShoppingCart
{
	//key = game_id, value = shopping cart attributes obtained from games schema
	private HashMap<String,ShoppingCartItem> items;
	
	public ShoppingCart() 
	{ 
		items = new HashMap<String,ShoppingCartItem>();
	}
	
	public HashMap<String,ShoppingCartItem> getItems() { return items; }
	//use this function to clear the cart.
	public void setItems(HashMap<String, ShoppingCartItem> items) { this.items = items; }
	
	public void put(String gameId, ShoppingCartItem item)
	{
		items.put(gameId, item);
	}
	
	public ShoppingCartItem getItem(String gameId)
	{
		if(items.containsKey(gameId))
			return items.get(gameId);
		
		return null;
	}
}