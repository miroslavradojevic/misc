package imj;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Line;
import ij.gui.Overlay;
import ij.gui.PointRoi;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import jv.Sorting;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by miroslav on 6/10/14.
 */
public class MeanShiftDirection implements PlugIn {

	float x0=128, y0=128;
	int W = (int)(2*x0);
	int H = (int)(2*y0);

	public void run(String s) {

		int nr_directions = 3;
		int max_iter = 50;
		float epsilon = 0.0001f;
		float alfa = Deg2Rad(20f); // mean-shift kernel neighbourhood measured in angles
		int min_cluster_cnt = 3;

		ArrayList<float[]> vxy = genDirectionsXY(nr_directions);

		/*
			extract number of directions and estimate them: ArrayList<float[]> vxy -> ArrayList<float[]> cls
		 */
		float[][] vxy_conv = meanShift(vxy, max_iter, epsilon, alfa); 				// mean-shift, vxy are normalized
		int[] out_lab = clustering(vxy_conv, alfa); 								// cluster convergences
		ArrayList<float[]> cls = extracting(out_lab, vxy, min_cluster_cnt);      	// extract clusters

		for (int ii=0; ii<cls.size(); ii++) IJ.log("cluster " + ii + " : " + Arrays.toString(cls.get(ii)));

		// visualize clusters
		Overlay init_directions = getDrawing(vxy, .3f*W, null);
		Color[] yellows = new Color[vxy.size()];
		Arrays.fill(yellows, Color.YELLOW);
		Overlay conv_directions = getDrawing(cls, .4f*W, yellows);

		Overlay currentK = new Overlay();  // concatenate overlays
		for (int i=0; i<init_directions.size(); i++) 	currentK.add(init_directions.get(i));
		for (int i=0; i<conv_directions.size(); i++) 	currentK.add(conv_directions.get(i));

		ImagePlus imout = new ImagePlus("", new ByteProcessor(W, H));
		imout.setOverlay(currentK);
		imout.show();

		IJ.log("done.");

	}

	private ArrayList<float[]> genDirectionsXY(int nr_directions)
	{

		ArrayList<Float> thetas = new ArrayList<Float>();
		int[] N = new int[nr_directions];
		float[] mean = new float[nr_directions];
		float[] sigma = new float[nr_directions];

		for(int i = 0; i < nr_directions; i++) {

			Random rd = new Random();

			float min_dist;
			do {
				N[i] = 10 + rd.nextInt(6);
				sigma[i] = .05f + rd.nextFloat() * .15f;
				mean[i] = (float) (rd.nextFloat() * 2*Math.PI);

				min_dist = Float.POSITIVE_INFINITY;
				for (int k = i-1; k>=0; k--) {

					float dist = Math.abs(mean[i]-mean[k]);

					if (dist<min_dist) {
						min_dist = dist;
					}

				}

			} while (min_dist<0.6f);

			for (int j = 0; j < N[i]; j++) {
				float theta_to_add = (float) (rd.nextGaussian() * sigma[i] + mean[i]);
				// wrap it
				while (theta_to_add>=2*Math.PI) {
					theta_to_add -= 2*Math.PI;
				}
				while (theta_to_add<0) {
					theta_to_add += 2*Math.PI;
				}
				thetas.add(theta_to_add);
			}
		}

		ArrayList<float[]> vxy = new ArrayList<float[]>(thetas.size());

		for (int i = 0; i < thetas.size(); i++) {
			float[] vec = new float[2];
			getXY(thetas.get(i), vec);
			vxy.add(vec);
		}

		return vxy;

	}

	private void getXY(float theta, float[] outXY)
	{
		// r is considered to be 1
		outXY[0] = (float) - Math.sin(theta);
		outXY[1] = (float)   Math.cos(theta);
	}

	private	float[][]	meanShift(ArrayList<float[]> v, int max_iter, float epsilon, float alfa) // v are directions, should be normalized
	{

		float[][] v_conv = new float[v.size()][2];
		for (int i = 0; i < v.size(); i++) {
			v_conv[i][0] = v.get(i)[0]; // should be normalized
			v_conv[i][1] = v.get(i)[1];
		}

		// auxiliary variable for iteration (to avoid allocation inside the loop)
		float[] new_v = new float[2];

		for (int i = 0; i < v_conv.length; i++) {
			int iter = 0;
			double d;

			do {

				runOne(v_conv[i], new_v, v, alfa); // new_v is normalized

				float dot_prod = new_v[0] * v_conv[i][0] + new_v[1] * v_conv[i][1];
				dot_prod = (dot_prod>1)? 1 : dot_prod;
				d = Math.acos(dot_prod);

				v_conv[i][0] = new_v[0];
				v_conv[i][1] = new_v[1];

				iter++;
			}
			while (iter < max_iter&& d > epsilon);

		}
		return v_conv;
	}

	// v are unit direction vectors in 2d
	private void runOne(float[] curr_v, float[] new_v, ArrayList<float[]> v, float alfa)
	{

		float sum 	= 0;
		new_v[0] 	= 0;
		new_v[1] 	= 0;

		for (int l = 0; l < v.size(); l++) { // loop all directions
			if (Math.acos(curr_v[0]*v.get(l)[0] + curr_v[1]*v.get(l)[1]) <= alfa) { // if they are unit vectors it is not necessary to divide with norms here
				sum += 1;
				new_v[0] += 1 * v.get(l)[0];
				new_v[1] += 1 * v.get(l)[1];
			}
		}

		new_v[0] /= sum;
		new_v[1] /= sum;
		float norm = (float) Math.sqrt(Math.pow(new_v[0],2)+Math.pow(new_v[1],2));

		if (sum>0 && norm>0) {
			// normalize (because we're mean-shifting directions)
			new_v[0] /= norm;
			new_v[1] /= norm;
		}
		else {
			new_v[0] = curr_v[0];
			new_v[1] = curr_v[1];
		}

	}

