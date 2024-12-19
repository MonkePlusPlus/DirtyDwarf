
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Data {
	
	public int width;
	public int height;
	public int size;
	public int speed;
	public KeyHandler key;
	public JPanel panel;
	public JFrame window;
	public Mouse mouse;

	public Data(int w, int h, int size, int speed, KeyHandler k, JFrame window, JPanel panel, Mouse mouse){
		this.height = h;
		this.width = w;
		this.size = size;
		this.speed = speed;
		this.key = k;
		this.panel = panel;
		this.window = window;
		this.mouse = mouse;
	}
}
