import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_SPACE;
import static java.awt.event.KeyEvent.VK_UP;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;

	public class KeyControl extends KeyAdapter {
		
		SimpleGame myGame;

		boolean movingLeft = false;
		boolean movingRight = false;
		boolean movingUp = false;
		
		public KeyControl(SimpleGame game){
			this.myGame = game;
		}

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
				myGame.shootBullet();
				System.out.println("WOWWW");
				myGame.shooting = true;
				myGame.laser.play();
				myGame.shootingTime = 0;

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

		void movingShip() {
			if (movingLeft) {

				changeHeadShapePos(false);
				myGame.shapeShip = AffineTransform.getRotateInstance(Math.toRadians(-3),
						myGame.shapeShip.getBounds2D().getCenterX(),
						myGame.shapeShip.getBounds2D().getCenterY())
						.createTransformedShape(myGame.shapeShip);

			}
			if (movingRight) {
				changeHeadShapePos(true);

				myGame.shapeShip = AffineTransform.getRotateInstance(Math.toRadians(3),
						myGame.shapeShip.getBounds2D().getCenterX(),
						myGame.shapeShip.getBounds2D().getCenterY())
						.createTransformedShape(myGame.shapeShip);

			}
			if (movingUp) {
				System.out.println("UP!: ");
				myGame.shapeShip = MyShapes.moveShape(myGame.shipHeadDegrees,myGame.shapeShip, 9);
				myGame.shapeShip = MyShapes.isWindowsEnds(myGame.shapeShip.getBounds().getLocation(),
						myGame.shapeShip);

			}
		}

		// This method is used to measure angle of the ship
		void changeHeadShapePos(boolean rightArrow) {
			if (rightArrow == true) {
				if (myGame.shipHeadDegrees < 357) {
					myGame.shipHeadDegrees += 3;
				} else {
					myGame.shipHeadDegrees = 0;
				}
			} else {
				if (myGame.shipHeadDegrees > 2) {
					myGame.shipHeadDegrees -= 3;
				} else {
					myGame.shipHeadDegrees = 357;
				}
			}
		}

	}