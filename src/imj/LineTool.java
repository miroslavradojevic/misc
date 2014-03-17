package imj;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Line;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 * Created by miroslav on 3/17/14.
 * takes the line roi drawn on the image and extracts the coordinates and image values
 * along the line, values are plotted and printed
 * parameters is treated as a sampling step (if <= 1 pix)
 * or
 * # sampling points along line segment (if > 1 pix)
 */
public class LineTool implements PlugInFilter {

	Line l;
	float x1, y1, x2, y2;

	// interface
	ImageProcessor 	ip_viz;
	ImagePlus		imp_viz = new ImagePlus();



	public int setup(String s, ImagePlus imagePlus) {

		if (imagePlus==null) return DONE;

		// check if there is a line roi drawn on the image
		Roi getRoi = imagePlus.getRoi();
		if (getRoi!=null && getRoi.isLine()) {

			IJ.log("contains the line, initialize");

			l = (Line) getRoi;

			x1 = l.x1;
			y1 = l.y1;

			x2 = l.x2;
			y2 = l.y2;

			IJ.log("limits:");
			IJ.log("(x1, y1) = ("+l.x1+" , "+l.y1+")");
			IJ.log("(x2, y2) = ("+l.x2+" , "+l.y2+")");

		}
		else {
			IJ.log("there was no line drawn!");
			return DONE;
		}

		return DOES_8G+DOES_32;
	}

	public void run(ImageProcessor imageProcessor) {

		IJ.log("run...");

		// extract locations along the line


		// print them

		// plot them



	}

	// extracts image values along the line

	// extracts coordinates along the line

}
