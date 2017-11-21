/** Ben F Rayfield offers HumanAiCore opensource GNU LGPL */
package humanaicore.common;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleUnaryOperator;

public class MathUtil{
	
	public static double sigmoid(double x){
		return 1/(1+Math.exp(-x));
	}
	
	//public static final double veryPositive = Double.MAX_VALUE/0x1000000;
	
	//public static final double veryNegative = -veryPositive;
	
	/** If fraction is 0, returns -Infinity. If 1, returns Infinity.
	Derivation:
	s = 1/(1+e^-x).
	s*(1+e^-x) = 1.
	1+e^-x = 1/s.
	e^-x = 1/s - 1.
	-x = logBaseE(1/s - 1).
	x = -logBaseE(1/s - 1).
	*/
	public static double inverseSigmoid(double fraction){
		//x = -logBaseE(1/s - 1)
		//if(s == 0) return .5; //TODO verify this is on the curve
		//if(fraction == 0) return veryNegative; //TODO verify this is on the curve
		return -Math.log(1/fraction - 1);
	}
	
	public static double derivativeOfSigmoid(double x){
		return sigmoid(x)*(1-sigmoid(x)); //TODO optimize
	}
	
	public static void main(String args[]){
		testSigmoidAndItsInverse();
		testWeightedCoinFlip();
	}
	
	public static void testWeightedCoinFlip(){
		System.out.print("Testing weightRandomBit...");
		for(double targetChance=0; targetChance<1; targetChance+=.03){
			int countZeros = 0, countOnes = 0;
			for(int i=0; i<100000; i++){
				if(weightedCoinFlip(targetChance,Rand.strongRand)) countOnes++;
				else countZeros++;
			}
			double observedChance = (double)countOnes/(countZeros+countOnes);
			System.out.println("targetChance="+targetChance+" observedChance="+observedChance);
			if(Math.abs(targetChance-observedChance) > .01) throw new RuntimeException("targetChance too far from observedChance");
		}
	}
		
	public static void testSigmoidAndItsInverse(){
		System.out.println("Testing sigmoid and inverseSigmoid");
		double epsilon = 1e-12;
		for(double s=0; s<=1; s+=1./64){
			double x = inverseSigmoid(s);
			double ss = sigmoid(x);
			System.out.println("s="+s+" x="+x+" ss="+ss);
			if(Math.abs(s-ss) > epsilon) throw new RuntimeException("s != ss and is not close");
		}	
	}
	
	/** Uses SecureRandom and only an average of 2 random bits from it */
	public static boolean weightedCoinFlip(double chance){
		return weightedCoinFlip(chance, Rand.strongRand);
	}
	
	/** Consumes an average of 2 random bits (so its practical to use SecureRandom which is slow)
	by consuming random bits until get the first 1 then going directly to that digit
	in the chance as a binary fraction and returning it as the weighted random bit observe.
	*/
	public static boolean weightedCoinFlip(double chance, BooleanSupplier rand){
		if(chance < 0 || 1 < chance) throw new ArithmeticException("chance="+chance);
		while(rand.getAsBoolean()){
			if(.5 <= chance) chance -= .5;
			chance *= 2;
		}
		return .5 <= chance;
	}
	
	/** same as weightedCoinFlip(double,BooleanSupplier) */
	public static boolean weightedCoinFlip(double chance, Random rand){
		if(chance < 0 || 1 < chance) throw new ArithmeticException("chance="+chance);
		while(rand.nextBoolean()){
			if(.5 <= chance) chance -= .5;
			chance *= 2;
		}
		return .5 <= chance;
	}
	
	public static boolean isPowerOf2(long i){
		return i>0 && (i&(i-1)) == 0;
	}
	
	public static void normBySortedPointers(double min, double max, double[] d){
		normBySortedPointers(min, max, d, sortedPointersInto(d));
	}
	
	/** If you already have sortedPointers(d) */
	public static void normBySortedPointers(double min, double max, double[] d, int[] pointers){
		int siz = d.length;
		double range = max-min;
		for(int i=0; i<siz; i++){
			double fraction = (double)i/(siz-1);
			d[pointers[i]] = min+fraction*range;
		}
	}
	
	/** Example: normBySortedPointers data in arbitrary range, run neuralnet on it,
	then normBySortedPointersInverse to put neuralnet's output back into that same spread of data
	but a different permutation of it.
	*/
	public static void normBySortedPointersInverse(double[] d, int[] pointers){
		double[] inverse = new double[d.length];
		for(int i=0; i<d.length; i++){
			inverse[i] = d[pointers[i]];
		}
		System.arraycopy(inverse, 0, d, 0, d.length);
	}
	
