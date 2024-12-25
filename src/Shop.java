
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Shop extends Block {

	private LinkedList<Object> sellingObj;
	private Map map;
	private Inventory inventory;
	private Player player;

	private JTabbedPane mainPane;
	private JPanel buyPane;
	private JScrollPane buySrollPane;
	private JLayeredPane buyObjPane;
	private JTextArea buyText;
	private JButton[] buyButton;

	private JPanel sellPane;
	private JPanel objDescPane;
	private JScrollPane sellSrollPane;
	private JLayeredPane sellObjPane;
	private JTextArea sellText;
	private JLabel shopImage;

	private JTextArea moneyText;

	final private int posX;
	final private int posY;
	final private int width;
	final private int height;
	final private int textSize;
	final private int spaceBetween;

	private BufferedImage shopKepper;

	private JButton shopButton;
	private boolean shopOpen;

	private BufferedImage collecterImage;
	private BufferedImage crafterImage;

	Color myColor = new Color(255, 255, 255, 150);
	Color transparent = new Color(0, 0, 0, 0);

	private Timer timerClick;
	private int sizeTile = 48;

	public Shop(Data data, Player player, int x, int y, boolean col, BufferedImage image, Inventory inventory, Map map){
		super(data, x, y, col, image);
		this.sellingObj = new LinkedList<Object>();
		this.inventory = inventory;
		this.map = map;
		this.player = player;
		this.posX = data.width / 10;
		this.posY = data.height / 10;
		this.width = (int)(data.width / 1.25);
		this.height = (int)(data.height / 1.25);
		this.textSize = height / 9;
		this.spaceBetween = width / 25;
		this.shopOpen = false;
		try {
			shopKepper = ImageIO.read(new File("asset/shop.png"));
			collecterImage = ImageIO.read(new File("asset/tiles.png")).getSubimage(sizeTile * 3, 0, sizeTile, sizeTile);
			crafterImage = ImageIO.read(new File("asset/tiles.png")).getSubimage(sizeTile * 4, 0, sizeTile, sizeTile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.timerClick = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				// wait
			}
		});
		timerClick.setRepeats(false);
	}

	@Override
	public TileType getType(){
		return TileType.SHOP;
	}

	public void initialiseObjShop(LinkedList<Object>[] listObj) {
		for (int i = 0; i < 3; i++){
			for (Object o : listObj[i]){
				switch (o.getType()) {
					case ITEM : case RECIPE : sellingObj.add(o);
						break;
					default: break ;
				}
			}
		}
		sellingObj.add(new Machine(data, TileType.COLLECTER , null, collecterImage, inventory, map, this));
		sellingObj.add(new Machine(data, TileType.CRAFTER , Crafter.CrafterType.NORMAL, crafterImage, inventory, map, this));
		sellingObj.add(new Machine(data, TileType.CRAFTER , Crafter.CrafterType.POLYVALENT, crafterImage, inventory, map, this));
		sellingObj.add(new PotionPlayer(data, player, collecterImage, "Player effect : (mine x 2)", 50, "P", 120, 2));
		sellingObj.add(new BlockPotion(data, map, inventory, crafterImage, "Machine : (work x 2)", 75, "M", 200, 2));
	}

	public void initialiseShop(){
		initialiseMoneyText();
		initialiseBuyPane();
		initialiseSellPane();
		initialiseMainPane();
	}

	public void updateShop(){
		updateMoney();
		updateBuyPane();
		updateSellPane();
	}

	public void initialiseMainPane(){
		mainPane = new JTabbedPane();
		mainPane.addTab("Buy", buyPane);
		mainPane.addTab("Sell", sellPane);
		mainPane.setBounds(posX, posY, width, height);
		mainPane.setBackground(myColor);
		mainPane.addKeyListener(data.key);
		mainPane.setFocusable(false);
		UIManager.put("TabbedPane.contentOpaque", false);
		UIManager.put("TabbedPane.selected", Color.RED);
		UIManager.put("TabbedPane.background", Color.RED);
		SwingUtilities.updateComponentTreeUI(mainPane);
		mainPane.setVisible(true);

		mainPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e){
				int selectedtab = mainPane.getSelectedIndex();
				switch (selectedtab) {
					case 1: sellPane.add(moneyText);
						break;
				
					default: buyPane.add(moneyText);
						break;
				}
			}
		});
		initializeTab();
	}

	public void initialiseBuyPane(){
		buyText = new JTextArea("BUY");
		buyText.setBackground(transparent);
		buyText.setFont(new Font("Squealer Embossed", Font.BOLD, 100 * data.size / 48));
		buyText.setFocusable(false);

		int[] sizeText = data.getTextSize(buyText);
		buyText.setBounds(width / 2 - sizeText[0] / 2, 0, sizeText[0], sizeText[1]);

		buyPane = new JPanel();
		buyPane.setLayout(null);
		buyPane.setBackground(myColor);
		buyPane.setFocusable(false);

		buySrollPane = new JScrollPane();
		buySrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		buySrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		buySrollPane.setBackground(transparent);
		buySrollPane.setBounds(width / 20, textSize + height / 20, width - width / 10, height - textSize * 3);
		buySrollPane.setFocusable(false);
		buySrollPane.setOpaque(true);

		buyObjPane = createBuyObjPane();
		buySrollPane.getViewport().add(buyObjPane);

		buySrollPane.setVisible(true);
		
		buyPane.add(buyText);
		buyPane.add(buySrollPane);
		buyPane.add(moneyText);
		updateBuyPane();
	}

	public JLayeredPane createBuyObjPane(){
		JLayeredPane pane = new JLayeredPane();
		int row = 0;
		int col = 0;
		int bindex = 0;
		JLabel imageLabel;
		int size = data.size * 2;

		buyButton = new JButton[sellingObj.size()];
		pane.setLayout(null);
		pane.setBackground(myColor);
		pane.setFocusable(true);
		pane.setPreferredSize(new Dimension(width - width / 10, (size + spaceBetween + height / 20) * sellingObj.size() / 8));
		for (Object obj : sellingObj) {

			imageLabel = new JLabel(new ImageIcon(new ImageIcon(obj.image).getImage().getScaledInstance(size, size, Image.SCALE_DEFAULT)));
			imageLabel.setBounds(col * size + (col + 1) * spaceBetween, row * size + row * (2 * spaceBetween), size, size);
			imageLabel.setBackground(Color.WHITE);
			imageLabel.setVisible(true);

			System.out.println(obj.name + " : " + obj.price);
			buyButton[bindex] = new JButton(obj.price + "$");
			buyButton[bindex].setFont(new Font("Squealer Embossed", Font.PLAIN, 30 * data.size / 48));
			buyButton[bindex].setFocusable(false);
			buyButton[bindex].setBackground(Color.YELLOW);
			buyButton[bindex].setBorder(BorderFactory.createLineBorder(Color.GREEN, 4 * data.width / 1920));
			buyButton[bindex].setBounds(col * size + (col + 1) * spaceBetween,
								row * size + row * (2 * spaceBetween) + size,
								size, height / 20);

			buyButton[bindex].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					inventory.addObj(obj, 1);
					inventory.money -= obj.price;
					updateShop();
				}
			});

			pane.add(imageLabel);
			pane.add(buyButton[bindex]);

			pane.setLayer(imageLabel, 0);
			pane.setLayer(buyButton[bindex], 1);
			bindex++;
			col++;
			if (col > 7){
				col = 0;
				row++;
			}
		}
		return pane;
	}

	public void updateBuyPane(){
		int i = 0;

		for (JButton b : buyButton){
			if (inventory.money >= sellingObj.get(i).price){
				b.setBorder(BorderFactory.createLineBorder(Color.GREEN, 4 * data.width / 1920));
				b.setEnabled(true);
			} else {
				b.setBorder(BorderFactory.createLineBorder(Color.RED, 4 * data.width / 1920));
				b.setEnabled(false);
			}
			i++;
		}
	}

	public void initialiseSellPane(){
		sellText = new JTextArea("SELL");
		sellText.setBackground(transparent);
		sellText.setFont(new Font("Squealer Embossed", Font.BOLD, 100 * data.size / 48));
		sellText.setFocusable(false);

		int[] sizeText = data.getTextSize(sellText);
		sellText.setBounds(width / 2 - sizeText[0] / 2, 0, sizeText[0], sizeText[1]);

		sellPane = new JPanel();
		sellPane.setLayout(null);
		sellPane.setBackground(myColor);
		sellPane.setFocusable(false);

		sellSrollPane = new JScrollPane();
		sellSrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sellSrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sellSrollPane.setBackground(myColor);
		sellSrollPane.setBounds(width / 20, textSize + height / 20, width / 2, height - textSize * 3);
		sellSrollPane.setFocusable(false);

		sellObjPane = createSellObjPane();
		sellSrollPane.getViewport().add(sellObjPane);

		shopImage = new JLabel(new ImageIcon(new ImageIcon(shopKepper).getImage().getScaledInstance(width / 3, height - textSize * 3, Image.SCALE_DEFAULT)));
		shopImage.setFocusable(false);
		shopImage.setBackground(transparent);
		shopImage.setBounds(0, 0, width / 3, height - textSize * 3);

		objDescPane = new JPanel();
		objDescPane.setOpaque(true);
		objDescPane.setBackground(transparent);
		objDescPane.setLayout(null);
		objDescPane.setBounds(width / 2 + width / 10, textSize + height / 20, width / 3, height - textSize * 3);
		objDescPane.setFocusable(false);
		objDescPane.add(shopImage);

		sellPane.add(sellText);
		sellPane.add(sellSrollPane);
		sellPane.add(objDescPane);
		updateSellPane();
	}

	public JLayeredPane createSellObjPane(){
		JLayeredPane pane = new JLayeredPane();
		int nb = inventory.getInventory().size();

		pane.setLayout(null);
		pane.setBackground(transparent);
		pane.setFocusable(true);
		pane.setPreferredSize(new Dimension(width / 2, (data.size * 2 + width / 25) * nb));
		return pane;
	}

	public JPanel createObjDesc(Slot s){

		int price = (80 * s.obj.price) / 100;
		int descWidth = width / 3;
		int descHeight = height - textSize * 3;
		System.out.println("width = " + descWidth + " height = " + descHeight);

		JPanel panel = new JPanel();
		panel.setBackground(myColor);
		panel.setLayout(null);
		panel.setBounds(0, 0, descWidth, descHeight);
		panel.setFocusable(false);

		JLabel image = new JLabel(new ImageIcon(new ImageIcon(s.obj.image).getImage().getScaledInstance(data.size * 2, data.size * 2, Image.SCALE_DEFAULT)));
		image.setBounds(descWidth / 2 - data.size, 0, data.size * 2, data.size * 2);
		image.setBackground(Color.WHITE);

		JTextArea text = new JTextArea(s.obj.name);
		text.setFont(new Font("Squealer Embossed", Font.PLAIN, 30 * data.size / 48));
		text.setBackground(transparent);
		text.setFocusable(false);
		text.setForeground(Color.BLACK);
	
		int [] textSize = data.getTextSize(text);
		text.setBounds(descWidth / 2 - textSize[0] / 2, data.size * 2 + data.size / 2, textSize[0], textSize[1]);

		String[] stringNb = new String[s.nb];
		for (int i = 0; i < s.nb; i++){
			stringNb[i] = "" + (i + 1);
		}

		JComboBox number = new JComboBox(stringNb);
		number.setBounds(descWidth - descWidth / 3, descHeight - descHeight / 3, descWidth / 4, descHeight / 10);
		number.setFont(new Font("Squealer Embossed", Font.PLAIN, 30 * data.size / 48));

		JButton sellButton = new JButton(price + "$");
		sellButton.setBounds(descWidth / 10, descHeight - descHeight / 3, descWidth / 2, descHeight / 10);
		sellButton.setBackground(Color.YELLOW);
		sellButton.setFocusable(false);
		sellButton.setFont(new Font("Squealer Embossed", Font.PLAIN, 30 * data.size / 48));
		sellButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				int nb = Integer.parseInt((String)number.getSelectedItem());
				inventory.deleteObj(s.obj, nb);
				inventory.money += (price * nb);
				objDescPane.removeAll();
				objDescPane.add(shopImage);
				updateShop();
			}
		});

	
     	DefaultListCellRenderer listRenderer = new DefaultListCellRenderer();
		listRenderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER); // center-aligned items
		number.setRenderer(listRenderer);
		number.setFocusable(false);
		number.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				sellButton.setText((price * Integer.parseInt((String)number.getSelectedItem())) + "$");
			}
		});

		panel.add(image);
		panel.add(text);
		panel.add(number);
		panel.add(sellButton);
		return panel;
	}

	public void updateSellPane(){
		int col = 0;
		int row = 0;
		int space = width / 25;
		int size = data.size * 2;
		int nb = inventory.getInventory().size();

		sellObjPane.removeAll();
		sellObjPane.setPreferredSize(new Dimension(width / 2, (size + space) * nb / 4));
		for (Slot s : inventory.getInventory()){
			JPanel objDesc = createObjDesc(s);

			JButton button = new JButton(new ImageIcon(new ImageIcon(s.obj.image).getImage().getScaledInstance(size, size, Image.SCALE_DEFAULT)));
			button.setBounds(size * col + space * (col + 1),
							 row * size + (row + 1) * space, 
							 size, size);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					if (objDesc.getParent() == objDescPane){
						objDescPane.removeAll();
						objDescPane.add(shopImage);
					} else if (objDescPane.getComponents() != null){
						objDescPane.removeAll();
						objDescPane.add(objDesc);
					} else {
						objDescPane.add(objDesc);
					}
				}
			});
			button.setFocusable(false);

			JTextArea text = new JTextArea("x" + s.nb);

			text.setFont(new Font("Squealer Embossed", Font.PLAIN, 30 * data.size / 48));
			text.setBackground(transparent);
			text.setFocusable(false);
			text.setForeground(Color.BLACK);

			int[] textSize = data.getTextSize(text);
			text.setBounds(size * (col + 1) + space * (col + 1), 
						   row * size + (row + 1) * space,
						   textSize[0], textSize[1]);

			sellObjPane.add(button);
			sellObjPane.add(text);

			sellObjPane.setLayer(button, 0);
			sellObjPane.setLayer(text, 1);
			col++;
			if (col >= 4){
				col = 0;
				row++;
			}
		}
	}

	public void initializeTab(){
		JLabel lab1 = new JLabel();
		lab1.setPreferredSize(new Dimension((int)(width / 2) - width / 20, height / 20));
		lab1.setText("Buy");
		mainPane.setTabComponentAt(0, lab1);

		JLabel lab2 = new JLabel();
		lab2.setPreferredSize(new Dimension((int)(width / 2) - width / 20, height / 20));
		lab2.setText("Sell");
		mainPane.setTabComponentAt(1, lab2);
	}

	public void initialiseMoneyText(){
		moneyText = new JTextArea(inventory.money + "$");
		moneyText.setBounds(width - width / 10, height / 50, moneyText.getText().length() * width / 100, height / 10);
		moneyText.setBackground(transparent);
		moneyText.setForeground(Color.BLACK);
		moneyText.setFont(new Font("Squealer Embossed", Font.PLAIN, 30 * data.size / 48));
	}

	public void updateMoney(){
		moneyText.setText(inventory.money + "$");

		int[] size = data.getTextSize(moneyText);
		moneyText.setBounds(width - width / 10 - size[0], height / 50, size[0], size[1]);
	}

	public void initialiseShopButton(){
		shopButton = new JButton("Shop");
		shopButton.setBounds(data.width / 100 + (2 * data.width) / 9, data.height / 100, data.width / 10, data.height / 15);
		shopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				drawShop();
			}
		});
		shopButton.setBackground(Color.WHITE);
		shopButton.setFocusPainted(false);
		shopButton.setVisible(true);
		showShopButton();
	}

	public void showShopButton(){
		data.panel.add(shopButton);
		data.panel.setLayer(shopButton, 1);

	}

	public void removeShopButton(){
		data.panel.remove(shopButton);
	}

	public void drawShop(){
		if (!data.menuPanel.isAncestorOf(mainPane)){
			shopOpen = false;
		}
		if (shopOpen == false){
			data.clearMenuPanel();
			updateShop();
			data.menuPanel.add(mainPane);
			data.windowOpen = true;
			data.key.move = false;
			shopOpen = true;
		}
		else {
			data.menuPanel.remove(mainPane);
			data.windowOpen = false;
			data.key.move = true;
			shopOpen = false;
		}
		data.panel.revalidate();
		data.panel.requestFocusInWindow();
	}

	public void removeShop() {
		if (shopOpen) {
			data.menuPanel.remove(mainPane);
			data.windowOpen = false;
			data.menuPanel.revalidate();
			data.menuPanel.requestFocusInWindow();
			shopOpen = false;
		}
	}

	public boolean isOpen(){
		return (data.menuPanel.isAncestorOf(mainPane));
	}

	@Override
	public void mouseClick(){
		if (data.key.editMode == false && touchClose() && timerClick.isRunning() == false){
			timerClick.restart();
			drawShop();
		}
	}
}
