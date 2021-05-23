package org.example;


import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Solver {
    private boolean solvable;
    private int moves = 0;
    private final ArrayList<Board> solutionBoardList = new ArrayList<>();
    private ArrayList<Board> result = new ArrayList<>();

    public Solver(Board initialBoard) {


        if (initialBoard == null) {
            throw new IllegalArgumentException("The Board object is empty.");
        }
        /* initialize SearchNodes, twins, and associated boards. (Board b, SearchNode prev, int moves, int
        priority, int manhattan) */
        SearchNode initialSearchNode = new SearchNode(initialBoard, null, 0, (initialBoard.manhattan()),
                (initialBoard.manhattan()));
        Board currentTwinBoard = initialBoard.twin();
        if (currentTwinBoard.isGoal()) {
            solvable = false;
            return;
        }
        /* (Board b, SearchNode prev, int moves, int priority, int manhattan) */
//        SearchNode initialTwinSearchNode = new SearchNode(currentTwinBoard, null, 0, (currentTwinBoard.manhattan()),
//                (currentTwinBoard.manhattan()));
        int twinManhattan = initialBoard.twin().manhattan();
        SearchNode initialTwinSearchNode = new SearchNode(initialBoard.twin(), null, 0, twinManhattan, twinManhattan);
        // Create priority queues
        MinPQ<SearchNode> currentPriorityQueue = new MinPQ<SearchNode>(new Comparator<SearchNode>() {
            @Override
            public int compare(SearchNode o1, SearchNode o2) {
                if (o1.prevSearchNode.numOfMoves + 1 + o1.manhattan
                        > o2.prevSearchNode.numOfMoves + 1 + o2.manhattan) return 1;
                else if (o2.prevSearchNode.numOfMoves + 1 + o2.manhattan >
                        o1.prevSearchNode.numOfMoves + 1 + o1.manhattan) return -1;
                else return 0;
            }
        });
        MinPQ<SearchNode> currentPriorityQueueTwin = new MinPQ<SearchNode>(new Comparator<SearchNode>() {
            @Override
            public int compare(SearchNode o1, SearchNode o2) {
                if (o1.prevSearchNode.numOfMoves + 1 + o1.manhattan
                        > o2.prevSearchNode.numOfMoves + 1 + o2.manhattan) return 1;
                else if (o2.prevSearchNode.numOfMoves + 1 + o2.manhattan >
                        o1.prevSearchNode.numOfMoves + 1 + o1.manhattan) return -1;
                else return 0;
            }
        });
        // put the first node in priority queues
        currentPriorityQueue.insert(initialSearchNode);

        currentPriorityQueueTwin.insert(initialTwinSearchNode);
        // Create a board for Goal
        /* Twin of the goal board is the goal of the twin of the initial board and it can lead to the goal
        board */
        /* Take the first nodes out of Priority Queues, calculate the neighbors, and put them in the nodes
        arrays */
        SearchNode minSearchNode = currentPriorityQueue.delMin();
        SearchNode minTwinNode = currentPriorityQueueTwin.delMin();
        /* (Board b, SearchNode prev, int moves, int priority, int manhattan) */
        for (Board tb : minTwinNode.getCurrentBoard().neighbors()) {
            twinManhattan = tb.manhattan();
            SearchNode temp1Twin = new SearchNode(tb, minTwinNode, minTwinNode.numOfMoves + 1, (twinManhattan +
                    (minTwinNode.numOfMoves + 1)), twinManhattan);
            currentPriorityQueueTwin.insert(temp1Twin);
        }
        /* Twins do not need to know the previous node. Create twins in the same loop as regular search  nodes
         * with null for previous search node, and other fields. You do not even need to push them to a minimum
         * priority queue. Just monitor them for matching the goal. */
        for (Board b : minSearchNode.currentBoard.neighbors()) {
            SearchNode temp1 = new SearchNode(b, minSearchNode, minSearchNode.numOfMoves + 1, (b.manhattan() +
                    (minSearchNode.numOfMoves + 1)), b.manhattan());
            currentPriorityQueue.insert(temp1);
        }

        while (!minSearchNode.currentBoard.isGoal()) {
            if (minTwinNode.currentBoard.isGoal()) {
                solvable = false;
                break;
            }

            /* If it was not visited or it might provide a better path expand it */
            minSearchNode = currentPriorityQueue.delMin();
            minTwinNode = currentPriorityQueueTwin.delMin();
            // populate the priority queues with more nodes
            for (Board tb : minTwinNode.currentBoard.neighbors()) {
                /* (Board b, SearchNode prev, int moves, int priority, int manhattan) */
                SearchNode temp1Twin = new SearchNode(tb, minTwinNode, minTwinNode.numOfMoves + 1,
                        ((minTwinNode.numOfMoves + 1) + tb.manhattan()), tb.manhattan());
                if (minTwinNode.prevSearchNode == null || !tb.equals(minTwinNode.prevSearchNode.
                        currentBoard)) { // make sure this line works
                    currentPriorityQueueTwin.insert(temp1Twin);
                }
            }
            /* add more nodes to priority queue
            (Board b, SearchNode prev, int moves, int priority, int manhattan) */
            for (Board b : minSearchNode.currentBoard.neighbors()) {
                SearchNode temp1 = new SearchNode(b, minSearchNode, minSearchNode.numOfMoves + 1, (b.manhattan()
                        + ((minSearchNode.numOfMoves) + 1)), b.manhattan());

                if (minSearchNode.prevSearchNode == null || !b.equals(minSearchNode.prevSearchNode.
                        getCurrentBoard())) {
                    currentPriorityQueue.insert(temp1);
                }
            }
        } // very first loop -
        if (minSearchNode.currentBoard.isGoal()) {
//            if (minSearchNode.currentBoard.equals(test1Board) && initialBoard.equals(test2Board))
//                StdOut.println("Line 116 is the source of the error. ");
            solvable = true;
            moves = 0;
            //while (!minSearchNode.currentBoard.equals(initialBoard)) {
            //while (!minSearchNode.equals(initialSearchNode)) {
            while (minSearchNode != initialSearchNode) {
                moves++;
                solutionBoardList.add(minSearchNode.currentBoard);
                minSearchNode = minSearchNode.prevSearchNode;
            }
            solutionBoardList.add(initialBoard);
        } else {
            solvable = false;
        }
    }


    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return solvable;
    }

    // min number of moves to solve initial board
    public int moves() {
        if (solvable) {
            return this.moves;
        } else return -1;
    }

    // sequence of boards in a shortest solution
    public Iterable<Board> solution() {
        if (solvable) {
            Collections.reverse(solutionBoardList);
            for (Object solObj : solutionBoardList) {
                result.add((Board) solObj);
            }
            return result;
        } else
            return null;
    }

    private static class SearchNode implements Comparable<SearchNode> {
        private final Board currentBoard;
        private final SearchNode prevSearchNode;
        private final int numOfMoves;
        private int priority;
        private final int manhattan;


        public SearchNode(Board b, SearchNode prev, int moves, int p, int manhattan) {
            currentBoard = b;
            numOfMoves = moves;
            prevSearchNode = prev;
            this.priority = p;
            this.manhattan = manhattan;
        }

        public Board getCurrentBoard() {
            return currentBoard;
        }


        public SearchNode getPrevSearchNode() {
            return prevSearchNode;
        }

        public int getPriority() {
            return (this.priority);
        }

        public int GetManhattan() {
            return this.manhattan;
        }

        public boolean equals(Object sNodeObj) {
            if (this == sNodeObj) return true;
            if (sNodeObj == null) return false;
            if (this.getClass() != sNodeObj.getClass()) return false;
            SearchNode that = (SearchNode) sNodeObj;
            if (!that.currentBoard.equals(this.currentBoard)) return false;
            return true;
        }

        @Override
        public int compareTo(SearchNode sObj) {
            if (this.getPriority() > sObj.getPriority()) return 1;
            if (this.getPriority() < sObj.getPriority()) return -1;
            return 0;
        }

    }

    // test client (see below)
    public static void main(String[] args) {
        /* int[][] tiles = new int[][]{{1, 2, 3, 4, 5, 7, 14}, {8, 9, 10, 11, 12, 13, 6}, {15, 16, 17, 18, 19, 20, 21},
                {22, 23, 24, 25, 26, 27, 28}, {29, 30, 31, 32, 0, 33, 34}, {36, 37, 38, 39, 40, 41, 35},
                {43, 44, 45, 46, 47, 48, 42}} */
        int[][] tiles2 = {{8, 4, 7}, {1, 5, 6}, {3, 2, 0}};
        Board testTiles2Board = new Board(tiles2);
        // Solver s = new Solver(testTilesBoard);
        Solver s2 = new Solver(testTiles2Board);
        if (!s2.solvable)
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves= " + s2.moves);
            for (Board boardObj : s2.solutionBoardList) {
                StdOut.println(" board: " + boardObj + " Hamming Distance of : " +
                        " Manhattan Distance of : " + boardObj.manhattan());
            }
        }
    }
}
