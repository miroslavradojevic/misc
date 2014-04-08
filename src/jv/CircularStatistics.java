package jv;

import ij.gui.Plot;

/**
 * Created by miroslav on 4/8/14.
 * demonstrates some concepts in circular statistics - calculating circular moments
 * form profiles - 1st order, second order circ. moments, kurtosis, etc...
 * java -cp "$HOME/misc/*:$HOME/jarlib/*" jv.CircularStatistics
 */
public class CircularStatistics {

    public static void main (String[] args) {

        System.out.println("Demo on directional statistics...");

        int[] profile = new int[]{40, 22, 20, 9, 6, 3, 3, 1, 6, 3, 11, 22, 24, 58, 136, 138, 143, 69}; // how the profile would look
        System.out.println("#intervals " + profile.length);

        int sum = 0;
        for (int i=0; i<profile.length; i++) sum += profile[i];
        System.out.println("total birds " + sum);

        float angle_step_deg = 360f / profile.length;
        float angle_step_rad = angle_step_deg * ((float)Math.PI/180f);
        System.out.println("angle step " + angle_step_deg);

        float[] profile_distribution = new float[profile.length];
        for (int i=0; i<profile.length; i++) profile_distribution[i] = (float) profile[i] / sum;

        // show angular distribution
        float[] theta_deg   = new float[profile.length];
        float[] theta       = new float[profile.length];
        for (int i=0; i< profile.length; i++) {
            theta_deg[i]    = i * angle_step_deg + angle_step_deg/2;
            theta[i]        = i * angle_step_rad + angle_step_rad/2;
        }
        Plot p = new Plot("angular profile", "angle", "p-ty distribution", theta_deg, profile_distribution);
        p.show();

        /*
        extract circular statistics to describe the profile
         */


        float theta_mean = Float.NaN;
        float a1_prim = 0, b1_prim = 0, a2_prim = 0, b2_prim = 0, a2 = 0, b2 = 0;
        for (int i=0; i<profile.length; i++) {
            a1_prim += profile_distribution[i] * Math.cos(theta[i]);
            // todo continue...
        }
        System.out.print("mean direction = ");
        System.out.println(""+theta_mean);



    }


}
