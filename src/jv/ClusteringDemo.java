package jv;

import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.Plot;
import ij.io.FileSaver;
import ij.process.ByteProcessor;

import java.awt.*;
import java.awt.Color;
import java.lang.System;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

public class ClusteringDemo {

	Vector<float[]> disks;
	static int N = 30;

    public static void main (String[] args) {

		float minx = 20,    miny = 20;
		float maxx = 100,   maxy = 100;
		float minr = 2,     maxr = 5;

		int H = 120, W = 120;

		// table with colors
		Color[] 		c = new Color[N];
		for (int i = 0; i < N; i++) c[i] = getRandomColor();

		float[][] p = new float[N][];

		// fille them up
		for (int i = 0; i < N; i++) {
			float x = minx + new Random().nextFloat() * (maxx-minx);
			float y = miny + new Random().nextFloat() * (maxy-miny);
			float r = minr + new Random().nextFloat() * (maxr-minr);
			p[i] = new float[]{x, y, r};
		}

		System.out.println("#########################");
		System.out.println("#### TEST 1 ####");
		System.out.println("#########################");

		// input disks 2d
		Vector<float[]> disks = new Vector<float[]>(N);

		for (int i = 0; i < N; i++) {
			disks.add(p[i]);
		}

		System.out.println("\ncreated "+disks.size()+" points:\n");
		int[] lab = clustering(disks);
		System.out.println(Arrays.toString(lab));

		ByteProcessor ip = new ByteProcessor(W, H);
		ImagePlus im = new ImagePlus("clustering2d", ip);

		Overlay ov = new Overlay();
		ov.drawNames(true);

		for (int i = 0; i < N; i++) {
			OvalRoi ovRoi = new OvalRoi(p[i][0]-p[i][2]+.5, p[i][1]-p[i][2]+.5, 2*p[i][2], 2*p[i][2]);
			ovRoi.setStrokeColor(c[lab[i]]);
			ovRoi.setName(Integer.toString(lab[i]));
			ov.add(ovRoi);
		}

		im.setOverlay(ov);
		im.show();

		ImageCanvas cnv = im.getCanvas();
		cnv.zoomIn(0, 0);
		cnv.zoomIn(0, 0);
		cnv.zoomIn(0, 0);
		cnv.zoomIn(0, 0);
		cnv.zoomIn(0, 0);

		// save it
		FileSaver fs = new FileSaver(im);
		fs.saveAsTiff("clustering2d.tif");

        /*
        example with indexes & inter-distances
         */

        System.out.println("#########################");
        System.out.println("#### TEST 2 ####");
        System.out.println("#########################");

        // random numbers in some interval, each has a value and index associated
		int N = 15;
		int RANGE = 40;
        int[] values = new int[N];
		int[] values_idx = new int[N];
        for (int i = 0; i < N; i++) {
            values[i] = new Random().nextInt(RANGE); // 0%(RANGE-1)
			values_idx[i] = i;
        }
		System.out.println("\nINPUT:\n");
		System.out.println(Arrays.toString(values));
		System.out.println("\nINDEXES:\n");
		System.out.println(Arrays.toString(values_idx));

        // table with precomputed inter-distances will be necessary for cluster mechanism
        int[][] dists = new int[N][N];
		for (int i=0; i< N; i++) {
			for (int j = i; j < N; j++) {
				if(i==j) {
					dists[i][j] = 0;
				}
				else {
					dists[i][j] = Math.abs(values[i]-values[j]);
					dists[j][i] = Math.abs(values[i]-values[j]);
				}

			}
		}
		System.out.println("\nDISTS:\n");
		for (int k = 0; k < dists.length; k++) {
			System.out.println(Arrays.toString(dists[k]));
		}

		// cluster labels
		int[] lab2 = clustering(values_idx, dists, 5);

		// show clusters in plot
		float[] plot_x = new float[values.length];
		for (int i=0; i<values.length; i++) plot_x[i] = (float) values[i];
		float[] plot_y = new float[values.length];

		Plot pl = new Plot("", "samples", "", new float[1], new float[1]);
		pl.setLimits(0, RANGE, 0, 1.2);
		Arrays.fill(plot_y, 0);
		pl.addPoints(plot_x, plot_y, Plot.X);
		Arrays.fill(plot_y, 1);
		pl.addPoints(plot_x, plot_y, Plot.X);

		// plot vertical Line


		plot_y = new float[]{0, 1};
		plot_x = new float[]{values[0], values[0]};
		pl.setColor(Color.BLUE);
		pl.addPoints(plot_x, plot_y, Plot.LINE);


//		pl.drawArrow(values[0], 0, values[0], 1, 2);
//		pl.drawArrow(values[0]+1, 0, values[0], 1, 2);
//		pl.drawArrow(values[0] + 10, 10, values[0] + 10, 15, 2);
		pl.show();

		System.out.println("done.");

    }

