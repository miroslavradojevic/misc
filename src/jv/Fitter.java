package jv;

import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Plot;

import java.util.ArrayList;

/**
 * Created by miroslav on 3/6/14.
 * class that fits the given 1D profile with library of profiles
 */
public class Fitter {

    int vector_len;
    private static float end_value = 0.2f;

    // template profiles
    private ArrayList<float[]> 	templates;
    private ArrayList<Float> 	templates_mean;
    private ArrayList<float[]>	templates_mean_subtr;
    private ArrayList<Float>    templates_mean_subtr_sumsqr;
    private ArrayList<String>	template_legend;

    float
            middle_idx,
            start_sigma, d_sigma, end_sigma, // slope
            start_width, d_width, end_width, // basic width
            start_shift, d_shift, end_shift; // sift from middle

    public Fitter(int _vector_len, boolean verbose) {

        if (verbose) System.out.println("creating templates...");

        vector_len  = _vector_len;
        middle_idx = (vector_len-1) / 2f;
        // sigma
        start_sigma = vector_len*0.02f;
        d_sigma     = vector_len*0.02f;
        end_sigma   = vector_len*0.12f;
        // width
        start_width = 0;
        d_width     = vector_len*0.05f;
        end_width   = vector_len*0.45f;
        //shift
        start_shift	= -vector_len*0.15f;
        d_shift		= vector_len*0.05f;
        end_shift	= -start_shift;

        templates 			        = new ArrayList<float[]>();
        templates_mean 		        = new ArrayList<Float>();
        templates_mean_subtr        = new ArrayList<float[]>();
        templates_mean_subtr_sumsqr = new ArrayList<Float>();
        template_legend             = new ArrayList<String>();

        for(float width = start_width; width<=end_width; width+=d_width) {

            for (float sigma = start_sigma; sigma <= end_sigma; sigma+=d_sigma) {

                for (float shift = start_shift; shift <= end_shift; shift+=d_shift) {

                    float[] templates_element = new float[vector_len];

                    float boundary_1 = Math.round(middle_idx + shift - width/2);
                    float boundary_2 = Math.round(middle_idx + shift + width/2);

                    for (int i = 0; i < vector_len; i++) {

                        if (i < boundary_1) {
                            double d = boundary_1 - i;
                            templates_element[i] = (float) Math.exp(-Math.pow(d, 2)/(2*sigma*sigma));
                        }
                        else if (i >= boundary_1 && i < boundary_2) {
                            templates_element[i] = 1;
                        }
                        else {
                            float d = i - boundary_2;
                            templates_element[i] = (float) Math.exp(-Math.pow(d, 2)/(2*sigma*sigma));
                        }

                    }

                    // check boundary elements are low enough
                    if (templates_element[0] < end_value && templates_element[vector_len-1] < end_value) {

                        templates.add(templates_element.clone());

                        // calculate mean
                        float mn = 0;
                        for (int ii=0; ii<templates_element.length; ii++) mn += templates_element[ii];
                        mn = mn / (float) templates_element.length;
                        templates_mean.add(mn);

                        // subtract and store in the same array, add the sum as well
                        float sum = 0;
                        for (int aa = 0; aa < vector_len; aa++) {
                            templates_element[aa] = templates_element[aa] - mn;
                            sum += templates_element[aa] * templates_element[aa];
                        }

                        templates_mean_subtr.add(templates_element.clone());
                        templates_mean_subtr_sumsqr.add(sum);
                        template_legend.add("wdt="+width+","+"sig="+sigma+","+"shf="+shift);

                    }

                }

            }

        }

        if (verbose) System.out.println("done. " + templates.size() + " templates.");

    }

    public void showTemplates() {

        ImageStack is_out = new ImageStack(528, 255);

        float[] xaxis = new float[vector_len];
        for (int aa=0; aa<vector_len; aa++) xaxis[aa] = aa;

        for (int aa= 0; aa<templates.size(); aa++) { // plot each template
            Plot p = new Plot("", "idx", "value", xaxis, getTemplate(aa));
            is_out.addSlice(getTemplateLegend(aa), p.getProcessor());
        }

        ImagePlus img_out = new ImagePlus("templates", is_out);
        img_out.show();

    }

    public float[] getTemplate(int template_index) {
        return templates.get(template_index);
    }

