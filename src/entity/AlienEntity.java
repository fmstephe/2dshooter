package entity;

import resources.Sprite;
import game.Game;

public class AlienEntity extends DynamicEntity {

	public AlienEntity(Sprite sprite, Game game, int x, int y, double rotation) {
		super(sprite, game, x, y, rotation);
	}
	
	public void move(long delta) {
		// if we have reached the edge of the screen and
		// are moving left then reverse direction
		
		// proceed with normal move
		super.move(delta);
	}
	
	public boolean collidesWith(Entity other) {
		
		boolean collides = super.collidesWith(other);
		
		// if its an shot, notify the game that the player
		// is dead
		if (collides && other instanceof ShotEntity) {
			game.removeEntity(this);
			game.removeEntity(other);
		}
		
		return collides;
	}
}
