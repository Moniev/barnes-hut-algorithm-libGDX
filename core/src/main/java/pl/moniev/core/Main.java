package pl.moniev.core;

import java.util.List;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import pl.moniev.core.Body.Body;
import pl.moniev.core.MainEngine.Engine;

public class Main implements ApplicationListener {
	public Texture texture;
	public SpriteBatch batch;
	public ShapeRenderer shapeRenderer;
	public float elapsed;

	private OrthographicCamera camera;

	private final int WINDOW_WIDTH = 1440;
    private final int WINDOW_HEIGHT = 1440; 

    private Engine engine; // The game or simulation engine responsible for processing and updating the scene.
    private BitmapFont font; // Font used for rendering text on the screen.
    private SpriteBatch spriteBatch; // Used for 2D sprite rendering.

    private boolean renderTree, renderBodies; // Flags to control whether the tree and bodies should be rendered.
    private boolean showFPS, showThreads, showMemoryUsage, showBodiesCount; // Flags to show various performance metrics like FPS, threads, memory usage, and particle count.
    private boolean paused; // Flag to pause the simulation or game.
    private int loop; // Counter for loop iterations (could be used for timing or limiting frame updates).

	private InputController inputController; // Class for controlling users keyboards nad mouse inputs.

	@Override
	public void create () {
        inputController = new InputController(this);
		Gdx.input.setInputProcessor(inputController);
        camera = new OrthographicCamera(WINDOW_WIDTH, WINDOW_HEIGHT);
        camera.position.set(WINDOW_WIDTH / 2f, WINDOW_HEIGHT / 2f, 0);
        camera.update();

		batch = new SpriteBatch();
        spriteBatch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		font = new BitmapFont();
		engine = new Engine(10000, 512, batch, 1, 4, 1440f, 1440f);
		renderTree = true;
		renderBodies = true;
        showThreads = true;
        showFPS = true;
        showMemoryUsage = true;
        showBodiesCount = true;
		List<Body> disk = engine.createDisk();
		engine.addBodies(disk);
	}

	@Override
	public void resize (int width, int height) {
	}

	@Override
	public void render () {
		loop++;
        elapsed += Gdx.graphics.getDeltaTime();
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (!paused) engine.update();

		if (renderBodies) {
            batch.begin();
            engine.renderBodies();
            batch.end();
        }

		if(renderTree) engine.renderTree(shapeRenderer);

		spriteBatch.begin();
        if (showMemoryUsage) font.draw(spriteBatch, "MEMORY USAGE: " + getMemoryUsage() + "mb", 10, Gdx.graphics.getHeight() - 10);
        if (showBodiesCount) font.draw(spriteBatch, "BODIES COUNT: " + getBodiesCount(), 10, Gdx.graphics.getHeight() - 25);
        if (showThreads) font.draw(spriteBatch, "THREADS: " + getThreads(), 10, Gdx.graphics.getHeight() - 40);
        if (showFPS) font.draw(spriteBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, Gdx.graphics.getHeight() - 55);
        if (paused) font.draw(spriteBatch, "PAUSED", Gdx.graphics.getWidth() / 2 - font.getBounds("PAUSED").width / 2, Gdx.graphics.getHeight() / 2);
        spriteBatch.end();
	}

	/**
     * Gets the number of threads currently in use.
     * 
     * @return the number of threads.
     */
    private int getThreads() {
        return Thread.getAllStackTraces().size();
    }

    /**
     * Gets the current memory usage of the application.
     * 
     * @return the memory usage in megabytes.
     */
    private long getMemoryUsage() {
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        return (totalMemory - freeMemory) / (1024 * 1024);
    }

    private int getBodiesCount() {
        return engine.quadTree.bodies.size();
    }

	public OrthographicCamera getCamera() {
		return camera;
	}

	public void toggleRenderTree() {
        renderTree = !renderTree;
    }

    public void toggleRenderBodies() {
        renderBodies = !renderBodies;
    }

    public void toggleShowFPS() {
        showFPS = !showFPS;
    }

    public void toggleShowMemoryUsage() {
        showMemoryUsage = !showMemoryUsage;
    }

    public void toggleShowThreads() {
        showThreads = !showThreads;
    }

    public void togglePaused() {
        paused = !paused;
    }

	@Override
	public void pause () {
	}

	@Override
	public void resume () {
	}

	@Override
	public void dispose () {
		batch.dispose();
        spriteBatch.dispose();
        shapeRenderer.dispose();
        font.dispose();
	}
}
