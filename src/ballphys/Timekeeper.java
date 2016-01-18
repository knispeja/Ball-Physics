/**
 * 
 */
package ballphys;

/**
 * This class runs the clock, and updates the rest of the app
 *
 * @author knispeja.
 *         Created Dec 9, 2014.
 */
public class Timekeeper implements Updateable{
	
	/**
	 * The main world to be updated
	 */
	Updateable updateMe;
	
	
	/**
	 * The thread this timekeeper is managing
	 */
	Thread currentThread;
	
	/**
	 * Constructs a generic timekeeper, with a world to update per tick
	 * 
	 * @param world
	 */
	public Timekeeper(Updateable world){
		this.updateMe = world;
	}
	
	/**
	 * Starts execution of this timekeeper's thread
	 */
	public void startExecution(){
		
		final long SLEEP_TIME = 20;
		
		Runnable tickTock = new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(SLEEP_TIME);
					} catch (InterruptedException e) {
						System.err.println("Problem sleeping: " + e.getMessage());
					}
					update();
				}
			}
		};

		Thread thread = new Thread(tickTock);
		this.currentThread = thread;
		thread.start();
	}
	
	/**
	 * Joins this timekeeper's thread (terminates it)
	 */
	public void stopExecution(){
		try {
			this.currentThread.join();
		} catch (InterruptedException e) {
			System.err.println("Error joining thread: " + e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ballphys.Updateable#update()
	 */
	@Override
	public void update() {
		this.updateMe.update();
	}
}