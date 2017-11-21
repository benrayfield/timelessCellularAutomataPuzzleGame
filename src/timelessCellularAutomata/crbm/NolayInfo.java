package timelessCellularAutomata.crbm;

/** node layer info, but nothing specific to 1 node */
public class NolayInfo{
	
	public final boolean isBit;
	
	public final float mass;
	
	public final float bias;
	
	//FIXME bring back aveNodeVal when layerAttention param is added to GameState funcs.
	//public final float aveNodeVal;
	
	public NolayInfo(boolean isBit, float mass, float bias){
	//public NolayInfo(boolean isBit, float mass, float bias, float aveNodeVal){
		this.isBit = isBit;
		this.mass = mass;
		this.bias = bias;
		//this.aveNodeVal = aveNodeVal;
	}

}