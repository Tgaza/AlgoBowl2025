/**
 * "NullPntrException"
 * @author Ty Gazaway
 * @author John Silva 
 * @author Thomas Dowd
 * 
 */
package main;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;

/**
 * <h3>Input Generator </h3>
 * 
 * procedurally generated 2 dimensional grid for the Tents and Trees problem
 * for CSCI 406. Creates grids with the following key: <br>
 * 
 * 'T' = <i>Tree</i> <br>
 * '.' = <i>AvailableLocation</i> <br>
 * '^' = <i>Tent</i> <br>
 * 
 * <br> Example File Location:
 * <br>gen#_RxC_(Density,Screw,Noise)_(Tents?)_(Special)_UniqueID_User.txt
 * <br>gen3_50x50_(0.15,0.05,0.30)_withTents_TEST_17403_tdowd.txt
 * <i>NOTE</i>: Changes may not be made in the documentation for the file generation.
 * 
 * <h3>InputGen Todo list: </h3>
 * - TODO: Double Check all outputs for viability <br>
 * - TODO: Remove all unused variables <br>
 * - FIXME: Remove Unused Imports, and Values <br>
 */
public class InputGenerator {
	// Tweakable Values
	private int rows;
	private int cols;
	private double solvDensity;
	private double screwoverDensity;
	private double inaccuracy;

	// Non-tweakable values
	private int[] rowTents;
	private int[] colTents;
	private GameGrid gameGrid;

	// Helper attributes/classes
	private Random rand = new Random();

	/**
	 * <b>Primary Entry point.</b>
	 * 
	 * <br>See Also: <br>
	 * {@link #generateInput()} <br>
	 * {@link #outputToFile(boolean)} <br>
	 * 
	 */
	public static void main(String[] args) {
		// ~ ~ ~ Adjustable Params ~ ~ ~ //
		
		// Creation Params
		final int ROWS = 30000;
		final int COLS = 3;
		final double SOLVABLE_DENSITY = 0.25;
		final double SCREWOVER_DENSITY = 0.10;
		final double INNACCURACY = 0.40;
		final boolean SHOW_TENTS = false;
		// Output Params 
		final boolean PRINT_TO_TERMINAL = false;
		// File Name Params
		final int INDICATOR_CURRENT_GEN = 4;
		final String INDICATOR_PARAM = "post"; // dunno what "PreParams and PostParams" mean
		final String SPECIAL_FLAG = null; // Can be null, or empty
		
		// ~ ~ ~ Begin Generating ~ ~ ~ //
		String fileID = createID(INDICATOR_CURRENT_GEN, ROWS, COLS, INDICATOR_PARAM, SHOW_TENTS,SOLVABLE_DENSITY, SCREWOVER_DENSITY,INNACCURACY, SPECIAL_FLAG);
		InputGenerator genny = new InputGenerator(ROWS, COLS, SOLVABLE_DENSITY, SCREWOVER_DENSITY, INNACCURACY);
		genny.generateInput();
		genny.outputToFile(SHOW_TENTS,fileID);
		if (PRINT_TO_TERMINAL) {
			//System.out.println("FileLocation:"+fileID+"\n"); // View Location of autogenerated files.
			genny.printGrid(SHOW_TENTS);
		}
		
	}
	
	/**
	 * <b>Default Constrctor for Input Generator.</b> <br>
	 * 
	 * @param rows Number of Rows
	 * @param cols Number of Columns
	 * @param solvDensity Value dictating if a tree should be placed
	 * @param screwoverDensity Value dictating if a tree or tent should be overwriten
	 * @param inaccuracy Value dictating general map noise
	 * 
	 */
	public InputGenerator(int rows, int cols, double solvDensity, double screwoverDensity, double inaccuracy) {
		this.rows = rows;
		this.cols = cols;
		this.solvDensity = solvDensity;
		this.screwoverDensity = screwoverDensity;
		this.inaccuracy = inaccuracy;
		this.rowTents = new int[rows];
		this.colTents = new int[cols];
		
		this.gameGrid = new GameGrid(rows, cols);
	}
	
