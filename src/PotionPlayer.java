
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.JProgressBar;
import javax.swing.Timer;

public class PotionPlayer extends Potion {
	
	private Data data;
	private Inventory inventory;
	private Player player;
	private int count;
	private int bonus;
	private JProgressBar progressBar;

	public PotionPlayer(Data data, Player player, Inventory inventory, BufferedImage image, String showName, int price, String symb, int time, int bonus){
		super();
		this.image = image;
		this.showName = showName;
		this.name = "PPOTION";
		this.price = price;
		this.symb = symb;
		this.time = time;
		this.data = data;
		this.inventory = inventory;
		this.bonus = bonus;
		this.player = player;
		progressBar = new JProgressBar(0, time * 10);
		progressBar.setStringPainted(true);
		progressBar.setForeground(Color.BLUE);
		progressBar.setString(showName);
		progressBar.setBounds(data.width / 2 + data.width / 12, data.height / 40, data.width / 6, data.height / 20);
		progressBar.setVisible(true);
	}

	@Override
	public Potion.PotionType getPotionType() {
		return Potion.PotionType.PLAYER;
	}

	@Override
	public void usePotion() {
		if (data.bonusPlayer){
			return ;
		}
		inventory.deleteObj(this, 1);
		inventory.updateInvPanel(); 
		progressBar.setValue(0);

		data.panel.add(progressBar);
		data.panel.setLayer(progressBar, 1);
		
		count = 0;
		player.bonus = bonus;
		data.bonusPlayer = true;
		this.timer = new Timer(100, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				progressBar.setValue(count);
				count++;
				if (data.running == false){
					data.bonusPlayer = false;
					timer.stop();
					return ;
				}
				if (data.key.pause){
					data.panel.remove(progressBar);
				} else if (data.panel.isAncestorOf(progressBar) == false){
					data.panel.add(progressBar);
				}
				if (count >= time * 10){
					player.bonus = 1;
					data.panel.remove(progressBar);
					data.bonusPlayer = false;
					timer.stop();
				}
			}
		});
		timer.setRepeats(true);
		timer.start();
	}
}
