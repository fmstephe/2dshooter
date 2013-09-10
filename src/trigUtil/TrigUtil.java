package trigUtil;


/**
 * A utility class of static methods for doing common trigonometric calculations
 * 
 * @author Francis Stephens
 */
public class TrigUtil {
	
	public static final double FULL_CIRCLE = Math.PI * 2;
	
	/**
	 * Determines the x,y coordinates at the other end of the extension defined by the parameters.
	 * 
	 * @param rotation The rotation from a pointing straight up position, gives the direction of the ray
	 * @param x Starting x position
	 * @param y Starting y position
	 * @param length The length of the extension
	 * @return The final terminating point of this extension
	 */
	public static double[] getExtensionPoint (double rotation, double x, double y, double length) {
		double extX = getXComponent(rotation,length);
		double extY = getYComponent(rotation,length);
		return new double[] {x+extX,y+extY};
	}
	
	/**
	 * Determines the x,y coordinates at the other end of the extension defined by the parameters.
	 * 
	 * @param x1 First x coord
	 * @param y1 First y coord
	 * @param x2 Second x coord
	 * @param y2 Second y coord
	 * @param length The length of the extension from the point (x1,y1)
	 * @return The final terminating point of this extension
	 */
	public static double[] getExtensionPoint (double x1, double y1, double x2, double y2, double length) {
		double rotation = getLineRotation(x1,y1,x2,y2);
		return getExtensionPoint(rotation,x1,y1,length);
	}
	
	/**
	 * Given two points {(x1,y1),(x2,y2)} returns {m,b} from the equation y = mx + b for a line
	 * which passes through both points.
	 * 
	 * @param x1 First x coordinate
	 * @param y1 First y coordinate
	 * @param x2 Second x coordinate
	 * @param y2 Second y coordinate
	 * @return The slope and y intercept of the line passing through the points provided
	 */
	public static double[] getLineEquation(double x1, double y1, double x2, double y2) {
		double coefficient = (y2 - y1) / (x2 - x1);
		double shift = -(coefficient * x1) + y1;
		return new double[] {coefficient,shift};
	}
	
	/**
	 * Given a rotation (in radians) and a single point (x,y) returns {m,b} from the equation y = mx + b
	 * for a line which passes through (x,y) and is oriented by rotation many radians clockwise from the
	 * pointing straight up position.
	 * 
	 * @param rotation The rotated orientation of the line from the straight up position
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @return The slope and y intercept of the line passing through the point provided with the provided rotation
	 */
	public static double[] getLineEquation(double rotation, double x, double y) {
		double coefficient = -(Math.cos(rotation) / Math.sin(rotation));
		double shift = -(coefficient * x) + y;
		return new double[] {coefficient,shift};
	}
	
	/**
	 * Given a line segment the rotation (from a pointing straight up position) in radians of that line.
	 * 
	 * @param x1 The first x coordinate
	 * @param y1 The first y coordinate
	 * @param x2 The second x coordinate
	 * @param y2 The second y coordinate
	 * @return The rotation in radians of the line described by the line segment provided
	 */
	public static double getLineRotation(double x1, double y1, double x2, double y2) {
		double dx = x2 - x1;
		double dy = y1 - y2;
		double length = Math.sqrt((dx*dx)+(dy*dy));
		double xRatio = dx/length;
		double r1 = Math.asin(Math.abs(xRatio));
		if (dx >  0) {
			double r2 = Math.PI/2+(Math.PI/2-r1);
			if (y1 > y2) { // Pointing up a bit
				return r1;
			}
			else {
				return r2;
			}
		}
		else {
			double r2 = Math.PI*1.5 + (Math.PI/2-r1);
			r1 = Math.PI + r1;
			if (y1 < y2) { // Pointing down a bit
				return r1;
			}
			else {
				return r2;
			}
		}
	}

