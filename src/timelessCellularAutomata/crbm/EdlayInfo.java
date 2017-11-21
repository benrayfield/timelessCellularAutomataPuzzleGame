package timelessCellularAutomata.crbm;

/** edge layer info, but nothing specific to 1 edge */
public class EdlayInfo{
	
	/** Odd. An edlay is a square of weights between 2 layer indexs (often not adjacent).
	Weights are indexed as w[sq>>1+direction*dy][sq>>1+direction*dx]
	where direction is 1 for inference up or within same layer, -1 for inference down or within same layer.
	Within the same layer, it can be 1 or -1 since its the same weights flipped or not.
	*/
	public final int sq;
	
	//TODO bring back aveWeight after layerAttention param is added back in GameState funcs. public final float aveWeight;
	
	/** Inference up vs down, as explained in comment of sq. direction=flip?1:-1 */
	public final boolean flip;
	
	/** True if this is from and to the same layer therefore must mirror weights across diagonal.
	Boltzmann neuralnet allows edges between any pair of nodes, but its subset topology RBM does not.
	*/
	public final boolean self;
	
	public EdlayInfo(boolean flip, boolean self, int sq){
	//public EdlayInfo(boolean flip, boolean self, int sq, float aveWeight){
		this.flip = flip;
		this.self = self;
		this.sq = sq;
		//this.aveWeight = aveWeight;
	}

}
