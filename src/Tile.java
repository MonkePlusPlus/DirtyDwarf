
public abstract class Tile {

	public enum TileType {
		WALL,
		FLOOR,
		RESSOURCE,
		COLLECTIBLE,
		CRAFTER,
		SHOP,
		EXIT
	}

	public abstract TileType getType();

	public abstract void mouseClick();
}
