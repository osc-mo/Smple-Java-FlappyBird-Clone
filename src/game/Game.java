package game;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import javax.swing.JFrame;

public class Game {
	
	public final static int WIDTH = 800, HEIGHT = 600;
	
	private String gameName = "Flappy Bird";
	
	private Canvas game = new Canvas(); 
	
	private Input input;
	
	private ArrayList<Updatable> updatables = new ArrayList<>();
	private ArrayList<Renderable> renderables = new ArrayList<>();
	
	public void addUpdatable(Updatable u) {
		updatables.add(u);
	}
	
	public void removeUpdatable(Updatable u) {
		updatables.remove(u);
	}
	
	public void addRenderable(Renderable r) {
		renderables.add(r);
	}
	
	public void removeRenderable(Renderable r) {
		renderables.remove(r);
	}
	
	public void start () {
		//Initialize windows 
		Dimension gameSize = new Dimension(Game.WIDTH, Game.HEIGHT);
		JFrame gameWindow = new JFrame(gameName);
		gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameWindow.setSize(gameSize);
		gameWindow.setResizable(false);
		gameWindow.setVisible(true);
		game.setSize(gameSize);
		game.setMinimumSize(gameSize);
		game.setMaximumSize(gameSize);
		game.setPreferredSize(gameSize);
		gameWindow.add(game);
		gameWindow.setLocationRelativeTo(null);
		
		// Init Input
		input = new Input();
		game.addKeyListener(input);
		
		//Game Loop
		final int TICKS_PER_SECOND = 60; //how many times game will loop (refresh) per second
		final int TIME_PER_TICK = 1000 / TICKS_PER_SECOND; //how long one loop (refresh) should take
		final int MAX_FRAMESKIPS = 5; //times allowed to update per render
		
		long timeAtLastFPSCheck = 0;
		int ticks = 0;
		
		long nextGameTick = System.currentTimeMillis();
		int loops;
		float interpolation;
		
		
		boolean running = true;
		while(running) {
			//Updating
			loops = 0;
			
			//if update is taking longer than expected, keep updating until update limit is reached
			while (System.currentTimeMillis() > nextGameTick && loops < MAX_FRAMESKIPS) {
				update();
				ticks++;
				
				nextGameTick += TIME_PER_TICK;
				loops++;
			}
			
			//Rendering
			interpolation = (float) (System.currentTimeMillis() + TIME_PER_TICK - nextGameTick) 
							/ (float) TIME_PER_TICK;
			render(interpolation);
			
			
			//FPS Check
			if (System.currentTimeMillis() - timeAtLastFPSCheck >= 1000) {
				System.out.println("FPS: " + ticks);
				gameWindow.setTitle(gameName + " - FPS: " + ticks);
				ticks = 0;
				timeAtLastFPSCheck = System.currentTimeMillis();
			}
			
			
		}
		//Game End
	}

	private void update() {
		for(Updatable u : updatables) {
			u.update(input);
		}
	}
	
	private void render (float interpolation) {
		
		BufferStrategy  b = game.getBufferStrategy();
		if(b == null) {
			game.createBufferStrategy(2);
			return;
		}
		
		Graphics2D g = (Graphics2D) b.getDrawGraphics();
		g.clearRect(0, 0, game.getWidth(), game.getHeight()); 
		for(Renderable r: renderables) {
			r.render(g, interpolation);
		}
		g.dispose();
		b.show();
	}
}
