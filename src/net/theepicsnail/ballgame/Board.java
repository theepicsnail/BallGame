package net.theepicsnail.ballgame;

import java.util.ArrayList;

import org.andengine.entity.Entity;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

public class Board extends Entity implements ITouchArea {
	
	//For ITouchArea
	@Override
	public boolean contains(float pX, float pY) {
		float[] scene = convertSceneToLocalCoordinates(pX, pY);
		if (scene[0] < 0 || scene[1] < 0)
			return false; // Fix rounding errors in TMXTileAt

		// If we can fetch a tile, then we're in the board. Otherwise we're not
		return layer.getTMXTileAt(pX, pY) != null;
	}

	boolean dragging_splitter = false;
	int splitter_pos = 0;

	//Touch events. Picking up/setting down splitters. Starting balls.
	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			float pTouchAreaLocalX, float pTouchAreaLocalY) {

		float[] scene = this.convertLocalToSceneCoordinates(pTouchAreaLocalX,
				pTouchAreaLocalY);
		TMXTile tile = layer.getTMXTileAt(scene[0], scene[1]);
		int pos = tile.getTileRow() * map.getTileColumns()
				+ tile.getTileColumn();

		if (dragging_splitter && pSceneTouchEvent.isActionUp()
				&& tile.getGlobalTileID() == Piece.EMPTY.tileId) {
			dragging_splitter = false;
			if (splitter_pos != pos)
				Piece.EMPTY
						.click(this, tile.getTileRow(), tile.getTileColumn());
		}
		if (tile == null || !pSceneTouchEvent.isActionDown())
			return false;

		if (tile.getGlobalTileID() == Piece.MOVABLE_SPLITTER.tileId) {
			dragging_splitter = true;
			splitter_pos = pos;
		} else
			dragging_splitter = false;

		float[] tilePos = { tile.getTileX(), tile.getTileY(),
				tile.getTileX() + tile.getTileWidth(),
				tile.getTileY() + tile.getTileHeight() };

		layer.getLocalToParentTransformation().transform(tilePos);

		Piece.fromTileId(tile.getGlobalTileID()).click(this, tile.getTileRow(),
				tile.getTileColumn());

		return false;
	}

	public void convertColRowToPosition(float[] f) {
		f[0] *= map.getTileWidth();
		f[1] *= map.getTileHeight();
		layer.getLocalToParentTransformation().transform(f);
	}

	// Constructor, not sure what all is necessary here. 
	private GameStateManager gameState;
	private GameResourceManager gameResource;
	private TMXTiledMap map;
	private TMXLayer layer;
	
	public Board(float size, TMXTiledMap map, GameStateManager state, GameResourceManager game) {
		this.gameResource = game;
		this.map = map;
		this.gameState = state;
		/*
		 * Don't delete this code until you've moved it into MAinActivity when we load the level
		 * This should set the splitter count in the GameResourceManager
		 
		for (TMXTiledMapProperty prop : map.getTMXTiledMapProperties()) {
			if (prop.getName().equals("splitters"))
				splitters = Integer.parseInt(prop.getValue());
		}
		*/

		layer = map.getTMXLayers().get(0);
		layer.setScaleCenter(0, 0);
		layer.setScale((float) (size)
				/ (map.getTileColumns() * map.getTileWidth()));
		attachChild(layer);
	}

	//TODO Look into making some manager for dealing with the balls and locks.
	// Update all of the balls
	private ArrayList<Ball> balls = new ArrayList<Ball>();
	private ArrayList<Ball> waitingBalls = new ArrayList<Ball>();
	private ArrayList<Ball> deadBalls = new ArrayList<Ball>();

	private final float seconds_per_keyframe = 1f;
	private float since_last_keyframe = 0;

	@Override
	public void onManagedUpdate(final float pSecondsElapsed) {
		synchronized (balls) {
			since_last_keyframe += pSecondsElapsed;
			float progress = 0;
			boolean advance = false;
			if (since_last_keyframe > seconds_per_keyframe) {
				advance = true;
				since_last_keyframe = 0;
				if ( balls.size() > 0 )
				gameResource.time().increment();
			} else {
				progress = since_last_keyframe / seconds_per_keyframe;
			}
			for (Ball b : balls) {

				if (advance)
					b.advanceCell();
				else {
					b.updateLocation(progress);
				}

			}

			if (advance) {
				synchronized (waitingBalls) {
					for (Ball b : waitingBalls) {
						this.balls.add(b);
					}
					waitingBalls.clear();
				}

				synchronized (deadBalls) {
					for (Ball b : deadBalls) {
						this.balls.remove(b);
						this.detachChild(b);
					}
				}
			}
		}
	}

	// Board Piece mutators
	public Piece getPiece(int row, int col) {
		if (row < 0 || col < 0 || col >= layer.getTileColumns()
				|| row >= layer.getTileRows())
			return null;
		TMXTile tile = layer.getTMXTile(col, row);
		return Piece.fromTileId(tile.getGlobalTileID());
	}

	public void setPiece(int row, int col, Piece piece) {

		TMXTile tile = new TMXTile(piece.tileId, col, row, map.getTileWidth(),
				map.getTileHeight(),
				map.getTextureRegionFromGlobalTileID(piece.tileId));

		layer.getTMXTiles()[row][col] = tile;

		layer.setIndex(col + row * layer.getTileColumns());
		layer.drawWithoutChecks(tile.getTextureRegion(), tile.getTileWidth()
				* col, tile.getTileHeight() * row, tile.getTileWidth(),
				tile.getTileHeight(), Color.WHITE_ABGR_PACKED_FLOAT);

		layer.submit();
	}
	
	
	//Board actions
	public void pickupSplitter(int row, int col) {
		Debug.d("PickupSplitter(" + row + ", " + col +") = " + getPiece(row, col));
		if(getPiece(row, col) != Piece.MOVABLE_SPLITTER)
			return;
		this.gameResource.splitters().increment();
		this.setPiece(row, col, Piece.EMPTY);
	}
	public void placeSplitter(int row, int col) {
		if(gameResource.splitters().isZero())
			return;
		
		gameResource.splitters().decrement();
		this.setPiece(row, col, Piece.MOVABLE_SPLITTER);
	}
	public void removeTarget(int row, int col) {
		if(getPiece(row, col) != Piece.TARGET)
			return;
		this.setPiece(row, col, Piece.EMPTY);
		this.gameResource.targets().decrement();
		if(this.gameResource.targets().isZero())
			gameState.onWin();
	}

	public void createBall(int row, int col, Direction direction) {
		Ball b = new Ball(this, layer.getScaleX(), row, col, direction);
		synchronized (waitingBalls) {
			this.waitingBalls.add(b);
			this.attachChild(b);
		}
	}

	public void removeBall(Ball b) {
		synchronized (deadBalls) {
			// Log.e("MAIN", "removeBall has deadBalls lock");
			deadBalls.add(b);
			// Log.e("MAIN", "removeBall releasing deadBalls lock");
		}
		this.detachChild(b);
	}

	public void removeBox(int row, int col) {
		if (getPiece(row, col) != Piece.BOX)
			return;
		this.gameResource.boxes().decrement();
		setPiece(row, col, Piece.EMPTY);
	}
}