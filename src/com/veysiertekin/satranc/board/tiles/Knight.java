package com.veysiertekin.satranc.board.tiles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;

import com.veysiertekin.satranc.Start;
import com.veysiertekin.satranc.board.Board;

/**
 * 
 * @author veysiertekin
 */
public class Knight extends Tile {
	private Comparator<int[]> comparatorHeuristic = new Comparator<int[]>() {
		/**
		 * Warnsdorff algorithm
		 * 
		 * @param o1
		 * @param o2
		 * @return
		 */
		@Override
		public int compare(int[] o1, int[] o2) {
			int x = getX();
			int y = getY();

			setX(o1[0]);
			setY(o1[1]);
			result[depth + 1] = Board.encodeLocation(o1[0], o1[1], size);
			int a = getAvailablePoints(result, size).size();

			setX(o2[0]);
			setY(o2[1]);
			result[depth + 1] = Board.encodeLocation(o2[0], o2[1], size);
			int b = getAvailablePoints(result, size).size();

			setX(x);
			setY(y);
			result[depth + 1] = -1;

			return a == b ? 0 : (a < b ? -1 : 1);
		}
	};
	int depth;
	int[] result;

	int size;

	public Knight() {
		try {
			setIcon(ImageIO.read(Start.class.getResource("img/png/at.png")));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<int[]> findAvailablePozitions(int[] result, int size, int depth) {
		this.result = result;
		this.depth = depth;
		this.size = size;

		List<int[]> resultPoints = getAvailablePoints(result, size);

		if (isHeuristicActive)
			Collections.sort(resultPoints, comparatorHeuristic);
		return resultPoints;
	}

	public List<int[]> getAvailablePoints(int[] result, int size) {
		List<int[]> resultPoints = new ArrayList<int[]>();

		int x = getX();
		int y = getY();

		int tmpX;
		int tmpY;

		if ((tmpX = x - 2) >= 0 && (tmpY = y - 1) >= 0) {
			setResultPoints(resultPoints, result, size, tmpX, tmpY);
		}
		if ((tmpX = x - 1) >= 0 && (tmpY = y - 2) >= 0) {
			setResultPoints(resultPoints, result, size, tmpX, tmpY);
		}
		if ((tmpX = x + 1) >= 0 && (tmpY = y - 2) >= 0) {
			setResultPoints(resultPoints, result, size, tmpX, tmpY);
		}
		if ((tmpX = x + 2) >= 0 && (tmpY = y - 1) >= 0) {
			setResultPoints(resultPoints, result, size, tmpX, tmpY);
		}
		if ((tmpX = x + 2) >= 0 && (tmpY = y + 1) >= 0) {
			setResultPoints(resultPoints, result, size, tmpX, tmpY);
		}
		if ((tmpX = x + 1) >= 0 && (tmpY = y + 2) >= 0) {
			setResultPoints(resultPoints, result, size, tmpX, tmpY);
		}
		if ((tmpX = x - 1) >= 0 && (tmpY = y + 2) >= 0) {
			setResultPoints(resultPoints, result, size, tmpX, tmpY);
		}
		if ((tmpX = x - 2) >= 0 && (tmpY = y + 1) >= 0) {
			setResultPoints(resultPoints, result, size, tmpX, tmpY);
		}

		return resultPoints;
	}

	public void setResultPoints(List<int[]> resultPoints, int[] result, int size, int tmpX, final int tmpY) {
		for (int index = 0; index < result.length; index++) {
			if (Board.encodeLocation(tmpX, tmpY, size) == result[index])
				return;
		}
		if ((tmpX < size && tmpY < size)) {
			resultPoints.add(new int[] { tmpX, tmpY });
		}
	}
}
