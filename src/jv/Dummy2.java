package jv;

import ij.gui.Plot;

/**
 * Created by miroslav on 24-6-14.
 */
public class Dummy2 {

    public static void main(String[] args){

        System.out.println("percentile");

        Plot p = new Plot("", "", "");
        p.setLimits(0, 100, 0, 20);

        for (int i = 1; i < 100; i++) {

            int t20 = Math.round((20f*i)/100f);
            t20 = (t20< 1)? 1  : t20;
            t20 = (t20>19)? 19 : t20;

            System.out.println("percentile " + i + " -> " + get20(i) + "/20");
            p.addPoints(new float[]{i}, new float[]{t20}, Plot.CROSS);

        }

        p.show();

    }

    private static int get20(int percentile)
    {
        percentile = (percentile< 1)?  1 : percentile;
        percentile = (percentile>99)? 99 : percentile;
        int t20 = Math.round((20f*percentile)/100f);
        t20 = (t20< 1)? 1  : t20;
        t20 = (t20>19)? 19 : t20;
        return t20;
    }

}
