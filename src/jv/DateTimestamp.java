package jv;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by miroslav on 4/24/14.
 */
public class DateTimestamp {

    public static void main (String[] args) {

        String 			timestamp = "";
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd__HH_mm_ss");
        timestamp =  dateFormat.format(new Date());
        System.out.println("the timestamp is:\t\t"+timestamp);

    }

}
