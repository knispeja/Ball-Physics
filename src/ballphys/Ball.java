/**
 * 
 */
package ballphys;

import java.awt.Color;
import java.awt.geom.Point2D;

/**
 * Describes a generic ball, which can exist in the main world
 * 
 * @author knispeja. Created Dec 9, 2014.
 */
public class Ball implements Updateable {

	private boolean ghost;

	private final double DEGREES_TO_RADIANS = 0.017453; // 0.0174532925
	private final double VEL_LOWER_BOUND = .35;

	private double x;
	private double xvel;

	private double y;
	private double yvel;

	private double elasticity;
	private double radius;
	private Color color;

	private WorldCommunicator wc;

	/**
	 * Creates a ball with all the given values, in the given world.
	 * 
	 * @param wc
	 * 
	 * @param containingWorld
	 * @param x
	 * @param y
	 * @param elasticity
	 * @param radius
	 * @param color
	 * @param ghost
	 * @param invisible
	 */
	public Ball(WorldCommunicator wc, double x, double y, double elasticity,
			double radius, Color color, boolean ghost) {

		this.wc = wc;

		this.ghost = ghost;
		this.color = color;

		this.x = x;
		this.y = y;
		this.elasticity = elasticity;
		this.radius = radius;

		this.xvel = 0;
		this.yvel = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mainapp.Updateable#update()
	 */
	@Override
	public void update() {

		double timeScale = this.wc.getTimeScale();

		if(this.xvel < this.VEL_LOWER_BOUND){
			this.x += this.xvel * (timeScale);
			this.xvel += this.wc.getXAccel(this) * timeScale;
		} else {
			this.xvel += this.wc.getXAccel(this) * timeScale;
			this.x += this.xvel * (timeScale);
		}
		
		if(this.yvel < this.VEL_LOWER_BOUND){
			this.y -= this.yvel * (timeScale);
			this.yvel += this.wc.getYAccel(this) * timeScale;
		} else {
			this.yvel += this.wc.getYAccel(this) * timeScale;
			this.y -= this.yvel * (timeScale);
		}

		if (!this.ghost) {
			for (Ball b2 : this.wc.getBalls()) {
				if (!b2.ghost && b2 != this)
					intersects(b2);
			}
		}
	}

	/**
	 * To be used when a ball is bouncing off a wall, specifically
	 * 
	 * @param leftRight
	 *            determines the direction the ball will bounce
	 */
	public void bounceWall(boolean leftRight) {
		double timeScale = this.wc.getTimeScale();
		this.xvel -= this.wc.getXAccel(this) * timeScale;
		this.yvel -= this.wc.getYAccel(this) * timeScale;
		
		double newxvel = this.xvel * this.elasticity;
		double newyvel = this.yvel * this.elasticity;
		if (leftRight) {
			newxvel *= -1;
		} else {
			newyvel *= -1;
		}

		if (Math.abs(newxvel) < this.VEL_LOWER_BOUND)
			this.xvel = 0;
		else
			this.xvel = newxvel;

		if (Math.abs(newyvel) < this.VEL_LOWER_BOUND)
			this.yvel = 0;
		else
			this.yvel = newyvel;
		
		int ms = (int) (Math.abs(this.xvel*10) + Math.abs(this.yvel*10));
		if(ms >= 75)
			this.wc.playTone(Note.A4$, ms);
	}

	/**
	 * To be used when a ball is bouncing off of a non-wall object
	 * 
	 * @param theta
	 *            The angle the ball will bounce *toward*, in degrees
	 */
	public void bounce(double theta) {

		double thetaRadians = this.DEGREES_TO_RADIANS * theta;

		double totalvel = Math.sqrt(this.xvel * this.xvel + this.yvel
				* this.yvel)
				* this.elasticity;
		double newxvel = -Math.signum(theta) * (Math.cos(thetaRadians))
				* totalvel;
		double newyvel = (Math.sin(thetaRadians)) * totalvel;

		if (Math.abs(newxvel) < this.VEL_LOWER_BOUND)
			this.xvel = 0;
		else
			this.xvel = newxvel;

		if (Math.abs(newyvel) < this.VEL_LOWER_BOUND)
			this.yvel = 0;
		else
			this.yvel = newyvel;
	}

	/**
	 * Checks if this object intersects with another of the same type
	 * 
	 * @param ball
	 *            The other ball we want to check for intersection
	 * @return True if this object is intersecting ball
	 */
	public boolean intersects(Ball ball) {

		double timeScale = this.wc.getTimeScale();

		double x1 = this.getX();
		double y1 = this.getY();

		double x2 = ball.getX();
		double y2 = ball.getY();

		double hyp = Point2D.distance(x1, y1, x2, y2);
		boolean returnval = (hyp < this.getRadius() + ball.getRadius());

		if (returnval) {
			// They intersected

			double base = Math.abs(x2 - x1);
			double theta = Math.toDegrees(Math.atan2(hyp, base));

			// ball is to the right of this
			if (x2 - x1 >= 0) {
				// ball is over this
				if (y2 - y1 >= 0) {
					this.bounce(theta + 180);
					ball.bounce(theta);
					// ball is under this
				} else {
					this.bounce(-theta + 180);
					ball.bounce(-theta + 360);
				}
				// ball is to the left of this
			} else {
				// ball is over this
				if (y2 - y1 >= 0) {
					this.bounce(-theta + 360);
					ball.bounce(-theta + 180);
					// ball is under this
				} else {
					this.bounce(theta);
					ball.bounce(theta + 180);
				}
			}

			this.x -= this.xvel * timeScale;
			this.y += this.yvel * timeScale;

			double tempxvel = this.xvel;
			double tempyvel = this.yvel;

			this.xvel = ball.xvel;
			this.yvel = ball.yvel;

			ball.xvel = tempxvel;
			ball.yvel = tempyvel;

		}

		return returnval;
	}

	/**
	 * Returns this ball's color
	 * 
	 * @return color of the ball
	 */
	public Color getColor() {
		return this.color;
	}

	/**
	 * Returns value of ghost
	 * 
	 * @return whether or not this ball is a ghost
	 */
	public boolean getGhost() {
		return this.ghost;
	}

	/**
	 * Returns this ball's radius
	 * 
	 * @return radius, in pixels
	 */
	public double getRadius() {
		return this.radius;
	}

	/**
	 * Returns this ball's x coordinate
	 * 
	 * @return x-coord, in pixels
	 */
	public double getX() {
		return this.x;
	}

	/**
	 * Returns this ball's y coordinate
	 * 
	 * @return y-coord, in pixels
	 */
	public double getY() {
		return this.y;
	}

	/**
	 * Returns this ball's x velocity
	 * 
	 * @return x-vel
	 */
	public double getxvel() {
		return this.xvel;
	}

	/**
	 * Returns this ball's y velocity
	 * 
	 * @return y-vel
	 */
	public double getyvel() {
		return this.yvel;
	}

	/**
	 * Sets this ball's x coordinate
	 * 
	 * @param x
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * Sets this ball's y coordinate
	 * 
	 * @param y
	 */
	public void setY(double y) {
		this.y = y;
	}

}
