
import java.awt.image.BufferedImage;


public abstract class Object{

	public enum ObjectType {
		ITEM,
		RECIPE,
		MACHINE,
		POTION
	}

	public int price;
	public int time;
	public String name;
	public String symb;
	public BufferedImage image;

	public abstract ObjectType getType();
}