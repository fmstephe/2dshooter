package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import resources.Sprite;
import resources.SpriteStore;
import trigUtil.TrigUtil;
import entity.ai.navigation.Flashlight;
import entity.ai.navigation.Navigator;
import game.Game;

public class PlayerEntity extends DynamicEntity {

	public static final double FORWARD_SPEED = -130;
	public static final double BACKWARD_SPEED = 70;
	public static final double SIDEWAYS_SPEED = 30;
	public static final double EVASIVE_ROTATION_SPEED = Math.PI/2;
	public static final double ROTATION_SPEED = Math.PI/2;
	public static final double AUTO_ROTATION_SPEED = Math.PI*4;
	public static final double VISION_EXTENSION = 7;
	
	private static final int FIRING_INTERVAL = 400;
	
	// Triggers for indicating directed movement of the player
	private boolean left, right, up, down, fire;
	private boolean keyPressed;
	private long lastFire;
	private KeyAdapter controlListener;
	private MouseAdapter mouseListener;
	private Navigator navigator;
	Flashlight flashlight;
	private double leftFeeler, rightFeeler;
	private double[] leftFLine, rightFLine;
	
	public PlayerEntity(Sprite sprite, Game game, int x, int y, double rotation) {
		super(sprite, game, x, y, rotation);
		left = right = up = down = fire = false;
	}
	
	/**
	 * Please initialise after construction :)
	 * This method is required to build those objects which require
	 * a reference to this object and thus need to be constructed
	 * after this object's constructor has fully completed.
	 */
	public void init() {
		controlListener = new KeyInputHandler();
		mouseListener = new MouseInputHandler();
		this.flashlight = new Flashlight(this,game);
	}
	
	@Override
	public void reset() {
		super.reset();
	}
	
	/**
	 * Set the destination for this PlayerEntity.
	 * 
	 * @param destX The x coordinate for the destination
	 * @param destY The y coordinate for the destination
	 */
	public void setDestination(int destX, int destY) {
		navigator = new Navigator(game.getEnvironment(),getX(),getY(),destX,destY);
	}
	
	@Override
	public void draw(Graphics2D g) {
		super.draw(g);
		if (navigator != null) {
			g.setColor(Color.red);
			g.drawString("X", (int)navigator.getUltimateDestination()[0], (int)navigator.getUltimateDestination()[1]);
			if (navigator.hasMoreSteps()) {
				g.drawLine((int)getX(),(int)getY(),(int)navigator.currentStep()[0], (int)navigator.currentStep()[1]);
			}
		}
		if (leftFLine != null && rightFLine != null) {
			g.setColor(Color.pink);
			g.drawLine((int)leftFLine[0],(int)leftFLine[1],(int)leftFLine[2],(int)leftFLine[3]);
			g.drawLine((int)rightFLine[0],(int)rightFLine[1],(int)rightFLine[2],(int)rightFLine[3]);
		}
		//flashlight.draw(g);
	}
	
	@Override
	public void move(long delta) {
		double[] currentStep = null;
		
		if (!keyPressed) {
			if (navigator != null) {
				if (navigator.hasMoreSteps()) {
					currentStep = navigator.currentStep();
					if (TrigUtil.getDistance(currentStep[0], currentStep[1], getX(), getY()) < (-FORWARD_SPEED*delta)/1000) {
						navigator.discardCurrentStep();
						setX(currentStep[0]);
						setY(currentStep[1]);
						return; // messy way to avoid the super.move call
					}
					// Do some steering
					double stepDistance = TrigUtil.getDistance(getX(),getY(),currentStep[0],currentStep[1]);
					
					if (Math.min(leftFeeler,rightFeeler) < stepDistance) { // There is an obstruction before the player
						if (leftFeeler < rightFeeler) {
							rotationalVelocity = EVASIVE_ROTATION_SPEED;
						}
						else {
							rotationalVelocity = -EVASIVE_ROTATION_SPEED;
						}
						super.move(delta);
					}
					else {
						double desiredRotation = TrigUtil.getLineRotation(getX(),getY(), currentStep[0], currentStep[1]);
						if (desiredRotation == getRotation()) {
							velocity = FORWARD_SPEED; // we are going somewhere
							super.move(delta);
						}
						else {
							int direction = TrigUtil.rotationDirection(getRotation(), desiredRotation);
							rotationalVelocity = direction*AUTO_ROTATION_SPEED;
							// Calculate the next move
							super.move(delta);
							// If the rotation direction changes after the move then we just set the desired rotation directly
							if (TrigUtil.rotationDirection(getRotation(), desiredRotation) != direction) {
								setRotation(desiredRotation);
							}
						}
					}
				}
				else {
					navigator = null; // There are no more steps to take so discard the navigator
				}
			}
		}
		else { // using the controls aborts the navigator
			navigator = null;
			
			if (left && !right) {
				rotationalVelocity = -ROTATION_SPEED;
			}
			if (right && !left) {
				rotationalVelocity = ROTATION_SPEED;
			}
			if (up && !down) {
				velocity = FORWARD_SPEED;
			}
			if (down && !up) {
				velocity = BACKWARD_SPEED;
			}
			super.move(delta);
		}
		leftFeeler = Double.MAX_VALUE;
		rightFeeler = Double.MAX_VALUE;
		leftFLine = null;
		rightFLine = null;
	}

