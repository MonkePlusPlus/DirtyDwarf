import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Map {
	
	private Data data;
	private Game game;
	private File fileMap;
	private String[][] stringMap;
	private Block[][] blockMap;
	private Item[][] listItem;

	private Inventory inventory;
	private JButton invButton;
	private boolean invOpen = false;
	
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
		System.out.println("Initialing map");
		InitializeStringMap();
		InitializeItem();
		InitializeBlockMap();
		InitializePos();
		InitializeButton();
		return true;
	}

	public void InitializePos(){
		for (int i = 0; i < lenY; i++){
			for (Block b : blockMap[i]){
				b.InitializePos(startPosX, startPosY);
			}
		}
	}

	public boolean InitializeBlockMap(){
		blockMap = new Block[lenY][lenX];
		System.out.println("x : " + lenX + " y : " + lenY);
		for (int i = 0; i < lenY; i++){
			for (int j = 0; j < lenX; j++){
				if (stringMap[i][j].equals("1")) {
					blockMap[i][j] = new Block(data, j, i, true, tiles.getSubimage(data.size, data.size * blockIndex, data.size, data.size));
				} else if (stringMap[i][j].equals("0")) {
					blockMap[i][j] = new Block(data, j, i, false, tiles.getSubimage(0, data.size * blockIndex, data.size, data.size));
				} else if (stringMap[i][j].equals("P")){
					startPosX = j;
					startPosY = i;
					blockMap[i][j] = new Block(data, j, i, false, tiles.getSubimage(data.size * 2, 0, data.size, data.size));
				} else if (stringMap[i][j].equals("N")) {
					blockMap[i][j] = new Block(data, j, i, true, null);
				} else {
					int index = 0;
					for (Item o : listItem[0]){
						if (stringMap[i][j].equals(o.getSymb())){
							blockMap[i][j] = new Ressource(o, data, j, i, true, 
											tiles.getSubimage(data.size * index, data.size * ressourceIndex, data.size, data.size));
							break ;
						}
						index++;
					}
					if (blockMap[i][j] == null)
						return false;
				}
			}
		}
		return true;
	}

	public boolean InitializeStringMap(){
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

	public boolean InitializeItem(){
		Scanner scan;
		String line = "";
		String[] tabItem;
		String[] tabRecipe;
		String[] tab;
		int i = 0;

		try {
			scan = new Scanner(fileMap);
			while (scan.hasNextLine() && line.equals("ENDMAP") == false){
				line = scan.nextLine();
			}
			line = scan.nextLine();
			tabItem = (line.split(":"))[1].split("_");
			line = scan.nextLine();
			tabRecipe = (line.split(":"))[1].split("_");
			listItem = new Item[2][];
			listItem[0] = new Item[tabItem.length];
			listItem[1] = new Item[tabRecipe.length];
			for (String s : tabItem){
				tab = s.split(";");
				listItem[0][i] = new Item(tab[0], tab[1], Integer.valueOf(tab[2]),
								tiles.getSubimage(data.size * i, data.size * itemIndex, data.size, data.size));
				i++;
			}
			i = 0;
			for (String s : tabRecipe){
				tab = s.split(";");
				listItem[1][i] = new Recipe(tab[0], tab[1], null, Integer.valueOf(tab[2]),
								tiles.getSubimage(data.size * i, data.size * recipeIndex, data.size, data.size));
				i++;
			}
			System.out.println();
			scan.close();
		} catch (FileNotFoundException e){
			e.printStackTrace();
			return false;
		}
		this.inventory = new Inventory(data, listItem);
		inventory.InitializeInventory();
		return true;
	}

	public void InitializeButton(){
		invButton = new JButton("Inventory");
		invButton.setBounds(data.width / 100, data.height / 100 , data.width / 10, data.height / 15);
		invButton.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent cEvt){
				if (invOpen && invButton.getModel().isPressed()){
					inventory.showInventory(false);
					invOpen = false;
				} else if (invOpen == false && invButton.getModel().isPressed()){
					inventory.showInventory(true);
					invOpen = true;
				}
			}
		});
		invButton.setBackground(Color.WHITE);
		invButton.setFocusPainted(false);
		data.panel.add(invButton);
		invButton.setVisible(true);
	}

	public void drawMap(Graphics2D g){
		for (int i = 0; i < lenY; i++){
			if (blockMap == null){
				System.out.println("failed to load map");
				return ;
			}
			if (blockMap[i] == null){
				System.out.println("failed to load : block" + i);
			}
			for (Block b : blockMap[i]){
				if (b == null){
					System.out.println("failed to load in block[i] : " + i);
				} else {
					b.drawBlock(g);
				}
			}
		}
	}

	public void update(){
		if (data.key.up){
			System.out.println("MOVEEE UP");
			moveUp();
			if (checkCollision() == false)
				moveDown();
		}
		if (data.key.down){
			System.out.println("MOVEEE DOWN");
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