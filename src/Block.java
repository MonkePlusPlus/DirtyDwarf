
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Block {
	private Data data;
	private boolean collision;
	private BufferedImage image;
	private int x;
	private int y;
	private int posY;
	private int posX;

	public Block(Data data, int x, int y, boolean col, BufferedImage image){
		this.data = data;
		this.x = x;
		this.y = y;
		this.collision = col;
		this.image = image;
		this.posX = x * data.size;
		this.posY = y * data.size;
	}

	public void moveBlockX(int speed){
		posX += speed;
	}

	public void moveBlockY(int speed){
		posY += speed;
	}

	public void drawBlock(Graphics2D g){
		if (posX >= -48 && posX <= data.width + 48 &&
			posY >= -48 && posY <= data.height + 48 && image != null) {
			g.drawImage(image, posX, posY, null);
		}
	}

	public void InitializePos(int startX, int startY){
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
}
