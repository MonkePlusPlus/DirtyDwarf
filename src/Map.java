import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class Map {
	
	private int width;
	private int height;
	private Game game;
	private File fileMap;
	private String[][] stringMap;
	private Block[][] blockMap;
	private int sizeTile;
	private int lenX = 0;
	private int lenY = 0;
	private int startPosX;
	private int startPosY;
	private int speed;
	private BufferedImage tiles;
	KeyHandler key;

	public Map(int width, int height, File file, int size, KeyHandler k, int speed){
		this.width = width;
		this.height = height;
		this.fileMap = file;
		this.sizeTile = size;
		this.key = k;
		this.speed = speed;
	}

	public boolean InitializeMap(){
		try {
			tiles = ImageIO.read(new File("asset/tiles.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		InitializeStringMap();
		blockMap = new Block[lenY][lenX];
		System.out.println("x : " + lenX + " y : " + lenY);
		for (int i = 0; i < lenY; i++){
			for (int j = 0; j < lenX; j++){
				if (stringMap[i][j].equals("1")) {
					blockMap[i][j] = new Block(width, height, sizeTile, j, i, true, tiles.getSubimage(sizeTile, 0, sizeTile, sizeTile), key, speed);
				} else if (stringMap[i][j].equals("0")) {
					blockMap[i][j] = new Block(width, height, sizeTile, j, i, false, tiles.getSubimage(0, 0, sizeTile, sizeTile), key, speed);
				} else if (stringMap[i][j].equals("P")){
					startPosX = j;
					startPosY = i;
					blockMap[i][j] = new Block(width, height, sizeTile, j, i, false, tiles.getSubimage(sizeTile * 2, 0, 48, 48), key, speed);
				} else if (stringMap[i][j].equals("N")) {
					blockMap[i][j] = new Block(width, height, sizeTile, j, i, true, null, key, speed);
				} else {
					return false;
				}
			}
		}
		InitializePos();
		return true;
	}

	public void InitializePos(){
		for (int i = 0; i < lenY; i++){
			for (Block b : blockMap[i]){
				b.InitializePos(startPosX, startPosY);
			}
		}
	}

	public boolean InitializeStringMap(){
		Scanner scan;
		String[] tabLine;

		System.out.println("Initialing map");
		try {
			scan = new Scanner(fileMap);
			while (scan.hasNextLine()){
				scan.nextLine();
				lenY++;
			}
			scan.close();
			scan = new Scanner(fileMap);
			tabLine = scan.nextLine().split(" ");
			lenX = tabLine.length;
			System.out.println(lenX);
			stringMap = new String[lenY][lenX];
			for (int i = 0; i < lenY; i++){
				stringMap[i] = tabLine;
				for (String s : stringMap[i]){
					s.replace("\n", "");
				}
				if (scan.hasNextLine())
					tabLine = scan.nextLine().split(" ");
			}
			scan.close();
		} catch (FileNotFoundException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void drawMap(Graphics2D g){
		for (int i = 0; i < lenY; i++){
			for (Block b : blockMap[i]){
				b.drawBlock(g);
			}
		}
	}

	public void update(){
		if (key.up){
			moveUp();
		}
		if (key.down){
			moveDown();
		}
		if (key.left){
			moveLeft();
		}
		if (key.right){
			moveRight();
		}
	}

	public void printMap(){
		System.out.println("Printing map");
		for (int i = 0; i < lenY; i++){
			for (int j = 0; j < lenX; j++){
				System.out.print(stringMap[i][j] + " ");
			}
			System.out.println();
		}
	}

	public void moveUp(){
		if (collisionUp()){
			for (int i = 0; i < lenX; i++){
				for (Block b : blockMap[i]){
					b.moveBlockY(speed);
				}
			}
		}
	}

	public void moveDown(){
		if (collisionDown()) {
			for (int i = 0; i < lenX; i++){
				for (Block b : blockMap[i]){
					b.moveBlockY(-speed);
				}
			}
		}
	}

	public void moveLeft(){
		if (collisionLeft()) {
			for (int i = 0; i < lenX; i++){
				for (Block b : blockMap[i]){
					b.moveBlockX(speed);
				}
			}
		}
	}

	public void moveRight(){
		if (collisionRight()) {
			for (int i = 0; i < lenX; i++){
				for (Block b : blockMap[i]){
					b.moveBlockX(-speed);
				}
			}
		}
	}

	public boolean collisionUp(){
		for (int i = 0; i < lenX; i++){
			for (Block b : blockMap[i]){
				if (b.getCollission() == true) {
					if (b.getPosY() + 48 < height / 2 - (sizeTile / 2) &&
						b.getPosY() + 48 + speed >= height / 2 - (sizeTile / 2) &&
						b.getPosX() > width / 2 - (sizeTile / 2) - 48 &&
						b.getPosX() < width / 2 + (sizeTile / 2)){
							return false;
					}
				}
			}
		}
		return true;
	}

	public boolean collisionDown(){
		for (int i = 0; i < lenX; i++){
			for (Block b : blockMap[i]){
				if (b.getCollission() == true) {
					if (b.getPosY() - 48 > height / 2 - (sizeTile / 2) &&
						b.getPosY() - 48 - speed <= height / 2 - (sizeTile / 2) &&
						b.getPosX() > width / 2 - (sizeTile / 2) - 48 &&
						b.getPosX() < width / 2 + (sizeTile / 2)){
							return false;
					}
				}
			}
		}
		return true;	}


	public boolean collisionLeft(){
		for (int i = 0; i < lenX; i++){
			for (Block b : blockMap[i]){
				if (b.getCollission() == true) {
					if (b.getPosY() > height / 2 - (sizeTile / 2) - 48 &&
						b.getPosY() < height / 2 + (sizeTile / 2) &&
						b.getPosX() + 48 < width / 2 - (sizeTile / 2) &&
						b.getPosX() + 48 + speed >= width / 2 - (sizeTile / 2)){
							return false;
					}
				}
			}
		}
		return true;
	}


	public boolean collisionRight(){
		for (int i = 0; i < lenX; i++){
			for (Block b : blockMap[i]){
				if (b.getCollission() == true) {
					if (b.getPosY() > height / 2 - (sizeTile / 2) - 48 &&
						b.getPosY() < height / 2 + (sizeTile / 2) &&
						b.getPosX() > width / 2 + (sizeTile / 2) &&
						b.getPosX() - speed <= width / 2 + (sizeTile / 2)){
							return false;
					}
				}
			}
		}
		return true;	
	}
}
