import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class KeyHandler implements KeyListener {

	public boolean right, left, up, down;
	public boolean key_e = false;
	public boolean pause = false;

	public boolean moneyP;
	public boolean moneyM;


	@Override
	public void keyTyped(KeyEvent e){

	}

	@Override
	public void keyPressed(KeyEvent e){
		int code = e.getKeyCode();
		
		if (pause)
			return ;

		if (code == KeyEvent.VK_UP){
			up = true;
		}
		if (code == KeyEvent.VK_DOWN){
			down = true;
		}
		if (code == KeyEvent.VK_LEFT){
			left = true;
		}
		if (code == KeyEvent.VK_RIGHT){
			right = true;
		}
		if (code == KeyEvent.VK_E && key_e == false) {
			key_e = true;
		} else if (code == KeyEvent.VK_E && key_e == true) {
			key_e = false;
		}
		if (code == KeyEvent.VK_P){
			moneyP = true;
		}
		if (code == KeyEvent.VK_O){
			moneyM = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e){
		int code = e.getKeyCode();

		if (pause)
			return ;

		if (code == KeyEvent.VK_UP){
			up = false;
		}
		if (code == KeyEvent.VK_DOWN){
			down = false;
		}
		if (code == KeyEvent.VK_LEFT){
			left = false;
		}
		if (code == KeyEvent.VK_RIGHT){
			right = false;
		}
		if (code == KeyEvent.VK_P){
			moneyP = false;
		}
		if (code == KeyEvent.VK_O){
			moneyM = false;
		}
	}
}