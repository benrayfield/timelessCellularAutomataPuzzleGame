package timelessCellularAutomata.crbm;
import static log.Lg.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import humanaicore.common.MathUtil;
import humanaicore.common.Parallel;
import humanaicore.common.Rand;
import timelessCellularAutomata.crbm.*;

/** Use as immutable despite the technical ability to modify arrays.
Is not determinstic, even if the BooleanSupplier is pseudorandom,
cuz is not strictfp and is not deterministic of order of ops,
but guarantees convergence to some localMinimum of energy of boltzmann neuralnet equation.
<br><br>
CONSTRAINTS:
(crbm.weight[toZ][fromZ]==null)==(crbm.info.ei[toZ][fromZ]==null);
*/
public class GameState{
	
	/** timeless cellular automata rule, a convolutional restricted/layered boltzmann machine/neuralnet */
	public final Crbm crbm;
	
	/** zyx[z=crbmLayer][y=screenVertical][x=screenHorizontal] */
	public final float[][][] zyx;
	
	public final float decay;
	
	/** new state is oldState*(1-decay)+newState*decay. */
	public GameState(Crbm crbm, float[][][] zyx, float decay){
		this.crbm = crbm;
		this.zyx = zyx;
		this.decay = decay;
	}
	
	/** Example BooleanSupplier: Rand.strongRandBits. All numbers in map must be double, like in benrayfield's json maps.
	new state is oldState*(1-decay)+newState*decay.
	*/
	public GameState nextState(Map inputs, BooleanSupplier rand){
		
		GameState g = paintByMapInputs(inputs,this);
			
		//lg("inputs="+inputs);
		int zSize = g.zyx.length, ySize = g.zyx[0].length, xSize = g.zyx[0][0].length;
		float[][][] nextZyx = new float[zSize][ySize][xSize];
		//contrastiveDivergence order for only 1 visible and 1 hidden
		int r = g.crbm.info.maxSq;
		List<Callable<Object>> tasks = new ArrayList();
		//FIXME for threading, must do in layers, cant reorder RBM layers but can reorder within a layer.
		for(int z=zSize-1; z>=0; z--){ //Works with only 1 visible and 1 hidden layers (which can be many crbmLayers)
			for(int y=r; y<ySize-r; y++){
				final int Z = z, Y = y;
				//tasks.add(()->{
					for(int x=r; x<xSize-r; x++){
						float targetCellState = g.nextCellState(Rand.strongRandBits,Z,Y,x);
						float oldCellState = g.zyx[Z][Y][x];
						nextZyx[Z][Y][x] = oldCellState*(1-decay) + decay*targetCellState;
						//nextZyx[Z][Y][x] = g.nextCellState(Rand.strongRandBits,Z,Y,x);
						//nextZyx[Z][Y][x] = g.nextCellState(()->Rand.weakRand.nextBoolean(),Z,Y,x);
						//nextZyx[z][y][x] = g.zyx[z][y][x]; //FIXME remove this line
					}
					//return null; //Callable
				//});
			}
		}
		//Parallel.cpus.invokeAll(tasks);
		//Parallel.cpus.awaitQuiescence(1L<<60, TimeUnit.NANOSECONDS); //until they're all done
		//FIXME what if other code is using Parallel.cpus and it never gets empty?
		
		/*if(mouseCellY != null && mouseCellX != null){
			int mouseCellYI = MathUtil.holdInRange(0, (int)(double)mouseCellY, nextZyx[0].length);
			int mouseCellXI = MathUtil.holdInRange(0, (int)(double)mouseCellX, nextZyx[0][0].length);
			nextZyx[0][mouseCellYI][mouseCellXI] = 1;
		}*/
		
		return new GameState(crbm,nextZyx,decay);
		//TODO multithread? Is it slowing it down with thread overhead? Or am I dividing it into too many tasks?
	}
	
	static GameState paintByMapInputs(Map input, GameState g){
		//TODO? or always 0? Double mouseCellY = (Double) input.get("mouseCellZ");
		Double mouseCellY = (Double) input.get("mouseCellY");
		Double mouseCellX = (Double) input.get("mouseCellX");
		
		Double mouseButton1 = (Double) input.get("mouseButton1");
		Double mouseButton3 = (Double) input.get("mouseButton3");
		if(mouseButton1 == null) mouseButton1 = 0.;
		if(mouseButton3 == null) mouseButton3 = 0.;
		double paintSign = mouseButton1-mouseButton3; //-1, 0, or 1
		
		Double dt = (Double) input.get("dt");
		
		//double paintScalar = dt*paintSign*50;
		double paintScalar = dt*paintSign*20;
		//double paintScalar = dt*paintSign*2;
		
		return g.paintOntoCrbmWeights(0,(int)(double)mouseCellY,(int)(double)mouseCellX,(float)paintScalar);
	}
	
