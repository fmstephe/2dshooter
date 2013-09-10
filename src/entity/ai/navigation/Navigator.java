package entity.ai.navigation;

import game.Environment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class Navigator {
	final double oX;
	final double oY;
	final double destX;
	final double destY;
	Environment environment;
	Stack<double[]> intermediateSteps;
	
	/**
	 * 
	 * @param originX
	 * @param originY
	 * @param destinationX
	 * @param destinationY
	 * @param game
	 */
	public Navigator(Environment environment, double originX, double originY, double destinationX,
			double destinationY) {
		super();
		System.out.println("Creating Navigator");
		this.environment = environment;
		this.oX = originX;
		this.oY = originY;
		// Find the nearest corner point visible from the destination provided
		List<double[]> destinationPoints = environment.findViewablePoints(destinationX,destinationY,Environment.MIN_DIST_BETWEEN_WALLS/2);
		aStarSort(destinationPoints, destinationX, destinationY, destinationX, destinationY);
		if (destinationPoints.size() > 0) {
			double[] destination = destinationPoints.get(destinationPoints.size()-1);
			destX = destination[0];
			destY = destination[1];
			intermediateSteps = generateSteps();
		}
		else {
			intermediateSteps = new Stack<double[]>();
			destX = oX;
			destY = oY;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private Stack<double[]> generateSteps() {
		// Naive depth first A* search
		Stack<double[]> steps = new Stack<double[]>();
		List<double[]> visitedLocations = new ArrayList<double[]>();
		// A reverse list as a stack, elements are pushed onto and popped off the end of the list
		Stack<Stack<double[]>> searchStack = new Stack<Stack<double[]>>();
		Stack<double[]> firstLevel = new Stack<double[]>();
		firstLevel.push(new double[] {oX,oY});
		searchStack.push(firstLevel);
		
		while (!searchStack.isEmpty()) {
			Stack<double[]> currentLevel = searchStack.peek();//Peek
			if (currentLevel.size() == 0) {
				searchStack.pop();// Pop
				steps.pop();// Pop
				continue;
			}
			double[] currentPoint = currentLevel.pop();//Pop
			visitedLocations.add(currentPoint);
			steps.push(currentPoint);
			if (currentPoint[0] == destX && currentPoint[1] == destY) {
				break;
			}
			Stack<double[]> newLevel = new Stack<double[]>();
			newLevel.addAll(environment.findViewablePoints(currentPoint[0],currentPoint[1],Environment.MIN_DIST_BETWEEN_WALLS/2));
			removeVisitedPoints(newLevel,visitedLocations);
			aStarSort(newLevel, currentPoint[0], currentPoint[1], destX, destY);
			// This relies on the elements being added to the end of searchStack
			searchStack.push(newLevel);
		}
		steps.remove(0);
		return steps;
	}
	
	/**
	 * Removes each of the visited points in visitedLocations which occur in newLevel.  This prevents
	 * infinite loops.
	 * 
	 * @param newLevel
	 * @param visitedLocations
	 */
	private void removeVisitedPoints(Stack<double[]> newLevel, List<double[]> visitedLocations) {
		for (Iterator<double[]> itr = newLevel.iterator(); itr.hasNext();) {
			double[] point = itr.next();
			for (double[] visited : visitedLocations) {
				if (Arrays.equals(point, visited)) {
					itr.remove();
				}
			}
		}
	}

	/**
	 * 
	 * @param viewablePoints
	 */
	void aStarSort(List<double[]> viewablePoints, final double x, final double y, final double destX, final double destY) {
		Collections.sort(viewablePoints, new Comparator<double[]>() {
			public int compare(double[] point1, double[] point2) {
				// Wee reverse ordering - most distant first
				double distanceFrom1 = Math.sqrt(((destX-point1[0])*(destX-point1[0]))+((destY - point1[1])*(destY - point1[1])));
				double distanceTo1 = Math.sqrt(((x-point1[0])*(x-point1[0]))+((y - point1[1])*(y - point1[1])));
				double distance1 = Math.sqrt(distanceFrom1*distanceFrom1 + distanceTo1*distanceTo1);
				
				double distanceFrom2 = Math.sqrt(((destX-point2[0])*(destX-point2[0]))+((destY - point2[1])*(destY - point2[1])));
				double distanceTo2 = Math.sqrt(((x-point2[0])*(x-point2[0]))+((y - point2[1])*(y - point2[1])));
				double distance2 = Math.sqrt((distanceFrom2*distanceFrom2) + (distanceTo2*distanceTo2));
				if (distance1 > distance2) {
					return -1;
				}
				else if (distance1 == distance2) {
					return 0;
				}
				else {
					return 1;
				}
			}
		});
	}

	public double[] getUltimateDestination() {
		return new double[] {destX,destY};
	}
	
	public boolean hasMoreSteps() {
		return !intermediateSteps.isEmpty();
	}

	public double[] currentStep() {
		return intermediateSteps.get(0);
	}
	
	public double[] discardCurrentStep() {
		return intermediateSteps.remove(0);
	}
}