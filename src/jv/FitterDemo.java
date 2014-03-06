package jv;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Plot;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by miroslav on 3/4/14.
 * example how to visualize using bars (short vectors)
 */
public class FitterDemo {

    public static void main (String[] args) {

        System.out.println("NCC fitting demo...");

        // will fit profiles ranging from 0 to 1 (min/max normalized)
        // profile length is Nbins, and they are products of min/max normalization

        int Nbins = 50;
        float[] x          =   new float[Nbins];
        for (int i=0; i<Nbins; i++) x[i] = i;

        Random rand = new Random();

        ImageStack is_input = new ImageStack(528,255);

        ArrayList<float[]> f = new ArrayList<float[]>();

        for (int k=0; k<30; k++) {
            float[] p_rand     =   new float[Nbins];
            for (int i=0; i<Nbins; i++) p_rand[i] = rand.nextFloat();
            f.add(p_rand);
            Plot p = new Plot("", "", "", x, p_rand);
            is_input.addSlice(p.getProcessor());
        }

        for (int k=0; k<30; k++) {
            float[] p_gauss    =   new float[Nbins];
            float mu    = 0.5f * Nbins + ((rand.nextFloat() * 2f) - 1) * 0.25f * Nbins;
            float sig   = rand.nextFloat() * 0.5f * Nbins;
            for (int i=0; i<Nbins; i++) p_gauss[i] = (float) Math.exp(-(i - mu) * (i - mu) / (2 * sig * sig));
            f.add(p_gauss);
            Plot p = new Plot("mu="+mu+",sig="+sig, "", "", x, p_gauss);
            is_input.addSlice(p.getProcessor());
        }

        new ImagePlus("inputs", is_input).show();

        ImageStack is_fit = new ImageStack(528,255);
        float[] ncc_scores = new float[f.size()];

        Fitter fitter = new Fitter(Nbins, true);
        fitter.showTemplates();

        System.out.println("fitting NCC...");
        for (int i=0; i<f.size(); i++) {
            float[] score = fitter.fit(f.get(i), "NCC");
            ncc_scores[i] = score[1];
            Plot p = new Plot("NCC="+ IJ.d2s(score[1],2), "", "", x, f.get(i));
            p.draw();
            p.setColor(Color.RED);
            p.setLineWidth(2);
            p.addPoints(x, fitter.getTemplate(Math.round(score[0])), Plot.LINE);
            is_fit.addSlice("NCC="+ IJ.d2s(score[1],2), p.getProcessor());
        }
        new ImagePlus("fit", is_fit).show();
        System.out.println("done.");

        // show the scores
        float[] xx = new float[ncc_scores.length];
        for (int i=0; i<xx.length; i++) xx[i] = i;
        Plot p = new Plot("", "# input profile", "NCC score", xx, ncc_scores);
        p.setLimits(0, ncc_scores.length, -1, 1);
        p.show();

    }

}
