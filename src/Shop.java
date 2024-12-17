
import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class Shop extends Block {

	LinkedList<Object> sellingObj;

	public Shop(Data data, int x, int y, boolean col, BufferedImage image){
		super(data, x, y, col, image);
		sellingObj = new LinkedList<Object>();
	}

	public void InitialiseObjShop(LinkedList<Object>[] listObj) {
		for (int i = 0; i < 3; i++){
			for (Object o : listObj[i]){
				sellingObj.add(o);
			}
		}
	}


}
