/**
 * "NullPntrException"
 * Solver for the Tents and Trees problem for Spring 2025: AlgoBowl
 * 
 * The puzzle game Tents and Trees is played on a 2-D grid, such that each cell may contain a tent, a tree, or it may be blank.
 *	1. No two tents are adjacent, even diagonally.
 *	2. Each row and column has the correct number of tents, as indicated by numbers marked outside the row or
 *		column.
 *	3. Each tent has its own tree, either horizontally or vertically adjacent.
 * Inputs may not be fully solvable and this class minimizes the number of violations.
 * 
 * @author Ty Gazaway
 * @author John Silva 
 * @author Thomas Dowd
 * 
 */
package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

// Time Spend Debugging CalcViolationChange: 9 Hours

/**
 * 
 * Tents And Tree's Simulated Annealing.<br> 
 * The present best solutions are kept Official Inputs/Output. Uses Simulated Annealing to solve this problem. <br>
 * <i>NOTE:</i> Not all functions are useful, methods marked with Misc, Utilities, or Testing are not ensured to be viable or safe.<br>
 * 
 * 
 * <br> VVV Output requirements VVV <br>
 * <br>~Incorect num of violations <- <i> INVALID </i> 
 * <br>~Tent Superposition (Tent cannot overlap with existing entity)<- <i> INVALID</i> 
 * <br>~Cant Fall off edge of world <- <i>INVALID</i> 
 * <br>~Tent cannot be paired with non-tree entity <-<i>INVALID</i> 
 * <br>~File must have proper formatting + No missing/corrupt data <- <i>INVALID</i>
 * <br>~Cant be an unpaired entity <- Violation
 * <br>~Multiple adjenecies != multiple violations (Specifially with Tents) <- Violation 
 * <br>~A row or column which has too many or too few tents causes multiple violations: one
 * <br>~violation for each tent to many or too few<- Violation
 * 
 *<br> <br> VVV Solving ideas/methods VVV <br>
 * MUST BE 1 Indexed; <br>
 * (ROW,COL) <br>
 * When complete, "todo" -> "fixme" <br>
 * 
 * <br> VVV References: VVV
 * <br>CSCI_404_2018 Lecture number 9 and 10
 * <br>Noureddin Sadawi's youtube playlist for Simmulated Annealing
 */
public class Solver {
	
	// ~ ~ ~ Class Attributes ~ ~ ~ //

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
	private String[] curPairings; // tent to tree

	// ~ ~ ~ Main ~ ~ ~ //
	public static void main(String[] args) {
		
		boolean runOfficials = false; // True -> Run all
		
		// Run many files at once
		if (runOfficials) {
			boolean continueRunning = true;
			HashSet<Integer> improvedFiles = new HashSet<Integer>();
			int[] filesToIgnore = { // Add all files to ignore here //
					973, 975, 976, 989, 998, 1001, 1004, 1006, 1007, 1008, 1009, 1010, 1011, 1012, 1014, 1015, 1020};
			while (continueRunning) { 
				int filesImproved = 0;
				for (int inputGroupNum = 963; inputGroupNum < 1025; inputGroupNum++) {
					boolean ignoreFile = false;
					for (int file : filesToIgnore) {
						if (inputGroupNum == file) {
							ignoreFile = true;
						}
					}
					if (ignoreFile) {
						continue;
					}
					String inputFileName = "input_group" + inputGroupNum + ".txt";
					String inputFileFolder = "officialInputs";
					String outputFileName = "output_group" + inputGroupNum + "_attempt.txt";
					String outputFileFolder = "officialOutputs";
					String inputFile = inputFileFolder + "/" + inputFileName;
					String outputFile = outputFileFolder + "/" + outputFileName;

					Solver solvee = new Solver();
					solvee.readInput(inputFile);
					solvee.calcInitialViolationCount();
					solvee.generateInitialSolFull();
					solvee.solveFull(inputFile, outputFile);

					int previousViolationCount = solvee.retrievePreviousViolationCount(outputFile);
					if (solvee.getSolViolationCount() < previousViolationCount) {
						solvee.outputToFile(outputFile);
						System.out.println("solution " + inputGroupNum + " improved");
						filesImproved++;
						improvedFiles.add(inputGroupNum);
					} else {
						System.out.println("Current best solution could not be improved...");
					}
				}
				if (filesImproved <= 5) {
					continueRunning = false;
				}
			}
			for (int file : improvedFiles) {
				System.out.println("Improved File - " + file);
			}
		} 
		
		// For running a single files at a time
		else { 
			int inputGroupNum = 991; // CHANGE HERE, to group number of choice
			
			String inputFileName = "input_group" + inputGroupNum + ".txt";
			String inputFileFolder = "officialInputs";
			String outputFileName = "output_group" + inputGroupNum + "_attempt.txt";
			String outputFileFolder = "officialOutputs";
			String inputFile = inputFileFolder + "/" + inputFileName;
			String outputFile = outputFileFolder + "/" + outputFileName;

			Solver solvee = new Solver();
			
			solvee.readInput(inputFile);
			solvee.calcInitialViolationCount();
			solvee.generateInitialSolFull();
			solvee.solveFull(inputFile, outputFile);

			int previousViolationCount = solvee.retrievePreviousViolationCount(outputFile);
			if (solvee.getSolViolationCount() < previousViolationCount) {
				solvee.outputToFile(outputFile);
				System.out.println("solution " + inputGroupNum + " improved");
			} else {
				System.out.println("solution not better than previous :( - " + solvee.getSolViolationCount());
			}

			@SuppressWarnings("unused")
			Verifier finalVerify = new Verifier(inputFile, outputFile);
			System.out.println(" ~ ~ ~ Simulation Complete ~ ~ ~ ");
		}
		
	}
	/**
	 * 
	 * Adjustable Parameters:<br>
	 * ~ {@link #temperature} to determine the temperature of the simulation <br>
	 * ~ {@link #coolingRate} to adjust the number of iterations of the simulation <br>
	 * 
	 * <i>NOTE:</i> This class is not optimized to run quickly, be patient while running. Exiting while this function is running could lead to undefined behavior
	 */
	public Solver() {
		// Adjustable Parameters
		this.temperature = 10;		   // ADJUST TEMP
		this.coolingRate = 0.00000001; // ADJUST RATE
		
		// Non-Adjustable Parameters
		this.rows = -1;
		this.cols = -1;
		this.rowTents = null;
		this.colTents = null;
		this.gameGrid = null;
		this.treeTentMap = new HashMap<Cell, Cell>();
		this.tentTreeMap = new HashMap<Cell, Cell>();
		this.availableCells = new ArrayList<Cell>();
		this.curViolationCount = 0;
		this.curRowTents = null;
		this.curColTents = null;
		this.solPairings = null;
		this.curPairings = null;
		this.solViolationCount = -1;
		this.solTentsPlaced = 0;

	}

