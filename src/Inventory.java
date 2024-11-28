import java.awt.Color;
import java.awt.Dimension;
import java.util.LinkedList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

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
	private int startX;
	private int startY;
	private int width;
	private int height;
	Color myColour = new Color(255, 255, 255, 127);

	public Inventory(Data data, Item[][] listItems){
		this.data = data;
		this.items = new LinkedList<>();
		this.money = 0;
		this.startX = data.width / 10;
		this.startY = data.height / 10;
		this.width = (int)(data.width / 1.25);
		this.height = (int)(data.height / 1.25);
		this.setBounds(startX, startY, width, height);
		this.setBackground(myColour);
		//this.setAlignmentY(Component.CENTER_ALIGNMENT);
		data.panel.add(this);
		this.setVisible(false);
	}

	public void InitializeInvPanel(){
		invPanel = new JPanel();
		invPanel.setBackground(Color.BLUE);
		invPanel.setPreferredSize(new Dimension(width, height));
		invPanel.setDoubleBuffered(true);
		invPanel.setFocusable(true);
	}

	public void InitializeCraftPanel(){
		craftPanel = new JPanel();
		craftPanel.setBackground(Color.RED);
		craftPanel.setPreferredSize(new Dimension(width, height));
		craftPanel.setDoubleBuffered(true);
		craftPanel.setFocusable(true);
	}

	public void InitializeInventory(){
		this.addTab("Inventory", invPanel);
		this.addTab("Craft", craftPanel);
	}

	public void showInventory(boolean show){
		this.setVisible(show);
		//g.setColor(myColour);
		//g.fillRect(startX, startY, width , height);
	}

	public void addObj(Item obj){
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).obj.equals(obj)){
				items.get(i).nb += 1;
				return ;
			}
		}
		items.add(new Slot(obj, 1));
	}

	public boolean deleteObj(Item obj, int nb){
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).obj.equals(obj)){
				if (items.get(i).nb >= nb)
					items.get(i).nb -= nb;
				else 
					return false;
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
}
