package main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameGrid {
	private int rows;
	private int cols;
	private Cell[][] grid;
	private Set<Cell> trees;
	private Set<Cell> tents;

	public GameGrid(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		this.grid = new Cell[rows][cols];
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				grid[row][col] = new Cell(row, col);
			}
		}
		this.trees = new HashSet<>();
		this.tents = new HashSet<>();
		this.initializeAdjLists();
	}

	private void initializeAdjLists() {
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				if (row - 1 > 0) {
					this.grid[row][col].updateCardinalAdjList(this.grid[row - 1][col]);
					this.grid[row - 1][col].updateCardinalAdjList(this.grid[row][col]);
				}
				if (col - 1 > 0) {
					this.grid[row][col].updateCardinalAdjList(this.grid[row][col - 1]);
					this.grid[row][col - 1].updateCardinalAdjList(this.grid[row][col]);
				}
				if (row - 1 > 0 && col - 1 > 0) {
					this.grid[row][col].updateDiagAdjList(this.grid[row - 1][col - 1]);
					this.grid[row - 1][col - 1].updateDiagAdjList(this.grid[row][col]);
				}
				if (row + 1 < rows && col - 1 > 0) {
					this.grid[row][col].updateDiagAdjList(this.grid[row + 1][col - 1]);
					this.grid[row + 1][col - 1].updateDiagAdjList(this.grid[row][col]);
				}
			}
		}
	}

	// Check whether each cardinally adj cell is a tent, and whether they are adj to
	// any tents
	public List<Cell> calculateTentTargets(Cell tree) {
		boolean validCell;
		List<Cell> targets = new ArrayList<>();
		for (Cell cardinalCell : tree.getCardinalAdjList()) {
			validCell = true;
			if (cardinalCell.isTent()) {
				continue;
			} else {
				for (Cell adjCell : cardinalCell.getCardinalAdjList()) {
					if (adjCell.isTent()) {
						validCell = false;
						break;
					}
				}
				for (Cell adjCell : cardinalCell.getDiagAdjList()) {
					if (adjCell.isTent()) {
						validCell = false;
						break;
					}
				}
			}
			if (validCell) {
				targets.add(cardinalCell);
			}
		}
		return targets;
	}

	public void updateCell(Cell cell, char symbol) {
		switch (symbol) {
		case '.':
			if (cell.isTree()) {
				this.trees.remove(cell);
			} else if (cell.isTent()) {
				this.tents.remove(cell);
			}
			break;
		case 'T':
			if (cell.isTent()) {
				this.tents.remove(cell);
			}
			this.trees.add(cell);
			break;
		case '^':
			if (cell.isTree()) {
				this.trees.remove(cell);
			}
			this.tents.add(cell);
			break;
		}
		cell.setSymbol(symbol);
	}

	public Set<Cell> getTrees() {
		return this.trees;
	}

	public Set<Cell> getTents() {
		return this.tents;
	}

	public Cell getCell(int row, int col) {
		return this.grid[row][col];
	}

}
