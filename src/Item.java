
import java.awt.image.BufferedImage;

public class Item extends Object {
	
	public String name;
	public String symb;
	public int time;
	public BufferedImage image;
	public int price;

	public Item(String name, String symb, int time, int price, BufferedImage image){
		this.name = name;
		this.symb = symb;
		this.time = time;
		this.price = price;
		this.image = image;
	}

	public boolean checkPrice(Inventory inventory) {
		return (inventory.money <= price);
	}

	public String getSymb(){
		return symb;
	}

    @Override
    public ObjectType getType() {
        return ObjectType.ITEM;
    }
}
