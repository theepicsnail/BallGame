package net.theepicsnail.ballgame;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

public class Ball extends Sprite {

	public static ITextureRegion BALL_TEXTURE;
	public static VertexBufferObjectManager VERTEX_MANAGER;
	private Board board;
	public Direction direction;
	public int row, col;

	public Ball(Board board, float scale, int row, int col, Direction direction) {
		super(0,0, BALL_TEXTURE, VERTEX_MANAGER);
		this.setScaleCenter(0, 0);
		this.setScale(scale/2);
		this.board = board;
		this.direction = direction;
		this.row = row;
		this.col = col;
		updateLocation(0);
	}

	public void updateLocation(float progress) {
		//Middle of the cell + whatever distance has been travled
		float[] pos = {
				col + progress * direction.dx +.5f ,
				row + progress * direction.dy +.5f 
		};
		
		//Turn it into position cords
		board.convertColRowToPosition(pos);
		
		//Center the ball on that position
		this.setPosition(
				pos[0]- getWidthScaled()/2 ,
				pos[1]- getHeightScaled() /2);
		
		
	}
	

	public void advanceCell() {
		this.row += this.direction.dy;
		this.col += this.direction.dx;
		Piece piece = board.getPiece(this.row, this.col);

		if (piece == null)
			board.removeBall(this);
		else
			piece.interact(board, this);
	}
	
	public void setDirection(Direction d) {
		direction = d;
	}
}
