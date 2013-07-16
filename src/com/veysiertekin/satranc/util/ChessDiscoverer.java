package com.veysiertekin.satranc.util;

import com.veysiertekin.satranc.board.Board;

/**
 * 
 * @author veysiertekin
 */
public class ChessDiscoverer {

	private Board board;
	private int[] solution;
	private int solutionStep;

	public ChessDiscoverer() {
		initialize(8);
	}

	public ChessDiscoverer(int size) {
		initialize(size);
	}

	public boolean findSolution() {
		int[] resultTMP = Board.generateResultTemplate(board.getTilesStates().length);
		solution = Algorithm.findSolution(board.getTile().getX(), board.getTile().getY(), board.getTile(), resultTMP, board.getTilesStates().length, 0);

		return solution[solution.length - 1] != -1;
	}

	public Board getBoard() {
		return board;
	}

	public int[] getSolution() {
		return solution;
	}

	public int getSolutionStep() {
		return solutionStep;
	}

	private void initialize(int size) {
		board = Board.getInstance(size);
		setSolution(Board.generateResultTemplate(board.getTilesStates().length));
	}

	public void printBoard() {
		Board.printBoard(board.getTilesStates());
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public void setSolution(int[] solution) {
		this.solution = solution;
	}

	public void setSolutionStep(int solutionStep) {
		this.solutionStep = solutionStep;
	}
}
