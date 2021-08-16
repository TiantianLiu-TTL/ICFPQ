/**
 * 
 */
package utilities;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * <h>DataGenConstant</h>
 * Constant Values in Data Generating
 * @author feng zijin, Tiantian Liu
 *
 */
public class DataGenConstant {
	public static String dataset = "syn";
	
	// PARAMETERS FOR INDOOR SPACES
	/** dimensions of the floor */
	public static double floorRangeX = 1368;
	public static double floorRangeY = 1368;

	public static double zoomLevel = 0.6;

	/** numbers of the floor */
	public static int nFloor = 5;

	/** type of dataset */
	public static int dataType = 1; // 1 means regular dataset; 0 means less doors; 2 means more doors;

	/** type of division */
	public static int divisionType = 1; // 1 means regular division; 0 means no division for hallway; 2 means more hallway;

	/** length of stairway between two floors */
	public static double lenStairway = 20.0;

	public static int startTime = 0;

	public static int endTime = 5000;

	public static int objects = 600;

//	public static int parInterval = 5;
//
//	public static ArrayList<Integer> doorInterval = new ArrayList<>(Arrays.asList(5, 10, 15, 20, 25));

	public static int parInterval = 10;

	public static ArrayList<Integer> doorInterval = new ArrayList<>(Arrays.asList(10, 20, 30, 40, 50));

//	public static int parInterval = 15;
//
//	public static ArrayList<Integer> doorInterval = new ArrayList<>(Arrays.asList(15, 30, 45, 60, 75));

//	public static int parInterval = 20;
//
//	public static ArrayList<Integer> doorInterval = new ArrayList<>(Arrays.asList(20, 40, 60, 80, 100));

//	public static ArrayList<Integer> queuePars = new ArrayList<>();

	public static ArrayList<Integer> queuePars = new ArrayList<>(Arrays.asList(3, 8, 35, 56, 62, 71, 78, 84, 90, 100, 128, 132, 133, 136));

//	public static ArrayList<Integer> queuePars = new ArrayList<>(Arrays.asList(3, 5, 8, 20, 35, 42, 56, 57, 62, 68, 71, 74, 78, 80, 84, 88, 90, 97, 100, 111, 115, 120, 128, 130, 132, 133, 135, 136));

//	public static ArrayList<Integer> queuePars = new ArrayList<>(Arrays.asList(1, 3, 4, 5, 8, 9, 14, 18, 20, 28, 35, 38, 42, 45, 56, 57, 62, 64, 68, 71, 74, 76, 78, 80, 84, 85, 88, 90, 95, 97, 100, 103, 111, 113, 115, 120, 128, 130, 132, 133, 135, 136));

//	public static ArrayList<Integer> queuePars = new ArrayList<>(Arrays.asList(1, 3, 4, 5, 8, 9, 14, 15, 18, 20, 25, 28, 30, 32, 35, 38, 42, 45, 47, 50, 53, 56, 57, 62, 64, 65, 68, 71, 74, 76, 78, 80, 84, 85, 88, 90, 93, 95, 97, 100, 103, 105, 108, 111, 113, 115, 120, 125, 128, 130, 132, 133, 135, 136, 138, 140));

	public static ArrayList<Integer> exitDoors = new ArrayList<>(Arrays.asList(210, 212, 213, 214));

	public static int destributionPara = 10;

	public static int volatilityPara = 10;

	public static int volatilityTheta = 3;
	
	// ID COUNTERS FOR INDOOR ENTITIES
	/** the ID counter of Partitions */
	public static int mID_Par = 0;

	/** the ID counter of Doors */
	public static int mID_Door = 0;
	
	/** the ID counter of Floors */
	public static int mID_Floor = 0;

	/** the ID counter of Objects */
	public static int mID_Object = 0;
	
	// KEYWORDS	
	public static int mKeyworSize = 0;

	
	// traveling speed 83.34m/min
	public static double traveling_speed = 1 * 83.34 / 60;

	public static void init(String dataName) {

		if (dataName.equals("newHsm")) {
			dataset = "newHsm";
			floorRangeX = 2100;
			floorRangeY = 2700;
			zoomLevel = 0.28;
			nFloor = 7;
			objects = 10;
			parInterval = 10;
			doorInterval = new ArrayList<>(Arrays.asList(10));

			queuePars = new ArrayList<>(Arrays.asList(1, 39, 47, 51, 57, 58, 59, 62, 71, 73, 74, 75, 78, 79, 81, 84, 85, 86, 88, 94, 95, 101, 102, 109, 110));
//
			exitDoors = new ArrayList<>(Arrays.asList(1, 5, 9, 10, 233, 617, 619, 620, 622, 626));
			destributionPara = 10;
			volatilityPara = 10;
			volatilityTheta = 3;


		}

	}

}
