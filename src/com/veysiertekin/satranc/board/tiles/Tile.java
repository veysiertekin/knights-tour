package com.veysiertekin.satranc.board.tiles;

import java.awt.Image;
import java.util.List;

/**
 * 
 * @author veysiertekin
 */
public abstract class Tile {
	private Image icon;
	public boolean isHeuristicActive = false;
	private int x;
	private int y;

	public abstract List<int[]> findAvailablePozitions(int[] result, int size, int depth);

	public Image getIcon() {
		return icon;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setIcon(Image icon) {
		this.icon = icon;
	}

	public Tile setPosition(int x, int y) {
		setX(x);
		setY(y);

		return this;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}
}
