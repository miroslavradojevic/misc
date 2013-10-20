package ij;

import ij.IJ;
import ij.plugin.PlugIn;

/**
 * Created with IntelliJ IDEA.
 * User: miroslav
 * Date: 8/15/13
 * Time: 10:06 AM
 */
public class SimplePlugIn implements PlugIn {
	public void run(String s) {
		IJ.log("run()");
		System.out.println("run()");
	}
}
