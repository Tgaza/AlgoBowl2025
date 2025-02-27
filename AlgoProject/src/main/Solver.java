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
import java.util.Collections;
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
	private int curViolationCount;
	
	// These two maps should always be an exact inverse of each other
	// If one is updated the other is updated to match
	private Map<Cell, Cell> treeTentMap; // tree to tent
	private Map<Cell, Cell> tentTreeMap; // tent to tree
	private ArrayList<Cell> availableCells;
	
	private int[] curRowTents;
	private int[] curColTents;
	
	// Solution attributes
	private String[] solPairings; // tent to tree
	private int solViolationCount;
	private int solTentsPlaced;

	// Helper attributes/classes
	private Random rand = new Random();

	public static void main(String[] args) {

//		String inputFileFolder = "AlgoBowl/testingInputs";
//		String outputFileFolder = "AlgoBowl/testingOutputFiles";
		
		
		
//	
//		for (int i = 963; i < 1025; i++) {
//			
//			try {
//				String inputFileName = "input_group" + i + ".txt";
//				String outputFileName = inputFileFolder.replace(".txt", "_Solved.txt");
//
//				String inputFile = inputFileFolder + "/" + inputFileName;
//				String outputFile = outputFileFolder + "/" + outputFileName;
//
//				//Solver solvee = new Solver();
//				//solvee.readInput(inputFile);
//				//solvee.calcInitialViolationCount();
//				//solvee.generateInitialSol();
//				//solvee.printGrid();
//				//solvee.curOutputToFile(outputFile);
//				//				
//				//Verifier very = new Verifier(inputFile, outputFile);
//
//				System.out.println("Processed File: " + inputFile + " -> " + outputFile);
//			} catch (Exception e) {
//				System.out.println("Skipped File");
//			}
//			
//		}
//	
			
			int inputGroupNum = 1001;
			String inputFileName = "input_group" + 1001 + ".txt";
			String inputFileFolder = "officialInputs";
			String outputFileName = "output_group" + 1001 + "_attempt_1.txt";
			String outputFileFolder = "testingOutputFiles";
			String inputFile = inputFileFolder + "/" + inputFileName;
			String outputFile = outputFileFolder + "/" + outputFileName;
			
			Solver solvee = new Solver();
			
			solvee.readInput(inputFile);
			solvee.calcInitialViolationCount();
			solvee.generateInitialSol();
			//solvee.printGrid();
			solvee.solve();
			solvee.outputToFile(outputFile);
			
			Verifier very = new Verifier(inputFile, outputFile);
		}
	
		

		

	public Solver() {
		this.rows = -1;
		this.cols = -1;
		this.rowTents = null;
		this.colTents = null;
		this.gameGrid = null;
		this.temperature = 100;
		this.coolingRate = 10;
		this.treeTentMap = new HashMap<Cell, Cell>();
		this.tentTreeMap = new HashMap<Cell, Cell>();
		this.availableCells = new ArrayList<Cell>();
		this.curViolationCount = 0;
		this.curRowTents = null;
		this.curColTents = null;
		this.solPairings = null;
		this.solViolationCount = -1;
		this.solTentsPlaced = 0;

	}

	public void solve() {
		while(this.temperature > 0) {
			anneal();
			if(curViolationCount < solViolationCount || solViolationCount == -1) {
				solViolationCount = curViolationCount;
				solTentsPlaced = this.gameGrid.getTents().size();
				this.updateSolutionPairings();
			}
			this.temperature -= this.coolingRate;
		}
	}
	
	public void updateSolutionPairings() {
		int linesToWrite = this.gameGrid.getTents().size();
		if(linesToWrite == 0) {
			return;
		}
		int curLine = 0;
		this.solPairings = new String[linesToWrite];
		String finalTreeDir = "";
		int finalTentRow = 0;
		int finalTentCol = 0;
		for(Cell tent: this.gameGrid.getTents()) {
			String treeDir = "X";
			if(this.tentTreeMap.containsKey(tent)) {
				Cell pairedTree = this.tentTreeMap.get(tent);
				int rowDiff = tent.getRow()-pairedTree.getRow();
				int colDiff = tent.getCol()-pairedTree.getCol();
				if(rowDiff == 1) {
					treeDir = "U";
				}else if(rowDiff == -1) {
					treeDir = "D";
				}else if(colDiff == 1) {
					treeDir = "L";
				}else if(colDiff == -1) {
					treeDir = "R";
				}
			}
			if(curLine == linesToWrite-1) {
				finalTreeDir = treeDir;
				finalTentRow = tent.getRow();
				finalTentCol = tent.getCol();
				break;
			}
			this.solPairings[curLine] = ((tent.getRow()+1) + " " + (tent.getCol()+1) + " " + treeDir + "\n");
			curLine++;
		}
		this.solPairings[curLine] = ((finalTentRow+1) + " " + (finalTentCol+1) + " " + finalTreeDir);
	}
	
	
	public void anneal() {
		int totalValidCells = this.availableCells.size();
		Cell chosenCell = this.availableCells.get(this.rand.nextInt(0, totalValidCells));
		ArrayList<Cell> availablePairings = chosenCell.getCardinalAdjList();
		int totalAvailablePairings = availablePairings.size();
		int chosenPairDecision = this.rand.nextInt(0, totalAvailablePairings);
		Cell chosenPairTree = availablePairings.get(chosenPairDecision);
		/*if chosenPairTree is a tree, then do one of the cases below
		 * Case 1: ChosenCell is a tent with no pairing, and ChosenPairTree has no pairing
		 * 	Result: pair the two together
		 * Case 2: ChosenCell is a tent with a pairing, and ChosenPairTree has no pairing
		 * 	Result: remove original pairing of ChosenCell and replace with a pairing with ChosenPairTree
		 * Case 3: ChosenCell is a tent with no pairing, and ChosenPairTree has a pairing
		 * 	Result: decouple paired tree's pairing and pair tent with pairtree
		 * Case 4: ChosenCell is a tent paired with ChosenPairTree
		 * 	Result: remove tent and unpair
		 * Case 5: ChosenCell is a tent with a pairing, and ChosenPairTree has a pairing, not with each other
		 * 	Result: decouple both pairings and pair tent to tree
		 * Case 6: ChosenCell is empty, and ChosenPairTree has no pairing
		 * 	Result: place tent and pair with tree
		 * Case 7: ChosenCell is empty, and ChosenPairTree has a pairing
		 * 	Result: decouple paired tree's pairing and pair tent with pairtree
		 * 
		 *else, chosenPairTree is a non-tree so do one of the cases below
		 * Case 8: ChosenCell is empty
		 * 	Result: place tent with no pairing
		 * Case 9: ChosenCell is a tent with no pairing
		 * 	Result: remove tent
		 * Case 10: ChosenCell is a tent with a pairing
		 * 	Result: currently remove tent and it's pairing
		 * */
		if(chosenPairTree.isTree()) {
			
		}else {
			
		}
		int violationChange = calcViolationChange(chosenCell, chosenPairTree);
		if(this.rand.nextDouble() < this.acceptanceProb(violationChange)) {
			this.curViolationCount += violationChange;
			this.adjustCell(chosenCell, chosenPairTree);
		}
	}

	public void generateInitialSol() {
		for (Cell tree : this.gameGrid.getTrees()) {
			tree.trimTrees();
			ArrayList<Cell> adjCells = (ArrayList<Cell>) tree.getCardinalAdjList();
			int cellToAdjust = this.rand.nextInt(adjCells.size());
			Cell changeCell = adjCells.get(cellToAdjust);
			int violationChange = calcViolationChange(changeCell, tree);
			this.curViolationCount += violationChange;
			adjustCell(changeCell, tree);
			this.availableCells.addAll(adjCells);
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
	
	//When done running, pairing in the maps should no longer exist
	public void decouplePairing(Cell tent, Cell tree) {
		
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

	public void outputToFile(String outputFile) {
		try (FileWriter writer = new FileWriter("data/" + outputFile)) {
			writer.write(this.solViolationCount + "\n");
			writer.write(this.solTentsPlaced + "\n");
			int linesToWrite = solPairings.length;
			System.out.println(linesToWrite);
			for(int lineNum = 0; lineNum < linesToWrite-1; lineNum++) {
				writer.write(this.solPairings[lineNum]);
			}
			writer.write(this.solPairings[linesToWrite-1]);
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
				for (int col = 0; col < this.cols; col++) {
					this.gameGrid.updateCell(row, col, line.charAt(col));
				}
			}
			System.out.println("Input read successfully");
		} catch (IOException e) {
			System.out.println("failed to read from file, msg- " + e.getMessage());
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
	public double acceptanceProb(int violationChange) {
		// Accept New Solution
		if (violationChange < 0) {
			return 1.0;
		}
		
		// calculate acceptance probability
		// DO NOT ADJUST HERE, MAKE ADJUSTMENTS ELSEWHERE
		return Math.exp( violationChange / this.temperature);
	}
	
	@Override
	public boolean equals(Object obj) {
		// FIXME: Could need to be fixed depending on implementation
		return super.equals(obj);
	}
	
}
