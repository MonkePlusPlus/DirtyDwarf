
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class MenuPause extends JPanel {
	
	private Data data;

	private JButton contButton;
	private JButton optiButton;
	private JButton sauvButton;
	private JButton quitButton;

	private JTextArea title;
	Color myColor = new Color(0, 0, 0, 127);

	public MenuPause(Data data){
		super();
		this.data = data;
		this.setBackground(myColor);
		this.setBounds(0, 0, data.width, data.height);
		this.addKeyListener(data.key);
		this.setVisible(true);
	}


	public void InitializeMenuPause(){
		title = new JTextArea("PAUSE");
	}

	public void displayMenuPause(){
		data.panel.add(this);
	}

	public void removeMenuPause(){
		data.panel.remove(this);
	}
}
