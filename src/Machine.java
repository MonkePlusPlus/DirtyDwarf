import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;


public class Machine extends Object {

	private Data data;
	private Shop shop;
	private Map map;
	private Inventory inventory;

	public JPanel mainPanel;

	private Timer timerError;
	private JTextArea errorMessage;

	private JTextArea clickText;

	private int width;
	private int height;
	private int panelWidth;
	private int panelHeight;

	public Tile.TileType type;
	public Crafter.CrafterType crafterType;

	private JButton putButton;

	Color myColor = new Color(255, 255, 255, 150);
	Color transparent = new Color(0, 0, 0, 0);

	public Machine(Data data, Tile.TileType type, Crafter.CrafterType crafterType, BufferedImage image, Inventory inventory, Map map, Shop shop) {
		this.data = data;
		this.inventory = inventory;
		this.shop = shop;
		this.map = map;
		this.type = type;
		this.crafterType = crafterType;
		this.image = image;
		this.width = (int)(data.width / 1.25);
		this.height = (int)(data.height / 1.25);
		this.panelWidth = width / 3;
		this.panelHeight = height - (height / 9) * 3;
		switch (type){
			case CRAFTER : this.price = (crafterType == Crafter.CrafterType.NORMAL) ? 150 : 200 ; 
						this.name = (crafterType == Crafter.CrafterType.NORMAL) ? "NCRAFTER" : "PCRAFTER";
						break;
			default: this.price = 100; this.name = "COLLECTER"; break;
		}
		this.timerError = new Timer(3000, new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				data.panel.remove(errorMessage);
			}
			
		});
		timerError.setRepeats(false);
	}

	public void initialiseMachine() {
		errorMessage = new JTextArea("CAN'T PLACE HERE");
		errorMessage.setFocusable(false);
		errorMessage.setBackground(transparent);
		errorMessage.setForeground(Color.RED);
		errorMessage.setFont(new Font("Squealer Embossed", Font.PLAIN, 50 * data.size / 48));

		int[] textSize = data.getTextSize(errorMessage);
		errorMessage.setBounds(data.width / 2 - textSize[0] / 2, textSize[1] * 2, textSize[0], textSize[1]);

		clickText = new JTextArea("Left click : PUT | Right Click : CANCEL"); 
		clickText.setFocusable(false);
		clickText.setBackground(Color.WHITE);
		clickText.setForeground(Color.BLACK);
		clickText.setFont(new Font("Squealer Embossed", Font.PLAIN, 30 * data.size / 48));

		textSize = data.getTextSize(clickText);
		clickText.setBounds(data.width / 2 - textSize[0] / 2, textSize[1], textSize[0], textSize[1]);


		mainPanel = new JPanel();
		mainPanel.setBackground(myColor);
		mainPanel.setLayout(null);

		mainPanel.setBounds(0, 0, panelWidth, panelHeight);
		mainPanel.setFocusable(false);

		JLabel imageIcon = new JLabel(new ImageIcon(new ImageIcon(image).getImage().getScaledInstance(data.size * 2, data.size * 2, Image.SCALE_DEFAULT)));
		imageIcon.setBounds(panelWidth / 2 - data.size, 0, data.size * 2, data.size * 2);
		imageIcon.setBackground(Color.WHITE);

		JTextArea text = new JTextArea(name);
		text.setFont(new Font("Squealer Embossed", Font.PLAIN, 30 * data.size / 48));
		text.setBackground(transparent);
		text.setFocusable(false);
		text.setForeground(Color.BLACK);

		int[] tsize = data.getTextSize(text);
		text.setBounds(panelWidth / 2 - tsize[0] / 2, data.size * 2 + data.size / 2, tsize[0], tsize[1]);

		if (type == Tile.TileType.CRAFTER) {
			JTextArea description = new JTextArea("Type : " + ((crafterType == Crafter.CrafterType.POLYVALENT) ? "polyvalent": "normal"));
			description.setBounds(panelWidth / 10, data.size * 4, panelWidth / 2, panelHeight / 10);
			description.setFont(new Font("Squealer Embossed", Font.PLAIN, 30 * data.size / 48));
			description.setBackground(transparent);
			description.setFocusable(false);
			description.setForeground(Color.BLACK);
			mainPanel.add(description);
		}

		putButton = new JButton("PUT");
		putButton.setFont(new Font("Squealer Embossed", Font.PLAIN, 30 * data.size / 48));
		putButton.setBounds(panelWidth / 2 - panelWidth / 4,  4 * panelHeight / 6, panelWidth / 2, panelHeight / 6);
		putButton.setFocusable(false);
		putButton.setBackground(Color.YELLOW);
		putButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Thread thread;
				thread = new Thread(new Runnable() {
					@Override
					public void run() {
						putMachine();
					}
                });
				thread.start();
				
			}
			
		});

		mainPanel.add(putButton);
		mainPanel.add(imageIcon);
		mainPanel.add(text);
	}

	public void showMachine(JPanel descPanel) {
		descPanel.add(mainPanel);
	}

	public void removeMachine(JPanel descPanel) {
		descPanel.remove(mainPanel);
	}

	public void putMachine(){
		data.key.editMode = true;
		data.key.move = true;
		switch (type) {
			case CRAFTER: data.key.type = 0; break;
			default: data.key.type = 1; break;
		}
		data.panel.add(clickText);
		data.panel.setLayer(clickText, 1);
		while (data.key.editMode) {
			SwingUtilities.invokeLater(() -> {
                if (data.mouse.rightClick || data.key.pause) {
					System.out.println("pause");
					data.panel.remove(clickText);
					if (data.panel.isAncestorOf(errorMessage)){
						data.panel.remove(errorMessage);
					}
					data.key.editMode = false;
				}
				if (data.mouse.leftClick) {
					switch (type){
						case CRAFTER : putCrafter(); break;
						default: putCollecter(); break;
					}
				}
            });
			try {
                Thread.sleep(16); // Add small delay to prevent CPU overuse
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
		}
	}

	public void putCrafter(){
		int[] index = map.getIndexPos(data.mouse.x, data.mouse.y);

		if (map.cantPut(index[0], index[1])) {
			data.panel.add(errorMessage);
			data.panel.setLayer(errorMessage, 1);
			timerError.restart();
			return ;
		}
		Crafter crafter = new Crafter(data, null, map, inventory, crafterType, 0, index[0], index[1], true, image, map.createNewSymb(), null);
		map.addNewMachine(crafter);
		map.setTile(crafter, index[0], index[1]);
		crafter.initialiseCrafer();
		inventory.deleteObj(this, 1);
		data.panel.remove(clickText);
		data.key.editMode = false;
	}

	public void putCollecter(){
		int[] index = map.getIndexPos(data.mouse.x, data.mouse.y);
		
		if (map.cantPut(index[0], index[1]) || map.nextToRessource(index[0], index[1]) == 0) {
			data.panel.add(errorMessage);
			data.panel.setLayer(errorMessage, 1);
			timerError.restart();
			return ;
		}
		Collecter collecter = new Collecter(null, data, map, inventory, index[0], index[1], true, image, 0, map.createNewSymb());
		map.addNewMachine(collecter);
		collecter.initialiseCollecter();
		map.setTile(collecter, index[0], index[1]);
		inventory.deleteObj(this, 1);
		data.panel.remove(clickText);
		data.key.editMode = false;
	}

	@Override
	public ObjectType getType() {
		return ObjectType.MACHINE;
	}
}
