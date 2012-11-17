package tests;

import java.util.Date;
import java.util.Random;

// Utility methods for testing
public class Test_Random {
	
	private static Random rand = new Random(System.currentTimeMillis());
	
	public static int randInt(){
		return rand.nextInt();
	}
	
	public static String randString(){
		return new Long(rand.nextLong()).toString();
	}
	
	public static Date randDate(){
		return new Date(rand.nextLong());
	}

	public static boolean randBool(){
		return rand.nextBoolean();
	}
}
