
public class Run implements Runnable {
	
	private int fps = 60;
	Thread gThread;
	public Game game;

	public Run(int width, int height) {
		this.game = new Game(width, height);
		this.game.InitializeGame();
	}

	public void startGthread(){
		gThread = new Thread(this);
		gThread.start();
	}

	@Override
	public void run(){
		double interval = 1000000000/fps;	
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;

		while (gThread != null){

			currentTime = System.nanoTime();

			delta += (currentTime - lastTime) / interval;
			lastTime = currentTime;

			if (delta >= 1){
				// GAME LOOP
				//System.out.println("loop");
				game.update();
				game.repaint();
				delta--;
			}
		}
	}
}
