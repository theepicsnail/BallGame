package net.theepicsnail.ballgame;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;

import android.util.DisplayMetrics;
import android.widget.Toast;

public class MainActivity extends SimpleBaseGameActivity implements GameStateManager {
	int CAMERA_WIDTH, CAMERA_HEIGHT;
	private BitmapTextureAtlas mBitmapTextureAtlas;
	private TextureRegion mFaceTextureRegion;
	private Font mFont;

	@Override
	public EngineOptions onCreateEngineOptions() {
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		CAMERA_WIDTH = displayMetrics.widthPixels;
		CAMERA_HEIGHT = displayMetrics.heightPixels;

		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED,
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}

	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(
				this.getTextureManager(), 128, 128, TextureOptions.BILINEAR);

		// Ball.BALL_TEXTURE = BitmapTextureAtlasTextureRegionFactory
		// .createFromAsset(this.mBitmapTextureAtlas, this, "ball.png", 0,
		// 0);
		this.mBitmapTextureAtlas.load();
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
		int gap = 10;

		/*
		 * Board board = new Board(8, board_size,
		 * this.getVertexBufferObjectManager());
		 */
		// Board board = new Board(this.getAssets(), this.getTextureManager(),
		// this.getVertexBufferObjectManager());

		final TMXLoader tmxLoader = new TMXLoader(this.getAssets(),
				this.mEngine.getTextureManager(),
				TextureOptions.BILINEAR_PREMULTIPLYALPHA,
				this.getVertexBufferObjectManager());

		/*
		 * new ITMXTilePropertiesListener() {
		 * 
		 * @Override public void onTMXTileWithPropertiesCreated( final
		 * TMXTiledMap pTMXTiledMap, final TMXLayer pTMXLayer, final TMXTile
		 * pTMXTile, final TMXProperties<TMXTileProperty> pTMXTileProperties) {
		 * switch (pTMXTile.getGlobalTileID()) { case 4: case 5: case 6: case
		 * 10:
		 * 
		 * } } });
		 */
		TMXTiledMap map = null;
		try {
			map = tmxLoader.loadFromAsset("tmx/level" + level + ".tmx");
		} catch(TMXLoadException excp) {
			
			Debug.e("Couldn't load level " + level, excp);
			return;
		}
		Ball.BALL_TEXTURE = map
				.getTextureRegionFromGlobalTileID(Piece.BALL.tileId);
		 board = new Board(CAMERA_HEIGHT, map,this);

		/*
		 * final TMXLayer layer = map.getTMXLayers().get(0);
		 */
		board.setPosition(CAMERA_WIDTH - CAMERA_HEIGHT, 0);
		scene.attachChild(board);
		scene.registerTouchArea(board);
		scene.registerUpdateHandler(board);

		scene.attachChild(MainActivity.makeColoredRectangle(0, 0, CAMERA_WIDTH
				- CAMERA_HEIGHT, CAMERA_HEIGHT, .5f,
				this.getVertexBufferObjectManager()));

		// board.setPosition(CAMERA_WIDTH - CAMERA_HEIGHT, 0);
		// scene.attachChild(board);

		final Entity toolboxGroup = new Entity(0, 0);
		toolboxGroup.attachChild(makeColoredRectangle(0, 0, CAMERA_WIDTH
				- CAMERA_HEIGHT, CAMERA_HEIGHT, 0,
				this.getVertexBufferObjectManager()));
		toolboxGroup.attachChild(makeColoredRectangle(gap, gap, CAMERA_WIDTH
				- CAMERA_HEIGHT - 2 * gap, CAMERA_HEIGHT - 2 * gap, 1,
				this.getVertexBufferObjectManager()));
		// scene.attachChild(toolboxGroup);

		// board.createBall(0, 0, Direction.DOWN);
		
		GameStatusDisplay display = new GameStatusDisplay(1, 2, 3, 4, 
				this.getVertexBufferObjectManager(), 
				this.getFontManager(),
				this.getTextureManager(),
				map);
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
		level ++;
		scene.detachChildren();
		scene.clearEntityModifiers();
		scene.clearTouchAreas();
		scene.clearUpdateHandlers();

		scene.reset();
		loadLevel(level);
	}
}