package simpleGame;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Random;


public class MyShapes extends Polygon {


	// Class represent shape of gamer space ship
	static class SpaceShip extends Polygon {
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

	// Class represent bullet shape (it's a dot)

	static class Bullet extends Polygon {

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
	// Class represent comet shape
	static class Comet extends Polygon {

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
	// Method used to rotate shapes. Created mainly for comets.
	public static Shape rotateShape(Shape shape) {
		AffineTransform at = new AffineTransform();

		at.rotate(Math.PI / 1000, shape.getBounds().getCenterX(), shape
				.getBounds().getCenterY());
		shape = at.createTransformedShape(shape);

		return shape;

	}
	// Method used for moving shapes //
	public static Shape moveShape( int shipHeadDegrees, Shape shape, int dist) {
		AffineTransform at;
		if (shipHeadDegrees == 0) {
			at = new AffineTransform();

			at.translate(0, -dist);
			shape = at.createTransformedShape(shape);
		}
		else if (shipHeadDegrees > 0 & shipHeadDegrees < 90) {
			at = new AffineTransform();
			int tempDistY = dist - (shipHeadDegrees / 10);
			int tempDistX = shipHeadDegrees / 10;
			at.translate(tempDistX, -tempDistY);
			shape = at.createTransformedShape(shape);
		}
		else if (shipHeadDegrees == 90) {
			at = new AffineTransform();

			at.translate(dist, 0);
			shape = at.createTransformedShape(shape);
		}
		else if (shipHeadDegrees > 90 & shipHeadDegrees < 180) {
			at = new AffineTransform();
			int tempDist = ((shipHeadDegrees - 90) / 10);
			int tempDistX = dist - ((shipHeadDegrees - 90) / 10);
			at.translate(tempDistX, tempDist);
			shape = at.createTransformedShape(shape);
		}
		else if (shipHeadDegrees == 180) {
			at = new AffineTransform();

			at.translate(0, dist);
			shape = at.createTransformedShape(shape);
		}
		else if (shipHeadDegrees > 180 & shipHeadDegrees < 270) {
			at = new AffineTransform();
			int tempDist = dist - ((shipHeadDegrees - 180) / 10);
			int tempDistX = ((shipHeadDegrees - 180) / 10);
			at.translate(-tempDistX, tempDist);
			shape = at.createTransformedShape(shape);
		}
		else if (shipHeadDegrees == 270) {
			at = new AffineTransform();

			at.translate(-dist, 0);
			shape = at.createTransformedShape(shape);
		}
		else if (shipHeadDegrees > 270 & shipHeadDegrees < 360) {
			at = new AffineTransform();
			int tempDist = ((shipHeadDegrees - 270) / 10);
			int tempDistX = dist - ((shipHeadDegrees - 270) / 10);
			at.translate(-tempDistX, -tempDist);
			shape = at.createTransformedShape(shape);
		}
		else if (shipHeadDegrees == 360) {
			at = new AffineTransform();

			at.translate(0, -dist);
			shape = at.createTransformedShape(shape);
		}else {
			at = new AffineTransform();

			at.translate(0, -dist);
			shape = at.createTransformedShape(shape);
		}
		return shape;
	}

