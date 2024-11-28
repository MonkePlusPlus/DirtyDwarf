import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import javax.swing.*;

public class Main {

	static private int width = 1920;
	static private int height = 1080;

	static GraphicsDevice device = GraphicsEnvironment
	.getLocalGraphicsEnvironment().getScreenDevices()[0];

	public static void main(String[] args) {	
		JFrame window = new JFrame("Dirty Dwarf");

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(width, height);
		window.setUndecorated(true);
		window.setResizable(false);

		device.setFullScreenWindow(window);

		Run runnable = new Run(width, height);
		window.add(runnable.game);

		window.pack();

		window.setLocationRelativeTo(null);
		window.setVisible(true);

		runnable.game.InitializeGame();
		runnable.startGthread();
	}
}