	// ~ ~ ~ Solve Methods ~ ~ ~ //
	
	/**
	 * Final Solve Function, Called from main, Begins annealing process.<br>
	 * See Also: {@link #annealFull()}
	 * @param inputFile
	 * @param outputFile
	 */
	public void solveFull(String inputFile, String outputFile) {
		this.solViolationCount = this.curViolationCount;
		this.solTentsPlaced = this.gameGrid.getTents().size();
		this.updateSolutionPairings();
		//		this.updateCurPairings();
		while (this.temperature > 0) {
			annealFull();
			if (this.curViolationCount < this.solViolationCount || this.solViolationCount == -1) {
				this.solViolationCount = this.curViolationCount;
				this.solTentsPlaced = this.gameGrid.getTents().size();
				this.updateSolutionPairings();
			}
			this.temperature -= this.coolingRate;
		}
		if (this.curViolationCount < this.solViolationCount || this.solViolationCount == -1) {
			this.solViolationCount = this.curViolationCount;
			this.solTentsPlaced = this.gameGrid.getTents().size();
			this.updateSolutionPairings();
		}
	}

	// ~ ~ ~ Anneal Methods ~ ~ ~ //
	public void annealFull() {
		int totalValidCells = this.availableCells.size();
		Cell chosenCell = this.availableCells.get(this.rand.nextInt(0, totalValidCells));
		ArrayList<Cell> availablePairings = chosenCell.getCardinalAdjList();
		int totalAvailablePairings = availablePairings.size();
		int chosenPairDecision = this.rand.nextInt(0, totalAvailablePairings);
		Cell chosenPairTree = availablePairings.get(chosenPairDecision);
		/*if chosenPairTree is a tree, then do one of the cases below
		 * Case 1: ChosenCell is a tent with no pairing, and ChosenPairTree has no pairing
		 * 	Result: pair the two together
		 * Case 2: ChosenCell is empty, and ChosenPairTree has no pairing
		 * 	Result: place tent and pair with tree
		 * Case 3: ChosenCell is a tent paired with ChosenPairTree
		 * 	Result: remove tent and unpair
		 * Case 4: ChosenCell is a tent with a pairing, and ChosenPairTree has no pairing
		 * 	Result: decouple chosenCell's pairing and replace with ChosenPairTree
		 * Case 5: ChosenCell is a tent with no pairing, and ChosenPairTree has a pairing
		 * 	Result: decouple paired tree's pairing and pair tent with pairtree
		 * Case 6: ChosenCell is empty, and ChosenPairTree has a pairing
		 * 	Result: decouple paired tree's pairing and pair tent with pairtree
		 * Case 7: ChosenCell is a tent with a pairing, and ChosenPairTree has a pairing, not with each other
		 * 	Result: decouple both pairings and pair tent to tree
		 * 
		 *else, chosenPairTree is a non-tree so do one of the cases below
		 * Case 8: ChosenCell is empty
		 * 	Result: place tent with no pairing
		 * Case 9: ChosenCell is a tent with no pairing
		 * 	Result: remove tent
		 * Case 10: ChosenCell is a tent with a pairing
		 * 	Result: currently remove tent and it's pairing
		 * */
		int violationChange = calcViolationChangeTrees(chosenCell, chosenPairTree);
		if (this.rand.nextDouble() < this.acceptanceProb(violationChange)) {
			this.curViolationCount += violationChange;
			this.adjustCellFull(chosenCell, chosenPairTree);
		}
	}

