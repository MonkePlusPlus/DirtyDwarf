import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Mouse extends MouseAdapter {
	
	//private Data data;
	public Data data;
	public int x;
	public int y;
	public boolean leftClick;
	public boolean rightClick;

	public Mouse() {
		super();
	}

	@Override 
	public void mousePressed(MouseEvent mouse) {
		if (mouse.getButton() == MouseEvent.BUTTON1){
			leftClick = true;
			System.out.println("left Mouse click : " + "x = " + x + " y = " + y);

		} else if (mouse.getButton() == MouseEvent.BUTTON3){
			rightClick = true;
			System.out.println("right Mouse click : " + "x = " + x + " y = " + y);
		}
		x = mouse.getX();
		y = mouse.getY();
	}

	@Override
	public void mouseReleased(MouseEvent mouse){
		if (mouse.getButton() == MouseEvent.BUTTON1){
			leftClick = false;
		} else if (mouse.getButton() == MouseEvent.BUTTON3){
			rightClick = false;
		}
	}

	@Override 
	public void mouseMoved(MouseEvent mouse) {
		x = mouse.getX();
		y = mouse.getY();
		//System.out.println("Mouse move : " + "x = " + x + " y = " + y);
	}

	@Override 
	public void mouseDragged(MouseEvent mouse) {
	}

	public void drawSelect(Graphics2D g, Rectangle rectangle, Color color){
		if (rectangle == null){
			return ;
		}
		g.setColor(color);
		g.setStroke(new BasicStroke(data.size / 50));
		g.draw(rectangle);
	}
}
