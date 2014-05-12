package imj;

import ij.*;
import ij.gui.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
//import imagescience.images.*;
import imagescience.image.*;
import imagescience.utility.Progressor;
//import imagescience.utilities.*;
import java.math.*;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
/**
 *  Description of the Class
 *
 * @author     ismal
 * @created    March 21, 2006
 */
public class HoughTransform implements PlugInFilter, MouseListener {

    private ImagePlus imp;

    private int[] pixels;
    private Rectangle roi;
    private int dimx, dimy;
    private ImagePlus marked	;

    private static float teta_m = 0.0f;
    private static float teta_M = (float) Math.PI;
    private static float ro_m = -128.0f;
    private static float ro_M = (float) Math.sqrt(2) * 128.0f;
    private static float thresh = (float) 160;
    private static float bin_teta = (float) 0.005;
    private static float bin_ro = (float) 1;


    /**
     *  Description of the Method
     *
     * @param  arg  Description of the Parameter
     * @param  imp  Description of the Parameter
     * @return      Description of the Return Value
     */
    public int setup(String arg, ImagePlus imp) {

        this.imp = imp;
        if (imp == null) {
            IJ.error("Input image required!");
            return DONE;
        }

        // Build dialog:
        GenericDialog dlg = new GenericDialog("Hough Transform");

//	dlg.addNumericField("teta_m: ", teta_m, 3);
//	dlg.addNumericField("teta_M: ", teta_M, 3);
//	dlg.addNumericField("ro_m: ", 		ro_m, 1);
//	dlg.addNumericField("ro_M: ", ro_M, 1);
        dlg.addNumericField("threshold: ", thresh, 1);
        dlg.addNumericField("bins_teta: ", bin_teta, 3);
        dlg.addNumericField("bins_ro: ", bin_ro, 3);

        dlg.showDialog();

        // Store current settings:
//	teta_m = (float) dlg.getNextNumber();
//	teta_M = (float) dlg.getNextNumber();
//	ro_m = (float) dlg.getNextNumber();
//	ro_M = (float) dlg.getNextNumber();
        thresh = (float) dlg.getNextNumber();
        bin_teta = (float) dlg.getNextNumber();
        bin_ro = (float) dlg.getNextNumber();

        // Stop or continue:
        if (dlg.wasCanceled()) {
            return DONE;
        }

        return DOES_8G + DOES_16 + DOES_32 + NO_CHANGES;
    }