	public void annealTrees() {
		int totalValidCells = this.availableCells.size();
		Cell chosenCell = this.availableCells.get(this.rand.nextInt(0, totalValidCells));
		ArrayList<Cell> availablePairings = chosenCell.getCardinalAdjList();
		int totalAvailablePairings = availablePairings.size();
		int chosenPairDecision = this.rand.nextInt(0, totalAvailablePairings);
		Cell chosenPairTree = availablePairings.get(chosenPairDecision);
		/*if chosenPairTree is a tree, then do one of the cases below
		 * Case 1: ChosenCell is a tent with no pairing, and ChosenPairTree has no pairing
		 * 	Result: pair the two together
		 * Case 2: ChosenCell is empty, and ChosenPairTree has no pairing
		 * 	Result: place tent and pair with tree
		 * Case 3: ChosenCell is a tent paired with ChosenPairTree
		 * 	Result: remove tent and unpair
		 * Case 4: ChosenCell is a tent with a pairing, and ChosenPairTree has no pairing
		 * 	Result: decouple chosenCell's pairing and replace with ChosenPairTree
		 * Case 5: ChosenCell is a tent with no pairing, and ChosenPairTree has a pairing
		 * 	Result: decouple paired tree's pairing and pair tent with pairtree
		 * Case 6: ChosenCell is empty, and ChosenPairTree has a pairing
		 * 	Result: decouple paired tree's pairing and pair tent with pairtree
		 * Case 7: ChosenCell is a tent with a pairing, and ChosenPairTree has a pairing, not with each other
		 * 	Result: decouple both pairings and pair tent to tree
		 * 
		 *else, chosenPairTree is a non-tree so do one of the cases below
		 * Case 8: ChosenCell is empty
		 * 	Result: place tent with no pairing
		 * Case 9: ChosenCell is a tent with no pairing
		 * 	Result: remove tent
		 * Case 10: ChosenCell is a tent with a pairing
		 * 	Result: currently remove tent and it's pairing
		 * */
		//this.printGrid(chosenCell);
		int violationChange = calcViolationChangeTrees(chosenCell, chosenPairTree);
		if (this.rand.nextDouble() < this.acceptanceProb(violationChange)) {
			this.curViolationCount += violationChange;
			this.adjustCellTrees(chosenCell, chosenPairTree);
		}
	}

	public void annealRowsCols() {
		int totalValidCells = this.availableCells.size();
		if (totalValidCells <= 0) {
			return;
		}
		Cell chosenCell = this.availableCells.get(this.rand.nextInt(0, totalValidCells));
		/* if chosenCell is a tent, do cases below
		 * 	delete the tent
		 * else it should be an empty cell, do cases below
		 * 	add a tent
		 * */
		int violationChange = calcViolationChangeRowsCols(chosenCell);
		if (this.rand.nextDouble() < this.acceptanceProb(violationChange)) {
			this.curViolationCount += violationChange;
			this.adjustCellRowsCols(chosenCell);
		}
	}

	// ~ ~ ~ Initial Solution Methods ~ ~ ~ //

	public void generateInitialSol() {
		for (Cell tree : this.gameGrid.getTrees()) {
			tree.trimTrees();
			ArrayList<Cell> adjCells = (ArrayList<Cell>) tree.getCardinalAdjList();
			if (adjCells.size() == 0) {
				continue;
			}
			int cellToAdjust = this.rand.nextInt(adjCells.size());
			Cell changeCell = adjCells.get(cellToAdjust);
			int violationChange = calcViolationChangeTrees(changeCell, tree);
			this.curViolationCount += violationChange;
			adjustCellTrees(changeCell, tree);
			this.availableCells.addAll(adjCells);
		}
	}

	public void generateInitialSolFull() {
		for (Cell tree : this.gameGrid.getTrees()) {
			tree.trimTrees();
			ArrayList<Cell> adjCells = (ArrayList<Cell>) tree.getCardinalAdjList();
			if (adjCells.size() == 0) {
				continue;
			}
			int cellToAdjust = this.rand.nextInt(adjCells.size());
			Cell changeCell = adjCells.get(cellToAdjust);
			int violationChange = calcViolationChangeTrees(changeCell, tree);
			this.curViolationCount += violationChange;
			adjustCellFull(changeCell, tree);
		}

		for (int row = 0; row < this.rows; row++) {
			for (int col = 0; col < this.cols; col++) {
				if (!this.gameGrid.getCell(row, col).isTree()) {
					this.availableCells.add(this.gameGrid.getCell(row, col));
				}
			}
		}
	}

