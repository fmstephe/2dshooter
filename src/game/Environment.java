package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import entity.Displayable;
import entity.Entity;
import entity.Wall;
import entity.ai.navigation.NavigationMap;
import entity.ai.navigation.Obstruction;
import entity.ai.navigation.VisionUtil;

public class Environment implements Displayable {
	public static final int MIN_DIST_BETWEEN_WALLS = 40;
	
	private NavigationMap navigationMap;
	private List<Wall> walls;
	
	public Environment() {
		walls = new ArrayList<Wall>();
	}
	
	public void addWallsAndMap() {
		int buffer = MIN_DIST_BETWEEN_WALLS/2;
//		walls.add(new Wall(0, 0, Game.SCREEN_WIDTH+buffer, 10+buffer, 0, Color.white));
//		walls.add(new Wall(Game.SCREEN_WIDTH-10, 0, 10+buffer, Game.SCREEN_HEIGHT+buffer, 0, Color.white));
//		walls.add(new Wall(0, Game.SCREEN_HEIGHT-10, Game.SCREEN_WIDTH+buffer, 10+buffer, 0, Color.white));
//		walls.add(new Wall(0, 0, 10+buffer, Game.SCREEN_HEIGHT+buffer, 0, Color.white));
		
		int wallCount = 0;
		Random rand = new Random(System.currentTimeMillis());
		while (wallCount <= 20) {
			int x = rand.nextInt(Game.SCREEN_WIDTH);
			int y = rand.nextInt(Game.SCREEN_HEIGHT);
			int width, height;
			if (rand.nextBoolean()) {
				width = rand.nextInt(Game.SCREEN_WIDTH) + 10 + buffer;
				height = 10 + buffer;
			}
			else {
				width = 10 + buffer;
				height = rand.nextInt(Game.SCREEN_HEIGHT) + 10 + buffer;
			}
			Wall wall = new Wall(x,y,width,height,0,Color.green);
			if (!this.handleCollision(wall)) {
				walls.add(wall);
				wallCount++;
			}
		}
		
		for (Wall wall : walls) {
			wall.setWidth(wall.getWidth()-buffer);
			wall.setHeight(wall.getHeight()-buffer);
		}
		navigationMap = NavigationMap.getInstance(this);
	}
	
	/**
	 * @return All of the walls in this game
	 */
	public List<? extends Entity> getWalls() {
		return walls;
	}
	
	/**
	 * Finds all visible points (corners of walls) from the position (x,y)
	 * 
	 * @param x The X coordinate
	 * @param y The Y coordinate
	 * @return List of all visible points (corners of walls) from the given position
	 */
	public List<double[]> findViewablePoints(double x, double y, double bufferSize) {
		List<Obstruction> obstructions = new ArrayList<Obstruction>(walls.size());
		for (Entity entity : walls) {
			Obstruction obstruction = new Obstruction(entity,x,y);
			obstructions.add(obstruction);
		}
		return VisionUtil.findViewablePoints(x, y, obstructions, bufferSize);
	}
	
	/**
	 * Tests each of the DynamicEntities for environmental collisions. Actual collision handling is 
	 * the responsibility of the DynamicEntity itself.
	 * 
	 * @param dynamicEntities The entities to be tested for Collision
	 */
	public boolean handleCollisions(List<? extends Entity> entities) {
		// Test against wall collisions
		boolean aCollision = false;
		for (Wall wall : walls) {
			for (Entity entity : entities) {
				aCollision |= entity.collidesWith(wall);
			}
		}
		return aCollision;
	}

	/**
	 * Tests entity for environmental collisions. Actual collision handling is 
	 * the responsibility of entity itself.
	 * 
	 * @param entity The DynamicEntity to be tested
	 */
	public boolean handleCollision(Entity entity) {
		boolean aCollision = false;
		for (Wall wall : walls) {
			aCollision |= entity.collidesWith(wall);
		}
		return aCollision;
	}
	
	/**
	 * Draws the environment onto g
	 * 
	 * @param g The Graphics2D onto which the environment (a bunch of walls) will be drawn
	 */
	@Override
	public void draw(Graphics2D g) {
		for (Wall wall : walls) {
			wall.draw(g);
		}
		navigationMap.draw(g);
	}
}