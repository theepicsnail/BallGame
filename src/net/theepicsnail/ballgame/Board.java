package net.theepicsnail.ballgame;

import java.util.ArrayList;

import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.Entity;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.TMXTiledMapProperty;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import android.util.Log;

public class Board extends Entity implements ITouchArea {
	GameStateManager gameManager;

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

	private ArrayList<Ball> balls = new ArrayList<Ball>();
	private ArrayList<Ball> waitingBalls = new ArrayList<Ball>();
	private ArrayList<Ball> deadBalls = new ArrayList<Ball>();

	private TMXTiledMap map;
	private TMXLayer layer;
	private int splitters = 0;

	public Board(float size, TMXTiledMap map, GameStateManager manager) {
		this.map = map;
		this.gameManager = manager;
		for (TMXTiledMapProperty prop : map.getTMXTiledMapProperties()) {
			if (prop.getName().equals("splitters"))
				splitters = Integer.parseInt(prop.getValue());
		}

		layer = map.getTMXLayers().get(0);
		layer.setScaleCenter(0, 0);
		layer.setScale((float) (size)
				/ (map.getTileColumns() * map.getTileWidth()));
		attachChild(layer);
	}

	public float getScale() {
		return layer.getScaleX();
	}

	public void createBall(int row, int col, Direction direction) {
		Ball b = new Ball(this, row, col, direction);
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

	public void addSplitter() {
		this.splitters++;
	}

	public boolean removeSplitter() {
		if (this.splitters == 0)
			return false;
		this.splitters--;
		return true;
	}

	public void removeTarget() {
		gameManager.onWin();
	}
}
