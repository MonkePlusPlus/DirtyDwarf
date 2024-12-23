
import java.awt.image.BufferedImage;

public class Item extends Object {
	
	public String symb;
	public int time;
	public boolean type; // true cant move false can move

	public Item(String name, String symb, int time, int price, BufferedImage image, boolean type){
		this.name = name;
		this.symb = symb;
		this.time = time;
		this.price = price;
		this.image = image;
		this.type = type;
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
