
import java.awt.image.BufferedImage;

public class Recipe extends Item {
	
	public String ingredient;
	public Slot[] ingredients;
	public int nbIngredient;

	public Recipe(String name, String ingredient, String symb, int time, BufferedImage image){
		super(name, symb, time, image);
		this.ingredient = ingredient;
	}

	public void createIngredient(Item[][] listItems){
		System.out.println(name + " = " + ingredient);
		String[] items = ingredient.split("\\+");
		this.nbIngredient = items.length;
		ingredients = new Slot[nbIngredient];
		for (int i = 0;i < nbIngredient; i++){
			String[] item = items[i].split("-");
			ingredients[i] = new Slot(getItem(item[1], listItems), Integer.valueOf(item[0]));
		}
	}

	public Item getItem(String name, Item[][] items){
		for (int n = 0; n < 2; n++){
			for (Item i : items[n]){
				if (i.name.equals(name)){
					return i;
				}
			}
		}
		return null;
	}
}