	private Overlay getDrawing(ArrayList<float[]> _vxy, float scale, Color[] paints)
	{

		Overlay ovout = new Overlay();
		PointRoi center = new PointRoi(x0+.5f, y0+.5f);
		ovout.add(center);

		for (int i = 0; i < _vxy.size(); i++) {
			PointRoi pt = new PointRoi(x0+scale*_vxy.get(i)[0]+.5f, y0+scale*_vxy.get(i)[1]+.5f);
			pt.setFillColor((paints==null)? Color.WHITE : paints[i]);
			pt.setStrokeColor((paints==null)? Color.WHITE : paints[i]);
			Line direction = new Line(
											 x0+.5f, y0+.5f,
											 x0+scale*_vxy.get(i)[0]+.5f, y0+scale*_vxy.get(i)[1]+.5f
			);
			direction.setFillColor((paints==null)? Color.WHITE : paints[i]);
			direction.setStrokeColor((paints==null)? Color.WHITE : paints[i]);
			direction.setStrokeWidth(2);
			ovout.add(direction);
			ovout.add(pt);
		}

		return ovout;

	}

	private Overlay getDrawing(float[][] _vxy, float scale, Color[] paints)
	{

		Overlay ovout = new Overlay();
		PointRoi center = new PointRoi(x0+.5f, y0+.5f);
		ovout.add(center);

		for (int i = 0; i < _vxy.length; i++) {
			PointRoi pt = new PointRoi(x0+scale*_vxy[i][0]+.5f, y0+scale*_vxy[i][1]+.5f);
			pt.setFillColor((paints==null)? Color.WHITE : paints[i]);
			pt.setStrokeColor((paints==null)? Color.WHITE : paints[i]);
			Line direction = new Line(
											 x0+.5f, y0+.5f,
											 x0+scale*_vxy[i][0]+.5f, y0+scale*_vxy[i][1]+.5f
			);
			direction.setFillColor((paints==null)? Color.WHITE : paints[i]);
			direction.setStrokeColor((paints==null)? Color.WHITE : paints[i]);
			direction.setStrokeWidth(2);
			ovout.add(direction);
			ovout.add(pt);
		}

		return ovout;

	}

	public int[] clustering(float[][] values, float threshold_dists)
	{
		// indxs represent indexes of values that need to be clustered according to their values read before
		// dists are the distances (idxs.length * idxs.length),
		// threshold_dists is the distance limit
		// output is list of unique labels

		int[] labels = new int[values.length];
		for (int i = 0; i < labels.length; i++) labels[i] = i; // each object gets its unique label

//		System.out.println("BEFORE:");
//		System.out.println(Arrays.toString(labels));

		// not really efficient but calculate it here
		float[][] dists = new float[values.length][values.length];
		for (int i = 0; i < values.length; i++) {
			for (int j = i; j < values.length; j++) {
				if(i==j) {
					dists[i][j] = 0;
				}
				else {

					float dot_prod = values[i][0]*values[j][0] + values[i][1]*values[j][1]; // vi * vj
					dot_prod = (dot_prod>1)? 1 : dot_prod;
					float dij = (float) Math.acos(dot_prod);
					dists[i][j] = dij;
					dists[j][i] = dij;
				}
			}
		}
//		for (int i =0 ; i<dists.length; i++) IJ.log(""+Arrays.toString(dists[i]));

		for (int i = 0; i < values.length; i++) {

			// one versus the rest
			for (int j = 0; j < values.length; j++) {

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

//		System.out.println("AFTER:");
//		System.out.println(Arrays.toString(labels));

		return labels;

	}

	public ArrayList<float[]> extracting(int[] labels, ArrayList<float[]> vals, int min_count)
	{

		boolean[] checked = new boolean[labels.length];
		ArrayList<float[]> out = new ArrayList<float[]>();
		ArrayList<Integer> cnt = new ArrayList<Integer>();    		// to make sure that it outputs sorted list

		for (int i = 0; i < labels.length; i++) {
			if (!checked[i]) {

				float centroid_x = vals.get(i)[0]; // idxs[i]
				float centroid_y = vals.get(i)[1]; // idxs[i]
				int count = 1;
				checked[i] = true;

				// check the rest
				for (int j = i+1; j < labels.length; j++) {
					if (!checked[j]) {
						if (labels[j]==labels[i]) {

							centroid_x += vals.get(j)[0]; // idxs[j]
							centroid_y += vals.get(j)[1]; // idxs[j]
							count++;
							checked[j] = true;

						}
					}
				}

				if (count >= min_count) {
					out.add(new float[]{centroid_x/count, centroid_y/count});
					cnt.add(count);
				}

			}
		}


		// print before sorting
		for (int ii = 0; ii < cnt.size(); ii++) {
			IJ.log(ii + " : " + cnt.get(ii) + " points,  at " + Arrays.toString(out.get(ii)));
		}


		// sort by the counts (take from Sorting.java)
		int[] desc_idx = Sorting.descending(cnt);      // it will change cnt list

		ArrayList<float[]> out_sorted = new ArrayList<float[]>(4); // top 4  if there are as many
		int clusters_nr = (desc_idx.length>4)? 4 : desc_idx.length ;
		for (int ii=0; ii<clusters_nr; ii++) {
			out_sorted.add(out.get(desc_idx[ii])); // add top 1,2,3 or 4 directions based on the count
		}
		return out_sorted;

	}

	private static float Deg2Rad(float ang_deg)
	{return (float) ((ang_deg/180f)*Math.PI);}

}