	@Override
	public void act(long delta) {
		if (fire) {
			tryToFire();
		}
	}
	
	private void tryToFire() {
		// check that we have waiting long enough to fire
		if (System.currentTimeMillis() - lastFire >= FIRING_INTERVAL) {
			// if we waited long enough, create the shot entity, and record the time.
			lastFire = System.currentTimeMillis();
			double[] firingPoint = TrigUtil.getExtensionPoint(getRotation(), getX()+getWidth()/2, getY()+getHeight()/2, getHeight()/2 + VISION_EXTENSION);
			ShotEntity shot = new ShotEntity(SpriteStore.getSprite(SpriteStore.shotImage),game,(int)firingPoint[0],(int)firingPoint[1],getRotation());
			game.addEntity(shot);
		}
	}
	
	@Override
	public boolean collidesWith(Entity other) {
		flashlight.checkEnvironment(other);
//		manageFeelers(other);
		
		// Cheap and cheerful sliding collision with vertical and horizontal walls
		if (super.collidesWith(other)) {
			// If we a re going backwards we just sneakily spin the rotation around for our calculations
			double effectiveRotation = down && !up ? TrigUtil.normaliseRadians(getRotation()+Math.PI) : getRotation();
			// The nearest intersecting line and the intersection itself, 0-3 make the line, 4,5 make the intersection
			double[] lineIntersection = other.nearestIntersectingLine(effectiveRotation, getOldX()+(getWidth()/2), getOldY()+(getHeight()/2));
			if (lineIntersection != null) {
				double xDiff = Math.abs(getOldX() - getX());
				double yDiff = Math.abs(getOldY() - getY());
				if (lineIntersection[0] == lineIntersection[2]) { // This is a vertical wall
					revertX();
//					if (navigator != null && xDiff > yDiff) {
//						// We are head-butting a wall directly
//						System.out.println("We are stuck, recalculating the path now...");
//						double[] dest = navigator.getUltimateDestination();
//						navigator = new Navigator(game.getEnvironment(),getX(),getY(),dest[0],dest[1]);
//					}
				}
				if (lineIntersection[1] == lineIntersection[3]) { // This is a horizontal wall
					revertY();
//					if (navigator != null && yDiff > xDiff) {
//						System.out.println("We are stuck, recalculating the path now...");
//						// We are head-butting a wall directly
//						double[] dest = navigator.getUltimateDestination();
//						navigator = new Navigator(game.getEnvironment(),getX(),getY(),dest[0],dest[1]);
//					}
				}
			}
		}
		// Below is a poor stab at sliding collision with angled walls
		/*if (super.collidesWith(other)) {
			// The nearest intersecting line and the intersection itself, 0-3 make the line, 4,5 make the intersection
			double[] lineIntersection = other.nearestIntersectingLine(getRotation(), getOldX()+(getWidth()/2), getOldY()+(getHeight()/2));
			// Two points on the line that we might be heading for, one will be well wrong
			double[] putativePoint1 = new double[]{TrigUtil.solveForX(lineIntersection[0], lineIntersection[1], lineIntersection[2], lineIntersection[3], getY()),getY()};
			double[] putativePoint2 = new double[] {getX(),TrigUtil.solveForY(lineIntersection[0], lineIntersection[0], lineIntersection[0], lineIntersection[0], getX())};
			// The rotation required for the player to be pointing at each putative point
			double rotation1 = TrigUtil.getLineRotation(getOldX(),getOldY(),putativePoint1[0], putativePoint1[1]);
			double rotation2 = TrigUtil.getLineRotation(getOldX(),getOldY(),putativePoint2[0], putativePoint2[1]);
			double point[] = null;
			// The point whose rotation is the nearest to the current rotation will be chosen
			if (TrigUtil.rotationDistance(getRotation(),rotation1) < TrigUtil.rotationDistance(getRotation(),rotation2)) {
				point = putativePoint1;
			}
			else {
				point = putativePoint2;
			}
			// The distance from the colliding surface that the player must be
			double bufferingDistance = Math.max(getWidth(),getHeight());
			double lineRotation = TrigUtil.getLineRotation(lineIntersection[0],lineIntersection[1],lineIntersection[2],lineIntersection[3]);
			// Choose the direction, perpendicular to the colliding surface, that the player must be pushed to
			// The rotation will always be the one furthest from our current rotation, i.e. against our direction of travel rather than with it
			double clockwise = Entity.normaliseRadians(lineRotation+Math.PI/2);
			double anticlockwise = Entity.normaliseRadians(lineRotation-Math.PI/2);
			double pushingRotation = -1;
			if (TrigUtil.rotationDistance(getRotation(), clockwise) > TrigUtil.rotationDistance(getRotation(), anticlockwise)) {
				pushingRotation = clockwise;
			}
			else {
				pushingRotation = anticlockwise;
			}
			double[] finalPoint = TrigUtil.getExtensionPoint(pushingRotation, point[0], point[1], bufferingDistance);
			revertX();
			revertY();
			setX(finalPoint[0]);
			setY(finalPoint[1]);
		}*/
		
		return false;
	}
	
