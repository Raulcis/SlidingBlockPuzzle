import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.LinkedStack;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

import java.util.Comparator;

//import edu.princeton.cs.algs4.LinkedStack;
//import edu.princeton.cs.algs4.MinPQ;

// A solver based on the A* algorithm for the 8-puzzle and its generalizations.

public class Solver {
    private LinkedStack<Board> solution;
    private MinPQ<SearchNode> pq;
    private Board initialB;
    private Board goalB;
    private int N;


    // Helper search node class.
    private class SearchNode {
        private Board board;
        private int gCost;
        private SearchNode previous;

        SearchNode(Board board, int moves, SearchNode previous) {
            this.board = board;
            gCost = moves;
            this.previous = previous;
        }
    }

    // Find a solution to the initial board (using the A* algorithm).
    public Solver(Board initial) {
        if (initial == null) throw new NullPointerException("Board cannot be null");
        if (!initial.isSolvable()) throw new IllegalArgumentException("Board cannot be solved");
        this.initialB = initial;
        N = this.initialB.size();

        Comparator<SearchNode> compare = new ManhattanOrder();

        pq = new MinPQ<SearchNode>(compare);

        int[][] goalBlocks = new int[N][N];
        int countGoal = 1;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                goalBlocks[i][j] = countGoal;
                countGoal++;
            }
        }
        goalBlocks[N - 1][N - 1] = 0; // blank tile
        goalB = new Board(goalBlocks);

        SearchNode min;

        pq.insert(new SearchNode(initialB, 0, null));

        while (!pq.min().board.equals(goalB)) {
            min = pq.min();
            pq.delMin();

            for (Board neighbor : min.board.neighbors()) {
                if (min.gCost == 0)
                    pq.insert(new SearchNode(neighbor, min.gCost + 1, min));
                else if (!neighbor.equals(min.previous.board))
                    pq.insert(new SearchNode(neighbor, min.gCost + 1, min));
            }

        }
    }

    // The minimum number of moves to solve the initial board.
    public int moves() {
        return pq.min().gCost;
    }

    // Sequence of boards in a shortest solution.
    public Iterable<Board> solution() {
        solution = new LinkedStack<Board>();
        SearchNode curr = pq.min();
        while (curr.previous != null) {
            solution.push(curr.board);
            curr = curr.previous;
        }
        solution.push(initialB);
        return solution;
    }

    // Helper hamming priority function comparator.
    private static class HammingOrder implements Comparator<SearchNode> {
        public int compare(SearchNode a, SearchNode b) {
            if ((a.board.hamming() + a.gCost) > (b.board.hamming() + b.gCost))
                return 1;
            else if ((a.board.hamming() + a.gCost) == (b.board.hamming() + b.gCost))
                return 0;
            return -1;
        }
    }

    // Helper manhattan priority function comparator.
    private static class ManhattanOrder implements Comparator<SearchNode> {
        public int compare(SearchNode a, SearchNode b) {
            if ((a.board.manhattan() + a.gCost) > (b.board.manhattan() + b.gCost))
                return 1;
            else if ((a.board.manhattan() + a.gCost) == (b.board.manhattan() + b.gCost))
                return 0;
            return -1;
        }
    }

/*
public class Solver {
    private LinkedStack<Board> solution;
    private MinPQ<SearchNode> pq;
    private Board initialB;
    private int moves;

    // Helper search node class.
    private class SearchNode {
        private final Board board;
        private final int moves;
        private SearchNode previous;

        SearchNode(Board board, int moves, SearchNode previous) {
            this.board = board;
            this.moves = moves;
            this.previous = previous;
        }
    }

    // Find a solution to the initial board (using the A* algorithm).

    public Solver(Board initial) {
        if (initial == null) throw new NullPointerException();
        if (!initial.isSolvable()) throw new IllegalArgumentException();

        this.initialB = initial;
        Comparator<SearchNode> compare = new ManhattanOrder();
        pq = new MinPQ<>(compare);
        solution = new LinkedStack<>();
        moves = 0;

        SearchNode first = new SearchNode(initial, moves, null);
        pq.insert(first);

        int[][] correctBoard = new int[initial.size()][initial.size()];
        int count = 1;
        for (int i = 0; i < initial.size(); i++) {
            for (int j = 0; j < initial.size(); j++) {
                correctBoard[i][j] = count;
                count++;
            }
        }

        correctBoard[initial.size() - 1][initial.size() - 1] = 0;
        Board goal = new Board(correctBoard);

        while (!pq.min().board.equals(goal)) {
            SearchNode node = pq.delMin();
            for (Board x : node.board.neighbors()) {
                if (node.moves == 0) {
                    SearchNode next = new SearchNode(x, node.moves + 1, node);
                    pq.insert(next);
                } else if (!x.equals(node.previous.board)) {
                    SearchNode next = new SearchNode(x, node.moves + 1, node);
                    pq.insert(next);
                }
            }
            this.moves = node.moves + 1;
        }

    }

    // The minimum number of moves to solve the initial board.
    public int moves() {
        return this.moves;
    }

    // Sequence of boards in a shortest solution.
    public Iterable<Board> solution() {

        solution = new LinkedStack<>();
        if (initialB.manhattan() == 0) {
            solution.push(initialB);
            return solution;
        }

        SearchNode curr = pq.min();
        while (curr.previous != null) {
            solution.push(curr.board);
            curr = curr.previous;
        }

        return solution;

    }

    // Helper hamming priority function comparator.
    private static class HammingOrder implements Comparator<SearchNode> {
        public int compare(SearchNode a, SearchNode b) {
            return Integer.compare(a.board.hamming() + a.moves, b.board.hamming() + b.moves);
        }
    }

    // Helper manhattan priority function comparator.
    private static class ManhattanOrder implements Comparator<SearchNode> {
        public int compare(SearchNode a, SearchNode b) {
            return Integer.compare(a.board.manhattan() + a.moves, b.board.manhattan() + b.moves);
        }

    }*/

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
        Board initial = new Board(tiles);
        if (initial.isSolvable()) {
            Solver solver = new Solver(initial);
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution()) {
                StdOut.println(board);
            }
        } else {
            StdOut.println("Unsolvable puzzle");
        }
    }
}
