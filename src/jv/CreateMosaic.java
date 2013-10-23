package jv;

import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileSaver;
import ij.process.ByteProcessor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: miroslav
 * Date: 10/20/13
 * Time: 3:01 AM
 */
public class CreateMosaic {

    // to align together stacks of images from nl1a diadem dataset
    // expects folder with stack images
    // name of each stack contains comma separated 3d root location og the stack
    // necessary to group them and align so that ground truth can be plotted over properly
    // example call:
    // java -cp ~/misc/misc_.jar:/home/miroslav/jarlib/ij.jar jv.CreateMosaic /home/miroslav/Copy/nl1a/ss2/stacks


	public static void main (String[] args) {

        ArrayList<ImagePlus>    imgs    = new ArrayList<ImagePlus>();
        ArrayList<int[]>        roots   = new ArrayList<int[]>();
        ArrayList<int[]>        dims    = new ArrayList<int[]>();

        int dimX=0,                         dimY=0,                     dimZ=0;
        //int startX=Integer.MAX_VALUE,     startY=Integer.MAX_VALUE,   startZ=Integer.MAX_VALUE;
        int startX=0,                       startY=0,                   startZ=0;
        int endX=Integer.MIN_VALUE,         endY=Integer.MIN_VALUE,     endZ=Integer.MIN_VALUE;


        ImagePlus mosaic;

        if (args.length>0) {

            File f = new File(args[0]);
            String directory = f.getAbsolutePath();// args[0];

            //System.out.println("source directory : \n" + directory);

            String[] listDirectory = f.list();

            for (int ii=0; ii<listDirectory.length; ii++) {

                String filePath = directory+File.separator+listDirectory[ii];

                if (new File(filePath).isFile()) {

                    // extract root locations from the name
                    String fileName = filePath.substring(filePath.lastIndexOf("/")+1, filePath.lastIndexOf("."));
                    String fileExt = filePath.substring(filePath.lastIndexOf(".")+1);

                    //System.out.println("file exts : "+fileExt);

                    if (fileExt.equalsIgnoreCase("TIF")) {

                        ImagePlus imp = new ImagePlus(filePath);
                        imgs.add(imp);   System.out.println("adding... "+filePath);

                        int c = 0x002C;  // comma
                        int loc1 = fileName.indexOf(c, 0);
                        int loc2 = fileName.lastIndexOf(c);

                        // these are the roots of each image
                        int locX = Integer.valueOf(fileName.substring(0,loc1));
                        int locY = Integer.valueOf(fileName.substring(loc1+1,loc2));
                        int locZ = Integer.valueOf(fileName.substring(loc2+1));

                        // in order to be compatible with reconstruction coordinates
                        // ignore negative values - consider only positive locations
                        // otherwise reconstruction is shifted

                        // loop local coordinates (0,0,0)->(W,H,L) and add the bias
                        // if they are negative after bias - just ignore to stay aligned

                        int lenX = imp.getWidth();
                        int lenY = imp.getHeight();
                        int lenZ = imp.getStackSize();

                        roots.add(new int[]{locX, locY, locZ});
                        dims.add(new int[]{lenX, lenY, lenZ});

                        //if (locX<startX) startX = locX; // this is index
                        //if (locY<startY) startY = locY;
                        //if (locZ<startZ) startZ = locZ;

                        if (locX+lenX-1>endX) endX = locX+lenX-1; // endX,Y,Z represents index
                        if (locY+lenY-1>endY) endY = locY+lenY-1;
                        if (locZ+lenZ-1>endZ) endZ = locZ+lenZ-1;

                    }

                }

            }

            System.out.println(imgs.size()+" images");

            dimX = endX-startX+1; // actually endX+1
            dimY = endY-startY+1; // endY+1
            dimZ = endZ-startZ+1; // endZ+1

            //System.out.println("["+dimX+"("+startX+" -- "+endX+"), "+dimY+"("+startY+" -- "+endY+"), "+dimZ+"("+startZ+" -- "+endZ+")]");

            // allocate new image
            ImageStack is = new ImageStack(dimX, dimY);
            for (int iii=0; iii<dimZ; iii++) {
                is.addSlice(new ByteProcessor(dimX, dimY));
            }

            mosaic = new ImagePlus("mosaic", is);

            // fill it with values, go image per image
            for (int k=0; k<imgs.size(); k++) { //

                //if(!imgs.get(k).getTitle().equalsIgnoreCase("0,0,0.tif")) continue;

                System.out.print("\nadding image "+imgs.get(k).getTitle()+" at "+Arrays.toString(roots.get(k)));

				for (int x=0; x<dims.get(k)[0]; x++) { // x++

                    for (int y=0; y<dims.get(k)[1]; y++) { // y++

                        for (int z=0; z<dims.get(k)[2]; z++) { // z++

                            // global location
                            int globX = roots.get(k)[0]  + x;   //-  startX
                            int globY = roots.get(k)[1]  + y;   // - startY
                            int globZ = roots.get(k)[2]  + z;   // - startZ

                            if (globX>0 && globY>0 && globZ>0) {  // ignore negative so that the reconstruction is properly positioned

                                // value to set
                                int val = imgs.get(k).getStack().getProcessor(z+1).get(x,y);
                                int curr_val = mosaic.getStack().getProcessor(globZ+1).get(globX, globY);

                                if (val>curr_val)
                                    mosaic.getStack().getProcessor(globZ+1).set(globX, globY, val);

                            }

                        }

                    }

                }

				System.out.println(" done.");

            }

            String outPath = System.getProperty("user.home")+File.separator+"mosaic.tif";
            new FileSaver(mosaic).saveAsTiffStack(outPath);
            System.out.println(outPath + " exported.");

        }
        else {

            System.out.println("needs one argument: folder where the files to mosaic are...");

        }

	}

}
