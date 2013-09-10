package entity.ai.navigation;

import trigUtil.TrigUtil;
import entity.Entity;

/**
 * 
 * 
 * @author Francis Stephens
 */
public class Obstruction implements Comparable<Obstruction> {
	Entity entity;
	double distance;
	double intersectX, intersectY;
	
	/**
	 * Creates a new Obstruction.  Obstructions are simply Entity objects which are considered
	 * an obstruction from somebody's perspective.  This perspective is denoted by x,y coords
	 * and a distance is established by finding the point on the surface of entity which is 
	 * nearest to the origin point.
	 * 
	 * @param entity The obstructing entity
	 * @param originX The x position from which entity is being considered an obstruction
	 * @param originY The y position from which entity is being considered an obstruction
	 */
	public Obstruction (Entity entity, double originX, double originY) {
		this.entity = entity;
		double[] nearestPoint = entity.nearestSurfacePoint(originX, originY);
		intersectX = nearestPoint[0];
		intersectY = nearestPoint[1];
		distance = TrigUtil.getDistance(originX,originY,nearestPoint[0],nearestPoint[1]);
	}
	
	/**
	 * Creates a new Obstruction.  Obstructions are simply Entity objects which are considered
	 * an obstruction from somebody's perspective.  This perspective is denoted by x,y coords
	 * and a distance is established using a separate intersection x,y coords.  It is expected,
	 * thought neither checked nor required, that the intersection be a coordinate on the surface
	 * of the obstructing entity.
	 * 
	 * @param entity The obstructing entity
	 * @param x The x position from which entity is being considered an obstruction
	 * @param y The y position from which entity is being considered an obstruction
	 */
	public Obstruction (Entity entity, double intersectX, double intersectY, double originX, double originY) {
		this.entity = entity;
		this.intersectX = intersectX;
		this.intersectY = intersectY;
		distance = TrigUtil.getDistance(originX,originY,intersectX,intersectY);
	}

	@Override
	public int compareTo(Obstruction o) {
		Obstruction obstruction = (Obstruction)o;
		if (distance > obstruction.distance) {
			return 1;
		}
		else if (distance == obstruction.distance) {
			return 0;
		}
		else { // distance < obstruction.distance
			return -1;
		}
	}
	
	@Override
	public String toString() {
		return entity.toString();
	}
}