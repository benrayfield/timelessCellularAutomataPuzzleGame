package timelessCellularAutomata.crbm;
import javax.swing.JFrame;

import humanaicore.common.Rand;
import humanaicore.common.SVar;
import humanaicore.common.ScreenUtil;
import timelessCellularAutomata.crbm.*;
import timelessCellularAutomata.crbm.ui.SpacePanel;

public class Start{
	public static void main(String[] args){
		int nolay0Size = 1;
		//int nolay1Size = 32;
		int nolay1Size = 11; //FIXME should be at least 32 so can do rule110 etc in any of 4 directions
		//int nolay1Size = 5; //FIXME should be at least 32 so can do rule110 etc in any of 4 directions
		//int nolay1Size = 1; //FIXME should be at least 32 so can do rule110 etc in any of 4 directions
		//int nolay1Size = 3; //FIXME should be at least 32 so can do rule110 etc in any of 4 directions
		int layers = nolay0Size+nolay1Size;
		//int ySize = 120, xSize = 120;
		//int ySize = 300, xSize = 120;
		//int ySize = 80, xSize = 80;
		//int ySize = 50, xSize = 50;
		int ySize = 60, xSize = 60; //TODO its a little slow. multithread.
		NolayInfo[] ni = new NolayInfo[layers];
		EdlayInfo[][] ei = new EdlayInfo[layers][layers];
		float[][][][] weight = new float[layers][layers][][];
		float nolay0Mass = 1f/nolay0Size;
		float nolay1Mass = 1f/nolay1Size;
		//float nolay0Bias = -1.7f;
		//float nolay1Bias = -2.2f;
		//float nolay0Bias = -.7f;
		//float nolay1Bias = -1.2f;
		//float nolay0Bias = -1.7f;
		//float nolay1Bias = -5.2f;
		//float nolay0Bias = -.03f;
		//float nolay1Bias = -.1f;
		//float nolay0Bias = -1.7f, nolay1Bias = -2.2f;
		float nolay0Bias = 1.4f, nolay1Bias = 1.2f;
		//int sq = 11;
		int sq = 7;
		//int sq = 5;
		//int sq = 3; //FIXME more
		boolean visibleNodesAreBit = false;
		boolean hiddenNodesAreBit = true; //FIXME true
		for(int layer=0; layer<nolay0Size; layer++){
			ni[layer] = new NolayInfo(visibleNodesAreBit,nolay0Mass,nolay0Bias);
		}
		for(int layer=nolay0Size; layer<layers; layer++){
			ni[layer] = new NolayInfo(hiddenNodesAreBit,nolay1Mass,nolay1Bias);
		}
		//float weightAve = 0, weightStdDev = 7.1f;
		//float weightAve = 0, weightStdDev = 1.2f;
		//float weightAve = 0, weightStdDev = 2.2f;
		//float weightAve = 0, weightStdDev = 3f;
		
		//float weightAve = -.5f, weightStdDev = 2.5f;
		float weightAve = -.5f, weightStdDev = 2f;
		for(int toLayer=0; toLayer<layers; toLayer++){
			for(int fromLayer=0; fromLayer<layers; fromLayer++){
				if(fromLayer < nolay0Size && nolay0Size <= toLayer){
					ei[toLayer][fromLayer] = new EdlayInfo(false, false, sq);
					weight[toLayer][fromLayer] = new float[sq][sq];
					Util.writeRandBells(weight[toLayer][fromLayer], Rand.strongRand, weightAve, weightStdDev);
				}
			}
		}
		for(int toLayer=0; toLayer<layers; toLayer++){
			for(int fromLayer=0; fromLayer<layers; fromLayer++){
				if(toLayer < nolay0Size && nolay0Size <= fromLayer){
					ei[toLayer][fromLayer] = new EdlayInfo(true, false, sq); //true means mirror
					weight[toLayer][fromLayer] = weight[fromLayer][toLayer]; //reuse it twice except diag
				}
			}
		}
		Crbm crbmState = new Crbm(new CrbmInfo(ni, ei), weight);
		float[][][] zyxState = new float[layers][ySize][xSize];
		float decay = .95f;
		GameState firstGameState = new GameState(crbmState, zyxState, decay);
		
		
		JFrame window = new JFrame("timelessCellularAutomata neural paint with mouse buttons");
		SVar gameVar = new SVar(firstGameState);
		window.add(new SpacePanel(gameVar));
		window.setSize(700, 700);
		ScreenUtil.moveToScreenCenter(window);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}
}
