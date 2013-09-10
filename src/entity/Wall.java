package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Wall extends Entity {
	
	int height, width;
	Color color;
	BufferedImage image;
	
	/**
	 * Constructor.
	 * 
	 * @param x
	 * @param y
	 * @param height
	 * @param width
	 * @param color
	 */
	public Wall(int x, int y, int width, int height, double rotation, Color color) {
		super(x, y,rotation);
		
		if (color == null) {
			throw new IllegalArgumentException();
		}
		this.height = height;
		this.width = width;
		this.color = color;
		createImage();
	}
	
	private void createImage() {
		image = new BufferedImage(width,height,BufferedImage.TYPE_INT_BGR);
		Graphics2D g = image.createGraphics();
		g.setColor(color);
		g.fillRect(0,0,image.getWidth(),image.getHeight());
	}
	
	public void draw(Graphics2D g) {
		AffineTransform xform = AffineTransform.getRotateInstance(getRotation(),getX()+(width/2),getY()+(height/2));
		xform.translate(getX(), getY());
		g.setColor(Color.red);
		g.drawImage(image, xform, null);
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
		createImage();
	}

	public void setHeight(int height) {
		this.height = height;
		createImage();
	}
}
