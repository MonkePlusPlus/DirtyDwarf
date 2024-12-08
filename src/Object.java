
public abstract class Object{

	public enum ObjectType {
		ITEM,
		RECIPE,
		POTION
	}

	public abstract ObjectType getType();
}