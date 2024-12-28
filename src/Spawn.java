
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.Timer;

public class Spawn extends Block {
	
	private Timer timerClick;

	public Spawn(Data data, int x, int y, String symb, boolean collision, BufferedImage image) {
		super(data, x, y, symb, collision, image);
		this.timerClick = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				// wait
			}
		});
		this.timerClick.setRepeats(false);
	}

	@Override
	public Tile.TileType getType(){
		return Tile.TileType.SPAWN;
	}

	@Override
	public void mouseClick(){
		if (data.key.editMode == false && touchClose() && timerClick.isRunning() == false){
			timerClick.restart();
			System.out.println("touch spawn");
		}
	}
}