	/**
	 * Returns the length travelled, in the x direction, of the ray given a rotation from the
	 * straight-up position and diagonal length provided.
	 * 
	 * @param rotation The rotated orientation of the line from the straight up position
	 * @param length The length travelled diagonally
	 * @return The horizontal distance travelled by the ray defined
	 */
	public static double getXComponent(double rotation, double length) {
		if (rotation == 0 || rotation == Math.PI) {
			return 0;
		}
		return (length * Math.sin(rotation));
	}
	
	/**
	 * Returns the length travelled, in the y direction, of the ray given a rotation from the
	 * straight-up position and diagonal length provided.
	 * 
	 * @param rotation The rotated orientation of the line from the straight up position
	 * @param length The length travelled diagonally
	 * @return The vertical distance travelled by the ray defined
	 */
	public static double getYComponent(double rotation, double length) {
		if (rotation == Math.PI/2 || rotation == Math.PI*1.5) {
			return 0;
		}
		// Because the coordinates are upside down we return the negative
		return -(length * Math.cos(rotation));
	}
	

	/**
	 * Determines the intersection of two line segments, if one exists.
	 * 
	 * @param x11 The first x coord of line 1
	 * @param y11 The first y coord of line 1
	 * @param x12 The second x coord of line 1
	 * @param y12 The second y coord of line 1
	 * @param x21 The first x coord of line 2
	 * @param y21 The first y coord of line 2
	 * @param x22 The second x coord of line 2
	 * @param y22 The second y coord of line 2
	 * @return The intersection of two lines, if it exists
	 */
	public static double[] findLineSegmentIntersection (double s1x1, double s1y1, double s1x2, double s1y2, double s2x1, double s2y1, double s2x2, double s2y2) {
		double[] point = findIntersectionOfTwoLines(s1x1,s1y1,s1x2,s1y2,s2x1,s2y1,s2x2,s2y2);
		point = nullIfOutOfBounds(point,s2x1,s2y1,s2x2,s2y2);
		return nullIfOutOfBounds(point,s1x1,s1y1,s1x2,s1y2);
	}
	
	public static double[] findLineSegmentIntersectionByRotation (double rotation, double x, double y, double length, double x1, double y1, double x2, double y2) {
		double[] extensionPoint = getExtensionPoint(rotation,x,y,length);
		return findLineSegmentIntersection(x,y,extensionPoint[0],extensionPoint[1],x1,y1,x2,y2);
	}
	
	/**
	 * Determines the intersection of two lines, if one exists.
	 * 
	 * @param rX1 The first x coord of the ray
	 * @param rY1 The first y coord of the ray
	 * @param rX2 The second x coord of the ray
	 * @param rY2 The second y coord of the ray
	 * @param sX1 The first x coord of line segment
	 * @param sY1 The first y coord of line segment
	 * @param sX2 The second x coord of line segment
	 * @param sY2 The second y coord of line segment
	 * @return The intersection of two lines, if it exists
	 */
	public static double[] findRayIntersection (double rx1, double ry1, double rx2, double ry2, double sx1, double sy1, double sx2, double sy2) {
		double[] point = findIntersectionOfTwoLines(rx1,ry1,rx2,ry2,sx1,sy1,sx2,sy2);
		point = nullIfOutOfBounds(point,sx1,sy1,sx2,sy2);
		return nullIfBehind(point,rx1,ry1,rx2,ry2);
	}
	
