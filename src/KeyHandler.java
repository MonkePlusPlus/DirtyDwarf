import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class KeyHandler implements KeyListener {

	public boolean right, left, up, down;
	public boolean key_e = false;


	@Override
	public void keyTyped(KeyEvent e){

	}

	@Override
	public void keyPressed(KeyEvent e){
		int code = e.getKeyCode();
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
	}

	@Override
	public void keyReleased(KeyEvent e){
		int code = e.getKeyCode();

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
	}
}