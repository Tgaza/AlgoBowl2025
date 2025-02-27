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
	
	//this was added to track the tent(s) associated with tree(s) and vice versa
	private List<Cell> pairedCells;
	
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
		this.pairedCells = new ArrayList<>();
	}
	
	//copy constructor
	public Cell(Cell other) {
	    this.row = other.row;
	    this.col = other.col;
	    this.symbol = other.symbol;
	    this.isTree = other.isTree;
	    this.isTent = other.isTent;
	    
	    // Copy the adjacency lists
	    this.diagAdjList = new ArrayList<>(other.diagAdjList);
	    this.cardinalAdjList = new ArrayList<>(other.cardinalAdjList);
	    
	    // Copy the paired cells list
	    this.pairedCells = new ArrayList<>(other.pairedCells);
	}

	
	@Override
	public String toString() {
		return "" + this.symbol;
	}

	public void updateCardinalAdjList(Cell adjCell) {
		this.cardinalAdjList.add(adjCell);
	}

	public void updateDiagAdjList(Cell adjCell) {
		this.diagAdjList.add(adjCell);
	}

	public List<Cell> getDiagAdjList() {
		return this.diagAdjList;
	}

	public List<Cell> getCardinalAdjList() {
		return this.cardinalAdjList;
	}
	
	public void addPairedCell(Cell pairCell) {
		this.pairedCells.add(pairCell);
	}
	
	public List<Cell> getPairedCells() {
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
}
