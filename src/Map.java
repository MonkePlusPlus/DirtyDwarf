import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Map extends JPanel {
	
	private Data data;
	private Game game;
	private Player player;
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

	public Map(Data data, File file, Inventory inventory, Player player){
		super();
		this.data = data;
		this.inventory = inventory;
		this.player = player;
		this.centerX = data.width / 2 - (data.size / 2);
		this.centerY = data.height / 2 - (data.size / 2);
		this.fileMap = file;

		this.setBounds(0, 0, data.width, data.height);
		this.setBackground(Color.BLACK);
		this.setDoubleBuffered(true);
		this.addKeyListener(data.key);
		this.setLayout(null);
		this.setFocusable(true);

		data.panel.add(this);
		data.panel.setLayer(this, 0);

		data.panel.add(data.menuPanel);
		data.panel.setLayer(data.menuPanel, 2);
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
					tileMap[i][j] = new Shop(data, j, i, true, shopImg, inventory, this);
					shop = (Shop)tileMap[i][j];
					createShop();
				} else {
					int index = 0;
					for (Object o : listObj[0]){
						Item item = (Item)o;
						if (stringMap[i][j].equals(item.getSymb())){
							tileMap[i][j] = new Ressource(item, inventory, data, j, i, true,
											tiles.getSubimage(sizeTile * index, sizeTile * ressourceIndex, sizeTile, sizeTile));
							Ressource r = (Ressource)tileMap[i][j];
							r.initialiseRessource();
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
								tiles.getSubimage(sizeTile * i, sizeTile * itemIndex, sizeTile, sizeTile),
								(Integer.parseInt(tab[4]) == 1)));
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

	@Override
	public void paintComponent(Graphics g){
		Toolkit.getDefaultToolkit().sync();
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D)g;
		drawMap(g2);
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
					if (data.key.editMode == false){
						b.mouseClick();
					}
				}
			}
		}
		Rectangle r = getPosMouse();
		if (!windowOpen() && data.windowOpen == false && data.key.pause == false){
			data.mouse.drawSelect(g, r, getColorMouse(r));
		}
		player.draw(g);
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
	
		if (data.key.editMode && data.key.type == 0){
			if (tileMap[y][x].collision || ((Block)tileMap[y][x]).touchPlayer()){
				return Color.RED;
			} else {
				return Color.GREEN;
			}
		}
		if (data.key.editMode && data.key.type == 1) {
			if (tileMap[y][x].collision || ((Block)tileMap[y][x]).touchPlayer()){
				return Color.RED;
			} else if (y > 0 && tileMap[y - 1][x].getType() == Tile.TileType.RESSOURCE){
				return Color.GREEN;
			} else if (y < lenY && tileMap[y + 1][x].getType() == Tile.TileType.RESSOURCE){
				return Color.GREEN;
			} else if (x > 0 && tileMap[y][x - 1].getType() == Tile.TileType.RESSOURCE){
				return Color.GREEN;
			} else if (x < lenX && tileMap[y][x + 1].getType() == Tile.TileType.RESSOURCE) {
				return Color.GREEN;
			} else {
				return Color.WHITE;
			}
		}
        return switch (tileMap[y][x].getType()) {
                case WALL, FLOOR -> Color.WHITE;
                default -> (distance <= data.size * 3) ? Color.GREEN : Color.RED;
        };
	}

	public void update(){
		if (data.key.pause || data.key.editMode){
			shop.removeShopButton();
			shop.removeShop();
			inventory.removeInventory();
			inventory.removeInvButton();
		}
		else {
			inventory.showInvButton();
			shop.showShopButton();
		}
		if (data.key.up && data.key.move){
			moveUp();
			if (checkCollision() == false)
				moveDown();
		}
		if (data.key.down && data.key.move){
			moveDown();
			if (checkCollision() == false)
				moveUp();
		}
		if (data.key.left && data.key.move){
			moveLeft();
			if (checkCollision() == false)
				moveRight();
		}
		if (data.key.right && data.key.move){
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
					if (b.touchPlayer()){
						return false;
					}
				}
			}
		}		
		return true;
	}

	public int nextToRessource(){
		for (int y = 0; y < lenY; y++){
			for (int x = 0; x < lenX; x++){
				if (tileMap[y][x].isTouched()){
					if (tileMap[y][x].collision || tileMap[y][x].getType() == Tile.TileType.EXIT
						|| ((Block)tileMap[y][x]).touchPlayer()){
						return 0;
					}
					if (y > 0 && tileMap[y - 1][x].getType() == Tile.TileType.RESSOURCE){
						return 1;
					} else if (y < lenY && tileMap[y + 1][x].getType() == Tile.TileType.RESSOURCE) {
						return 2;
					} else if (x > 0 && tileMap[y][x - 1].getType() == Tile.TileType.RESSOURCE){
						return 3;
					} else if (x < lenX && tileMap[y][x + 1].getType() == Tile.TileType.RESSOURCE){
						return 4;
					}
				}
			}
		}
		return 0;
	}

	public boolean cantPut(){
		for (int y = 0; y < lenY; y++){
			for (int x = 0; x < lenX; x++){
				if (tileMap[y][x].isTouched()){
					if (tileMap[y][x].collision || tileMap[y][x].getType() == Tile.TileType.EXIT
						|| ((Block)tileMap[y][x]).touchPlayer()){
						return true;
					}
				}
			}
		}
		return false;
	}

	public int[] getIndexTouched(){
		int[] index = new int[2];
		
		for (int y = 0; y < lenY; y++){
			for (int x = 0; x < lenX; x++){
				if (tileMap[y][x].isTouched()){
					index[0] = y;
					index[1] = x;
					return index;
				}
			}
		}
		return null;
	}

	public void setTile(Tile tile, int x, int y){
		tile.posX = tileMap[y][x].posX;
		tile.posY = tileMap[y][x].posY;
		tileMap[y][x] = tile;
	}
}