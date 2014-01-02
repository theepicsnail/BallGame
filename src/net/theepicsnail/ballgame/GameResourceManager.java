package net.theepicsnail.ballgame;

import org.andengine.entity.Entity;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.font.FontManager;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.graphics.Typeface;

public class GameResourceManager extends Entity {
	private GameDimension shots, splitters, boxes, targets, time;

	public GameResourceManager(int nshots, int nsplitters, int nboxes,
			int ntargets, VertexBufferObjectManager manager, FontManager fontM,
			TextureManager textM, TMXTiledMap map) {

		Font font = FontFactory.create(fontM, textM, 256, 256,
				Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 64);
		font.load();

		shots = new GameDimension(font,
				map.getTextureRegionFromGlobalTileID(Piece.BALL.tileId),
				manager, nshots);
		shots.setPosition(0, 0);
		attachChild(shots);
		
		splitters = new GameDimension(font,
				map.getTextureRegionFromGlobalTileID(Piece.MOVABLE_SPLITTER.tileId),
				manager, nsplitters);
		splitters.setPosition(0, 128);
		attachChild(splitters);
		
		boxes = new GameDimension(font,
				map.getTextureRegionFromGlobalTileID(Piece.BOX.tileId), 
				manager, nboxes);
		boxes.setPosition(0, 128*2);
		attachChild(boxes);
		
		targets = new GameDimension(font,
				map.getTextureRegionFromGlobalTileID(Piece.TARGET.tileId),
				manager, ntargets);
		targets.setPosition(0, 128*3);
		attachChild(targets);
		
		time = new GameDimension(font,
				map.getTextureRegionFromGlobalTileID(Piece.CLOCK.tileId),
				manager, 0);
		time.setPosition(0, 128*4);
		attachChild(time);
	}
	
	public GameDimension shots() {
		return shots;
	}
	public GameDimension splitters() {
		return splitters;
	}
	public GameDimension boxes() {
		return boxes;
	}
	public GameDimension targets() {
		return targets;
	}
	public GameDimension time() {
		return time;
	}
}
