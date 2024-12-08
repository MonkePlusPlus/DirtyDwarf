
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JTextArea;


public class Menu {
	
	private Data data;
	private Map map;
	private Player player;
	private Inventory inventory;
	private File fileMap = new File("save/maptest.txt");

	private JButton quitButton;
	private JButton contButton;
	private JButton newgButton;

	private JTextArea titleScreen;

	private boolean level = false;

	public Menu(Data data){
		this.data = data;
		this.player = new Player(data);
		this.map = new Map(data, fileMap);
		this.inventory = new Inventory(data);
	}

	public void update(){
		map.update();
		player.update();
	}

	public void drawGame(Graphics2D g){
		if (level){
			map.drawMap(g);
			player.draw(g);
		}
	}

	public void InitialiseMenu(){
		titleScreen = new JTextArea("DIRTY DWARF");
		titleScreen.setBounds(data.width / 5, data.height / 10, data.width, data.height / 5);
		titleScreen.setFont(new Font("Arial Black", Font.BOLD, 150 * (data.width / 1920)));
		titleScreen.setOpaque(true);
		titleScreen.setBackground(new Color(0, 0, 0, 0));
		titleScreen.setForeground(Color.WHITE);
		titleScreen.setFocusable(false);

		contButton = createButton("CONTINUE", data.width / 3, (data.height / 5) * 2);
		newgButton = createButton("NEW GAME", data.width / 3, (data.height / 5) * 3);
		quitButton = createButton("QUIT", data.width / 3, (data.height / 5) * 4);

		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				data.panel.removeAll();
				System.exit(0);
			}
		});

		contButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				data.panel.removeAll();
				InitializeLevel();
				inventory.showInvButton();
				level = true;
			}
		});
	}

	public void InitializeLevel(){
		player.InitializePlayer(0, 0);
		map.InitializeMap();
		inventory.InitializeInventory(map.listItem, fileMap);
		map.printMap();
	}

	public void displayMenu(){
		data.panel.removeAll();
		data.panel.add(titleScreen);
		data.panel.add(quitButton);
		data.panel.add(contButton);
		data.panel.add(newgButton);
	}

	public JButton createButton(String name, int x, int y){
		JButton button = new JButton(name);
		button.setBounds(x, y, data.width / 3, data.height / 8);
		button.setFocusPainted(false);
		return button;
	}
}
