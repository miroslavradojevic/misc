package imj;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Line;
import ij.gui.Overlay;
import ij.gui.Plot;
import ij.gui.PointRoi;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import weka.clusterers.SimpleKMeans;
import weka.core.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by miroslav on 6/6/14.
 */
public class KMeansDirection implements PlugIn {

	float x0=128, y0=128;
	int W = (int)(2*x0);
	int H = (int)(2*y0);

	public void run(String s) {

		// single cases, with visualization
		kMeansClusteringExperiment(3, 3, true); // generate 3 directions, use 3 clusters
		kMeansClusteringExperiment(3, 4, true); // use 4 clusters
		//
		kMeansClusteringExperiment(4, 3, true);
		kMeansClusteringExperiment(4, 4, true);

		// simulate what happens on number of experimants
		int NrCases = 100; // generate cases with NN branches

		float[] xaxis = new float[NrCases];// for plotting
		for (int i =0; i < xaxis.length; i++) xaxis[i] = i;

		float[] ratio = new float[NrCases];
		float min_ratio = Float.POSITIVE_INFINITY;
		float max_ratio = Float.NEGATIVE_INFINITY;

		for (int ll = 0; ll<NrCases; ll++) {
			float err_k = kMeansClusteringExperiment(3, 3, false);
			float err_k1 = kMeansClusteringExperiment(3, 4, false);
			ratio[ll] = (err_k - err_k1)/err_k1; //; // / err_k1;  //  - err_k1

			if (ratio[ll]>max_ratio) max_ratio = ratio[ll];
			if (ratio[ll]<min_ratio) min_ratio = ratio[ll];

		}

		Plot p = new Plot("", "", "");
		p.setLimits(0, NrCases-1, min_ratio, max_ratio);
		p.addPoints(xaxis, ratio, Plot.LINE);
		p.show();

	}

	private float getTheta(float x, float y) {
		return (float) Math.atan2(y, x);
	}

	private void getXY(float theta, float[] outXY){
		// r is considered to be 1
		outXY[0] = (float) - Math.sin(theta);
		outXY[1] = (float)   Math.cos(theta);
	}

