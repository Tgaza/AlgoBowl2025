/**
 * "NullPntrException"
 * @author Ty Gazaway
 * @author John Silva 
 * @author Thomas Dowd
 * 
 */
package main;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

/**
 * -TODO: Create new Offical Folder to pull solutions/Inputfiles from 
 * -TODO: Ensure one indexed output !!!
 * 
 * VVV Output requirments VVV 
 * -TODO: Incorect num of violations <- INVALID
 * -TODO: Tent Superposition (Tent cannot overlap with existing entity)<-INVALID 
 * -TODO: Cant Fall off edge of world <- INVALID 
 * -TODO: Tent cannot be paired with non-tree entity <-INVALID 
 * -TODO: File must have proper formatting + No missing/corrupt data <- INVALID
 * 
 * 
 * -TODO: Cant be an unpaired entity <- Violation
 * -TODO: Multiple adjenecies != multiple violations (Specifially with Tents) <- Violation 
 * -TODO: A row or column which has too many or too few tents causes multiple violations: one
 * violation for each tent to many or too few<- Violation
 * 
 * VVV Solving ideas/methods VVV
 * MUST BE 1 Indexed;
 * (ROW,COL)
 * When complete, "todo" -> "fixme"
 */
public class Solver {
	

	// Grid attributes
	private int rows;
	private int cols;
	private int[] rowTents;
	private int[] colTents;
	private GameGrid gameGrid;

	// Solving attributes
	private double temperature;
	private double coolingRate; 
	private int violationChange;
	private int curViolationCount;
	private int bestViolationCount;
	
	// These two maps should always be an exact inverse of each other
	// If one is updated the other is updated to match
	private Map<Cell, Cell> treeTentMap; // tree to tent
	private Map<Cell, Cell> tentTreeMap; // tent to tree
	private Set<Cell> adjTentViols;
	private Set<Cell> availableCells;
	
	private int[] curRowTents;
	private int[] curColTents;
	
	// Solution attributes
	private Map<Cell, Cell> solPairings; // tent to tree
	private int solViolationCount;
	private int solTentsPlaced;

	// Helper attributes/classes
	private Random rand = new Random();

	public static void main(String[] args) {

	}

	public Solver() {
		this.rows = -1;
		this.cols = -1;
		this.rowTents = null;
		this.colTents = null;
		this.gameGrid = null;
		this.temperature = 100;
		this.coolingRate = 1;
		this.treeTentMap = new HashMap<Cell, Cell>();
		this.tentTreeMap = new HashMap<Cell, Cell>();
		this.adjTentViols = new HashSet<Cell>();
		this.curViolationCount = 0;
		this.curRowTents = null;
		this.curColTents = null;
		this.solPairings = new HashMap<Cell, Cell>();
		this.solViolationCount = -1;
		this.solTentsPlaced = 0;

	}

	public int solve() {
		return -1;
	}
	
	
	
	public void anneal() {

	}

	public void generateInitialSol() {
		for (Cell tree : this.gameGrid.getTrees()) {
			tree.trimTrees();
			ArrayList<Cell> adjCells = (ArrayList<Cell>) tree.getCardinalAdjList();
			int cellToAdjust = this.rand.nextInt(adjCells.size());
			Cell changeCell = adjCells.get(cellToAdjust);
			if (changeCell.getSymbol() == '.') {
				adjustCell(changeCell, tree);
			} else {
				
			}
		}
	}

	/*
	 * Updates cell to go from empty to tent and vice versa, and updates adj tree to be pair and vice versa
	 * 
	 * Case 1: Empty Cell and no tree to pair
	 * 	Result: Place tent with no pairing
	 * Case 2: Empty Cell with tree to pair
	 * 	Result: Place tent with paired tent
	 * Case 3: tentCell with no paired tree, and pairTree is null
	 * 	Result: remove tent
	 * Case 4: tentCell with pairTree as it's current paired Tree
	 * 	Result: remove tent and it's pairing
	 * Case 5: tentCell with pairTree as a tree it is not paired with
	 * 	Result: switch tent's pairing to new tree
	 * Case 6: tentCell with no paired tree, and pairTree is not null
	 * 	Result: switch tent's pairing to new tree
	*/
	public void adjustCell(Cell changeCell, Cell pairTree) {
		if (changeCell.getSymbol() == '.') {//Case 1
			changeCell.setSymbol('^');
			if (pairTree != null) {//Case 2
				this.treeTentMap.put(pairTree, changeCell);
				this.tentTreeMap.put(changeCell, pairTree);
			}
		} else if(this.tentTreeMap.get(changeCell) == pairTree || pairTree == null){// Cases 3
			changeCell.setSymbol('.');
			if (this.tentTreeMap.get(changeCell) == pairTree) {//Case 4
				this.treeTentMap.remove(pairTree, changeCell);
				this.tentTreeMap.remove(changeCell, pairTree);
			}
		} else {// Cases 5/6
			if(this.tentTreeMap.containsKey(changeCell)){//Case 5
				this.treeTentMap.remove(this.tentTreeMap.get(changeCell), changeCell);
				this.tentTreeMap.remove(changeCell);
				this.treeTentMap.put(pairTree, changeCell);
				this.tentTreeMap.put(changeCell, pairTree);
			}else {// Case 6
				this.treeTentMap.put(pairTree, changeCell);
				this.tentTreeMap.put(changeCell, pairTree);
			}
		}
		
		// FIXME:? May need to reset "fittness" (currentViolations)
		
	}

