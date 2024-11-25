import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class Map {
	
	private Data data;
	private Game game;
	private File fileMap;
	private String[][] stringMap;
	private Block[][] blockMap;
	private Object[][] listObject;
	private int lenX = 0;
	private int lenY = 0;
	private int startPosX;
	private int startPosY;
	private int centerX;
	private int centerY;
	private int updateX = 0;
	private int updateY = 0;
	private BufferedImage tiles;

	public Map(Data data, File file){
		this.data = data;
		this.centerX = data.width / 2 - (data.size / 2);
		this.centerY = data.height / 2 - (data.size / 2);
		this.fileMap = file;
	}

	public boolean InitializeMap(){
		try {
			tiles = ImageIO.read(new File("asset/tiles.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		InitializeStringMap();
		InitializeObject();
		blockMap = new Block[lenY][lenX];
		System.out.println("x : " + lenX + " y : " + lenY);
		for (int i = 0; i < lenY; i++){
			for (int j = 0; j < lenX; j++){
				if (stringMap[i][j].equals("1")) {
					blockMap[i][j] = new Block(data, j, i, true, tiles.getSubimage(data.size, 0, data.size, data.size));
				} else if (stringMap[i][j].equals("0")) {
					blockMap[i][j] = new Block(data, j, i, false, tiles.getSubimage(0, 0, data.size, data.size));
				} else if (stringMap[i][j].equals("P")){
					startPosX = j;
					startPosY = i;
					blockMap[i][j] = new Block(data, j, i, false, tiles.getSubimage(data.size * 2, 0, data.size, data.size));
				} else if (stringMap[i][j].equals("N")) {
					blockMap[i][j] = new Block(data, j, i, true, null);
				} else {
					int index = 0;
					for (Object o : listObject[0]){
						if (stringMap[i][j].equals(o.getSymb())){
							blockMap[i][j] = new Ressource(o, data, j, i, true, tiles.getSubimage(data.size * index, data.size, data.size, data.size));
							break ;
						}
						index++;
					}
					if (blockMap[i][j] == null)
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
		String line = "";
		String[] tabLine;

		System.out.println("Initialing map");
		try {
			scan = new Scanner(fileMap);
			line = scan.nextLine();
			while (scan.hasNextLine() && line.equals("ENDMAP") == false){
				line = scan.nextLine();
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

	public boolean InitializeObject(){
		Scanner scan;
		String line = "";
		String[] tabObject;
		String[] tabRecipe;
		String[] tab;
		int i = 0;

		System.out.println("Initialing map");
		try {
			scan = new Scanner(fileMap);
			while (scan.hasNextLine() && line.equals("ENDMAP") == false){
				line = scan.nextLine();
			}
			line = scan.nextLine();
			tabObject = (line.split(":"))[1].split("_");
			line = scan.nextLine();
			tabRecipe = (line.split(":"))[1].split("_");
			listObject = new Object[2][];
			listObject[0] = new Object[tabObject.length];
			listObject[1] = new Object[tabRecipe.length];
			for (String s : tabObject){
				tab = s.split(";");
				listObject[0][i] = new Object(tab[0], tab[1], Integer.valueOf(tab[2]), null);
				i++;
			}
			i = 0;
			for (String s : tabRecipe){
				tab = s.split(";");
				listObject[1][i] = new Recipe(tab[0], tab[1], null, Integer.valueOf(tab[2]), null);
				i++;
			}
			System.out.println();
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
		if (data.key.up){
			moveUp();
			if (checkCollision() == false)
				moveDown();
		}
		if (data.key.down){
			moveDown();
			if (checkCollision() == false)
				moveUp();
		}
		if (data.key.left){
			moveLeft();
			if (checkCollision() == false)
				moveRight();
		}
		if (data.key.right){
			moveRight();
			if (checkCollision() == false)
				moveLeft();
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
		for (int i = 0; i < lenY; i++){
			for (Block b : blockMap[i]){
				b.moveBlockY(data.speed);
			}
		}
		updateY += data.speed;
	}

	public void moveDown(){
		for (int i = 0; i < lenY; i++){
			for (Block b : blockMap[i]){
				b.moveBlockY(-data.speed);
			}
		}
		updateY -= data.speed;
	}

	public void moveLeft(){
		for (int i = 0; i < lenY; i++){
			for (Block b : blockMap[i]){
				b.moveBlockX(data.speed);
			}
		}
		updateX += data.speed;
	}

	public void moveRight(){
		for (int i = 0; i < lenY; i++){
			for (Block b : blockMap[i]){
				b.moveBlockX(-data.speed);
			}
		}
		updateX -= data.speed;
	}

	public boolean checkCollision(){
		for (int i = 0; i < lenY; i++){
			for (Block b : blockMap[i]){
				if (b.getCollission() == true) {
					if (b.getPosY() > data.height / 2 - (data.size / 2) - 48 &&
						b.getPosY() < data.height / 2 + (data.size / 2) &&
						b.getPosX() > data.width / 2 - (data.size / 2) - 48 &&
						b.getPosX() < data.width / 2 + (data.size / 2)){
						return false;
					}
				}
			}
		}		
		return true;
	}

	/* 
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
		return true;	
	}


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
		*/
}