	// Method using to move shapes to preconfigured directions 	---------------//
	// Now its  used only for Comets -TO DO Moving all shapes using one method //
	public static Shape moveShape(Shape shape, int direction, int dist) {
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
	// Method that creates Array List of comets using random position //
		public static ArrayList<Shape> createRandCometList(ArrayList<Shape> finalShapeList,
				int howManyComets,Shape shipCurrentPosition) {

			int randTab[] = new int[howManyComets];
			// Random generator for comets
			Random generator = new Random();

			for (int i = 0; i < howManyComets; i++) {
				generator.setSeed(System.nanoTime());
				// chose one from 5 different comets shown below //
				int temp = generator.nextInt(5); 
				randTab[i] = temp;
				

			}
			for (int tempInt = 0; tempInt < randTab.length; tempInt++) {
				generator.setSeed(System.nanoTime());
				int x;
				if(tempInt%2 == 0){
					 x = 5;
				}else x = SimpleGame.WIDTH - 10;
				//int x = generator.nextInt(SimpleGame.WIDTH - 40);
				int y = generator.nextInt(SimpleGame.HEIGHT - 40);
				while( (x < shipCurrentPosition.getBounds2D().getMaxX() + 10 & x > shipCurrentPosition.getBounds2D().getMinX() - 10) |
					(y < shipCurrentPosition.getBounds2D().getMaxY() + 10 & y > shipCurrentPosition.getBounds2D().getMinY() -10)){
						//x = generator.nextInt(SimpleGame.WIDTH - 40);
						y = generator.nextInt(SimpleGame.HEIGHT - 40);
					}
				
				
				switch (randTab[tempInt]) {
				case 0:
					finalShapeList.add(new MyShapes.Comet(x, y, x + 20, y - 5, x + 40,
							y + 5, x + 30, y + 30, x + 10, y + 20, x + 10, y + 10));
					break;
				case 1:
					finalShapeList.add(new MyShapes.Comet(x, y, x + 20, y - 5, x + 40,
							y + 5, x + 30, y + 30, x + 10, y + 20, x + 10, y + 10));
					break;
				case 2:
					finalShapeList
							.add(new MyShapes.Comet(x, y, x + 19, y - 21, x - 1, y - 37,
									x - 22, y - 34, x - 26, y - 31, x - 12, y - 22));
					break;

				case 3:
					finalShapeList.add(new MyShapes.Comet(x, y, x + 20, y - 5, x + 40,
							y + 5, x + 30, y + 30, x + 10, y + 20, x + 10, y + 10));
					break;
				case 4:
					finalShapeList
							.add(new MyShapes.Comet(x, y, x + 20, y - 15, x + 45, y + 10,
									x + 30, y + 35, x + 10, y + 25, x + 10, y + 15));
					break;
				case 5:
					finalShapeList.add(new MyShapes.Comet(x, y, x + 20, y - 5, x + 40,
							y + 5, x + 30, y + 20, x + 10, y + 20, x + 10, y + 10));
					break;
				}
			}

			return finalShapeList;
		}
		// Method used for checking when shape hit another shape
		public static boolean testIntersection(Shape shapeA, Shape shapeB) {
			Area areaA = new Area(shapeA);
			areaA.intersect(new Area(shapeB));
			return !areaA.isEmpty();
		}

		// This method checking if any shape reach end of window and needs to be
		// draw on another side of window
		public static Shape isWindowsEnds(Point location, Shape shapeToTeleport) {
			AffineTransform at;
			if (location.getX() > SimpleGame.WIDTH - 1) {
				at = new AffineTransform();
				at.translate(-(SimpleGame.WIDTH - 1), 0);
				shapeToTeleport = at.createTransformedShape(shapeToTeleport);
				return shapeToTeleport;
			} else if (location.getX() < 1) {
				at = new AffineTransform();
				at.translate(SimpleGame.WIDTH - 1, 0);
				shapeToTeleport = at.createTransformedShape(shapeToTeleport);
				return shapeToTeleport;
			} else if (location.getY() > SimpleGame.HEIGHT - 1) {
				at = new AffineTransform();
				at.translate(0, -(SimpleGame.HEIGHT - 1));
				shapeToTeleport = at.createTransformedShape(shapeToTeleport);
				return shapeToTeleport;
			} else if (location.getY() < 1) {
				at = new AffineTransform();
				at.translate(0, SimpleGame.HEIGHT - 1);
				shapeToTeleport = at.createTransformedShape(shapeToTeleport);
				return shapeToTeleport;
			} else
				return shapeToTeleport;

		}


		// Method used to generate random direction for comets.
		public static ArrayList<Integer> getRandDirections(ArrayList<Integer> intList,
				int howMany) {
			Random gen = new Random();
			gen.setSeed(System.currentTimeMillis());
			// ArrayList intList = new <Integer>ArrayList();
			for (int i = 0; i < howMany; i++) {
				intList.add(gen.nextInt(12));

			}
			return intList;
		}
}
