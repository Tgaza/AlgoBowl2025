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
 * Creates procedurally inputs for Tents. TODO: Double Check all outputs for
 * viability.
 */
public class InputGenerator {
	// Tweakable Values
	private int rows;
	private int cols;
	private double density;

	// Non-tweakable values
	private int[] rowTents;
	private int[] colTents;
	private GameGrid gameGrid;

	// Helper attributes/classes
	private Random rand = new Random();

	public static void main(String[] args) {
		final int ROWS = 300;
		final int COLS = 300;
		final double DENSITY = 0.15;
		InputGenerator genny = new InputGenerator(ROWS, COLS, DENSITY);
		genny.generateInput();
		genny.outputToFile();
		//genny.printGrid();
	}

	public InputGenerator(int rows, int cols, double density) {
		this.rows = rows;
		this.cols = cols;
		this.density = density;
		this.rowTents = new int[rows];
		this.colTents = new int[cols];
		this.gameGrid = new GameGrid(rows, cols);
	}

	public void generateInput() {
		// generate spot for a tree or open space for the entire graph
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				Cell curCell = this.gameGrid.getCell(row, col);
				if (curCell.isTree() || curCell.isTent()) {
					continue;
				}
				// kind of the heuristic for determining the density of trees on the graph
				boolean place = densityCheck();
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

	public boolean densityCheck() {
		return this.rand.nextDouble() < this.density; // Returns true if a tree should be placed for the density
	}

	public Cell pickTent(List<Cell> availableTentSpots) {
		if (availableTentSpots.isEmpty()) {
			return null;
		}
		return availableTentSpots.get(this.rand.nextInt(0, availableTentSpots.size()));
	}

	public void printGrid() {
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
				System.out.print(this.gameGrid.getCell(row, col) + " ");
			}
			System.out.println();
		}
	}

	public void outputToFile() {
		try(FileWriter writer = new FileWriter("data/generatedInputsUnverified/gen1.txt")) {
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
					writer.write(this.gameGrid.getCell(row, col) + " ");
				}
				writer.write("\n");
			}
		} catch (Exception e) {
			System.out.println("failed to output to file, msg- " + e.getMessage());
		}
	}
}
