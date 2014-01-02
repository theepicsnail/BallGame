package net.theepicsnail.ballgame;

import org.andengine.util.debug.Debug;

public enum Piece {

	LEFT_ARROW(1) {
		@Override
		public void interact(Board board, Ball ball) {
			ball.setDirection(Direction.LEFT);
		}

		public void click(Board board, int row, int col) {
			board.createBall(row, col, Direction.LEFT);
		}
	},
	DOWN_ARROW(2) {
		@Override
		public void interact(Board board, Ball ball) {
			ball.setDirection(Direction.DOWN);
		}

		public void click(Board board, int row, int col) {
			board.createBall(row, col, Direction.DOWN);
		}
	},

	UP_ARROW(3) {
		@Override
		public void interact(Board board, Ball ball) {
			ball.setDirection(Direction.UP);
		}

		public void click(Board board, int row, int col) {
			board.createBall(row, col, Direction.UP);
		}
	},

	RIGHT_ARROW(4) {
		@Override
		public void interact(Board board, Ball ball) {
			ball.setDirection(Direction.RIGHT);
		}

		public void click(Board board, int row, int col) {
			board.createBall(row, col, Direction.RIGHT);
		}
	},

	SPLITTER(5) {
		@Override
		public void interact(Board board, Ball ball) {
			board.createBall(ball.row, ball.col, ball.direction.toRight());
			ball.setDirection(ball.direction.toLeft());
		}

		public void click(Board board, int row, int col) {
		}

	},
	MOVABLE_SPLITTER(6) {

		public void interact(Board board, Ball ball) {
			SPLITTER.interact(board, ball);
		}

		@Override
		public void click(Board board, int row, int col) {
			board.setPiece(row, col, EMPTY);
			board.addSplitter();
		}

	},
	// blue ring 7
	// orange ring 8

	EMPTY(9) {
		@Override
		public void interact(Board board, Ball ball) {
		}

		public void click(Board board, int row, int col) {
			if (board.removeSplitter()) {
				board.setPiece(row, col, MOVABLE_SPLITTER);
			}
		}
	},
	BALL(10) {
		public void interact(Board board, Ball ball) {
		}

		public void click(Board board, int row, int col) {
		}
	},
	CLOCK(11) {
		public void interact(Board board, Ball ball) {
		}

		public void click(Board board, int row, int col) {
		}
	},
	TARGET(12) {

		@Override
		public void interact(Board board, Ball ball) {
			board.setPiece(ball.row, ball.col, EMPTY);
			board.removeTarget();
			board.removeBall(ball);
		}

		@Override
		public void click(Board board, int row, int col) {
			// TODO Auto-generated method stub

		}
	},

	BOX(13) {
		public void interact(Board board, Ball ball) {
			board.setPiece(ball.row, ball.col, EMPTY);
			board.removeBall(ball);
		}

		public void click(Board board, int row, int col) {
		}
	},

	BRICK(14) {
		@Override
		public void interact(Board board, Ball ball) {
			board.removeBall(ball);
		}

		@Override
		public void click(Board board, int row, int col) {
		}
	};

	public abstract void interact(Board board, Ball ball);

	public abstract void click(Board board, int row, int col);

	public final int tileId;

	private Piece(int id) {
		this.tileId = id;
	}

	private static final Piece[] tilePieceMap = new Piece[16];
	static {
		for (Piece p : Piece.values()) {
			tilePieceMap[p.tileId] = p;
		}
	}

	public static Piece fromTileId(int globalTileID) {
		return tilePieceMap[globalTileID];
	}
}