
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Block extends Tile {
	public Data data;
	public boolean collision;
	public BufferedImage image;
	public int x;
	public int y;
	public int posY;
	public int posX;

	public Block(Data data, int x, int y, boolean col, BufferedImage image){
		this.data = data;
		this.x = x;
		this.y = y;
		this.collision = col;
		this.image = image;
		this.posX = x * data.size;
		this.posY = y * data.size;
	}

	public TileType getType(){
		return ((collision) ? TileType.FLOOR : TileType.WALL);
	}

	public void moveBlockX(int speed){
		posX += speed;
	}

	public void moveBlockY(int speed){
		posY += speed;
	}

	public void drawBlock(Graphics2D g){
		if (posX >= -data.size && posX <= data.width + data.size &&
			posY >= -data.size && posY <= data.height + data.size && image != null) {
			g.drawImage(image, posX, posY, data.size, data.size, null);
		}
	}

	public void initializePos(int startX, int startY){
		int centerX = data.width / 2 - (data.size / 2);
		int centerY = data.height / 2 - (data.size / 2);
		x = x - startX;
		y = y - startY;
		posX = centerX + (x * data.size);
		posY = centerY + (y * data.size);
	}

	public void setX(int x){
		this.x = x;
	}

	public void setY(int y){
		this.y = y;
	}

	public void setPos(int x, int y){
		this.posX = x;
		this.posY = y;
	}

	public int getPosX(){
		return posX;
	}

	public int getPosY(){
		return posY;
	}

	public boolean getCollission(){
		return collision;
	}

	public double getDistance(){
		int centerX = data.width / 2 - (data.size / 2);
		int centerY = data.height / 2 - (data.size / 2);

		double distance = Math.sqrt(Math.pow(Math.abs(centerX - (posX + data.size / 2)), 2)
						+ Math.pow(Math.abs(centerY - (posY + data.size / 2)), 2));
		
		return distance;
	}

	@Override
	public void mouseClick(){
		
	}
}
