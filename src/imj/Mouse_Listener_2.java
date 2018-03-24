package imj;

import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.io.OpenDialog;
import ij.plugin.PlugIn;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by miroslav on 5/18/14.
 */
public class Mouse_Listener_2 implements PlugIn, MouseListener {

	ImageCanvas can;

	@Override
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		int offscreenX = can.offScreenX(x);
		int offscreenY = can.offScreenY(y);
		IJ.log("Mouse clicked: " + offscreenX + "," + offscreenY);
	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void run(String s) {

		OpenDialog dc = new OpenDialog("Select file");
		String image_path = dc.getPath();
		if (image_path==null) return;
		IJ.log(image_path);

		ImagePlus ip_load = new ImagePlus(image_path);
		if(ip_load==null) return;
		ip_load.show();

//		int cnt = WindowManager.getImageCount();
//		IJ.log(cnt+" images shown");

		can = ip_load.getCanvas();
		can.addMouseListener(this);
		IJ.log("done!");

	}
}
