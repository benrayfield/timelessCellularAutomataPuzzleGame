package timelessCellularAutomata.crbm;

import java.util.Random;

public class Util{
	private Util(){}
	
	public static float[][] writeRandBells(float[][] yx, Random rand, float ave, float stdDev){
		for(int y=0; y<yx.length; y++){
			for(int x=0; x<yx[0].length; x++){
				yx[y][x] = (float)(ave+stdDev*rand.nextGaussian());
			}
		}
		return yx;
	}

}
