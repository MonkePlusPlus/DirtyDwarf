import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Player {

	private Data data;
	public int x;
	public int y;
	public int posX;
	public int posY;
	public int bonus = 1;

	private int axisX = 0;
	private int axisY = 0;

	private int sizeTile = 24;

	private BufferedImage imageTile;
	private BufferedImage image;
	private BufferedImage[][] animImage;
	public Player(Data data){
		this.data = data;
		try {
			this.imageTile = ImageIO.read(new File("asset/old/player.png"));
			this.image = imageTile.getSubimage(0, 0, sizeTile, sizeTile);
		} catch (IOException e){
			e.setStackTrace(null);
		}
	}

	public void initializePlayer(){
		this.x = data.width / 2 - (data.size / 2);
		this.y = data.height / 2 - (data.size / 2);
		this.posX = x;
		this.posY = y;
	}

	public void draw(Graphics2D g){
		g.drawImage(image, x, y, data.size, data.size, null);
	}
	
	public void update(){
		if (data.key.up){
			//System.out.println("move up");
		}
		if (data.key.down){
			//System.out.println("move down");
		}
		if (data.key.left){
			image = imageTile.getSubimage(0, 0, sizeTile, sizeTile);
			//System.out.println("move left");
		}
		if (data.key.right){
			image = imageTile.getSubimage(sizeTile, 0, sizeTile, sizeTile);
			//System.out.println("move right");
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
