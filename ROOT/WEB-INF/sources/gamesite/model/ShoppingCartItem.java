package gamesite.model;

//goal: to cache results found from query into this data structure
//purpose: to minimize the amount of times needed to query the database for shopping cart items
public class ShoppingCartItem{

	private String gameName;
	private Integer price;
	private Integer quantity;
	
	//all bean aka model classes need to have a zero argument default constructor
	public ShoppingCartItem() {}
	
	public ShoppingCartItem(String gameName, Integer price, Integer quantity)
	{
		this.gameName = gameName;
		this.price = price;
		this.quantity = quantity;
	}
	
	public String getGameName() { return gameName; }
	public void setGameId(String gameName) { this.gameName = gameName; }
	
	public Integer getPrice() { return price; }
	public void setPrice(Integer price) { this.price = price; }
	
	public Integer getQuantity() { return quantity; }
	public void setQuantity(Integer quantity) { this.quantity = quantity; }
	
	public Integer getTotalPrice() { return price * quantity; }
}