	/** curve receives a fraction and returns the new double */
	public static void normBySortedPointers(DoubleUnaryOperator curve, double d[]){
		int siz = d.length;
		int pointers[] = sortedPointersInto(d);
		for(int i=0; i<siz; i++){
			double fraction = (double)i/(siz-1);
			d[pointers[i]] = curve.applyAsDouble(fraction);
		}
	}
	
	public static int[] sortedPointersInto(double d[]){
		return sortedPointersInto_tryingToImproveSpeed(d);
	}
	
	public static strictfp int[] sortedPointersInto(final long d[]){
		Integer Ints[] = new Integer[d.length];
		for(int i=0; i<d.length; i++) Ints[i] = i;
		Comparator<Integer> compare = new Comparator<Integer>(){
			public int compare(Integer x, Integer y){
				long xd = d[x], yd = d[y];
				if(xd < yd) return -1;
				if(xd > yd) return 1;
				return 0;
			}
		};
		Arrays.sort(Ints, compare);
		int ints[] = new int[d.length];
		for(int i=0; i<d.length; i++) ints[i] = Ints[i];
		return ints;
	}
	
	public static int[] sortedPointersInto_tryingToImproveSpeed(final double d[]){
		/*int pointers[] = new int[d.length];
		for(int i=0; i<d.length; i++) pointers[i] = i;
		//TODO? Arrays.parallelSort(arg0);
		*/
		
		/*for(int i=0; i<d.length; i++){
			double x = d[i];
			if(x != x){ //NaN, because it may be causing sorting inconsistency
				d[i] = Double.MAX_VALUE;
			}
		}*/
		
		Integer Ints[] = new Integer[d.length];
		for(int i=0; i<d.length; i++) Ints[i] = d.length-1-i;
		Comparator<Integer> compare = new Comparator<Integer>(){
			public int compare(Integer x, Integer y){
				double xd = d[x], yd = d[y];
				if(xd < yd) return -1;
				if(xd > yd) return 1;
				return 0;
			}
		};
		/*while(true){
			try{
				Arrays.sort(Ints, compare);
				break;
			}catch(Exception e){
				System.out.println("This is probably 'Comparison method violates its general contract' which strictfp avoids always singlethreaded but it appears some thread is using it, but which one could it be since its a local var? For now, since it happens only 1 20000 times its faster to just catch this and do it again those times. TODO find that thread and synchronize here and there! "+e.getMessage());
				e.printStackTrace(System.out);
			}
		}*/
		Arrays.sort(Ints, compare);
		int ints[] = new int[d.length];
		for(int i=0; i<d.length; i++) ints[i] = Ints[i];
		return ints;
	}
	
	/** Fast because it leaves it the complexity of NaN and positive/negative zero.
	TODO consider using java.lang.Math funcs instead of this in case its native optimized internal to JVM?
	*/
	public static double max(double x, double y){
		return x>y ? x : y;
	}
	
	/** TODO consider using java.lang.Math funcs instead of this in case its native optimized internal to JVM? */
	public static float max(float x, float y){
		return x>y ? x : y;
	}
	
	/** Fast because it leaves it the complexity of NaN and positive/negative zero.
	TODO consider using java.lang.Math funcs instead of this in case its native optimized internal to JVM?
	*/
	public static double min(double x, double y){
		return x<y ? x : y;
	}
	
	/** TODO consider using java.lang.Math funcs instead of this in case its native optimized internal to JVM? */
	public static float min(float x, float y){
		return x<y ? x : y;
	}
	
	/** Same as max(minValue, min(value, maxValue))
	TODO consider using java.lang.Math funcs instead of this in case its native optimized internal to JVM?
	*/
	public static double holdInRange(double min, double value, double max){
		if(value < min) return min;
		if(value > max) return max;
		return value;
	}
	
	/** TODO consider using java.lang.Math funcs instead of this in case its native optimized internal to JVM? */
	public static float holdInRange(float min, float value, float max){
		if(value < min) return min;
		if(value > max) return max;
		return value;
	}
	
	/** TODO consider using java.lang.Math funcs instead of this in case its native optimized internal to JVM? */
	public static int holdInRange(int min, int value, int max){
		if(value < min) return min;
		if(value > max) return max;
		return value;
	}

	public static int firstPowerOf2AtLeast(int i){
		//TODO this can be done in log steps. YES, wheres that code? Somewhere in the xorlisp experimental code.
		//Also related is Long.highestOneBit and Long.lowestOneBit.
		//This can be done with log number of if statements and bit shifts by half as many bits each time.
		int j = 1;
		int powerOf2 = 0;
		while(j < i){
			powerOf2++;
			j <<= 1;
		}
		return powerOf2;
	}

