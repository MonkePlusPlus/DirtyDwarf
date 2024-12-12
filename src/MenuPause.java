
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class MenuPause extends JPanel {
	
	private Data data;

	private JButton pausButton;
	private JButton contButton;
	private JButton optiButton;
	private JButton sauvButton;
	private JButton quitButton;

	private Menu menu;
	private Inventory inventory;

	private JTextArea title;
	Color myColor = new Color(0, 0, 0, 10);

	public MenuPause(Data data){
		super();
		this.data = data;
		this.setBackground(myColor);
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
	}

	public void InitializeMenuPause(Menu menu, Inventory inventory){
		this.menu = menu;
		this.inventory = inventory;

		title = new JTextArea("PAUSE");
		title.setBounds(data.width / 3, data.height / 10, data.width, data.height / 5);
		title.setFont(new Font("Squealer", Font.BOLD, 150 * (data.width / 1920)));
		title.setOpaque(true);
		title.setBackground(new Color(0, 0, 0, 0));
		title.setForeground(Color.WHITE);
		title.setFocusable(false);

		contButton = createButton("CONTINUE", data.width / 3, (data.height / 6) * 2);
		sauvButton = createButton("SAVE", data.width / 3, (data.height / 6) * 3);
		optiButton = createButton("OPTION", data.width / 3, (data.height / 6) * 4);
		quitButton = createButton("QUIT", data.width / 3, (data.height / 6) * 5);

		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				data.panel.removeAll();
				data.key.pause = false;
				menu.displayMenu();
			}
		});

		contButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				removeMenuPause();
				data.panel.add(pausButton);
				inventory.showInvButton();
				data.key.pause = false;
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
		inventory.removeInvButton();
		inventory.removeInventory();
		data.panel.remove(pausButton);
		data.panel.add(this);

		data.panel.revalidate();
		data.panel.requestFocusInWindow();
	}

	public void removeMenuPause(){
		data.panel.remove(this);
	}
}
