public class ShoppingCartItem{

	//item name
	public String name;
	public float price;
	public int quantity;
	
	public ShoppingCartItem(String name, float price)
	{
		this.name = name;
		this.price = price;
		quantity = 1;
	}
	
	public ShoppingCartItem(String name, float price, int quantity)
	{
		this.name = name;
		this.price = price;
		this.quantity = quantity;
	}
	
	public float getTotalPrice()
	{
		return price * quantity;
	}

}