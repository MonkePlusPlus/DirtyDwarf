import java.awt.*;
import java.io.File;
import javax.swing.*;

public class Game extends JPanel {

	private int width;
	private int height;
	private Map map;
	KeyHandler key = new KeyHandler();
	private Player player;

	public Game(int width, int height) {
		super();
		this.width = width;
		this.height = height;
		this.player = new Player(width, height, key, 48);
		this.map = new Map(width, height, new File("save/maptest.txt"), 48, key, 4);
		setPreferredSize(new Dimension(width, height));
		setBackground(Color.BLACK);
		setDoubleBuffered(true);
		addKeyListener(key);
		setFocusable(true);
	}

	public void update(){
		map.update();
		player.update();
	}

	public void InitializeGame(){
		player.InitializePlayer(0, 0, 4);
		map.InitializeMap();
		map.printMap();
	}

	@Override
	public void paintComponent(Graphics g){
		Toolkit.getDefaultToolkit().sync();
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D)g;
		map.drawMap(g2);
		player.draw(g2);
	}
}
