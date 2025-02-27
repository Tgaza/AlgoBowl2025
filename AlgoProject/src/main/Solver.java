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
//import java.util.List;
/**
 * -TODO: Solver
 */
public class Solver {	
	Verifier verifier;
	GameGrid grid;
	
	private double coolingRate;
	private double initialTemp;
	private int maxIterations;
	
	public static void main(String[] args) {
		new Solver();
	}
	
	public Solver() {
		super();
		
		this.verifier = new Verifier();
		this.grid = verifier.buildBaseGrid("data/testingInputs/test15x15_1.txt");
		
		int numCells = grid.getNumCells();
		
		if(numCells > 10000) {
			this.initialTemp = 5000;
		} else if(numCells > 1000) {
			this.initialTemp = 500;
		} else {
			this.initialTemp = 50;
		}
		
		this.maxIterations = 100*numCells;
		this.coolingRate = 0.99;
		
		//randomly fill grid with tents before starting annealing
//		randomlyFillGrid();
		printGrid(grid);
		System.out.println();
		
		GameGrid solution = simAnneal(maxIterations, initialTemp, coolingRate);
		
		printGrid(solution);
		System.out.println("Total violations: " + verifier.sumViolations(solution));
	}
	
	//for debugging purposes
	private void printGrid(GameGrid g) {		
		for(int r = 0; r < g.getRows(); r++) {
			for(int c = 0; c < g.getCols(); c++) {
				System.out.print(g.getCell(r, c).getSymbol() + " ");
			}
			System.out.println();
		}
	}
	
	
	
	//randomly fill the grid
//	private void randomlyFillGrid() {
//		for(int r = 0; r < grid.getRows(); r++) {
//			for(int c = 0; c < grid.getCols(); c++) {
//				Cell currCell = grid.getCell(r, c);
//				char symbol = currCell.getSymbol();
//				
//				if(symbol == '.') {
//					Random rand = new Random();
//					if(rand.nextDouble() < 0.15) {
//						currCell.setSymbol('^');
//						grid.rmEmpty(currCell);
//						grid.addTentRowCol(r, c);
//					}
//				}
//			}
//		}
//	}
	

	private void deleteTent(GameGrid g, Cell cell) {
		g.rmTentRowCol(cell);
		g.addEmpty(cell);
	}
	
	private void unPair(Cell cell, Cell neighbor) {
   	 	cell.getPairedCells().removeFirst();
   	 	neighbor.getPairedCells().removeFirst();
	}
	
	//creates pairing from list of available cells
	private void createPairing(Cell cell, Cell neighbor) {
		 cell.addPairedCell(neighbor);
		 neighbor.addPairedCell(cell);
	}
	
	private int genRandom(int upperBound, int lowerBound) {
		Random rand = new Random();
		return rand.nextInt(upperBound) + lowerBound;
	}
	
	
	private void removeCell(GameGrid g) {
		Iterator<Cell> iterator = g.getTents().iterator();
		Cell cell = iterator.next();
		
		if(cell.getPairedCells().size() != 0) {
			Cell neighbor = cell.getPairedCells().getFirst();
			unPair(cell, neighbor);
		}
		
		deleteTent(g, cell);
	}
	
	private void addCell(GameGrid g) {
		//select a random empty cell
		Iterator<Cell> iterator = g.getEmpty().iterator();
		Cell cell = iterator.next();
		//check to see if the cell has neighboring trees, if it doesn't just add it, if it does, pair it to a random tree
		Set<Cell> neighboringTrees = new HashSet<>();
		for(Cell neighbor : cell.getCardinalAdjList()) {
			if(neighbor.getSymbol() == 'T') {
				neighboringTrees.add(neighbor);
			}
		}
		
		if(neighboringTrees.size() == 0) {
			g.addTentRowCol(cell);
			g.rmEmpty(cell);
		} else {
			iterator = neighboringTrees.iterator();
			Cell neighbor = iterator.next();
			g.addTentRowCol(cell);
			g.rmEmpty(cell);
			createPairing(cell, neighbor);
		}
	}
	
	private GameGrid modifyProblem(GameGrid g) {
	    GameGrid next = new GameGrid(g); // Use the copy constructor
	    int random = genRandom(2, 0);

	    if (random == 0) {
	        if (next.getTents().size() != 0) {
	            removeCell(next);
	        } else {
	            addCell(next);
	        }
	    } else {
	        if (next.getEmpty().size() != 0) {
	            addCell(next);
	        } else {
	            removeCell(next);
	        }
	    }

	    return next;
	}

	private GameGrid simAnneal(int maxIterations, double initialTemp, double coolingRate) {
	    GameGrid currentSolution = new GameGrid(grid); // Copy the initial grid
	    int currentCost = verifier.sumViolations(currentSolution);

	    GameGrid bestSolution = new GameGrid(currentSolution);
	    int bestCost = currentCost;

	    double temperature = initialTemp;

	    for (int i = 0; i < maxIterations; i++) {
	        if (temperature <= 0) {
	            break;
	        }

	        GameGrid newSolution = modifyProblem(currentSolution);
	        int newCost = verifier.sumViolations(newSolution);

	        int costDifference = newCost - currentCost;

	        if (costDifference < 0 || Math.exp(-costDifference / temperature) > Math.random()) {
	            currentSolution = new GameGrid(newSolution);
	            currentCost = newCost;
	        }

	        if (currentCost < bestCost) {
	            bestSolution = new GameGrid(currentSolution);
	            bestCost = currentCost;
	        }

	        temperature *= coolingRate;

	        printGrid(currentSolution);
	        System.out.println("Total violations: " + newCost);
	    }

	    return bestSolution;
	}

}
