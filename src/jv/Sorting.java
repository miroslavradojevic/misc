package jv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by miroslav on 6/11/14.
 * example call:
 * java -cp /home/miroslav/jarlib/*:/home/miroslav/misc/misc_.jar  jv.Sorting
 */
public class Sorting {

	public static void main (String[] args) {
		System.out.println("sorting array");

		float[] a = new float[10];
		Random rd = new Random();
		for (int i = 0; i < a.length; i++) a[i] = rd.nextFloat() * 100; // generate random numbers 0.0 - 100.0

		System.out.println("INPUT ARRAY:");
		System.out.println(Arrays.toString(a));

		System.out.println("ASCENDING ARRAY:");
		int[] ascending_idx = ascending(a);
		System.out.println(Arrays.toString(a));
		System.out.println(Arrays.toString(ascending_idx));

		System.out.println("DESCENDING ARRAY:");
		int[] descending_idx = descending(a);
		System.out.println(Arrays.toString(a));
		System.out.println(Arrays.toString(descending_idx));


		ArrayList<Integer> aa = new ArrayList<Integer>();
		for (int i = 0; i < 10; i++) aa.add(i, rd.nextInt(100)); // generate random numbers 0.0 - 100.0
		System.out.println("INPUT INTEGER ARRAY:");
		for (int i = 0; i < aa.size(); i++) System.out.print(aa.get(i) + "  ");
		System.out.println();
		int[] descending_idx1 = descending(aa);

		System.out.println("DESCENDING ARRAY:");
		for (int i = 0; i < aa.size(); i++) System.out.print(aa.get(i) + "  ");
		System.out.println();
		System.out.println(Arrays.toString(descending_idx1));

	}

	// will sort the input arrays and give out the indexes

	public static int[] ascending(float[] a) {

		// prepare array with indexes first
		int[] idx = new int[a.length];
		for (int i=0; i<idx.length; i++) idx[i] = i;

		for (int i = 0; i < a.length-1; i++) {
			for (int j = i+1; j < a.length; j++) {
				if (a[j]<a[i]) { // asc.
					float temp 	= a[i];
					a[i]		= a[j];
					a[j] 		= temp;

					int temp_idx 	= idx[i];
					idx[i] 			= idx[j];
					idx[j]			= temp_idx;
				}
			}
		}

		return idx;

	}

	public static int[] descending(float[] a) {

		// prepare array with indexes first
		int[] idx = new int[a.length];
		for (int i=0; i<idx.length; i++) idx[i] = i;

		for (int i = 0; i < a.length-1; i++) {
			for (int j = i+1; j < a.length; j++) {
				if (a[j]>a[i]) { // desc.
					float temp 	= a[i];
					a[i]		= a[j];
					a[j] 		= temp;

					int temp_idx 	= idx[i];
					idx[i] 			= idx[j];
					idx[j]			= temp_idx;
				}
			}
		}

		return idx;

	}

	public static int[] descending(ArrayList<Integer> a) {

		// prepare array with indexes first
		int[] idx = new int[a.size()];
		for (int i=0; i<idx.length; i++) idx[i] = i;

		for (int i = 0; i < a.size()-1; i++) {
			for (int j = i+1; j < a.size(); j++) {
				if (a.get(j)>a.get(i)) { // desc.
					int temp 	= a.get(i);
					a.set(i, a.get(j));
					a.set(j, temp);

					int temp_idx 	= idx[i];
					idx[i] 			= idx[j];
					idx[j]			= temp_idx;
				}
			}
		}

		return idx;

	}

}
