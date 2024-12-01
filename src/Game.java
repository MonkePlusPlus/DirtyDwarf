import java.awt.*;
import java.io.File;
import javax.swing.*;

public class Game extends JPanel {

	private Data data;
	private Map map;
	private Player player;
	private Inventory inventory;
	private File fileMap = new File("save/maptest.txt");

	public Game(int width, int height) {
		super();
		this.data = new Data(width, height, 48, 4, new KeyHandler(), this);
		this.player = new Player(data);
		this.map = new Map(data, fileMap);
		this.inventory = new Inventory(data);
		setPreferredSize(new Dimension(width, height));
		setBackground(Color.BLACK);
		setDoubleBuffered(true);
		addKeyListener(data.key);
		setLayout(null);
		setFocusable(true);
	}

	public void update(){
		map.update();
		player.update();
	}

	public void InitializeGame(){
		player.InitializePlayer(0, 0);
		map.InitializeMap();
		inventory.InitializeInventory(map.listItem, fileMap);
		map.printMap();
	}

	public void drawGame(Graphics2D g2){
		map.drawMap(g2);
		player.draw(g2);
	}

	@Override
	public void paintComponent(Graphics g){
		Toolkit.getDefaultToolkit().sync();
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D)g;
		drawGame(g2);
	}
}
