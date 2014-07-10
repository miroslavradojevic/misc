package imj;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;

/**
 * Created by miroslav on 10-7-14.
 */
public class JustOpen implements PlugIn {

	public void run(String s) {

		IJ.open();
		ImagePlus curr_img = IJ.getImage();

		System.out.println("testing... \nimage name: " + curr_img.getShortTitle());

	}
}
