import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import javax.swing.*;

public class Main {

	static private int width = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	static private int height = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();

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
		SwingUtilities.invokeLater(runnable);

		window.add(runnable.game);

		window.pack();

		window.setLocationRelativeTo(null);
		window.setVisible(true);

		runnable.game.InitializeGame();
		runnable.startGthread();
	}
}