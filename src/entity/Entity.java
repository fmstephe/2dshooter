package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import trigUtil.TrigUtil;

public abstract class Entity implements Displayable {
	
	private double x;
	private double y;
	private double rotation;

	/**
	 * Returns a list of lines, represented by a size four array of double {x1,y1,x2,y2}, for this Entity.
	 * 
	 * TODO This should probably account for rotation but currently does not!
	 * 
	 * @return A list of lines for this Entity
	 */
	public List<double[]> getLines() {
		double leftX = getX();
		double rightX = getX() + getWidth();
		double topY = getY();
		double bottomY = getY() + getHeight();
		double[] leftSide = new double[] {leftX,topY,leftX,bottomY};
		double[] rightSide = new double[] {rightX,topY,rightX,bottomY};
		double[] topSide = new double[] {leftX,topY,rightX,topY};
		double[] bottomSide = new double[] {leftX,bottomY,rightX,bottomY};
		List<double[]> lines = new ArrayList<double[]>(4);
		lines.add(leftSide);
		lines.add(rightSide);
		lines.add(topSide);
		lines.add(bottomSide);
		return lines;
	}
	
	/**
	 * Finds all intersections between the lines of the outline of this Entity and the line described by the parameters.
	 * NB: A line which terminates at the same location as one of this Entity's terminating lines is not considered to intersect.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public List<double[]> findIntersectionsIgnoringMutualTerminals(double x1, double y1, double x2, double y2){
		List<double[]> lines = getLines();
		List<double[]> intersections = new ArrayList<double[]>();
		for (double[] line : lines) {
			if (mutualTerminal(line,new double[] {x1,y1,x2,y2})) {
				continue;
			}
			double[] intersection = TrigUtil.findLineSegmentIntersection(x1,y1,x2,y2,line[0],line[1],line[2],line[3]);
			if (intersection != null) {
				intersections.add(intersection);
			}
		}
		return intersections;
	}
	
	/**
	 * Indicates whether or not the two lines have a common terminating point.
	 * 
	 * @param line1 The first line
	 * @param line2 The second line
	 * @return True if the two lines have a terminating point in common, false otherwise
	 */
	private boolean mutualTerminal(double[] line1, double[] line2) {
		if (line1[0] == line2[0] && line1[1] == line2[1]) {
			return true;
		}
		if (line1[2] == line2[2] && line1[3] == line2[3]) {
			return true;
		}
		if (line1[0] == line2[2] && line1[1] == line2[3]) {
			return true;
		}
		if (line1[2] == line2[0] && line1[3] == line2[1]) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param myRotation
	 * @param x
	 * @param y
	 * @return
	 */
	public List<double[]> findIntersections(double myRotation, double x, double y){
		List<double[]> lines = getLines();
		List<double[]> intersections = new ArrayList<double[]>();
		for (double[] line : lines) {
			double[] intersection = TrigUtil.findRayIntersectionByRotation(myRotation,x,y,line[0],line[1],line[2],line[3]);
			if (intersection != null) {
				intersections.add(intersection);
			}
		}
		return intersections;
	}
	
	/**
	 * 
	 * @param lineRotation
	 * @param lineX
	 * @param lineY
	 * @return
	 */
	public double[] nearestIntersectingLine(double lineRotation, double lineX, double lineY) {
		List<double[]> lines = getLines();
		double[] lineIntersection = null;
		double nearestDistance = Double.MAX_VALUE;
		
		for (double[] line : lines) {
			double[] intersection = TrigUtil.findRayIntersectionByRotation(lineRotation,lineX,lineY,line[0],line[1],line[2],line[3]);
			if (intersection != null) {
				double distance = TrigUtil.getDistance(lineX, lineY, intersection[0], intersection[1]);
				if (distance < nearestDistance) {
					nearestDistance = distance;
					lineIntersection = new double[] {line[0],line[1],line[2],line[3],intersection[0],intersection[1]};
				}
			}
		}
		
		return lineIntersection;
	}
	
	/**
	 * Finds the nearest point along the surface of this entity to the point (x,y)
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public double[] nearestSurfacePoint(double x, double y) {
		List<double[]> lines = getLines();
		double[] nearestPoint = null;
		double nearestDistance = Double.MAX_VALUE;
		
		for (double[] line : lines) {
			double[] intersection = TrigUtil.findNearestPointAlongSegment(x, y, line[0], line[1], line[2], line[3]);
			if (intersection != null) {
				double distance = TrigUtil.getDistance(x, y, intersection[0], intersection[1]);
				if (distance < nearestDistance) {
					nearestDistance = distance;
					nearestPoint = intersection;
				}
			}
		}
		return nearestPoint;
	}
	
	/**
	 * Returns the four corner points for this Entity in no particular order.
	 * 
	 * @return The four corner points for this Entity.
	 */
	public List<double[]> getCorners() {
		double[] topLeft = new double[] {x,y};
		double[] topRight = new double[] {x+this.getWidth(),y};
		double[] bottomLeft = new double[] {x,y+this.getHeight()};
		double[] bottomRight = new double[] {x+this.getWidth(),y+this.getHeight()};
		List<double[]> points = new ArrayList<double[]>(4);
		points.add(topLeft);
		points.add(topRight);
		points.add(bottomLeft);
		points.add(bottomRight);
		return points;
	}
	
	/**
	 * Indicates whether or not one of the 
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isCorner(double x, double y) {
		List<double[]> corners = getCorners();
		for (double[] corner : corners) {
			if (corner[0] == x && corner[1] == y) {
				return true;
			}
		}
		return false;
	}

	public Entity(int x, int y, double rotation) {
		this.x = x;
		this.y = y;
		this.rotation = rotation;
	}
	
	public abstract void draw(Graphics2D g);

	/**
	 * Indicates whether this entity collides with the entity provided.
	 * Now with new rotation awareness.
	 * 
	 * @param other
	 * @return
	 */
	public boolean collidesWith(Entity other) {
		AffineTransform xformMe = AffineTransform.getRotateInstance(rotation,x+(getWidth()/2),y+(getHeight()/2));
		Shape me = xformMe.createTransformedShape(new Rectangle((int) x,(int) y,this.getWidth(),this.getHeight()));
		AffineTransform xformOther = AffineTransform.getRotateInstance(other.rotation,other.x+(other.getWidth()/2),other.y+(other.getHeight()/2));
		Shape him = xformOther.createTransformedShape(new Rectangle((int) other.x,(int) other.y,other.getWidth(),other.getHeight()));
		
		if (shapeContainedBy(me,him) || shapeContainedBy(him,me) || shapesIntersect(me,him)) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Indicates whether the isContained has any point contained by container.
	 * 
	 * @param isContained
	 * @param container
	 * @return
	 */
	private boolean shapeContainedBy(Shape isContained, Shape container) {
		double[] point = new double[6];
		for (PathIterator itr = isContained.getPathIterator(new AffineTransform()); !itr.isDone(); itr.next()) {
			itr.currentSegment(point);
			if (container.contains(point[0],point[1])) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Indicates whether any of the line segments making up intersector intersects with
	 * isIntersected.
	 * 
	 * @param isIntersected
	 * @param intersector
	 * @return
	 */
	private boolean shapesIntersect(Shape shape1, Shape shape2) {
		
		List<double[]> lines1 = getShapeLines(shape1);
		List<double[]> lines2 = getShapeLines(shape2);
		
		for (double[] line1 : lines1) {
			for (double[] line2 : lines2) {
				if (TrigUtil.findLineSegmentIntersection(line1[0], line1[1], line1[2], line1[3], line2[0], line2[1], line2[2], line2[3]) != null) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private List<double[]> getShapeLines(Shape shape) {
		
		List<double[]> points = new ArrayList<double[]>(4);
		double[] point1 = new double[6];
		double[] point2 = new double[6];
		PathIterator itr = shape.getPathIterator(new AffineTransform());
		itr.currentSegment(point1);
		itr.next();
		
		for (;!itr.isDone();itr.next()) {
			itr.currentSegment(point2);
			points.add(new double[] {point1[0], point1[1], point2[0], point2[1]});
			System.arraycopy(point2, 0, point1, 0, point2.length);
		}
		
		return points;
	}
	
	/**
	 * Pushes the points provided away from this entity by 
	 * 
	 * @param testingPoints
	 * @return
	 */
	public List<double[]> pushOutPoints(List<double[]> points, double pushingDistance) {
		double[] centre = TrigUtil.findIntersectionOfTwoLines(getX(), getY(), getX()+getWidth(), getY()+getHeight(), getX()+getWidth(), getY(), getX(), getY()+getHeight());
		List<double[]> pushedPoints = new ArrayList<double[]>(points.size());
		
		for (double[] point : points) {
			double rotation = TrigUtil.getLineRotation(centre[0], centre[1], point[0], point[1]);
			double[] pushedPoint = TrigUtil.getExtensionPoint(rotation, point[0], point[1], pushingDistance);
			pushedPoints.add(pushedPoint);
		}
		
		return pushedPoints;
	}

	@Override
	/**
	 * @returns A nice string representation of this entity based on its x,y coords
	 */
	public String toString() {
		List<double[]> lines = getLines();
		StringBuilder string = new StringBuilder();
		
		for (double[] line : lines) {
			string.append(Arrays.toString(line));
			string.append("\n");
		}
		return string.toString();
	}
	
	public void setX(double newX) {
		this.x = newX;
	}
	
	public double getX() {
		return x;
	}
	
	public void setY(double newY) {
		this.y = newY;
	}
	
	public double getY() {
		return y;
	}
	
	public void setRotation(double newRotation) {
		this.rotation = TrigUtil.normaliseRadians(newRotation);
	}
	
	public double getRotation() {
		return rotation;
	}
	
	public abstract int getWidth();
	
	public abstract int getHeight();
}