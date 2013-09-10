package entity;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;

import resources.Sprite;
import game.Game;

public class DynamicEntity extends Entity {
	public double velocity;
	public double rotationalVelocity; // NB: A positive value spins you clockwise a negative counter-clockwise
	protected Sprite sprite;
	private double oldY;
	private double oldX;
	private double oldRotation;
	protected Game game;

	
	public DynamicEntity(Sprite sprite,Game game,int x,int y, double rotation) {
		super(x,y, rotation);
		if (sprite == null) {
			throw new IllegalArgumentException("Sprite cannot be null");
		}
		this.game = game;
		this.sprite = sprite;
		this.velocity = 0;
	}
	
	/**
	 * This method should be called at the beginning of each iteration of the main
	 * game loop.  This clears temporary data which gets recalculated each round.
	 */
	public void reset() {
		velocity = 0;
		rotationalVelocity = 0;
	}
	
	/**
	 * An overridable action method for non-movement actions to be performed
	 * 
	 * @param delta Time since last action was taken (millis)
	 */
	public void act(long delta) {
		// do nothing
	}
	
	public void move(long delta) {
		double rV = (delta*rotationalVelocity)/1000;
		double xV = -(velocity * Math.sin(getRotation()));
		double yV = (velocity * Math.cos(getRotation()));
		setRotation(getRotation() + rV);
		setX(getX() + (delta*xV)/1000);
		setY(getY() + (delta*yV)/1000);
	}
	
	/**
	 * Undoes the last move made by this entity
	 */
	public void unmove() {
		setX(oldX);
		setY(oldY);
	}

	public void draw(Graphics2D g) {
		Image image = sprite.getImage();
		int width = sprite.getWidth();
		int height = sprite.getHeight();
		AffineTransform xform = AffineTransform.getRotateInstance(getRotation(),getX()+(width/2),getY()+(height/2));
		xform.translate(getX(), getY());
		g.drawImage(image, xform, null);
	}
	
	public void setX(double newX) {
		oldX = getX();
		super.setX(newX);
	}
	
	public void revertX() {
		double keepX = getX();
		setX(oldX);
		oldX = keepX;
	}
	
	public double getOldX() {
		return oldX;
	}
	
	public void setY(double newY) {
		oldY = getY();
		super.setY(newY);
	}
	
	public void revertY() {
		double keepY = getY();
		setY(oldY);
		oldY = keepY;
	}
	
	public double getOldY() {
		return oldY;
	}
	
	public void setRotation(double newRotation) {
		oldRotation = getRotation();
		super.setRotation(newRotation);
	}
	
	public void revertRotation() {
		double keepRotation = getRotation();
		setRotation(oldRotation);
		oldRotation = keepRotation;
	}

	@Override
	public int getHeight() {
		return sprite.getHeight();
	}

	@Override
	public int getWidth() {
		return sprite.getWidth();
	}
}