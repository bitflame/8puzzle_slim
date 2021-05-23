package org.example;

import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;


public class Board {
    private final int[][] tiles;
    private final int n;
    private int blankRow;
    private int blankCol;
    private final int hamming;
    private final int manhattan;

    public Board(int[][] tiles) {
        if (tiles == null) {
            throw new IllegalArgumentException("The board you are submitting is empty.");
        }
        this.n = tiles[0].length;
        if (this.n < 2 || this.n > 128) {
            throw new IllegalArgumentException("The value of n is more than expected.");
        }
        this.tiles = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                this.tiles[i][j] = tiles[i][j];
                if (this.tiles[i][j] == 0) {
                    this.blankRow = i;
                    this.blankCol = j;
                }
            }
        }
        int distanceHamming = 0;
        for (char i = 0; i < n; i++) {
            for (char j = 0; j < n; j++) {
                if (tiles[i][j] == 0) continue;
                if (tiles[i][j] != (((i * tiles.length) + 1) + j)) {
                    distanceHamming++;
                }
            }
        }
        int distanceManhattan = 0;
        for (char i = 0; i < n; i++) {
            for (char j = 0; j < n; j++) {
                // if (tiles[i][j] != goal[i][j] && tiles[i][j] != 0) {
                if (tiles[i][j] != ((n * i) + (j + 1)) && tiles[i][j] != 0) {
                    int targetX = (tiles[i][j] - 1) / n;
                    int targetY = (tiles[i][j] - 1) % n;
                    int dx = i - targetX;
                    int dy = j - targetY;
                    distanceManhattan += Math.abs(dx) + Math.abs(dy);
                }
            }
        }
        this.manhattan = distanceManhattan;
        this.hamming = distanceHamming;
        boolean solvable;
        if (((n * n) % 2 != 0) && (inversions() & 1) == 1) solvable = false;
        else if (((n * n) % 2 != 0) && (inversions() & 1) == 0) solvable = true;
        else if (((inversions() + blankRow) % 2) == 0) solvable = false;
        else if (((inversions() + blankRow) % 2) == 1) solvable = true;
    }

    private int inversions() {
        int current = 0;
        int inversionCount = 0;
        int[] temp = new int[n * n];
        int k = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                temp[k] = tiles[i][j];
                current = tiles[i][j];
                for (int m = 0; m <= k; m++) {
                    if (current == 0) continue;
                    if (current < temp[m]) inversionCount++;
                }
                k++;
            }
        }
        return inversionCount;
    }

    // string representation of this board
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(n + "\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                s.append(String.format("%2d ", tiles[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    // board dimension n
    public int dimension() {
        return this.n;
    }

    // number of tiles out of place
    public int hamming() {
        return this.hamming;
    }

    public int manhattan() {
        return this.manhattan;
    }

    // is this board the goal board?
    public boolean isGoal() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (this.tiles[i][j] == 0) continue;
                if (this.tiles[i][j] != (n * i) + (j + 1)) return false;
            }
        }
        return true;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == null) return false;
        if (this.getClass() != y.getClass()) return false;
        int length = ((Board) y).n;
        if (this.n != length) return false;
        if (this == y) return true;
        Board that = (Board) y;
        if (this.n != that.n) return false;
        for (char i = 0; i < n; i++) {
            for (char j = 0; j < n; j++) {
                if (this.tiles[i][j] != that.tiles[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        ArrayList<Board> neighbors = new ArrayList<>();
        int[][] neighbor = copyBoard(this.tiles);
        int index = n - 1;
        if (blankRow < index) { // zero is less than n - Up move
            // neighbor = copyBoard(this.tiles);
            neighbor[blankRow + 1][blankCol] = 0;
            neighbor[blankRow][blankCol] = this.tiles[blankRow + 1][blankCol];
            neighbors.add(new Board(neighbor));
        }
        if (blankCol > 0) { // Left move
            neighbor = copyBoard(this.tiles);
            neighbor[blankRow][blankCol - 1] = 0;
            neighbor[blankRow][blankCol] = this.tiles[blankRow][blankCol - 1];
            neighbors.add(new Board(neighbor));
        }
        if (blankRow > 0) {  // Down move
            neighbor = copyBoard(this.tiles);
            neighbor[blankRow - 1][blankCol] = 0;
            neighbor[blankRow][blankCol] = this.tiles[blankRow - 1][blankCol];
            neighbors.add(new Board(neighbor));
        }
        if (blankCol < index) { // Right move
            neighbor = copyBoard(this.tiles);
            neighbor[blankRow][blankCol + 1] = 0;
            neighbor[blankRow][blankCol] = this.tiles[blankRow][blankCol + 1];
            neighbors.add(new Board(neighbor));
        }
        ArrayList<Board> neiCopy = new ArrayList<Board>(neighbors);
        return neiCopy;
    }


    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {

        int[][] tempTiles = copyBoard(this.tiles);
        if (blankRow < n - 1 && blankCol < n - 1) {
            int temp = this.tiles[blankRow + 1][blankCol + 1];
            int temp2 = this.tiles[blankRow][blankCol + 1];
            tempTiles[blankRow + 1][blankCol + 1] = temp2;
            tempTiles[blankRow][blankCol + 1] = temp;
        } else if (blankRow > 0 && blankCol > 0) {
            int temp = tiles[blankRow - 1][blankCol - 1];
            int temp2 = tiles[blankRow][blankCol - 1];
            tempTiles[blankRow - 1][blankCol - 1] = temp2;
            tempTiles[blankRow][blankCol - 1] = temp;
        } else if (blankRow > 0 && blankRow < n - 1) {
            int temp = tiles[blankRow - 1][blankCol];
            int temp2 = tiles[blankRow + 1][blankCol];
            tempTiles[blankRow + 1][blankCol] = temp;
            tempTiles[blankRow - 1][blankCol] = temp2;
        } else if (blankRow == 0 && blankCol == n - 1) {
            int temp = tiles[blankRow + 1][blankCol];
            int temp2 = tiles[blankRow][blankCol - 1];
            tempTiles[blankRow + 1][blankCol] = temp2;
            tempTiles[blankRow][blankCol - 1] = temp;
        } else if (blankRow > 0 && blankCol < n - 1) {
            int temp = tiles[blankRow - 1][blankCol];
            int temp2 = tiles[blankRow][blankCol + 1];
            tempTiles[blankRow - 1][blankCol] = temp2;
            tempTiles[blankRow][blankCol + 1] = temp;
        }
        Board retBoard = new Board(tempTiles);
        return retBoard;
    }

    private static int[][] copyBoard(int[][] b) {
        int[][] temp = new int[b.length][b.length];
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b.length; j++) {
                temp[i][j] = b[i][j];
            }
        }
        return temp;
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        StdOut.println("");
    }
}
