package imj;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

import java.awt.*;
import java.util.ArrayList;

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
    float[][] inimg_xy; // store image in an array

	// interface
	ImageProcessor 	ip_viz;
	ImagePlus		imp_viz = new ImagePlus();
    ImageCanvas     cnv;

    // parameter
    float sampling;

	public int setup(String s, ImagePlus imagePlus)
    {

        IJ.setTool("line");

		if (imagePlus==null) return DONE;

        cnv = imagePlus.getCanvas();

		// check if there is a line roi drawn on the image
		Roi getRoi = imagePlus.getRoi();
		if (getRoi!=null && getRoi.isLine()) {

			IJ.log("contains the line, initialize");

			l = (Line) getRoi;

			x1 = (float) l.x1d;
			y1 = (float) l.y1d;
			x2 = (float) l.x2d;
            y2 = (float) l.y2d;

		}
		else {

			IJ.log("there was no line drawn!");
			return DONE;

		}

        /*
            generic dialog
         */
        GenericDialog gd = new GenericDialog("Sampling along line");
        gd.addNumericField("sampling rate:", 0.8f, 2, 10, "if <=1 resolution, else # points");
        gd.showDialog();
        if (gd.wasCanceled()) {
            return DONE;
        }
        sampling = (float) gd.getNextNumber();
        IJ.log("line length "+l.getLength());
        sampling = (sampling>l.getLength())? 1f : sampling;


        IJ.log("line: ");
        IJ.log("(x1, y1) = ("+l.x1+" , "+l.y1+")");
        IJ.log("(x2, y2) = ("+l.x2+" , "+l.y2+")");
        IJ.log("sampling rate = " + sampling);

        // load image in float[][] in a similar manner used at PeakAnalyzer2D
        inimg_xy = new float[imagePlus.getWidth()][imagePlus.getHeight()]; // x~column, y~row

        if (imagePlus.getType()== ImagePlus.GRAY8) {
            byte[] read = (byte[]) imagePlus.getProcessor().getPixels();
            for (int idx=0; idx<read.length; idx++) {
                inimg_xy[idx%imagePlus.getWidth()][idx/imagePlus.getWidth()] = (float) (read[idx] & 0xff);
            }

        }
        else if (imagePlus.getType()==ImagePlus.GRAY32) {
            float[] read = (float[]) imagePlus.getProcessor().getPixels();
            for (int idx=0; idx<read.length; idx++) {
                inimg_xy[idx%imagePlus.getWidth()][idx/imagePlus.getWidth()] = read[idx];
            }
        }
        else {
            IJ.log("image type not recognized");
            return DONE;
        }

        return DOES_8G+DOES_32;
	}

	public void run(ImageProcessor imageProcessor)
    {

        ArrayList<float[]> locs         = localLineLocs(x1, y1, x2, y2); // extract locations along the line
        ArrayList<OvalRoi> ovals   = localLineOvals(locs);               // turn them into OvalRoi array

		// print them on top of opened canvas
        Overlay ov = new Overlay();
        ov.add(l); // first add the line
        for (int ii=0; ii<locs.size(); ii++) ov.add(ovals.get(ii));
        cnv.setOverlay(ov);

		// visualize the line profile values
        float[] xx = new float[locs.size()];
        float[] yy = new float[locs.size()];
        for (int ii=0; ii<xx.length; ii++) { xx[ii]=ii; yy[ii] = interpolateAt(locs.get(ii)[0], locs.get(ii)[1], inimg_xy); }
        Plot p = new Plot("", "", "", xx, yy);
        ip_viz = p.getProcessor();
        imp_viz.setProcessor("values along line", ip_viz);
        if (imp_viz!=null) imp_viz.show();

        // plot values
        String out_string = "";
        float min_val = Float.MAX_VALUE;
        float max_val = Float.NEGATIVE_INFINITY;

        for (int ii=0; ii<yy.length; ii++) {

            out_string += IJ.d2s(yy[ii], 2);

            if (ii==yy.length-1) out_string += "\n";
            else out_string += ", ";

            if (yy[ii]<min_val) min_val = yy[ii];
            if (yy[ii]>max_val) max_val = yy[ii];

        }
        IJ.log(out_string);

        // normalized
        out_string = "";
        for (int ii=0; ii<yy.length; ii++) {
            float normalized_val = (yy[ii]-min_val)/(max_val-min_val);
            out_string += IJ.d2s(normalized_val, 2);

            if (ii==yy.length-1) out_string += "\n";
            else out_string += ", ";

        }

        IJ.log(out_string);

	}

    /*
		methods that deal with local line
	 */

    // extracts image values along the line
    private ArrayList<float[]> localLineLocs(float x1, float y1, float x2, float y2) {

        float dist = (float) Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2)); //  + Math.pow(z2lay-z1lay, 2)

        int elementsInLine = (int) (dist / sampling);  // how many increment can safely fit between

        float dx = (x2 - x1) / dist;
        float dy = (y2 - y1) / dist;

        dx *= sampling;
        dy *= sampling;

        ArrayList<float[]> pts = new ArrayList<float[]>(elementsInLine);

        for (int cc = 0; cc<elementsInLine; cc++) {

            float atX = x1      + cc * dx;
            float atY = y1      + cc * dy;

            pts.add(new float[]{atX, atY});

        }

        return pts;
    }

    private ArrayList<OvalRoi> localLineOvals(ArrayList<float[]> locs)
    {
        float R = sampling/3f; // radius of the oval circles

        ArrayList<OvalRoi> out_ovals = new ArrayList<OvalRoi>(locs.size());
        for (int ii=0; ii<locs.size(); ii++) {
            float atX = locs.get(ii)[0];
            float atY = locs.get(ii)[1];
            OvalRoi ovroi = new OvalRoi(atX-R/2, atY-R/2, R, R);
            ovroi.setStrokeWidth(R/2f);
            ovroi.setStrokeColor(Color.GREEN);
            out_ovals.add(ovroi);
        }
        return out_ovals;
    }

    private float	interpolateAt(float atX, float atY, float[][] img2d_xy) {

        int x1 = (int) atX;
        int x2 = x1 + 1;
        float x_frac = atX - x1;

        int y1 = (int) atY;
        int y2 = y1 + 1;
        float y_frac = atY - y1;

        boolean isIn =
                        y1>=0 && y1<img2d_xy[0].length  &&
                        y2>=0 && y2<img2d_xy[0].length  &&
                        x1>=0 && x1<img2d_xy.length     &&
                        x2>=0 && x2<img2d_xy.length;

        if(!isIn){
            return 0;
        }

        // take neighbourhood
        float I11_1 = img2d_xy[ x1  ][ y1 ];  // upper left
        float I12_1 = img2d_xy[ x2  ][ y1 ];  // upper right
        float I21_1 = img2d_xy[ x1  ][ y2  ]; // bottom left
        float I22_1 = img2d_xy[ x2  ][ y2  ]; // bottom right

//        float I11_2 = img3d_zxy[ z2  ][ x1 ][ y1 ]; // upper left
//        float I12_2 = img3d_zxy[ z2  ][ x2 ][ y1 ]; // upper right
//        float I21_2 = img3d_zxy[ z2  ][ x1 ][ y2 ]; // bottom left
//        float I22_2 = img3d_zxy[ z2  ][ x2 ][ y2 ]; // bottom right

        float I_1 = (  (1-y_frac) * ((1-x_frac)*I11_1 + x_frac*I12_1) + (y_frac) * ((1-x_frac)*I21_1 + x_frac*I22_1) );

        return I_1;

    }

}
