/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package timelessCellularAutomata.crbm.ui;
import humanaicore.common.ScreenUtil;
import humanaicore.common.Time;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Map;

import humanaicore.common.Rand;
import humanaicore.common.SVar;
import humanaicore.ui.StretchVideo;
import timelessCellularAutomata.crbm.GameState;

public class SpacePanel extends StretchVideo{
	
	public final SVar<GameState> game;
	
	public final Map inputs = new HashMap();
	
	protected double timeLastState = Time.time();

	public SpacePanel(final SVar<GameState> g){
		super(
			g.get().zyx[0].length,
			g.get().zyx[0][0].length,
			(int y, int x)->0xff888888 //replace with painter
		);
		this.game = g;
		painter = (int y, int x)->{ //position on screen to colorARGB
			
			//return 0xff000000 | Rand.strongRand.nextInt(0x1000000); //FIXME shouldnt be random
			float brightFraction = SpacePanel.this.game.get().zyx[0][y][x]; //FIXME is SVar synchronized too slow for this?
			int brightByte = Math.max(0,Math.min((int)(brightFraction*256),255));
			return 0xff000000 + brightByte * 0x10101;
			
			/*float red = 0;
			float green = (float)x/game.get().zyx[0][0].length;
			float blue = (float)y/game.get().zyx[0].length;
			return ScreenUtil.color(red, green, blue);
			*/
		};
		game.startListening(
			(SVar<GameState> gameVar)->{
				repaint();
			}
		);
		addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e){}
			public void mousePressed(MouseEvent e){
				inputs.put("mouseButton"+e.getButton(), 1.);
			}
			public void mouseReleased(MouseEvent e){
				inputs.put("mouseButton"+e.getButton(), 0.);
			}
			public void mouseEntered(MouseEvent e){}
			public void mouseExited(MouseEvent e){}
			
		});
		
		addMouseMotionListener(new MouseMotionListener(){
			public void mouseMoved(MouseEvent e){
				inputs.put("mouseCellY", (double)selectedCellY());
				inputs.put("mouseCellX", (double)selectedCellX());
				gameNextState(new HashMap(inputs));
			}
			public void mouseDragged(MouseEvent e){ mouseMoved(e); }
		});
	}
	
	public void gameNextState(Map inputs){
		double now = Time.time();
		double dt = Math.max(0, Math.min(now-timeLastState, .1));
		timeLastState = now;
		inputs.put("dt", dt);
		game.accept(game.get().nextState(inputs,Rand.strongRandBits));
	}

}
