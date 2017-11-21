/** Ben F Rayfield offers HumanAiCore opensource GNU LGPL */
package humanaicore.ui;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import java.util.function.*;
import javax.swing.JPanel;

/** Interactive pixel grid that stretches to panel size, normally low resolution
because of the slowness of an IntBinaryOperator function call per magnified pixel.
Remembers which keys and mouse buttons are up and down and mouse position
and which square the mouse is over. Displays colored squares
as IntBinaryOperator of (y,x) to colorARGB.
*/
public class StretchVideo extends JPanel implements MouseListener, MouseMotionListener{
	
	//TODO optimize: use DrawPixelsAsInts which uses MemoryImageSource which is inTheory faster than BufferedImage
	
	public IntBinaryOperator painter;
	
	public final int squaresTall, squaresWide;
	
	protected final BufferedImage img;
	
	public final Set<Integer> mouseButtonsDown = new HashSet<Integer>();
	
	public int mouseY, mouseX;
	
	public int selectedCellY(){
		return Math.max(0, Math.min(mouseY*squaresTall/getHeight(), squaresTall-1));
	}
	
	public int selectedCellX(){
		return Math.max(0, Math.min(mouseX*squaresWide/getWidth(), squaresWide-1));
	}
	
	/** The Rect is how many squares tall and wide. The Bifunction takes (y,x) and returns colorARGB.
	If painter is null, then caller must set the pixels in the BufferedImage directly.
	*/
	public StretchVideo(int squaresTall, int squaresWide, IntBinaryOperator painter){
		this.squaresTall = squaresTall;
		this.squaresWide = squaresWide;
		this.painter = painter;
		img = new BufferedImage(squaresWide, squaresTall, BufferedImage.TYPE_4BYTE_ABGR);
		/*addMouseMotionListener(new MouseMotionListener(){
			public void mouseMoved(MouseEvent e){
				mouseY = e.getY();
				mouseX = e.getX();
				StretchVideo.this.repaint();
			}
			public void mouseDragged(MouseEvent e){ mouseMoved(e); }
		});*/
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	public void paint(Graphics g){
		if(painter != null){ //else caller already set BufferedImage pixels directly
			for(int y=0; y<squaresTall; y++){
				for(int x=0; x<squaresWide; x++){
					int colorARGB = painter.applyAsInt(y, x);
					img.setRGB(x, y, colorARGB);
				}
			}
		}
		double yMagnify = (double)getHeight()/squaresTall;
		double xMagnify = (double)getWidth()/squaresWide;
		if(g instanceof Graphics2D){ //stretch to panel size
			Graphics2D G = (Graphics2D)g;
			AffineTransform aftrans = new AffineTransform(xMagnify, 0, 0, yMagnify, 0, 0);
			G.drawImage(img, aftrans, this);
		}else{ //so you can see something but it will be very small
			g.drawImage(img, 0, 0, this);
		}
	}
	
	/** called when mouse or keyboard event, and maybe in subclasses other events such as RBM changed. */
	public void event(){
		repaint(); //only needed if IntBinaryOperator depends on the stored mouse and keyboard state
	}
	
	public void mouseDragged(MouseEvent e){ mouseMoved(e); }

	public void mouseMoved(MouseEvent e){
		mouseY = e.getY();
		mouseX = e.getX();
		event();
	}

	public void mouseClicked(MouseEvent e){}

	public void mousePressed(MouseEvent e){
		mouseButtonsDown.add(e.getButton());
		event();
	}

	public void mouseReleased(MouseEvent e){
		mouseButtonsDown.remove(e.getButton());
		event();
	}

	public void mouseEntered(MouseEvent e){
		event();
	}

	public void mouseExited(MouseEvent e){
		event();
	}

}
