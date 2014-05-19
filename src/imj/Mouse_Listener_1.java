package imj;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by miroslav on 5/18/14.
 */
public class Mouse_Listener_1 implements PlugInFilter, MouseListener {

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
	public int setup(String s, ImagePlus imagePlus) {
		can = imagePlus.getCanvas();
		can.addMouseListener(this);
		return DOES_8G+DOES_32+NO_CHANGES;
	}

	@Override
	public void run(ImageProcessor imageProcessor) {
		IJ.log("run()");


	}
}
