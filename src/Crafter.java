
import java.awt.image.BufferedImage;

public class Crafter extends Block {
	
	public enum CrafterType {
		POLYVALENT,
		NORMAL
	}

	private CrafterType type;

	public Crafter(Data data, CrafterType type, int x, int y, boolean collision, BufferedImage image){
		super(data, x, y, collision, image);
		this.type = type;

	}
}
