
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JPanel;

public class MiniMap {
	
	private Data data;

	private JPanel MiniBackgroundPane;
	private JPanel bigMapPane;
	private boolean[][] MiniMap;
	private JButton button;

	private int lenX;
	private int lenY;
	private int startX;
	private int startY;

	private Color floorColor = new Color(0, 255, 255, 200);
	private Color backColor = new Color(0, 160, 179, 150);

	public MiniMap(Data data, Map map, int lenX, int lenY, int startX, int startY){
		this.startX = startX;
		this.startY = startY;
		this.lenX = lenX;
		this.lenY = lenY;
	}

	public void initialiseMiniMap(Tile[][] tileMap) {
		MiniMap = new boolean[lenY][lenX];

		for (int y = 0; y < lenY; y++){
			for (int x = 0; x < lenX; x++){
				switch (tileMap[y][x].getType()) {
					case WALL: case NONE: MiniMap[y][x] = false; break;
					default: MiniMap[y][x] = true;
				}
			}
		}

		button = new JButton();
		button.setBackground(new Color(0, 0, 0, 0));
		button.setOpaque(false);
		button.setBounds((int)(data.width - data.width / 6.5), data.height / 4 + data.height / 20, data.width / 6, data.height / 4);

	}
}
