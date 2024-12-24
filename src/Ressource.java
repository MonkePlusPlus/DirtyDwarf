import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

public class Ressource extends Block {

	private Inventory inventory;
	public Object object;
	private JProgressBar progressBar;
	private boolean isMaking;
	private int width;
	private int height;
	private JTextArea clickText;

	public Ressource(Object object, Inventory inventory, Data data, int x, int y, boolean col, BufferedImage image){
		super(data, x, y, col, image);
		this.object = object;
		this.width = data.size;
		this.height = data.size / 3;
		this.inventory = inventory;
		this.symb = ((Item)object).symb;
	}

	public void initialiseRessource(){
		Item item = (Item)object;
		progressBar = new JProgressBar(0, item.time);
		progressBar.setStringPainted(true);
		progressBar.setForeground(Color.GREEN);
		progressBar.setString("");
		progressBar.setBounds(getPosX(), getPosY() - height, width, height);
		progressBar.setVisible(true);

		clickText = new JTextArea("Right Click : CANCEL"); 
		clickText.setFocusable(false);
		clickText.setBackground(Color.WHITE);
		clickText.setForeground(Color.BLACK);
		clickText.setFont(new Font("Squealer Embossed", Font.PLAIN, 30 * data.size / 48));

		int[] textSize = data.getTextSize(clickText);
		clickText.setBounds(data.width / 2 - textSize[0] / 2, textSize[1], textSize[0], textSize[1]);
	}

	public boolean isOut(){
		return (getPosX() < 0 || getPosX() + width > data.width
			||  getPosY() < 0 || getPosY() - height > data.height);
	}

	@Override
	public TileType getType(){
		return TileType.RESSOURCE;
	}

	@Override
	public void mouseClick(){
		if (data.key.editMode == false && touchClose() && isMaking == false) {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					isMaking = true;
					Item item = (Item)object;
					if (item.type){
						data.panel.add(clickText);
						data.panel.setLayer(clickText, 1);
						data.key.move = false;
					}
					long start = System.currentTimeMillis();
					long t;
					data.panel.add(progressBar);
					data.panel.setLayer(progressBar, 1);
					while ((t = ((System.currentTimeMillis() - start) / 1000)) < item.time){
						if (data.running == false) {
							return ;
						}
						progressBar.setValue((int)t);
						if (data.mouse.rightClick && item.type){
							data.key.move = true;
							isMaking = false;
							data.panel.remove(progressBar);
							data.panel.remove(clickText);
							return ;
						}
						if (isOut() || data.windowOpen || data.key.pause) {
							if (data.panel.isAncestorOf(progressBar)){
								data.panel.remove(progressBar);
							}
							if (item.type){
								data.panel.remove(clickText);
							}
						} else {
							if (data.panel.isAncestorOf(progressBar) == false){
								data.panel.add(progressBar);
								data.panel.setLayer(progressBar, 1);
							}
							if (item.type){
								data.key.move = false;
								if (data.panel.isAncestorOf(clickText) == false)
									data.panel.add(clickText);
							}
							progressBar.setLocation(getPosX(), getPosY() - height);
						}
					}
					if (data.panel.isAncestorOf(progressBar)){
						data.panel.remove(progressBar);
					}
					inventory.addObj(object, 1);
					if (item.type){
						data.panel.remove(clickText);
						data.key.move = true;
					}
					isMaking = false;
				}
			});
			thread.start();
		}
	}
}
