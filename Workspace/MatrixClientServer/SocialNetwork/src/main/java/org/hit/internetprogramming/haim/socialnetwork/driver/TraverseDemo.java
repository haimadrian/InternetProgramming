package org.hit.internetprogramming.haim.socialnetwork.driver;

import org.hit.internetprogramming.haim.matrix.common.graph.MatrixGraphAdapter;
import org.hit.internetprogramming.haim.matrix.common.mat.Index;
import org.hit.internetprogramming.haim.matrix.common.mat.StandardMatrix;
import org.hit.internetprogramming.haim.socialnetwork.algorithm.DFSVisit;
import org.hit.internetprogramming.haim.socialnetwork.algorithm.TraversableMatrix;

import java.util.List;

public class TraverseDemo {
    public static void main(String[] args) {
        int[][] myArray = new int[][] {
            {1, 1, 0, 1},
            {0, 1, 1, 1},
            {1, 0, 0, 1},
            {1, 1, 0, 0}
        };

        MatrixGraphAdapter<Integer> matrix = new MatrixGraphAdapter<>(new StandardMatrix(myArray), new Index(0, 0));
        TraversableMatrix<Integer> traversableMatrix = new TraversableMatrix<>(matrix);
        DFSVisit<Index> dfsVisit = new DFSVisit<>();

        Index originIndex = new Index(0, 0);
        traversableMatrix.setOriginIndex(originIndex);
        List<Index> visitedVertices = dfsVisit.traverse(traversableMatrix);
        System.out.println("From " + originIndex + ": " + System.lineSeparator() + visitedVertices);

        originIndex = new Index(3, 0);
        traversableMatrix.setOriginIndex(originIndex);
        visitedVertices = dfsVisit.traverse(traversableMatrix);
        System.out.println("From " + originIndex + ": " + System.lineSeparator() + visitedVertices);
    }
}
