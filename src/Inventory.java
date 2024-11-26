import java.util.LinkedList;

class Slot {
	public Item obj;
	public int nb;

	public Slot(Item obj, int nb){
		this.obj = obj;
		this.nb = nb;
	}
}

public class Inventory {
	
	private LinkedList<Slot> items;
	public int money;

	public Inventory(){
		items = new LinkedList<>();
		this.money = 0;
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
