import java.awt.image.BufferedImage;

public abstract class Tile {

	public enum TileType {
		NONE,
		WALL,
		FLOOR,
		RESSOURCE,
		COLLECTER,
		CRAFTER,
		SHOP,
		SPAWN
	}

	public BufferedImage image;
	public boolean collision;
	public int x;
	public int y;
	public int posY;
	public int posX;
	public String symb;

	public abstract TileType getType();

	public abstract void mouseClick();

	public abstract boolean isTouched();

	public abstract boolean touchPlayer();
}