	/**
	 * Iterates through the 2 Dimensional array, generates a spot for a space or a 
	 * tree for the whole graph, given an empty space. Determines <i>density</i> for given grid. 
	 * Uses <i>DENSITY</i>. Communicates/Creates new instances of {@link main.GameGrid} as well as {@link main.Cell} <br>
 	 * <br>
	 * See Also: <br>
	 * {@link #solvDensityCheck()} <br>
	 * {@link main.GameGrid#calculateTentTargets(Cell)} <br>
	 * 
	 */
	public void generateInput() {
		// generate spot for a tree or open space for the entire graph
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				Cell curCell = this.gameGrid.getCell(row, col);
				if (curCell.isTree() || curCell.isTent()) {
					continue;
				}
				// kind of the heuristic for determining the solvDensity of trees on the graph
				boolean place = solvDensityCheck();
				Cell pairedTent = pickTent(this.gameGrid.calculateTentTargets(curCell));
				if (place && pairedTent != null) {
					curCell.setSymbol('T');
					pairedTent.setSymbol('^');
					this.rowTents[pairedTent.getRow()] += 1;
					this.colTents[pairedTent.getCol()] += 1;
				}
			}
		}
	}
	
	/**
	 * Uses {@link #screwoverDensity} to determine if a grid entity will be overwritten <br>
	 * 
	 * <br>
	 * See Also:<br>
	 * {@link main.GameGrid#getCell(int, int)}<br>
	 * {@link #screwoverDensityCheck()} <br>
	 */
	public void parameterizeInput() {
		// Add in screwover trees at specified density
		for (int row = 0; row < this.rows; row++) {
			for (int col = 0; col < this.cols; col++) {
				Cell curCell = this.gameGrid.getCell(row, col);
				if (curCell.isTree()) {
					continue;
				}
				// kind of the heuristic for determining the solvDensity of trees on the graph
				boolean place = screwoverDensityCheck();
				if (place) {
					curCell.setSymbol('T');
				}
			}
		}

		// Modify row and col values by innaccuracy parrameter
		for (int row = 0; row < rows; row++) {
			this.rowTents[row] += Math.pow(-1, row % 2)
					* (int) (this.rowTents[row] * this.rand.nextDouble() * this.inaccuracy);
		}

		for (int col = 0; col < cols; col++) {
			this.colTents[col] += Math.pow(-1, col % 2)
					* (int) (this.colTents[col] * this.rand.nextDouble() * this.inaccuracy);
		}
	}

	/**
	 * Determines if the entity should be placed given that the random generator spits out below a given value: {@link #solvDensity}
	 * @return Boolean
	 */
	public boolean solvDensityCheck() {
		return this.rand.nextDouble() < this.solvDensity; // Returns true if a tree should be placed for the solvDensity
	}

	/**
	 * Determines if the entity should be overwritten {@link #screwoverDensity}
	 * @return Boolean
	 */
	public boolean screwoverDensityCheck() {
		return this.rand.nextDouble() < this.screwoverDensity; 
	}
	
	/**
	 * Determines a list of viable locations for tents to spawn. <br>
	 * 
	 * @param availableTentSpots List <{@link Cell}> 
	 * @return List <{@link Cell}>: Availiable tent locations <i>OR</i> NULL
	 */
	public Cell pickTent(List<Cell> availableTentSpots) {
		if (availableTentSpots.isEmpty()) {
			return null;
		}
		return availableTentSpots.get(this.rand.nextInt(0, availableTentSpots.size()));
	}
	
	/**
	 * Grid Visualizer. <b>FOR DEV USE ONLY</b>
	 * @param showTents If the Tents are to be shown when visualizing.
	 */
	public void printGrid(boolean showTents) {
		System.out.println(this.rows + " " + this.cols);
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
				if (!showTents && symbol.equals("^")) {
					symbol = ".";
				}
				System.out.print(symbol + " ");
			}
			System.out.println();
		}
	}

	/**
	 * Puts a generated grid in a given file location. <br><br>
	 * 
	 * - TODO: Create Unquie ID for Saved File <br>
	 * 
	 * @param showTents If the Tents are to be represented when saving output.
	 */
	public void outputToFile(boolean showTents, String fileName) {
		try (FileWriter writer = new FileWriter("data/generatedInputsUnverified/"+fileName)) { // Yes filename includes the ".txt"
			writer.write(this.rows + " " + this.cols + "\n");
			for (int row = 0; row < this.rows; row++) {
				writer.write(rowTents[row] + " ");
			}
			writer.write("\n");
			for (int col = 0; col < this.cols; col++) {
				writer.write(colTents[col] + " ");
			}
			writer.write("\n");
			// print out generated grid
			for (int row = 0; row < rows; row++) {
				for (int col = 0; col < cols; col++) {
					String symbol = this.gameGrid.getCell(row, col).toString();
					if (!showTents && symbol.equals("^")) {
						symbol = ".";
					}
					writer.write(symbol);
				}
				writer.write("\n");
			}
		} catch (Exception e) {
			System.out.println("failed to output to file, msg- " + e.getMessage());
		}
	}
	
	
	// ~ ~ ~ UNUSED ~ ~ ~ //
	/**
	 * Creates a unique ID for each created file <br>
	 * <b>UNUSED</b><br>
	 * eg:<br>
	 *  gen2_300x300_postparams_HasTents_(SpecialFlag?_)17189_tdowd.txt
	 * @param currentGen Current Generation, should be updated occasionally.
	 * @param row int
	 * @param col int
	 * @param postOrPre any string, comes before "params"
	 * @param hasTents Boolean
	 * @param specialIndicator Can be a string or null, Use null as standard practice. Is a special flag that does <i>not</i> need to be included. 
	 * @return
	 */
	public static String createID(int currentGen, int row, int col, String postOrPre, boolean hasTents, double dense, double screw, double noise, String specialIndicator) {    
		// Get User
		String User = getGitConfig("user.name").trim(); // Grab Username, If you are uncomforable with this, use
											     		// String User = "JaneDoe"
        if (User != null) {
            User = User.replace("-", "").trim();
        }
		// Get Other ID info
		String millis = String.valueOf(System.currentTimeMillis());
		millis = millis.substring(0, Math.min(5, millis.length()));
		String tup = String.format("(%.2f,%.2f,%.2f)", dense, screw, noise);
		String tempHaveTents = (hasTents == true) ? "withTents":"noTents";
		String specialFlag =  (specialIndicator == null) ? "":specialIndicator + "_";
		
		// gen<n>_<n>x<m>_<Post?Pre>params_HasTents_ID_User.txt
		// gen2_300x300_postparams_HasTents_(~,~,~)_(SpecialFlag?_)17189_tdowd.txt
		return "gen"+currentGen+"_"+row+"x"+col+"_"+tup+"_"+tempHaveTents+"_"+specialFlag+millis+"_"+User+".txt";
	}
	
	/**
	 * Attempts to grab local configs for file 
	 * @param configName Should <i>ONLY</i> be any intended bit of information: "user.name" or "user.email"
	 * @return 
	 */
	 public static String getGitConfig(String configName) {
	        try {
	            ProcessBuilder processBuilder = new ProcessBuilder("git", "config", "--get", configName);
	            processBuilder.redirectErrorStream(true);
	            Process process = processBuilder.start();

	            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
	                String result = reader.readLine();	                
	                return (result != null) ? result : "JaneDoe"; // Standard Output
	            }
	        } catch (IOException e) {
	        	System.out.println("Error fetching Git config: " + e.getMessage());
	            return "EvanSmith"; // Evan for Error Reading File
	        }
	    }
}
