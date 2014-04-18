package imj;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.PointRoi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

import java.awt.*;
import java.util.Vector;

/**
 * Created by miroslav on 4/18/14.
 *
 */
public class PointToCirc implements PlugInFilter {

	ImagePlus loaded_image;
	double rad = 5f;

	public int setup(String s, ImagePlus imagePlus) {

		GenericDialog gd = new GenericDialog("Radius");
		gd.addMessage("converts all the PointRois in current image into OvalRois of predefined radius");
		gd.addMessage("keeps the color scheme");
		gd.addNumericField("radius: ", 5, 0, 10, "pix");
//		Vector v = gd.getNumericFields();
//		TextField tf = (TextField)v.get(0);
//		tf.setBackground(Color.YELLOW);
//		tf.setForeground(Color.RED);
//		gd.addMessage("times in blue", Font.decode("times"), Color.BLUE);
		gd.showDialog();
		if (gd.wasCanceled()) {
			return 0;
		}

		rad = gd.getNextNumber();
		IJ.log(rad + " was it..");


		loaded_image = imagePlus.duplicate();
		Overlay new_overlay = new Overlay();

		if (imagePlus.getOverlay()!=null) {

			int nr_overlays = imagePlus.getOverlay().size();

			for (int i=0; i<nr_overlays; i++) {

				String type = imagePlus.getOverlay().get(i).getTypeAsString();

				if (type.equalsIgnoreCase("Point")) {
					PointRoi pt_roi = (PointRoi) imagePlus.getOverlay().get(i);
					double xc = pt_roi.getBounds().getCenterX();
					double yc = pt_roi.getBounds().getCenterY();
					Color col = pt_roi.getStrokeColor();
					if (col.equals(Color.RED)) { // TP)
						col = new Color(0f, 0f, 1f, 0.6f); // to be compatible with the plots in R used for publication
					}
					if (col.equals(Color.BLUE)) { // FN
						col = new Color(1f, 0f, 0f, 0.6f);
					}
//					IJ.log(i + " : " + col);
					OvalRoi ov_roi = new OvalRoi(xc-rad/2f, yc-rad/2f, rad, rad);
					ov_roi.setStrokeColor(col);
					ov_roi.setFillColor(col);
					//new_overlay.add(pt_roi);
					new_overlay.add(ov_roi);
				}


			}

			loaded_image.setOverlay(new_overlay);

		}
		else {
			IJ.log("overlay empty");
		}
		return DOES_8G+DOES_32+NO_CHANGES;
	}

	@Override
	public void run(ImageProcessor imageProcessor) {

		IJ.log("test run...");
		loaded_image.show();

	}
}
