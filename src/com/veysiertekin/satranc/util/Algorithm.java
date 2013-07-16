package com.veysiertekin.satranc.util;

import java.util.List;

import com.veysiertekin.satranc.board.Board;
import com.veysiertekin.satranc.board.tiles.Tile;

/**
 * 
 * @author veysiertekin
 */
public class Algorithm {
	// private static Logger logger = Logger.getLogger(Board.class.getName());

	public static int[] findSolution(int x, int y, Tile tile, int[] result, int size, int depth) {
		tile.setPosition(x, y);
		result[depth] = Board.encodeLocation(x, y, (int) Math.sqrt(result.length));

		List<int[]> list = tile.findAvailablePozitions(result, size, depth);

		if (result[result.length - 1] != -1) {
			return result;
		}

		int i, j;
		for (int[] point : list) {
			i = point[0];
			j = point[1];

			int[] resultTMP = new int[result.length];
			System.arraycopy(result, 0, resultTMP, 0, result.length);

			resultTMP = findSolution(i, j, tile, resultTMP, size, depth + 1);

			if (resultTMP[resultTMP.length - 1] != -1) {
				return resultTMP;
			}
		}

		return result;
	}

	// public static void main(String[] args) throws CloneNotSupportedException, InterruptedException {
	// {
	// int size=8;
	//
	// TileState[][] boardState = Board.getEmptyTileStates(size);
	//
	// int[] resultTMP = Board.generateResultTemplate(boardState.length);
	// Tile tile = new Knight().setPosition(0, 0);
	// tile.isHeuristicActive=true;
	//
	// long start, end;
	// start = System.currentTimeMillis();
	// int[] result = Algorithm.findSolution(tile.getX(), tile.getY(), tile, resultTMP, size, 0);
	// end = System.currentTimeMillis();
	//
	// logger.info(Arrays.toString(result));
	// logger.info("Time Estimation: "+(end-start)+" millisecond(s)");
	//
	// Board.getPositionalBoardState(tile, result, size*size-1);
	// }
	//
	// System.exit(0);
	// }
}
