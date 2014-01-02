package net.theepicsnail.ballgame;

import org.andengine.entity.Entity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.font.FontManager;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.HorizontalAlign;

import android.graphics.Typeface;

public class GameStatusDisplay extends Entity {
	private int shots, splitters, boxes, targets;
	private Sprite shots_sprite;
	private Sprite splitters_sprite;
	private Sprite boxes_sprite;
	private Sprite targets_sprite;
	private Text shots_text;
	
	public GameStatusDisplay(int shots, int splitters, int boxes, int targets, 
			VertexBufferObjectManager manager, FontManager fontM, TextureManager textM,  TMXTiledMap map) {
		
		this.shots = shots;
		this.splitters = splitters;
		this.boxes = boxes;
		this.targets = targets;

		Font mFont = FontFactory.create(fontM,
				textM, 256, 256,
				Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 64);
		mFont.load();
		
		shots_sprite = new Sprite(0, 0,
				map.getTextureRegionFromGlobalTileID(Piece.BALL.tileId),
				manager);
		attachChild(shots_sprite);
		
		shots_text = new Text(shots_sprite.getWidth(), shots_sprite.getY() + 
				shots_sprite.getHeightScaled()/2 - 
				mFont.getLineHeight()/2, 
				mFont, "" + this.shots,
				new TextOptions(HorizontalAlign.LEFT),
				manager);
		attachChild(shots_text);
		
		splitters_sprite = new Sprite(
				0,
				shots_sprite.getY() + shots_sprite.getHeight(),
				map.getTextureRegionFromGlobalTileID(Piece.MOVABLE_SPLITTER.tileId),
				manager);
		attachChild(splitters_sprite);

		boxes_sprite = new Sprite(0, splitters_sprite.getY() + splitters_sprite.getHeight(),
				map.getTextureRegionFromGlobalTileID(Piece.BOX.tileId),
				manager);
		attachChild(boxes_sprite);

		targets_sprite = new Sprite(0, boxes_sprite.getY() + boxes_sprite.getHeight(),
				map.getTextureRegionFromGlobalTileID(Piece.TARGET.tileId),
				manager);
		attachChild(targets_sprite);
	}
	
	public boolean take_shot() {
		if(this.shots == 0)
			return false;
		
		this.shots -= 1;
		return true;
	}
	
	public boolean take_splitter() {
		if(this.splitters == 0)
			return false;
		this.splitters -= 1;
		return true;
	}
	
	public void remove_box() {
		this.boxes -= 1;
	}
	
	public boolean remove_target() {
		this.targets -= 1;
		return this.targets == 0;
	}
	
	public void return_splitter() {
		this.splitters += 1;
	}
}
