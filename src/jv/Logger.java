package jv;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: miroslav
 * Date: 10/30/13
 * Time: 5:18 PM
 */
public class Logger {

    // example of logger often necessary for debugging or building an output

    // run command
    //java -cp misc_.jar jv.Logger

    public static void main (String[] args) {

        // fix parameters
        String          logPath     = "logTest.txt";  // just name will export in current folder
        int             limit       = 8;

        run(limit, logPath); // method that includes logging

    }


    private static void run(int limit, String logName) {

        /*  method will do some dummy task,
            here it's just empty looping till some limit
            every loop will be documented in the log with the name submitted as an argument
         */

        /*
            initialize writer
         */
        PrintWriter     logWriter = null;

        /*
            empty the file before logging...
         */
        try {
            logWriter = new PrintWriter(logName);
            logWriter.print("");
            logWriter.close();
        } catch (FileNotFoundException ex) {}


        /*
            initialize detection log file
         */
        try {
            logWriter = new PrintWriter(new BufferedWriter(new FileWriter(logName, true)));
            logWriter.println("# EXAMPLE LOG...");
        } catch (IOException e) {}

        /*
            main loop
         */
        for (int ii=0; ii<limit; ii++) {

            System.out.println("Loop "+ii+"/"+(limit-1));

            /*
                log it
             */
            logWriter.println(""+ii);

        }

        /*
            close log
         */
        logWriter.close();

    }


}
