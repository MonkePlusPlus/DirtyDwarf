import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;

public class Collecter extends Block {
	
	private int maxCapacity = 100;
	private Object object;

	private int panelWidth;
	private int panelHeight;

	private Timer timerClick;

	private JLayeredPane mainPanel;
	private JPanel backPanel;
	private JButton collectButton;
	private JButton cancelButton;

	private JTextArea nameText;
	private JTextArea numberObj;

	Color myColor = new Color(255, 255, 255, 150);
	Color transparent = new Color(0, 0, 0, 0);

	public Collecter(Data data, int x, int y, boolean collision, BufferedImage image){
		super(data, x, y, collision, image);
		this.panelWidth = data.width / 5;
		this.panelHeight = data.height / 3;
		this.timerClick = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				// wait
			}
		});
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

		nameText = new JTextArea("Collecter");
		nameText.setBackground(transparent);
		nameText.setFont(new Font("Squealer Embossed", Font.BOLD, 30 * data.size / 48));
		nameText.setFocusable(false);

		int[] tsize = data.getTextSize(nameText);
		nameText.setBounds(panelWidth / 2 - tsize[0] / 2, tsize[1], tsize[0], tsize[1]);

		mainPanel.add(nameText);
		mainPanel.add(backPanel);

		mainPanel.setLayer(backPanel, 0);
		mainPanel.setLayer(nameText, 1);
		initialiseButton();
	}

	public void initialiseButton(){
		cancelButton = new JButton("CANCEL");
		cancelButton.setFocusable(false);
		cancelButton.setBackground(Color.YELLOW);
		cancelButton.setFont(new Font("Squealer Embossed", Font.BOLD, 50 * data.size / 48));
		cancelButton.setBounds(panelWidth / 2 - (panelWidth - panelWidth / 5) / 2, 4 * panelHeight / 5, panelWidth - panelWidth / 5, panelHeight / 5);

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeCollecter();
			}
		});
		mainPanel.add(cancelButton);
		mainPanel.setLayer(cancelButton, 1);
	}

	public void showCollecter() {
		data.menuPanel.add(mainPanel);
		data.windowOpen = true;
		data.key.move = false;
		data.panel.revalidate();
		data.panel.requestFocusInWindow();
	}

	public void removeCollecter() {
		data.menuPanel.remove(mainPanel);
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
