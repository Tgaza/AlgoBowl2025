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


/**
 * -TODO: Create new Offical Folder to pull solutions/Inputfiles from
 * -TODO: Ensure one indexed output !!!
 * 
 * 
 * -TODO: Incorect num of violations <- INVALID
 * -TODO: Tent Superposition (Tent cannot overlap with existing entity) <- INVALID
 * -TODO: Cant Fall off edge of world <- INVALID
 * -TODO: Tent cannot be paired with non-tree entity <-INVALID
 * -TODO: File must have proper formatting + No missing/corrupt data <- INVALID
 * 
 * 
 * -TODO: Cant be an unpaired entity <- Violation
 * -TODO: Multiple adjenecies != multiple violations (Specifially with Tents) <- Violation
 * -TODO: A row or column which has too many or too few tents causes multiple violations: one violation for each tent to many or too few<- Violation 
 * 
 * When complete, "todo" -> "fixme"
 */
public class Verifier {
	private GameGrid grid;
	private Scanner scanner;
	
	private String inputFile;
	private String outputFile;
	
	private int rows;
	private int columns;
	
	private int[] tentRowCount;
	private int[] tentColumnCount;
	
	private int[] desiredTentRowCount;
	private int[] desiredTentColumnCount;
	
	private int claimedViolations;
	private int claimedNumTentsPlaced;
	
	private int actualTentCount = 0;
	
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
		int rowViolations = checkRowViolations();
		int columnViolations = checkColumnViolations();
		
		//check how many violations are caused by adjacent tents
		int adjViolations = checkAdjViolations();
		
		//check how many violations there are in terms of pairing
		int pairViolations = checkPairViolations();
		
		//Total number of violations made
		int totalViolations = rowViolations + columnViolations + adjViolations + pairViolations;
		
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
	
	//These functions are used for debugging purposes
	//Prints out a copy of the input file
//	private void printGrid() {
//		for(int r = 0; r < rows; r++) {
//			System.out.print(desiredTentRowCount[r] + " ");
//		}
//		
//		System.out.println();
//		
//		for(int c = 0; c < columns; c++) {
//			System.out.print(desiredTentColumnCount[c] + " ");
//		}
//		
//		System.out.println();
//		
//		for(int r = 0; r < rows; r++) {
//			for(int c = 0; c < columns; c++) {
//				System.out.print(grid.getCell(r, c).getSymbol());
//			}
//			System.out.println();
//		}
//		
//		System.out.println();
//	}
	
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
	private int checkRowViolations() {
		int violations = 0;
		for(int r = 0; r < rows; r++) {
			if(desiredTentRowCount[r] != tentRowCount[r]) {
				violations = violations + Math.abs(desiredTentRowCount[r] - tentRowCount[r]);
			}
		}
		return violations;
	}
	
	//counts the number of column violations
	private int checkColumnViolations() {
		int violations = 0;
		for(int c = 0; c < columns; c++) {
			if(desiredTentColumnCount[c] != tentColumnCount[c]) {
				violations = violations + Math.abs(desiredTentColumnCount[c] - tentColumnCount[c]);
			}
		}
		return violations;
	}
	
	//checks the number of Adjacent Violations
	private int checkAdjViolations() {
		int violations = 0;
		for(int r = 0; r < rows; r++) {
			for(int c = 0; c < columns; c++) {
				Cell currCell = grid.getCell(r, c);
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
	private int checkPairViolations() {
		int violations = 0;
		for(int r = 0; r < rows; r++) {
			for(int c = 0; c < columns; c++) {
				Cell cell = grid.getCell(r, c);
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
	private void buildBaseGrid() {
		try {
	        scanner = new Scanner(new File(inputFile)); // Corrected Scanner initialization
	    } catch (FileNotFoundException e) {
	        System.err.println("Error: File not found - " + inputFile);
	        return;
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
					grid.getCell(r, c).setSymbol(rowElements[0].charAt(c));
				}
			}
		}
		
		//Function is used for testing
		//printGrid();
		scanner.close();
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
		tentRowCount = new int[rows];
		tentColumnCount = new int[columns];
		
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
				tentRowCount[r]++;
				tentColumnCount[c]++;
				
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
		//printGrid();
		//printTentCounts();
		
	}
	
	//builds the grid for the verifier
	public void readInput() {
		buildBaseGrid();
		buildGridWithTents();
	}

	
}
