/**
 * 
 */
package ballphys;

import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * Paints all the balls for the main portion of the project
 *
 * @author knispeja.
 *         Created Dec 11, 2014.
 */
public class BallWindow extends JPanel{
	
	private WorldCommunicator wc;
	
	/**
	 * Creates a generic BallWindow in the given world
	 * 
	 * @param wc 
	 */
	public BallWindow(WorldCommunicator wc){
		super();
		this.wc = wc;
	}
	
	@Override
	public void paintComponent(final Graphics g){
		if(!this.wc.getPaintballs())
			super.paintComponent(g);
    	for(Ball ball:this.wc.getBalls()){
    		int radius = (int) ball.getRadius();
    		int doubleRadius = radius * 2;
    		g.setColor(ball.getColor());
    		g.fillOval((int) ball.getX() - radius, (int) ball.getY() - radius, doubleRadius, doubleRadius);
    	}
	}
}
