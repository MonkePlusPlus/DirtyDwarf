import java.awt.image.BufferedImage;

public abstract class Tile {

	public enum TileType {
		WALL,
		FLOOR,
		RESSOURCE,
		COLLECTER,
		CRAFTER,
		SHOP,
		EXIT
	}

	public BufferedImage image;
	public boolean collision;
	public int x;
	public int y;
	public int posY;
	public int posX;

	public abstract TileType getType();

	public abstract void mouseClick();

	public abstract boolean isTouched();
}