	//adjust cells methods

	/*
	 * Updates cell to go from empty to tent and vice versa, and updates adj tree to be pair and vice versa
	 * 
	 * Case 1: ChosenCell is a tent with no pairing, and ChosenPairTree has no pairing
	 * 	Result: pair the two together
	 * Case 2: ChosenCell is empty, and ChosenPairTree has no pairing
	 * 	Result: place tent and pair with tree
	 * Case 3: ChosenCell is a tent paired with ChosenPairTree
	 * 	Result: remove tent and unpair
	 * Case 4: ChosenCell is a tent with a pairing, and ChosenPairTree has no pairing
	 * 	Result: decouple chosenCell's pairing and replace with ChosenPairTree
	 * Case 5: ChosenCell is a tent with no pairing, and ChosenPairTree has a pairing
	 * 	Result: decouple paired tree's pairing and pair tent with pairtree
	 * Case 6: ChosenCell is empty, and ChosenPairTree has a pairing
	 * 	Result: decouple paired tree's pairing and pair tent with pairtree
	 * Case 7: ChosenCell is a tent with a pairing, and ChosenPairTree has a pairing, not with each other
	 * 	Result: decouple both pairings and pair tent to tree
	 * 
	 * 
	 *else, chosenPairTree is a non-tree so do one of the cases below
	 * Case 8: ChosenCell is empty
	 * 	Result: place tent with no pairing
	 * Case 9: ChosenCell is a tent with no pairing
	 * 	Result: remove tent
	 * Case 10: ChosenCell is a tent with a pairing
	 * 	Result: currently remove tent and it's pairing
	 */
	public void adjustCellFull(Cell changeCell, Cell pairTree) {
		// Case 8 9 10 // Chnage Cell is Not a Tree
		if ((pairTree != null) && (!pairTree.isTree())) {
			// Case 8: Chosen Cell is empty -> Put Tent
			if (changeCell.getSymbol() == '.') {
				this.gameGrid.updateCell(changeCell, '^');
				this.curRowTents[changeCell.getRow()]--;
				this.curColTents[changeCell.getCol()]--;
			} else if (changeCell.getSymbol() == '^') { // We must be a tent
				// Case 9,10
				decouplePairings(changeCell, tentTreeMap.get(changeCell));
				this.gameGrid.updateCell(changeCell, '.');
				this.curRowTents[changeCell.getRow()]++;
				this.curColTents[changeCell.getCol()]++;
			}
			return;
		}

		if ((pairTree != null) && (pairTree.isTree()) && changeCell.isTent()) {
			if (this.tentTreeMap.get(changeCell) == pairTree) {// Case 3
				this.decouplePairings(changeCell, pairTree);
				this.gameGrid.updateCell(changeCell, '.');
				this.curRowTents[changeCell.getRow()]++;
				this.curColTents[changeCell.getCol()]++;
			} else {// Cases 1/4/5/7
				this.decouplePairings(changeCell, pairTree);
				this.tentTreeMap.put(changeCell, pairTree);
				this.treeTentMap.put(pairTree, changeCell);
			}
		} else { //Cases 2/6
			this.decouplePairings(changeCell, pairTree);
			this.gameGrid.updateCell(changeCell, '^');
			this.tentTreeMap.put(changeCell, pairTree);
			this.treeTentMap.put(pairTree, changeCell);
			this.curRowTents[changeCell.getRow()]--;
			this.curColTents[changeCell.getCol()]--;
		}

	}

