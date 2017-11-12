/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package timelessCellularAutomata.ui;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import humanaicore.common.Rand;
import humanaicore.ui.StretchVideo;
import timelessCellularAutomata.BoltzCellAutomata;

public class GamePanel extends StretchVideo{
	
	public final BoltzCellAutomata game;

	public GamePanel(BoltzCellAutomata game){
		super(
			game.space.length,
			game.space[0].length,
			(int y, int x)->{ //position on screen to colorARGB
				//return 0xff000000 | Rand.strongRand.nextInt(0x1000000); //FIXME shouldnt be random
				float brightFraction = game.space[y][x];
				int brightByte = Math.max(0,Math.min((int)(brightFraction*256),255));
				return 0xff000000 + brightByte * 0x10101;
			}
		);
		this.game = game;
		addMouseMotionListener(new MouseMotionListener(){
			public void mouseMoved(MouseEvent e){ game.nextState(); }
			public void mouseDragged(MouseEvent e){ mouseMoved(e); }
		});
	}

}
