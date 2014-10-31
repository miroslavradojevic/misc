package jv;

import java.util.Arrays;

/**
 * Created by miroslav on 13-10-14.
 * java -cp "$HOME/misc/*:$HOME/jarlib/*" jv.Dummy3
 */
public class Dummy3 {

    public static void main(String[] args){
        System.out.println("test switch...");


        for (int nr = 0; nr < 10; nr++) {

            System.out.print(nr + " -> ");

            switch(nr)
            {
                case 0:
                    System.out.println("0");
                    break;
                case 1:
                    System.out.println("1");
                    break;
                case 2:
                case 3:
                    System.out.println("3");
                    break;
                default:
            }


        }

        System.out.println("");

        float arr1[][] = {{0,1}, {2,3}, {4,5}, {6,7}, {8,9}, {10,11}};
        float arr2[][] = new float[3][2]; // just allocate

        // copies an array from the specified source array
        System.arraycopy(arr1, 0, arr2, 0, 3); // src, src_pos, dest, dest_pos, len

        System.out.println("arr1: " + Arrays.toString(arr1));
        plot2(arr1);
        System.out.println("arr2: " + Arrays.toString(arr2));
        plot2(arr2);

    }

    private static void plot2(float[][] a) {
        for (int i = 0; i < a.length; i++) {
            System.out.println(Arrays.toString(a[i]));
        }
    }

}
