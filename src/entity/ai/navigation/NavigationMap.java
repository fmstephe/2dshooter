package entity.ai.navigation;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import entity.Displayable;
import entity.Entity;
import game.Environment;

/**
 * A Map of points, size two double array (x,y), which are directly navigable by line of site.
 * It is expected that these points will be the corner points of static objects in the game such
 * as walls etc.  In this way navigation is made a simple case of graph traversal moving from
 * edge to edge.
 * 
 * @author Francis Stephens
 */
public class NavigationMap implements Displayable {
	Map<double[],Set<double[]>> adjacencyMap;

	/**
	 * Private constructor locks up class
	 */
	private NavigationMap() {
		adjacencyMap = new HashMap<double[],Set<double[]>>();
	}
	
	/**
	 * Makes two points directly navigable by line of site.
	 * 
	 * @param point1 The first point
	 * @param point2 The second point
	 */
	public void addConnection(double[] point1, double[] point2) {
		Set<double[]> point1List = adjacencyMap.get(point1);
		Set<double[]> point2List = adjacencyMap.get(point2);
		
		if (point1List != null) {
			point1List.add(point2);
		}
		else {
			point1List = new HashSet<double[]>();
			point1List.add(point2);
			adjacencyMap.put(point1, point1List);
		}
		if (point2List != null) {
			point2List.add(point2);
		}
		else {
			point2List = new HashSet<double[]>();
			point2List.add(point1);
			adjacencyMap.put(point2, point2List);
		}
	}
	
	/**
	 * Returns a list of all the points directly navigable from the point of origin.
	 * 
	 * @param origin The point whose neighbour points we wish for
	 * @return The list of points directly navigable from origin.
	 */
	public Set<double[]> accessiblePoints(double[] origin) {
		return adjacencyMap.get(origin);
	}
	
	/**
	 * 
	 * 
	 * @param game
	 * @return
	 */
	public static NavigationMap getInstance(Environment environment) {
		NavigationMap navigationMap = new NavigationMap();
		List<? extends Entity> allWalls = environment.getWalls();
		
		for (Entity entity : allWalls) {
			List<double[]> points = entity.getCorners();
			for (double[] point : points) {
				List<double[]> viewablePoints = environment.findViewablePoints(point[0],point[1],Environment.MIN_DIST_BETWEEN_WALLS/2);
				for (double[] viewablePoint : viewablePoints) {
					navigationMap.addConnection(point, viewablePoint);
				}
			}
		}
		return navigationMap;
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.DARK_GRAY);
		for (double[] point : adjacencyMap.keySet()) {
			Set<double[]> connectedPoints = adjacencyMap.get(point);
			for (double[] connectedPoint : connectedPoints) {
				g.drawLine((int)point[0],(int)point[1],(int)connectedPoint[0],(int)connectedPoint[1]);
			}
		}
	}
}