	/*
	 * Updates cell to go from empty to tent and vice versa, and updates adj tree to be pair and vice versa
	 * 
	 * Case 1: ChosenCell is a tent with no pairing, and ChosenPairTree has no pairing
	 * 	Result: pair the two together
	 * Case 2: ChosenCell is empty, and ChosenPairTree has no pairing
	 * 	Result: place tent and pair with tree
	 * Case 3: ChosenCell is a tent paired with ChosenPairTree
	 * 	Result: remove tent and unpair
	 * Case 4: ChosenCell is a tent with a pairing, and ChosenPairTree has no pairing
	 * 	Result: decouple chosenCell's pairing and replace with ChosenPairTree
	 * Case 5: ChosenCell is a tent with no pairing, and ChosenPairTree has a pairing
	 * 	Result: decouple paired tree's pairing and pair tent with pairtree
	 * Case 6: ChosenCell is empty, and ChosenPairTree has a pairing
	 * 	Result: decouple paired tree's pairing and pair tent with pairtree
	 * Case 7: ChosenCell is a tent with a pairing, and ChosenPairTree has a pairing, not with each other
	 * 	Result: decouple both pairings and pair tent to tree
	 * 
	 * 
	 *else, chosenPairTree is a non-tree so do one of the cases below
	 * Case 8: ChosenCell is empty
	 * 	Result: place tent with no pairing
	 * Case 9: ChosenCell is a tent with no pairing
	 * 	Result: remove tent
	 * Case 10: ChosenCell is a tent with a pairing
	 * 	Result: currently remove tent and it's pairing
	 */
	public void adjustCellTrees(Cell changeCell, Cell pairTree) {
		// Case 8 9 10 // Chnage Cell is Not a Tree
		if ((pairTree != null) && (!pairTree.isTree())) {
			// Case 8: Chosen Cell is empty -> Put Tent
			if (changeCell.getSymbol() == '.') {
				this.gameGrid.updateCell(changeCell, '^');
				this.curRowTents[changeCell.getRow()]--;
				this.curColTents[changeCell.getCol()]--;
			} else if (changeCell.getSymbol() == '^') { // We must be a tent
				// Case 9,10
				decouplePairings(changeCell, tentTreeMap.get(changeCell));
				this.gameGrid.updateCell(changeCell, '.');
				this.curRowTents[changeCell.getRow()]++;
				this.curColTents[changeCell.getCol()]++;
			}
			return;
		}

		if ((pairTree != null) && (pairTree.isTree()) && changeCell.isTent()) {
			if (this.tentTreeMap.get(changeCell) == pairTree) {// Case 3
				this.decouplePairings(changeCell, pairTree);
				this.gameGrid.updateCell(changeCell, '.');
				this.curRowTents[changeCell.getRow()]++;
				this.curColTents[changeCell.getCol()]++;
			} else {// Cases 1/4/5/7
				this.decouplePairings(changeCell, pairTree);
				this.tentTreeMap.put(changeCell, pairTree);
				this.treeTentMap.put(pairTree, changeCell);
			}
		} else { //Cases 2/6
			this.decouplePairings(changeCell, pairTree);
			this.gameGrid.updateCell(changeCell, '^');
			this.tentTreeMap.put(changeCell, pairTree);
			this.treeTentMap.put(pairTree, changeCell);
			this.curRowTents[changeCell.getRow()]--;
			this.curColTents[changeCell.getCol()]--;
		}

	}

	/*Calculates the violation count change if a cell is to be adjusted and it's pairing should it have one
	 * 
	 * if chosenCell is tent
	 * delete the tent
	 * else
	 * place a tent
	 * */
	public void adjustCellRowsCols(Cell chosenCell) {
		if (chosenCell.isTent()) {
			this.gameGrid.updateCell(chosenCell, '.');
			this.curRowTents[chosenCell.getRow()]++;
			this.curColTents[chosenCell.getCol()]++;
		} else {
			this.gameGrid.updateCell(chosenCell, '^');
			this.curRowTents[chosenCell.getRow()]--;
			this.curColTents[chosenCell.getCol()]--;
		}
	}

	// ~ ~ ~ calc violation change methods ~ ~ ~ //

