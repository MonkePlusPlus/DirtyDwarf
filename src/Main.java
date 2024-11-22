import javax.swing.*;

public class Main {

	static private int width = 1500;
	static private int height = 900;

	public static void main(String[] args) {		
		JFrame window = new JFrame("Dirty Dwarf");

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(width, height);
		window.setResizable(false);

		Game game = new Game(width, height);
		window.add(game);

		window.pack();

		window.setLocationRelativeTo(null);
		window.setVisible(true);

		game.InitializeGame();
		game.startGthread();
	}
}