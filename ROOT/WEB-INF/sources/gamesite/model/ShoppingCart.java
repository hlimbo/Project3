package gamesite.model;

/* Container model class that will be set as 
   an attribute during a user's session on the website.
*/

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
		if(items.containsKey(gameId))
		{
			ShoppingCartItem existing_item = items.get(gameId);
			if(existing_item != null)
				existing_item.setQuantity(existing_item.getQuantity() + 1);
			else
				items.put(gameId, item);
		}
		else
		{
			items.put(gameId, item);
		}
	}
	
	public ShoppingCartItem getItem(String gameId)
	{
		if(items.containsKey(gameId))
			return items.get(gameId);
		
		return null;
	}
	
	public int size() { return items.size(); }
	
	//used for iterating through a map
	//	e.g. for(Map.entry<String,ShoppingCart entry : itemSet);
	public Set<Map.Entry<String,ShoppingCartItem>> itemSet()
	{
		return items.entrySet();
	}
	
	public Integer getTotalPrice()
	{
		Integer totalPrice = 0;
		for (Map.Entry<String,ShoppingCartItem> entry : items.entrySet())
		{
			ShoppingCartItem item = entry.getValue();
			totalPrice += item.getTotalPrice();
		}
		
		return totalPrice;
	}
}