package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.List;
import java.util.Random;

import resources.Sprite;
import trigUtil.TrigUtil;
import game.Game;

public class ShotEntity extends DynamicEntity {
	
	protected static final int SHOT_PERSISTENCE = 500;
	protected static final double SHOT_SPREAD = Math.PI * 0.03;
	
	Sprite sprite;
	long birthdate;
	double nearestDistance = Double.MAX_VALUE;
	double[] impactPoint =  new double[]{Double.MAX_VALUE,Double.MAX_VALUE};
	double coefficient = -1;
	double shift = -1;
	Random random;
	
	public ShotEntity(Sprite sprite, Game game, int x, int y, double rotation) {
		super(sprite,game, x, y, rotation);
		this.sprite = sprite;
		random = new Random();
		setRotation(rotation);
		jiggleGunfire();
		birthdate = System.currentTimeMillis();
	}
	
	protected void jiggleGunfire() {
		double rotation = getRotation();
		// Every time we draw this guy he gets a new position by jiggling the rotation
		if (random.nextBoolean()) {
			rotation += random.nextDouble()*SHOT_SPREAD;
		} else {
			rotation -= random.nextDouble()*SHOT_SPREAD;
		}
		setRotation(rotation);
		// We take the negative of the coefficient because the coord system of the screen is top down
		coefficient = -(Math.cos(rotation) / Math.sin(rotation));
		shift = -(coefficient * getX()) + getY();
	}

	public boolean collidesWith(Entity other) {
		boolean doesIntersect = false;
		List<double[]> intersections = other.findIntersections(getRotation(),getX(),getY());
		
		for (double[] intersection : intersections) {
			double intersectionDistance = TrigUtil.getDistance(intersection[0],intersection[1],getX(),getY());
			
			if (nearestDistance > intersectionDistance) {
				nearestDistance = intersectionDistance;
				impactPoint = intersection;
			}
			doesIntersect = true;
		}
		
		return doesIntersect;
	}

	@Override
	public void draw(Graphics2D g) {
		Image image = sprite.getImage();
		g.setColor(Color.magenta);
		g.drawLine((int)getX(), (int)getY(), (int)impactPoint[0], (int)impactPoint[1]);
		g.drawImage(image, (int)impactPoint[0], (int)impactPoint[1], null);
		if (System.currentTimeMillis() > (SHOT_PERSISTENCE + birthdate)) {
			game.removeEntity(this);
		}
		jiggleGunfire();
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public int getWidth() {
		return 0;
	}
}