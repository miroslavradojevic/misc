import java.awt.Color;

import ij.ImagePlus;
import ij.gui.Plot;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagescience.image.Axes;
import imagescience.image.Coordinates;
import imagescience.image.Image;

/**
 * Created with IntelliJ IDEA.
 * User: miroslav
 * Date: 9/16/13
 * Time: 10:17 AM
 */

public class FloatConversion implements PlugInFilter {

	ImagePlus img;

	public void run(ImageProcessor arg0) {

		int H = img.getHeight();
		int W = img.getWidth();

		/*
		 *  my way of reading (used it so far)
		 */
		float[] img_array1 = (float [])img.getProcessor().convertToFloat().getPixels();

		/*
		 * alternative way of reading (with explicit conversion)
		 */
		float[] img_array = new float[H*W];
		byte[]  img_array_byte = (byte[])img.getProcessor().getPixels();
		for (int i = 0; i < img_array_byte.length; i++) {
			img_array[i] = (float)(img_array_byte[i] & 0xff);
		}

		/*
		 *  using imagescience, creating Image object
		 */
		Image imgin = Image.wrap(img);
		imgin.axes(Axes.X+Axes.Y);
		double[][] aImg;

		// now take a patch from the center
		int w = 20; // patch width, patch height
		int start_y = H/2-w/2;
		int start_x = W/2-w/2;

		// prepare plots in out1, out2, and out3
		double[] out1 = new double[w*w];
		int cnt = 0;
		for (int row = start_y; row < start_y+w; row++) {
			for (int col = start_x; col < start_x+w; col++) {
				out1[cnt] = img_array[row*W+col];
				cnt++;
			}
		}

		double[] out2 = new double[w*w];
		cnt = 0;
		for (int row = start_y; row < start_y+w; row++) {
			for (int col = start_x; col < start_x+w; col++) {
				out2[cnt] = img_array1[row*W+col];
				cnt++;
			}
		}

		double[] out3 = new double[w*w];
		Coordinates coords = new Coordinates(start_x, start_y);
		aImg = new double[w][w];
		imgin.get(coords, aImg);

		cnt = 0;
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < w; j++) {
				out3[cnt] = aImg[i][j];
				cnt++;
			}
		}


		// plot itself
		double[] x_axis = new double[w*w];
		for (int i = 0; i < x_axis.length; i++) {
			x_axis[i] = i;
		}

		Plot p = new Plot("mid-patch values", "pixel", "pixel's value");
		p.setSize(1200, 600);
		p.setLimits(0, w*w, 0, 255);
		p.setColor(Color.RED);
		p.addPoints(x_axis, out1, Plot.X);
		p.addLabel(0, 0, "explicit conversion");
		p.setColor(Color.BLUE);
		p.addPoints(x_axis, out2, Plot.LINE);
		p.addLabel(0.3, 0, "using convertToFloat()");
		p.setColor(Color.GREEN);
		p.addPoints(x_axis, out3, Plot.BOX);
		p.addLabel(0.6, 0, "using imagescience");
		p.draw();
		p.show();


	}

	public int setup(String arg0, ImagePlus arg1) {
		img = arg1;
		return DOES_8G+NO_CHANGES;
	}

}