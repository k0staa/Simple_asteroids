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
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Toolkit;
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
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class SimpleGame implements Runnable {

	final static int WIDTH = 1000;
	final static int HEIGHT = 700;
	// Tables of points used to configure shape of the ship.//
	final int shipShapeTableX[] = new int[] { 480, 500, 520, 500 }; 
	final int shipShapeTableY[] = new int[] { 370, 360, 370, 330 }; 
	//-----------------------------------------------------//
	int howManyKilled; // Counting how many comets has been destroyed//
	int pointsGained;  //Points gained during game//
	int shipHeadDegrees = 0;  	// Angle of ship in degrees//
	int shootingTime; 			// Time duration of bullet life//
	int numberOfComets = 12; 	// Number of comets in game-TO DO In-game config//
	
	//Data used to configure frame per second in game //
	long desiredFPS = 70;
	long desiredDeltaLoop = (1000 * 1000 * 1000) / desiredFPS;
	long fps;
	//-----------------------------------------------//
	boolean shooting = false; 		// Drawing bullets when true//
	boolean killingComplete = true; // Method killComet is active//								
	boolean explode = false; 		// Bullet hit and destroy comet when true//
	boolean running = false; 		// Main game loop//
	boolean gamePaused = false; 	// Used for pausing game//
	boolean gameOver = false; 		// Tells if game is already played and finish//
	
	// SOUNDS //
	AudioClip laser;
	AudioClip cometExplode;
	AudioClip music;
	// FRAME AND GRAPHICS //
	JFrame frame;
	Canvas canvas;
	BufferStrategy bufferStrategy;
	Image backgroundImg;
	// GAME OBJECTS //
	Shape shapeShip;
	Shape shapeBullet;
	Shape comet;
	ArrayList<Shape> explosionParticles;
	ArrayList<Shape> cometList;
	ArrayList<Integer> cometDirectionList;
	ArrayList<Integer> particlesExplosionTime;
	// CONTROLS //
	KeyControl myKeyControl;
	// GAME THREAD //
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
		try {
			
			backgroundImg = ImageIO.read(new File("src/background.gif"));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Image can't be loaded");
		}
		panel.add(canvas);

		myKeyControl = new KeyControl(this);
		
		canvas.addKeyListener(myKeyControl);

		frame.setJMenuBar(addMenuBar());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);

		canvas.createBufferStrategy(2);
		bufferStrategy = canvas.getBufferStrategy();

		canvas.requestFocus();
		
		//Sound initialization
		URL sndLaser = this.getClass().getResource("laser.wav");
		URL sndMusic = this.getClass().getResource("music.wav");
		URL sndExplode = this.getClass().getResource("explode.wav");
		laser = Applet.newAudioClip(sndLaser);
		cometExplode = Applet.newAudioClip(sndExplode);
		music = Applet.newAudioClip(sndMusic);
		music.loop(); 
		
		//Shapes initialization
		shapeShip = new MyShapes.SpaceShip(shipShapeTableX, shipShapeTableY);
		explosionParticles = new ArrayList<Shape>();
		cometList = new ArrayList<Shape>();
		cometList = MyShapes.createRandCometList(cometList, numberOfComets,shapeShip);
		cometDirectionList = new ArrayList<Integer>();
		cometDirectionList = MyShapes.getRandDirections(cometDirectionList,
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
						shapeShip = new MyShapes.SpaceShip(shipShapeTableX,
								shipShapeTableY);
						explosionParticles = new ArrayList<Shape>();
						cometList = new ArrayList<Shape>();
						cometList = MyShapes.createRandCometList(cometList,
								numberOfComets,shapeShip);
						cometDirectionList = new ArrayList<Integer>();
						cometDirectionList = MyShapes.getRandDirections(
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



	// Method creates bullet after pressing SPACE key
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
		shapeBullet = new MyShapes.Bullet(bulletTableX, bulletTableY);

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
			temp = new MyShapes.Bullet(bulletTableX, bulletTableY);
			explosionParticles.add(i, temp);
			particlesExplosionTime.add(i, 0);
		}
	}

	// Main game loop //
	public void run() {
		long beginLoopTime;
		long endLoopTime;
		long currentUpdateTime = System.nanoTime();
		long lastUpdateTime;
		long deltaLoop;

		// This is main loop for game //
		while (running) {
			while (gamePaused) {
				try {
					Thread.currentThread().sleep(100);
				} catch (InterruptedException e) {
					
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
				// Do nothing. Frame Rate are low now.
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


	// Method used just for preparing main window of game and starts
	// render(Graphics2D) to draw all elements of game

	private void render() {
		Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
		//g.setBackground(Color.black);
		g.clearRect(0, 0, WIDTH, HEIGHT);
		g.drawImage(backgroundImg, 0, 0, null);
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

			shapeBullet = MyShapes.moveShape(shipHeadDegrees,shapeBullet, 10);
			shootingTime++;
			// this loop check if bullet hits comet
			for (int i = 0; i < cometList.size(); i++) {
				if (MyShapes.testIntersection(cometList.get(i), shapeBullet)) {
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
						if (MyShapes.testIntersection(cometList.get(i), cometList.get(y))) {
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
			if (MyShapes.testIntersection(cometList.get(i), shapeShip)) {
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
						MyShapes.moveShape(explosionParticles.get(i), x, 1));
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
					MyShapes.moveShape(cometList.get(i), cometDirectionList.get(i), 1));
			cometList.set(i, MyShapes.rotateShape(cometList.get(i)));
			cometList.set(
					i,
					MyShapes.isWindowsEnds(cometList.get(i).getBounds().getLocation(),
							cometList.get(i)));

		}
		while (x > 500) {
			x = 0;
			if (howManyKilled > 0) {
				cometList = MyShapes.createRandCometList(cometList, 1, shapeShip);
				cometDirectionList = MyShapes.getRandDirections(cometDirectionList,
						1);
				howManyKilled--;

			}

		}
		if (shootingTime > 30) {
			shooting = false;

		}
		myKeyControl.movingShip();

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
		// Showing string with FPS on screen ---------------//
		g.setColor(Color.yellow);
		g.drawString(String.valueOf(fps) + " fps", 900, 50); 
		
		// Showing angle of the ship -----------------------------------//
		g.drawString("angle: " + String.valueOf(shipHeadDegrees) + " deg.",
				900, 30); 
		
		// Showing player score during game ---------------------------//
		g.setColor(Color.red);
		g.drawString("SCORE:: " + String.valueOf(pointsGained), 900, 10); 
		
		// Drawing from list of comets ------//
		if (true) {
			for (Shape sh : cometList) {
				drawComet(g, sh);
			}

		}
	}

	// Method used for destroying comets when bullet hits them
	private void destroyComet(int cometId) {
		System.out.println("Zabijam " + cometId);
		cometList.remove(cometId);
		cometList.trimToSize();
		cometDirectionList.remove(cometId);
		cometDirectionList.trimToSize();
		killingComplete = true;
	}

	// Method used to draw gamer ship
	private void drawKillerPolygon(Graphics2D g2) {
		g2.setColor(Color.red);
		g2.draw(shapeShip);
		g2.fill(shapeShip);

	}

	// Method used to draw bullets
	private void drawBullet(Graphics2D g2) {
		g2.setColor(Color.yellow);
		g2.draw(shapeBullet);
		g2.fill(shapeBullet);
	}

	// Mehtod used to draw comet
	private void drawComet(Graphics2D g2, Shape comet) {
		g2.setColor(Color.blue);
		g2.draw(comet);
		g2.fill(comet);
	}







	// Main function

	public static void main(String[] args) {
		SimpleGame game = new SimpleGame();
		
		new Thread(game).start();
	}

}
