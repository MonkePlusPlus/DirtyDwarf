
import java.awt.image.BufferedImage;

public class Item extends Object {
	
	public String name;
	public String symb;
	public int time;
	public BufferedImage image;

	public Item(String name, String symb, int time, BufferedImage image){
		this.name = name;
		this.symb = symb;
		this.time = time;
		this.image = image;
	}

	public String getSymb(){
		return symb;
	}

    @Override
    public ObjectType getType() {
        return ObjectType.ITEM;
    }
}
