package imj;

import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;

/**
 * Created by miroslav on 12-8-14.
 */
public class TestHeadless implements PlugIn {


    @Override
    public void run(String s) {
        ImagePlus ip = new ImagePlus("", new ByteProcessor(256, 256));
        ip.show();
        System.out.println("should have showed it...");
    }
}
