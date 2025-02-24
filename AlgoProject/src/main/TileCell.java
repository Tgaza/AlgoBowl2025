package main;

public class TileCell {
    private int row, col;
    private char symbol;

    public TileCell(){
        
    }

    public TileCell(int row, int col, char symbol){
        this.row = row;
        this.col = col;
        this.symbol = symbol;
    }

    public int getRow(){
        return this.row;
    }

    public int getCol(){
        return this.col;
    }

    public char getSymbol(){
        return this.symbol;
    }
}
