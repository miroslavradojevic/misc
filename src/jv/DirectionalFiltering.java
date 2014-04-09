package jv;

import ij.gui.Plot;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.util.Arrays;

/**
 * Created by miroslav on 4/8/14.
 * demo for options on directional filtering:
 * normalized gaussian function and mexican hat (normalized second derivative of gaussian function)
 * java -cp "$HOME/misc/*:$HOME/jarlib/*" jv.DirectionalFiltering
 */
public class DirectionalFiltering {

    public static void main (String[] args) {

        System.out.println("Demo on directional filtering...");

        int N = 101;
        float D = 4;
        float sigma = D/6;

        float[] xx = new float[N];
        float[] gauss = new float[N];
        float[] mexican_hat = new float[N];
        float min_plot = Float.POSITIVE_INFINITY;
        float max_plot = Float.NEGATIVE_INFINITY;
        for (int i=0; i<N; i++) {
            xx[i] = -D/2 + i* D/(N-1);       // dist from center (center is 0)
            gauss[i] = (float) Math.exp(-(xx[i]*xx[i])/(2*sigma*sigma)); // without normalization
            mexican_hat[i] = (float) ((1 - (xx[i]*xx[i])/(sigma*sigma)) * Math.exp(-(xx[i]*xx[i])/(2*sigma*sigma)));
            min_plot = (gauss[i]<min_plot)? gauss[i] : min_plot;
            min_plot = (mexican_hat[i]<min_plot)? mexican_hat[i] : min_plot;
            max_plot = (gauss[i]>max_plot)? gauss[i] : max_plot;
            max_plot = (mexican_hat[i]>max_plot)? mexican_hat[i] : max_plot;

        }

//        System.out.println(Arrays.toString(xx));
//        System.out.println(Arrays.toString(gauss));

        Plot p = new Plot("", "d", "");
        p.setLimits(-D/2, D/2, min_plot, max_plot);
        p.addPoints(xx, gauss, Plot.LINE);
        p.draw();
        p.setColor(Color.GREEN);
        p.addPoints(xx, mexican_hat, Plot.LINE);
        p.show();


    }


}
