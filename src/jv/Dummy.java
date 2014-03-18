package jv;

import ij.IJ;

import java.util.Arrays;

/**
 * Created by miroslav on 3/13/14.
 * java -cp "$HOME/misc/*:$HOME/jarlib/*" jv.Dummy
 */
public class Dummy {

    public static void main(String[] args){

        float number1 = Float.NaN;
        float number2 = 5.6f;
        float number3 = 3*Float.MIN_VALUE;

        IJ.log("printout of a number1: " + number1);
        IJ.log("printout of a number2: " + number2);

        IJ.log("is number1 a NaN? " + Float.isNaN(number1));
        IJ.log("is number2 a NaN? " + Float.isNaN(number2));
        IJ.log("MIN_VALUE" + number3);

        // array with different size vectors
        float[][][] ar = new float[1][4][];
        ar[0][0] = new float[5];
        ar[0][1] = new float[2];
        ar[0][2] = null;
        ar[0][3] = null;

        IJ.log(Arrays.toString(ar[0][0]));
        IJ.log(Arrays.toString(ar[0][1]));
        IJ.log(Arrays.toString(ar[0][2]));
        IJ.log(Arrays.toString(ar[0][3]));

    }

}