	public static double[] findIntersectionOfTwoLines(double l1x1, double l1y1, double l1x2, double l1y2, double l2x1, double l2y1, double l2x2, double l2y2) {
		// Cheating check to make sure the lines do not connect up end to end
		// This is required because such delicate intersections can be fucked by cumulative floating point error
		if (l1x1 == l2x1 && l1y1 == l2y1) {
			return new double[] {l1x1,l1y1};
		}
		if (l1x1 == l2x2 && l1y1 == l2y2) {
			return new double[] {l1x1,l1y1};
		}
		if (l1x2 == l2x1 && l1y2 == l2y1) {
			return new double[] {l1x2,l1y2};
		}
		if (l1x2 == l2x2 && l1y2 == l2y2) {
			return new double[] {l1x2,l1y2};
		}
		if (l1x1 == l1x2 && l1y1 > l1y2) {// The first line is pointing up
			if (l2x1 == l2x2) { // The second line is vertical
				return new double[] {l1x1,Math.max(l2y1,l2y2)};
			}
			else {
				double intersectY = solveForY(l2x1,l2y1,l2x2,l2y2,l1x1);
				return new double[] {l1x1,intersectY};
			}
		}
		if (l1x1 == l1x2 && l1y1 < l1y2) { // The first line is pointing straight down
			if (l2x1 == l2x2) { // The second line is vertical
				return new double[] {l1x1,Math.min(l2y1,l2y2)};
			}
			else {
				double intersectY = solveForY(l2x1,l2y1,l2x2,l2y2,l1x1);
				return new double[] {l1x1,intersectY};
			}
		}
		if (l2x1 == l2x2) { // The second line is vertical
			double yIntersect = solveForY(l1x1,l1y1,l1x2,l1y2,l2x1);
			return new double[] {l2x1,yIntersect};
		}
		// Here we solve the simultaneous equation to get the intersection
		double[] firstLine = TrigUtil.getLineEquation(l1x1,l1y1,l1x2,l1y2);
		double[] secondLine = TrigUtil.getLineEquation(l2x1,l2y1,l2x2,l2y2);
		double coefficient1 = firstLine[0];
		double shift1 = firstLine[1];
		double coefficient2 = secondLine[0];
		double shift2 = secondLine[1];
		double xIntersect = (shift1 - shift2)/(coefficient2 - coefficient1);
		double yIntersect = solveForY(l2x1,l2y1,l2x2,l2y2,xIntersect);
		
		return new double[] {xIntersect,yIntersect};
	}
	
	/**
	 * Given a single point, p,  and a line segment, which point along the line segment is nearest to p.
	 * This problem can also be described as what is the intersection of the line which includes p and
	 * is perpendicular to the line segment.  There are two edge cases when the perpendicular line 
	 * intersects not with the line segment but with the line containing that line segment.  Then
	 * we just take the end of the line segment nearest that intersection point.
	 * 
	 * @param px The x coord of the point
	 * @param py The y coord of the point
	 * @param lx1 The first x coord of the line segment
	 * @param ly1 The first y coord of the line segment
	 * @param lx2 The second x coord of the line segment
	 * @param ly2 The second y coord of the line segment
	 * @return The point along the line segment closest to the point p
	 */
	public static double[] findNearestPointAlongSegment(double px, double py, double lx1, double ly1, double lx2, double ly2) {
		double lineRotation = getLineRotation(lx1,ly1,lx2,ly2);
		double perpRotation = normaliseRadians(lineRotation + Math.PI/2);
		double[] xp = getExtensionPoint(perpRotation,px,py,10);
		double[] intersection = findIntersectionOfTwoLines(px,py,xp[0],xp[1],lx1,ly1,lx2,ly2);
		if (nullIfBehind(intersection,lx1,ly1,lx2,ly2) == null) {
			return new double[] {lx1,ly1};
		}
		else if (nullIfBehind(intersection,lx2,ly2,lx1,ly1) == null) {
			return new double[] {lx2,ly2};
		}
		else {
			return intersection;
		}
	}

	/**
	 * Tests whether point lies within the x and y boundaries given.
	 * 
	 * @param point The point for bounds testing
	 * @param bX1 The first x coord
	 * @param bY1 The first y coord
	 * @param bX2 The second x coord
	 * @param bY2 The second y coord
	 * @return point if point lies within the boundaries, null otherwise
	 */
	private static double[] nullIfOutOfBounds(double[] point, double bX1, double bY1,double bX2, double bY2) {
		if (point != null && isBetween(point[0],bX1,bX2) && isBetween(point[1],bY1,bY2)) {
			return point;
		}
		else {
			return null;
		}
	}
	
