import java.awt.*;
import javax.swing.*;

public class Game extends JPanel {

	private Data data;
	private Menu menu;
	
	public Game(int width, int height) {
		super();
		this.data = new Data(width, height, (int)((width + height) / 62.5), 4, new KeyHandler(), this);
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

	public void InitializeGame(){
		menu.InitialiseMenu();
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
