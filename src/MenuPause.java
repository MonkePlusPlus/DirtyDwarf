
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class MenuPause extends JPanel {
	
	private Data data;
	private Map map;

	private JButton pausButton;
	private JButton contButton;
	private JButton optiButton;
	private JButton sauvButton;
	private JButton quitButton;

	private Menu menu;

	private JTextArea title;
	Color myColor = new Color(0, 0, 0, 100);

	public MenuPause(Data data, Map map){
		super();
		this.data = data;
		this.map = map;
		this.setBackground(myColor);
		this.setOpaque(false);
		this.setBounds(0, 0, data.width, data.height);
		this.addKeyListener(data.key);
		this.setLayout(null);
		this.setVisible(true);
	}

	public void showPauseButton(){
		pausButton = new JButton("PAUSE");
		pausButton.setBounds(data.width / 100, data.height / 100 , data.width / 10, data.height / 15);
		pausButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				displayMenuPause();
			}
		});
		pausButton.setBackground(Color.WHITE);
		pausButton.setFocusPainted(false);
		pausButton.setVisible(true);

		data.panel.add(pausButton);
		data.panel.setLayer(pausButton, 1);
	}

	public void initializeMenuPause(Menu menu){
		this.menu = menu;

		title = new JTextArea("PAUSE");
		title.setFont(new Font("Squealer Embossed", Font.PLAIN, 150 * data.size / 48));
		title.setBackground(new Color(0, 0, 0, 0));
		title.setForeground(Color.WHITE);
		title.setFocusable(false);

		int[] tsize = data.getTextSize(title);
		title.setBounds(data.width / 2 - tsize[0] / 2, data.height / 10, tsize[0], tsize[1]);

		contButton = createButton("CONTINUE", data.width / 3, (data.height / 6) * 2);
		sauvButton = createButton("SAVE", data.width / 3, (data.height / 6) * 3);
		optiButton = createButton("OPTION", data.width / 3, (data.height / 6) * 4);
		quitButton = createButton("QUIT", data.width / 3, (data.height / 6) * 5);

		sauvButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				try {
					map.saveMap();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				data.running = false;
				data.key.pause = false;
				data.panel.removeAll();
				menu.displayMenu();
			}
		});

		contButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				removeMenuPause();
				data.panel.add(pausButton);
				data.panel.setLayer(pausButton, 1);
				data.key.pause = false;
				data.windowOpen = false;
				data.key.move = true;
				data.panel.revalidate();
				data.panel.requestFocusInWindow();
			}
		});

		this.add(title);
		this.add(contButton);
		this.add(sauvButton);
		this.add(optiButton);
		this.add(quitButton);
	}

	public JButton createButton(String name, int x, int y){
		JButton button = new JButton(name);
		button.setBounds(x, y, data.width / 3, data.height / 8);
		button.setFocusPainted(false);
		return button;
	}

	public void displayMenuPause(){
		data.key.pause = true;
		data.windowOpen = true;

		data.clearMenuPanel();
		
		data.panel.remove(pausButton);
		if (!data.panel.isAncestorOf(this)) {
			data.panel.add(this);
			data.panel.setLayer(this, 2);
		}

		data.panel.revalidate();
		data.panel.requestFocusInWindow();
	}

	public void removeMenuPause(){
		data.panel.remove(this);
	}
}
