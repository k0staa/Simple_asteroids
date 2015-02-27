/*Asteroid game made 
 * by Michal Kostewicz
 * 
 * 
 */

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_SPACE;
import static java.awt.event.KeyEvent.VK_UP;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.BufferStrategy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class SimpleGame implements Runnable {

	final int WIDTH = 1000;
	final int HEIGHT = 700;
	final int shipShapeTableX[] = new int[] { 480, 500, 520, 500 }; // Table of
																	// x points
																	// of ship
																	// shape
	final int shipShapeTableY[] = new int[] { 370, 360, 370, 330 }; // Table of
																	// y points
																	// of ship
																	// shape
	int shipHeadDegrees = 0; // This is used to measure angle of ship using
								// degrees
	boolean shooting = false; // Used to draw bullets in render method
	int shootingTime; // Time duration of bullet life
	int numberOfComets = 12; // Number of comets in game
	boolean killingComplete = true; // This tells when method killComet is
									// active
	boolean explode = false; // This tells when bullet hit and destroy comet

	long desiredFPS = 70;
	long desiredDeltaLoop = (1000 * 1000 * 1000) / desiredFPS;
	long fps;

	boolean running = false; // Main loop boolean
	boolean gamePaused = false; // Boolean used for pausing game
	boolean gameOver = false; // tells if game is already played and finish;
	int howManyKilled; // Counting how many comets been destroyed
	int pointsGained;
	// SOUNDS
	AudioClip laser;
	AudioClip cometExplode;
	AudioClip music;
	// FRAME AND GRAPHICS
	JFrame frame;
	Canvas canvas;
	BufferStrategy bufferStrategy;
	// GAME OBJECTS
	Shape shapeShip;
	Shape shapeBullet;
	Shape comet;
	ArrayList<Shape> explosionParticles;
	ArrayList<Shape> cometList;
	ArrayList<Integer> cometDirectionList;
	ArrayList<Integer> particlesExplosionTime;
	KeyControl myKeyControl;
	Thread gameThread;

	public SimpleGame() {
		gameThread = new Thread(this);

		frame = new JFrame("Basic Asteroid Game");

		JPanel panel = (JPanel) frame.getContentPane();
		panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		panel.setLayout(null);

		canvas = new Canvas();
		canvas.setBounds(0, 0, WIDTH, HEIGHT);
		canvas.setIgnoreRepaint(true);

		panel.add(canvas);

		myKeyControl = new KeyControl();
		canvas.addKeyListener(myKeyControl);

		frame.setJMenuBar(addMenuBar());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);

		canvas.createBufferStrategy(2);
		bufferStrategy = canvas.getBufferStrategy();

		canvas.requestFocus();

		URL sndLaser = this.getClass().getResource("laser.wav");
		URL sndMusic = this.getClass().getResource("music.wav");
		URL sndExplode = this.getClass().getResource("explode.wav");
		laser = Applet.newAudioClip(sndLaser);
		cometExplode = Applet.newAudioClip(sndExplode);
		music = Applet.newAudioClip(sndMusic);
		music.loop(); 
		

		shapeShip = new SpaceShip(shipShapeTableX, shipShapeTableY);
		explosionParticles = new ArrayList<Shape>();
		cometList = new ArrayList<Shape>();
		cometList = createRandCometList(cometList, numberOfComets);
		cometDirectionList = new ArrayList<Integer>();
		cometDirectionList = this.getRandDirections(cometDirectionList,
				numberOfComets);
		particlesExplosionTime = new ArrayList<Integer>();

	}

	// Create the menu bar.
	private JMenuBar addMenuBar() {
		final JMenuBar menuBar;
		JMenu menu_game, menu_help;
		JMenuItem menuItemStart;
		JMenuItem menuItemPause;
		JMenuItem menuItemAbout;
		JMenuItem menuItemHelp;

		menuBar = new JMenuBar();

		menu_game = new JMenu("Game");
		menu_game.setMnemonic(KeyEvent.VK_A);
		menu_game.getAccessibleContext().setAccessibleDescription("Game menu.");

		menu_help = new JMenu("Help");
		menu_help.getAccessibleContext().setAccessibleDescription("Help menu.");

		menuBar.add(menu_game);
		menuBar.add(menu_help);

		menuItemStart = new JMenuItem("Start", KeyEvent.VK_T);
		menuItemStart.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if ("Start".equals(e.getActionCommand())) {
					if (gamePaused & gameOver == false) {
						gamePaused = false;
					} else if (gameOver) {
						shapeShip = new SpaceShip(shipShapeTableX,
								shipShapeTableY);
						explosionParticles = new ArrayList<Shape>();
						cometList = new ArrayList<Shape>();
						cometList = createRandCometList(cometList,
								numberOfComets);
						cometDirectionList = new ArrayList<Integer>();
						cometDirectionList = getRandDirections(
								cometDirectionList, numberOfComets);
						particlesExplosionTime = new ArrayList<Integer>();
						shipHeadDegrees = 0;
						pointsGained = 0;
						myKeyControl.movingLeft = false;
						myKeyControl.movingRight = false;
						myKeyControl.movingUp = false;

						gameOver = false;
						gamePaused = false;

					} else {

						running = true;
						gameThread.start();
						canvas.requestFocus();
					}
				}

			}

		});
		menuItemPause = new JMenuItem("Pause", KeyEvent.VK_Q);
		menuItemPause.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if ("Pause".equals(e.getActionCommand())) {
					gamePaused = true;
				}

			}

		});
		menuItemAbout = new JMenuItem("About", KeyEvent.VK_H);
		menuItemAbout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String aboutText = "Asteroid game ver 0.1 \nCreated by Michal Kostewicz";
				JOptionPane.showMessageDialog(frame, aboutText);

			}

		});
		menuItemHelp = new JMenuItem("Game Help", KeyEvent.VK_H);
		menuItemHelp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String aboutText = "Aim of this game:\n"
						+ "-destroy comets and gain points\n"
						+ "-don't get kill\n" + "-have fun\n" + "Stering:\n"
						+ "-Left arrow to trun left\n"
						+ "-Right arrow to turn right\n"
						+ "Up arrow to thrust!";
				JOptionPane.showMessageDialog(frame, aboutText);

			}

		});
		menu_help.add(menuItemHelp);
		menu_help.add(menuItemAbout);
		menu_game.add(menuItemStart);
		menu_game.add(menuItemPause);
		return menuBar;
	}

	private class KeyControl extends KeyAdapter {

		boolean movingLeft = false;
		boolean movingRight = false;
		boolean movingUp = false;

		@Override
		public void keyPressed(KeyEvent ke) {
			if (ke.getKeyCode() == VK_UP) {
				movingUp = true;

			}
			if (ke.getKeyCode() == VK_LEFT) {
				movingLeft = true;

			}
			if (ke.getKeyCode() == VK_RIGHT) {
				movingRight = true;

			}
			if (ke.getKeyCode() == VK_SPACE) {
				shootBullet();
				System.out.println("WOWWW");
				shooting = true;
				laser.play();
				shootingTime = 0;

			}
		}

		public void keyReleased(KeyEvent ke) {
			if (ke.getKeyCode() == VK_UP) {
				movingUp = false;

			}
			if (ke.getKeyCode() == VK_LEFT) {
				movingLeft = false;

			}
			if (ke.getKeyCode() == VK_RIGHT) {
				movingRight = false;

			}
		}

	}

	// metod creates bullet after pressing SPACE key
	void shootBullet() {

		int bulletTableX[] = new int[] {
				(int) shapeShip.getBounds().getCenterX(),
				((int) shapeShip.getBounds().getCenterX()) - 2,
				(int) shapeShip.getBounds().getCenterX(),
				((int) shapeShip.getBounds().getCenterX()) + 2 };
		int bulletTableY[] = new int[] {
				(int) shapeShip.getBounds().getCenterY(),
				((int) shapeShip.getBounds().getCenterY()) - 2,
				((int) shapeShip.getBounds().getCenterY()) - 4,
				((int) shapeShip.getBounds().getCenterY()) - 2 };
		shapeBullet = new Bullet(bulletTableX, bulletTableY);

	}

	// metod creates particles when comet explode. Particles looks same as
	// bullets but there is more of them.
	void addExplosionParticles(Shape destroyedComet) {
		Shape temp;

		for (int i = 0; i < 13; i++) {
			int bulletTableX[] = new int[] {
					(int) destroyedComet.getBounds().getCenterX(),
					((int) destroyedComet.getBounds().getCenterX()) - 2,
					(int) destroyedComet.getBounds().getCenterX(),
					((int) destroyedComet.getBounds().getCenterX()) + 2 };
			int bulletTableY[] = new int[] {
					(int) destroyedComet.getBounds().getCenterY(),
					((int) destroyedComet.getBounds().getCenterY()) - 2,
					((int) destroyedComet.getBounds().getCenterY()) - 4,
					((int) destroyedComet.getBounds().getCenterY()) - 2 };
			temp = new Bullet(bulletTableX, bulletTableY);
			explosionParticles.add(i, temp);
			particlesExplosionTime.add(i, 0);
		}
	}

	// main loop for the game
	public void run() {
		long beginLoopTime;
		long endLoopTime;
		long currentUpdateTime = System.nanoTime();
		long lastUpdateTime;
		long deltaLoop;

		// this is main loop for our game where we setup frame rate for later
		// use in update method
		while (running) {
			while (gamePaused) {
				try {
					Thread.currentThread().sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			beginLoopTime = System.nanoTime();

			render();

			lastUpdateTime = currentUpdateTime;
			currentUpdateTime = System.nanoTime();
			update((int) ((currentUpdateTime - lastUpdateTime) / (1000 * 1000)));

			endLoopTime = System.nanoTime();
			deltaLoop = endLoopTime - beginLoopTime;
			if (deltaLoop > 0) {
				fps = 100000000 / deltaLoop;
			}
			// This loop is to maintain proper FPS (default 60)
			if (deltaLoop > desiredDeltaLoop) {
				// Do nothing. Frame Rate are low now!.
				System.out.println("low FPS: " + fps);
			} else {
				try {
					Thread.sleep((desiredDeltaLoop - deltaLoop) / (1000 * 1000));
				} catch (InterruptedException e) {
					// Do nothing
				}
			}

		}

	}

	void movingShip() {
		if (myKeyControl.movingLeft) {

			changeHeadShapePos(false);
			shapeShip = AffineTransform.getRotateInstance(Math.toRadians(-3),
					shapeShip.getBounds2D().getCenterX(),
					shapeShip.getBounds2D().getCenterY())
					.createTransformedShape(shapeShip);

		}
		if (myKeyControl.movingRight) {
			changeHeadShapePos(true);

			shapeShip = AffineTransform.getRotateInstance(Math.toRadians(3),
					shapeShip.getBounds2D().getCenterX(),
					shapeShip.getBounds2D().getCenterY())
					.createTransformedShape(shapeShip);

		}
		if (myKeyControl.movingUp) {
			System.out.println("UP!: ");
			shapeShip = moveShape(shapeShip, 9);
			shapeShip = isWindowsEnds(shapeShip.getBounds().getLocation(),
					shapeShip);

		}
	}

	// This metod is used to measure angle of the ship
	void changeHeadShapePos(boolean rightArrow) {
		if (rightArrow == true) {
			if (shipHeadDegrees < 357) {
				shipHeadDegrees += 3;
			} else {
				shipHeadDegrees = 0;
			}
		} else {
			if (shipHeadDegrees > 2) {
				shipHeadDegrees -= 3;
			} else {
				shipHeadDegrees = 357;
			}
		}
	}

	// Method used just for preparing main window of game and starts
	// render(Graphics2D) to draw
	// all elements of game

	private void render() {
		Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
		g.setBackground(Color.black);
		g.clearRect(0, 0, WIDTH, HEIGHT);
		render(g);
		g.dispose();
		bufferStrategy.show();
	}

	// This is for TESTING (yellow rectangle)
	private double x = 0;

	// Main updating method. All changes are here.
	protected void update(int deltaTime) {
		x += deltaTime * 0.2;

		if (shooting) {

			shapeBullet = moveShape(shapeBullet, 10);
			shootingTime++;
			// this loop check if bullet hits comet
			for (int i = 0; i < cometList.size(); i++) {
				if (testIntersection(cometList.get(i), shapeBullet)) {
					if (cometList.get(i) != null) {
						this.addExplosionParticles(cometList.get(i));
					}
					this.explode = true;
					cometExplode.play(); // explosion sound play
					killingComplete = false;
					this.destroyComet(i); // THIS SOMETIMES MOVING OUT OF
											// cometList SIZE ! NEEDS TO BE
											// MOVED OUTSIDE OF THIS LOOP
					this.howManyKilled++;
					this.pointsGained += 5;
				}

			}

		}
		// check if another comet hits comet
		if (killingComplete == true) {
			for (int i = 0; i < cometList.size(); i++) {
				for (int y = 0; y < cometList.size(); y++) {
					if (cometList.get(i) != cometList.get(y)) {
						if (testIntersection(cometList.get(i), cometList.get(y))) {
							this.addExplosionParticles(cometList.get(i));
							this.explode = true;
							killingComplete = false;
							this.destroyComet(i); // THIS SOMETIMES MOVING OUT
													// OF cometList SIZE ! NEEDS
													// TO BE MOVED OUTSIDE OF
													// THIS LOOP
							this.howManyKilled++;
						}
					}
				}
			}
		}
		for (int i = 0; i < cometList.size(); i++) {
			if (testIntersection(cometList.get(i), shapeShip)) {
				gameOver = true;
				gamePaused = true;
				JOptionPane.showMessageDialog(frame, "GAME OVER!");
			}
		}
		// this loop is for moving explosion Particles.
		if (explode) {
			int x = 0;
			for (int i = 0; i < explosionParticles.size(); i++) {
				explosionParticles.set(i,
						moveShape(explosionParticles.get(i), x, 1));
				if (x > 12) {
					x = 0;
				} else
					x++;
				particlesExplosionTime
						.set(i, particlesExplosionTime.get(i) + 1);
				if (particlesExplosionTime.get(i) > 70) {
					explosionParticles.remove(i);
					particlesExplosionTime.remove(i);
				}
			}
		}
		for (int i = 0; i < cometList.size(); i++) {

			cometList.set(i,
					moveShape(cometList.get(i), cometDirectionList.get(i), 1));
			cometList.set(i, rotateShape(cometList.get(i)));
			cometList.set(
					i,
					isWindowsEnds(cometList.get(i).getBounds().getLocation(),
							cometList.get(i)));

		}
		while (x > 500) {
			x = 0;
			if (howManyKilled > 0) {
				cometList = this.createRandCometList(cometList, 1);
				cometDirectionList = this.getRandDirections(cometDirectionList,
						1);
				howManyKilled--;

			}

		}
		if (shootingTime > 30) {
			shooting = false;

		}
		movingShip();

	}

	// Method used to draw all components after updating them
	protected void render(Graphics2D g) {
		drawKillerPolygon(g);
		if (shooting) {
			drawBullet(g);
		}
		if (explode) {
			for (Shape sh : explosionParticles) {
				drawComet(g, sh);
			}
		}
		g.setColor(Color.yellow);
		g.drawString(String.valueOf(fps) + " fps", 900, 50); // Showing string
																// with FPS on
																// screen
		g.drawString("angle: " + String.valueOf(shipHeadDegrees) + " deg.",
				900, 30); // Showing angle of the ship
		g.setColor(Color.red);
		g.drawString("SCORE:: " + String.valueOf(pointsGained), 900, 10); // Showing
																			// player
																			// score
		// Drawing from list of comets
		if (true) {
			for (Shape sh : cometList) {
				drawComet(g, sh);
			}

		}
	}

	// method used for destroying comets when bullet hits them
	private void destroyComet(int cometId) {
		System.out.println("Zabijam " + cometId);
		cometList.remove(cometId);
		cometList.trimToSize();
		cometDirectionList.remove(cometId);
		cometDirectionList.trimToSize();
		killingComplete = true;
	}

	// method used to draw gamer ship
	private void drawKillerPolygon(Graphics2D g2) {
		g2.setColor(Color.red);
		g2.draw(shapeShip);
		g2.fill(shapeShip);

	}

	// metod used to draw bullet when shooting
	private void drawBullet(Graphics2D g2) {
		g2.setColor(Color.yellow);
		g2.draw(shapeBullet);
		g2.fill(shapeBullet);
	}

	// metod used to draw comet
	private void drawComet(Graphics2D g2, Shape comet) {

		g2.setColor(Color.blue);
		g2.draw(comet);
		g2.fill(comet);
	}

	// metod used for testing when some shape hit another
	private boolean testIntersection(Shape shapeA, Shape shapeB) {
		Area areaA = new Area(shapeA);
		areaA.intersect(new Area(shapeB));
		return !areaA.isEmpty();
	}

	// This method checking if any shape reach end of window and needs to be
	// 'teleported' to another side of window
	private Shape isWindowsEnds(Point location, Shape shapeToTeleport) {
		AffineTransform at;
		if (location.getX() > 999) {
			at = new AffineTransform();
			at.translate(-999, 0);
			shapeToTeleport = at.createTransformedShape(shapeToTeleport);
			return shapeToTeleport;
		} else if (location.getX() < 1) {
			at = new AffineTransform();
			at.translate(999, 0);
			shapeToTeleport = at.createTransformedShape(shapeToTeleport);
			return shapeToTeleport;
		} else if (location.getY() > 699) {
			at = new AffineTransform();
			at.translate(0, -699);
			shapeToTeleport = at.createTransformedShape(shapeToTeleport);
			return shapeToTeleport;
		} else if (location.getY() < 1) {
			at = new AffineTransform();
			at.translate(0, +699);
			shapeToTeleport = at.createTransformedShape(shapeToTeleport);
			return shapeToTeleport;
		} else
			return shapeToTeleport;

	}

	// class represent shape of gamer space ship
	private static class SpaceShip extends Polygon {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public SpaceShip(int x1, int y1, int x2, int y2, int x3, int y3,
				int x4, int y4) {
			super(new int[] { x1, x2, x3, x4 }, new int[] { y1, y2, y3, y4 }, 4);
		}

		public SpaceShip(int tableX[], int tableY[]) {
			this(tableX[0], tableY[0], tableX[1], tableY[1], tableX[2],
					tableY[2], tableX[3], tableY[3]);
		}

	}

	// class represent bullet shape (it's dot)

	private static class Bullet extends Polygon {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Bullet(int x1, int y1, int x2, int y2, int x3, int y3, int x4,
				int y4) {
			super(new int[] { x1, x2, x3, x4 }, new int[] { y1, y2, y3, y4 }, 4);
		}

		public Bullet(int tableX[], int tableY[]) {
			this(tableX[0], tableY[0], tableX[1], tableY[1], tableX[2],
					tableY[2], tableX[3], tableY[3]);
		}
	}

	private static class Comet extends Polygon {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Comet(int x1, int y1, int x2, int y2, int x3, int y3, int x4,
				int y4, int x5, int y5, int x6, int y6) {
			super(new int[] { x1, x2, x3, x4, x5, x6 }, new int[] { y1, y2, y3,
					y4, y5, y6 }, 6);

		}

		public Comet(int tableX[], int tableY[]) {
			this(tableX[0], tableY[0], tableX[1], tableY[1], tableX[2],
					tableY[2], tableX[3], tableY[3], tableX[4], tableY[4],
					tableX[5], tableY[5]);

		}

	}

	// method that creates Array List of comets (you can chose how many)
	// using random position
	private ArrayList<Shape> createRandCometList(
			ArrayList<Shape> finalShapeList, int howManyComets) {

		// Random generating different numbers
		Random generator = new Random();

		int randTab[] = new int[howManyComets];

		for (int i = 0; i < howManyComets; i++) {
			generator.setSeed(System.nanoTime());
			int temp = generator.nextInt(5); // chose one from 5 different
												// comets show below

			randTab[i] = temp;
			System.out.println("Chosen Comet nb: " + temp);

		}
		for (int tempInt = 0; tempInt < randTab.length; tempInt++) {
			generator.setSeed(System.nanoTime());
			int x = generator.nextInt(WIDTH - 40);
			int y = generator.nextInt(HEIGHT - 40);
			while( (x < shapeShip.getBounds2D().getMaxX() & x > shapeShip.getBounds2D().getMinX()) &
				(y < shapeShip.getBounds2D().getMaxY() & y > shapeShip.getBounds2D().getMinY())){
					x = generator.nextInt(WIDTH - 40);
					y = generator.nextInt(HEIGHT - 40);
				}
			
			
			switch (randTab[tempInt]) {
			case 0:
				finalShapeList.add(new Comet(x, y, x + 20, y - 5, x + 40,
						y + 5, x + 30, y + 30, x + 10, y + 20, x + 10, y + 10));
				break;
			case 1:
				finalShapeList.add(new Comet(x, y, x + 20, y - 5, x + 40,
						y + 5, x + 30, y + 30, x + 10, y + 20, x + 10, y + 10));
				break;
			case 2:
				finalShapeList
						.add(new Comet(x, y, x + 19, y - 21, x - 1, y - 37,
								x - 22, y - 34, x - 26, y - 31, x - 12, y - 22));
				break;

			case 3:
				finalShapeList.add(new Comet(x, y, x + 20, y - 5, x + 40,
						y + 5, x + 30, y + 30, x + 10, y + 20, x + 10, y + 10));
				break;
			case 4:
				finalShapeList
						.add(new Comet(x, y, x + 20, y - 15, x + 45, y + 10,
								x + 30, y + 35, x + 10, y + 25, x + 10, y + 15));
				break;
			case 5:
				finalShapeList.add(new Comet(x, y, x + 20, y - 5, x + 40,
						y + 5, x + 30, y + 20, x + 10, y + 20, x + 10, y + 10));
				break;
			}
		}

		return finalShapeList;
	}

	// second method for creating Array List of comets ,but this time using just
	// some preset shapes
	// but chosing them randomly
	private ArrayList<Shape> createCometList(ArrayList<Shape> ShapeList,
			int howManyComets) {
		ArrayList<Shape> cList = new ArrayList<Shape>();
		cList.add(new Comet(30, 20, 50, 15, 70, 25, 60, 50, 40, 40, 40, 30));
		cList.add(new Comet(130, 120, 150, 115, 170, 125, 160, 150, 140, 140,
				140, 130));
		cList.add(new Comet(56, 67, 75, 46, 55, 30, 34, 33, 30, 36, 44, 45));
		cList.add(new Comet(230, 220, 250, 215, 270, 225, 260, 250, 240, 240,
				240, 230));
		cList.add(new Comet(430, 415, 450, 400, 475, 425, 460, 450, 440, 440,
				440, 430));
		cList.add(new Comet(730, 720, 750, 715, 770, 725, 760, 750, 740, 740,
				740, 730));
		// Random generating different numbers
		Random generator = new Random();

		int randTab[] = new int[howManyComets];
		boolean numbIsThere = false;

		for (int i = 0; i < howManyComets;) {
			generator.setSeed(System.nanoTime());
			int temp = generator.nextInt(cList.size());

			for (int j = 0; j < i; j++) {
				if (temp == randTab[j]) {
					numbIsThere = true;
				} else {
					numbIsThere = false;
				}
			}
			if (!numbIsThere) {
				randTab[i] = temp;
				System.out.println("Chosen Comet nb: " + temp);
				ShapeList.add((Shape) cList.get(temp));
				i++;
			}
		}

		return ShapeList;

	}

	// method used to create random direction for comets.
	private ArrayList<Integer> getRandDirections(ArrayList<Integer> intList,
			int howMany) {
		Random gen = new Random();
		gen.setSeed(System.currentTimeMillis());
		// ArrayList intList = new <Integer>ArrayList();
		for (int i = 0; i < howMany; i++) {
			intList.add(gen.nextInt(12));

		}
		return intList;
	}

	// method used to rotate shapes. Created mainly for comets.
	public Shape rotateShape(Shape shape) {
		AffineTransform at = new AffineTransform();

		at.rotate(Math.PI / 1000, shape.getBounds().getCenterX(), shape
				.getBounds().getCenterY());
		shape = at.createTransformedShape(shape);

		return shape;

	}

	public Shape moveShape(Shape shape, int dist) {
		AffineTransform at;
		if (shipHeadDegrees == 0) {
			at = new AffineTransform();

			at.translate(0, -dist);
			shape = at.createTransformedShape(shape);
		}
		if (shipHeadDegrees > 0 & shipHeadDegrees < 90) {
			at = new AffineTransform();
			int tempDistY = dist - (shipHeadDegrees / 10);
			int tempDistX = shipHeadDegrees / 10;
			at.translate(tempDistX, -tempDistY);
			shape = at.createTransformedShape(shape);
		}
		if (shipHeadDegrees == 90) {
			at = new AffineTransform();

			at.translate(dist, 0);
			shape = at.createTransformedShape(shape);
		}
		if (shipHeadDegrees > 90 & shipHeadDegrees < 180) {
			at = new AffineTransform();
			int tempDist = ((shipHeadDegrees - 90) / 10);
			int tempDistX = dist - ((shipHeadDegrees - 90) / 10);
			at.translate(tempDistX, tempDist);
			shape = at.createTransformedShape(shape);
		}
		if (shipHeadDegrees == 180) {
			at = new AffineTransform();

			at.translate(0, dist);
			shape = at.createTransformedShape(shape);
		}
		if (shipHeadDegrees > 180 & shipHeadDegrees < 270) {
			at = new AffineTransform();
			int tempDist = dist - ((shipHeadDegrees - 180) / 10);
			int tempDistX = ((shipHeadDegrees - 180) / 10);
			at.translate(-tempDistX, tempDist);
			shape = at.createTransformedShape(shape);
		}
		if (shipHeadDegrees == 270) {
			at = new AffineTransform();

			at.translate(-dist, 0);
			shape = at.createTransformedShape(shape);
		}
		if (shipHeadDegrees > 270 & shipHeadDegrees < 360) {
			at = new AffineTransform();
			int tempDist = ((shipHeadDegrees - 270) / 10);
			int tempDistX = dist - ((shipHeadDegrees - 270) / 10);
			at.translate(-tempDistX, -tempDist);
			shape = at.createTransformedShape(shape);
		}
		if (shipHeadDegrees == 360) {
			at = new AffineTransform();

			at.translate(0, -dist);
			shape = at.createTransformedShape(shape);
		}
		return shape;
	}

	// method using to move all shape using AffineTransform function -Now its
	// used only for Comets
	public Shape moveShape(Shape shape, int direction, int dist) {
		AffineTransform at;

		switch (direction) {
		case 1:
			at = new AffineTransform();

			at.translate(0, -dist);
			shape = at.createTransformedShape(shape);

			break;
		case 2:
			at = new AffineTransform();

			at.translate(dist, -dist * Math.toRadians(60));
			shape = at.createTransformedShape(shape);
			break;
		case 3:
			at = new AffineTransform();

			at.translate(dist, -dist * Math.toRadians(30));
			shape = at.createTransformedShape(shape);
			break;

		case 4:
			at = new AffineTransform();

			at.translate(dist, 0);
			shape = at.createTransformedShape(shape);
			break;
		// ok
		case 5:
			at = new AffineTransform();

			at.translate(dist, dist * Math.toRadians(30));
			shape = at.createTransformedShape(shape);
			break;
		case 6:
			at = new AffineTransform();

			at.translate(dist, dist * Math.toRadians(60));
			shape = at.createTransformedShape(shape);
			break;
		case 7:
			at = new AffineTransform();

			at.translate(0, dist);
			shape = at.createTransformedShape(shape);
			break;
		case 8:
			at = new AffineTransform();

			at.translate(-dist, dist * Math.toRadians(60));
			shape = at.createTransformedShape(shape);

			break;
		case 9:
			at = new AffineTransform();

			at.translate(-dist, dist * Math.toRadians(30));
			shape = at.createTransformedShape(shape);

			break;
		case 10:
			at = new AffineTransform();

			at.translate(-dist, 0);
			shape = at.createTransformedShape(shape);
			break;
		// ok
		case 11:
			at = new AffineTransform();

			at.translate(-dist, -dist * Math.toRadians(30));
			shape = at.createTransformedShape(shape);
			break;
		case 12:
			at = new AffineTransform();

			at.translate(-dist, -dist * Math.toRadians(60));
			shape = at.createTransformedShape(shape);
			break;

		}
		return shape;
	}

	// Main function

	public static void main(String[] args) {
		SimpleGame ex = new SimpleGame();
		// new Thread(ex).start();
	}

}
