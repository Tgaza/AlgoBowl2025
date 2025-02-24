/**
 * "NullPntrException"
 * @author Ty Gazaway
 * @author John Silva 
 * @author Thomas Dowd
 * 
 */
package main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
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


	public static void main(String[] args) {
		final int ROWS = 300;
		final int COLS = 300;
		final double SOLVABLE_DENSITY = 0.15;
		final double SCREWOVER_DENSITY = 0.05;
		final double INNACCURACY = 0.30;
		final boolean SHOW_TENTS = true;
		InputGenerator genny = new InputGenerator(ROWS, COLS, SOLVABLE_DENSITY, SCREWOVER_DENSITY, INNACCURACY);
		genny.generateInput();
		genny.outputToFile(SHOW_TENTS);
		//genny.printGrid(SHOW_TENTS);
	}

	public InputGenerator(int rows, int cols, double solvDensity, double screwoverDensity, double inaccuracy) {
		this.rows = rows;
		this.cols = cols;
		this.solvDensity = solvDensity;
		this.screwoverDensity = screwoverDensity;
		this.inaccuracy = inaccuracy;
		this.rowTents = new int[rows];
		this.colTents = new int[cols];
	}

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

	public void parameterizeInput() {
		// Add in screwover trees at specified density
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
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

	public boolean solvDensityCheck() {
		return this.rand.nextDouble() < this.solvDensity; // Returns true if a tree should be placed for the solvDensity
	}

	public boolean screwoverDensityCheck() {
		return this.rand.nextDouble() < this.screwoverDensity; // Returns true if a tree should be placed for the
																// solvDensity
	}

	public Cell pickTent(List<Cell> availableTentSpots) {
		if (availableTentSpots.isEmpty()) {
			return null;
		}
		return availableTentSpots.get(this.rand.nextInt(0, availableTentSpots.size()));
	}

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

	public void outputToFile(boolean showTents) {
		try (FileWriter writer = new FileWriter("data/generatedInputsUnverified/gen2_postparams_withTents.txt")) {
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
					writer.write(symbol + " ");
				}
				writer.write("\n");
			}
		} catch (Exception e) {
			System.out.println("failed to output to file, msg- " + e.getMessage());
		}
	}
}
