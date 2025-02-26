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
import java.io.IOException;
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
		String inputFileName = "test8x8_1.txt";
		String inputFileFolder = "testingInputs";
		String outputFileName = "test8x8_1_Solver_attempt_1.txt";
		String outputFileFolder = "testingOutputFiles";
		String inputFile = inputFileFolder + "/" + inputFileName;
		String outputFile = outputFileFolder + "/" + outputFileName;
		Solver solvee = new Solver();
		solvee.readInput(inputFile);
		solvee.calcInitialViolationCount();
		solvee.generateInitialSol();
		solvee.printGrid();
		solvee.printCurOutput();
		solvee.curOutputToFile(outputFile);
		
		Verifier very = new Verifier(inputFile, outputFile);
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
			int violationChange = calcViolationChange(changeCell, tree);
			this.curViolationCount += violationChange;
			System.out.println(violationChange);
			adjustCell(changeCell, tree);
		}
	}
	
	public void calcInitialViolationCount() {
		int violationCount = 0;
		for (int row = 0; row < this.rows; row++) {
			violationCount += this.rowTents[row];
		}
		for (int col = 0; col < this.cols; col++) {
			violationCount += this.colTents[col];
		}
		violationCount += this.gameGrid.getTrees().size();
		this.curViolationCount = violationCount;
	}

	/*
	 * Updates cell to go from empty to tent and vice versa, and updates adj tree to be pair and vice versa
	 * NOTE: Assumes that caller will not pass in a combination that will cause an invalid output,
	 * -such as: 
	 * - providing a tree that is already paired with another tent
	 * - providing a tree to changeCell or tent to pairTree
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
			this.gameGrid.updateCell(changeCell, '^');
			this.curRowTents[changeCell.getRow()]--;
			this.curColTents[changeCell.getCol()]--;
			if (pairTree != null) {//Case 2
				this.treeTentMap.put(pairTree, changeCell);
				this.tentTreeMap.put(changeCell, pairTree);
			}
		} else if(this.tentTreeMap.get(changeCell) == pairTree || pairTree == null){// Cases 3
			this.gameGrid.updateCell(changeCell, '.');
			this.curRowTents[changeCell.getRow()]++;
			this.curColTents[changeCell.getCol()]++;
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
	 * NOTE: Assumes that caller will not pass in a combination that will cause an invalid output,
	 * -such as: 
	 * - providing a tree that is already paired with another tent
	 * - providing a tree to changeCell or tent to pairTree
	 * 
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
	public int calcViolationChange(Cell changeCell, Cell pairTree) {
		int violationChange = 0;
		int row = changeCell.getRow();
		int col = changeCell.getCol();
		//Check if the cell to change is an empty cell
		if (changeCell.getSymbol() == '.') {//Case 1
			violationChange += (Math.abs(this.curRowTents[row] - 1) - Math.abs(this.curRowTents[row]));//update Violations from rowCount
			violationChange += (Math.abs(this.curColTents[col] - 1) - Math.abs(this.curColTents[col]));//update Violations from colCount
			if (pairTree != null) {//Check if there is a tree to be paired with then reduce violation, Case 2
				violationChange--;
			} else {//else increase violation, Case 1
				violationChange++;
			}
			if (this.gameGrid.isAdjTent(changeCell)) {//check if there is at least one adj tent, either case
				violationChange++;
				violationChange += this.gameGrid.checkAdjAliens(changeCell, false);
			}
			//else if the cell is a tree
		} else if(this.tentTreeMap.get(changeCell) == pairTree || pairTree == null){// Cases 3
			violationChange += (Math.abs(this.curRowTents[row] + 1) - Math.abs(this.curRowTents[row]));//update Violations from rowCount
			violationChange += (Math.abs(this.curColTents[col] + 1) - Math.abs(this.curColTents[col]));//update Violations from colCount
			if (this.tentTreeMap.get(changeCell) == pairTree) {//Case 4
				violationChange++;
			}else {//Case 3
				violationChange--;
			}
			if (this.gameGrid.isAdjTent(changeCell)) {//check if there is at least one adj tent, either case
				violationChange--;
				violationChange -= this.gameGrid.checkAdjAliens(changeCell, true);
			}
		} //Cases 5 and 6 have no change to violation count
		return violationChange;
	}
	
	public void printGrid() {
		System.out.println(this.rows + " " + this.cols);
		System.out.println(this.curViolationCount);
		System.out.println(this.gameGrid.getTents().size());
		for (int row = 0; row < this.rows; row++) {
			System.out.print(rowTents[row] + " ");
		}
		System.out.println();
		for (int col = 0; col < this.cols; col++) {
			System.out.print(colTents[col] + " ");
		}
		System.out.println();
		// print out generated grid
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				String symbol = this.gameGrid.getCell(row, col).toString();
				System.out.print(symbol);
			}
			System.out.println();
		}
	}
	
	public void printCurOutput() {
		System.out.println(this.curViolationCount);
		System.out.println(this.gameGrid.getTents().size());
		for (Map.Entry<Cell, Cell> pair : tentTreeMap.entrySet()) {
			int rowDiff = pair.getKey().getRow()-pair.getValue().getRow();
			int colDiff = pair.getKey().getCol()-pair.getValue().getCol();
			String treeDir = "";
			if(rowDiff == 1) {
				treeDir = "U";
			}else if(rowDiff == -1) {
				treeDir = "D";
			}else if(colDiff == 1) {
				treeDir = "L";
			}else if(colDiff == -1) {
				treeDir = "R";
			}
			System.out.println((pair.getKey().getRow()+1) + " " + (pair.getKey().getCol()+1) + " " + treeDir);
		}
	}

	public void curOutputToFile(String outputFile) {
		try (FileWriter writer = new FileWriter("data/" + outputFile)) {
			writer.write(this.curViolationCount + "\n");
			writer.write(this.gameGrid.getTents().size() + "\n");
			int linesToWrite = tentTreeMap.entrySet().size();
			Map.Entry<Cell, Cell> finalPair = null;
			for (Map.Entry<Cell, Cell> pair : tentTreeMap.entrySet()) {
				if(linesToWrite == 1) {
					finalPair = pair;
					break;
				}
				int rowDiff = pair.getKey().getRow()-pair.getValue().getRow();
				int colDiff = pair.getKey().getCol()-pair.getValue().getCol();
				String treeDir = "";
				if(rowDiff == 1) {
					treeDir = "U";
				}else if(rowDiff == -1) {
					treeDir = "D";
				}else if(colDiff == 1) {
					treeDir = "L";
				}else if(colDiff == -1) {
					treeDir = "R";
				}
				writer.write((pair.getKey().getRow()+1) + " " + (pair.getKey().getCol()+1) + " " + treeDir + "\n");
				linesToWrite--;
			}
			int rowDiff = finalPair.getKey().getRow()-finalPair.getValue().getRow();
			int colDiff = finalPair.getKey().getCol()-finalPair.getValue().getCol();
			String treeDir = "";
			if(rowDiff == 1) {
				treeDir = "U";
			}else if(rowDiff == -1) {
				treeDir = "D";
			}else if(colDiff == 1) {
				treeDir = "L";
			}else if(colDiff == -1) {
				treeDir = "R";
			}
			writer.write((finalPair.getKey().getRow()+1) + " " + (finalPair.getKey().getCol()+1) + " " + treeDir);
		} catch (IOException e) {
			System.out.println("failed to output to file, msg- " + e.getMessage());
		}
	}
	
	public void readInput(String inputFile) {
		try (Scanner sc = new Scanner(new File("data/" + inputFile))) {
			this.rows = Integer.parseInt(sc.next());
			this.cols = Integer.parseInt(sc.next());

			this.rowTents = new int[this.rows];
			this.colTents = new int[this.cols];
			this.curRowTents = new int[this.rows];
			this.curColTents = new int[this.cols];
			this.gameGrid = new GameGrid(this.rows, this.cols);

			for (int row = 0; row < this.rows; row++) {
				this.rowTents[row] = sc.nextInt();
				this.curRowTents[row] = this.rowTents[row];
			}
			for (int col = 0; col < this.cols; col++) {
				this.colTents[col] = sc.nextInt();
				this.curColTents[col] = this.colTents[col];
			}
			sc.nextLine();
			for (int row = 0; row < this.rows; row++) {
				String line = sc.nextLine();
				System.out.println(line);
				for (int col = 0; col < this.cols; col++) {
					this.gameGrid.updateCell(row, col, line.charAt(col));
				}
			}
			System.out.println("Input read successfully");
			this.printGrid();
		} catch (IOException e) {
			System.out.println("failed to read from file, msg- " + e.getMessage());
		}
	}

	public void outputToFile(String outputFile) {
		try (FileWriter writer = new FileWriter("data/" + outputFile)) {
			writer.write(this.solViolationCount + "\n");
			writer.write(this.solTentsPlaced + "\n");
			int linesToWrite = solPairings.entrySet().size();
			Map.Entry<Cell, Cell> finalPair = null;
			for (Map.Entry<Cell, Cell> pair : solPairings.entrySet()) {
				if(linesToWrite == 1) {
					finalPair = pair;
					break;
				}
				int rowDiff = pair.getKey().getRow()-pair.getValue().getRow();
				int colDiff = pair.getKey().getCol()-pair.getValue().getCol();
				String treeDir = "";
				if(rowDiff == 1) {
					treeDir = "U";
				}else if(rowDiff == -1) {
					treeDir = "D";
				}else if(colDiff == 1) {
					treeDir = "L";
				}else if(colDiff == -1) {
					treeDir = "R";
				}
				writer.write((pair.getKey().getRow()+1) + " " + (pair.getKey().getCol()+1) + " " + treeDir + "\n");
			}
			int rowDiff = finalPair.getKey().getRow()-finalPair.getValue().getRow();
			int colDiff = finalPair.getKey().getCol()-finalPair.getValue().getCol();
			String treeDir = "";
			if(rowDiff == 1) {
				treeDir = "U";
			}else if(rowDiff == -1) {
				treeDir = "D";
			}else if(colDiff == 1) {
				treeDir = "L";
			}else if(colDiff == -1) {
				treeDir = "R";
			}
			writer.write((finalPair.getKey().getRow()+1) + " " + (finalPair.getKey().getCol()+1) + " " + treeDir);
		} catch (IOException e) {
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
