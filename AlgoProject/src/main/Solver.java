/**
 * "NullPntrException"
 * @author Ty Gazaway
 * @author John Silva 
 * @author Thomas Dowd
 * 
 */
package main;

import java.io.File;
import java.io.FileNotFoundException;
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


import java.util.Scanner;
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
	private String[] curPairings; // tent to tree

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
		
		boolean runAllFiles = true;
		if(runAllFiles) {
			int groupToIgnore = 1020;
			
			for(int group = 974; group <=976; group++) {
				if(group == groupToIgnore) {
					continue;
				}
				String input = "officialInputs/input_group" + group + ".txt";
				String output = "newBest/" + group + ".txt";
				
				//read the number of violations from our best
				String baseOut = "data/BestSoFar/output_from_991_to_";
				String end = ".txt";
				String bestFile = baseOut + group + end;
				int bestViolationCount;
				try {
			        Scanner scanner = new Scanner(new File(bestFile));
			        bestViolationCount = scanner.nextInt();
			        scanner.close();
			    } catch (FileNotFoundException e) {
			        System.err.println("Error: File not found - " + bestFile);
			        return;
			    }
				
				for(int annealRuns = 0; annealRuns < 5; annealRuns++) {
					
					
					Solver solvee = new Solver();
					solvee.readInput(input);
					solvee.calcInitialViolationCount();
					solvee.generateInitialSol();
					solvee.solveWithIssues(input, output);
					
					if(solvee.getSolViolationCount() < bestViolationCount) {
						solvee.outputToFile(output);
						bestViolationCount = solvee.getSolViolationCount();
						System.out.println("Solution for group " + group + " input improved!");
					} else {
						System.out.println("Current best solution could not be improved...");
					}
				}
			}
		}
		
		
