import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;

public class Crafter extends Block {
	
	public enum CrafterType {
		POLYVALENT,
		NORMAL
	}

	private Map map;
	private Inventory inventory;

	private Timer timerClick;
	private Timer timerObj;

	private int maxCapacity = 100;
	private int number;
	private int count;

	private int panelWidth;
	private int panelHeight;
	private int modifWidth;
	private int modifHeight;

	private int toggle;

	private CrafterType type;
	private boolean first;

	private Recipe recipe;
	private Slot[] objects;

	private JLayeredPane mainPane;
	private JLayeredPane modifyPane;
	private JPanel backModiPane;
	private JPanel backPane;
	private JScrollPane scrollObj;
	private JPanel objPane;

	private JButton collectButton;
	private JButton cancelButton;
	private JButton modifyButton;
	private JButton confirmButton;
	private JButton backButton;
	private JButton[] addButtons;

	private JTextArea[] numberObj;
	private JComboBox recipeBox;
	private JProgressBar progressBar;
	private JTextArea numberText;
	private JTextArea nameText;
	private JTextArea modText;

	Color myColor = new Color(255, 255, 255, 150);
	Color transparent = new Color(0, 0, 0, 0);

	public Crafter(Data data, Recipe recipe, Map map, Inventory inventory, CrafterType type, int x, int y, boolean collision, BufferedImage image, String symb) {
		super(data, x, y, symb, collision, image);
		this.type = type;
		this.map = map;
		this.recipe = recipe;
		this.inventory = inventory;
		this.panelWidth = data.width / 2;
		this.panelHeight = data.height / 2;
		this.modifWidth = data.width / 5;
		this.modifHeight = data.height / 3;
		this.timerClick = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				// wait
			}
		});
		this.first = (recipe == null);
		this.timerClick.setRepeats(false);
	}

	public void initialiseCrafer(){
		initialiseMainPane();
		initialiseModify();
	}

	public void initialiseMainPane(){
		mainPane = new JLayeredPane();
		mainPane.setBackground(transparent);
		mainPane.setBounds(data.width / 2 - panelWidth / 2, data.height / 2 - panelHeight / 2, panelWidth, panelHeight);
		mainPane.addKeyListener(data.key);
		mainPane.setFocusable(false);
		mainPane.setLayout(null);
		mainPane.setVisible(true);

		backPane = new JPanel();
		backPane.setBackground(myColor);
		backPane.setBounds(0, 0, panelWidth, panelHeight);
		backPane.setFocusable(false);
		backPane.setLayout(null);
		backPane.setVisible(true);

		nameText = new JTextArea("CRAFTER" + ((type == CrafterType.POLYVALENT) ? " : POlYVALENT": " : NORMAL"));
		nameText.setFocusable(false);
		nameText.setFont(new Font("Squealer Embossed", Font.BOLD, 30 * data.size / 48));
		nameText.setBackground(transparent);

		int[] tsize = data.getTextSize(nameText);
		nameText.setBounds(panelWidth - panelWidth / 3, tsize[1], tsize[0], tsize[1]);

		numberText = new JTextArea("0");
		numberText.setFocusable(false);
		numberText.setFont(new Font("Squealer Embossed", Font.BOLD, 50 * data.size / 48));
		numberText.setBackground(transparent);

		tsize = data.getTextSize(numberText);
		numberText.setBounds(panelWidth - panelWidth / 5 + panelWidth / 20, panelHeight / 2 - data.size * 4 / 2, tsize[0], tsize[1]);

		mainPane.add(backPane);
		mainPane.setLayer(backPane, 0);
		mainPane.add(numberText);
		mainPane.setLayer(numberText, 1);
		mainPane.add(nameText);
		mainPane.setLayer(nameText, 1);
		timerObj = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addRecipe();
			}
		});
		timerObj.setRepeats(true);
		timerObj.start();
		initialiseButton();
		initialiseAddButton();
	}

	public void initialiseButton(){
		collectButton = data.createButton("",
						new Rectangle(panelWidth - data.size * 4 - panelWidth / 5, panelHeight / 2 - data.size * 4 / 2, data.size * 4, data.size * 4),
				30, Font.BOLD, Color.WHITE);
		if (recipe != null){
			collectButton.setIcon(new ImageIcon(new ImageIcon(recipe.image).getImage().getScaledInstance(data.size * 3, data.size * 3, Image.SCALE_DEFAULT)));
		}

		collectButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (recipe != null){
					inventory.addObj(recipe, number);
					number = 0;
					count = 0;
					updateNumber();
				}
			}
			
		});

		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setForeground(Color.GREEN);
		progressBar.setString("");
		progressBar.setBounds(panelWidth - data.size * 4 - panelWidth / 5, panelHeight / 2 - data.size * 4 / 2 - panelHeight / 20, data.size * 4, panelHeight / 20);
		progressBar.setMinimum(0);

		cancelButton = data.createButton("CANCEL", 
					   new Rectangle(panelWidth - 2 * panelWidth / 6 - panelWidth / 10, panelHeight - panelHeight / 6, panelWidth / 6, panelHeight / 6),
			  30, Font.BOLD, Color.YELLOW);
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeCrafter();
			}
		});

		modifyButton = data.createButton("MODIFY",
						new Rectangle(panelWidth - panelWidth / 6 - panelWidth / 20, panelHeight - panelHeight / 6, panelWidth / 6, panelHeight / 6),
						30, Font.BOLD, Color.YELLOW);
		modifyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeMainPane();
				showModifyPane();
				toggle = 1;
			}
		});
		if (type == CrafterType.NORMAL){
			modifyButton.setEnabled(false);
		}
		
		mainPane.add(collectButton);
		mainPane.setLayer(collectButton, 1);
		mainPane.add(cancelButton);
		mainPane.setLayer(cancelButton, 1);
		mainPane.add(modifyButton);
		mainPane.setLayer(modifyButton, 1);
		mainPane.add(progressBar);
		mainPane.setLayer(progressBar, 1);
	}

	public void initialiseAddButton(){
		objPane = new JPanel();
		objPane.setLayout(null);
		if (recipe != null){
			updateObjPane();
		}
		scrollObj = new JScrollPane(objPane);
		scrollObj.setBounds(panelWidth / 20, panelHeight / 20, panelWidth / 2, panelHeight - panelHeight / 10);
		scrollObj.setFocusable(false);
		scrollObj.setBackground(myColor);
		scrollObj.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollObj.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		mainPane.add(scrollObj);
		mainPane.setLayer(scrollObj, 1);
	}

	public void updateObjPane(){
		int space = panelHeight / 20;
		int nbIngredient = recipe.ingredients.length;
		int index = 0;

		System.out.println("nb : " + nbIngredient);
		objPane.setBounds(0, 0, panelWidth / 2, data.size * 2 * nbIngredient + space * (nbIngredient + 1));
		progressBar.setMaximum(recipe.time);
		collectButton.setIcon(new ImageIcon(new ImageIcon(recipe.image).getImage().getScaledInstance(data.size * 3, data.size * 3, Image.SCALE_DEFAULT)));

		objects = new Slot[nbIngredient];
		numberObj = new JTextArea[nbIngredient];
		addButtons = new JButton[nbIngredient];
		for (Slot s : recipe.ingredients) {
			objects[index] = new Slot(s.obj, 0);

			JPanel back = new JPanel();
			back.setFocusable(false);
			back.setBackground(Color.WHITE);
			back.setBounds(panelWidth / 30, (index + 1) * space + data.size * 2 * index,
						   panelWidth / 2 - panelWidth / 15, data.size * 2 + panelHeight / 40);
			back.setLayout(null);

			JLabel image = new JLabel(new ImageIcon(new ImageIcon(s.obj.image).getImage().getScaledInstance(data.size * 2, data.size * 2, Image.SCALE_DEFAULT)));
			image.setFocusable(false);
			image.setBackground(Color.WHITE);
			image.setBounds(panelWidth / 20, panelHeight / 40, data.size * 2, data.size * 2);

			addButtons[index] = new JButton("add");
			addButtons[index].setBackground(Color.YELLOW);
			addButtons[index].setBounds(panelWidth / 2 - panelWidth / 5, (data.size * 2 + panelHeight / 40) / 2 - panelHeight / 20, panelWidth / 10, panelHeight / 10);
			addButtons[index].setFocusable(false);
			final int iObj = index;
			System.out.println("index obj : " + iObj);
			addButtons[index].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (inventory.checkObj(objects[iObj].obj, 1)){
						addObj(objects[iObj].obj, 1);
						updateAddPane();
					}
				}
			});
			numberObj[index] = new JTextArea("0 / " + s.nb);
			numberObj[index].setFocusable(false);
			numberObj[index].setFont(new Font("Squealer Embossed", Font.BOLD, 30 * data.size / 48));
			numberObj[index].setBackground(transparent);
			numberObj[index].setForeground(Color.RED);

			int[] tsize = data.getTextSize(numberObj[index]);
			numberObj[index].setBounds(data.size * 2 + panelWidth / 6 - tsize[0], panelHeight / 20 + tsize[1] / 2, tsize[0], tsize[1]);

			back.add(image);
			back.add(addButtons[index]);
			back.add(numberObj[index]);
			objPane.add(back);
			index++;
		}
		updateAddPane();
	}

	public void updateAddPane(){
		int i = 0;
		for (JButton b : addButtons){
			if (inventory.checkObj(objects[i].obj, 1)){
				b.setBorder(BorderFactory.createLineBorder(Color.GREEN, 4 * data.size / 48));
				b.setEnabled(true);
			} else {
				b.setBorder(BorderFactory.createLineBorder(Color.RED, 4 * data.size / 48));
				b.setEnabled(false);
			}

			if (objects[i].nb >= recipe.ingredients[i].nb){
				numberObj[i].setForeground(Color.GREEN);
			} else {
				numberObj[i].setForeground(Color.RED);
			}
			numberObj[i].setText(objects[i].nb + " / " + recipe.ingredients[i].nb);

			int[] tsize = data.getTextSize(numberObj[i]);
			numberObj[i].setBounds(data.size * 2 + panelWidth / 6 - tsize[0], panelHeight / 20 + tsize[1] / 2, tsize[0], tsize[1]);
			i++;
		}
	}

	public void initialiseModify(){
		modifyPane = new JLayeredPane();
		modifyPane.setBackground(transparent);
		modifyPane.setBounds(data.width / 2 - modifWidth / 2, data.height / 2 - modifHeight / 2, modifWidth, modifHeight);
		modifyPane.addKeyListener(data.key);
		modifyPane.setFocusable(false);
		modifyPane.setLayout(null);
		modifyPane.setVisible(true);

		backModiPane = new JPanel();
		backModiPane.setBackground(myColor);
		backModiPane.setBounds(0, 0, modifWidth, modifHeight);
		backModiPane.setFocusable(false);
		backModiPane.setLayout(null);
		backModiPane.setVisible(true);

		modifyPane.add(backModiPane);
		modifyPane.setLayer(backModiPane, 0);

		initialiseModifButton();
	}

	public void initialiseModifButton(){
		modText = new JTextArea("MODIFY");
		modText.setBackground(transparent);
		modText.setFont(new Font("Squealer Embossed", Font.BOLD, 30 * data.size / 48));
		modText.setFocusable(false);

		int[] tsize = data.getTextSize(modText);
		modText.setBounds(modifWidth / 2 - tsize[0] / 2, 0, tsize[0], tsize[1]);

		backButton = data.createButton("BACK", 
					new Rectangle(modifWidth / 2 - (modifWidth / 2 - modifWidth / 6) - modifWidth / 10,
					4 * modifHeight / 5, modifWidth / 2 - modifWidth / 6, modifHeight / 5), 30, Font.BOLD, Color.YELLOW);
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeModifyPane();
				showMainPane();
				toggle = 0;
			}
		});
		backButton.setEnabled(!first);
		LinkedList<Object> recipes = map.getRecipes();
		ImageIcon[] images = new ImageIcon[recipes.size()];
		int i = 0;
		for (Object rec : recipes) {
			images[i] = new ImageIcon(new ImageIcon(rec.image).getImage().getScaledInstance(data.size * 3, data.size * 3, Image.SCALE_DEFAULT));
			i++;
		}
		recipeBox = new JComboBox(images);
		recipeBox.setFocusable(false);
		recipeBox.setBounds(modifWidth / 2 - data.size * 3 / 2, modifHeight / 2 - data.size * 3 / 2, data.size * 3, data.size * 3);
		
		confirmButton = data.createButton("YES",
						new Rectangle(modifWidth / 2 + modifWidth / 10, 4 * modifHeight / 5, modifWidth / 2 - modifWidth / 6, modifHeight / 5),
			   30, Font.BOLD, Color.YELLOW);
		confirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = recipeBox.getSelectedIndex();
				Recipe rec = (Recipe)recipes.get(index);
				if (recipe != rec) {
					if (recipe != null) {
						for (Slot s : objects){
							inventory.addObj(s.obj, s.nb);
						}
						inventory.addObj(recipe, number);
					}
					number = 0;
					count = 0;
					recipe = rec;
					progressBar.setValue(0);
					objPane.removeAll();
					updateObjPane();
					updateNumber();
				}
				removeModifyPane();
				showMainPane();
				toggle = 0;
				first = false;
				backButton.setEnabled(true);
			}		
		});

		modifyPane.add(modText);
		modifyPane.setLayer(modText, 1);
		modifyPane.add(backButton);
		modifyPane.setLayer(backButton, 1);
		modifyPane.add(confirmButton);
		modifyPane.setLayer(confirmButton, 1);
		modifyPane.add(recipeBox);
		modifyPane.setLayer(recipeBox, 1);
	}

	public boolean canCreate(){
		for (Slot slot1 : recipe.ingredients) {
			for (Slot slot2 : objects){
				if (slot1.obj == slot2.obj){
					if (slot1.nb > slot2.nb){
						return false;
					}
				}
			}
		}
		return true;
	}

	public void addRecipe(){
		if (recipe != null && canCreate()) {
			count += bonus;
			if (count >= recipe.time) {
				if (number < maxCapacity){
					number++;
					updateNumber();
					updateObjNumber();
					updateAddPane();
					count = 0;
				}
			}
			progressBar.setValue(count);
		}
	}

	public void addObj(Object obj, int nb){
		for (Slot s : objects) {
			if (s.obj == obj){
				s.nb += nb;
				inventory.deleteObj(obj, nb);
				return ;
			}	
		}
	}

	public void showMainPane(){
		toggle = 0;
		updateAddPane();
		data.menuPanel.add(mainPane);
	}

	public void removeMainPane(){
		data.menuPanel.remove(mainPane);
	}

	public void showModifyPane(){
		toggle = 1;
		data.menuPanel.add(modifyPane);
	}

	public void removeModifyPane(){
		data.menuPanel.remove(modifyPane);
	}

	public void showCrafter(){
		data.windowOpen = true;
		data.key.move = false;
		if (first){
			showModifyPane();
		} else {
			showMainPane();
		}
		data.panel.revalidate();
		data.panel.requestFocusInWindow();
	}

	public void removeCrafter(){
		if (toggle == 0){
			removeMainPane();
		} else {
			removeModifyPane();
		}
		data.windowOpen = false;
		data.key.move = true;
		data.panel.revalidate();
		data.panel.requestFocusInWindow();
	}

	public void updateNumber(){
		numberText.setText(number + "");

		int[] tsize = data.getTextSize(numberText);
		numberText.setBounds(panelWidth - panelWidth / 5 + panelWidth / 20, panelHeight / 2 - data.size * 4 / 2, tsize[0], tsize[1]);
	}

	public void updateObjNumber(){
		int i = 0;
		for (Slot s : recipe.ingredients){
			objects[i].nb -= s.nb;
			i++;
		}
		updateAddPane();
	}

	@Override
	public Tile.TileType getType(){
		return Tile.TileType.CRAFTER;
	}

	@Override
	public void mouseClick(){
		if (data.key.editMode == false && touchClose() && timerClick.isRunning() == false){
			timerClick.restart();
			showCrafter();
		}
	}
}
