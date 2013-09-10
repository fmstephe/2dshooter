package entity.ai.navigation;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import trigUtil.TrigUtil;
import entity.Displayable;
import entity.Entity;
import entity.PlayerEntity;
import game.Game;

/**
 * This entity highlights the viewable section of the map in front of the <code>PlayerEntity</code>.
 * 
 * TODO this really looks like an entity now.  But maybe it's not.
 * 
 * @author Francis Stephens
 */
public class Flashlight implements Displayable {
	// The angle, either side from straight ahead, that the player entity can see
	private static final double rightHorizon = (Math.PI / 2) * 0.8;
	private static final double leftHorizon = (Math.PI * 2) - rightHorizon;
	private static final double flashlightLength = TrigUtil.getDistance(0,0,Game.SCREEN_WIDTH,Game.SCREEN_HEIGHT);
	
	PlayerEntity player;
	Game game;
	List<Obstruction> obstructions;
	double x,y,rotation;
	double[] leftPeriphery, rightPeriphery;
	double leftRotation, rightRotation;
	
	public Flashlight(PlayerEntity player, Game game) {
		this.game = game;
		this.player = player;
		obstructions = new ArrayList<Obstruction>();
		leftPeriphery = rightPeriphery = null;
	}
	
	public void draw (Graphics2D g) {
		List<double[]> viewablePoints = findViewablePoints(g);
		g.setColor(Color.blue);
		g.drawLine((int)x,(int)y,(int)leftPeriphery[0],(int)leftPeriphery[1]);
		g.drawLine((int)x,(int)y,(int)rightPeriphery[0],(int)rightPeriphery[1]);
		for (double[] point : viewablePoints) {
			g.drawString("/",(int)point[0],(int)point[1]);
		}
		obstructions.clear();
		leftPeriphery = rightPeriphery = null;
	}

	/**
	 * Traversing the list of obstructions we find every point (corner of an entity) which is visible to 
	 * the flashlight.
	 * @param g A graphics context on which visible points can be drawn for debugging purposes
	 */
	private List<double[]> findViewablePoints(Graphics2D g) {
		List<double[]> viewablePoints = VisionUtil.findViewablePoints(x, y, obstructions,0);
		filterForFieldOfView(viewablePoints,g);
		return viewablePoints;
	}

	private void filterForFieldOfView(List<double[]> viewablePoints, Graphics2D g) {
		List<double[]> newPoints = new ArrayList<double[]>();
		
		for (Iterator<double[]> itr = viewablePoints.iterator();itr.hasNext();) {
			double[] point = itr.next();
			
			if (!isWithinFieldOfView(point)) {
				itr.remove();
				continue;
			}
			if (g != null) {
				g.setColor(Color.orange);
				g.drawLine((int)x,(int)y, (int)point[0], (int)point[1]);
			}
		}
		viewablePoints.addAll(newPoints);
	}
	
	private boolean isWithinFieldOfView(double[] point) {
		double r = TrigUtil.getLineRotation(player.getX()+player.getWidth()/2, player.getY()+player.getHeight()/2, point[0], point[1]);
		if (leftRotation < rightRotation) {
			return TrigUtil.isBetween(r, leftRotation, rightRotation);
		}
		else {
			return r > leftRotation || r < rightRotation;
		}
	}

	/**
	 * Provides an entity so that it can be tested to see if it obstructs the path of the flashlight.
	 * 
	 * @param entity The entity which may be obstructing this flashlight
	 */
	public void checkEnvironment(Entity entity) {
		if (cornersWithinFlashlight(entity) || entityWithinFlashlight(entity)) {
			obstructions.add(new Obstruction(entity,this.x,this.y));
		}
	}

	/**
	 * Indicates whether the entity provided lies between the two periphery
	 * lines.
	 * 
	 * @param entity The entity to test for visibility
	 * @return Whether entity lies within the flashlight
	 */
	private boolean entityWithinFlashlight(Entity entity) {
		setFieldOfView();
		double[] point = entity.nearestSurfacePoint(x,y);
		List<double[]> pointList = new ArrayList<double[]>(1);
		pointList.add(point);
		filterForFieldOfView(pointList,null);
		return pointList.size() > 0;
	}
	
	/**
	 * Indicates whether the entity provided has at least one corner point lying between the two periphery
	 * lines.
	 * 
	 * @param entity The entity to test for visibility
	 * @return Whether entity lies within the flashlight
	 */
	private boolean cornersWithinFlashlight(Entity entity) {
		setFieldOfView();
		List<double[]> points = entity.getCorners();
		filterForFieldOfView(points,null);
		return points.size() > 0;
	}

	private void setFieldOfView() {
		if (leftPeriphery == null || rightPeriphery == null) {
			double centreX = player.getX() + player.getWidth()/2;
			double centreY = player.getY() + player.getHeight()/2;
			rotation = player.getRotation();
			double[] frontPoint = TrigUtil.getExtensionPoint(rotation, centreX, centreY, player.getHeight()/2+PlayerEntity.VISION_EXTENSION);
			x = frontPoint[0];
			y = frontPoint[1];
			leftRotation = TrigUtil.normaliseRadians(rotation + leftHorizon);
			rightRotation = TrigUtil.normaliseRadians(rotation + rightHorizon);
			leftPeriphery = TrigUtil.getExtensionPoint(leftRotation,x,y,flashlightLength);
			rightPeriphery = TrigUtil.getExtensionPoint(rightRotation,x,y,flashlightLength);
		}
	}
}