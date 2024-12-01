
import java.awt.image.BufferedImage;

public class Item {
	
	public String name;
	public String symb;
	public int time_to_make;
	public BufferedImage image;

	public Item(String name, String symb, int time, BufferedImage image){
		this.name = name;
		this.symb = symb;
		this.time_to_make = time;
		this.image = image;
	}

	public String getSymb(){
		return symb;
	}
}
