import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class KeyHandler implements KeyListener {

	public boolean right, left, up, down;
	public boolean key_e = false;
	public boolean pause = false;
	public boolean move = true;
	public boolean editMode;
	public int type; // 0 crafter | 1 collecter

	public boolean moneyP;
	public boolean moneyM;


	@Override
	public void keyTyped(KeyEvent e){

	}

	@Override
	public void keyPressed(KeyEvent e){
		int code = e.getKeyCode();
		
		if (pause || move == false){
			up = false;
			down = false;
			left = false;
			right = false;
			return ;
		}
		if (code == KeyEvent.VK_UP || code == KeyEvent.VK_Z){
			up = true;
		}
		if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S){
			down = true;
		}
		if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_Q){
			left = true;
		}
		if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D){
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

		//if (pause)
		//	return ;

		if (code == KeyEvent.VK_UP || code == KeyEvent.VK_Z){
			up = false;
		}
		if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S){
			down = false;
		}
		if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_Q){
			left = false;
		}
		if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D){
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