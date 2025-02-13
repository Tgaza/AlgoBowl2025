
/*
 * Input Generator is a class that creates inputs for the 2025 algobowl problem using given total columns
 *  and rows, and outputs the generated input to a file located in a given destination.
 * 
 */

import java.io.FileWriter;

public class InputGenerator {
    private int totalRows;
    private int totalCols;
    private int[] rowValues;
    private int[] colValues;
    private FileWriter fw;
    private String outputPath;

    public InputGenerator(String outputPath) {
        this.outputPath = outputPath;
    }

    public void generateInput(int totalRows, int totalCols) {
        this.totalRows = totalRows;
        this.totalCols = totalCols;
    }

    public void printInput() {

    }

    public void writeInputToFile() {

    }
}
