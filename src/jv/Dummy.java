package jv;

import ij.IJ;

/**
 * Created by miroslav on 3/13/14.
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

    }

}
