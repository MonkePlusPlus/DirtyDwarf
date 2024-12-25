
import java.awt.Color;
import java.awt.Font;
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
		if (data.running){
			map.update();
			player.update();
		}
	}

	public void drawGame(){
		if (data.running){
			map.repaint();
		}
	}

	public void initialiseMenu(){
		titleScreen = new JTextArea("DIRTY DWARF");
		titleScreen.setBounds(data.width / 5, data.height / 10, data.width, data.height / 5);
		titleScreen.setFont(new Font("Squealer", Font.BOLD, 150 * data.size / 48));
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
				initializeLevel();
				data.running = true;
			}
		});

		this.add(titleScreen);
		this.add(contButton);
		this.add(newgButton);
		this.add(optiButton);
		this.add(quitButton);
	}

	public void showButtons(){
		inventory.showInvButton();
		menuPause.showPauseButton();
	}

	public void initializeLevel(){
		data.window.addMouseListener(data.mouse);
		data.window.addMouseMotionListener(data.mouse);

		this.player = new Player(data);
		this.inventory = new Inventory(data, player);
		this.map = new Map(data, fileMap, inventory, player);
		this.menuPause = new MenuPause(data);

		player.initializePlayer();
		map.initializeMap();
		inventory.initializeInventory(map.listObj, fileMap);
		map.updateMachine();
		menuPause.initializeMenuPause(this);
		map.printMap();

		showButtons();
	}

	public void displayMenu(){
		data.panel.removeAll();
		data.running = false;
		data.panel.add(this);
	}

	public JButton createButton(String name, int x, int y){
		JButton button = new JButton(name);
		button.setBounds(x, y, data.width / 3, data.height / 8);
		button.setFocusPainted(false);
		return button;
	}
}