	/*Calculates the violation count change if a cell is to be adjusted and it's pairing should it have one
	 * 
	 * if chosenPairTree is a tree, then do one of the cases below
	 * Case 1: ChosenCell is a tent with no pairing, and ChosenPairTree has no pairing
	 * 	Result: pair the two together
	 * Case 2: ChosenCell is empty, and ChosenPairTree has no pairing
	 * 	Result: place tent and pair with tree
	 * Case 3: ChosenCell is a tent paired with ChosenPairTree
	 * 	Result: remove tent and unpair
	 * Case 4: ChosenCell is a tent with a pairing, and ChosenPairTree has no pairing
	 * 	Result: decouple chosenCell's pairing and replace with ChosenPairTree
	 * Case 5: ChosenCell is a tent with no pairing, and ChosenPairTree has a pairing
	 * 	Result: decouple paired tree's pairing and pair tent with pairtree
	 * Case 6: ChosenCell is empty, and ChosenPairTree has a pairing
	 * 	Result: decouple paired tree's pairing and pair tent with pairtree
	 * Case 7: ChosenCell is a tent with a pairing, and ChosenPairTree has a pairing, not with each other
	 * 	Result: decouple both pairings and pair tent to tree
	 * 
	 *else, chosenPairTree is a non-tree so do one of the cases below
	 * Case 8: ChosenCell is empty
	 * 	Result: place tent with no pairing
	 * Case 9: ChosenCell is a tent with no pairing
	 * 	Result: remove tent
	 * Case 10: ChosenCell is a tent with a pairing
	 * 	Result: currently remove tent and it's pairing
	 * 
	 * If something has gone wrong, odds are its around here.
	 * */
	public int calcViolationChangeTrees(Cell changeCell, Cell pairTree) {
		int violationChange = 0;
		int row = changeCell.getRow();
		int col = changeCell.getCol();
		//Check if the cell to change is an empty cell
		if (pairTree != null && pairTree.isTree()) {
			if (this.tentTreeMap.get(changeCell) == pairTree) {// Case 3
				//				 * Case 3: ChosenCell is a tent paired with ChosenPairTree
				//				 * 	Result: remove tent and unpair
				violationChange += (Math.abs(this.curRowTents[row] + 1) - Math.abs(this.curRowTents[row]));//update Violations from rowCount
				violationChange += (Math.abs(this.curColTents[col] + 1) - Math.abs(this.curColTents[col]));//update Violations from colCount
				violationChange++;
				if (this.gameGrid.isAdjTent(changeCell)) {//check if there is at least one adj tent, either case
					violationChange--;
					violationChange -= this.gameGrid.checkAdjAliens(changeCell, true);
				}
			} else if (this.tentTreeMap.containsKey(changeCell) || this.treeTentMap.containsKey(pairTree)) {// Cases 4/5/6/7
				//				 * Case 4: ChosenCell is a tent with a pairing, and ChosenPairTree has no pairing, no change
				//				 * Case 5: ChosenCell is a tent with no pairing, and ChosenPairTree has a pairing, no change
				//				 * Case 6: ChosenCell is empty, and ChosenPairTree has a pairing, multiple changes
				//		 		 * Case 7: ChosenCell is a tent with a pairing, and ChosenPairTree has a pairing, not with each other, multiple changes
				//				 * 	Result: decouple pairings and pair tent to tree
				if (this.tentTreeMap.containsKey(changeCell) && this.treeTentMap.containsKey(pairTree)) {
					violationChange += 2;
				}
				if (this.treeTentMap.containsKey(pairTree) && !changeCell.isTent()) {
					violationChange++;
				}
				if (!changeCell.isTent()) {
					violationChange += (Math.abs(this.curRowTents[row] - 1) - Math.abs(this.curRowTents[row]));//update Violations from rowCount
					violationChange += (Math.abs(this.curColTents[col] - 1) - Math.abs(this.curColTents[col]));//update Violations from colCount
					if (this.gameGrid.isAdjTent(changeCell)) {//check if there is at least one adj tent, either case
						violationChange++;
						violationChange += this.gameGrid.checkAdjAliens(changeCell, false);
					}
				}
			} else if (changeCell.isTent()) {// Case 1
				//				* Case 1: ChosenCell is a tent with no pairing, and ChosenPairTree has no pairing, -1 violation
				//				 * 	Result: pair the two together
				violationChange--;
				violationChange--;
			} else {//Case 2
				//				 * Case 2: ChosenCell is empty, and ChosenPairTree has no pairing, multipleChanges
				//				 * 	Result: place tent and pair with tree
				violationChange += (Math.abs(this.curRowTents[row] - 1) - Math.abs(this.curRowTents[row]));//update Violations from rowCount
				violationChange += (Math.abs(this.curColTents[col] - 1) - Math.abs(this.curColTents[col]));//update Violations from colCount
				if (this.gameGrid.isAdjTent(changeCell)) {//check if there is at least one adj tent
					violationChange++;
					violationChange += this.gameGrid.checkAdjAliens(changeCell, false);
				}
				violationChange--; //pair with tree
			}
		} else {
			if (changeCell.isTent()) {//Cases 9/10
				//			     * Case 9: ChosenCell is a tent with no pairing, multiple changes
				//				 * Case 10: ChosenCell is a tent with a pairing, multiple changes
				//				 * 	Result: decouple and remove tent
				violationChange += (Math.abs(this.curRowTents[row] + 1) - Math.abs(this.curRowTents[row]));//update Violations from rowCount
				violationChange += (Math.abs(this.curColTents[col] + 1) - Math.abs(this.curColTents[col]));//update Violations from colCount
				if (this.gameGrid.isAdjTent(changeCell)) {//check if there is at least one adj tent
					violationChange--;
					violationChange -= this.gameGrid.checkAdjAliens(changeCell, true);
				}
				if (this.tentTreeMap.containsKey(changeCell)) {
					violationChange++;
				} else {
					violationChange--;
				}
			} else {// Case 8
				//				 * Case 8: ChosenCell is empty, multiple changes
				//				 * 	Result: place tent with no pairing
				violationChange += (Math.abs(this.curRowTents[row] - 1) - Math.abs(this.curRowTents[row]));//update Violations from rowCount
				violationChange += (Math.abs(this.curColTents[col] - 1) - Math.abs(this.curColTents[col]));//update Violations from colCount
				violationChange++;
				if (this.gameGrid.isAdjTent(changeCell)) {//check if there is at least one adj tent
					violationChange++;
					violationChange += this.gameGrid.checkAdjAliens(changeCell, false);
				}
			}
		}
		return violationChange;
	}

