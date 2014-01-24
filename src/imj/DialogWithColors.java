package imj;

import ij.IJ;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;

import java.awt.*;
import java.util.Vector;

/**
 * Created by miroslav on 1/24/14.
 */
public class DialogWithColors implements PlugIn {

    public void run(String s) {

        IJ.log("test dialog with colors...");
        System.out.println("test dialog with colors...");

        GenericDialog gd = new GenericDialog("Test dialog");
        gd.addNumericField("parameter: ", 56, 2, 15, "units");
        Vector v = gd.getNumericFields();
        TextField tf = (TextField)v.get(0);
        tf.setBackground(Color.YELLOW);
        tf.setForeground(Color.RED);
        gd.addMessage("times in blue", Font.decode("times"), Color.BLUE);
        gd.showDialog();
        if (gd.wasCanceled()) {
            return;
        }

    }

}