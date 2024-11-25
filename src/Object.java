
import java.awt.image.BufferedImage;

public class Object {
	
	private String name;
	private String symb;
	private int time_to_make;
	private BufferedImage image;

	public Object(String name, String symb, int time, BufferedImage image){
		this.name = name;
		this.symb = symb;
		this.time_to_make = time;
		this.image = image;
	}

	public String getSymb(){
		return symb;
	}
}
