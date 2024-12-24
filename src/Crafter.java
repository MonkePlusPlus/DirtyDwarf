
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

	private int maxCapacity = 100;
	private int number;

	private int panelWidth;
	private int panelHeight;
	private int toggle;

	private CrafterType type;
	private boolean first;

	private Recipe recipe;
	private LinkedList<Slot> objects;

	private JLayeredPane mainPane;
	private JLayeredPane modifyPane;
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

	Color myColor = new Color(255, 255, 255, 150);
	Color transparent = new Color(0, 0, 0, 0);

	public Crafter(Data data, Recipe recipe, Map map, Inventory inventory, CrafterType type, int x, int y, boolean collision, BufferedImage image, String symb) {
		super(data, x, y, collision, image);
		this.type = type;
		this.symb = symb;
		this.map = map;
		this.recipe = recipe;
		this.inventory = inventory;
		this.panelWidth = data.width / 2;
		this.panelHeight = data.height / 2;
		this.timerClick = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				// wait
			}
		});
		this.timerClick.setRepeats(false);
	}

	public void initialiseCrafer(){
		initialiseMainPane();
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

		nameText = new JTextArea("CRAFTER");
		nameText.setFocusable(false);
		nameText.setFont(new Font("Squealer Embossed", Font.BOLD, 50 * data.size / 48));
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
			}
		});
		
		mainPane.add(collectButton);
		mainPane.setLayer(collectButton, 1);
		mainPane.add(cancelButton);
		mainPane.setLayer(cancelButton, 1);
		mainPane.add(modifyButton);
		mainPane.setLayer(modifyButton, 1);
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

		objPane.setBounds(0, 0, panelWidth / 2, data.size * 2 * nbIngredient + space * (nbIngredient + 1));

		numberObj = new JTextArea[nbIngredient];
		addButtons = new JButton[nbIngredient];
		for (Slot s : recipe.ingredients) {
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

			numberObj[index] = new JTextArea();

			back.add(image);
			back.add(addButtons[index]);
			objPane.add(back);
			index++;
		}
	}

	public void updateAddPane(){
		for (JButton b : addButtons){
			if (true){
				b.setBorder(BorderFactory.createLineBorder(Color.GREEN, 4 * data.width / 1920));
				b.setEnabled(true);
			} else {
				b.setBorder(BorderFactory.createLineBorder(Color.RED, 4 * data.width / 1920));
				b.setEnabled(false);
			}
		}
	}


	public void addObj(Object obj, int nb){
		for (Slot s : objects) {
			if (s.obj == obj){
				s.nb += nb;
				return ;
			}	
		}
		objects.add(new Slot(obj, nb));
	}

	public void showMainPane(){
		toggle = 0;
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
		numberText.setBounds(panelWidth / 2 - 3 * data.size / 2 + data.size * 3, panelHeight / 2 - data.size * 3 / 2, tsize[0], tsize[1]);
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
