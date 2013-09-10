package entity;

import java.awt.Graphics2D;

public interface Displayable {
	
	/**
	 * Allows the Displayable object to draw itself.
	 * 
	 * @param g Graphics context on which the object can draw itself.
	 */
	public abstract void draw (Graphics2D g);
}