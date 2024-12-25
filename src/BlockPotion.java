import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class BlockPotion extends Potion {

	private Data data;
	private Map map;
	private LinkedList<Integer> count;
	private int bonus;
	private JTextArea clickText;
	private JTextArea errorMessage;
	private Timer timerError;
	private Inventory inventory;
	private LinkedList<JProgressBar> progressBar;

	Color transparent = new Color(0, 0, 0, 0);

	public BlockPotion(Data data, Map map, Inventory inventory, BufferedImage image, String name, int price, String symb, int time, int bonus){
		super();
		this.image = image;
		this.name = name;
		this.price = price;
		this.symb = symb;
		this.time = time;
		this.data = data;
		this.bonus = bonus;
		this.map = map;
		this.inventory = inventory;

		this.progressBar = new LinkedList<JProgressBar>();
		this.count = new LinkedList<Integer>();

		clickText = new JTextArea("Left click : USE | Right Click : CANCEL"); 
		clickText.setFocusable(false);
		clickText.setBackground(Color.WHITE);
		clickText.setForeground(Color.BLACK);
		clickText.setFont(new Font("Squealer Embossed", Font.PLAIN, 30 * data.size / 48));

		int[] textSize = data.getTextSize(clickText);
		clickText.setBounds(data.width / 2 - textSize[0] / 2, textSize[1], textSize[0], textSize[1]);

		errorMessage = new JTextArea("CAN'T PLACE HERE");
		errorMessage.setFocusable(false);
		errorMessage.setBackground(transparent);
		errorMessage.setForeground(Color.RED);
		errorMessage.setFont(new Font("Squealer Embossed", Font.PLAIN, 50 * data.size / 48));

		textSize = data.getTextSize(errorMessage);
		errorMessage.setBounds(data.width / 2 - textSize[0] / 2, textSize[1] * 2, textSize[0], textSize[1]);
		this.timerError = new Timer(3000, new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				data.panel.remove(errorMessage);
			}
			
		});
		timerError.setRepeats(false);
	}

	@Override
	public void usePotion() {
		data.clearMenuPanel();
		data.key.editMode = true;
		data.key.type = 3;
		data.key.move = true;

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				data.panel.add(clickText);
				data.panel.setLayer(clickText, 1);
				while (data.key.editMode) {
					SwingUtilities.invokeLater(() -> {
						if (data.mouse.rightClick || data.key.pause) {
							System.out.println("pause");
							data.panel.remove(clickText);
							if (data.panel.isAncestorOf(errorMessage)){
								data.panel.remove(errorMessage);
							}
							data.key.editMode = false;
						}
						if (data.mouse.leftClick) {
							int[] pos = map.getIndexPos(data.mouse.x, data.mouse.y);
							Tile tile = map.getTile(pos[0], pos[1]);
							switch (tile.getType()) {
								case CRAFTER : case COLLECTER :
									if (((Block)tile).hasBonus){
										data.panel.add(errorMessage);
										data.panel.setLayer(errorMessage, 1);
										timerError.restart();
										break ;
									}
									putBonus(tile);
									data.panel.remove(clickText);
									if (data.panel.isAncestorOf(errorMessage)){
										data.panel.remove(errorMessage);
									}
									data.key.editMode = false;
									break ;
								default:
									data.panel.add(errorMessage);
									data.panel.setLayer(errorMessage, 1);
									timerError.restart();
									break ;
							}
						}
					});
					try {
						Thread.sleep(16); // Add small delay to prevent CPU overuse
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						break;
					}
				}
				
			}
		});
		thread.start();
	}

	public void putBonus(Tile tile){
		Block machine = (Block)tile;

		inventory.deleteObj(this, 1);
		machine.setBonus(bonus);
		machine.hasBonus = true;
		progressBar.add(new JProgressBar(0, time * 1000));

		JProgressBar pg = progressBar.getLast();
		pg.setStringPainted(true);
		pg.setForeground(Color.BLUE);
		pg.setString("x" + bonus);
		pg.setSize(data.size, data.size / 3);
		pg.setVisible(true);

		count.add(0);
		int index = count.indexOf(count.getLast());
		
		pg.setLocation(machine.posX, machine.posY - data.size / 3);
		data.panel.add(pg);
		data.panel.setLayer(pg, 1);
		this.timer = new Timer(1, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pg.setValue(count.get(index));
				count.set(index, count.get(index) + 1);
				if (data.key.pause){
					data.panel.remove(pg);
				} else if (data.panel.isAncestorOf(pg) == false){
					data.panel.add(pg);
					data.panel.setLayer(pg, 1);
				}
				pg.setLocation(machine.posX, machine.posY - data.size / 3);
				if (count.get(index) >= time * 1000){
					machine.setBonus(1);
					data.panel.remove(pg);
					progressBar.remove(pg);
					count.remove(count.get(index));
					machine.hasBonus = false;
					timer.stop();
				}
			}
		});
		timer.setRepeats(true);
		timer.start();
	}

	@Override
	public Potion.PotionType getPotionType() {
		return Potion.PotionType.BLOCK;
	}
	
}
