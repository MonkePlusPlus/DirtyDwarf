import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Player {

	private int x;
	private int y;
	private int posX;
	private int posY;
	private int width;
	private int height;
	private int speed = 4;
	private int axisX = 0;
	private int axisY = 0;
	private int size = 48;
	private KeyHandler key;

	private BufferedImage imageTile;
	private BufferedImage image;
	private BufferedImage[][] animImage;

	public Player(int width, int height, KeyHandler keyH, int size){
		this.width = width;
		this.height = height;
		this.key = keyH;
		this.size = size;
		try {
			this.imageTile = ImageIO.read(new File("asset/player.png"));
			this.image = imageTile.getSubimage(0, 0, 24, 24);
		} catch (IOException e){
			e.setStackTrace(null);
		}
	}

	public void InitializePlayer(int x, int y, int speed){
		this.x = width / 2 - (size / 2);
		this.y = height / 2 - (size / 2);
		this.posX = x;
		this.posY = y;
		this.speed = speed;
	}

	public void draw(Graphics2D g){
		g.drawImage(image, x, y, size, size, null);
	}
	
	public void update(){
		if (key.up){
			System.out.println("move up");
		}
		if (key.down){
			System.out.println("move down");
		}
		if (key.left){
			image = imageTile.getSubimage(0, 0, 24, 24);
			System.out.println("move left");
		}
		if (key.right){
			image = imageTile.getSubimage(24, 0, 24, 24);
			System.out.println("move right");
		}
	}

	public Image getImage(){
		return this.image;
	}

	public int getX(){
		return this.x;
	}

	public int getY(){
		return this.y;
	}

	public int getAxisX(){
		return this.axisX;
	}

	public int getAxisY(){
		return this.axisY;
	}
}