//		boolean runOfficials = false;
//		if (runOfficials) {
//			boolean continueRunning = true;
//			HashSet<Integer> improvedFiles = new HashSet<Integer>();
//			int[] filesToIgnore = { 1001, 1007, 1008, 1020 };
//			while (continueRunning) {
//				int filesImproved = 0;
//				for (int inputGroupNum = 963; inputGroupNum < 1025; inputGroupNum++) {
//					boolean ignoreFile = false;
//					for (int file : filesToIgnore) {
//						if (inputGroupNum == file) {
//							ignoreFile = true;
//						}
//					}
//					if (ignoreFile) {
//						continue;
//					}
//					String inputFileName = "input_group" + inputGroupNum + ".txt";
//					String inputFileFolder = "officialInputs";
//					String outputFileName = "output_group" + inputGroupNum + "_attempt.txt";
//					String outputFileFolder = "officialOutputs";
//					String inputFile = inputFileFolder + "/" + inputFileName;
//					String outputFile = outputFileFolder + "/" + outputFileName;
//
//					Solver solvee = new Solver();
//
//					solvee.readInput(inputFile);
//					solvee.calcInitialViolationCount();
//					solvee.generateInitialSol();
//					solvee.solveWithIssues(inputFile, outputFile);
//
//					int previousViolationCount = solvee.retrievePreviousViolationCount(outputFile);
//					if (solvee.getSolViolationCount() < previousViolationCount) {
//						solvee.outputToFile(outputFile);
//						System.out.println("solution " + inputGroupNum + " improved");
//						filesImproved++;
//						improvedFiles.add(inputGroupNum);
//					} else {
//						System.out.println("solution not better than previous :( - " + solvee.getSolViolationCount());
//					}
//
//					Verifier finalVerify = new Verifier(inputFile, outputFile);
//				}
//				//				if (filesImproved <= 5) {
//				continueRunning = false;
//				//				}
//			}
//			for (int file : improvedFiles) {
//				System.out.println("Improved File - " + file);
//			}
//		} else {
//			int inputGroupNum = 1019;
//			String inputFileName = "input_group" + inputGroupNum + ".txt";
//			String inputFileFolder = "officialInputs";
//			String outputFileName = "output_group" + inputGroupNum + "_attempt.txt";
//			String outputFileFolder = "testingOutputFiles";
//			String inputFile = inputFileFolder + "/" + inputFileName;
//			String outputFile = outputFileFolder + "/" + outputFileName;
//
//			Solver solvee = new Solver();
//
//			solvee.readInput(inputFile);
//			solvee.calcInitialViolationCount();
//			solvee.generateInitialSol();
//			solvee.solveWithIssues(inputFile, outputFile);
//
//			int previousViolationCount = solvee.retrievePreviousViolationCount(outputFile);
//			if (solvee.getSolViolationCount() < previousViolationCount) {
//				solvee.outputToFile(outputFile);
//				System.out.println("solution " + inputGroupNum + " improved");
//			} else {
//				System.out.println("solution not better than previous :( - " + solvee.getSolViolationCount());
//			}
//
//			Verifier finalVerify = new Verifier(inputFile, outputFile);
//
//		}
	}

	public Solver() {
		this.rows = -1;
		this.cols = -1;
		this.rowTents = null;
		this.colTents = null;
		this.gameGrid = null;
		this.temperature = 10;
		this.coolingRate = 0.0000001;
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

	//solve methods

	public void solve(String inputFile) {
		double initialTemp = this.temperature;
		this.solViolationCount = this.curViolationCount;
		this.solTentsPlaced = this.gameGrid.getTents().size();
		this.updateSolutionPairings();
		while (this.temperature > 0) {
			annealTrees();
			if (this.curViolationCount < this.solViolationCount || solViolationCount == -1) {
				solViolationCount = curViolationCount;
				solTentsPlaced = this.gameGrid.getTents().size();
				this.updateSolutionPairings();
			}
			this.temperature -= this.coolingRate;
		}
		if (this.curViolationCount < this.solViolationCount || this.solViolationCount == -1) {
			this.solViolationCount = this.curViolationCount;
			this.solTentsPlaced = this.gameGrid.getTents().size();
			this.updateSolutionPairings();
		}
		System.out.println("attempting to reload current solution");
		this.reloadSolution(inputFile);
		ArrayList<Cell> availableCells2 = new ArrayList<Cell>();
		for (int row = 0; row < this.rows; row++) {
			for (int col = 0; col < this.cols; col++) {
				availableCells2.add(this.gameGrid.getCell(row, col));
			}
		}
		availableCells2.removeAll(this.availableCells);
		availableCells2.removeAll(this.gameGrid.getTrees());
		this.availableCells.clear();
		this.availableCells = new ArrayList<Cell>(availableCells2);
		this.temperature = initialTemp;
		while (this.temperature > 0) {
			annealRowsCols();
			if (curViolationCount < solViolationCount || solViolationCount == -1) {
				solViolationCount = curViolationCount;
				solTentsPlaced = this.gameGrid.getTents().size();
				this.updateSolutionPairings();
			}
			this.temperature -= this.coolingRate;
		}

	}

	public void solveWithIssues(String inputFile, String outputFile) {
		double initialTemp = this.temperature;
		this.solViolationCount = this.curViolationCount;
		this.solTentsPlaced = this.gameGrid.getTents().size();
		this.updateSolutionPairings();
//		this.updateCurPairings();
		while (this.temperature > 0) {
			annealTrees();
			if (this.curViolationCount < this.solViolationCount || this.solViolationCount == -1) {
				this.solViolationCount = this.curViolationCount;
				this.solTentsPlaced = this.gameGrid.getTents().size();
				this.updateSolutionPairings();
			}
//			this.updateCurPairings();
//			this.curOutputToFile(outputFile);
//			this.printCurOutput();
//			System.out.println();
//			Verifier curVerify = new Verifier(inputFile, outputFile);
			this.temperature -= this.coolingRate;
		}
		if (this.curViolationCount < this.solViolationCount || this.solViolationCount == -1) {
			this.solViolationCount = this.curViolationCount;
			this.solTentsPlaced = this.gameGrid.getTents().size();
			this.updateSolutionPairings();
		}
		this.updateCurPairings();
		this.curOutputToFile(outputFile);
		this.printCurOutput();
		System.out.println("attempting to reload current solution");
//		this.printGrid();
		this.reloadSolution(inputFile);
		this.printGrid();
		ArrayList<Cell> availableCells2 = new ArrayList<Cell>();
		for (int row = 0; row < this.rows; row++) {
			for (int col = 0; col < this.cols; col++) {
				Cell cell = this.gameGrid.getCell(row, col);
				if(!this.availableCells.contains(cell)) {
					availableCells2.add(this.gameGrid.getCell(row, col));
				}
			}
		}
		availableCells2.removeAll(this.gameGrid.getTrees());
		this.availableCells.clear();
		this.availableCells = availableCells2;
		this.temperature = initialTemp;
		this.updateCurPairings();
		this.curOutputToFile(outputFile);
		this.printCurOutput();
		System.out.println();
		Verifier curVerify = new Verifier(inputFile, outputFile);
		while (this.temperature > 0) {
			annealRowsCols();
			if (this.curViolationCount < this.solViolationCount || this.solViolationCount == -1) {
				this.solViolationCount = this.curViolationCount;
				this.solTentsPlaced = this.gameGrid.getTents().size();
				this.updateSolutionPairings();
			}
			this.temperature -= this.coolingRate;
		}
	}

	//anneal methods

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
//		this.printGrid(chosenCell);
		int violationChange = calcViolationChangeRowsCols(chosenCell);
		if (this.rand.nextDouble() < this.acceptanceProb(violationChange)) {
			this.curViolationCount += violationChange;
			this.adjustCellRowsCols(chosenCell);
		}
	}

	//generate initial solution methods

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

	//calc violation change methods

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

	/*Calculates the violation count change if a cell is to be adjusted and it's pairing should it have one
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

	//other methods

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

	//When done running, pairing in the maps should no longer exist, neither 
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
