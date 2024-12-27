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
import java.nio.Buffer;
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
	public LinkedList<Block> listMachine;

	private BufferedImage floorImg;
	private BufferedImage[] wallImg;
	private BufferedImage shopImg;
	private BufferedImage startImg;
	private BufferedImage craftNImg;
	private BufferedImage craftPImg;
	private BufferedImage collectImg;

	private BufferedImage packImg;
	
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
	private int recipeIndex = 3;
	private int coinIndex = 4;

	private BufferedImage tiles;
	private int sizeTile = 24;

	public Map(Data data, File file, Inventory inventory, Player player, String[] texturePack){
		super();
		this.data = data;
		this.inventory = inventory;
		this.player = player;
		this.centerX = data.width / 2 - (data.size / 2);
		this.centerY = data.height / 2 - (data.size / 2);
		this.fileMap = file;

		try {
			this.packImg = ImageIO.read(new File("asset/pack/" + texturePack[0]));
		} catch (IOException e) {
			e.printStackTrace();
		}

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
		wallImg = new BufferedImage[4];
		for (int i = 0; i < 4;i++){
			wallImg[i] = tiles.getSubimage(sizeTile * i, 0, sizeTile, sizeTile);
		}
		floorImg = tiles.getSubimage(sizeTile * 4, 0, sizeTile, sizeTile);
		startImg = tiles.getSubimage(sizeTile * 4, sizeTile, sizeTile, sizeTile);
		collectImg = tiles.getSubimage(0, sizeTile, sizeTile, sizeTile);
		craftNImg = tiles.getSubimage(sizeTile, sizeTile, sizeTile, sizeTile);
		craftPImg = tiles.getSubimage(sizeTile * 2, sizeTile, sizeTile, sizeTile);
		shopImg = tiles.getSubimage(sizeTile * 3, sizeTile, sizeTile, sizeTile);

		System.out.println("initialing map");
		initializeStringMap();
		initializeItem();
		initialiseMachine();
		initializeTileMap();
		initializePos();
		return true;
	}

	public void initializePos(){
		int j;
		for (int i = 0; i < lenY; i++){
			j = 0;
			for (Tile t : tileMap[i]){
				Block b = (Block)t;
				b.initializePos(startPosX, startPosY);
				j++;
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
		tileMap = new Tile[lenY][lenX];
		System.out.println("x : " + lenX + " y : " + lenY);
		for (int i = 0; i < lenY; i++){
			for (int j = 0; j < lenX; j++){
				if (stringMap[i][j].equals("1")) {
					tileMap[i][j] = new Block(data, j, i, "1", true, getWallImage(j, i));
				} else if (stringMap[i][j].equals("0")) {
					tileMap[i][j] = new Block(data, j, i, "0", false, floorImg);
				} else if (stringMap[i][j].equals("P")){
					startPosX = j;
					startPosY = i;
					tileMap[i][j] = new Block(data, j, i, "P", false, startImg);
				} else if (stringMap[i][j].equals(".")) {
					tileMap[i][j] = new Block(data, j, i, ".", true, null);
				} else if (stringMap[i][j].equals("S")) {
					tileMap[i][j] = new Shop(data, player, j, i, true, shopImg, inventory, this, packImg);
					shop = (Shop)tileMap[i][j];
					createShop();
				} else {
					int index = 0;
					for (Object o : listObj[0]){
						Item item = (Item)o;
						if (stringMap[i][j].equals(item.getSymb())){
							tileMap[i][j] = new Ressource(item, inventory, player, data, j, i, item.getSymb(), true,
											packImg.getSubimage( sizeTile * index, 0, sizeTile, sizeTile));
							Ressource r = (Ressource)tileMap[i][j];
							r.initialiseRessource();
							break ;
						}
						index++;
					}
					for (Block machine : listMachine){
						if (stringMap[i][j].equals(machine.symb)){
							tileMap[i][j] = machine;
							System.out.println("Machine " + tileMap[i][j].symb);
							machine.x = j;
							machine.y = i;
							break ;
						}
					}
					if (tileMap[i][j] == null)
						return false;
				}
			}
		}
		return true;
	}

	public BufferedImage getWallImage(int x, int y){
		if (stringMap[y + 1][x].equals(".")){
			return wallImg[3];
		} else if (stringMap[y][x - 1].equals("1") && stringMap[y][x + 1].equals("1")){
			//if (stringMap[y - 1][x].equals("0")) {
				//return data.rotateImage(wallImg[1], 180);
			//}//
			return wallImg[1];
		} else if (stringMap[y - 1][x].equals("1") && stringMap[y + 1][x].equals("1")){
			if (stringMap[y][x + 1].equals("0") || (!stringMap[y][x + 1].equals(".") && !stringMap[y][x + 1].equals("1"))){
				return data.rotateImage(wallImg[1], -90);
			}
			return data.rotateImage(wallImg[1], 90);

		} else if (stringMap[y - 1][x].equals("1") && stringMap[y][x + 1].equals("1")){
			if (stringMap[y - 1][x + 1].equals("0") || (!stringMap[y - 1][x + 1].equals(".") && !stringMap[y - 1][x + 1].equals("1"))){
				return data.rotateImage(wallImg[2], 180);
			} else {
				return wallImg[0];
			}
		} else if (stringMap[y - 1][x].equals("1") && stringMap[y][x - 1].equals("1")){
			if (stringMap[y - 1][x - 1].equals("0") || (!stringMap[y - 1][x - 1].equals(".") && !stringMap[y - 1][x - 1].equals("1"))){
				return data.rotateImage(wallImg[2], 90);
			} else {
				return data.rotateImage(wallImg[0], -90);
			}
		} else if (stringMap[y + 1][x].equals("1") && stringMap[y][x + 1].equals("1")){
			if (stringMap[y + 1][x + 1].equals("0") || (!stringMap[y + 1][x + 1].equals(".") && !stringMap[y + 1][x + 1].equals("1"))){
				return data.rotateImage(wallImg[2], -90);
			} else {
				return data.rotateImage(wallImg[0], 90);
			}
		} else if (stringMap[y + 1][x].equals("1") && stringMap[y][x - 1].equals("1")){
			if (stringMap[y + 1][x - 1].equals("0") || (!stringMap[y + 1][x - 1].equals(".") && !stringMap[y + 1][x - 1].equals("1"))){
				return wallImg[2];
			} else {
				return data.rotateImage(wallImg[0], 180);
			}
		}
		return wallImg[1];
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
							packImg.getSubimage(sizeTile * i, sizeTile, sizeTile, sizeTile),
							(Integer.parseInt(tab[4]) == 1)));
				i++;
			}
			i = 0;
			for (String s : tabRecipe){
				tab = s.split("_");
				listObj[1].add(new Recipe(tab[0], tab[1], null, Integer.parseInt(tab[2]), Integer.parseInt(tab[3]),
							packImg.getSubimage(sizeTile * i, sizeTile * 2, sizeTile, sizeTile)));
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

	public boolean initialiseMachine(){
		Scanner scan;
		String line;
		String[] tabCollecter;
		String[] tabCrafter;
		String[] tab;
		int nbCollecter;
		int nbCrafter;
		int i = 0;

		listMachine = new LinkedList<Block>();
		try {
			scan = new Scanner(fileMap);
			line = scan.nextLine();
			while (scan.hasNextLine() && line.equals("ENDINV") == false){
				line = scan.nextLine();
			}
			line = scan.nextLine();
			tab = line.split(":");
			if (tab.length > 1) {
				tabCollecter = tab[1].split(";");
				for (String s : tabCollecter){
					tab = s.split("_");
					listMachine.add(new Collecter(getObject(tab[1]), data, this, inventory, 0, 0, true, collectImg,
								Integer.parseInt(tab[2]), tab[0]));
					i++;
				}
			}
			line = scan.nextLine();
			tab = line.split(":");
			if (tab.length > 1) {
				tabCrafter = tab[1].split(";");
				i = 0;
				for (String s : tabCrafter){
					tab = s.split("_");
					listMachine.add(new Crafter(data, (Recipe)getRecipe(tab[2]), this, inventory,
									((tab[1].equals("N")) ? Crafter.CrafterType.NORMAL : Crafter.CrafterType.POLYVALENT),
									0, 0, true, 
									((tab[1].equals("N")) ? craftNImg : craftPImg), tab[0]));
					System.out.println("Machine : " + listMachine.getLast().symb);
					i++;
				}
			}
			System.out.println();
			scan.close();
		} catch (FileNotFoundException e){
			return false;
		}
		return true;
	}

	public void updateMachine(){
		for (int y = 0; y < lenY; y++){
			for (int x = 0; x < lenX; x++){
				switch (tileMap[y][x].getType()) {
					case COLLECTER : Collecter collecter = (Collecter)tileMap[y][x];
						collecter.x = x; collecter.y = y; collecter.initialiseCollecter(); break;
					case CRAFTER : Crafter crafter = (Crafter)tileMap[y][x]; 
						crafter.x = x; crafter.y = y; crafter.initialiseCrafer(); break;
					default: break;
				}
			}
		}
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
				//System.out.println("failed to load map");
				return ;
			}
			if (tileMap[i] == null){
				//System.out.println("failed to load : block" + i);
			}
			for (Tile t : tileMap[i]){
				Block b = (Block)t;
				if (b == null){
					//System.out.println("failed to load in block[i] : " + i);
				} else {
					switch (b.getType()) {
						case CRAFTER : case COLLECTER : case RESSOURCE : case SHOP :
							g.drawImage(floorImg, b.posX, b.posY, data.size, data.size, null);
							break;
						default:
							break;
					}
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

	public int[] getIndexPos(int posX, int posY){
		int[] pos = new int[2];

		int centerX = data.width / 2 - (data.size / 2);
		int centerY = data.height / 2 - (data.size / 2);

		posX = ((posX - updateX) - centerX);
		posY = ((posY - updateY) - centerY);

		int modX = (Math.floorMod(posX, data.size));
		int modY = (Math.floorMod(posY, data.size));
		posX -= modX;
		posY -= modY;

		pos[0] = (posX / data.size) + startPosX;
		pos[1] = (posY / data.size) + startPosY;
		if (pos[0] < 0 || pos[0] >= lenX || pos[1] < 0 || pos[1] >= lenY){
			return null;
		}
		return pos;
	}

	public Tile getTile(int x, int y){
		return tileMap[y][x];
	}

	public Rectangle getPosMouse(){
		int[] pos = getIndexPos(data.mouse.x, data.mouse.y);

		if (pos == null){
			return null;
		}
		return new Rectangle(tileMap[pos[1]][pos[0]].posX, tileMap[pos[1]][pos[0]].posY, data.size, data.size);
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
		} else if (data.key.editMode && data.key.type == 1) {
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
		} else if (data.key.editMode && data.key.type == 3) {
			switch (tileMap[y][x].getType()) {
				case CRAFTER : case COLLECTER : return ((((Block)tileMap[y][x]).hasBonus) ? Color.RED : Color.GREEN);
				default: return Color.RED;
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
			inventory.removeInvButton();
			data.clearMenuPanel();
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

	public void printTileMap(){
		System.out.println("Printing map");
		for (int i = 0; i < lenY; i++){
			for (int j = 0; j < lenX; j++){
				System.out.print(tileMap[i][j].symb + " ");
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

	public boolean checkCollision(){/* 
		int[] topL = getIndexPos(player.posX, player.posY);
		int[] topR = getIndexPos(player.posX + data.size, player.posY);
		int[] botL = getIndexPos(player.posX, player.posY + data.size);
		int[] botR = getIndexPos(player.posX + data.size, player.posY + data.size);

		return (!(tileMap[topL[1]][topL[0]].collision || tileMap[topR[1]][topR[0]].collision
			||  tileMap[botL[1]][botL[0]].collision || tileMap[botR[1]][botR[0]].collision));*/

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

	public int nextToRessource(int x, int y){
		if (y > 0 && tileMap[y - 1][x].getType() == Tile.TileType.RESSOURCE){
			return 1;
		} else if (y < lenY && tileMap[y + 1][x].getType() == Tile.TileType.RESSOURCE) {
			return 2;
		} else if (x > 0 && tileMap[y][x - 1].getType() == Tile.TileType.RESSOURCE){
			return 3;
		} else if (x < lenX && tileMap[y][x + 1].getType() == Tile.TileType.RESSOURCE){
			return 4;
		}
		return 0;
	}

	public LinkedList<Slot> getNextObject(int x, int y){
		LinkedList<Slot> objects = new LinkedList<>();

		if (y > 0 && tileMap[y - 1][x].getType() == Tile.TileType.RESSOURCE){
			objects.add(new Slot(((Ressource)tileMap[y - 1][x]).object, 1));
		} 
		if (y < lenY && tileMap[y + 1][x].getType() == Tile.TileType.RESSOURCE) {
			objects.add(new Slot(((Ressource)tileMap[y + 1][x]).object, 2));
		}
		if (x > 0 && tileMap[y][x - 1].getType() == Tile.TileType.RESSOURCE){
			objects.add(new Slot(((Ressource)tileMap[y][x - 1]).object, 3));
		} 
		if (x < lenX && tileMap[y][x + 1].getType() == Tile.TileType.RESSOURCE){
			objects.add(new Slot(((Ressource)tileMap[y][x + 1]).object, 4));
		}
		return objects;
	}

	public boolean cantPut(int x, int y){
		return (tileMap[y][x].collision || tileMap[y][x].getType() == Tile.TileType.EXIT
			|| tileMap[y][x].touchPlayer());
	}

	public void setTile(Tile tile, int x, int y){
		tile.posX = tileMap[y][x].posX;
		tile.posY = tileMap[y][x].posY;
		tileMap[y][x] = tile;
	}

	public void addNewMachine(Block machine){
		listMachine.add(machine);
	}

	public Object getObject(String symb){
		for (Object obj : listObj[0]) {
			if (obj.symb.equals(symb)){
				return obj;
			}
		}
		return null;
	}

	public Object getRecipe(String name){
		for (Object obj : listObj[1]) {
			if (obj.name.equals(name)){
				return obj;
			}
		}
		return null;
	}

	public LinkedList<Object> getRecipes(){
		return listObj[1];
	}

	public String createNewSymb(){
		return null;
	}
}