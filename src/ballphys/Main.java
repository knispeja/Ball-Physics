/**
 * 
 */
package ballphys;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.Box;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * The main world, contains all balls and handles all interactions!
 * 
 * @author knispeja. Created Dec 9, 2014.
 */
public class Main extends Applet implements Updateable, MouseListener,
		MouseMotionListener, ActionListener, ChangeListener {

	private final int WINDOW_WIDTH = 730;
	private final int WINDOW_HEIGHT = 715;

	private final int X_ACCELERATION_MIN = -5;
	private final int X_ACCELERATION_DEF = 0;
	private final int X_ACCELERATION_MAX = 5;
	private final int X_ACCELERATION_MINOR = 1;
	private final int X_ACCELERATION_MAJOR = 5;

	private final int Y_ACCELERATION_MIN = -5;
	private final int Y_ACCELERATION_DEF = -1;
	private final int Y_ACCELERATION_MAX = 5;
	private final int Y_ACCELERATION_MINOR = 1;
	private final int Y_ACCELERATION_MAJOR = 5;

	private final int ELAST_MIN = 0;
	private final int ELAST_MAX = 100;
	private final int ELAST_DEF = 80;
	private final int ELAST_MINOR = 5;
	private final int ELAST_MAJOR = 20;

	private final int TIME_MIN = 0;
	private final int TIME_MAX = 200;
	private final int TIME_DEF = 100;
	private final int TIME_MINOR = 20;
	private final int TIME_MAJOR = 50;

	private final int RADIUS_MIN = 1;
	private final int RADIUS_MAX = 101;
	private final int RADIUS_DEF = 25;
	private final int RADIUS_MINOR = 5;
	private final int RADIUS_MAJOR = 20;

	private final double GRAVITY_MULTIPLIER = .05;

	private ArrayList<Ball> balls;

	private double mouseX;
	private double mouseY;

	private double timeScale;
	private Random rand;

	private JSlider sbElasticity;
	private JSlider sbTimescale;
	private JSlider sbRadius;
	private JSlider sbXAccel;
	private JSlider sbYAccel;

	private JPanel mainPanel;
	private Button btnReset;

	private JTabbedPane tabPane;
	private JColorChooser colorpick;

	private Checkbox cbGravmouse;
	private Checkbox cbPaintballs;
	private Checkbox cbVertBounds;
	private Checkbox cbGhost;
	private Checkbox cbRandcolors;
	private Checkbox cbPlanets;

	private WorldCommunicator wc;
	private MusicBox mb;

	@Override
	public void init() {

		// Create a communicator all objects will use with this main world
		this.wc = new WorldCommunicator(this);
		
		// Create a music box which will play all sounds
		this.mb = new MusicBox();
		
		// Initialize randomness
		this.rand = new Random();

		// Set up the layout for the applet frame
		setLayout(new BorderLayout());

		// Setup all sliders, buttons, panels, etc...
		this.tabPane = new JTabbedPane();

		JPanel containerMainTab = new JPanel();
		JPanel panelNorthEast = new JPanel();
		JPanel panelNorthWest = new JPanel();
		JPanel panelEast = new JPanel();

		JPanel containerOptionsTab = new JPanel();
		containerOptionsTab.setLayout(new GridLayout(1, 2));

		panelEast.setLayout(new GridLayout(2, 1));
		panelNorthWest.setLayout(new GridLayout(2, 1));

		// Elasticity slider bar and label
		JPanel elast = new JPanel();
		elast.add(new JLabel("Nonstick"));
		this.sbElasticity = initializeSlider(true, this.ELAST_MIN,
				this.ELAST_MAX, this.ELAST_DEF, this.ELAST_MINOR,
				this.ELAST_MAJOR);
		elast.add(this.sbElasticity);
		panelNorthWest.add(elast);

		// Radius slider bar and label
		JPanel rad = new JPanel();
		rad.add(new JLabel(" Radius "));
		this.sbRadius = initializeSlider(true, this.RADIUS_MIN,
				this.RADIUS_MAX, this.RADIUS_DEF, this.RADIUS_MINOR,
				this.RADIUS_MAJOR);
		rad.add(this.sbRadius);
		panelNorthWest.add(rad);

		JPanel panelSouth = new JPanel();

		// Timescale slider bar and label
		panelSouth.add(new JLabel("Time Scale"));
		this.sbTimescale = initializeSlider(true, this.TIME_MIN, this.TIME_MAX,
				this.TIME_DEF, this.TIME_MINOR, this.TIME_MAJOR);
		this.timeScale = this.TIME_DEF / 100.0;
		this.sbTimescale.addChangeListener(this);
		panelSouth.add(this.sbTimescale);
		panelSouth.add(Box.createRigidArea(new Dimension(170, 0)));

		// X acceleration slider bar and label
		panelSouth.add(new JLabel("X Accel"));
		this.sbXAccel = initializeSlider(true, this.X_ACCELERATION_MIN,
				this.X_ACCELERATION_MAX, this.X_ACCELERATION_DEF,
				this.X_ACCELERATION_MINOR, this.X_ACCELERATION_MAJOR);
		panelSouth.add(this.sbXAccel);

		// Reset button
		JPanel temp = new JPanel();
		this.btnReset = new Button("Reset");
		temp.add(this.btnReset);
		this.btnReset.addActionListener(this);
		panelEast.add(temp);

		// Y acceleration slider bar and label
		JPanel tempPanel = new JPanel();
		tempPanel.setLayout(new BorderLayout());
		tempPanel.add(new JLabel("Y Accel"), BorderLayout.NORTH);
		this.sbYAccel = initializeSlider(false, this.Y_ACCELERATION_MIN,
				this.Y_ACCELERATION_MAX, this.Y_ACCELERATION_DEF,
				this.Y_ACCELERATION_MINOR, this.Y_ACCELERATION_MAJOR);
		tempPanel.add(this.sbYAccel, BorderLayout.CENTER);
		panelEast.add(tempPanel);

		this.cbGhost = new Checkbox("Ghost");
		panelNorthEast.add(this.cbGhost);

		this.cbPaintballs = new Checkbox("Paintballs");
		panelNorthEast.add(this.cbPaintballs);

		this.cbGravmouse = new Checkbox("Gravity Mouse");
		panelNorthEast.add(this.cbGravmouse);
		
		this.cbPlanets = new Checkbox("Planets");
		panelNorthEast.add(this.cbPlanets);

		this.cbVertBounds = new Checkbox("Side Walls");
		panelNorthEast.add(this.cbVertBounds);

		JPanel temp2 = new JPanel();
		temp2.setLayout(new BorderLayout());
		temp2.add(panelNorthWest, BorderLayout.WEST);
		temp2.add(panelNorthEast, BorderLayout.EAST);

		containerOptionsTab.setLayout(new GridLayout(2, 1));
		containerOptionsTab.add(temp2);

		JPanel temp3 = new JPanel();
		temp3.setLayout(new BorderLayout());
		this.cbRandcolors = new Checkbox("Random Colors");
		this.cbRandcolors.setState(true);
		temp3.add(this.cbRandcolors, BorderLayout.NORTH);

		this.colorpick = new JColorChooser();
		// this.colorpick.setPreviewPanel(new JPanel());

		temp3.add(this.colorpick, BorderLayout.SOUTH);
		containerOptionsTab.add(temp3);

		// JLabel tempLabel = new JLabel("Paused");
		// tempLabel.setFont(new Font("TimesRoman", Font.BOLD, 24));
		// containerOptionsTab.add(tempLabel, BorderLayout.SOUTH);

		GridBagConstraints c = new GridBagConstraints();
		containerMainTab.setLayout(new GridBagLayout());

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.SOUTH;
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = .1;
		c.weighty = 0;
		containerMainTab.add(panelSouth, c);

		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1000;
		c.weighty = 1000;
		this.mainPanel = new BallWindow(this.wc);
		this.mainPanel.setBackground(Color.white);
		containerMainTab.add(this.mainPanel, c);

		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.VERTICAL;
		c.anchor = GridBagConstraints.EAST;
		c.weightx = 0;
		c.weighty = .1;
		containerMainTab.add(panelEast, c);

		this.tabPane.addTab("Main", containerMainTab);
		this.tabPane.addTab("Options", containerOptionsTab);
		add(this.tabPane);

		this.balls = new ArrayList<Ball>();

		// Begin execution of the applet
		Timekeeper tk = new Timekeeper(this);
		tk.startExecution();

		// Add mouse listener to the applet
		this.mainPanel.addMouseListener(this);
		this.mainPanel.addMouseMotionListener(this);
	}

	private void addBall(double x, double y) {
		try {
			Color color;
			boolean ghost = this.cbGhost.getState();
			if (this.cbRandcolors.getState()) {
				// Generate a random pastel color for the ball
				float hue = this.rand.nextFloat();
				float saturation = (this.rand.nextInt(2000) + 1000) / 10000f;
				float luminance = (this.rand.nextFloat() + 0.25f) / 1.25f;
				color = Color.getHSBColor(hue, saturation, luminance);
			} else {
				color = this.colorpick.getColor();
			}

			Ball newBall = new Ball(this.wc, x, y,
					this.sbElasticity.getValue() / 100.0,
					this.sbRadius.getValue(), color, ghost);

			// Check to make sure the user isn't trying to create the ball
			// anywhere illegal
			if (!ghost) {
				if (newBall.getY() + newBall.getRadius() > this.WINDOW_HEIGHT) {
					return;
				}
				if (this.cbVertBounds.getState()) {
					if (newBall.getX() - newBall.getRadius() < 0) {
						return;
					}
					if (newBall.getX() + newBall.getRadius() > this.WINDOW_WIDTH) {
						return;
					}
				}

				for (Ball ball : this.balls) {
					double x1 = newBall.getX();
					double y1 = newBall.getY();

					double x2 = ball.getX();
					double y2 = ball.getY();

					double hyp = Point2D.distance(x1, y1, x2, y2);
					if (hyp < newBall.getRadius() + ball.getRadius()) {
						return;
					}
				}
			}

			this.balls.add(newBall);

		} catch (Exception e) {
			// Do nothing >_> *whistles*
		}
	}

	private JSlider initializeSlider(boolean horz, int min, int max, int def,
			int minor, int major) {

		int orientation;
		if (horz)
			orientation = SwingConstants.HORIZONTAL;
		else
			orientation = SwingConstants.VERTICAL;

		JSlider newSlider = new JSlider(orientation, min, max, def);
		newSlider.setMinorTickSpacing(minor);
		newSlider.setMajorTickSpacing(major);
		newSlider.setPaintLabels(true);
		newSlider.setPaintTicks(true);
		return newSlider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mainapp.Updateable#update()
	 */
	@Override
	public void update() {
		try {
			for (Ball ball : this.balls) {
				ball.update();
				if (ball.getY() + ball.getRadius() > this.WINDOW_HEIGHT) {
					ball.bounceWall(false);
					ball.setY(this.WINDOW_HEIGHT - ball.getRadius());
				}
				if (this.cbVertBounds.getState()) {
					if (ball.getX() - ball.getRadius() < 0) {
						ball.bounceWall(true);
						ball.setX(ball.getRadius());
					}
					if (ball.getX() + ball.getRadius() > this.WINDOW_WIDTH) {
						ball.bounceWall(true);
						ball.setX(this.WINDOW_WIDTH - ball.getRadius());
					}
				}
			}

			this.mainPanel.repaint();
		} catch (Exception e) {
			// Do nothing >_>
		}
	}

	/**
	 * Use the music box to play a tone
	 *
	 * @param note
	 * @param ms
	 */
	public void playTone(Note note, int ms){
		//this.mb.play(note, ms);
	}
	
	/**
	 * Grabs the current x acceleration as defined by a slider
	 * 
	 * @param caller
	 * 
	 * @return value of acceleration in the x-direction
	 */
	public int getXAccel(Ball caller) {
		int xAccel = this.sbXAccel.getValue();
		
		if (!this.cbGravmouse.getState())
			return xAccel;

		double ballX = caller.getX();

		return (int) ((this.mouseX - ballX) * this.GRAVITY_MULTIPLIER) + xAccel;
	}

	/**
	 * Grabs the current y acceleration as defined by a slider
	 * 
	 * @param caller
	 * 
	 * @return value of acceleration in the y-direction
	 */
	public int getYAccel(Ball caller) {
		int yAccel = this.sbYAccel.getValue();
		
		if (!this.cbGravmouse.getState())
			return yAccel;

		double ballY = caller.getY();
		
		return (int) ((ballY - this.mouseY) * this.GRAVITY_MULTIPLIER) + yAccel;
	}

	/**
	 * Return the timescale percentage
	 * 
	 * @return timescale
	 */
	public double getTimeScale() {
		if (this.tabPane.getSelectedIndex() != 1)
			return this.timeScale;

		return 0;
	}

	/**
	 * Returns the value that determines whether or not balls are paintballs
	 * 
	 * @return value describing whether or not balls are paintballs
	 */
	public boolean getPaintballs() {
		return this.cbPaintballs.getState();
	}

	/**
	 * Returns the list that contains all balls in the world
	 * 
	 * @return the arraylist of all the active balls
	 */
	public ArrayList<Ball> getBalls() {
		return this.balls;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.btnReset) {
			this.balls.clear();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		// Nothing here
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		addBall(e.getX(), e.getY());
		e.consume();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent
	 * )
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		if (this.cbGravmouse.getState()) {
			this.mouseX = e.getX();
			this.mouseY = e.getY();
			e.consume();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent
	 * )
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		this.timeScale = this.sbTimescale.getValue() / 100.0;
	}
}