    public String getTemplateLegend(int template_index) {
        return template_legend.get(template_index);
    }

    /*
    fitting
     */

    public float[] fit(float[] profile, String mode) {

        // returns the fitting result: (index of the profile, fitting score)
        float[] out = new float[2];
        out[0] = Float.NaN;
        if (
                mode.equalsIgnoreCase("SAD")  ||
                        mode.equalsIgnoreCase("SSD")  ||
                        mode.equalsIgnoreCase("NSAD") ||
                        mode.equalsIgnoreCase("NSSD")
                )
        {
            out[1] = Float.POSITIVE_INFINITY; // looking for smallest
        }
        else if (mode.equalsIgnoreCase("NCC")) {
            out[1] = Float.NEGATIVE_INFINITY;
        }
        else {
            out[1] = Float.NaN;
            return out;
        }

        // loop the templates
        for (int i=0; i<templates.size(); i++) {

            // calculate score
            float curr_score;

            if (mode.equalsIgnoreCase("SAD")) {
                curr_score = sad(profile, templates.get(i));
                if (curr_score < out[1]) {out[0]=i; out[1]=curr_score;}
            }
            else if (mode.equalsIgnoreCase("SSD")) {
                curr_score = ssd(profile, templates.get(i));
                if (curr_score < out[1]) {out[0]=i; out[1]=curr_score;}
            }
            else if (mode.equalsIgnoreCase("NSAD")) {
                curr_score = nsad(profile, templates.get(i));
                if (curr_score < out[1]) {out[0]=i; out[1]=curr_score;}
            }
            else if (mode.equalsIgnoreCase("NSSD")) {
                curr_score = nssd(profile, templates.get(i));
                if (curr_score < out[1]) {out[0]=i; out[1]=curr_score;}
            }
            else if (mode.equalsIgnoreCase("NCC")) {
                curr_score = ncc(profile, templates_mean_subtr.get(i), templates_mean_subtr_sumsqr.get(i));
                if (curr_score > out[1]) {out[0]=i; out[1]=curr_score;}
            }

        }

        return out;

    }

    /*
        sum of absolute differences
     */
    private float sad(float[] f, float[] t) { // both f[] and t[] are normalized 0-1
        float sc = 0;
        for (int aa=0; aa<vector_len; aa++) sc += Math.abs(f[aa] - t[aa]);  // score computation
        return sc;
    }

    /*
        sum of squared differences
     */
    private float ssd(float[] f, float[] t){
        float sc = 0;
        for (int aa=0; aa<vector_len; aa++) sc += (f[aa]-t[aa]) *(f[aa]-t[aa]);
        return sc;
    }

    /*
        normalized cross correlation
     */
    private float ncc(float[] f, float[] t_tM, float sumsqr_t_tM){

        float f_mean = 0;
        for (int i=0; i < f.length; i++) f_mean += f[i];
        f_mean = f_mean / (float)f.length;

        float sc = 0;
        float f_sub_f_mean_sumsqr = 0;

        for (int aa=0; aa<vector_len; aa++) {
            sc += (f[aa]-f_mean) * t_tM[aa]; // important that input f is vector_len length
            f_sub_f_mean_sumsqr += (f[aa]-f_mean) * (f[aa]-f_mean);
        }

        return sc / (float) Math.sqrt(f_sub_f_mean_sumsqr * sumsqr_t_tM);

    }

    /*
        normalized sum of absolute differences
        normalized wrt cumulative sum of the template signal
        0 - absolute fit
        1 - discrepancy is comparable to the amount of signal
     */
    private float nsad(float[] f, float[] t) {
        float sc = 0;
        float norm = 0;
        for (int aa=0; aa<vector_len; aa++) {sc+=Math.abs(f[aa]-t[aa]); norm+=Math.abs(t[aa]);}
        return sc / norm;
    }

    /*
        normalized sum of squared differences
        normalized wrt cumulative sum of the template signal squared
        0 - absolute fit
        1 - discrepancy comparable to the amount of signal (higher differences weighted more)
     */
    private float nssd(float[] f, float[] t) {
        float sc = 0;
        float norm = 0;
        for (int aa=0; aa<vector_len; aa++) {sc+=(f[aa]-t[aa])*(f[aa]-t[aa]); norm+=t[aa]*t[aa];}
        return sc / norm;
    }

}