	private Overlay getDrawing(ArrayList<float[]> _vxy, float scale, Color[] paints) {

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

	private Overlay getDrawing(float[][] _vxy, float scale, Color[] paints) {

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

	private void show(ArrayList<float[]> _vxy, float scale, Color[] paints) {

		Overlay ovout = getDrawing(_vxy, scale, paints);

		ImagePlus imout = new ImagePlus("", new ByteProcessor(W, H));
		imout.setOverlay(ovout);
		imout.show();

	}

	private void show(float[][] _vxy, float scale, Color[] paints) {

		Overlay ovout = getDrawing(_vxy, scale, paints);

		ImagePlus imout = new ImagePlus("", new ByteProcessor(W, H));
		imout.setOverlay(ovout);
		imout.show();

	}

	private Color getRandomColor(){

		Random  random_color_gen = new Random();

		float choose_R = random_color_gen.nextFloat();
		float choose_G = random_color_gen.nextFloat();
		float choose_B = random_color_gen.nextFloat();

		return new Color(choose_R, choose_G, choose_B);

	}

	private Color[] getRandomColors(int Nr){

		Color[] out = new Color[Nr];

		for (int i = 0; i < Nr; i++) {
			out[i] = getRandomColor();
		}

		return out;

	}

	private Instances readDirections(ArrayList<float[]> _vxy) {

		// declare attributes (vector coordinates)
		Attribute vx = new Attribute("vx");
		Attribute vy = new Attribute("vy");

		// declare feature vector (2d coordinate)
		FastVector att = new FastVector(2);
		att.addElement(vx);
		att.addElement(vy);

		// declare & fill train set up with instances
		Instances dirs_inst = new Instances("vxy", att, 10); // initialize with 10 instances
		for (int i = 0; i < _vxy.size(); i++) {
			// create the instance
			Instance iExample = new Instance(2);
			iExample.setValue((Attribute)att.elementAt(0), _vxy.get(i)[0]);
			iExample.setValue((Attribute)att.elementAt(1), _vxy.get(i)[1]);
			dirs_inst.add(iExample); // add the instance
		}

		return dirs_inst;
	}

	private SimpleKMeans kMeansDirections(Instances _directions2d, int _N) {

		// create the model
		SimpleKMeans kMeans = new SimpleKMeans();
		try {
			kMeans.setNumClusters(_N);
			kMeans.buildClusterer(_directions2d);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return kMeans;

	}

	private int[] clusterDirections(ArrayList<float[]> _vxy, int _N) throws Exception {

		Instances directions2d = readDirections(_vxy);
		SimpleKMeans kMeans = kMeansDirections(directions2d, _N);

		// get cluster membership for each instance
		int[] out = new int[directions2d.numInstances()];
		for (int i = 0; i < directions2d.numInstances(); i++) {
			try {
//				IJ.log(directions2d.instance(i) + " is cluster " + (kMeans.clusterInstance(directions2d.instance(i)) + 1));
				out[i] = kMeans.clusterInstance(directions2d.instance(i));  // store cluster index
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return out;

	}

	/*
		output cluster directions
	 */
	private float getClusterDirections(ArrayList<float[]> _vxy, int _N, float[][] _out_directions) throws Exception {

		Instances directions2d = readDirections(_vxy);
		SimpleKMeans kMeans = kMeansDirections(directions2d, _N);

//		IJ.log("K=" + _N + " means squared error " + IJ.d2s(kMeans.getSquaredError(),2));
//		Instances cluster_stds = kMeans.getClusterStandardDevs();
//		IJ.log(""+ cluster_stds.numInstances());
//		for (int ii=0; ii<cluster_stds.numInstances(); ii++) {
//			IJ.log("cluster " + ii + " standard dev: " + IJ.d2s(cluster_stds.instance(ii).value(0),2));
//		}

		Instances centroids = kMeans.getClusterCentroids();      // extract centroids

		// get direction of every cluster
		//float[][] out_directions = new float[_N][2];
		for (int i = 0; i < centroids.numInstances(); i++) {
			_out_directions[i][0] = (float) centroids.instance(i).value(0);
			_out_directions[i][1] = (float) centroids.instance(i).value(1);

			// normalize (we're working with unit directions)
			float norm = (float) Math.sqrt( Math.pow(_out_directions[i][0],2) + Math.pow(_out_directions[i][1],2) );
			_out_directions[i][0] /= norm;
			_out_directions[i][1] /= norm;

		}

		return (float) kMeans.getSquaredError();

	}

	/*
		the experiment where you generate nr. of directions (_genK) and try to cluster them with nr. of clusters (_detK)
	 */
	private float kMeansClusteringExperiment(int _genK, int _detK, boolean verbose)
	{

		ArrayList<Float> thetas = new ArrayList<Float>();

		int[] N = new int[_genK];
		float[] mean = new float[_genK];
		float[] sigma = new float[_genK];

		for(int i = 0; i < _genK; i++) {

			Random rd = new Random();

			float min_dist;
			do {
				N[i] = 10 + rd.nextInt(6);
				sigma[i] = .05f + rd.nextFloat() * .05f;
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

		//show(vxy, .45f*W, null); // all same color
		Overlay all = getDrawing(vxy, .3f*W, null);




		float[][] cluster_directions = new float[_detK][2];
		float sqerr = Float.POSITIVE_INFINITY;


		for (int runIdx = 0; runIdx<10; runIdx++) {    // try some times

			float curr_sqerr = Float.POSITIVE_INFINITY;
			float[][] curr_cluster_directions = new float[_detK][2];

			try {

				sqerr = getClusterDirections(vxy, _detK, cluster_directions); // result will be stored in cluster_directions

			} catch (Exception e) {
				e.printStackTrace();
			}
			if (curr_sqerr<sqerr) {

				sqerr = curr_sqerr;

				for (int i=0; i<_detK; i++) {
					for (int j = 0; j < 2; j++) {
						cluster_directions[i][j] = curr_cluster_directions[i][j];
					}
				}

			}

		}

		// add cluster directions
		Color[] cluster_col = new Color[_detK];
		for (int i = 0; i < _detK; i++) cluster_col[i] = getRandomColor();
		//show(cluster_directions, .4f * W, cluster_col);
		Overlay cluster_direction = getDrawing(cluster_directions, .4f * W, cluster_col);


		Overlay direction_labels = new Overlay();
		if (false) {
			// labelling direciton clusters
			int[] lab = new int[vxy.size()];
			try {
				lab = clusterDirections(vxy, _detK);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Color[] direction_color = new Color[vxy.size()];
			for (int i = 0; i < vxy.size(); i++) direction_color[i] = cluster_col[lab[i]];
			//show(vxy, .3f*W, direction_color);
			direction_labels = getDrawing(vxy, .2f*W, direction_color);
		}







		// concatenate overlays
		Overlay currentK = new Overlay();
		for (int i=0; i<all.size(); i++) 				currentK.add(all.get(i));
		for (int i=0; i<cluster_direction.size(); i++) 	currentK.add(cluster_direction.get(i));
		for (int i=0; i<direction_labels.size(); i++) 	currentK.add(direction_labels.get(i));


		if (verbose) {

			ImagePlus imout = new ImagePlus("", new ByteProcessor(W, H));
			imout.setTitle("K=" + _detK + ", sqerr=" + IJ.d2s(sqerr,2));
			imout.setOverlay(currentK);
			imout.show();

		}

		return sqerr;

	}

}