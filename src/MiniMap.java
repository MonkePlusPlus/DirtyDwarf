
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;

public class MiniMap extends JPanel {
	
	private Data data;
	private Map map;
	private Player player;

	private JPanel miniBackgroundPane;
	private boolean[][] miniMap;
	private JButton button;

	private boolean miniMapOpen;

	private int lenX;
	private int lenY;
	private int startX;
	private int startY;

	private Color floorColor = new Color(0, 255, 255, 255);
	private Color backColor = new Color(0, 160, 179, 150);
	private Color playerColor = new Color(0, 220, 0, 255);

	public MiniMap(Data data, Player player, Map map, int lenX, int lenY, int startX, int startY){
		this.data = data;
		this.map = map;
		this.player = player;
		this.startX = startX;
		this.startY = startY;
		this.lenX = lenX;
		this.lenY = lenY;
		this.setBackground(backColor);
		this.setFocusable(false);
		this.setBounds((int)(data.width - data.width / 5), data.height / 20, data.width / 6, data.height / 4);
	}

	public void initialiseMiniMap(Tile[][] tileMap) {
		miniMap = new boolean[lenY][lenX];

		for (int y = 0; y < lenY; y++){
			for (int x = 0; x < lenX; x++){
				switch (tileMap[y][x].getType()) {
					case WALL: case NONE: miniMap[y][x] = false; break;
					default: miniMap[y][x] = true;
				}
			}
		}

		button = new JButton();
		button.setBackground(new Color(0, 0, 0, 0));
		//button.setBackground(backColor);
		button.setFocusable(false);
		button.setOpaque(false);
		button.setBounds((int)(data.width - data.width / 5), data.height / 20, data.width / 6, data.height / 4);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (miniMapOpen){
					data.windowOpen = false;
					data.clearMenuPanel();
					miniMapOpen = false;
				} else {
					miniMapOpen = true;
					data.windowOpen = true;
					data.clearMenuPanel();
				}				
				data.panel.revalidate();
				data.panel.requestFocusInWindow();
			}			
		});
		/*
		miniBackgroundPane = new JPanel();
		miniBackgroundPane.setFocusable(false);
		miniBackgroundPane.setBackground(backColor);
		miniBackgroundPane.setBounds((int)(data.width - data.width / 5), data.height / 20, data.width / 6, data.height / 4);
*/
		data.menuPanel.add(this);
	}

	@Override
	public void paintComponent(Graphics g){
		Toolkit.getDefaultToolkit().sync();
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D)g;
		drawMiniMap(g2);
	}

	public void drawMiniMap(Graphics2D g){
		if (data.key.pause){
			removeButtonMiniMap();
			data.menuPanel.repaint();
			data.panel.revalidate();
			data.panel.requestFocusInWindow();
			return ;
		}
		showButtonMiniMap();
		if (miniMapOpen == false){
			drawMiniMapTop(g);
			return ;
		}
		this.setBounds(data.width / 5, data.height / 5,  3 * data.width / 5, 3 * data.height / 5);
		int sizeX = (3 * data.width / 5) / lenX;
		int sizeY = (3 * data.height / 5) / lenY;

		for (int y = 0; y < lenY; y++) {
			for (int x = 0; x < lenX; x++) {
				if (miniMap[y][x]) {
					g.setColor(floorColor);
					g.fillRect(x * sizeX, y * sizeY, sizeX, sizeY);
				}
			}
		}
		int[] pPos = map.getIndexPos(player.x, player.y);
		g.setColor(playerColor);
		g.fillRect(pPos[0] * sizeX, pPos[1] * sizeY, sizeX, sizeY);
	}

	public void drawMiniMapTop(Graphics2D g){
		this.setBounds((int)(data.width - data.width / 5), data.height / 20, data.width / 6, data.height / 4);

		int sizeX = (data.width / 6) / 30;
		int sizeY = (data.height / 4) / 30;

		int[] pPos = map.getIndexPos(player.x, player.y);
		int i = 0;
		int j;
		for (int y = pPos[1] - 15; y < pPos[1] + 15; y++) {
			j = 0;
			for (int x = pPos[0] - 15; x < pPos[0] + 15; x++) {
				if (x == pPos[0] && y == pPos[1]) {
					g.setColor(playerColor);
					g.fillRect(j * sizeX, i * sizeY, sizeX, sizeY);
				} else if (x > 0 && y > 0 && x < lenX && y < lenY && miniMap[y][x]) {
					g.setColor(floorColor);
					g.fillRect(j * sizeX, i * sizeY, sizeX, sizeY);
				}
				j++;
			}
			i++;
		}
	}

	public void showBigMap(){
		data.menuPanel.add(this);
	}

	public void update() {
		repaint();
	}

	public void showButtonMiniMap(){
		if (!data.panel.isAncestorOf(button)){
			data.panel.add(button);
			data.panel.setLayer(button, 2);
		}
	}

	public void removeButtonMiniMap(){
		if (data.panel.isAncestorOf(button)){
			data.panel.remove(button);
		}
	}
}
