import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

public class RandomMapGenerator {

	private String[][] map;
	private Random random = new Random();
	private LinkedList<Rectangle> rooms;
	private LinkedList<int[]> pairs;
	private int maxX = 100;
	private int maxY = 100;
	private int pathWidth;

	final private int TOP = 1;
	final private int BOTTOM = 2;
	final private int LEFT = 3;
	final private int RIGHT = 4;

	private void startMap(){
		map = new String[maxY][maxX];
		for (int i = 0; i < maxY; i++) {
			for (int j = 0; j < maxX; j++){
				map[i][j] = ".";
			}
		}
	}

	public void createRandomMap(File file, String[] pack) {
		int nbRoom = random.nextInt(4, 6);
		Rectangle rectangle;
		int index = 0;
		int width;
		int height;

		startMap();
		rooms = new LinkedList<Rectangle>();
		pairs = new LinkedList<int[]>();
		while (index < nbRoom){
			width = random.nextInt(10, 30);
			height = random.nextInt(10, 30);
			rectangle = new Rectangle(random.nextInt(5, maxX - 5 - width),
									  random.nextInt(5, maxY - 5 - height),
									  width, 
									  height);
			if (checkPos(rectangle) && checkOthers(rectangle)) {
				rooms.add(rectangle);
				fillRectangle(rectangle);
				index++;
			}
		}
		int index1;
		//fillFile(file);
		for (Rectangle rect : rooms){
			index1 = rooms.indexOf(rect);
			while ((index = random.nextInt(0, nbRoom)) == index1 || !checkPairs(index1, index)){
			}
			System.out.println("Connect : " + index1 + " + " + index);
			connectRooms(rect, rooms.get(index));
			int[] pair = new int[2];
			pair[0] = index1;
			pair[1] = index;	
			pairs.add(pair);
		}
		fillWall();
		fillObj();
		fillFile(file, pack[0], pack[1]);
	}

	public void connectRooms(Rectangle rect1, Rectangle rect2) {
		int placementDoor1;
		int placementDoor2;

		int xAdding;
		int yAdding;

		int x = (int)rect1.getCenterX();
		int y = (int)rect1.getCenterY();
	
		int notConnect = 0;


		int pos1X = (rect1.getX() > rect2.getX()) ? LEFT : RIGHT;
		int pos2X = (pos1X == LEFT) ? RIGHT : LEFT;
		int pos1Y = (rect1.getY() > rect2.getY()) ? TOP : BOTTOM;
		int pos2Y = (pos1Y == TOP) ? BOTTOM : TOP;
		placementDoor1 = (random.nextInt(0, 2) == 0) ? pos1X : pos1Y;
		placementDoor2 = (placementDoor1 == TOP || placementDoor1 == BOTTOM) ? pos2X : pos2Y;

		yAdding = (placementDoor1 == TOP || placementDoor2 == BOTTOM) ? -1 : 1;
		xAdding = (placementDoor1 == LEFT || placementDoor2 == RIGHT) ? -1 : 1;

		int i = 0;

		int direction = (placementDoor1 == TOP || placementDoor1 == BOTTOM) ? 0 : 1;
		System.out.println("placementDoor1 : " + placementDoor1 + " placementDoor2 : " + placementDoor2);
		while (notConnect < 2){
			if (rect2.contains(new Point(x, y))){
				notConnect++;
			}
			if ((placementDoor1 == TOP && placementDoor2 == LEFT && y >= rect2.getMinY() + 5 && y <= rect2.getCenterY())
			 || (placementDoor1 == TOP && placementDoor2 == RIGHT && y <= rect2.getMaxY() - 5 && y >= rect2.getCenterY())
			 || (placementDoor1 == BOTTOM && placementDoor2 == LEFT && y >= rect2.getMinY() + 5 && y <= rect2.getCenterY())
			 || (placementDoor1 == BOTTOM && placementDoor2 == RIGHT && y <= rect2.getMaxY() - 5 && y >= rect2.getCenterY())){
				direction = 1;
			} 
			if ((placementDoor1 == LEFT && placementDoor2 == TOP && x >= rect2.getMinX() + 5 && x <= rect2.getCenterX()) 
			 || (placementDoor1 == LEFT && placementDoor2 == BOTTOM && x <= rect2.getMaxX() - 5 && x >= rect2.getCenterX())
			 || (placementDoor1 == RIGHT && placementDoor2 == TOP && x >= rect2.getMinX() + 5 && x <= rect2.getCenterX())
			 || (placementDoor1 == RIGHT && placementDoor2 == BOTTOM && x <= rect2.getMaxX() - 5 && x >= rect2.getCenterX())){
				direction = 0;
			}
			if (direction == 1){
				if (map[y - 2][x].equals("0") == false){
					map[y - 2][x] = "1";
				}
				map[y - 1][x] = "0";
				map[y][x] = "0";
				map[y + 1][x] = "0";
				if (map[y + 2][x].equals("0") == false){
					map[y + 2][x] = "1";
				}
				x += xAdding;
			} else if (direction == 0){
				if (!map[y][x - 2].equals("0")){
					map[y][x - 2] = "1";
				}
				map[y][x - 1] = "0";
				map[y][x] = "0";
				map[y][x + 1] = "0";
				if (!map[y][x + 2].equals("0")){
					map[y][x + 2] = "1";
				}
				y += yAdding;
			}
			i++;
		}
		
	}

