package timelessCellularAutomata.crbm;

/** Immutable. All crbm arrays are square in y z and rectangle with z.
OLD: Not triangle array cuz EdlayInfo.flip obsoleted that.
ni.length==ei.length && ei[layer].length==layer+1 && 0<=layer && layer<ni.length
&& ni[layer] != null && ei[layer]!=null; ei[layer][any] is null if no weights there.
*/
public class CrbmInfo{
	
	public final NolayInfo[] ni;
	
	/** UPDATE: SQUARE ARRAY AND USE EdlayInfo.flip and EdlayInfo.self.
	Triangle array with diag. Null, or varying EdlayInfo.squareSide, where sparse.
	*/
	public final EdlayInfo[][] ei;
	
	/** Odd. maxSq>>1 distances from y x edges of game volume cant update by cellular automata
	cuz would read from outside array.
	*/
	public final int maxSq;
	
	public CrbmInfo(NolayInfo[] ni, EdlayInfo[][] ei){
		this.ni = ni;
		this.ei = ei;
		int maxSqTemp = -1;
		for(EdlayInfo[] eia : ei){
			for(EdlayInfo e : eia){
				if(e != null) maxSqTemp = Math.max(maxSqTemp, e.sq);
			}
		}
		maxSq = maxSqTemp;
	}

}
