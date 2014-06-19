package imj;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;

/**
 * Created by miroslav on 6/17/14.
 */
public class WindowPositioning implements PlugIn {

	public void run(String s) {

		System.out.println("testing window positioning...");

		ImagePlus ip1 = IJ.openImage("http://imagej.nih.gov/ij/images/blobs.gif");
		ip1.show();
		ImageWindow iw1 = ip1.getWindow();

		int horiz = 200;
		int verti = 100;
		int W = 500;
		int H = 500;
		iw1.setLocationAndSize(horiz, verti , W, H);
		//ip1.getCanvas().fitToWindow();

		ImagePlus     ip2 = new ImagePlus("", new ByteProcessor(128, 128));
		ip2.show();
		ImageWindow iw2 = ip2.getWindow();




		verti = verti+H;
		iw2.setLocationAndSize(horiz, verti , 300, 300);




	}
}
