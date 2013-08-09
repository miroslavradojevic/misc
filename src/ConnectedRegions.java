import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import conn.Find_Connected_Regions;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.*;
import ij.io.FileSaver;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class ConnectedRegions  implements PlugInFilter {

	/*
	 * uses conn.Find_Connected_Regions class
	 */

    ImagePlus img;

    public static void main(String[] args){

        if(args.length!=1) {
            System.out.println(
                    "/* finding connected regions */ \n" +
                            "/* with the same gray-level value */ \n" +
                            "/* recommendable not more than several gray-levels (computation time and usage reasons) */ \n" +
                            "usage: set image path or name as argument\n"
            );
            return;
        }

        if(!(new File(args[0])).exists() || (new File(args[0])).isDirectory()){
            System.out.println("there is no file named ' "+args[0]+" '");
            return;
        }

        ImagePlus img = new ImagePlus((new File(args[0]).getAbsolutePath()));

        Find_Connected_Regions conn_reg = new Find_Connected_Regions(img, true);
        conn_reg.run("");

        System.out.println(conn_reg.getNrConnectedRegions()+" connected regions extracted...");
        ImagePlus regs = conn_reg.showLabels();

        String out_name = "labeled_regions.tif";

        if(img.getStack().getSize()>1){
            new FileSaver(regs).saveAsTiffStack(out_name);
        }
        else{
            new FileSaver(regs).saveAsTiff(out_name);
        }

        System.out.println((new File(out_name)).getAbsolutePath()+" saved...");

    }

    public void run(ImageProcessor arg0) {

        // dialog to enter input values
        GenericDialog gd = new GenericDialog("Connected Regions", IJ.getInstance());

        gd.addMessage(
                "Plugin will extract connected regions that contain the same GRAY8 value\n" +
                        "it is recommendable that the image does not contain too many intensities."
        );

        gd.showDialog();
        if (gd.wasCanceled()) return;

        long t1 = System.currentTimeMillis();
        Find_Connected_Regions conn_reg = new Find_Connected_Regions(img, true);
        conn_reg.run("");
        long t2 = System.currentTimeMillis();

        int nr_regions = conn_reg.getNrConnectedRegions();

        IJ.log(nr_regions+" connected regions extracted.\n" +
                "elapsed: "+((t2-t1)/1000f)+ " seconds.");

        ArrayList<ArrayList<int[]>> regs = conn_reg.getConnectedRegions();

        ImagePlus imageLabels = conn_reg.showLabels();
        imageLabels.show();

        Overlay ov = new Overlay();

        for (int i=0; i<regs.size(); i++) {

            IJ.log("region "+i+" :\t"+regs.get(i).size()+" elements");

            if (regs.get(i).size()>1) {
                float[] ellipseParams = extractEllipse(regs.get(i));
                IJ.log(""+ Arrays.toString(ellipseParams));
                PointRoi pt = new PointRoi(ellipseParams[1], ellipseParams[0]);
                ov.add(pt);
                float A =   (float)Math.sqrt(ellipseParams[3]);
                float B =   (float)Math.sqrt(ellipseParams[2]);
                float k =   A/B;
                B = (float) Math.sqrt(regs.get(i).size()/(k*Math.PI));
                A = k*B;
                ov.add(drawEllipse(ellipseParams[1], ellipseParams[0], A, B, ellipseParams[4], Color.RED, 1, 50));

            }

        }

        imageLabels.setOverlay(ov);

    }

    public int setup(String arg0, ImagePlus img) {

        this.img = img;// ImageConversions.ImagePlusToGray8(img);
        return DOES_8G+NO_CHANGES; //+DOES_RGB+

    }

    private PolygonRoi drawEllipse(float x, float y, float a, float b, float angle, Color clr, float lineWidth, int nrPoints)
    {

        float[] xEll = new float[nrPoints];
        float[] yEll = new float[nrPoints];
        double step = (2*Math.PI)/nrPoints;
        double beta = -angle*(Math.PI/180);

        for (int i=0; i<nrPoints; i++) {
            double alpha = i*step;
            xEll[i] = (float) (x + a * Math.cos(alpha) * Math.cos(beta) - b * Math.sin(alpha) * Math.sin(beta));
            yEll[i] = (float) (y + a * Math.cos(alpha) * Math.sin(beta) + b * Math.sin(alpha) * Math.cos(beta));
        }

        PolygonRoi p = new PolygonRoi(xEll, yEll, nrPoints, Roi.POLYGON);
        p.setStrokeWidth(lineWidth);
        p.setStrokeColor(clr);

        return p;

    }

    private float[] extractEllipse(ArrayList<int[]> locs) // row, col, majorAxis, minorAxis, angle
    {
        float[] ellipseParams = new float[5];

        double M00  = locs.size();
        double M10  = 0;
        double M01  = 0;
        double M11  = 0;
        double M20  = 0;
        double M02 	= 0;

        for (int i=0; i<locs.size(); i++) {

            M10 += locs.get(i)[0];
            M01 += locs.get(i)[1];
            M11 += locs.get(i)[1] * locs.get(i)[0];
            M20 += locs.get(i)[0] * locs.get(i)[0];
            M02 += locs.get(i)[1] * locs.get(i)[1];

        }

        ellipseParams[0] = (float) (M10 / M00);
        ellipseParams[1] = (float) (M01 / M00);

        if(M00>2) {

            float mu11 = (float) (M11 / M00) - ellipseParams[0]*ellipseParams[1];
            float mu20 = (float) (M20 / M00) - ellipseParams[0]*ellipseParams[0];
            float mu02 = (float) (M02 / M00) - ellipseParams[1]*ellipseParams[1];

            //IJ.log("EIGEN: mu20:"+mu20+",mu11:"+mu11+",mu02:"+mu02);

            ArrayList<float[]> out = eigen(mu20, mu11, mu11, mu02);

            //IJ.log("OUT");
            //for (int i=0; i<out.size(); i++) {
            //    for (int j=0; j<out.get(i).length; j++) {
            //        IJ.log(""+out.get(i)[j]+" , ");
            //    }
            //}

            if (out.size()>1) {

                // ellipseParams[2] is smaller one, ellipse[4] is it's angle
                if ( Math.abs(out.get(0)[0]) >= Math.abs(out.get(1)[0]) ) {
                    ellipseParams[2] = Math.abs(out.get(1)[0]);
                    ellipseParams[3] = Math.abs(out.get(0)[0]);
                    //IJ.log("from vec: 2_ "+out.get(1)[2]+" , 1_ "+out.get(1)[1]);
                    ellipseParams[4] = (float) (Math.atan2(out.get(1)[2], out.get(1)[1]) * (180/Math.PI));
                }
                else {
                    ellipseParams[2] = Math.abs(out.get(0)[0]);
                    ellipseParams[3] = Math.abs(out.get(1)[0]);
                    //IJ.log("from vec: 2_ "+out.get(0)[2]+" , 1_ "+out.get(0)[1]);
                    ellipseParams[4] = (float) (Math.atan2(out.get(0)[2], out.get(0)[1]) * (180/Math.PI));
                }

            }
            else {
                ellipseParams[2] = ellipseParams[3] = Math.abs(out.get(0)[0]);
                //IJ.log("from vec (both are equal): 2_ "+out.get(0)[2]+" , 1_ "+out.get(0)[1]);
                ellipseParams[4] = (float) (Math.atan2(out.get(0)[2], out.get(0)[1]) * (180/Math.PI));
            }

        }
        else {
            // there was just two of them
            ellipseParams[2] = 1;
            ellipseParams[3] = 1;
            //IJ.log("just two angle was 0 ");
            ellipseParams[4] = 0;

        }

        return ellipseParams;
    }

    public static ArrayList<float[]> eigen(float a11, float a12, float a21, float a22)
    {

        float a = 1;
        float b = -a11-a22;
        float c = a11*a22 - a12*a21;

        double discriminant = b*b-4*a*c;

        double VERY_SMALL_POSITIVE = 1e-6;

        ArrayList<float[]> out = new ArrayList<float[]>();

        if (discriminant>VERY_SMALL_POSITIVE) {

            float norml, v1_lmb1, v2_lmb1, v1_lmb2, v2_lmb2;

            // 2 distinct real roots
            float lambda1 = (float) ((-b + Math.sqrt(discriminant)) / (2*a));
            float lambda2 = (float) ((-b - Math.sqrt(discriminant)) / (2*a));

            if (a12<VERY_SMALL_POSITIVE && a12 >-VERY_SMALL_POSITIVE) {

                //a12~0
                v1_lmb1 = 0;
                v2_lmb1 = 1;

                v1_lmb2 = -1;
                v2_lmb2 = 0;

            }
            else {

                v1_lmb1 = 1;
                v2_lmb1 = (float) ((lambda1-a11)/a12);
                // normalize them
                norml = (float) Math.sqrt(1+v2_lmb1*v2_lmb1);
                v1_lmb1 = v1_lmb1 / norml;
                v2_lmb1 = v2_lmb1 / norml;

                // lambda2 vectors
                v1_lmb2 = 1;
                v2_lmb2 = (float) ((lambda2-a11)/a12);
                // normalize them
                norml = (float) Math.sqrt(1+v2_lmb2*v2_lmb2);
                v1_lmb2 = v1_lmb2 / norml;
                v2_lmb2 = v2_lmb2 / norml;

            }

            out.add(new float[]{lambda1, v1_lmb1, v2_lmb1});
            out.add(new float[]{lambda2, v1_lmb2, v2_lmb2});

        }
        else if (discriminant<-VERY_SMALL_POSITIVE) {
            // complex roots
            out.add(new float[]{0, 0, 0});

        }
        else {
            // one real root

            float norml, v1_lmb1, v2_lmb1, v1_lmb2, v2_lmb2;

            float lambda1 = (float) ((-b) / (2*a));

            if (a12<VERY_SMALL_POSITIVE && a12 >-VERY_SMALL_POSITIVE) {

                //a12~0
                v1_lmb1 = 0;
                v2_lmb1 = 1;

                v1_lmb2 = -1;
                v2_lmb2 = 0;

            }
            else {

                // lambda1 vectors
                v1_lmb1 = 1;
                v2_lmb1 = (float) ((lambda1-a11)/a12);
                // normalize them
                norml = (float) Math.sqrt(1+v2_lmb1*v2_lmb1);
                v1_lmb1 = v1_lmb1 / norml;
                v2_lmb1 = v2_lmb1 / norml;



            }



            out.add(new float[]{lambda1, v1_lmb1, v2_lmb1});

        }

        return out;

    }

}
