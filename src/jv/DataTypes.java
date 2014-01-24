package jv;

/**
 * Created with IntelliJ IDEA.
 * User: miroslav
 * Date: 11/28/13
 * Time: 9:22 AM
 */
public class DataTypes {

	public static void main (String[] args) {

		System.out.println("testing data types...");

		int RANGE_BYTE = 255; 		// maximal int value
		int RANGE_SHORT = 65535;    // 0.0038 is the step

		float VAL_MAX = 255;
		float VAL_MIN = 0;

		int N = 1000;

		// define 100 float numbers
		float[] a 		= new float[N];
		byte[] 	a_byte 	= new byte[N];
		short[] a_short = new short[N];

		for (int cnt=0; cnt<N; cnt++) {
			a[cnt] = VAL_MIN + cnt*(VAL_MAX/N);
			a_byte[cnt] 	= (byte)	Math.round(  (a[cnt]/VAL_MAX)*RANGE_BYTE   );
			a_short[cnt] 	= (short)	Math.round(  (a[cnt]/VAL_MAX)*RANGE_SHORT  );
			System.out.println(String.format("%3.6f (%b) \t %d (%b)", // 	 %d (%b)
													a[cnt],
													(cnt>0)?(a[cnt]>a[cnt-1]):(true),
													//a_byte[cnt],
													//(cnt>0)?(a_byte[cnt]>a_byte[cnt-1]):(true),
													a_short[cnt],
													(cnt>0)?( (a_short[cnt] & 0xffff) > (a_short[cnt-1] & 0xffff) ):(true)));
		}

	}

}
