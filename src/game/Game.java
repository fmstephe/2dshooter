package game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import resources.SpriteStore;
import entity.AlienEntity;
import entity.Entity;
import entity.PlayerEntity;
import entity.ShotEntity;

/**
 * 
 * @author Francis Stephens
 */
public class Game extends Canvas {

	private static final long serialVersionUID = -4969980112298526169L;
	public static final int SCREEN_WIDTH = 800;
	public static final int SCREEN_HEIGHT = 600;
	public static final double DIAGONAL_LENGTH = Math.sqrt(((SCREEN_HEIGHT*SCREEN_HEIGHT)+(SCREEN_WIDTH*SCREEN_WIDTH)));
	
	private PlayerEntity player;
	private List<AlienEntity> aliens;
	private List<AlienEntity> addedAliens;
	private List<AlienEntity> removedAliens;
	private List<ShotEntity> addedShots;
	private List<ShotEntity> removedShots;
	private List<ShotEntity> shots;
	private BufferStrategy strategy;
	private Environment environment;
	
	public static void main(String[] argv) {
		Game game = new Game();
		game.init();
		// Start the loop
		game.gameLoop();
	}
	
	private void init() {
		createEntities();
		// Setup the controls
		addKeyListener(player.getControlListener());
		addMouseListener(player.getMouseListener());
		JFrame container = new JFrame("2D Shooter");
		
		// get the panel used by container in order to change its config
		JPanel panel = (JPanel) container.getContentPane();
		panel.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
		// null the layout
		panel.setLayout(null);
		setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		
		panel.add(this);
		
		//Tell AWT not to redraw our frame
		setIgnoreRepaint(true);
		
		// Finally make the window visible
		container.pack();
		container.setResizable(false);
		container.setVisible(true);
		
		// Create the buffering strategy that will allow us to use
		createBufferStrategy(2);
		strategy = getBufferStrategy();
	}
	
	/**
	 * Returns the static environment for this game.
	 * 
	 * @return The environment for this game
	 */
	public Environment getEnvironment() {
		return environment;
	}
	
	private void createEntities() {
		player = new PlayerEntity(SpriteStore.getSprite(SpriteStore.playerImage),this,500,500,0);
		player.init();
		aliens = new ArrayList<AlienEntity>();
		addedAliens = new ArrayList<AlienEntity>();
		removedAliens = new ArrayList<AlienEntity>();
		shots = new ArrayList<ShotEntity>();
		addedShots = new ArrayList<ShotEntity>();
		removedShots = new ArrayList<ShotEntity>();
		environment = new Environment();
		environment.addWallsAndMap();
	}
	
	/**
	 * 
	 * 
	 * @param entity
	 */
	public synchronized void removeEntity(Entity entity) {
		System.out.println("Removing entity");
		if (entity instanceof AlienEntity) {
			synchronized(removedAliens) {
				removedAliens.add((AlienEntity)entity);
			}
		}
		if (entity instanceof ShotEntity) {
			synchronized(removedShots) {
				removedShots.add((ShotEntity)entity);
			}
		}
	}
	
	/**
	 * 
	 * 
	 * @param entity
	 */
	public synchronized void addEntity(Entity entity) {
		System.out.println("Adding Entity");
		if (entity instanceof AlienEntity) {
			synchronized(addedAliens) {
				addedAliens.add((AlienEntity)entity);
			}
		}
		if (entity instanceof ShotEntity) {
			synchronized(addedShots) {
				addedShots.add((ShotEntity)entity);
			}
		}
	}
	
	private void gameLoop() {
		boolean gameRunning = true;
		long lastLoopTime = System.currentTimeMillis();
		
		while(gameRunning) {
//			System.out.println("Game Running");
			// Determine time lapse since last update
			long delta = System.currentTimeMillis() - lastLoopTime;
			lastLoopTime = System.currentTimeMillis();
			
			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
			g.setColor(Color.black);
			g.fillRect(0,0,SCREEN_WIDTH,SCREEN_HEIGHT);
			
			manageEntities(g,delta);
			
			g.dispose();
			strategy.show();
			
			// Pause for a bit
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void manageEntities(Graphics2D g, long delta) {
		updateEntities();
		for (AlienEntity alien : aliens) {
			alien.reset();
			alien.move(delta);
			alien.act(delta);
		}
		player.reset();
		player.move(delta);
		player.act(delta);
		
		environment.handleCollisions(aliens);
		environment.handleCollisions(shots);
		environment.handleCollision(player);
		environment.draw(g);
		
		for (AlienEntity alien : aliens) {
			alien.draw(g);
		}
		for (ShotEntity shot : shots) {
			shot.draw(g);
		}
		player.draw(g);
	}

	private void updateEntities() {
		
		synchronized(removedShots) {
			shots.removeAll(removedShots);
			removedShots.clear();
		}
		synchronized(addedShots) {
			shots.addAll(addedShots);
			addedShots.clear();
		}
		synchronized(removedAliens) {
			aliens.removeAll(removedAliens);
			removedAliens.clear();
		}
		synchronized(addedAliens) {
			aliens.addAll(addedAliens);
			addedAliens.clear();
		}
	}
}