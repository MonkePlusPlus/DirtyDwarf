
public abstract class Object{

	public enum ObjectType {
		ITEM,
		RECIPE,
		MACHINE,
		POTION
	}

	public abstract ObjectType getType();
}