package net.theepicsnail.ballgame;

public enum Direction {
	UP(-1, 0), DOWN(1, 0), LEFT(0, -1), RIGHT(0, 1);

	public final int dy, dx;
	public final float offsetX, offsetY;
	private Direction(int dy, int dx) {
		this.dy = dy;
		this.dx = dx;
		if(dy == 0){
			this.offsetX = 0;
			this.offsetY = .5f;
		} else {
			this.offsetX = .5f;
			this.offsetY = 0;
		}
	}
	
	public Direction toLeft() {
		switch(this) {
		case UP: return LEFT;
		case LEFT: return DOWN;
		case DOWN: return RIGHT;
		case RIGHT: return UP;
		default:
			return null;
		}
	}

	
	public Direction toRight() {
		switch(this) {
		case UP: return RIGHT;
		case RIGHT: return DOWN;
		case DOWN: return LEFT;
		case LEFT: return UP;
		default:
			return null;
		}
	}
	
}