	public static int lastPowerOf2NotExceeding(int i){
		//TODO this can be done in log steps. YES, wheres that code? Somewhere in the xorlisp experimental code.
		//Also related is Long.highestOneBit and Long.lowestOneBit.
		//This can be done with log number of if statements and bit shifts by half as many bits each time.
		int j = 1;
		int powerOf2 = 0;
		while(j <= i){
			powerOf2++;
			j <<= 1;
		}
		return powerOf2-1;
	}
	
	public static double vectorLengthDyDx(double dy, double dx){
		return Math.sqrt(dy*dy + dx*dx);
	}
	
	public static double dotProd(double x[], double y[]){
		if(x.length != y.length) throw new RuntimeException("Arrays must be same size");
		double sum = 0;
		for(int i=0; i<x.length; i++) sum += x[i]*y[i];
		return sum;
	}
	
	/** useful in contrastiveDivergence and backprop */
	public static void addMultOfSameSize1dArraysIntoSquareArray(double x[], double y[], double square[][]){
		final int siz = x.length;
		if(siz!=y.length || siz!=square.length || siz!=square[0].length)
			throw new RuntimeException("Arrays must be same size");
		for(int i=0; i<siz; i++){
			final double mult = x[i];
			for(int j=0; j<siz; j++){
				square[i][j] += mult*y[j];
			}
		}
	}
	
	public static double len(double vec[]){
		double sumOfSquares = 0;
		for(double d : vec) sumOfSquares += d*d;
		return Math.sqrt(sumOfSquares);
	}
	
	/** sum of absvals */
	public static double lenManhattan(double vec[]){
		double sum = 0;
		for(double d : vec) sum += Math.abs(d);
		return sum;
	}
	
	/*public static double[] eachFourHexDigitsToScalar(String hex){
		double d[] = new double[hex.length()/4];
		for(int i=0; i<d.length; i++){
			int uint16 = Integer.parseInt(hex.substring(i*4,(i+1)*4), 16);
			d[i] = ((double)uint16-(1<<15))/(1<<15);
		}
		return d;
	}*/
	
	public static boolean readBit(byte b[], int index){
		return (b[index>>3] & (128>>(index&7))) != 0;
	}
	
	public static void writeBit(byte b[], int index, boolean value){
		if(value) b[index>>3] |= (128>>(index&7));
		else b[index>>3] &= ~(128>>(index&7));
	}
	
	public static double ave(double d[]){
		return sum(d)/d.length;
	}
	
	public static double sum(double d[]){
		double sum = 0;
		for(double dd : d) sum += dd;
		return sum;
	}
	
	/** standard deviation */
	public static double dev(double d[]){
		return devGivenAve(ave(d), d);
	}
	
	/** standard deviation, after you've already computed average */
	public static double devGivenAve(double ave, double d[]){
		double sumOfSquares = 0;
		for(double dd : d){
			double diff = dd-ave;
			sumOfSquares += diff*diff;
		}
		double aveSquare = sumOfSquares/d.length;
		return Math.sqrt(aveSquare);
	}
	
	public static float[] toFloats(double[] d){
		float[] f = new float[d.length];
		for(int i=0; i<f.length; i++) f[i] = (float)d[i];
		return f;
	}
	
	public static double[] toDoubles(float[] f){
		double[] d = new double[f.length];
		for(int i=0; i<d.length; i++) d[i] = f[i];
		return d;
	}
	
	/** only the values at odd indexs */
	public static double[] odds(double[] d){
		double[] odds = new double[d.length/2];
		for(int i=0; i<odds.length; i++){
			odds[i] = d[i+i+1];
		}
		return odds;
	}
	
	/** only the values at even indexs */
	public static double[] evens(double[] d){
		double[] evens = new double[(d.length+1)/2];
		for(int i=0; i<evens.length; i++){
			evens[i] = d[i+i];
		}
		return evens;
	}
	
	public static double[] joinEvensOdds(double[] evens, double[] odds){
		if(evens.length!=odds.length && evens.length!=odds.length+1) throw new Error(
			"There can be same number of odds and evens or 1 more even");
		double[] d = new double[evens.length+odds.length];
		for(int i=0; i<odds.length; i++){
			d[i+i+1] = odds[i];
		}
		for(int i=0; i<evens.length; i++){
			d[i+i] = evens[i];
		}
		return d;
	}
	
}
