import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.swing.JProgressBar;

public class Ressource extends Block {

	private Inventory inventory;
	private Object object;
	private JProgressBar progressBar;
	private boolean isMaking;
	private int width;
	private int height;

	public Ressource(Object object, Inventory inventory, Data data, int x, int y, boolean col, BufferedImage image){
		super(data, x, y, col, image);
		this.object = object;
		this.width = data.size;
		this.height = data.size / 3;
		this.inventory = inventory;
	}

	public void initialiseRessource(){
		Item item = (Item)object;
		progressBar = new JProgressBar(0, item.time);
		progressBar.setStringPainted(true);
		progressBar.setForeground(Color.GREEN);
		progressBar.setString("");
		progressBar.setBounds(getPosX(), getPosY() - height, width, height);
		progressBar.setVisible(true);
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
		if (touchClose() && isMaking == false) {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					isMaking = true;
					Item item = (Item)object;
					if (item.type){
						data.key.move = false;
					}
					long start = System.currentTimeMillis();
					long t;
					data.panel.add(progressBar);
					while ((t = ((System.currentTimeMillis() - start) / 1000)) < item.time){
						progressBar.setValue((int)t);
						if (isOut() || data.windowOpen || data.key.pause) {
							if (data.panel.isAncestorOf(progressBar)){
								data.panel.remove(progressBar);
						}
						} else {
							if (data.panel.isAncestorOf(progressBar) == false){
								data.panel.add(progressBar);
							}
							progressBar.setLocation(getPosX(), getPosY() - height);
						}
					}
					if (data.panel.isAncestorOf(progressBar)){
						data.panel.remove(progressBar);
					}
					inventory.addObj(object, 1);
					if (item.type){
						data.key.move = true;
					}
					isMaking = false;
				}
			});
			thread.start();
		}
	}
}