	/*
	 * Calculates the violation count change if a cell is to be adjusted and it's pairing should it have one
	 * 
	 * if chosenCell is tent
	 * delete the tent
	 * else
	 * place a tent
	 * */
	public int calcViolationChangeRowsCols(Cell chosenCell) {
		int violationChange = 0;
		int row = chosenCell.getRow();
		int col = chosenCell.getCol();
		if (chosenCell.isTent()) {
			violationChange += (Math.abs(this.curRowTents[row] + 1) - Math.abs(this.curRowTents[row]));//update Violations from rowCount
			violationChange += (Math.abs(this.curColTents[col] + 1) - Math.abs(this.curColTents[col]));//update Violations from colCount
			violationChange--;
			if (this.gameGrid.isAdjTent(chosenCell)) {//check if there is at least one adj tent
				violationChange--;
				violationChange -= this.gameGrid.checkAdjAliens(chosenCell, true);
			}
		} else {
			violationChange += (Math.abs(this.curRowTents[row] - 1) - Math.abs(this.curRowTents[row]));//update Violations from rowCount
			violationChange += (Math.abs(this.curColTents[col] - 1) - Math.abs(this.curColTents[col]));//update Violations from colCount
			violationChange++;
			if (this.gameGrid.isAdjTent(chosenCell)) {//check if there is at least one adj tent
				violationChange++;
				violationChange += this.gameGrid.checkAdjAliens(chosenCell, false);
			}
		}
		return violationChange;
	}

	// ~ ~ ~ Misc Methods ~ ~ ~ //

	public void updateSolutionPairings() {
		int linesToWrite = this.gameGrid.getTents().size();
		int curLine = 0;
		this.solPairings = new String[linesToWrite];
		String finalTreeDir = "";
		int finalTentRow = 0;
		int finalTentCol = 0;
		if (linesToWrite == 0) {
			return;
		}
		for (Cell tent : this.gameGrid.getTents()) {
			String treeDir = "X";
			if (this.tentTreeMap.containsKey(tent)) {
				Cell pairedTree = this.tentTreeMap.get(tent);
				int rowDiff = tent.getRow() - pairedTree.getRow();
				int colDiff = tent.getCol() - pairedTree.getCol();
				if (rowDiff == 1) {
					treeDir = "U";
				} else if (rowDiff == -1) {
					treeDir = "D";
				} else if (colDiff == 1) {
					treeDir = "L";
				} else if (colDiff == -1) {
					treeDir = "R";
				}
			}
			if (curLine == linesToWrite - 1) {
				finalTreeDir = treeDir;
				finalTentRow = tent.getRow();
				finalTentCol = tent.getCol();
				break;
			}
			this.solPairings[curLine] = ((tent.getRow() + 1) + " " + (tent.getCol() + 1) + " " + treeDir + "\n");
			curLine++;
		}
		this.solPairings[curLine] = ((finalTentRow + 1) + " " + (finalTentCol + 1) + " " + finalTreeDir);
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

	// When done running, pairing in the maps should no longer exist. 
	public void decouplePairings(Cell tent, Cell tree) {
		if (this.tentTreeMap.containsKey(tent)) {
			this.treeTentMap.remove(this.tentTreeMap.get(tent));
		}
		if (this.treeTentMap.containsKey(tree)) {
			this.tentTreeMap.remove(this.treeTentMap.get(tree));
		}
		this.tentTreeMap.remove(tent);
		this.treeTentMap.remove(tree);
	}
	
	/**
	 * Outputs to a file. 
	 * @param outputFile
	 */
	public void outputToFile(String outputFile) {
		try (FileWriter writer = new FileWriter("data/" + outputFile)) {
			writer.write(this.solViolationCount + "\n");
			writer.write(this.solTentsPlaced + "\n");
			int linesToWrite = this.solPairings.length;
			for (int lineNum = 0; lineNum < linesToWrite - 1; lineNum++) {
				writer.write(this.solPairings[lineNum]);
			}
			if (linesToWrite != 0) {
				writer.write(this.solPairings[linesToWrite - 1]);
			}
		} catch (IOException e) {
			System.out.println("failed to output to file, msg- " + e.getMessage());
		}
	}

	/**
	 * Reads input from official input. 
	 * @param inputFile
	 */
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

	// ~ ~ ~ Utilities ~ ~ ~ //

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
		return Math.exp(-violationChange / this.temperature);
	}

	public double acceptanceProbWeighted(int violationChange) {
		// Accept New Solution
		if (violationChange < 0) {
			return 1.0;
		}

		// calculate acceptance probability
		return Math.exp((Math.pow(violationChange + 1, 4)) / this.temperature);
	}

	@Override
	public boolean equals(Object obj) {
		// FIXME: Could need to be fixed depending on implementation
		return super.equals(obj);
	}

	public int retrievePreviousViolationCount(String outputFile) {
		try (Scanner sc = new Scanner(new File("data/" + outputFile))) {
			return sc.nextInt();
		} catch (IOException e) {
			System.out.println("failed to read from file, msg- " + e.getMessage());
		}
		return -1;
	}

	public int getSolViolationCount() {
		return this.solViolationCount;
	}

	//Testing methods

