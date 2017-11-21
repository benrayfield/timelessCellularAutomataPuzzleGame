package timelessCellularAutomata.crbm;

/** Convolutional RBM. Use as immutable despite the technical ability to modify weight array. */
public class Crbm{
	
	public final CrbmInfo info;
	
	/** weight[toLayer][fromLayer][info.sq>>1+upVsDown*dy][info.sq>>1+upVsDown*dx]
	where upVsDown is 1 if inference up (or same layer) or -1 if inference down (or same layer),
	and upVsDown=info.ei[toLayer][fromLayer].flip?1:-1;
	<br><br>
	UPDATE: Use EdlayInfo.flip and EdlayInfo.self to make this a flat array,
	with 2 pointers to each float[][] except just 1 on diagonal.
	Since there are no self edges in RBM topology, there will be no such diagonals in RBM
	but the datastruct supports it for possible future expansion.
	OLD...
	Dims are triangle triangle square square, to match the CrbmInfo.
	weight[highOrEqualLayer][lowOrEqualLayer][info.sq>>1+upVsDown*dy][info.sq>>1+upVsDown*dx]
	where upVsDown is 1 if inference up or -1 if inference down. Inference can be done in any combination,
	including only up, only down, both at once, or any random subset of nodes inferring to any subset.
	This sparse datastruct is an optimization of dense 4d weight array
	where each layer has a 3d weight array toAndFrom all near nodes in 3d node array.
	Must match the CrbmInfo.
	FIXME: For future compatibility with ufnode, which does not efficiently support triangle arrays,
	change the triangle to square and reuse last 2 dims that are already square, so approx same memory.
	*/
	public final float[][][][] weight;
	
	public Crbm(CrbmInfo info, float[][][][] weight){
		this.info = info;
		this.weight = weight;
	}
	
	//TODO public Crbm learn()
	
	/** array sizes are [bigSquare][bigSquare][smallSquare][smallSquare], and the last 2 dims may be null */
	public static float[][][][] copySparseWeightsArray(float[][][][] copyMe){
		float[][][][] w = new float[copyMe.length][copyMe.length][][];
		for(int to=0; to<copyMe.length; to++){
			for(int from=0; from<copyMe.length; from++){
				if(copyMe[to][from] != null){
					w[to][from] = new float[copyMe[to][from].length][copyMe[to][from].length];
					for(int y=0; y<copyMe[to][from].length; y++){
						System.arraycopy(copyMe[to][from][y], 0, w[to][from][y], 0, copyMe[to][from].length);
					}
				}
			}
		}
		return w;
		
		/*float[][][][] ret = new float[copyMe.length];
		for(int a=0; a<ret.length; a++){ //do all 4 loops here so get arrays near in memory for cacheLocality
			if(copyMe[a] != null){
				ret[a] = new float[ret[a].length][][];
				for(int b=0; b<ret[a].length; b++){
					if(copyMe[a][b] != null){
						ret[a][b] = new float[ret[a].length][][];
						for(int b=0; b<ret[a].length; b++){
							
						}
					}
				}
			}
		}*/
	}

}
