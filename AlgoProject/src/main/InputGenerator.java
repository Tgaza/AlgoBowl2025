/**
 * "NullPntrException"
 * @author Ty Gazaway
 * @author John Silva 
 * @author Thomas Dowd
 * 
 */
package main;

import java.util.Random;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Creates procedurally inputs for Tents.
 * TODO: Double Check all outputs for viability.
 */
public class InputGenerator {
	// Tweakable Values
	//15 by 35
	private int rows = 300;
	private int columns = 300;
//	private static final double LAMBDA = 0.0005;

	// Non-tweakable values
	private int[] rowTents = new int[rows];
	private int[] columnTents = new int[columns];
	private char[][] gameGrid = new char[rows][columns];
	private int[] treeRowCount = new int[rows];
	private int[] treeColCount = new int[columns];

	public Random rand = new Random();
	public FileWriter writer;

	public static boolean shouldPlaceTree(int n, int m) {
//		int numTiles = n * m;
		double probability = 0.15;
		Random random = new Random();
		return random.nextDouble() < probability; // Returns true if a tree should be placed
	}
	
	private void treeLocationGen() {
		//generate spot for a tree or open space for the entire graph
		for(int row = 0; row < rows; row++) {
			for(int column = 0; column < columns; column++) {
				//kind of the heuristic for determining the density of trees on the graph
				boolean place = shouldPlaceTree(rows, columns);
				if(place) {
					gameGrid[row][column] = 'T';
					treeRowCount[row]++;
					treeColCount[column]++;
				} else {
					gameGrid[row][column] = '.';
				}
			}
			System.out.println();
		}
	}
	
	private void rmBoxedTrees() {
		//check and make sure that no trees are completed locked in by other trees as that forces a violation
		// ^^ Maybe we try to make an implementation where we DO have a "Locked In" Tree situation? -TDowd
		for(int row = 0; row < rows; row++) {
			for(int col = 0; col < columns; col++) {
				//track the number of violations around a single tree
				int blockCount = 0;
				
				//check if neighboring trees box in a specified tree
				if(row-1 > 0 && gameGrid[row][col] == 'T') {
					blockCount++;
				} else if(row+1 < rows && gameGrid[row][col] == 'T') {
					blockCount++;
				} else if(col-1 > 0 && gameGrid[row][col] == 'T') {
					blockCount++;
				} else if(col+1 < rows && gameGrid[row][col] == 'T') {
					blockCount++;
				}
				
				//if the tree is boxed in, remove it from the grid, and adjust tree column and row count values
				if(blockCount == 4) {
					treeRowCount[row]--;
					treeColCount[col]--;
					gameGrid[row][col] = '.';
				}
			}
		}
	}
	
	private void TentCntGen(int upperBound, int[] lowerBound, int[] tentAxis, int axisCount) {
		//generate the number of tents for the row and columns
		for(int i = 0; i < axisCount; i++) {
			int maxTents = Math.max(0, upperBound - lowerBound[i]); 			 // Ensure non-negative value 
			int randomInt = (maxTents > 0) ? rand.nextInt(maxTents + 1) : 0; // Allow 0 as a possibility
			tentAxis[i] = randomInt;
			System.out.print(randomInt + " ");
			
			try {
				writer.write(randomInt + " ");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void printGeneratedGrid() throws IOException {
		for(int row = 0; row < rows; row++) {
			for(int column = 0; column < columns; column++) {
				System.out.print(gameGrid[row][column] + " ");
				writer.write(gameGrid[row][column]);
			}
			System.out.println();
			writer.write("\n");
		}
	}

	public InputGenerator() throws IOException {
		//create output file
		writer = new FileWriter("InputGen/output.txt");
		
		//Generate tree locations
		treeLocationGen();
		
		//Remove trees that have been boxed in by other trees
		rmBoxedTrees();
		
		//print out the rows and columns of the gameGrid
		System.out.println(rows + " " + columns);
		writer.write(rows + " " + columns + "\n");
		
		//Generate the number of tents to be placed in each row
		TentCntGen(columns, treeRowCount, rowTents, rows);

		//print out a newline to separate the rows and columns
		System.out.println();
		writer.write("\n");
		
		//Generate the number of tents to be placed in each column
		TentCntGen(rows, treeColCount, columnTents, columns);

		System.out.println();
		writer.write("\n");

		//print out generated grid
		printGeneratedGrid();
		
		//close writer
		writer.close();
	}

	public static void main(String[] args) throws IOException {
		new InputGenerator();
	}
}

