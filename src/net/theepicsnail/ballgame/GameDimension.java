package net.theepicsnail.ballgame;

import org.andengine.entity.Entity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class GameDimension extends Entity {
	private int value;
	private Text counter;
	private Sprite icon;

	public GameDimension(Font font, ITextureRegion texture,
			VertexBufferObjectManager manager, int initialValue) {

		value = initialValue;

		counter = new Text(160, 64 - font.getLineHeight() / 2, font,
				Integer.toString(initialValue), 3, manager);
		icon = new Sprite(0, 0, texture, manager);

		attachChild(counter);
		attachChild(icon);

	}

	public void increment() {
		this.value += 1;
		updateText();
	}

	public void decrement() {
		this.value -= 1;
		updateText();
	}

	public boolean isZero() {
		return this.value == 0;
	}

	private void updateText() {
		counter.setText("" + this.value);
	}
}
