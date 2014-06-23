package jv;

import java.util.ArrayList;

/**
 * Created by miroslav on 6/21/14.
 */
public class Dummy1 {

	public static void main(String[] args){
		System.out.println("test void funciton call..");

		ArrayList<ArrayList<float[]>> ls = new ArrayList<ArrayList<float[]>>();
		ls.add(new ArrayList<float[]>(1));
		ls.add(new ArrayList<float[]>(1));
		ls.add(new ArrayList<float[]>(1));

		System.out.println("---");
		for (int i = 0; i < ls.size(); i++)
			System.out.println("list member " + i + " : " + ls.get(i).size() + " elements");

		add_to_first(ls);

		System.out.println("---");
		for (int i = 0; i < ls.size(); i++)
			System.out.println("list member " + i + " : " + ls.get(i).size() + " elements");

		add_to_second(ls);

		System.out.println("---");
		for (int i = 0; i < ls.size(); i++)
			System.out.println("list member " + i + " : " + ls.get(i).size() + " elements");

		clean_first(ls);

		System.out.println("---");
		for (int i = 0; i < ls.size(); i++)
			System.out.println("list member " + i + " : " + ls.get(i).size() + " elements");

		float[][][] aa = new float[10][][];



	}

	private static void add_to_first(ArrayList<ArrayList<float[]>> arg)
	{
		arg.get(0).add(new float[]{1,2});
	}

	private static void add_to_second(ArrayList<ArrayList<float[]>> arg)
	{
		arg.get(1).add(new float[]{1,2});
	}

	private static void clean_first(ArrayList<ArrayList<float[]>> arg)
	{
		arg.get(0).clear();
	}

}
