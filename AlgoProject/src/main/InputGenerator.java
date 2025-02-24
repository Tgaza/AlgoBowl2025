/**
 * "NullPntrException"
 * @author Ty Gazaway
 * @author John Silva 
 * @author Thomas Dowd
 * 
 */
package main;


import java.util.Random;

/**
 * Procedurally Generates Inputs<br>
 * 
 * 'T' = <i>Tree</i> <br>
 * '.' = <i>AvailableLocation</i> <br>
 * '^' = <i>Tent</i> <br>
 * 
 * TODO: Double Check all outputs for viability <br>
 * TODO: Remove all unused variables <br>
 */
public class InputGenerator {
	// Tweakable Values
	private int rows = 15;
	private int columns = 35;
	private static final double DENSITY = 0.3;

	// Non-tweakable values
	private int[] rowTents = new int[rows];
	private int[] columnTents = new int[columns];
	private char[][] gameGrid = new char[rows][columns];
	private int[] treeRowCount = new int[rows];
	private int[] treeColCount = new int[columns];

	public Random rand = new Random();

	public static boolean densityCheck(int rows2, int columns2) {
		Random random = new Random();
		boolean densityCheck = random.nextDouble() < DENSITY;
		return densityCheck; // Returns true if a tree should be placed
	}
	
	public char[] availableTentSpots(int row, int col) {
		boolean checkConditions = true; 
		boolean checkEdgeCases = true;
		char[] availableSpots = new char[] {'X','X','X','X'};
		//Check for conditions where placing a tent would create an unavoidable violation
		//Also check for edge cases where a tree shouldn't be placed
		if(row-1 > 0 && gameGrid[row-1][col] == '.') {
			// FIXME: Whats supposed to go here???
		}
		return availableSpots; // Returns true if a tree should be placed
	}

	public InputGenerator() {
		//generate spot for a tree or open space for the entire graph
		for(int row = 0; row < rows; row++) {
			for(int column = 0; column < columns; column++) {
				//kind of the heuristic for determining the density of trees on the graph
				boolean place = densityCheck(rows, columns);
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

		//check and make sure that no trees are completed locked in by other trees as that forces a violation
		// ^^ Maybe we try to make an implementation where we DO have a "Locked In" Tree situation? -TDowd
		for(int row = 0; row < rows; row++) {
			for(int col = 0; col < columns; col++) {
				int blockCount = 0;
				if(row-1 > 0 && gameGrid[row][col] == 'T') {
					blockCount++;
				} else if(row+1 < rows && gameGrid[row][col] == 'T') {
					blockCount++;
				} else if(col-1 > 0 && gameGrid[row][col] == 'T') {
					blockCount++;
				} else if(col+1 < rows && gameGrid[row][col] == 'T') {
					blockCount++;
				}

				if(blockCount == 4) {
					treeRowCount[row]--;
					treeColCount[col]--;
					gameGrid[row][col] = '.';
				}
			}
		}

		//print out the rows and columns of the gameGrid
		System.out.println(rows + " " + columns);

		//generate number of tents for the row and columns
		for(int i = 0; i < rows; i++) {
			int maxTents = Math.max(0, rows - treeRowCount[i]); 			 // Ensure non-negative value 
			int randomInt = (maxTents > 0) ? rand.nextInt(maxTents + 1) : 0; // Allow 0 as a possibility
			rowTents[i] = randomInt;
			System.out.print(randomInt + " ");
		}

		//print out a newline to separate the rows and columns
		System.out.println();

		for(int i = 0; i < columns; i++) {
			int randomInt = rand.nextInt(rows - treeColCount[i]);
			columnTents[i] = randomInt;
			System.out.print(randomInt + " ");
		}

		System.out.println();

		//print out generated grid
		for(int row = 0; row < rows; row++) {
			for(int column = 0; column < columns; column++) {
				System.out.print(gameGrid[row][column] + " ");
			}
			System.out.println();
		}

	}

	public static void main(String[] args) {
		new InputGenerator();
	}
}