	/**
	 * Updates all parings to trivial paring locations<br>
	 * 
	 */
	public void updateCurPairings() {
		int linesToWrite = this.gameGrid.getTents().size();
		int curLine = 0;
		this.curPairings = new String[linesToWrite];
		String finalTreeDir = "";
		int finalTentRow = 0;
		int finalTentCol = 0;
		if (linesToWrite == 0) {
			return;
		}
		for (Cell tent : this.gameGrid.getTents()) {
			String treeDir = "X";
			if (this.tentTreeMap.containsKey(tent)) {
				Cell pairedTree = this.tentTreeMap.get(tent);
				int rowDiff = tent.getRow() - pairedTree.getRow();
				int colDiff = tent.getCol() - pairedTree.getCol();
				if (rowDiff == 1) {
					treeDir = "U";
				} else if (rowDiff == -1) {
					treeDir = "D";
				} else if (colDiff == 1) {
					treeDir = "L";
				} else if (colDiff == -1) {
					treeDir = "R";
				}
			}
			if (curLine == linesToWrite - 1) {
				finalTreeDir = treeDir;
				finalTentRow = tent.getRow();
				finalTentCol = tent.getCol();
				break;
			}
			this.curPairings[curLine] = ((tent.getRow() + 1) + " " + (tent.getCol() + 1) + " " + treeDir + "\n");
			curLine++;
		}
		this.curPairings[curLine] = ((finalTentRow + 1) + " " + (finalTentCol + 1) + " " + finalTreeDir);
	}

	public void curOutputToFile(String outputFile) {
		try (FileWriter writer = new FileWriter("data/" + outputFile)) {
			writer.write(this.curViolationCount + "\n");
			writer.write(this.gameGrid.getTents().size() + "\n");
			int linesToWrite = curPairings.length;
			for (int lineNum = 0; lineNum < linesToWrite - 1; lineNum++) {
				writer.write(this.curPairings[lineNum]);
			}
			if (linesToWrite != 0) {
				writer.write(this.curPairings[linesToWrite - 1]);
			}
		} catch (IOException e) {
			System.out.println("failed to output to file, msg- " + e.getMessage());
		}
	}

	public void printCurOutput() {
		System.out.print(this.curViolationCount + "\n");
		System.out.print(this.gameGrid.getTents().size() + "\n");
		int linesToWrite = curPairings.length;
		for (int lineNum = 0; lineNum < linesToWrite - 1; lineNum++) {
			System.out.print(this.curPairings[lineNum]);
		}
		if (linesToWrite != 0) {
			System.out.print(this.curPairings[linesToWrite - 1]);
		}
	}

	public void printGrid() {
		System.out.println(this.rows + " " + this.cols);
		System.out.println(this.curViolationCount + " vcount");
		System.out.println(this.gameGrid.getTents().size() + " tcount");
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

	public void printGrid(Cell ProcessCell) {

		System.out.println(this.rows + " " + this.cols);
		System.out.println(this.curViolationCount + " vcount");
		System.out.println(this.gameGrid.getTents().size() + " tcount");
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
				if (ProcessCell.getRow() == row && ProcessCell.getCol() == col) {
					System.out.print('*');
				} else {
					String symbol = this.gameGrid.getCell(row, col).toString();
					System.out.print(symbol);
				}
			}
			System.out.println();
		}
	}

	public void reloadSolution(String inputFile) {
		try (Scanner sc = new Scanner(new File("data/" + inputFile))) {
			this.rows = Integer.parseInt(sc.next());
			this.cols = Integer.parseInt(sc.next());

			this.rowTents = new int[this.rows];
			this.colTents = new int[this.cols];
			this.curRowTents = new int[this.rows];
			this.curColTents = new int[this.cols];
			this.tentTreeMap.clear();
			this.treeTentMap.clear();

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
			//loop through the lines that contain the coordinate 
			for (int line = 0; line < this.solPairings.length; line++) {
				String[] elements = this.solPairings[line].split(" ");

				//add code to throw an error if the types don't match
				int row = 0, col = 0;
				row = Integer.parseInt(elements[0]) - 1;
				col = Integer.parseInt(elements[1]) - 1;
				char direction = elements[2].charAt(0);

				Cell currCell = this.gameGrid.getCell(row, col);

				char currCellChar = currCell.getSymbol();
				if (currCellChar == '.') {
					this.gameGrid.updateCell(currCell, '^');

					//adjust row and column tent counts
					curRowTents[row]--;
					curColTents[col]--;

					if (direction == 'X') {
						continue;
					} else {
						int treeR = currCell.getRow();
						int treeC = currCell.getCol();

						if (direction == 'U') {
							treeR--;
						} else if (direction == 'R') {
							treeC++;
						} else if (direction == 'D') {
							treeR++;
						} else {
							treeC--;
						}
						this.tentTreeMap.put(currCell, this.gameGrid.getCell(treeR, treeC));
						this.treeTentMap.put(this.gameGrid.getCell(treeR, treeC), currCell);
					}
				}
			}
			this.curViolationCount = this.solViolationCount;
			//			this.printGrid();
		} catch (IOException e) {
			System.out.println("failed to read from file, msg- " + e.getMessage());
		}
	}
}
