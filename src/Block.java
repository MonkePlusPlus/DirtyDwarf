
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Block {
	private boolean collision;
	private BufferedImage image;
	private int width;
	private int height;
	private int size;
	private int speed;
	private int x;
	private int y;
	private int posY;
	private int posX;
	KeyHandler key;

	public Block(int width, int height, int size, int x, int y, boolean col, BufferedImage image, KeyHandler key, int speed){
		this.width = width;
		this.height = height;
		this.size = size;
		this.x = x;
		this.y = y;
		this.collision = col;
		this.image = image;
		this.posX = x * size;
		this.posY = y * size;
		this.key = key;
		this.speed = speed;
	}

	public void moveBlockX(int speed){
		posX += speed;
	}

	public void moveBlockY(int speed){
		posY += speed;
	}

	public void drawBlock(Graphics2D g){
		if (posX >= -48 && posX <= width + 48 &&
			posY >= -48 && posY <= height + 48 && image != null) {
			g.drawImage(image, posX, posY, null);
		}
	}

	public void InitializePos(int startX, int startY){
		int centerX = width / 2 - (size / 2);
		int centerY = height / 2 - (size / 2);
		x = x - startX;
		y = y - startY;
		posX = centerX + (x * size);
		posY = centerY + (y * size);
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
}
