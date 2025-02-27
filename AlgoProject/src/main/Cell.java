/**
 * "NullPntrException"
 * @author Ty Gazaway
 * @author John Silva 
 * @author Thomas Dowd
 * 
 */
package main;

import java.util.ArrayList;
import java.util.List;

/**
 * Grid Node. 
 * 
 */
public class Cell {
	private int row, col;
	private char symbol;
	private boolean isTree;
	private boolean isTent;

	private ArrayList<Cell> diagAdjList;
	private ArrayList<Cell> cardinalAdjList;
	private ArrayList<Cell> treeAdjList;
	
	//this was added to track the tent(s) associated with tree(s) and vice versa
	private ArrayList<Cell> pairedCells;
	
	/**
	 * Constructor
	 * @param row
	 * @param col
	 */
	public Cell(int row, int col) {
		this.row = row;
		this.col = col;
		this.symbol = '.';
		this.isTree = false;
		this.isTent = false;
		this.diagAdjList = new ArrayList<>();
		this.cardinalAdjList = new ArrayList<>();
		this.treeAdjList = new ArrayList<>();
		this.pairedCells = new ArrayList<>();
	}
	
	@Override
	public String toString() {
		return "" + this.symbol;
	}

	public void updateCardinalAdjList(Cell adjCell) {
		this.cardinalAdjList.add(adjCell);
	}
	
	public void trimTrees() {
		for(int i = 0; i < this.cardinalAdjList.size(); i++) {
			if(this.cardinalAdjList.get(i).isTree()) {
				this.cardinalAdjList.remove(i);
				i--;
			}
		}
	}
	
	public void updateTreeAdjList() {
		for(int i = 0; i < this.cardinalAdjList.size(); i++) {
			if(this.cardinalAdjList.get(i).isTree()) {
				this.treeAdjList.add(this.cardinalAdjList.get(i));
			}
		}
	}

	public void updateDiagAdjList(Cell adjCell) {
		this.diagAdjList.add(adjCell);
	}

	public ArrayList<Cell> getDiagAdjList() {
		return this.diagAdjList;
	}

	public ArrayList<Cell> getCardinalAdjList() {
		return this.cardinalAdjList;
	}
	
	public void addPairedCell(Cell pairCell) {
		this.pairedCells.add(pairCell);
	}
	
	public ArrayList<Cell> getPairedCells() {
		return this.pairedCells;
	}

	public void setSymbol(char symbol) {
		this.symbol = symbol;
		switch(symbol) {
		case '.':
			this.isTree = false;
			this.isTent = false;
			break;
		case 'T':
			this.isTree = true;
			this.isTent = false;
			break;
		case '^':
			this.isTree = false;
			this.isTent = true;
			break;
		}
	}

	public boolean isTree() {
		return this.isTree;
	}

	public boolean isTent() {
		return this.isTent;
	}

	public int getRow() {
		return this.row;
	}

	public int getCol() {
		return this.col;
	}

	public char getSymbol() {
		return this.symbol;
	}

	public ArrayList<Cell> getTreeAdjList() {
		return treeAdjList;
	}
}
