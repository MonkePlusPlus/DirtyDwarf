
import java.awt.image.BufferedImage;


public abstract class Object{

	public enum ObjectType {
		ITEM,
		RECIPE,
		MACHINE,
		POTION
	}

	public int price;
	public String name;
	public BufferedImage image;

	public abstract ObjectType getType();
}