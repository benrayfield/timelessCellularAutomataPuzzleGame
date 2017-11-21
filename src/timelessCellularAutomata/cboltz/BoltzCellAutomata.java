/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package timelessCellularAutomata.cboltz;

import humanaicore.common.MathUtil;
import humanaicore.common.Rand;

public class BoltzCellAutomata{
	
	/** neuralnode states, range 0 to 1 computed as sigmoid of weightedSum of near nodes. */
	public final float[][] space;
	
	/** Alternate updating weightedSum and space */
	public final float[][] weightedSums;
	
	/** Boltzmann neuralnet weights from/to each cell in 2d space.
	Its recommended for this kind of automata that all weights be nonpositive and bias positive
	cuz that aligns with npcomplete maxclique theory,
	though thats not recommended for boltzmann neuralnets in general which use pos and neg weights.
	This has lots of duplication if all pairs of cells at same distance are held to same weight.
	That constraint could be relaxed to just enforce symmetry across center
	so about half of these would be duplicates.
	*/
	public final float[][] weights;
	
	/** boltzmann neuralnet bias, same for each node */
	public float bias;
	
	public BoltzCellAutomata(float[][] space, float[][] weights, float bias){
		if(weights.length != weights[0].length) throw new Error("weights[][] is not square");
		if((weights.length&1)!=1) throw new Error("weights[][] size must be odd: "+weights.length);
		this.space = space;
		this.weightedSums = new float[space.length][space[0].length];
		this.weights = weights;
		this.bias = bias;
	}
	
	/** Next boltzmann neuralnet state. If weights and bias are big enough, guaranteed to converge.
	Thats why its called a timeless cellular automata.
	*/
	public void nextState(){
		int h = space.length, w = space[0].length;
		int sq = weights.length;
		int r = sq/2; //min weights square radius
		//TODO use ForkJoinPool or Thread directly to multithread space.
		//Each space[y] will be written by 1 thread which will have a range of y, for caching.
		for(int y=0; y<h-sq; y++){
			for(int x=0; x<w-sq; x++){
				float weightedSum = bias;
				for(int sqY=0; sqY<sq; sqY++){
					for(int sqX=0; sqX<sq; sqX++){
						weightedSum += space[y+sqY][x+sqX]*weights[sqY][sqX];
					}
				}
				weightedSums[y+r][x+r] = weightedSum;
			}
		}
		for(int y=r; y<h-r; y++){
			for(int x=r; x<w-r; x++){
				//if(Rand.strongRand.nextBoolean()){ //avoid flashing most nodes by updating random sets of them together
				if(MathUtil.weightedCoinFlip(.1)){ //avoid flashing most nodes by updating random sets of them together
					this.space[y][x] = (float)sigmoid(weightedSums[y][x]);
					//this.space[y][x] = MathUtil.weightedCoinFlip(sigmoid(weightedSums[y][x]))?1:0;
				}
			}
		}
	}
	
	public static double sigmoid(double d){
		return 1/(1+Math.exp(-d));
	}
	
	/** This, or a stronger symmetry norm, is required to guarantee boltz convergence,
	which also requires bias andOr weights be big enough.
	Relative to center of weights[][], for (y,x) and (-y,-x) sets both to their average.
	This is weaker than all weights of same distance (same x squared + y squared) averaged together.
	*/
	public void weakestSymmetryNorm(){
		int sq = weights.length;
		for(int smallY=1; smallY<sq; smallY++){
			for(int smallX=0; smallX<=smallY; smallX++){
				float ave = (weights[smallY][smallX]+weights[sq-1-smallY][sq-1-smallX]);
				weights[smallY][smallX] = ave;
				weights[sq-1-smallY][sq-1-smallX] = ave;
			}
		}
	}
	
	/** Norms weights[][] by averaging
	everything thats at the same distance (from center of that 2d array) together.
	*/
	public void distanceSymmetryNorm(){
		//This could be optimized much better but it doesnt matter cuz nextState() is bottleneck
		int sq = weights.length;
		int maxPossibleDistanceSquared = 2*(1+sq/2)*(1+sq/2);
		for(int dist=0; dist<maxPossibleDistanceSquared; dist++){
			int howManySquaresAtThatDistance = 0;
			float sumAtThatDistance = 0;
			for(int smallY=0; smallY<sq; smallY++){
				for(int smallX=0; smallX<sq; smallX++){
					int dy = smallY-sq/2, dx = smallX-sq/2;
					int observeDistSq = dy*dy + dx*dx;
					if(observeDistSq == dist){
						howManySquaresAtThatDistance++;
						sumAtThatDistance += weights[smallY][smallX];
					}
				}
			}
			float aveAtThatDistance = sumAtThatDistance/howManySquaresAtThatDistance; //if NaN, wont be used
			for(int smallY=0; smallY<sq; smallY++){
				for(int smallX=0; smallX<sq; smallX++){
					int dy = smallY-sq/2, dx = smallX-sq/2;
					int observeDistSq = dy*dy + dx*dx;
					if(observeDistSq == dist) weights[smallY][smallX] = aveAtThatDistance;
				}
			}
		}
	}
	
	

}
