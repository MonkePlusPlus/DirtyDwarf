import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class Map {
	
	private Data data;
	private Game game;
	private Inventory inventory;
	private Shop shop;

	private File fileMap;
	private String[][] stringMap;
	private Tile[][] tileMap;
	public LinkedList<Object>[] listObj;

	private BufferedImage floorImg;
	private BufferedImage wallImg;
	private BufferedImage shopImg;
	private BufferedImage startImg;
	
	private int lenX = 0;
	private int lenY = 0;
	private int startPosX;
	private int startPosY;
	private int centerX;
	private int centerY;
	private int updateX = 0;
	private int updateY = 0;
	private int blockIndex = 0;
	private int ressourceIndex = 1;
	private int itemIndex = 2;
	private int recipeIndex = 3;
	private int coinIndex = 4;

	private BufferedImage tiles;
	private int sizeTile = 48;

	public Map(Data data, File file, Inventory inventory){
		this.data = data;
		this.inventory = inventory;
		this.centerX = data.width / 2 - (data.size / 2);
		this.centerY = data.height / 2 - (data.size / 2);
		this.fileMap = file;
	}

	public boolean initializeMap(){
		try {
			tiles = ImageIO.read(new File("asset/tiles.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		wallImg = tiles.getSubimage(sizeTile, sizeTile * blockIndex, sizeTile, sizeTile);
		floorImg = tiles.getSubimage(0, sizeTile * blockIndex, sizeTile, sizeTile);
		startImg = tiles.getSubimage(sizeTile * 2, 0, sizeTile, sizeTile);
		shopImg = tiles.getSubimage(sizeTile * 5, 0, sizeTile, sizeTile);
		System.out.println("initialing map");
		initializeStringMap();
		initializeItem();
		initializeTileMap();
		initializePos();

		return true;
	}

	public void initializePos(){
		for (int i = 0; i < lenY; i++){
			for (Tile t : tileMap[i]){
				Block b = (Block)t;
				b.initializePos(startPosX, startPosY);
			}
		}
	}

	public BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
		Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
		BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
		outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
		return outputImage;
	}
	
	public void createShop(){
		inventory.getShop(shop);
		shop.initialiseObjShop(listObj);
		shop.initialiseShop();
		shop.initialiseShopButton();
	}

	public boolean initializeTileMap(){
		tileMap = new Block[lenY][lenX];
		System.out.println("x : " + lenX + " y : " + lenY);
		for (int i = 0; i < lenY; i++){
			for (int j = 0; j < lenX; j++){
				if (stringMap[i][j].equals("1")) {
					tileMap[i][j] = new Block(data, j, i, true, wallImg);
				} else if (stringMap[i][j].equals("0")) {
					tileMap[i][j] = new Block(data, j, i, false, floorImg);
				} else if (stringMap[i][j].equals("P")){
					startPosX = j;
					startPosY = i;
					tileMap[i][j] = new Block(data, j, i, false, startImg);
				} else if (stringMap[i][j].equals("N")) {
					tileMap[i][j] = new Block(data, j, i, true, null);
				} else if (stringMap[i][j].equals("M")) {
					tileMap[i][j] = new Shop(data, j, i, true, shopImg, inventory);
					shop = (Shop)tileMap[i][j];
					createShop();
				} else {
					int index = 0;
					for (Object o : listObj[0]){
						Item item = (Item)o;
						if (stringMap[i][j].equals(item.getSymb())){
							tileMap[i][j] = new Ressource(item, data, j, i, true, 
											tiles.getSubimage(sizeTile * index, sizeTile * ressourceIndex, sizeTile, sizeTile));
							break ;
						}
						index++;
					}
					if (tileMap[i][j] == null)
						return false;
				}
			}
		}
		return true;
	}

	public boolean initializeStringMap(){
		Scanner scan;
		String line;
		String[] tabLine;

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

	public boolean initializeItem(){
		Scanner scan;
		String line;
		String[] tabItem;
		String[] tabRecipe;
		String[] tab;
		int i = 0;

		listObj = new LinkedList[3];
		listObj[0] = new LinkedList<Object>();
		listObj[1] = new LinkedList<Object>();
		listObj[2] = new LinkedList<Object>();
		try {
			scan = new Scanner(fileMap);
			line = scan.nextLine();
			while (scan.hasNextLine() && line.equals("ENDMAP") == false){
				line = scan.nextLine();
			}
			line = scan.nextLine();
			tabItem = (line.split(":"))[1].split(";");
			line = scan.nextLine();
			tabRecipe = (line.split(":"))[1].split(";");

			for (String s : tabItem){
				tab = s.split("_");
				listObj[0].add(new Item(tab[0], tab[1], Integer.parseInt(tab[2]), Integer.parseInt(tab[3]),
								tiles.getSubimage(sizeTile * i, sizeTile * itemIndex, sizeTile, sizeTile)));
				i++;
			}
			i = 0;
			for (String s : tabRecipe){
				tab = s.split("_");
				listObj[1].add(new Recipe(tab[0], tab[1], null, Integer.parseInt(tab[2]), Integer.parseInt(tab[3]),
								tiles.getSubimage(sizeTile * i, sizeTile * recipeIndex, sizeTile, sizeTile)));
				i++;
			}
			System.out.println();
			scan.close();
		} catch (FileNotFoundException e){
			return false;
		}
		for (Object o : listObj[1]){
			((Recipe)o).createIngredient(listObj);
		}
		return true;
	}

	public void drawMap(Graphics2D g){
		for (int i = 0; i < lenY; i++){
			if (tileMap == null){
				System.out.println("failed to load map");
				return ;
			}
			if (tileMap[i] == null){
				System.out.println("failed to load : block" + i);
			}
			for (Tile t : tileMap[i]){
				Block b = (Block)t;
				if (b == null){
					System.out.println("failed to load in block[i] : " + i);
				} else {
					b.drawBlock(g);
					b.mouseClick();
				}
			}
		}
		Rectangle r = getPosMouse();
		if (!windowOpen()){
			data.mouse.drawSelect(g, r, getColorMouse(r));
		}
	}

	public boolean windowOpen(){
		return (inventory.isOpen() || shop.isOpen());
	}

	public Rectangle getPosMouse(){
		for (int i = 0; i < lenY; i++){
			for (Tile t : tileMap[i]) {
				Block b = (Block)t;
				if (data.mouse.x <= b.getPosX() + data.size && 
					data.mouse.x >= b.getPosX() &&
					data.mouse.y <= b.getPosY() + data.size &&
					data.mouse.y >= b.getPosY()
				){
					return (new Rectangle(b.getPosX(), b.getPosY(), data.size, data.size));
				}
			}
		}
		return null;
	}

	public Color getColorMouse(Rectangle r){
		if (r == null){
			return Color.BLACK;
		}
		int centerX = data.width / 2 - (data.size / 2);
		int centerY = data.height / 2 - (data.size / 2);

		int x = ((((int)r.getX() - updateX) - centerX) / data.size) + startPosX;
		int y = ((((int)r.getY() - updateY) - centerY) / data.size) + startPosY;

		double distance = Math.sqrt(Math.pow(Math.abs(centerX - (r.getX() + data.size / 2)), 2)
						+ Math.pow(Math.abs(centerY - (r.getY() + data.size / 2)), 2));

		switch (tileMap[y][x].getType()){
			case WALL : case FLOOR : return Color.WHITE;
			default: return ((distance <= data.size * 3) ? Color.GREEN : Color.RED);
		}
	}

	public void update(){
		if (data.key.pause){
			shop.removeShopButton();
			shop.removeShop();
		}
		else {
			shop.showShopButton();
		}
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
		if (data.key.moneyP){
			inventory.addMoney(100);
		}
		if (data.key.moneyM){
			inventory.removeMoney(100);
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
			for (Tile t : tileMap[i]){
				Block b = (Block)t;
				b.moveBlockY(data.speed);
			}
		}
		updateY += data.speed;
	}

	public void moveDown(){
		for (int i = 0; i < lenY; i++){
			for (Tile t : tileMap[i]){
				Block b = (Block)t;
				b.moveBlockY(-data.speed);
			}
		}
		updateY -= data.speed;
	}

	public void moveLeft(){
		for (int i = 0; i < lenY; i++){
			for (Tile t : tileMap[i]){
				Block b = (Block)t;
				b.moveBlockX(data.speed);
			}
		}
		updateX += data.speed;
	}

	public void moveRight(){
		for (int i = 0; i < lenY; i++){
			for (Tile t : tileMap[i]){
				Block b = (Block)t;
				b.moveBlockX(-data.speed);
			}
		}
		updateX -= data.speed;
	}

	public boolean checkCollision(){
		for (int i = 0; i < lenY; i++){
			for (Tile t : tileMap[i]){
				Block b = (Block)t;
				if (b.getCollission() == true) {
					if (b.getPosY() > data.height / 2 - (data.size / 2) - data.size &&
						b.getPosY() < data.height / 2 + (data.size / 2) &&
						b.getPosX() > data.width / 2 - (data.size / 2) - data.size &&
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
			for (Block b : tileMap[i]){
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
			for (Block b : tileMap[i]){
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
			for (Block b : tileMap[i]){
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
			for (Block b : tileMap[i]){
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