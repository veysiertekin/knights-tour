package com.veysiertekin.satranc.board;

import com.veysiertekin.satranc.board.tiles.Tile;
import com.veysiertekin.satranc.board.tiles.TileState;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * 
 * @author veysiertekin
 */
public class Board {
	private Tile tile;

	private TileState[][] tilesStates;
	private static Board board;
	private static Logger logger = Logger.getLogger(Board.class.getName());

	public static int[] decodeLocation(int l, int size) {
		if (l < 0)
			return null;

		int x = l % size, y = l / size;

		return new int[] { x, y };
	}

	public static int encodeLocation(int x, int y, int size) {
		return x + (y * size);
	}

	public static int[] generateResultTemplate(int size) {
		int[] result = new int[size * size];
		Arrays.fill(result, -1);
		return result;
	}

	public static TileState[][] getEmptyTileStates(int size) {
		TileState[][] tileStates = new TileState[size][size];
		for (TileState[] tileState : tileStates) {
			Arrays.fill(tileState, TileState.__EMPTY__);
		}
		return tileStates;
	}

	public static Board getInstance() {
		if (board == null) {
			board = new Board(8);
		}
		return board;
	}

	public static Board getInstance(int size) {
		board = new Board(size);
		return board;
	}

	public static TileState[][] getPositionalBoardState(Tile tile, int[] result, int position) {
		int size = (int) Math.sqrt(result.length);
		int[] resultTMP = Board.generateResultTemplate(size);
		TileState[][] states = Board.getEmptyTileStates(size);

		for (int j = 0; j <= position; j++) {
			// (++++++++ Clean up board
			for (TileState[] statesTMP : states) {
				for (int i = 0; i < statesTMP.length; i++) {
					if (statesTMP[i] == TileState._CURRENT_) {
						statesTMP[i] = TileState.NOT_EMPTY;
					}
					else if (statesTMP[i] != TileState.NOT_EMPTY) {
						statesTMP[i] = TileState.__EMPTY__;
					}
				}
			}
			// Clean up board ---------)

			if (result[j] == -1)
				return null;

			resultTMP[j] = result[j];
			int[] point = decodeLocation(result[j], size);

			tile.setPosition(point[0], point[1]);
			states[point[1]][point[0]] = TileState._CURRENT_;
			List<int[]> availablePositions = tile.findAvailablePozitions(resultTMP, size, j);
			for (int[] pointTMP : availablePositions) {
				states[pointTMP[1]][pointTMP[0]] = TileState.AVAILABLE;
			}
		}

		printBoard(states);
		return states;
	}

	public static void printBoard(TileState[][] states) {
		logger.info(Arrays.deepToString(states).replace("[[", "\n").replace("]]", "\n").replaceAll("\\], \\[", "\n"));
	}

	private Board(int size) {
		setTilesStates(getEmptyTileStates(size));
	}

	public Tile getTile() {
		return tile;
	}

	public TileState[][] getTilesStates() {
		return tilesStates;
	}

	public void setTile(Tile tile) {
		this.tile = tile;
	}

	public void setTilesStates(TileState[][] tilesStates) {
		this.tilesStates = tilesStates;
	}
}
