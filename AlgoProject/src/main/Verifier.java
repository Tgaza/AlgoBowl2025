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
import java.util.Scanner;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Verifier {
	private GameGrid grid;
	private Scanner scanner;
	
	private String inputFile;
	private String outputFile;
	
	private int rows;
	private int columns;
	
	private int[] desiredTentRowCount;
	private int[] desiredTentColumnCount;
	
	private int claimedViolations;
	private int claimedNumTentsPlaced;
	
	private int actualTentCount = 0;
	
	private int totalViolations = 0;
	
	
	
	public static void main(String[] args) {
		//this is some temporary bull shit just to get the program to run with files plugged in manually
		//replace with args[0] and args[1] later
//		new Verifier("officialInputs/input_group1001.txt", "officialOutputs/output_group1001_NullPntrException.txt");
		boolean verifAll = true;
		if (verifAll)
			for (int i = 963; i < 1025; i++) {
				if (i != 1020) {
				//			
				try {
//					String outputFileName = "fileToBeVerified/output_from_991_to_"+i+".txt";
					String outputFileName = "officialOutputs/output_group"+i+"_attempt.txt";
					String inputFileName = "officialInputs/input_group" + i + ".txt";
					
				
					new Verifier(inputFileName, outputFileName);
					
					
					
					
				} catch (Exception e) {
					System.out.println("Skipped File");
				}}
		}
	}
	
	public Verifier() {
		super();
	}

	
	//Main code for running the verifier
	public Verifier(String inputFile, String outputFile) {
		super();
		//for the time being the arguments are being left out intentionally

		this.inputFile = "data/" + inputFile;
		this.outputFile = "data/" + outputFile;

		readInput();
		
		//check to make sure the number of tents match
		checkTentCount();
		
		//check how many violations there are per row and column
		
		
		//Total number of violations made
		totalViolations = sumViolations(grid);
		
		//just for testing purposes
//		System.out.println(rowViolations);
//		System.out.println(columnViolations);
//		System.out.println(adjViolations);
//		System.out.println(pairViolations);
//		System.out.println(totalViolations);
		
		
		if(totalViolations != claimedViolations) {
//			System.out.print("incorrect Violation count "+outputFile+"- ");
			exitProgram();
		} else {
			System.out.println("Valid Output File");
		}
		
	}
	
	public int sumViolations(GameGrid g) {
		int rowViolations = checkRowViolations(g);
		
		int columnViolations = checkColumnViolations(g);
		
		//check how many violations are caused by adjacent tents
		int adjViolations = checkAdjViolations(g);
		
		//check how many violations there are in terms of pairing
		int pairViolations = checkPairViolations(g);
		
//		System.out.println(rowViolations);
//		System.out.println(columnViolations);
//		System.out.println(adjViolations);
//		System.out.println(pairViolations);
		
		return rowViolations + columnViolations + adjViolations + pairViolations;
	}
	
//	These functions are used for debugging purposes
//	Prints out a copy of the input file
	private void printGrid() {
		for(int r = 0; r < rows; r++) {
			System.out.print(desiredTentRowCount[r] + " ");
		}
		
		System.out.println();
		
		for(int c = 0; c < columns; c++) {
			System.out.print(desiredTentColumnCount[c] + " ");
		}
		
		System.out.println();
		
		for(int r = 0; r < rows; r++) {
			for(int c = 0; c < columns; c++) {
				System.out.print(grid.getCell(r, c).getSymbol());
			}
			System.out.println();
		}
		
		System.out.println();
	}
	
	//Prints out a list of the detected counts of tents for each set of rows and columns
//	private void printTentCounts() {
//		for(int r = 0; r < rows; r++) {
//			System.out.print(tentRowCount[r] + " ");
//		}
//		
//		System.out.println();
//		
//		for(int c = 0; c < columns; c++) {
//			System.out.print(tentColumnCount[c] + " ");
//		}
//		
//		System.out.println();
//	}
	
	//If an issue with the output file is found, the program exits with printed statement
	private void exitProgram() {
		System.out.println("Invalid Output File");
//		System.exit(1);
		
	}
	
	//checks the number of counted tents
	private void checkTentCount() {
		if(actualTentCount != claimedNumTentsPlaced) {
			System.out.print("incorrect tent count - ");
			exitProgram();
		}
	}
	
	//checks the number of row violations
	private int checkRowViolations(GameGrid g) {
		int violations = 0;
		for(int r = 0; r < g.getRows(); r++) {
			if(desiredTentRowCount[r] != g.getTentRowCount()[r]) {
				violations = violations + Math.abs(desiredTentRowCount[r] - g.getTentRowCount()[r]);
			}
		}
		return violations;
	}
	
	//counts the number of column violations
	private int checkColumnViolations(GameGrid g) {
		int violations = 0;
		for(int c = 0; c < g.getCols(); c++) {
			if(desiredTentColumnCount[c] != g.getTentColumnCount()[c]) {
				violations = violations + Math.abs(desiredTentColumnCount[c] - g.getTentColumnCount()[c]);
			}
		}
		return violations;
	}
	
	//checks the number of Adjacent Violations
	private int checkAdjViolations(GameGrid g) {
		int violations = 0;
		for(int r = 0; r < g.getRows(); r++) {
			for(int c = 0; c < g.getCols(); c++) {
				Cell currCell = g.getCell(r, c);
				char cellSymbol = currCell.getSymbol();
				
				if(cellSymbol == '^') {
					List<Cell> diagAdjList = currCell.getDiagAdjList();
					List<Cell> cardinalAdjList = currCell.getCardinalAdjList();
					
					Boolean adjDetected = false;
					
					for(Cell cell : diagAdjList) {
						if(cell.getSymbol() == '^') {
							violations++;
							adjDetected = true;
							break;
						}
					}
					
					if(adjDetected == false) {
						for(Cell cell : cardinalAdjList) {
							if(cell.getSymbol() == '^') {
								violations++;
								break;
							}
						}
					}
					
				}

			}
		}
		
		return violations;
	}
	
	//checks the number of Pairing Violations
	private int checkPairViolations(GameGrid g) {
		int violations = 0;
		for(int r = 0; r < g.getRows(); r++) {
			for(int c = 0; c < g.getCols(); c++) {
				Cell cell = g.getCell(r, c);
				if(cell.getSymbol() != '.') {
					List<Cell> cellPairs = cell.getPairedCells();
					if(cellPairs.size() > 1) {
						System.out.print("cell paired with multiple cells - ");
						exitProgram();
					}
					
					if(cellPairs.size() < 1) {
						violations++;
					}
				}
			}
		}
		
		return violations;
	}
	
	//splits the line read from the file into it's individual elements
	private String[] getElementsFromLine() {
		String line = scanner.nextLine();
		String[] elements = line.split(" ");
		return elements;
	}
	
	private int[] stringToInt(String[] input) {
		int[] output = new int[input.length];
		for(int index = 0; index < input.length; index++) {
			output[index] = Integer.parseInt(input[index]);
		}
		
		return output;
	}
	
	//reads in the grid information of the input file and builds the grid
	public GameGrid buildBaseGrid(String path) {
		try {
	        scanner = new Scanner(new File(path)); // Corrected Scanner initialization
	    } catch (FileNotFoundException e) {
	        System.err.println("Error: File not found - " + path);
	        exitProgram();
	    }
		
		//Get grid dimensions
		int[] gridDimensions = stringToInt(getElementsFromLine());
		rows = gridDimensions[0];
		columns = gridDimensions[1];
		grid = new GameGrid(rows, columns);
		
		//Get tent count of each row element
		desiredTentRowCount = stringToInt(getElementsFromLine());
		
		//Get tent count of each column element
		desiredTentColumnCount = stringToInt(getElementsFromLine());
		
		//Read remaining input lines to build Grid
		//Also counts the number of tree's associated with each row and column
		while(scanner.hasNextLine()) {
			for(int r = 0; r < rows; r++) {
				String[] rowElements = getElementsFromLine();
				for(int c = 0; c < columns; c++) {
					//This is hella scuffed but it just sets the symbol properly
					char symbol = rowElements[0].charAt(c);
					grid.getCell(r, c).setSymbol(symbol);
					
					if(symbol == 'T') {
						grid.addTree(grid.getCell(r, c));
					}
					if(symbol == '.') {
						grid.addEmpty(grid.getCell(r, c));
					}
				}
			}
		}
		
		//Function is used for testing
		//printGrid();
		scanner.close();
		
		return grid;
	}
	
	//checks to make sure the correct number of arguments are provided from the output file
	private void checkFormatting(int requirement, int elementCount) {
		if(elementCount > requirement || elementCount < requirement) {
			System.out.print("incorrect file formatting - ");
			exitProgram();
		}
	}
	
	//include out of bounds detection function here
	private void outOfBoundsCheck(int x, int y) {
		if(x < 0) {
			System.out.print("tent or paired tree out of bounds - ");
			exitProgram();
		}
		if(x > rows) {
			System.out.print("tent or paired tree out of bounds - ");
			exitProgram();
		}
		if(y < 0) {
			System.out.print("tent or paired tree out of bounds - ");
			exitProgram();
		}
		if(y > columns) {
			System.out.print("tent or paired tree out of bounds - ");
			exitProgram();
		}
	}
	
	//include direction character validation
	private void directionCharValidation(char c) {
		if (c != 'X' && c != 'U' && c != 'R' && c != 'D' && c != 'L') {
			System.out.print("invalid char present - ");
		    exitProgram();
		}

	}
	
	//creates pairs between tents associated with a specific tree and vice versa
	private void createCellPairs(Cell cell, char direction) {
		if(direction == 'X') {
			return;
		} else {
			int treeR = cell.getRow();
			int treeC = cell.getCol();
			
			if (direction == 'U') {
				treeR--;
			} else if (direction == 'R') {
				treeC++;
			} else if (direction == 'D') {
				treeR++;
			} else {
				treeC--;
			}
			
			//Used for debugging
			//System.out.println("Tree cords: " + treeR + " " + treeC);
			
			//check that tree coordinates are within bounds
			outOfBoundsCheck(treeR, treeC);
			
			//create pair from tent to tree
			cell.addPairedCell(grid.getCell(treeR, treeC));
			//create pair from tree to tent
			grid.getCell(treeR, treeC).addPairedCell(cell);
		}
	}
	
	//adds tents to the existing base grid
	private void placeTents() {
		Set<Cell> tentSet = new HashSet<>();
		
		//initialize the size of the tent row and tree counts
//		tentRowCount = new int[rows];
//		tentColumnCount = new int[columns];
		
		//loop through the lines that contain the coordinate 
		while(scanner.hasNextLine()) {
			String[] elements = getElementsFromLine();
			
			//checks to make sure that the number of elements provided is correct
			checkFormatting(3, elements.length);
			
			//add code to throw an error if the types don't match
			int r = 0, c = 0;
			r = Integer.parseInt(elements[0])-1;
			c = Integer.parseInt(elements[1])-1;
			char direction = elements[2].charAt(0);
			
			//quick tests to verify cell coordinates and direction character
			outOfBoundsCheck(r, c);
			directionCharValidation(direction);
			
			Cell currCell = grid.getCell(r, c);
			
			if(!tentSet.add(currCell)) {
				System.out.print("duplicate tents - ");
				exitProgram();
			}
			
			char currCellChar = currCell.getSymbol();
			if(currCellChar == '.') {
				currCell.setSymbol('^');
				
				//adjust row and column tent counts
				grid.addTentRowCol(currCell);

				//increase the number of tents
				actualTentCount++;
				
				createCellPairs(currCell, direction);
				
				
			} else {
				System.out.print("tent placed ontop of another nonempty cell - ");
				exitProgram();
			}
		}
	}
	
	//Reads in necessary data to begin the tent placing process and building the completed grid with tents
	private void buildGridWithTents() {
		try {
			scanner = new Scanner(new File(outputFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//retrieve the claimed number of violations from the output file
		String[] elements = getElementsFromLine();
		checkFormatting(1, elements.length);
		claimedViolations = stringToInt(elements)[0];
		
		//retrieve the claimed number of tents placed from the output file
		elements = getElementsFromLine();
		checkFormatting(1, elements.length);
		claimedNumTentsPlaced = stringToInt(elements)[0];
		
		//go line by line reading tent placement coordinates and the direction of the associated tree
		placeTents();
		
		//Methods used for testing
		printGrid();
		//printTentCounts();
		
	}
	
	//builds the grid for the verifier
	public void readInput() {
		buildBaseGrid(inputFile);
		buildGridWithTents();
	}

	public GameGrid getGrid() {
		return grid;
	}

	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public int getTotalViolations() {
		return totalViolations;
	}

	
	
}