	/**
	 * Tests whether point, (x,y), lies backward of the ray which starts at (rX1,rY1) and passes through (rX2,rY2).
	 * This does not test that the point lies along the ray, but simply that if the x component is increasing along
	 * the ray then x must be greater than rX1, otherwise it must be smaller.  The same check is done for y.
	 * 
	 * @param point The point for bounds testing
	 * @param bX1 The first x coord
	 * @param bY1 The first y coord
	 * @param bX2 The second x coord
	 * @param bY2 The second y coord
	 * @return point if point lies within the boundaries, null otherwise
	 */
	private static double[] nullIfBehind(double[] point, double rX1, double rY1,double rX2, double rY2) {
		if (point == null) {
			return null;
		}
		else if (!(rX1 >= rX2 && point[0] <= rX1) && 
				!(rX1 <= rX2 && point[0] >= rX1)) {
			return null;
		}
		else if (!(rY1 >= rY2 && point[1] <= rY1) && 
				!(rY1 <= rY2 && point[1] >= rY1)) {
			return null;
		}
		else {
			return point;
		}
	}

	/**
	 * Determines the intersection of two lines, where one line is defined by a single point and a rotation
	 * in radians.  And the other line is defined by a pair of points.
	 * 
	 * @param rotation The rotated orientation of the line from the straight up position
	 * @param x The x coordinate of the first line
	 * @param y The y coordinate of the first line
	 * @param x1 First x coordinate of the second line
	 * @param y1 First y coordinate of the second line
	 * @param x2 Second x coordinate of the second line
	 * @param y2 Second y coordinate of the second line
	 * @return The intersecting point, if one exists, null otherwise
	 */
	public static double[] findRayIntersectionByRotation (double rotation, double x, double y, double x1, double y1, double x2, double y2) {
		double[] extensionPoint = getExtensionPoint(rotation,x,y,10);
		return findRayIntersection(x,y,extensionPoint[0],extensionPoint[1],x1,y1,x2,y2);
	}

	/**
	 * Solves the equation of the ray described by the point (x1,y1) and rotation for X given
	 * the value of y provided.
	 * 
	 * If the ray does is not solvable for y, the Double.NaN is returned.  Since we are solving
	 * for a ray and not a line there are some values of y which do not yield an x value.
	 * 
	 * @param rotation the rotation, in radians, clockwise from the pointing up position
	 * @param x1 First x coordinate
	 * @param y1 First y coordinate
	 * @param y The value of y
	 * @return The value of x for this line, given y or Double.Nan if there is no solution
	 */
	public static double solveForX(double rotation, double x1, double y1,
			double y) {
		if (isBetween(rotation,Math.PI/2,Math.PI*1.5) && y < y1) {
			return Double.NaN;
		} 
		else if (y < y1) {
			return Double.NaN;
		}
		double[] line = getLineEquation(rotation,x1,y1);
		double coefficient = line[0];
		double shift = line[1];
		return (y - shift)/coefficient;
	}
	
	/**
	 * Solves the equation of the line described by the point (x1,y1) and rotation for Y given
	 * the value of x provided.
	 * 
	 * If the ray is not solvable for x, the Double.NaN is returned.  Since we are solving
	 * for a ray and not a line there are some values of x which do not yield an y value.
	 * 
	 * @param rotation the rotation, in radians, clockwise from the pointing up position
	 * @param x1 First x coordinate
	 * @param y1 First y coordinate
	 * @param x The value of x
	 * @return The value of y for this line, given x or Double.Nan if there is no solution
	 */
	public static double solveForY (double rotation, double x1, double y1,
		double x) {
		if (isBetween(rotation,0,Math.PI) && x < x1 ) { // Facing right
			return Double.NaN;
		} 
		else if ( isBetween(rotation,Math.PI,Math.PI*2) && x > x1) { // Facing left
			return Double.NaN;
		}
		double[] line = getLineEquation(rotation,x1,y1);
		double coefficient = line[0];
		double shift = line[1];
		return (coefficient * x) + shift;
	}

