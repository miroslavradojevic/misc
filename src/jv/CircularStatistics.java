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
            theta[i]        = i * angle_step_rad + angle_step_rad/2;   // data points from the interval are from the middle of that interval
        }
        Plot p = new Plot("angular profile", "angle", "p-ty distribution", theta_deg, profile_distribution);
        p.show();

        /*
        extract circular statistics to describe the profile
        with different measures
         */

		float a1 = 0, b1 = 0, a2 = 0, b2 = 0;
        for (int i=0; i<profile.length; i++) {
            a1 += profile_distribution[i] * Math.cos(1*theta[i]);
            b1 += profile_distribution[i] * Math.sin(1*theta[i]);
			a2 += profile_distribution[i] * Math.cos(2*theta[i]);
			b2 += profile_distribution[i] * Math.sin(2*theta[i]);
        }

		System.out.println("a1,b1="+a1+","+b1);
		System.out.println("a2,b2="+a2+","+b2);

		float R_ = (float) Math.sqrt(Math.pow(a1,2)+Math.pow(b1,2)); // mean resultant length
		float R_2 = (float) Math.sqrt(Math.pow(a2,2)+Math.pow(b2,2));

		System.out.println("R_,R_2="+R_+","+R_2);

		float theta_ = Float.NaN;
		if (R_>Float.MIN_VALUE) {
			if (a1>=0) theta_ = (float) Math.atan(b1 / a1);
			else theta_ = (float) (Math.atan(b1 / a1) + Math.PI);
		}

		float theta_2 = Float.NaN;
		if (R_2>Float.MIN_VALUE) {
			if (a2>=0) theta_2 = (float) Math.atan(b2 / a2);
			else theta_2 = (float) (Math.atan(b2 / a2) + Math.PI);
		}

		theta_ 	= wrap_0_2PI(theta_); // wrap theta [0, 2pi) range
		theta_2 = wrap_0_2PI(theta_2); // wrap theta [0, 2pi) range

		float a2_ = 0, b2_ = 0;
		for (int i=0; i<profile.length; i++) {
			a2_ += profile_distribution[i] * Math.cos(2*(theta[i]-theta_));
			b2_ += profile_distribution[i] * Math.sin(2*(theta[i]-theta_));
		}

		System.out.println("a2_, b2_ ="+a2_+","+b2_);


		System.out.println("theta_ \t= "+theta_*(180f/Math.PI)+", theta_2 = "+theta_2*(180f/Math.PI));
		System.out.println("R_ \t= " + R_ + ", V (circular variance) = " + (1-R_) + ", circular std. = "+Math.sqrt(-2*Math.log(R_)));

		// concentration and dispersion
		// R_ measures if they are tightly clustered or dispersed or periodical (concentration measure)

		float[] alfa = new float[18];
		float[] D = new float[alfa.length];
		float[] d0 = new float[alfa.length];
		for (int i = 0; i < alfa.length; i++) {

			alfa[i] = (float) (i * (2*Math.PI / alfa.length));

			D[i] = 0;// dispersion about given angle alfa version 1
			for (int ii=0; ii<profile.length; ii++) {
				D[i] += profile_distribution[ii] * (1 - Math.cos(theta[ii]-alfa[i]));
			}

			d0[i] = 0;
			for (int ii=0; ii<profile.length; ii++) {
				d0[i] += profile_distribution[ii] * (Math.PI - Math.abs(Math.PI - Math.abs(theta[ii]-alfa[i])) );
			}

		}

		Plot pD 	= new Plot("dispersion 1", "alfa", "D(alfa)", alfa, D);
		pD.show();
		Plot pd0 	= new Plot("dispersion 2", "alfa", "d0(alfa)", alfa, d0);
		pd0.show();

		// sample circular dispersion
		System.out.println("sample circular dispersion: "+((1-R_2)/(2*R_*R_)));

		// skewness
		System.out.println("skewnwness: " + ( (R_2*Math.sin(theta_2-2*theta_)) / Math.pow(1-R_, 3f/2)));

		// kurtiosis
		System.out.println("kurtosis: " + (R_2*Math.cos(theta_2-2*theta_)-Math.pow(R_,4)) / Math.pow(1-R_, 2) );

    }

	private static float wrap_0_2PI(float ang) {
		float out = ang;
		while (out<0) {
			out += 2*Math.PI;
		}
		while (out>=2*Math.PI) {
			out -= 2*Math.PI;
		}
		return out;
	}


}