	private static Color getRandomColor(){
		int choose = new Random().nextInt(10);
		switch (choose) {
			case 0: return Color.RED;
			case 1: return Color.GREEN;
			case 2: return Color.WHITE;
			case 3: return Color.ORANGE;
			case 4: return Color.PINK;
			case 5: return Color.YELLOW;
			case 6: return Color.GRAY;
			case 7: return Color.MAGENTA;
			case 8: return Color.CYAN;
			case 9: return Color.BLUE;
			default:return Color.BLACK;
		}

	}

	public static int[] clustering(int[] idxs, int[][] dists, int threshold_dists)
	{
		// indxs represent indexes of values that need to be clustered,
		// dists are the distances,
		// threshold_dists is the distance limit
		// output is list of unique labels
		int[] labels = new int[idxs.length];
		for (int i = 0; i < labels.length; i++) labels[i] = i;

		System.out.println("INIT. LABELS:");
		System.out.println(Arrays.toString(labels));

		for (int i = 0; i < idxs.length; i++) {

			// one versus the rest
			for (int j = 0; j < idxs.length; j++) {

				//
				if (i != j) {

					if (dists[i][j]<=threshold_dists) {

						if (labels[j] != labels[i]) {

							int currLabel = labels[j];
							int newLabel  = labels[i];

							labels[j] = newLabel;

							//set all that also were currLabel to newLabel
							for (int k = 0; k < labels.length; k++)
								if (labels[k]==currLabel)
									labels[k] = newLabel;

						}

					}

				}

			}

		}

		System.out.println("OUT LABELS:");
//		for (int ii = 0; ii < labels.length; ii++)
//			System.out.print(labels[ii]+" ");
		System.out.println(Arrays.toString(labels));

		return labels;

	}

	public static int[] clustering(Vector<float[]> disks) //[x, y, r]  // Vector<float[]>
	{

		int[] labels = new int[disks.size()];
		for (int i = 0; i < labels.length; i++) labels[i] = i;

		System.out.println("INIT. LABELS:");
		for (int i = 0; i < labels.length; i++)
			System.out.print(labels[i]+" ");
		System.out.println();

		for (int i = 0; i < disks.size(); i++) {

			// one versus the rest
			for (int j = 0; j < disks.size(); j++) {

				if (i!=j) {

					double dst2 	= Math.pow(disks.get(i)[0]-disks.get(j)[0], 2) + Math.pow(disks.get(i)[1]-disks.get(j)[1], 2);
					double rd2 		= Math.pow(disks.get(i)[2]+disks.get(j)[2], 2);
					if (dst2<=rd2) {  // they are neighbours

						if (labels[j]!=labels[i]) {

							int currLabel = labels[j];
							int newLabel  = labels[i];

							labels[j] = newLabel;

							//set all that also were currLabel to newLabel
							for (int k = 0; k < labels.length; k++)
								if (labels[k]==currLabel)
									labels[k] = newLabel;

						}

					}

				}

			}

		}

		System.out.println("OUT LABELS:");
		for (int ii = 0; ii < labels.length; ii++)
			System.out.print(labels[ii]+" ");
		System.out.println();

		return labels; // cluster labels for each disc

	}

}