	/**
	 * Solves the equation of the line described by the pair of points {(x1,y1),(x2,y2)} for Y given
	 * the value of x provided.
	 * 
	 * @param x1 First x coordinate
	 * @param y1 First y coordinate
	 * @param x2 Second x coordinate
	 * @param y2 Second y coordinate
	 * @param x The value of x
	 * @return The value of y for this line, given x
	 */
	public static double solveForY(double x1, double y1, double x2, double y2,
			double x) {
		double[] line = getLineEquation(x1,y1,x2,y2);
		double coefficient = line[0];
		double shift = line[1];
		return (coefficient * x) + shift;
	}

	/**
	 * Solves the equation of the line described by the pair of points {(x1,y1),(x2,y2)} for X given
	 * the value of y provided.
	 * 
	 * @param x1 First x coordinate
	 * @param y1 First y coordinate
	 * @param x2 Second x coordinate
	 * @param y2 Second y coordinate
	 * @param y The value of y
	 * @return The value of x for this line, given y
	 */
	public static double solveForX(double x1, double y1, double x2, double y2,
			double y) {
		double[] line = getLineEquation(x1,y1,x2,y2);
		double coefficient = line[0];
		double shift = line[1];
		return (y - shift)/coefficient;
	}

	/**
	 * Indicates if the value x is between x1 and x2.
	 * 
	 * If x is NaN then I am currently assuming that it will never lie between any
	 * two double values.  Surprisingly couldn't easily find a reference to how
	 * weird double comparisons behave in Java :)
	 * 
	 * @param x The value for testing
	 * @param x1 First boundary value
	 * @param x2 Second boundary value
	 * @return true if the x is between x1 and x2, false otherwise
	 */
	public static boolean isBetween(double x, double x1, double x2) {
		if (x <= x1 && x >= x2) {
			return true;
		}
		if (x <= x2 && x >= x1) {
			return true;
		}
		return false;
	}

	/**
	 * Gets the straight line distance between the pair of points {(x,y),(x1,y1)}.
	 * 
	 * @param x The first x coordinate
	 * @param y The first y coordinate
	 * @param x1 The second x coordinate
	 * @param y1 The second y coordinate
	 * @return The straight line distances between {(x,y),(x1,y1)}
	 */
	public static double getDistance(double x, double y, double x1, double y1) {
		return Math.sqrt(Math.pow(x-x1,2) + Math.pow(y-y1,2));
	}

	/**
	 * Finds the shortest rotational distances, in radians between two rotations.
	 * 
	 * @param r1 The first rotation
	 * @param r2 The second rotation
	 * @return The shortest rotational distance between r1 and r2
	 */
	public static double rotationDistance(double r1, double r2) {
		double d1 = Math.abs(r1-r2);
		double d2 = (Math.PI*2)-Math.max(r1, r2)+(Math.min(r1,r2));
		return Math.min(d1, d2);
	}
	
	/**
	 * Indicates whether r1 is nearest to r2 in the clockwise (>0) or
	 * anti-clockwise (<0) direction.  If the two rotations are the same
	 * then 0 is returned.
	 * 
	 * @param r1 The first rotation
	 * @param r2 The second rotation
	 * @return int indicating the direction, clockwise or anti-clockwise, to travel for r2 to meet r1
	 */
	public static int rotationDirection(double rotation1, double rotation2) {
		double r1 = rotation1;
		double r2 = rotation2;
		
		if (r1 == r2) {
			return 0;
		}
		if (r1 > Math.PI) {
			r1 = normaliseRadians(r1 - Math.PI);
			r2 = normaliseRadians(r2 - Math.PI);
		}
		if ((r2 - r1) > 0 && (r2 - r1) < Math.PI) {
			return 1;
		}
		else {
			return -1;
		}
	}
	
	public static double normaliseRadians(double radians) {
		while (radians > FULL_CIRCLE) {
			radians -= FULL_CIRCLE;
		}
		while (radians < -FULL_CIRCLE) {
			radians += FULL_CIRCLE;
		}
		if (radians < 0) {
			radians = Math.PI*2 + radians;
		}
		
		return radians;
	}
}
