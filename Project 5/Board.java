import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.LinkedQueue;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;

// Models a board in the 8-puzzle game or its generalization.
public class Board {
    private int N;
    private int[] board;
    private int[][] tiles;
    private int hamming;
    private int manhattan;

    // Construct a board from an N-by-N array of tiles, where
    // tiles[i][j] = tile at row i and column j, and 0 represents the blank
    // square.
    public Board(int[][] originalTiles) {
        this.N = originalTiles.length;
        this.tiles = copy(originalTiles, N);
        board = new int[N * N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                board[encode(i, j)] = tiles[i][j];
            }
        }
    }

    private int[][] copy(int[][] originalTiles, int size) {
        int[][] copy = new int[size][size];
        for (int i = 0; i < originalTiles.length; i++) {
            for (int j = 0; j < originalTiles[i].length; j++)
                copy[i][j] = originalTiles[i][j];
        }
        return copy;
    }

    private int encode(int r, int c) {
        return c % N + N * r;
    }

    private int[] restoreTileElements(int index) {
        int[] rc = new int[2];
        rc[0] = index % N; // column
        rc[1] = index / N; // row
        return rc;
    }

    // Tile at row i and column j.
    public int tileAt(int i, int j) {
        return board[encode(i, j)];
    }

    // Size of this board.
    public int size() {
        return N;
    }

    // Number of tiles out of place.
    public int hamming() {
        int total = 0;
        for (int i = 0; i < board.length; i++) {
            if (i != board[i] - 1 && board[i] != 0) {
                total++;
            }
        }
        hamming = total;
        return hamming;
    }

    // Sum of Manhattan distances between tiles and goal.
    public int manhattan() {
        int total = 0;
        int x1, x2, y1, y2;

        for (int i = 0; i < board.length; i++) {
            if (i != board[i] - 1 && board[i] != 0) {
                int[] arr1 = restoreTileElements(i);
                x1 = arr1[0];
                y1 = arr1[1];
                int[] arr2 = restoreTileElements(board[i] - 1);
                x2 = arr2[0];
                y2 = arr2[1];
                total += (Math.abs(x1 - x2) + Math.abs(y1 - y2));
            }
        }
        manhattan = total;
        return manhattan;
    }

    // Is this board the goal board?
    public boolean isGoal() {
        for (int i = 0; i < board.length; i++) {
            if (i != board[0] - 1)
                return false;
        }
        return true;

        // return hamming() ==0;
    }

    // Is this board solvable?
    public boolean isSolvable() {
        int inversions = inversions();
        int blankrow = 0;
        if (N % 2 != 0) {
            if (inversions % 2 != 0) return false;
        }
        if (N % 2 == 0) {
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (tiles[i][j] == 0) {
                        blankrow = i;
                        break;
                    }
                }
            }
            if ((inversions + blankrow) % 2 == 0)
                return false;
        }

        return true;
    }

    // Does this board equal that?
    public boolean equals(Board that) {
        if (that == this) return true;
        if (that == null) return false;
        if (that.getClass() != this.getClass()) return false;
        if (that.size() != this.size()) return false;
        for (int i = 0; i < board.length; i++) {
            if (this.board[i] != that.board[i])
                return false;
        }
        return true;
    }

    // All neighboring boards.
    public Iterable<Board> neighbors() {
        LinkedQueue<Board> neighbors = new LinkedQueue<Board>();
        ArrayList<Integer> blocks = new ArrayList<Integer>();

        int[] arr = restoreTileElements(blankPos());
        int column = arr[0];
        int row = arr[1];

        if (bound(row - 1, column)) {
            blocks.add(encode(row - 1, column));
        }

        if (bound(row, 1 + column)) {
            blocks.add(encode(row, 1 + column));
        }

        if (bound(row + 1, column)) {
            blocks.add(encode(row + 1, column));
        }

        if (bound(row, column - 1)) {
            blocks.add(encode(row, column - 1));
        }

        for (int i = 0; i < blocks.size(); i++) {
            int[] tempArray = copyArray(board);
            swap(tempArray, blankPos(), blocks.get(i));
            int[][] temp2d = cloneTiles(tempArray);
            Board neighbor = new Board(temp2d);
            neighbors.enqueue(neighbor);
        }

        return neighbors;
    }

    private boolean bound(int r, int c) {
        if (r < 0 || r >= N || c < 0 || c >= N) return false;
        return true;
    }

    private int[] copyArray(int[] input) {
        int[] copy = new int[input.length];
        for (int i = 0; i < input.length; i++)
            copy[i] = input[i];
        return copy;
    }


    private void swap(int[] input, int i, int j) {
        int temp = input[i];
        input[i] = input[j];
        input[j] = temp;
    }

    // String representation of this board.
    public String toString() {
        String s = N + "\n";
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s += String.format("%2d", tiles[i][j]);
                if (j < N - 1) {
                    s += " ";
                }
            }
            if (i < N - 1) {
                s += "\n";
            }
        }
        return s;
    }

    // Helper method that returns the position (in row-major order) of the
    // blank (zero) tile.
    private int blankPos() {
        int blankPos = 0;
        for (int i = 0; i < board.length; i++) {
            if (board[i] == 0) blankPos = i;
        }
        return blankPos;
    }

    // Helper method that returns the number of inversions.
    private int inversions() {
        int inversions = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (i < j) {
                    if (board[i] > board[j] && i != blankPos() && j != blankPos())
                        inversions++;
                }
            }
        }
        return inversions;
    }

    // Helper method that clones the tiles[][] array in this board and
    // returns it.
    private int[][] cloneTiles(int[] input) {
        int[][] copy = new int[N][N];
        for (int i = 0; i < input.length; i++) {
            int[] arr = restoreTileElements(i);
            int x = arr[0];
            int y = arr[1];
            copy[y][x] = input[i];
        }
        return copy;
    }

    // Test client. [DO NOT EDIT]
    public static void main(String[] args) {
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] tiles = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                tiles[i][j] = in.readInt();
            }
        }
        Board board = new Board(tiles);
        StdOut.println(board.hamming());
        StdOut.println(board.manhattan());
        StdOut.println(board.isGoal());
        StdOut.println(board.isSolvable());
        for (Board neighbor : board.neighbors()) {
            StdOut.println(neighbor);
        }
    }
}