	/** Either nextCellChance or weightedCoinFlip of it depending on NodeInfo.isBit */
	public float nextCellState(BooleanSupplier rand, int z, int y, int x){
		float chance = nextCellChance(z,y,x);
		return crbm.info.ni[z].isBit ? (MathUtil.weightedCoinFlip(chance,rand)?1:0) : chance;
	}
	
	public float nextCellChance(int z, int y, int x){
		float sum = crbm.info.ni[z].bias;
		for(int fromZ=0; fromZ<zyx.length; fromZ++){
			if(crbm.weight[z][fromZ] != null){ //weights exists between those pairs
				int sq = crbm.info.ei[z][fromZ].sq;
				int startY = y-(sq>>1);
				int startX = x-(sq>>1);
				float sumInSquare = 0;
				//Mirror dy and dx for down inference, or not if up inference,
				//or either way works if from and to same layer.
				//Put doubleLoop inside the IF as optimization.
				if(crbm.info.ei[z][fromZ].flip){
					for(int sqY=0; sqY<sq; sqY++){
						for(int sqX=0; sqX<sq; sqX++){
							sumInSquare += zyx[fromZ][startY+sqY][startX+sqX]
								*crbm.weight[z][fromZ][sq-1-sqY][sq-1-sqX];
						}
					}
				}else{
					for(int sqY=0; sqY<sq; sqY++){
						for(int sqX=0; sqX<sq; sqX++){
							sumInSquare += zyx[fromZ][startY+sqY][startX+sqX]
								*crbm.weight[z][fromZ][sqY][sqX];
						}
					}
				}
				sum += sumInSquare*crbm.info.ni[fromZ].mass;
			}
		}
		//if(z==0 && x==y && x < 30){
		//	lg("x"+x+" y"+y+" sum="+sum);
		//}
		return (float)MathUtil.sigmoid(sum);
	}
	
