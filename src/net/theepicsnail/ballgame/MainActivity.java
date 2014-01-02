package net.theepicsnail.ballgame;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;

import android.util.DisplayMetrics;

public class MainActivity extends SimpleBaseGameActivity implements
		GameStateManager {
	int CAMERA_WIDTH, CAMERA_HEIGHT;
	private  Camera camera;
	@Override
	public EngineOptions onCreateEngineOptions() {
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		CAMERA_WIDTH = displayMetrics.widthPixels;
		CAMERA_HEIGHT = displayMetrics.heightPixels;

		camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		
		EngineOptions opts = new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR,
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
		
		
		return opts;
	}

	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		Ball.VERTEX_MANAGER = this.getVertexBufferObjectManager();
	}

	Board board;
	final Scene scene = new Scene();

	@Override
	protected Scene onCreateScene() {
		scene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
		onWin();
		return scene;
	}

	public void loadLevel(int level) {
		
		MapLoader loader;
		try {
			loader = new MapLoader(level, this.getTextureManager(), this.getAssets(),
					this.getVertexBufferObjectManager());
		} catch (TMXLoadException e) {
			Debug.e("Couldn't load level " + level, e);
			return;
		}
		
		TMXTiledMap map = loader.getMap();
		
		GameResourceManager display = new GameResourceManager(
				loader.getShots(),
				loader.getSplitters(),
				loader.getBoxes(),
				loader.getTargets(),
				this.getVertexBufferObjectManager(), 
				this.getFontManager(),
				this.getTextureManager(), map);
		
		Ball.BALL_TEXTURE = map
				.getTextureRegionFromGlobalTileID(Piece.BALL.tileId);
		board = new Board(CAMERA_HEIGHT, map, this, display );

		board.setPosition(CAMERA_WIDTH - CAMERA_HEIGHT, 0);
		scene.attachChild(board);
		scene.registerTouchArea(board);
		scene.registerUpdateHandler(board);

		scene.attachChild(MainActivity.makeColoredRectangle(0, 0, CAMERA_WIDTH
				- CAMERA_HEIGHT, CAMERA_HEIGHT, .5f,
				this.getVertexBufferObjectManager()));

		final Entity toolboxGroup = new Entity(0, 0);
		toolboxGroup.attachChild(makeColoredRectangle(0, 0, CAMERA_WIDTH
				- CAMERA_HEIGHT, CAMERA_HEIGHT, 0.8f,
				this.getVertexBufferObjectManager()));

		scene.attachChild(display);
	}

	public static Rectangle makeColoredRectangle(final float pX,
			final float pY, final float pw, final float ph, final float color,
			VertexBufferObjectManager manager) {
		final Rectangle coloredRect = new Rectangle(pX, pY, pw, ph, manager);
		coloredRect.setColor(color, color, color);
		return coloredRect;
	}

	int level = 0;

	public void onWin() {
		level++;
		scene.detachChildren();
		scene.clearEntityModifiers();
		scene.clearTouchAreas();
		scene.clearUpdateHandlers();

		scene.reset();
		loadLevel(level);
	}
}