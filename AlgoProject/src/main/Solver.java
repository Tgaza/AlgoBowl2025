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

import java.util.Random;
import java.util.Iterator;
import java.util.List;
/**
 * -TODO: Solver
 */
public class Solver {
	private Verifier gridToSolve;
	
	//these need to be added to the verifier so that they don't get overwritten
//	private Set<Cell> tents = new HashSet<>();
//	private Set<Cell> trees = new HashSet<>();
//	private int numTents;
//	private int numTrees;
	
	private int rows;
	private int columns;
	private int numCells;
	
	private double coolingRate;
	private double initialTemp;
	private int maxIterations;
	
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
		
//		System.out.println(rows + " " + columns);
		
		//fills all empty space with trees
		fillEmptySpacesWithTents();
		
		this.coolingRate = 0.97;
		
		this.numCells = rows*columns;
		
		if(numCells > 10000) {
			this.initialTemp = 50000;
		} else if(numCells > 1000) {
			this.initialTemp = 5000;
		} else {
			this.initialTemp = 500;
		}
		
		this.maxIterations = 100*numCells;
		
		Verifier solution = simAnneal(maxIterations, initialTemp, coolingRate);
		
//		gridToSolve = solution;
		
		printGrid();
		System.out.println("Total violations: " + solution.sumViolations());
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
					gridToSolve.tents.add(currCell);
					gridToSolve.numTents++;
				}
				if(currCell.getSymbol() == 'T') {
					gridToSolve.trees.add(currCell);
					gridToSolve.numTrees++;
				}
			}
		}
		
		//for debugging
		printGrid();
		System.out.println("Total violations: " + gridToSolve.sumViolations());
	}
	
	private void deleteTent(Cell cell, Verifier verifier) {
		cell.setSymbol('.');
		verifier.numTents--;
	}
	
	private void unPair(Cell cell) {
		Cell pairedCell = cell.getPairedCells().getFirst();
   	 	cell.getPairedCells().removeFirst();
   	 	pairedCell.getPairedCells().removeFirst();
	}
	
	//creates pairing from list of available cells
	private void createPairing(Cell cell, Set<Cell> nearByTrees) {
		 //convert set to iterator
		 Iterator<Cell> iterator = nearByTrees.iterator();
		 Cell pairedCell = iterator.next();
		 //create pair
		 cell.addPairedCell(pairedCell);
		 pairedCell.addPairedCell(cell);
	}
	
	private Verifier modifyProblem(Verifier curr) {
		Verifier next = curr;
		int numMods;
		
		if(numCells > 10000) {
			numMods = 20;
		} else if(numCells > 1000) {
			numMods = 5;
		} else {
			numMods = 1;
		}
		
		Iterator<Cell> iterator = next.tents.iterator();
		for(int i = 0; i < numMods; i++) {
			if(next.tents.size() == 0) {
				break;
			}
			Cell tent = iterator.next();
			next.tents.remove(tent);
			List<Cell> adjList = tent.getCardinalAdjList();
			
			Set<Cell> nearByTrees = new HashSet<>();
			for(Cell cell : adjList) {
				if(cell.getSymbol() == 'T') {
					nearByTrees.add(cell);
				}
			}
			
			//just remove the tree if it's alone
			if(nearByTrees.size() == 0) {
				deleteTent(tent, next);
			} else {
				 Random rand = new Random();
			     int randomNum = rand.nextInt(2) + 1;
			     //if 1 delete, if 2 don't
			     if(randomNum == 1) {
			    	 //check to see if paired, because if paired, you got to unpair the paired set
			    	 if(tent.getPairedCells().size() == 0) {
			    		 deleteTent(tent, next);
			    	 } else {
			    		 deleteTent(tent, next);
				    	 //removes the pairing between the two cells
				    	 unPair(tent);
			    	 }
			     } else {
			    	 //first if the tent is not paired yet, we randomly choose what cell to pair it to
			    	 if(tent.getPairedCells().size() == 0) {
			    		 createPairing(tent, nearByTrees);
			    	 } else {
			    		 //first off make sure that there is more than one tent to pair too, otherwise we're just going to delete the cell
			    		 if(tent.getPairedCells().size() > 1) {
			    			 //first remove the cell that the tent was currently paired to from the list of total possible cells to pair to
			    			 Cell currPair = tent.getPairedCells().getFirst();
			    			 nearByTrees.remove(currPair);
			    			 //now delete the previous pairing
			    			 unPair(tent);
			    			 //now create pairing
			    			 createPairing(tent, nearByTrees);
			    		 } else {
			    			 //we also need to remove the existing pairing
			    			 unPair(tent);
			    			 
			    			 deleteTent(tent, next);
			    		 }
			    	 }
			     }
			}	
		}
		
		
		return next;
	}
	
	private Verifier simAnneal(int maxIterations, double initialTemp, double coolingRate) {
		//set up for simulated annealing
		Verifier currentSolution = gridToSolve;
		int currentCost = currentSolution.sumViolations();
		
		Verifier bestSolution = currentSolution;
		int bestCost = currentCost;
		
		double temperature = initialTemp;
		
		for(int i = 0; i < maxIterations; i++) {
			if(temperature <= 0) {
				break;
			}
			
			Verifier newSolution = modifyProblem(bestSolution);
			int newCost = newSolution.sumViolations();
			
			int costDifference = newCost - currentCost;
			
			if(costDifference < 0 || Math.exp(-costDifference / temperature) > Math.random()) {
				currentSolution = newSolution;
				currentCost = newCost;
			}
			
			if(currentCost < bestCost) {
				bestSolution = currentSolution;
				bestCost = currentCost;
			}
			
			temperature = temperature * coolingRate;
			
			printGrid();
			System.out.println("Total violations: " + newCost);
		}
		
		return bestSolution;
	}
}
