/**
 * 
 */
package ballphys;

import java.util.ArrayList;

/**
 * Communicates between individual balls and the world
 *
 * @author knispeja.
 *         Created Dec 14, 2014.
 */
public class WorldCommunicator {

	/**
	 * The world with which to communicate
	 */
	Main containingWorld;
	
	/**
	 * Constructs a communicator with the given world
	 *
	 * @param world
	 */
	public WorldCommunicator(Main world){
		this.containingWorld = world;
	}
	
	/**
	 * Play a note for ms milliseconds
	 *
	 * @param note
	 * @param ms
	 */
	public void playTone(Note note, int ms){
		this.containingWorld.playTone(note, ms);
	}
	
	/**
	 * Gets all the balls in the world
	 *
	 * @return all balls
	 */
	public ArrayList<Ball> getBalls(){
		return this.containingWorld.getBalls();
	}
	
	/**
	 * Gets the time scale of the world
	 *
	 * @return timescale
	 */
	public double getTimeScale(){
		return this.containingWorld.getTimeScale();
	}
	
	/**
	 * Returns whether or not paintballs is on
	 *
	 * @return paintballs
	 */
	public boolean getPaintballs(){
		return this.containingWorld.getPaintballs();
	}
	
	/**
	 * Gets the proper x acceleration of the given ball
	 *
	 * @param caller
	 * @return x-accel
	 */
	public double getXAccel(Ball caller){
		return this.containingWorld.getXAccel(caller);
	}
	
	/**
	 * Gets the proper y acceleration of the given ball
	 *
	 * @param caller
	 * @return y-accel
	 */
	public double getYAccel(Ball caller){
		return this.containingWorld.getYAccel(caller);
	}
}
