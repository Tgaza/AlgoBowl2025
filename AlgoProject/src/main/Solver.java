/**
 * "NullPntrException"
 * @author Ty Gazaway
 * @author John Silva 
 * @author Thomas Dowd
 * 
 */
package main;

import java.util.HashSet;
import java.util.Set;
/**
 * -TODO: Solver
 */
public class Solver {
	private Verifier gridToSolve;
	private Set<Cell> tents = new HashSet<>();
	
	private int rows;
	private int columns;
	
	public static void main(String[] args) {
		new Solver();
	}
	
	public Solver() {
		super();
		this.gridToSolve = new Verifier();
		
		this.gridToSolve.setInputFile("data/testingInputs/basicIn.txt");
		this.gridToSolve.buildBaseGrid();
		
		this.rows = gridToSolve.getGrid().getRows();
		this.columns = gridToSolve.getGrid().getCols();
		
		this.gridToSolve.initializeTentRowCount();
		this.gridToSolve.initializeTentColumnCount();
		
		System.out.println(rows + " " + columns);
		
		fillEmptySpacesWithTents();
	}
	
	//for debugging purposes
	private void printGrid() {
		GameGrid grid = gridToSolve.getGrid();
		
		for(int r = 0; r < rows; r++) {
			for(int c = 0; c < columns; c++) {
				System.out.print(grid.getCell(r, c).getSymbol() + " ");
			}
			System.out.println();
		}
	}
	
	private void fillEmptySpacesWithTents() {
		for(int r = 0; r < rows; r++) {
			for(int c = 0; c < columns; c++) {
				
				Cell currCell = gridToSolve.getGrid().getCell(r, c);
				if(currCell.getSymbol() == '.') {
					gridToSolve.getGrid().getCell(r, c).setSymbol('^');
					gridToSolve.increaseTentRowCount(r);
					gridToSolve.increaseTentColumnCount(c);
				}
			}
		}
		
		//for debugging
		printGrid();
		System.out.println(gridToSolve.sumViolations());
	}
}
