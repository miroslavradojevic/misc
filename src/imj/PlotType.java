package imj;

import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Plot;
import ij.plugin.PlugIn;
import ij.process.ColorProcessor;

import java.awt.*;
import java.util.Random;

/**
 * Created by miroslav on 23-6-14.
 * java -jar ~/jarlib/ij.jar -ijpath ~/ImageJ/plugins/ -run "Plot Type"
 */
public class PlotType implements PlugIn {

    public void run(String s) {

        System.out.println("Which type is the plot processor?");
        float[] x = new float[100];
        float[] y = new float[100];

        Random rd = new Random();
        for (int i = 0; i < x.length; i++) x[i] = rd.nextFloat();
        for (int i = 0; i < y.length; i++) y[i] = rd.nextFloat();

        Plot p = new Plot("", "", "");
        p.setLimits(0, 1, 0, 1);
        p.setColor(Color.RED);
        p.addPoints(x, y, Plot.CIRCLE);
        //p.show();

        System.out.println("grayscale? " + p.getProcessor().isGrayscale());
        System.out.println("bit depth: " + p.getProcessor().getBitDepth());

//        ColorProcessor cp = new ColorProcessor(p.getImagePlus().getImage());

        ImageStack is_test = new ImageStack(528, 255);
        is_test.addSlice("1", p.getProcessor());
        is_test.addSlice("2", p.getProcessor());
        is_test.addSlice("3", p.getProcessor());
        is_test.addSlice("4", p.getProcessor());
        new ImagePlus("BEFORE", is_test).show();


        is_test.deleteSlice(4);
        new ImagePlus("AFTER", is_test).show();


    }
}
