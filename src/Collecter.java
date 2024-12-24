import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.Timer;

public class Collecter extends Block {
	
	private Map map;
	private Inventory inventory;

	private int maxCapacity = 100;
	private Object object;
	private int number;
	private JProgressBar progressBar;

	private int panelWidth;
	private int panelHeight;

	private Timer timerClick;
	private Timer timerObj;
	private int count;
	private int toggle;

	private JLayeredPane mainPanel;
	private JPanel backPanel;
	private JButton collectButton;
	private JButton cancelButton;

	private JTextArea nameText;
	private JTextArea numberObj;
	private JButton modifyButton;

	private JTextArea modText;
	private JComboBox objBox;
	private JButton confirmButton;
	private JButton backButton;
	private JTextArea noneText;

	private BufferedImage baseImage;

	private boolean first = true;

	Color myColor = new Color(255, 255, 255, 150);
	Color transparent = new Color(0, 0, 0, 0);

	public Collecter(Object object, Data data, Map map, Inventory inventory, int x, int y, boolean collision, BufferedImage image, int number, String symb){
		super(data, x, y, collision, image);
		this.object = object;
		this.map = map;
		this.inventory = inventory;
		this.panelWidth = data.width / 5;
		this.panelHeight = data.height / 3;
		this.baseImage = image;
		this.number = number;
		this.count = 0;
		this.symb = symb;
		this.timerClick = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				// wait
			}
		});
		if (object != null){
			first = false;
		}
		timerClick.setRepeats(false);
	}

	public void initialiseCollecter() {
		mainPanel = new JLayeredPane();
		mainPanel.setBackground(transparent);
		mainPanel.setBounds(data.width / 2 - panelWidth / 2, data.height / 2 - panelHeight / 2, panelWidth, panelHeight);
		mainPanel.addKeyListener(data.key);
		mainPanel.setFocusable(false);
		mainPanel.setLayout(null);
		mainPanel.setVisible(true);

		backPanel = new JPanel();
		backPanel.setBackground(myColor);
		backPanel.setBounds(0, 0, panelWidth, panelHeight);
		backPanel.setFocusable(false);
		backPanel.setLayout(null);
		backPanel.setVisible(true);

		nameText = new JTextArea("COLLECTER");
		nameText.setBackground(transparent);
		nameText.setFont(new Font("Squealer Embossed", Font.BOLD, 30 * data.size / 48));
		nameText.setFocusable(false);

		int[] tsize = data.getTextSize(nameText);
		nameText.setBounds(panelWidth / 2 - tsize[0] / 2, 0, tsize[0], tsize[1]);

		mainPanel.add(backPanel);
		mainPanel.setLayer(backPanel, 0);
		timerObj = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (object != null) {
					count++;
					if (count >= object.time) {
						if (number < maxCapacity){
							number++;
							updateNumber();
							count = 0;
						}
					}
					progressBar.setValue(count);
				}
			}
		});
		timerObj.setRepeats(true);
		timerObj.start();
		initialiseButton();
		initialiseModify();
		if (object != null){
			updateCollecter(map.nextToRessource(x, y));
		}

	}

	public void initialiseButton(){
		cancelButton = new JButton("CANCEL");
		cancelButton.setFocusable(false);
		cancelButton.setBackground(Color.YELLOW);
		cancelButton.setFont(new Font("Squealer Embossed", Font.BOLD, 30 * data.size / 48));
		cancelButton.setBounds(panelWidth / 2 - (panelWidth / 2 - panelWidth / 6) - panelWidth / 10,
							4 * panelHeight / 5, panelWidth / 2 - panelWidth / 6, panelHeight / 5);

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeCollecter();
			}
		});

		modifyButton = data.createButton("MODIFY", 
					new Rectangle(panelWidth / 2 + panelWidth / 10, 4 * panelHeight / 5, panelWidth / 2 - panelWidth / 6, panelHeight / 5),
					30, Font.BOLD, Color.YELLOW);

		modifyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeCollectPane();
				showModifyPane();
				toggle = 1;
			}
		});
	
		noneText = new JTextArea("NONE");
		noneText.setFocusable(false);

		numberObj = new JTextArea("0");
		numberObj.setFocusable(false);
		numberObj.setFont(new Font("Squealer Embossed", Font.BOLD, 30 * data.size / 48));
		numberObj.setBackground(transparent);

		int[] tsize = data.getTextSize(numberObj);
		numberObj.setBounds(panelWidth / 2 - 3 * data.size / 2 + data.size * 3 + tsize[0], panelHeight / 2 - data.size * 3 / 2, tsize[0], tsize[1]);

		collectButton = data.createButton("", new Rectangle(panelWidth / 2 - data.size * 3 / 2, panelHeight / 2 - data.size * 3 / 2, data.size * 3, data.size * 3),
						30, Font.BOLD, Color.WHITE);
		collectButton.add(noneText);
		if (object != null){
			collectButton.setIcon(new ImageIcon(new ImageIcon(object.image).getImage().getScaledInstance(data.size * 3, data.size * 3, Image.SCALE_DEFAULT)));
		}

		collectButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				collectObject();
			}
		});

		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setForeground(Color.GREEN);
		progressBar.setString("");
		progressBar.setBounds(panelWidth / 2 - data.size * 3 / 2, data.size * 2, data.size * 3, data.size / 2);
		progressBar.setMinimum(0);
	}

	public void initialiseModify(){
		modText = new JTextArea("MODIFY");
		modText.setBackground(transparent);
		modText.setFont(new Font("Squealer Embossed", Font.BOLD, 30 * data.size / 48));
		modText.setFocusable(false);

		int[] tsize = data.getTextSize(modText);
		modText.setBounds(panelWidth / 2 - tsize[0] / 2, 0, tsize[0], tsize[1]);

		backButton = data.createButton("BACK", 
					new Rectangle(panelWidth / 2 - (panelWidth / 2 - panelWidth / 6) - panelWidth / 10,
					4 * panelHeight / 5, panelWidth / 2 - panelWidth / 6, panelHeight / 5), 30, Font.BOLD, Color.YELLOW);
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeModifyPane();
				showCollectPane();
				toggle = 0;
			}
		});
		LinkedList<Slot> objects = map.getNextObject(x, y);
		ImageIcon[] image = new ImageIcon[objects.size()];
		int i = 0;
		for (Slot s : objects) {
			Object obj = s.obj;
			image[i] = new ImageIcon(new ImageIcon(obj.image).getImage().getScaledInstance(data.size * 3, data.size * 3, Image.SCALE_DEFAULT));
			i++;
		}
		objBox = new JComboBox(image);
		objBox.setFocusable(false);
		objBox.setBounds(panelWidth / 2 - data.size * 3 / 2, panelHeight / 2 - data.size * 3 / 2, data.size * 3, data.size * 3);
		
		confirmButton = data.createButton("YES",
						new Rectangle(panelWidth / 2 + panelWidth / 10, 4 * panelHeight / 5, panelWidth / 2 - panelWidth / 6, panelHeight / 5),
			   30, Font.BOLD, Color.YELLOW);
		confirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = objBox.getSelectedIndex();
				Slot s = objects.get(index);
				if (object != s.obj){
					if (object != null){
						collectObject();
					}
					object = s.obj;
					updateCollecter(s.nb);
				}
				removeModifyPane();
				showCollectPane();
			}		
		});
	}

	public void updateNumber(){
		numberObj.setText(number + "");

		int[] tsize = data.getTextSize(numberObj);
		numberObj.setBounds(panelWidth / 2 - 3 * data.size / 2 + data.size * 3, panelHeight / 2 - data.size * 3 / 2, tsize[0], tsize[1]);
	}

	public void updateCollecter(int pos) {
		switch (pos) {
			case 1 -> image = data.rotateImage(baseImage, -90);
			case 2 -> image = data.rotateImage(baseImage, 90);
			case 3 -> image = data.flipImage(baseImage);
			
			default -> {
				image = baseImage; break;
			}
		}
		collectButton.setIcon(new ImageIcon(new ImageIcon(object.image).getImage().getScaledInstance(data.size * 3, data.size * 3, Image.SCALE_DEFAULT)));
		number = 0;
		count = 0;
		progressBar.setValue(0);
		progressBar.setMaximum(object.time);
		if (collectButton.isAncestorOf(noneText)){
			collectButton.remove(noneText);
		}
	}

	public void collectObject(){
		inventory.addObj(object, number);
		number = 0;
		updateNumber();
	}

	public void removeModifyPane(){
		mainPanel.remove(backButton);
		mainPanel.remove(confirmButton);
		mainPanel.remove(objBox);
		mainPanel.remove(modText);
	}

	public void showModifyPane(){
		mainPanel.add(modText);
		mainPanel.setLayer(modText, 1);
		mainPanel.add(backButton);
		mainPanel.setLayer(backButton, 1);
		mainPanel.add(confirmButton);
		mainPanel.setLayer(confirmButton, 1);
		mainPanel.add(objBox);
		mainPanel.setLayer(objBox, 2);
	}

	public void removeCollectPane(){
		mainPanel.remove(collectButton);
		mainPanel.remove(modifyButton);
		mainPanel.remove(cancelButton);
		mainPanel.remove(nameText);
		mainPanel.remove(progressBar);
		mainPanel.remove(numberObj);
	}

	public void showCollectPane(){
		mainPanel.add(nameText);
		mainPanel.setLayer(nameText, 1);
		mainPanel.add(collectButton);
		mainPanel.setLayer(collectButton, 1);
		mainPanel.add(modifyButton);
		mainPanel.setLayer(modifyButton, 1);
		mainPanel.add(cancelButton);
		mainPanel.setLayer(cancelButton, 1); 
		mainPanel.add(numberObj);
		mainPanel.setLayer(numberObj, 2);
		mainPanel.add(progressBar);
		mainPanel.setLayer(progressBar, 1);
	}

	public void showCollecter() {
		data.menuPanel.add(mainPanel);
		data.windowOpen = true;
		data.key.move = false;
		if (first){
			showModifyPane();
			first = false;
		} else {
			showCollectPane();
		}
		data.panel.revalidate();
		data.panel.requestFocusInWindow();
	}

	public void removeCollecter() {
		data.menuPanel.remove(mainPanel);
		if (toggle == 0){
			removeCollectPane();
		} else {
			removeModifyPane();
		}
		data.windowOpen = false;
		data.key.move = true;
		data.panel.revalidate();
		data.panel.requestFocusInWindow();
	}

	@Override
	public Tile.TileType getType(){
		return TileType.COLLECTER;
	}

	@Override
	public void mouseClick(){
		if (data.key.editMode == false && touchClose() && timerClick.isRunning() == false){
			timerClick.restart();
			showCollecter();
		}
	}
}
