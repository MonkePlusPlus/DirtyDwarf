
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;


public class Menu extends JPanel {
	
	private Data data;
	private Map map;
	private Player player;
	private Inventory inventory;
	private MenuPause menuPause;
	private File fileMap = new File("save/maptest.txt");

	private JButton quitButton;
	private JButton contButton;
	private JButton optiButton;
	private JButton newgButton;

	private JTextArea titleScreen;

	private boolean running = false;

	public Menu(Data data){
		super();
		this.data = data;
		setBackground(Color.BLACK);
		setDoubleBuffered(true);
		addKeyListener(data.key);
		setLayout(null);
		setFocusable(true);
		setBounds(0, 0, data.width, data.height);
	}

	public void update(){
		if (running){
			map.update();
			player.update();
		}
	}

	public void drawGame(Graphics2D g){
		if (running){
			map.drawMap(g);
			player.draw(g);
		}
	}

	public void InitialiseMenu(){
		titleScreen = new JTextArea("DIRTY DWARF");
		titleScreen.setBounds(data.width / 5, data.height / 10, data.width, data.height / 5);
		titleScreen.setFont(new Font("Squealer", Font.BOLD, 150 * (data.width / 1920)));
		titleScreen.setOpaque(true);
		titleScreen.setBackground(new Color(0, 0, 0, 0));
		titleScreen.setForeground(Color.WHITE);
		titleScreen.setFocusable(false);

		contButton = createButton("CONTINUE", data.width / 3, (data.height / 6) * 2);
		newgButton = createButton("NEW GAME", data.width / 3, (data.height / 6) * 3);
		optiButton = createButton("OPTION", data.width / 3, (data.height / 6) * 4);
		quitButton = createButton("QUIT", data.width / 3, (data.height / 6) * 5);

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
				running = true;
			}
		});

		this.add(titleScreen);
		this.add(contButton);
		this.add(newgButton);
		this.add(optiButton);
		this.add(quitButton);
	}

	public void InitializeLevel(){
		this.player = new Player(data);
		this.inventory = new Inventory(data);
		this.map = new Map(data, fileMap, inventory);
		this.menuPause = new MenuPause(data);

		player.InitializePlayer(0, 0);
		map.InitializeMap();
		inventory.InitializeInventory(map.listObj, fileMap);
		menuPause.InitializeMenuPause(this, inventory);
		map.printMap();

		inventory.showInvButton();
		menuPause.showPauseButton();
	}

	public void displayMenu(){
		data.panel.removeAll();
		data.panel.add(this);
	}

	public JButton createButton(String name, int x, int y){
		JButton button = new JButton(name);
		button.setBounds(x, y, data.width / 3, data.height / 8);
		button.setFocusPainted(false);
		return button;
	}
}
