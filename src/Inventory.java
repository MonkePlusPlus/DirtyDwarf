import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Inventory extends JTabbedPane {
	
	private LinkedList<Slot> items;
	private Object[][] listItem;

	public int money;
	final private Data data;

	private JLayeredPane invPanel;
	private JLayeredPane craftPanel;

	private JScrollPane scrollInv;

	final private int textSize;

	final private int startX;
	final private int startY;
	final private int width;
	final private int height;

	private JButton invButton;
	private boolean invOpen = false;

	private JButton[] craftButton;
	private int nbRecipe;

	final private int spaceBetween;
	
	Color myColor = new Color(255, 255, 255, 127);
	Color transparent = new Color(0, 0, 0, 0);
	//Color myColor = Color.GRAY;

	public Inventory(Data data){
		super();
		this.data = data;
		this.items = new LinkedList<>();
		this.money = 0;
		this.startX = data.width / 10;
		this.startY = data.height / 10;
		this.width = (int)(data.width / 1.25);
		this.height = (int)(data.height / 1.25);
		this.spaceBetween = width / 20;
		this.textSize = height / 9;
		this.setBounds(startX, startY, width, height);
		this.setBackground(myColor);
		this.addKeyListener(data.key);
		UIManager.put("TabbedPane.contentOpaque", false);
		UIManager.put("TabbedPane.selected", Color.RED);
		UIManager.put("TabbedPane.background", Color.RED);
		SwingUtilities.updateComponentTreeUI(this);
		this.setVisible(true);
	}

	public void updateInventory(){
		updateCraftPanel();
		updateInvPanel();
	}

	public void updateInvPanel(){
		invPanel.removeAll();
		int i = 0;
		int j = 0;
	
		for (Slot s : items){
			JButton button = new JButton(new ImageIcon(new ImageIcon(s.obj.image).getImage().getScaledInstance(data.size * 2, data.size * 2, Image.SCALE_DEFAULT)));
			button.setBounds(data.size * 2 * i + spaceBetween * i, j * data.size * 2 + (j + 1) * spaceBetween, data.size * 2, data.size * 2);
			
			String t = "x" + s.nb;

			JTextArea text = new JTextArea(t);
			text.setBounds(width / 10 + data.size * 2 * (i + 1) + spaceBetween * i, textSize + j * data.size * 2 + (j + 1) * spaceBetween, t.length() * width / 10, data.size);
			text.setFont(new Font("Karmatic Arcade", Font.PLAIN, 20 * data.size / 48));
			text.setBackground(transparent);
			text.setFocusable(false);
			text.setForeground(Color.BLACK);

			invPanel.add(button);
			invPanel.add(text);
			invPanel.setLayer(button, 1);
			invPanel.setLayer(text, 2);
			i++;
			if (i > 10){
				i = 0;
				j++;
			}
		}
	}


	public void InitializeInvPanel(){
		invPanel = new JLayeredPane();
		invPanel.setBackground(transparent);
		invPanel.setOpaque(true);
		invPanel.setPreferredSize(new Dimension(width / 2, textSize + items.size() * data.size + (items.size() + 1) * spaceBetween));
		invPanel.setDoubleBuffered(true);
		invPanel.setFocusable(true);	
		invPanel.setLayout(null);
		updateInvPanel();
		invPanel.setVisible(true);
	}

	public JButton createCraftButtun(Recipe r, int i){
		JButton button = new JButton(r.time + "s");
		button.setBounds(width - width / 5, textSize + i * data.size + (i + 1) * spaceBetween, width / 8, data.size);
		button.setBackground(Color.YELLOW);
		button.setFocusable(false);
		button.setBorder(BorderFactory.createLineBorder(Color.GREEN, 4 * data.width / 1920));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				Thread thread = new Thread(new Runnable(){
					@Override
					public void run(){
						JProgressBar progressBar = new JProgressBar(0, r.time);
						progressBar.setValue(0);
						progressBar.setStringPainted(true);
						progressBar.setBounds(width - width / 5, textSize + i * data.size + (i + 1) * spaceBetween, width / 8, data.size);
						progressBar.setVisible(true);
						craftPanel.add(progressBar);
						button.setVisible(false);
						long start = System.currentTimeMillis();
						long t;
						while ((t = ((System.currentTimeMillis() - start) / 1000)) < r.time){
							progressBar.setValue((int)t);
						}
						addObj(r, 1);
						for (Slot s : r.ingredients){
							deleteObj(s.obj, s.nb);
						}
						craftPanel.remove(progressBar);
						button.setVisible(true);
						updateInventory();
					}
				});
				thread.start();
			}
		});
		return button;
	}

	public void createCraftPanel(){
		craftPanel.removeAll();
		JTextArea crafttext = new JTextArea("CRAFT");
		crafttext.setBounds(width / 3, 0, width, textSize);
		crafttext.setBackground(transparent);
		crafttext.setFont(new Font("Arial Black", Font.BOLD, 100 * data.size / 48));
		crafttext.setFocusable(false);
		nbRecipe = listItem[1].length;
		craftButton = new JButton[nbRecipe];
		for (int i = 0; i < nbRecipe; i++){
			Recipe r = (Recipe)listItem[1][i];

			JLabel lab = new JLabel(new ImageIcon(new ImageIcon(r.image).getImage().getScaledInstance(data.size, data.size, Image.SCALE_DEFAULT)));
			lab.setBounds(width / 10, textSize + i * data.size + (i + 1) * spaceBetween, data.size, data.size);

			String t = r.name + " = ";
			for (int j = 0; j < r.nbIngredient; j++){
				String textItem = r.ingredients[j].obj.name + " x " + r.ingredients[j].nb;
				t += (j == r.nbIngredient - 1) ? textItem : textItem + " + ";
			} 

			JTextArea text = new JTextArea(t);
			text.setFont(new Font("Arial Black", Font.PLAIN, 30 * data.size / 48));
			text.setBounds(width / 10 + data.size * 2, textSize + i * data.size + (i + 1) * spaceBetween, t.length() * width / 10, data.size);
			text.setBackground(transparent);
			text.setFocusable(false);
			text.setForeground(Color.WHITE);

			craftButton[i] = createCraftButtun(r, i);
			craftPanel.add(craftButton[i]);
			craftPanel.add(lab);
			craftPanel.add(text);
		}
		craftPanel.add(crafttext);
	}

	public void updateCraftPanel(){
		int i = 0;
		for (Object o : listItem[1]){
			Recipe r = (Recipe)o;
			System.out.println(r.name);
			if (!checkIngredient(r)) {
				craftButton[i].setEnabled(false);
				craftButton[i].setBorder(BorderFactory.createLineBorder(Color.RED, 4 * width / 1920));
			}
			else {
				craftButton[i].setEnabled(true);
				craftButton[i].setBorder(BorderFactory.createLineBorder(Color.GREEN, 4 * width / 1920));
			}
			i++;
		}
	}

	public boolean checkIngredient(Recipe r){
		int did = 0;
		int todo = r.ingredients.length;

		for (Slot s : r.ingredients){
			for (Slot item : items){
				if (s.obj.name.equals(item.obj.name)){
					if (s.nb <= item.nb){
						did++;
					}
				}
			}
		}
		return (todo == did);
	}

	public void InitializeCraftPanel(){
		craftPanel = new JLayeredPane();
		craftPanel.setBackground(transparent);
		craftPanel.setPreferredSize(new Dimension(width, textSize + listItem[1].length * data.size + (listItem[1].length + 1) * spaceBetween));
		craftPanel.setDoubleBuffered(true);
		craftPanel.setOpaque(true);
		craftPanel.setFocusable(true);
		craftPanel.setLayout(null);
		createCraftPanel();
		craftPanel.setVisible(true);
	}

	public void InitializeTab(){
		JLabel lab1 = new JLabel();
		lab1.setPreferredSize(new Dimension((int)(width / 2) - width / 20, height / 20));
		lab1.setText("Inventory");
		this.setTabComponentAt(0, lab1);

		JLabel lab2 = new JLabel();
		lab2.setPreferredSize(new Dimension((int)(width / 2) - width / 20, height / 20));
		lab2.setText("Craft");
		this.setTabComponentAt(1, lab2);
	}

	public void InitializeInventory(Object[][] listItems, File fileMap){
		this.listItem = listItems;
		InitializeCraftPanel();
		InitializeInvPanel();
		InitializeButton();

		JPanel tab1 = createInvTab(invPanel);
		JScrollPane tab2 = createCraftTab(craftPanel);

		this.addTab("Inventory", tab1);
		this.addTab("Craft", tab2);
		InitializeTab();
		startingInventory(fileMap);
		updateCraftPanel();
	}

	public void InitializeButton(){
		invButton = new JButton("Inventory");
		invButton.setBounds(data.width / 100 + data.width / 9, data.height / 100, data.width / 10, data.height / 15);
		invButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				drawInventory();
			}
		});
		invButton.setBackground(Color.WHITE);
		invButton.setFocusPainted(false);
		invButton.setVisible(true);
	}

	public boolean startingInventory(File fileMap){
		Scanner scan;
		String line;
		String[] tab;

		try {
			scan = new Scanner(fileMap);
			line = scan.nextLine();
			while (scan.hasNextLine() && line.equals("ENDOBJ") == false){
				line = scan.nextLine();
			}
			line = scan.nextLine();
			tab = line.split(":");
			fillInventory(tab[1]);
			scan.close();
		} catch (FileNotFoundException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void fillInventory(String startInv){
		String[] items = startInv.split("_");

		for (String s : items){
			String[] i = s.split("-");
			addObj(getItem(i[1], listItem), Integer.valueOf(i[0]));
		}
	}

	public void showInvButton(){
		data.panel.add(invButton);
	}

	public void removeInvButton(){
		data.panel.remove(invButton);
	}

	public void removeInventory(){
		if (invOpen == true){
			data.panel.remove(this);
			invOpen = false;
		}
	}

	public void drawInventory(){
		if (invOpen == false){
			updateInventory();
			data.panel.add(this);
			invOpen = true;
		}
		else {
			data.panel.remove(this);
			invOpen = false;
		}
		data.panel.revalidate();
		data.panel.requestFocusInWindow();
	}

	public void addObj(Item obj, int nb){
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).obj.equals(obj)){
				items.get(i).nb += nb;
				return ;
			}
		}
		items.add(new Slot(obj, nb));
	}

	public boolean deleteObj(Item obj, int nb){
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).obj.equals(obj)){
				if (items.get(i).nb >= nb){
					items.get(i).nb -= nb;
					if (items.get(i).nb == 0){
						items.remove(i);
					}
				} else {
					return false;
				}
				return true;
			}
		}
		return false;
	}

	public void	addMoney(int amount){
		money += amount;
	}

	public boolean removeMoney(int amount){
		if (amount > money){
			return false;
		}
		money -= amount;
		return true;
	}

	public JScrollPane createCraftTab(JLayeredPane pane){
		JScrollPane tab = new JScrollPane(pane);
		JScrollBar bar = new JScrollBar();

		tab.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		tab.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		tab.setBackground(myColor);
		tab.setPreferredSize(new Dimension(width, height));

		bar.setBounds(0, height / 80, width / 30, height - height / 10);
		bar.setBackground(Color.GRAY);

		tab.setVerticalScrollBar(bar);
		return tab;
	}

	public JPanel createInvTab(JLayeredPane pane){
		JTextArea invtext = new JTextArea("INVENTAIRE");
		invtext.setBounds(width / 4, 0, width, textSize);
		invtext.setBackground(transparent);
		invtext.setFont(new Font("Arial Black", Font.BOLD, 100 * data.size / 48));
		invtext.setFocusable(false);


		JPanel pan = new JPanel();
		JScrollPane tab = new JScrollPane(pane);
		JScrollBar bar = new JScrollBar();

		tab.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		tab.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		tab.setBackground(myColor);
		tab.setBounds(width / 20, textSize + height / 20, width / 2, height - textSize * 3);
		tab.setLayout(null);

		bar.setBounds(0, 0, width / 40, height - textSize * 3);
		bar.setBackground(Color.GRAY);

		tab.setVerticalScrollBar(bar);

		pan.setLayout(null);
		pan.setBackground(myColor);
		scrollInv = tab;
		pan.add(tab);
		pan.add(invtext);

		return pan;
	}

	public Item getItem(String name, Object[][] items){
		for (int n = 0; n < 2; n++){
			for (Object o : items[n]){
				Item i = (Item)o;
				if (i.name.equals(name)){
					return i;
				}
			}
		}
		return null;
	}
}