	/*Calculates the violation count change if a cell is to be adjusted and it's pairing should it have one
	* Case 1: Empty Cell and no tree to pair
	 * 	Result: Place tent with no pairing
	 * Case 2: Empty Cell with tree to pair
	 * 	Result: Place tent with paired tent
	 * Case 3: tentCell with no paired tree, and pairTree is null
	 * 	Result: remove tent
	 * Case 4: tentCell with pairTree as it's current paired Tree
	 * 	Result: remove tent and it's pairing
	 * Case 5: tentCell with pairTree as a tree it is not paired with
	 * 	Result: switch tent's pairing to new tree
	 * Case 6: tentCell with no paired tree, and pairTree is not null
	 * 	Result: switch tent's pairing to new tree
	*/
	public int calcViolationChange(Cell changeCell, Cell pairTree) {
		int violationChange = 0;
		int row = changeCell.getRow();
		int col = changeCell.getCol();
		//Check if the cell to change is an empty cell
		if (changeCell.getSymbol() == '.') {//Case 1
			violationChange += Math.abs(this.curRowTents[row] - (this.curRowTents[row] - 1));//update Violations from rowCount
			violationChange += Math.abs(this.curColTents[col] - (this.curColTents[col] - 1));//update Violations from colCount
			if (pairTree != null) {//Check if there is a tree to be paired with then reduce violation, Case 2
				violationChange--;
			} else {//else increase violation, Case 1
				violationChange++;
			}
			if (this.gameGrid.isAdjTent(changeCell)) {//check if there is at least one adj tent, either case
				violationChange++;
			}
			//else if the cell is a tree
		} else if(this.tentTreeMap.get(changeCell) == pairTree || pairTree == null){// Cases 3
			violationChange += Math.abs(this.curRowTents[row] - (this.curRowTents[row] + 1));//update Violations from rowCount
			violationChange += Math.abs(this.curColTents[col] - (this.curColTents[col] + 1));//update Violations from colCount
			if (this.tentTreeMap.get(changeCell) == pairTree) {//Case 4
				violationChange++;
			}else {//Case 3
				violationChange--;
			}
			if (this.gameGrid.isAdjTent(changeCell)) {//check if there is at least one adj tent, either case
				violationChange--;
			}
		} //Cases 5 and 6 have no change to violation count
		return violationChange;
	}

	public void readInput(String fileName) {
		try (Scanner sc = new Scanner(new File("data/testingOutputs/" + fileName))) {
			this.rows = Integer.parseInt(sc.next());
			this.cols = Integer.parseInt(sc.next());

			this.rowTents = new int[this.rows];
			this.colTents = new int[this.cols];
			this.curRowTents = new int[this.rows];
			this.curColTents = new int[this.cols];
			this.gameGrid = new GameGrid(this.rows, this.cols);

			for (int row = 0; row < this.rows; row++) {
				this.rowTents[row] = sc.nextInt();
			}
			for (int col = 0; col < this.cols; col++) {
				this.colTents[col] = sc.nextInt();
			}

			for (int row = 0; row < this.rows; row++) {
				String line = sc.nextLine();
				for (int col = 0; col < this.cols; col++) {
					this.gameGrid.updateCell(row, col, line.charAt(col));
					//					if (this.gameGrid.getCell(row, col).getSymbol() == '.') {
					//						this.availableCells.add(this.gameGrid.getCell(row, col));
					//					}
				}
			}
			System.out.println("Input read successfully");
		} catch (Exception e) {
			System.out.println("failed to read from file, msg- " + e.getMessage());
		}
	}

	public void outputToFile(String fileName) {
		try (FileWriter writer = new FileWriter("data/generatedInputsUnverified/" + fileName)) {
			writer.write(this.solViolationCount);
			writer.write(this.solTentsPlaced);
			for (Map.Entry<Cell, Cell> pair : solPairings.entrySet()) {
				writer.write(pair.getKey().getRow() + " " + pair.getKey().getCol());
			}
		} catch (Exception e) {
			System.out.println("failed to output to file, msg- " + e.getMessage());
		}
	}
	
	// ~ ~ ~ Utilites ~ ~ ~ //
	
	/**
	 * Should be used as follows:
	 * 	if ( acceptanceProb (~,~,~) > RandomNum ) 
	 * FOR HOT STUFF
	 * 
	 * @param currentViolations
	 * @param newViolations
	 * @param temp
	 * @return
	 */
	public static double acceptanceProb(int currentViolations, int newViolations, double temp) {
		// Accept New Solution
		if (newViolations < currentViolations) {
			return 1.0;
		}
		
		if (temp <= 0.0) {
			return 0.0; // Stop, shit is too cold
		}
		
		// Reject new solution and calculate acceptance probability.
		// DO NOT ADJUST HERE, MAKE ADJUSTMENTS ELSEWHERE
		return Math.exp( (currentViolations - newViolations) / temp);
	}
	
	@Override
	public boolean equals(Object obj) {
		// FIXME: Could need to be fixed depending on implementation
		return super.equals(obj);
	}
	
}
