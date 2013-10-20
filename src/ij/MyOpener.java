package ij;

import ij.IJ;
import ij.plugin.PlugIn;

import javax.swing.*;
import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: miroslav
 * Date: 8/9/13
 * Time: 1:46 PM
 */
public class MyOpener implements PlugIn {

    public static String open(String message, boolean showPath)
	{

        IJ.showMessage(message);

		/*
		 * based on File_Opener.java ij source code
		 * this sequence enables to browse any file
		 * and get the path to it
		 */

        File dir=null;
        JFileChooser fc = null;
        try {fc = new JFileChooser();}
        catch (Throwable e) {return null;}
        if (dir==null) {
            String sdir = System.getProperty( "user.home" );//OpenDialog.getDefaultDirectory();
            if (sdir!=null)
                dir = new File(sdir);
        }
        if (dir!=null)
            fc.setCurrentDirectory(dir);
        int returnVal = fc.showOpenDialog(IJ.getInstance());
        if (returnVal!=JFileChooser.APPROVE_OPTION)
            return null;

        File train_dataset_file = fc.getSelectedFile();

        String train_dataset_path = fc.getSelectedFile().getAbsolutePath();

        if(!train_dataset_file.exists()){
            IJ.showMessage("file "+train_dataset_path+" does not exist");
            return null;
        }
        else{
            if (showPath) IJ.showMessage("selected    " + train_dataset_path);
        }

        return fc.getSelectedFile().getAbsolutePath();

    }

    public void run(String s) {

        System.out.println("testing MyOpener...");
        String legend = "Plugin enables browsing&selecting file from some location and returns it's absolute path.";
        IJ.log("\n\t "+MyOpener.open(legend, true));

    }
}
