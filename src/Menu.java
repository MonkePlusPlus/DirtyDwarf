
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;

public class Menu extends JPanel {
	
	private Data data;
	private Map map;
	private Player player;
	private Inventory inventory;
	private MenuPause menuPause;

	private RandomMapGenerator mapGenerator;
	//private File fileMap = new File("save/test/level1.txt");

	private JButton quitButton;
	private JButton contButton;
	private JButton optiButton;
	private JButton newgButton;
	private JTextArea titleScreen;

	private JTextArea mapText;
	private JLayeredPane mapMenu;
	private JPanel backMapPane;
	private JButton[] mapButton;
	private JButton[] delButton;

	private JScrollPane selectPane;
	private JPanel buttonPane;
	private JButton backButton;

	private JTextArea createName;
	private JTextField nameSave;
	private JPanel createPane;
	private JButton createButton;
	private JButton backCButton;
	private JTextArea errorMessage;

	private String[] texturePack;

	public Menu(Data data){
		super();
		this.data = data;
		this.mapGenerator = new RandomMapGenerator();
		setBackground(Color.BLACK);
		setDoubleBuffered(true);
		addKeyListener(data.key);
		setLayout(null);
		setFocusable(true);
		setBounds(0, 0, data.width, data.height);
		texturePack = new String[3];
		texturePack[0] = "pack1.png";
		texturePack[1] = "objet:FER_F_5_10_2;OR_O_5_10_1;DIAMANT_D_20_100_1\n";
		texturePack[2] = "recette:LINGOT DE FER_3-FER_0_20;LINGOT D'OR_3-OR_0_20;LINGOT DE DIAMANT_3-DIAMANT_10_100;" + 
						 "EPEE_5-LINGOT D'OR+5-LINGOT DE FER_10_40;BOUCLIER_10-LINGOT D'OR+10-LINGOT DE FER_20_80;" + 
						 "BIJOU_30-LINGOT D'OR+1-LINGOT DE DIAMANT_40_250;CASQUE_50-LINGOT D'OR+50-LINGOT DE FER+20-LINGOT DE DIAMANT_120_1000\n";
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
		titleScreen.setFont(new Font("Squealer Embossed", Font.PLAIN, 150 * data.size / 48));
		titleScreen.setBackground(new Color(0, 0, 0, 0));
		titleScreen.setForeground(Color.WHITE);
		titleScreen.setFocusable(false);

		int[]tsize = data.getTextSize(titleScreen);
		titleScreen.setBounds(data.width / 2 - tsize[0] / 2, data.height / 10, tsize[0], tsize[1]);

		contButton = createButton("CONTINUE", data.width / 3, (data.height / 6) * 2);
		newgButton = createButton("NEW GAME", data.width / 3, (data.height / 6) * 3);
		optiButton = createButton("OPTION", data.width / 3, (data.height / 6) * 4);
		quitButton = createButton("QUIT", data.width / 3, (data.height / 6) * 5);

		newgButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				displayCreate();
			}
		});

		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				data.panel.removeAll();
				System.exit(0);
			}
		});

		contButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				displayMapMenu();
			}
		});

		this.add(titleScreen);
		this.add(contButton);
		this.add(newgButton);
		this.add(optiButton);
		this.add(quitButton);
		initialiseMapMenu();
		initialiseCreatePane();
	}

	public void initialiseMapMenu(){
		mapMenu = new JLayeredPane();
		mapMenu.setDoubleBuffered(true);
		mapMenu.addKeyListener(data.key);
		mapMenu.setLayout(null);
		mapMenu.setFocusable(true);
		mapMenu.setBounds(0, 0, data.width, data.height);

		backMapPane = new JPanel();
		backMapPane.setLayout(null);
		backMapPane.setFocusable(false);
		backMapPane.setBackground(Color.BLACK);
		backMapPane.setBounds(0, 0, data.width, data.height);

		mapText = new JTextArea("Select Save");
		mapText.setFont(new Font("Squealer Embossed", Font.PLAIN, 150 * data.size / 48));
		mapText.setBackground(new Color(0, 0, 0, 0));
		mapText.setForeground(Color.WHITE);
		mapText.setFocusable(false);

		int[] tsize = data.getTextSize(mapText);
		mapText.setBounds(data.width / 2 - tsize[0] / 2, data.height / 10, tsize[0], tsize[1]);

		backButton = data.createButton("BACK",
					new Rectangle(data.width / 2 - data.width / 10, data.height - data.height / 5, data.width / 5, data.height / 10), 
					50, Font.PLAIN, Color.WHITE);
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				displayMenu();
			}
		});

		buttonPane = new JPanel();
		buttonPane.setLayout(null);
		buttonPane.setBackground(Color.WHITE);
		buttonPane.setLocation(0, 0);

		selectPane = new JScrollPane(buttonPane);
		selectPane.setBounds(data.width / 2 - data.width / 6, data.height / 4, data.width / 3, data.height / 2);
		selectPane.setBackground(Color.WHITE);
		selectPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		selectPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		mapMenu.add(selectPane);
		mapMenu.add(backMapPane);
		mapMenu.add(mapText);
		mapMenu.add(backButton);

		mapMenu.setLayer(selectPane, 1);
		mapMenu.setLayer(backMapPane, 0);
		mapMenu.setLayer(mapText, 1);
		mapMenu.setLayer(backButton, 1);
	}

	public void updateMapMenu(){
		File[] saveFolder = (new File("save")).listFiles();
		int nbSave = saveFolder.length;
		int index = 0;
		int space = data.height / 20;

		mapButton = new JButton[nbSave];
		delButton = new JButton[nbSave];
		buttonPane.setMinimumSize(new Dimension(data.width / 3, data.height / 10 * nbSave));
		buttonPane.setPreferredSize(new Dimension(data.width / 3, data.height / 10 * nbSave + (nbSave + 1) * space));
		buttonPane.removeAll();
		if (!data.panel.isAncestorOf(selectPane)){
			data.panel.add(selectPane);
			data.panel.setLayer(selectPane, 1);
			data.panel.add(backButton);
			data.panel.setLayer(backButton, 1);
		}
		for (File file : saveFolder){
			mapButton[index] = data.createButton(file.getName(),
								new Rectangle(data.width / 6 - data.width / 8, index * data.height / 10 + (index + 1) * space, data.width / 4, data.height / 10),
								40, Font.PLAIN, Color.GRAY);
			mapButton[index].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					File[] levels = file.listFiles();
					System.out.println("map name : " + levels[0].getName());
					data.panel.removeAll();
					initializeLevel(levels[0]);
					data.running = true;
				}
			});
			delButton[index] = data.createButton("Delete",
				new Rectangle(data.width / 6 - data.width / 12, index * data.height / 10 + (index + 1) * space + data.height / 10, data.width / 6, data.height / 30), 
								30, Font.PLAIN, Color.RED);
			delButton[index].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					data.panel.removeAll();
					JPanel back = new JPanel();
					back.setBackground(Color.BLACK);
					back.setLayout(null);
					back.setBounds(0, 0, data.width, data.height);

					JTextArea text = new JTextArea("Are you sure ?");
					text.setFont(new Font("Squealer Embossed", Font.PLAIN, 150 * data.size / 48));
					text.setBackground(new Color(0, 0, 0, 0));
					text.setForeground(Color.WHITE);
					text.setFocusable(false);
			
					int[] tsize = data.getTextSize(text);
					text.setBounds(data.width / 2 - tsize[0] / 2, data.height / 3, tsize[0], tsize[1]);

					JButton yesButton = data.createButton("YES", 
										new Rectangle(data.width / 2 + data.width / 30, data.height / 3 + data.width / 10, data.width / 10, data.height / 20), 
										40, Font.PLAIN, Color.WHITE);
					yesButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							File[] levels = file.listFiles();
							for (File f : levels){
								f.delete();
							}
							file.delete();
							data.panel.removeAll();
							updateMapMenu();
							displayMapMenu();
						}
						
					});
					JButton noButton = data.createButton("NO", 
									   new Rectangle(data.width / 2 - data.width / 30 - data.width / 20 , data.height / 3 + data.width / 10, data.width / 10, data.height / 20), 
									   40, Font.PLAIN, Color.WHITE);

					noButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							data.panel.removeAll();
							displayMapMenu();
						}
					});

					data.panel.add(back);
					data.panel.add(text);
					data.panel.add(yesButton);
					data.panel.add(noButton);

					data.panel.setLayer(back, 2);
					data.panel.setLayer(text, 3);
					data.panel.setLayer(yesButton, 3);
					data.panel.setLayer(noButton, 3);
				}
			});

			buttonPane.add(mapButton[index]);
			buttonPane.add(delButton[index]);
			index++;
		}
	}

	public void initialiseCreatePane(){
		createPane = new JPanel();
		createPane.setDoubleBuffered(true);
		createPane.addKeyListener(data.key);
		createPane.setBackground(Color.BLACK);
		createPane.setLayout(null);
		createPane.setFocusable(true);
		createPane.setBounds(0, 0, data.width, data.height);

		errorMessage = new JTextArea("Already exist");
		errorMessage.setFont(new Font("Squealer Embossed", Font.PLAIN, 40 * data.size / 48));
		errorMessage.setBackground(new Color(0, 0, 0, 0));
		errorMessage.setForeground(Color.RED);
		errorMessage.setFocusable(false);

		int[] tsize = data.getTextSize(errorMessage);
		errorMessage.setBounds(data.width / 2 - tsize[0] / 2, data.height / 2 - data.height / 8, tsize[0], tsize[1]);

		createName = new JTextArea("Enter name :");
		createName.setFont(new Font("Squealer Embossed", Font.PLAIN, 150 * data.size / 48));
		createName.setBackground(new Color(0, 0, 0, 0));
		createName.setForeground(Color.WHITE);
		createName.setFocusable(false);

		tsize = data.getTextSize(createName);
		createName.setBounds(data.width / 2 - tsize[0] / 2, data.height / 10, tsize[0], tsize[1]);

		backCButton = data.createButton("BACK",
					new Rectangle(data.width / 2 - data.width / 10, data.height - data.height / 5, data.width / 5, data.height / 10), 
					50, Font.PLAIN, Color.WHITE);
		backCButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				displayMenu();
			}
		});

		nameSave = new JTextField();
		nameSave.setFocusable(true);
		nameSave.setBackground(Color.WHITE);
		nameSave.setBounds(data.width / 2 - data.width / 5, data.height / 2 - data.height / 16, data.width / 5, data.height / 8);
		nameSave.setFont(new Font("Squealer Embossed", Font.PLAIN, 50 * data.size / 48));

		createButton = data.createButton("CREATE",
										new Rectangle(data.width / 2, data.height / 2 - data.height / 16, data.width / 6, data.height / 8), 
										70, Font.PLAIN, Color.GREEN);
		
		createButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File file = new File("save/" + nameSave.getText());
				if (!file.exists() && !nameSave.getText().equals("")){
					file.mkdir();
					try {
						File level = new File("save/" + file.getName() + "/level1.txt");
						level.createNewFile();
						mapGenerator.createRandomMap(level, texturePack);
						data.panel.removeAll();
						initializeLevel(level);
						data.running = true;
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {
					if (data.panel.isAncestorOf(errorMessage)){
						return ;
					}
					Timer timer = new Timer(3000, new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							if (data.panel.isAncestorOf(errorMessage)){
								data.panel.remove(errorMessage);
								data.panel.revalidate();
								data.panel.repaint();
							}
						}
					});
					timer.setRepeats(false);
					timer.start();
					data.panel.add(errorMessage);
				}
			}
			
		});

		createPane.add(nameSave);
		createPane.add(createButton);
		createPane.add(createName);
		createPane.add(backCButton);
	}

	public void showButtons(){
		inventory.showInvButton();
		menuPause.showPauseButton();
	}

	public void initializeLevel(File fileMap){
		data.window.addMouseListener(data.mouse);
		data.window.addMouseMotionListener(data.mouse);

		this.player = new Player(data);
		this.inventory = new Inventory(data, player);
		this.map = new Map(data, fileMap, inventory, player, texturePack);
		this.menuPause = new MenuPause(data);

		player.initializePlayer();
		map.initializeMap();
		inventory.initializeInventory(map.listObj, fileMap);
		map.updateMachine();
		menuPause.initializeMenuPause(this);

		showButtons();
	}

	public void displayMenu(){
		data.panel.removeAll();
		data.running = false;
		data.panel.add(this);
	}

	public void displayMapMenu(){
		data.panel.removeAll();
		updateMapMenu();
		data.running = false;
		data.panel.add(mapMenu);
	}

	public void displayCreate(){
		data.panel.removeAll();
		data.running = false;
		data.panel.add(createPane);
	}

	public JButton createButton(String name, int x, int y){
		JButton button = new JButton(name);
		button.setBounds(x, y, data.width / 3, data.height / 8);
		button.setFocusPainted(false);
		return button;
	}
}