	/** This is a very simple change, using the current node states of adjacent nodes,
	only looking out 1 level deep, based on the theory that its already mostly converged
	so contrastiveDivergence from there would do approx that.
	Returns a GameState with a new Crbm but same float[][][] zyx,
	since the next GameState derived from nextState func will depend on that Crbm.
	<br><br>
	TODO define the "change" param more precisely. Is it a change proportional to brightness, weights, etc?
	If defined as changing brightness, then backprop could change weights infinitely
	if have to change brightness to 0 or 1. If defined as change of weights, then it could be confusing
	when some weights can be negative and some positive. For now, it approximately means that
	both weights and brightness rise or fall when change is positive or negative.
	*/
	public GameState paintOntoCrbmWeights(int z, int y, int x, float change){
		int maxSq = crbm.info.maxSq;
		if(y < maxSq || zyx[0].length-maxSq <= y || x < maxSq || zyx[0][0].length-maxSq <= x){
			return this; //too close to edge
		}
		float[][][][] newWeight = Crbm.copySparseWeightsArray(crbm.weight);
		//FIXME scale by specificMass/totalRelevantMass? That wont have any effect on
		//the change ratios with just 1 edlay and all hidden nodes having same mass which differs from visible node mass,
		//but in more complex topology and masses it would have effect.
		float mult = change;
		for(int fromZ=0; fromZ<zyx.length; fromZ++){
			if(crbm.weight[z][fromZ] != null){ //weights exists between those pairs
				int sq = crbm.info.ei[z][fromZ].sq;
				int startY = y-(sq>>1);
				int startX = x-(sq>>1);
				float sumInSquare = 0;
				
				if(crbm.info.ei[z][fromZ].flip){
					for(int sqY=0; sqY<sq; sqY++){
						for(int sqX=0; sqX<sq; sqX++){
							newWeight[z][fromZ][sq-1-sqY][sq-1-sqX] += mult*zyx[fromZ][startY+sqY][startX+sqX];
						}
					}
				}else{
					for(int sqY=0; sqY<sq; sqY++){
						for(int sqX=0; sqX<sq; sqX++){
							newWeight[z][fromZ][sqY][sqX] += mult*zyx[fromZ][startY+sqY][startX+sqX];
						}
					}
				}
			}
		}
		return new GameState(new Crbm(crbm.info,newWeight),zyx,decay);
	}
	
	
	/*
	
	DELAY THE layerAttention PARAM UNTIL STATPAINTING IS WORKING ON SCREEN TRAINED ON RULE110
	AND I'VE CREATED A FEW GENERATIONS OF FUN SMOOTH AUTOMATA AFTER THAT,
	CUZ THAT PARAM IS COMPLICATING IT SO MUCH I CANT THINK FAR ENOUGH AHEAD TO DO ANYTHING
	AND I'M STUCK. I NEED TO GO FORWARD WITH JUST A 1 EDLAY DESIGN, USING SAME DATASTRUCT
	BUT WITHOUT layerAttention. Its simply "public float nextCellChance(int z, int y, int x)"
	IN VISIBLE LAYER NOLAY0 AND ALL HIDDEN LAYERS WHICH ARE parallel at NOLAY1.
	
	/** If NolayInfo.isBit for that cell, then next cell state is a weightedCoinFlip of this chance,
	else is chance directly. Throws IndexOutOfBoundsException if the Crbm size goes off game y x edges,
	so there are cells near y and x edges that are constant the whole game
	or could be copied from another source such as duplicating parts of game area to wrap.
	TODO write math expression that says what params are allowed in terms of crbm.info.maxSq and zyx sizes.
	<br><br>
	In RBM this is normally called in same order of contrastiveDivergence zigzagging,
	but more generally it will converge even if called in random order. The zigzagging works better.
	layerAttention defines which layers to read from, such as to implement that zigzagging.
	layerAttention of 0 or null EdlayInfo/float[][] avoids a doubleLoop in the body of this sparse tripleLoop.
	(crbm.weight[toZ][fromZ]==null)==(crbm.info.ei[toZ][fromZ]==null)
	<br><br>
	For multithread efficiency, this only writes on stack,
	doesnt write the float[][][][] crbm weights or float[][][] game area.
	*
	public float nextCellChance(int z, int y, int x, float... layerAttention){
		if(layerAttention.length != zyx.length) throw new Error(
			"layerAttention must be same size as layers="+zyx.length);
		float estimateSum = 0;
		for(int fromZ=0; fromZ<zyx.length; fromZ++){
			crbm.info.ni[fromZ].aveNodeVal and .mass
			but how can we estimateSum if not counting the weights yet?
			Dont want an estimate. Want a number to multiply by. Name it something else.
		}
		
		
		float sum = 0;
		
		FIXME merge attention and mass? In this func, they'll always be used as attention*mass aka atmas:
				
		FIXME use atmas and Nolay.aveNodeVal to reduce stdDev of sum.
		SOLUTION: Divide local sum var by the sum of attention*mass*aveNodeVal
		so local sum var averages sum of weights+bias if other nodes are random,
		but cuz of the assoc property of boltz, things with negative weights tend to be off.
		
		FIXME how to compute with this...
		The NodeInfo.aveNodeVal could be interpreted as using nodeVal/aveNodeVal as nodeVal
		so all nodesVals average 1.
		Then we take a doubleWeightedSum of those as sum of attention*weight*nodeVal,
		where sum of attention is 1, and weights can be anything, and nodeVal averages 1.
		Does that interfere with, forExample, having 32 times more nolay1 size than nolay0?
		In that case, in nolay1, attention would be approx 32 times less, and bias would be more negative.
		Attention can only be nonzero where weights exist, since weights are sparse between crbmLayers.
		
		for(int fromZ=0; fromZ<zyx.length; fromZ++){
			if(crbm.weight[z][fromZ] != null){ //weights exists between those pairs
				if(layerAttention[fromZ] != 0){ //If theres 0 attention, it has no effect
					int sq = crbm.info.ei[z][fromZ].sq;
					int startY = y-sq>>1;
					int startX = x-sq>>1;
					float sumInSquare = 0;
					//Mirror dy and dx for down inference, or not if up inference,
					//or either way works if from and to same layer.
					//Put doubleLoop inside the IF as optimization.
					if(crbm.info.ei[z][fromZ].flip){
						for(int sqY=0; sqY<sq; sqY++){
							for(int sqX=0; sqX<sq; sqX++){
								sumInSquare += zyx[fromZ][startY+sqY][startX+sqX]
									*crbm.weight[z][fromZ][sq-1-sqY][sq-1-sqX];
							}
						}
					}else{
						for(int sqY=0; sqY<sq; sqY++){
							for(int sqX=0; sqX<sq; sqX++){
								sumInSquare += zyx[fromZ][startY+sqY][startX+sqX]
									*crbm.weight[z][fromZ][sqY][sqX];
							}
						}
					}
					sum += sumInSquare*crbm.info.ni[fromZ].mass;
				}
			}
		}
		return (float)MathUtil.sigmoid(sum);
	}
	
	/** Either nextCellChance or weightedCoinFlip of it depending on NodeInfo.isBit *
	public float nextCellState(BooleanSupplier rand, int z, int y, int x, float... layerAttention){
		float chance = nextCellChance(z,y,x,layerAttention);
		return crbm.info.ni[z].isBit ? (MathUtil.weightedCoinFlip(chance,rand)?1:0) : chance;
	}*/

}