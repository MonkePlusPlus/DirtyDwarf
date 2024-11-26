
import java.awt.image.BufferedImage;

public class Recipe extends Item {
	
	String ingredient;

	public Recipe(String name, String ingredient, String symb, int time, BufferedImage image){
		super(name, symb, time, image);
		this.ingredient = ingredient;
	}

}
