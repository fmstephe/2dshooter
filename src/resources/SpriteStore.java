package resources;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class SpriteStore {
	
	public static final String alienImage = "images/alienImage.jpg";
	public static final String playerImage = "images/playerImage.jpg";
	public static final String shotImage = "images/shotImage.jpg";
	
	private static final SpriteStore singleton = new SpriteStore();
	private Map<String,Sprite> spritesMap;
	
	private SpriteStore() {
		spritesMap = new HashMap<String,Sprite>();
	}
	
	public static SpriteStore getInstance() {
		return singleton;
	}
	
	public static Sprite getSprite(String ref) {
		return getInstance().getSpriteInt(ref);
	}
	private Sprite getSpriteInt(String ref) {
		Sprite sprite = spritesMap.get(ref);
		
		try {
			if (sprite == null) {
				URL url = this.getClass().getClassLoader().getResource(ref);
				Image sourceImage = ImageIO.read(url);
				GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
				Image image = gc.createCompatibleImage(sourceImage.getWidth(null),sourceImage.getHeight(null),Transparency.BITMASK);
				image.getGraphics().drawImage(sourceImage,0,0,null);
				// Create the sprite and cache it
				sprite = new Sprite(image);
				spritesMap.put(ref, sprite);
			}
		}
		catch(Exception e) {
			throw new RuntimeException("Failed to open a sprite file with ref = " + ref,e);
		}
			
		return sprite;
	}
}