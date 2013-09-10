package entity.ai.navigation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * 
 * @author Francis Stephens
 */
public class VisionUtil {

	/**
	 * Traversing the list of obstructions we find every point (corner of an entity) which is visible to 
	 * the flashlight.
	 * 
	 * @param viewerX
	 * @param viewerY
	 * @param obstructions
	 * @param bufferSize 
	 * @return
	 */
	public static List<double[]> findViewablePoints(double viewerX, double viewerY, List<Obstruction> obstructions, double bufferSize) {
		Collections.sort(obstructions);
		List<double[]> viewablePoints = new ArrayList<double[]>();
		// Traverse the list from farthest to nearest obstruction
		for (Obstruction obstruction : obstructions) {
			List<double[]> testingPoints = obstruction.entity.pushOutPoints(obstruction.entity.getCorners(),bufferSize);
			for (Obstruction blocker : obstructions) {
				if (testingPoints.isEmpty()) {break;}
				filterBlockedPoints(viewerX,viewerY,testingPoints,blocker); // check remaining blockers
			}
			viewablePoints.addAll(testingPoints);
		}
		return viewablePoints;
	}

	/**
	 * 
	 * @param viewerX
	 * @param viewerY
	 * @param obstructionPoints
	 * @param blocker
	 * @param compareWithSelf
	 */
	private static void filterBlockedPoints(double viewerX, double viewerY, List<double[]> testingPoints, Obstruction blocker) {
		for (Iterator<double[]> itr = testingPoints.iterator(); itr.hasNext();) {
			double[] point = itr.next();
			List<double[]> intersections = blocker.entity.findIntersectionsIgnoringMutualTerminals(viewerX,viewerY,point[0],point[1]);
			if (intersections.size() > 0) {
				itr.remove();
			}
		}
	}
}