import java.awt.image.BufferedImage;

public class Ressource extends Block {

	private Object object;

	public Ressource(Object o, Data data, int x, int y, boolean col, BufferedImage image){
		super(data, x, y, col, image);
		this.object = o;
	}
}
