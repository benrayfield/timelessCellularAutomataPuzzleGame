/** Ben F Rayfield offers this "common" software opensource GNU LGPL or MIT license */
package humanaicore.common;
import java.security.SecureRandom;
import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

public class Rand{
	private Rand(){}
	
	public static final Random weakRand;
	
	public static final SecureRandom strongRand;
	
	public static final BooleanSupplier strongRandBits;
	
	/** 0 (inclusive) to 1 (exclusive) */
	public static final DoubleSupplier strongRandInclExclFractions;
	
	/** ave 0 stdDev 1 */
	public static final DoubleSupplier strongRandBell;
	
	static{
		strongRand = new SecureRandom();
		//TODO set seed as bigger byte array, more hashcodes to fill it maybe
		strongRand.setSeed(3+System.nanoTime()*49999+System.currentTimeMillis()*new Object().hashCode());
		weakRand = new Random(strongRand.nextLong());
		strongRandBits = ()->strongRand.nextBoolean();
		strongRandInclExclFractions = ()->strongRand.nextDouble();
		strongRandBell = ()->strongRand.nextGaussian();
	}

}
