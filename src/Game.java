import java.awt.*;
import java.io.File;
import javax.swing.*;

public class Game extends JPanel {

	private Data data;
	private Map map;
	private Player player;

	public Game(int width, int height) {
		super();
		this.data = new Data(width, height, 48, 4, new KeyHandler());
		this.player = new Player(data);
		this.map = new Map(data, new File("save/maptest.txt"));
		setPreferredSize(new Dimension(width, height));
		setBackground(Color.BLACK);
		setDoubleBuffered(true);
		addKeyListener(data.key);
		setFocusable(true);
	}

	public void update(){
		map.update();
		player.update();
	}

	public void InitializeGame(){
		player.InitializePlayer(0, 0);
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
