package jv;

import ij.gui.Plot;

/**
 * Created by miroslav on 3/4/14.
 * example how to visualize using bars (short vectors)
 */
public class DrawErrorBars {

    public static void main (String[] args) {

        System.out.println("drawing error bars...");

        // x axis
        int Nbins = 8;
        float[] xx=  new float[Nbins];
        float[] yy = new float[Nbins];
        for (int i=0; i<Nbins; i++) {
            xx[i] = i * (1 / (float)(Nbins-1));
            yy[i] = i * (1 / (float)(Nbins-1));
        }

        Plot p = new Plot("bar plot", "nr", "value");
        p.setLimits(0, 1, 0, 1);
        p.addPoints(xx, yy, Plot.BOX);
        p.addErrorBars(yy);
        p.show();

    }

}
