/**
 * {InsertDescription}
 * "{NullpointerException}"
 * @author Ty Gazaway
 * @author John Silva 
 * @author Thomas Dowd
 * 
 */
package main;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * 
 */
public class InputGenerator {
    private int numRows, numCols;
    private int[] rowNumbers, colNumbers;
    private TileCell[][] board;

    public InputGenerator() {
        this.numRows = -1;
        this.numCols = -1;
        this.rowNumbers = null;
        this.colNumbers = null;
        this.board = null;
    }

    public void generateInput() {
        if (this.numRows <= 0 || this.numCols <= 0) {
            System.out.println("Improperly set parameters");
            return;
        }
        this.rowNumbers = new int[this.numRows];
        this.colNumbers = new int[this.numCols];
        this.board = new TileCell[this.numRows][this.numCols];
        for(int row = 0; row < this.numRows; row++){
            for(int col = 0; col < this.numCols; col++){
                this.board[row][col] = new TileCell(row, col, 'b');
            }
        }
        Random rand = new Random();
        Set<TileCell> visited = new HashSet<TileCell>();
        int maxIterations = 1000;
        int curIteration = 0;
        while(maxIterations > curIteration){
            int row = rand.nextInt(this.numRows);
            int col = rand.nextInt(this.numCols);
        }
    }

    public void displayInput() {

    }

    public void setRows(int numRows) {
        this.numRows = numRows;
    }

    public void setCols(int numCols) {
        this.numCols = numCols;
    }
}
