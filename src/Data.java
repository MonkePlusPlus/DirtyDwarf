
import javax.swing.JPanel;

public class Data {
	
	public int width;
	public int height;
	public int size;
	public int speed;
	public KeyHandler key;
	public JPanel panel;

	public Data(int w, int h, int size, int speed, KeyHandler k, JPanel panel){
		this.height = h;
		this.width = w;
		this.size = size;
		this.speed = speed;
		this.key = k;
		this.panel = panel;
	}
}