	private void fillObj(){
		Rectangle r = rooms.getFirst();

		map[(int)r.getCenterY()][(int)r.getCenterX()] = "P";

		int pos = random.nextInt(1, 5);
		Rectangle rectShop = rooms.get(random.nextInt(0, rooms.size()));

		int startX = 0;
		int endX = 0;
		int startY = 0;
		int endY = 0;

		switch (pos) {
			case TOP: startX = (int)rectShop.getMinX() + 1; endX = (int)rectShop.getMaxX() - 1;
					  startY = (int)rectShop.getMinY() + 1; endY = (int)rectShop.getMinY() + 1; break;

			case BOTTOM: startX = (int)rectShop.getMinX() + 1; endX = (int)rectShop.getMaxX() - 1;
						 startY = (int)rectShop.getMaxY() - 1; endY = (int)rectShop.getMaxY() - 1; break;

			case LEFT: startX = (int)rectShop.getMinX() + 1; endX = (int)rectShop.getMinX() + 1;
					   startY = (int)rectShop.getMinY() + 1; endY = (int)rectShop.getMaxY() - 1; break;

			case RIGHT: startX = (int)rectShop.getMaxX() - 1; endX = (int)rectShop.getMaxX() - 1;
						startY = (int)rectShop.getMinY() + 1; endY = (int)rectShop.getMaxY() - 1; break;
			default: break;
		}

		int x = random.nextInt(startX, endX + 1);
		int y = random.nextInt(startY, endY + 1);
		int i = 0;
		while (!map[y + 1][x].equals("1") && !map[y - 1][x].equals("1")
			&& !map[y][x - 1].equals("1") && !map[y][x + 1].equals("1") && i < 30){
			x = random.nextInt(startX, endX + 1);
			y = random.nextInt(startY, endY + 1);
			i++;
		}
		map[y][x] = "S";
	}

	private void fillWall(){
		for (int y = 0; y < maxY; y++){
			for (int x = 0; x < maxX; x++){
				if (map[y][x].equals("0")){
					if (map[y + 1][x].equals(".")){
						map[y + 1][x] = "1";
					}
					if (map[y + 1][x + 1].equals(".")){
						map[y + 1][x + 1] = "1";
					}
					if (map[y + 1][x - 1].equals(".")){
						map[y + 1][x - 1] = "1";
					}
					if (map[y][x + 1].equals(".")){
						map[y][x + 1] = "1";
					}
					if (map[y][x - 1].equals(".")){
						map[y][x - 1] = "1";
					}
					if (map[y - 1][x].equals(".")){
						map[y - 1][x] = "1";
					}
					if (map[y - 1][x + 1].equals(".")){
						map[y - 1][x + 1] = "1";
					}
					if (map[y - 1][x - 1].equals(".")){
						map[y - 1][x - 1] = "1";
					}
				}
			}
		}
	}

	private void fillFile(File file, String object, String recipe){
		try {
			FileWriter fileWriter = new FileWriter(file);

			for (int y = 0; y < maxY; y++){
				for (int x = 0; x < maxX; x++){
					if (x == maxX - 1){
						fileWriter.write(map[y][x] + "\n");
					} else {
						fileWriter.write(map[y][x] + " ");
					}
				}
			}
			fileWriter.write("ENDMAP\n" +
							object + 
							recipe + 
							"ENDOBJ\n" + 
							"inventaire:\n" + 
							"money:0\n" + 
							"machine:\n" + 
							"ENDINV\n" + 
							"collecter:\n" + 
							"crafter:\n" + 
							"ENDMACHINE\n");
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void fillRectangle(Rectangle rectangle){
		for (int i = (int)rectangle.getY(); i <= (int)rectangle.getMaxY(); i++){
			for (int j = (int)rectangle.getX(); j <= (int)rectangle.getMaxX(); j++){
				if (i == (int)rectangle.getY() || i == (int)rectangle.getMaxY()
				 || j == (int)rectangle.getX() || j == (int)rectangle.getMaxX()){
					map[i][j] = "1";
				} else if (j == (int)rectangle.getCenterX() && i == (int)rectangle.getCenterY()) {
					map[i][j] = "" + rooms.indexOf(rectangle);
				}else {
					map[i][j] = "0";
				}
			}
		}
		
	}

	private boolean checkPairs(int r1, int r2){
		for (int[] pair : pairs){
			if ((pair[0] == r1 && pair[1] == r2) || (pair[0] == r2 && pair[1] == r1)){
				return false;
			}
		}
		return true;
	}

	private boolean checkOthers(Rectangle rectangle) {
		for (int i = (int)rectangle.getY(); i <= (int)rectangle.getMaxY(); i++){
			for (int j = (int)rectangle.getX(); j <= (int)rectangle.getMaxX(); j++){
				if (!map[i][j].equals(".")) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean checkPos(Rectangle rectangle){
		return (rectangle.getWidth() + rectangle.getX() < maxX 
				&& rectangle.getHeight() + rectangle.getY() < maxY);
	}
}
