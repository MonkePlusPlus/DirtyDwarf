import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneLayout;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

class Slot {
	public Item obj;
	public int nb;

	public Slot(Item obj, int nb){
		this.obj = obj;
		this.nb = nb;
	}
}

public class Inventory extends JTabbedPane {
	
	private LinkedList<Slot> items;
	private Item[][] listItem;

	public int money;
	private Data data;

	private JPanel invPanel;
	private JPanel craftPanel;

	private int textSize;

	private int startX;
	private int startY;
	private int width;
	private int height;

	private JButton invButton;
	private boolean invOpen = false;

	private JButton[] craftButton;
	private int nbRecipe;

	private int spaceBetween;
	
	Color myColor = new Color(255, 255, 255, 127);
	Color transparent = new Color(255, 255, 255, 0);
	//Color myColor = Color.GRAY;

	public Inventory(Data data){
		this.data = data;
		this.items = new LinkedList<>();
		this.money = 0;
		this.startX = data.width / 10;
		this.startY = data.height / 10;
		this.width = (int)(data.width / 1.25);
		this.height = (int)(data.height / 1.25);
		this.spaceBetween = height / 50;
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
		JTextArea invtext = new JTextArea("INVENTAIRE");
		invtext.setBounds(width / 4, 0, width, textSize);
		invtext.setBackground(transparent);
		invtext.setFont(new Font("Arial Black", Font.BOLD, 100));
		invtext.setFocusable(false);

		for (Slot s : items){
			JLabel lab = new JLabel(new ImageIcon(s.obj.image));
			lab.setBounds(width / 10, textSize + i * data.size + (i + 1) * spaceBetween, data.size, data.size);

			String t = s.obj.name + " x " + s.nb;

			JTextArea text = new JTextArea(t);
			text.setBounds(width / 10 + data.size * 2, textSize + i * data.size + (i + 1) * spaceBetween, t.length() * width / 10, data.size);
			text.setFont(new Font("Arial Black", Font.PLAIN, 30));
			text.setBackground(transparent);
			text.setFocusable(false);
			text.setForeground(Color.WHITE);

			invPanel.add(lab);
			invPanel.add(text);
			i++;
		}
		invPanel.add(invtext);
	}


	public void InitializeInvPanel(){
		invPanel = new JPanel();
		invPanel.setBackground(transparent);
		invPanel.setPreferredSize(new Dimension(width, textSize + items.size() * data.size + (items.size() + 1) * spaceBetween));
		invPanel.setDoubleBuffered(true);
		invPanel.setFocusable(true);	
		invPanel.setLayout(null);	
		updateInvPanel();
		invPanel.setVisible(true);
	}

	public JButton createCraftButtun(Recipe r, int i){
		JButton button = new JButton("CRAFT");
		button.setBounds(width - width / 5, textSize + i * data.size + (i + 1) * spaceBetween, width / 8, data.size);
		button.setBackground(Color.YELLOW);
		button.setFocusable(false);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				Thread thread = new Thread(new Runnable(){
					@Override
					public void run(){
						JProgressBar progressBar = new JProgressBar(0, r.time_to_make);
						progressBar.setValue(0);
						progressBar.setStringPainted(true);
						progressBar.setBounds(width - width / 5, textSize + i * data.size + (i + 1) * spaceBetween, width / 8, data.size);
						progressBar.setVisible(true);
						craftPanel.add(progressBar);
						button.setVisible(false);
						long start = System.currentTimeMillis();
						long t;
						while ((t = ((System.currentTimeMillis() - start) / 1000)) < r.time_to_make){
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
		crafttext.setFont(new Font("Arial Black", Font.BOLD, 100));
		crafttext.setFocusable(false);
		nbRecipe = listItem[1].length;
		craftButton = new JButton[nbRecipe];
		for (int i = 0; i < nbRecipe; i++){
			Recipe r = (Recipe)listItem[1][i];

			JLabel lab = new JLabel(new ImageIcon(r.image));
			lab.setBounds(width / 10, textSize + i * data.size + (i + 1) * spaceBetween, data.size, data.size);

			String t = r.name + " = ";
			for (int j = 0; j < r.nbIngredient; j++){
				String textItem = r.ingredients[j].obj.name + " x " + r.ingredients[j].nb;
				t += (j == r.nbIngredient - 1) ? textItem : textItem + " + ";
			} 

			JTextArea text = new JTextArea(t);
			text.setFont(new Font("Arial Black", Font.PLAIN, 30));
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
		for (Item item : listItem[1]){
			Recipe r = (Recipe)item;
			System.out.println(r.name);
			craftButton[i].setEnabled(checkIngredient(r));
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
		craftPanel = new JPanel();
		craftPanel.setBackground(transparent);
		craftPanel.setPreferredSize(new Dimension(width, textSize + listItem[1].length * data.size + (listItem[1].length + 1) * spaceBetween));
		craftPanel.setDoubleBuffered(true);
		craftPanel.setFocusable(true);
		craftPanel.setLayout(null);
		createCraftPanel();
		craftPanel.setVisible(true);
	}

	public void InitializeTab(){
		JLabel lab1 = new JLabel();
		lab1.setPreferredSize(new Dimension((int)(width / 2.1), height / 20));
		lab1.setText("Inventory");
		this.setTabComponentAt(0, lab1);

		JLabel lab2 = new JLabel();
		lab2.setPreferredSize(new Dimension((int)(width / 2.1), height / 20));
		lab2.setText("Craft");
		this.setTabComponentAt(1, lab2);
	}

	public void InitializeInventory(Item[][] listItems, File fileMap){
		this.listItem = listItems;
		InitializeCraftPanel();
		InitializeInvPanel();
		InitializeButton();

		JScrollPane tab1 = createTab(invPanel);
		JScrollPane tab2 = createTab(craftPanel);

		this.addTab("Inventory", tab1);
		this.addTab("Craft", tab2);
		InitializeTab();
		startingInventory(fileMap);
		updateCraftPanel();
	}

	public void InitializeButton(){
		invButton = new JButton("Inventory");
		invButton.setBounds(data.width / 100, data.height / 100 , data.width / 10, data.height / 15);
		invButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				drawInventory();
			}
		});
		invButton.setBackground(Color.WHITE);
		invButton.setFocusPainted(false);
		data.panel.add(invButton);
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

	public JScrollPane createTab(JPanel pane){
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

	public Item getItem(String name, Item[][] items){
		for (int n = 0; n < 2; n++){
			for (Item i : items[n]){
				if (i.name.equals(name)){
					return i;
				}
			}
		}
		return null;
	}
}