	private void manageFeelers(Entity other) {
		double length = TrigUtil.getDistance(getX()+getWidth()/2,getY()+getHeight()/2, getX(),getY()+getHeight());
		double leftRotation = TrigUtil.getLineRotation(getX()+getWidth()/2,getY()+getHeight()/2, getX(),getY()+getHeight());
		leftRotation = TrigUtil.normaliseRadians(leftRotation + getRotation());
		double[] leftPoint = TrigUtil.getExtensionPoint(leftRotation,getX()+getWidth()/2,getY()+getHeight()/2,length);
		double[] leftLineIntersection = other.nearestIntersectingLine(getRotation(), leftPoint[0], leftPoint[1]);
		if (leftLineIntersection != null ) {
			double leftDistance = TrigUtil.getDistance(leftPoint[0],leftPoint[1],leftLineIntersection[4],leftLineIntersection[5]);
			if (leftFeeler > leftDistance) {
				leftFeeler = leftDistance;
				leftFLine = new double[] {leftPoint[0],leftPoint[1],leftLineIntersection[4],leftLineIntersection[5]};
			}
		}
		
		double rightRotation = TrigUtil.getLineRotation(getX()+getWidth()/2,getY()+getHeight()/2, getX()+getWidth(),getY()+getHeight());
		rightRotation = TrigUtil.normaliseRadians(rightRotation + getRotation());
		double[] rightPoint = TrigUtil.getExtensionPoint(rightRotation,getX()+getWidth()/2,getY()+getHeight()/2,length);
		double[] rightLineIntersection = other.nearestIntersectingLine(getRotation(),rightPoint[0],rightPoint[1]);
		if (rightLineIntersection != null) {
			double rightDistance = TrigUtil.getDistance(rightPoint[0],rightPoint[1],rightLineIntersection[4],rightLineIntersection[5]);
			if (rightFeeler > rightDistance) {
				rightFeeler = rightDistance;
				rightFLine = new double[] {rightPoint[0],rightPoint[1],rightLineIntersection[4],rightLineIntersection[5]};
			}
		}
	}

	public KeyAdapter getControlListener() {
		return controlListener;
	}
	
	public MouseListener getMouseListener() {
		return mouseListener;
	}
	
	private class KeyInputHandler extends KeyAdapter {
		
		public KeyInputHandler() {
			System.out.println("KeyInputHandler");
		}
		
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				keyPressed = true;
				left = true;
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				keyPressed = true;
				right = true;
			}
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				keyPressed = true;
				fire = true;
			}
			if (e.getKeyCode() == KeyEvent.VK_UP) {
				keyPressed = true;
				up = true;
			}
			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				keyPressed = true;
				down = true;
			}
		}
		
		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				left = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				right = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				fire = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_UP) {
				up = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				down = false;
			}
			if (!left && !right && !fire && ! up && !down) {
				keyPressed = false;
			}
		}
		
		@Override
		public void keyTyped(KeyEvent e) {
			// if we hit escape, then quit the game
			if (e.getKeyChar() == 27) {
				System.exit(0);
			}
		}
	}
	
	private class MouseInputHandler extends MouseAdapter {
		// This is probably not the best way to implement double-click
		private static final long DOUBLE_CLICK_THRESHOLD = 1000;
		private long lastClick;

		public MouseInputHandler() {
			System.out.println("MouseInputHandler");
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			if (isDoubleClick()) {
				setDestination(e.getX(),e.getY());
			}
		}

		private boolean isDoubleClick() {
			long thisClick = System.currentTimeMillis();
			if (thisClick - lastClick < DOUBLE_CLICK_THRESHOLD) {
				return true;
			}
			else {
				lastClick = thisClick;
				return false;
			}
		}
	}
}