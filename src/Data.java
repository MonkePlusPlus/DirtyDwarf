
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Data {
	
	public int width;
	public int height;
	public int size;
	public int speed;
	public KeyHandler key;
	public JLayeredPane panel;

	public JFrame window;
	public Mouse mouse;
	public Thread thread;
	public boolean windowOpen;
	public JPanel menuPanel;

	public boolean bonusPlayer;
	public boolean running;

	public Data(int w, int h, int size, int speed, KeyHandler k, JFrame window, JLayeredPane panel, Mouse mouse, Thread thread){
		this.height = h;
		this.width = w;
		this.size = size;
		this.speed = speed;
		this.key = k;
		this.panel = panel;
		this.window = window;
		this.mouse = mouse;
		this.thread = thread;
	}

	public void initialisePanel(){
		this.menuPanel = new JPanel();
		this.menuPanel.setBounds(0, 0, width, height);
		this.menuPanel.setOpaque(false);
		this.menuPanel.setFocusable(false);
		this.menuPanel.setLayout(null);
		this.menuPanel.setDoubleBuffered(true);
		this.menuPanel.addKeyListener(key);
		this.menuPanel.setVisible(true);
	}

	public int[] getTextSize(JTextArea text){
		FontMetrics fontMetrics = text.getFontMetrics(text.getFont());

		int[] i = new int[2];
		i[0] = fontMetrics.charsWidth(text.getText().toCharArray(), 0, text.getText().length());
		i[1] = fontMetrics.getHeight();
		return (i);
	}

	public BufferedImage rotateImage(BufferedImage buffImage, double angle) {
		double radian = Math.toRadians(angle);
		double sin = Math.abs(Math.sin(radian));
		double cos = Math.abs(Math.cos(radian));
	
		int width = buffImage.getWidth();
		int height = buffImage.getHeight();
	
		int nWidth = (int) Math.floor((double) width * cos + (double) height * sin);
		int nHeight = (int) Math.floor((double) height * cos + (double) width * sin);
	
		BufferedImage rotatedImage = new BufferedImage(
				nWidth, nHeight, BufferedImage.TYPE_INT_ARGB);
	
		Graphics2D graphics = rotatedImage.createGraphics();
	
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	
		graphics.translate((nWidth - width) / 2, (nHeight - height) / 2);
		// rotation around the center point
		graphics.rotate(radian, (double) (width / 2), (double) (height / 2));
		graphics.drawImage(buffImage, 0, 0, null);
		graphics.dispose();
	
		return rotatedImage;
	}

	public BufferedImage flipImage(BufferedImage image){
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage flippedImage = new BufferedImage(w, h, image.getType());
        Graphics2D g = flippedImage.createGraphics();
        g.drawImage(image, 0, 0, w, h, w, 0, 0, h, null);
        g.dispose();
        return flippedImage;
	}

	public JButton createButton(String string, Rectangle rect, int fontSize, int fontType, Color color){
		JButton button = new JButton(string);

		button.setFocusable(false);
		button.setBackground(color);
		button.setFont(new Font("Squealer Embossed", fontType, fontSize * size / 48));
		button.setBounds(rect);
		return button;
	}

	public JButton createButton(ImageIcon image, Rectangle rect, int fontSize, int fontType, Color color){
		JButton button = new JButton(image);

		button.setFocusable(false);
		button.setBackground(color);
		button.setFont(new Font("Squealer Embossed", fontType, fontSize * size / 48));
		button.setBounds(rect);
		return button;
	}

	public void clearMenuPanel(){
		menuPanel.removeAll();
		windowOpen = false;
	}
}
