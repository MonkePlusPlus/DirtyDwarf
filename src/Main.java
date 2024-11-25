import javax.swing.*;

public class Main {

	static private int width = 1920;
	static private int height = 1080;

	public static void main(String[] args) {	
		JFrame window = new JFrame("Dirty Dwarf");

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(width, height);
		window.setResizable(false);

		Run runnable = new Run(width, height);
		window.add(runnable.game);

		window.pack();

		window.setLocationRelativeTo(null);
		window.setVisible(true);

		runnable.game.InitializeGame();
		runnable.startGthread();
	}
}