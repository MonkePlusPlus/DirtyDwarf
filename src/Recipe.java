
import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class Recipe extends Item {
	
	public String ingredient;
	public Slot[] ingredients;
	public int nbIngredient;

	public Recipe(String name, String ingredient, String symb, int time, int price, BufferedImage image){
		super(name, symb, time, price, image, true);
		this.ingredient = ingredient;
	}

	public void createIngredient(LinkedList<Object>[] listObj){
		System.out.println(name + " = " + ingredient);
		String[] items = ingredient.split("\\+");
		this.nbIngredient = items.length;
		ingredients = new Slot[nbIngredient];
		for (int i = 0;i < nbIngredient; i++){
			String[] item = items[i].split("-");
			ingredients[i] = new Slot(getItem(item[1], listObj), Integer.valueOf(item[0]));
		}
	}

	public Item getItem(String name, LinkedList<Object>[] items){
		for (int n = 0; n < 2; n++){
			for (Object o : items[n]){
				Item i = (Item)o;
				if (i.name.equals(name)){
					return i;
				}
			}
		}
		return null;
	}

    @Override
    public ObjectType getType() {
        return ObjectType.RECIPE;
    }
}
