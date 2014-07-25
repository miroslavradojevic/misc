package imj;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Line;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.plugin.PlugIn;
import ij.plugin.frame.RoiManager;
import ij.process.ByteProcessor;
import imagescience.image.*;

import java.awt.*;
import java.io.File;
import java.util.Random;

/**
 * Created by miroslav on 24-7-14.
 */
public class SavingOverlay implements PlugIn {

	int W = 512, H = 512;
	float R = W/8f;

	public void run(String s) {

		Random rd = new Random();

		float x1 = rd.nextFloat() * W;
		float y1 = rd.nextFloat() * H;
		Color c1 = Color.RED;
		float r1 = (W/30f) + rd.nextFloat() * (W/20f);

		float x2 = rd.nextFloat() * W;
		float y2 = rd.nextFloat() * H;
		Color c2 = Color.YELLOW;
		float r2 = (W/30f) + rd.nextFloat() * (W/20f);

		// create an empty image
		ByteProcessor bp = new ByteProcessor(W, H);
		ImagePlus imp = new ImagePlus("the_name", bp);
		imp.show();

		IJ.saveAs(imp, "Tiff", System.getProperty("user.dir") + File.separator + imp.getShortTitle()+".tif");

		ImagePlus imp1 = imp.duplicate(); // to show after reading
		imp1.setTitle("the_name");

		Overlay ov = new Overlay();

		// red oval
		OvalRoi ovalroi = new OvalRoi(x1-r1, y1-r1, 2*r1, 2*r1);
		ovalroi.setFillColor(c1);
		ovalroi.setStrokeColor(c1);
		ov.add(ovalroi);
		System.out.println("added "+c1+" oval at:\t" + x1 + " , " + y1 + ", " + r1 );

		// red line
		Line ln = new Line(x1, y1, x1-r1, y1-r1);
		ln.setStrokeWidth(r1/4f);
		ln.setStrokeColor(c1);
		ln.setFillColor(c1);
		ov.add(ln);

		ln = new Line(x1, y1, x1-r1, y1+r1);
		ln.setStrokeWidth(r1/4f);
		ln.setStrokeColor(c1);
		ln.setFillColor(c1);
		ov.add(ln);

		ln = new Line(x1, y1, x1+Math.sqrt(2)*r1, y1);
		ln.setStrokeWidth(r1/4f);
		ln.setStrokeColor(c1);
		ln.setFillColor(c1);
		ov.add(ln);

		// yellow oval
		ovalroi = new OvalRoi(x2-r2, y2-r2, 2*r2, 2*r2);
		ovalroi.setFillColor(c2);
		ovalroi.setStrokeColor(c2);
		ov.add(ovalroi);
		System.out.println("added "+Color.YELLOW+" oval at:\t" + x2 + " , " + y2 + ", " + r2 );

		ln = new Line(x2, y2, x2+Math.sqrt(2)*r2, y2);
		ln.setStrokeWidth(r2/4f);
		ln.setStrokeColor(c2);
		ln.setFillColor(c2);
		ov.add(ln);

		imp.setOverlay(ov);

		// save overlay  as zip
		System.out.println("saving overlay...");
		RoiManager rm = new RoiManager();
		for (int i = 0; i < ov.size(); i++) {
			rm.addRoi(ov.get(i));
		}
		rm.runCommand("Save", System.getProperty("user.dir") + File.separator + imp.getShortTitle()+".zip");
		rm.close();
		IJ.run("Close All", "");

//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

		System.out.print("\n***************\nreading it back... ");
		RoiManager rm1 = new RoiManager();
		rm1.runCommand("Open", System.getProperty("user.dir") + File.separator + imp.getShortTitle()+".zip");
		Roi[] out = rm1.getRoisAsArray();
		System.out.println(" " + out.length+ " els.");

		Overlay ov_read = new Overlay();
		for (int i = 0; i < out.length; i++) {

			if (out[i].getType()==1) {

				float xc = (float) out[i].getBounds().getX();

//				System.out.println("1: " + xc);
//				System.out.println("2: " + out[i].getXBase());
//				System.out.println("3: " + out[i].getFloatBounds());

				float yc = (float) out[i].getBounds().getY();
				float rc = (float) out[i].getBounds().getWidth();

				xc = xc + rc/2;

				yc = yc + rc/2;

				rc = rc/2;

				System.out.println(out[i].getStrokeColor() + " oval at:\t" + xc + ", " + yc + " , " + rc);
			}

			ov_read.add(out[i]);
		}

		imp1.getProcessor().set(150);
		imp1.setOverlay(ov_read);
		imp1.show();

		IJ.saveAs(imp1, "Tiff", System.getProperty("user.dir") + File.separator + "after_read.tif");

	}
}
