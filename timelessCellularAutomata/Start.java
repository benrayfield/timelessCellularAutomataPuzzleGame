/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package timelessCellularAutomata;

import javax.swing.JFrame;

import humanaicore.common.Rand;
import humanaicore.common.ScreenUtil;
import timelessCellularAutomata.ui.GamePanel;

public class Start{
	
	public static void main(String[] args){
		JFrame window = new JFrame("timelessCellularAutomataPuzzleGame");
		int bigSquare = 120;
		int smallSquare = 19;
		float bias = 44.25f;
		BoltzCellAutomata game = new BoltzCellAutomata(new float[bigSquare][bigSquare], new float[smallSquare][smallSquare], bias);
		for(float[] row : game.space){
			for(int x=0; x<bigSquare; x++){
				row[x] = Rand.strongRand.nextFloat();
			}
		}
		float weightSum = 0;
		for(int smallY=0; smallY<smallSquare; smallY++){
			for(int smallX=0; smallX<smallSquare; smallX++){
				//float bifraction = Rand.strongRand.nextFloat()*2-1;
				//smallRow[smallX] = bifraction/smallSquare;
				if(smallX!=smallSquare/2 || smallY!=smallSquare/2){ //self weight must be 0
					int dy = smallY-smallSquare/2, dx = smallX-smallSquare/2;
					//float dist = (float)Math.sqrt(dy*dy + dx*dx);
					int distSq = dy*dy + dx*dx;
					//float influence = 1f/(27+distSq);
					//float influence = 1f/(10+distSq);
					//float distPow = 5f;
					//float distPow = 3.4f;
					//float distPow = 1.5f;
					float distPow = 2.5f;
					float influence = 1f/(83.35f+(float)Math.pow(distSq, .5*distPow));
					//float dist = (float)Math.sqrt(distSq);
					//float influence = 1f/(3+dist*dist*dist);
					game.weights[smallY][smallX] = -Rand.strongRand.nextFloat()*influence;
				}
				weightSum += game.weights[smallY][smallX];
			}
		}
		//float weightMult = -2*bias/weightSum; //nodes average about .5
		//float weightMult = -2.3f*bias/weightSum;
		//float weightMult = -12.3f*bias/weightSum;
		//float weightMult = -4.3f*bias/weightSum;
		float weightMult = -2f*bias/weightSum;
		for(int smallY=0; smallY<smallSquare; smallY++){
			for(int smallX=0; smallX<smallSquare; smallX++){
				game.weights[smallY][smallX] *= weightMult;
			}
		}
		game.weakestSymmetryNorm(); //on game.weights. required to guarantee boltz convergence.
		//game.distanceSymmetryNorm(); //on game.weights. guarantees boltz convergence and more symmetry than that
		for(int smallY=0; smallY<smallSquare; smallY++){
			for(int smallX=0; smallX<smallSquare; smallX++){
				String s = (game.weights[smallY][smallX]+"000000").substring(0,5);
				System.out.print(" "+s);
			}
			System.out.println();
		}
		window.add(new GamePanel(game));
		window.setSize(700, 700);
		ScreenUtil.moveToScreenCenter(window);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}

}
