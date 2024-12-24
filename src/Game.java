import java.awt.*;
import javax.swing.*;

public class Game extends JLayeredPane {

	private JFrame window;
	private Data data;
	private Menu menu;
	private Mouse mouse;
	
	public Game(int width, int height, JFrame window, Thread thread) {
		super();
		this.window = window;
		this.mouse = new Mouse();
		this.data = new Data(width, height, (int)((width + height) / 62.5), 4 * (int)((width + height) / 62.5) / 48, new KeyHandler(), window, this, mouse, thread);
		mouse.data = data;
		this.menu = new Menu(data);
		setPreferredSize(new Dimension(width, height));
		setBackground(Color.BLACK);
		setDoubleBuffered(true);
		addKeyListener(data.key);
		setLayout(null);
		setFocusable(true);

		this.data.initialisePanel();
		this.add(data.menuPanel);
		this.setLayer(data.menuPanel, 1);
	}

	public void update(){
		menu.update();
	}

	public void initializeGame(){
		menu.initialiseMenu();
		menu.displayMenu();
	}

	public void drawGame(){
		menu.drawGame();
	}

	//@Override
	//public void paintComponent(Graphics g){
	//	Toolkit.getDefaultToolkit().sync();
	//	super.paintComponent(g);

	//	Graphics2D g2 = (Graphics2D)g;
	//	drawGame(g2);
	//}
}
