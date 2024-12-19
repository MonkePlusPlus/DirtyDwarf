import java.awt.*;
import javax.swing.*;

public class Game extends JPanel {

	private JFrame window;
	private Data data;
	private Menu menu;
	private Mouse mouse;
	
	public Game(int width, int height, JFrame window) {
		super();
		this.window = window;
		this.mouse = new Mouse();
		this.data = new Data(width, height, (int)((width + height) / 62.5), 4 * (width / 1920), new KeyHandler(), window, this, mouse);
		mouse.data = data;
		this.menu = new Menu(data);
		setPreferredSize(new Dimension(width, height));
		setBackground(Color.BLACK);
		setDoubleBuffered(true);
		addKeyListener(data.key);
		setLayout(null);
		setFocusable(true);
	}

	public void update(){
		menu.update();
	}

	public void initializeGame(){
		menu.initialiseMenu();
		menu.displayMenu();
	}

	public void drawGame(Graphics2D g2){
		menu.drawGame(g2);
	}

	@Override
	public void paintComponent(Graphics g){
		Toolkit.getDefaultToolkit().sync();
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D)g;
		drawGame(g2);
	}
}
