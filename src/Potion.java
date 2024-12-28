
import javax.swing.Timer;

public abstract class Potion extends Object {

	public enum PotionType {
		BLOCK,
		PLAYER
	}

	public Timer timer;
	public String showName;

	public abstract void usePotion();

	public abstract PotionType getPotionType();

	@Override
	public Object.ObjectType getType() {
		return Object.ObjectType.POTION;
	}
	
}