    /**
     *  Main processing method for the Hough_Transform object
     *
     * @param  ip  Description of the Parameter
     */
    public void run(ImageProcessor ip) {

        // Initialize:
        final ImageStack instack = imp.getStack();
        ImageStack outstack = null;
        Image inimg = null;
        Image outimg = null;
        float min = 0.0f;
        float max = 255.0f;

        ImagePlus instack_imp = new ImagePlus("", instack);

        final Object type = instack.getPixels(1);
        if (type instanceof byte[]) {
            inimg = new ByteImage(instack_imp);
        } else if (type instanceof short[]) {
            inimg = new ShortImage(instack_imp);
        } else if (type instanceof float[]) {
            inimg = new FloatImage(instack_imp);
        }

        // Process:
        final Dimensions dims = inimg.dimensions();
        dimx=dims.x; dimy=dims.y;

        roi = ip.getRoi();
        ro_m = (float) -roi.getWidth();
        ro_M = (float) (Math.sqrt(Math.pow(roi.getWidth(), 2) + Math.pow(roi.getHeight(), 2)));

//		ro_m = (float) -dims.x;
//		ro_M = (float) (Math.sqrt(Math.pow(dims.x, 2) + Math.pow(dims.y, 2)));
//		IJ.write("x="+roi.getX()+"   "+roi.getY()+"   "+roi.getWidth()+"   "+roi.getHeight());

        outimg = new FloatImage(new Dimensions(Math.round((teta_M - teta_m) / bin_teta), Math.round((ro_M - ro_m) / bin_ro)));
        final Coordinates cin = new Coordinates();
        final Coordinates cout = new Coordinates();
        final Progressor pgs = new Progressor();
        pgs.steps(dims.z * dims.y);

        final double[][] accum = new double[Math.round((ro_M - ro_m) / bin_ro)][Math.round((teta_M - teta_m) / bin_teta)];
        final double[][] tempix = new double[1][1];

//	IJ.write("dimensions="+Math.round((ro_M-ro_m)/bin_ro)+" x "+Math.round((teta_M-teta_m)/bin_teta));

        inimg.axes(Axes.X + Axes.Y);
        outimg.axes(Axes.X + Axes.Y);
        for (cin.y = (int)roi.getY(); cin.y < (int)(roi.getY()+roi.getHeight()); ++cin.y) {
            for (cin.x = (int)roi.getX(); cin.x < (int)(roi.getX()+roi.getWidth()); ++cin.x) {
                inimg.get(cin, tempix);
                if (tempix[0][0] > thresh) {
                    for (int teta = 0; teta < Math.round((teta_M - teta_m) / bin_teta); teta++) {
                        float ro = (float) ((cin.x-roi.getX()) * Math.cos(teta * bin_teta + teta_m) +
                                (cin.y - roi.getY()) * Math.sin(teta * bin_teta + teta_m));
                        //IJ.write("ro="+ro+"   "+ro_M);
                        //if((Math.round((ro-ro_m)/bin_ro)>=0))
                        accum[Math.round((ro - ro_m) / bin_ro)][teta]++;
                        //=tempix[0][0];
                    }
                }
            }
            pgs.step();
        }
//        pgs.end();

        outimg.set(cout, accum);

        // Show output image:
//        outstack = outimg.;
        final ImagePlus outimp = outimg.imageplus();//new ImagePlus();
        //final FloatProcessor minmax = new FloatProcessor(2, 1);
        //minmax.putPixelValue(0, 0, outimg.minimum());
        //minmax.putPixelValue(1, 0, outimg.maximum());
        //minmax.resetMinAndMax();
        //outstack.update(minmax);
        //outimp.setStack("", outstack);
        outimp.setTitle("Hough Transform");
        outimp.show();
        outimp.updateAndRepaintWindow();


        // 	create copy of the original image
        marked = NewImage.createRGBImage("Inverse Hough Transform", dims.x, dims.y, dims.t, NewImage.FILL_BLACK);
        ImageStack marked_stack = marked.getStack();
        for(int t=1; t<=dims.t;t++) (marked_stack.getProcessor(t)).copyBits(instack.getProcessor(t),0,0,Blitter.COPY);
        pixels = (int[]) (marked_stack.getProcessor(1)).getPixels();

        //plot roi
        if(roi.getX()!=0&&roi.getY()!=0){
            for (int x=(int)roi.getX();x<=(int)(roi.getX()+roi.getWidth());x++){
                pixels[(((int)roi.getY())*dims.x+(int)x)] = (( 15& 0xff) << 16) +((223 & 0xff) << 8) +(240 & 0xff);
                pixels[(((int)(roi.getY()+roi.getHeight())))*dims.x+(int)(x)] = (( 15& 0xff) << 16) +((223 & 0xff) << 8) +(240 & 0xff);
            }
            for (int y=(int)roi.getY();y<(int)(roi.getY()+roi.getHeight());y++){
                pixels[(((int)(y))*dims.x+(int)roi.getX())] = (( 15& 0xff) << 16) +((223 & 0xff) << 8) +(240 & 0xff);
                pixels[(((int)y)*dims.x+(int)(roi.getX()+roi.getWidth()))] = (( 15& 0xff) << 16) +((223 & 0xff) << 8) +(240 & 0xff);
            }
        }

        ImageWindow imw = outimp.getWindow();
        ImageCanvas imc	 = imw.getCanvas();
        imc.addMouseListener(this);

        marked.show();
        marked.updateAndDraw();


    }
    public void mouseClicked(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
    public void mousePressed(MouseEvent e) {
        //IJ.write("X= "+e.getX()+"  Y= "+e.getY()+"\n");
        float L_ro	= (float) 	(bin_ro*e.getY()+ro_m);
        float L_teta = (float) (bin_teta*e.getX()+teta_m);

        if((Math.atan(roi.getWidth()/roi.getHeight())<L_teta)&&(Math.atan(roi.getHeight()/roi.getWidth())+Math.PI/2>L_teta)){
            for (int x=(int)roi.getX();x<(int)(roi.getX()+roi.getWidth());x++){
                if(((roi.getY()+(L_ro-(x-roi.getX())*Math.cos(L_teta))/Math.sin(L_teta))<dimy)&&
                        ((roi.getY()+(L_ro-(x-roi.getX())*Math.cos(L_teta))/Math.sin(L_teta))>0))
                    pixels[(((int)(roi.getY()+(L_ro-(x-roi.getX())*Math.cos(L_teta))/Math.sin(L_teta)))*dimx+(int)x)] = (( 1& 0xff) << 16) +((255 & 0xff) << 8) +(1 & 0xff);
            }
        }
        else {
            for (int y=(int)roi.getY();y<(int)(roi.getY()+roi.getHeight());y++){
                if(((int)(roi.getX()+(L_ro-(y-roi.getY())*Math.sin(L_teta))/Math.cos(L_teta))<dimx)&&
                        ((int)(roi.getX()+(L_ro-(y-roi.getY())*Math.sin(L_teta))/Math.cos(L_teta))>0))
                    pixels[(((int)y)*dimx+(int)(roi.getX()+(L_ro-(y-roi.getY())*Math.sin(L_teta))/Math.cos(L_teta)))] = (( 1& 0xff) << 16) +((255 & 0xff) << 8) +(1 & 0xff);
            }
        }
        marked.show();
        marked.updateAndDraw();
    }
    public void mouseReleased(MouseEvent e) { }

}

