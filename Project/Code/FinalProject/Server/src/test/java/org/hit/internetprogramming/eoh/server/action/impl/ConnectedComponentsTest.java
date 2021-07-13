package org.hit.internetprogramming.eoh.server.action.impl;

import org.hit.internetprogramming.eoh.common.graph.MatrixGraphAdapter;
import org.hit.internetprogramming.eoh.common.mat.Index;
import org.hit.internetprogramming.eoh.common.mat.RegularMatrix;
import org.hit.internetprogramming.eoh.common.mat.StandardMatrix;
import org.hit.internetprogramming.eoh.server.graph.algorithm.DFSVisit;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;


public class ConnectedComponentsTest {

    @Test
    public void testConnectedComponents_oneCCInRegularMatrix() {
        int[][] arr = {{1, 0, 0}, {1, 0, 1}, {0, 1, 1}};
        RegularMatrix regularMatrix = new RegularMatrix(arr);
        MatrixGraphAdapter<Integer> graph = new MatrixGraphAdapter<>(regularMatrix, new Index(0, 0));
        DFSVisit<Index> dfsVisit = new DFSVisit<>();

        HashSet<List<Index>> actual = new HashSet<>();
        List<Index> oneCC = new ArrayList<>();
        oneCC.add(new Index(0, 0));
        oneCC.add(new Index(1, 0));
        oneCC.add(new Index(1, 2));
        oneCC.add(new Index(2, 1));
        oneCC.add(new Index(2, 2));
        actual.add(oneCC);


        //1. Test equal.
        assertThat(dfsVisit.traverse(graph), is(actual));
    }

    @Test
    public void testConnectedComponents_oneCCInRegularMatrixButFiveCCInStandardMatrix() {
        int[][] arr = {{1, 0, 1}, {0, 1, 0}, {1, 0, 1}};
        RegularMatrix regularMatrix = new RegularMatrix(arr);
        StandardMatrix standardMatrix = new StandardMatrix(arr);
        MatrixGraphAdapter<Integer> graph = new MatrixGraphAdapter<>(regularMatrix, new Index(0, 0));
        MatrixGraphAdapter<Integer> graph2 = new MatrixGraphAdapter<>(standardMatrix, new Index(0, 0));
        DFSVisit<Index> dfsVisit = new DFSVisit<>();

        HashSet<List<Index>> actual = new HashSet<>();
        List<Index> firstCCInRegularMatrix = new ArrayList<>();
        firstCCInRegularMatrix.add(new Index(0, 0));
        firstCCInRegularMatrix.add(new Index(0, 2));
        firstCCInRegularMatrix.add(new Index(1, 1));
        firstCCInRegularMatrix.add(new Index(2, 0));
        firstCCInRegularMatrix.add(new Index(2, 2));
        actual.add(firstCCInRegularMatrix);

        HashSet<List<Index>> actual2 = new HashSet<>();
        List<Index> firstCCInSM = new ArrayList<>();
        firstCCInSM.add(new Index(0, 0));

        List<Index> secondCCInSM = new ArrayList<>();
        secondCCInSM.add(new Index(0, 2));

        List<Index> thirdCCInSM = new ArrayList<>();
        thirdCCInSM.add(new Index(1, 1));

        List<Index> fourthCCInSM = new ArrayList<>();
        fourthCCInSM.add(new Index(2, 0));

        List<Index> fifthCCInSM = new ArrayList<>();
        fifthCCInSM.add(new Index(2, 2));

        actual2.add(firstCCInSM);
        actual2.add(secondCCInSM);
        actual2.add(thirdCCInSM);
        actual2.add(fourthCCInSM);
        actual2.add(fifthCCInSM);

        //1. Test equal.
        assertThat(actual, is(dfsVisit.traverse(graph)));
        //2. Check List Size
        assertThat(actual, hasSize(1));

        //1. Test equal.
        assertThat(actual2, is(dfsVisit.traverse(graph2)));
        //2. Check List Size
        assertThat(actual2, hasSize(5));
    }

    @Test
    public void testConnectedComponents_twoColumnsCCInRegularMatrix() {
        int[][] arr = {{1, 0, 1}, {1, 0, 1}, {1, 0, 1}};
        RegularMatrix regularMatrix = new RegularMatrix(arr);
        MatrixGraphAdapter<Integer> graph = new MatrixGraphAdapter<>(regularMatrix, new Index(0, 0));
        DFSVisit<Index> dfsVisit = new DFSVisit<>();

        HashSet<List<Index>> actual = new HashSet<>();
        List<Index> firstCC = new ArrayList<>();
        firstCC.add(new Index(0, 0));
        firstCC.add(new Index(1, 0));
        firstCC.add(new Index(2, 0));
        List<Index> secondCC = new ArrayList<>();
        secondCC.add(new Index(0, 2));
        secondCC.add(new Index(1, 2));
        secondCC.add(new Index(2, 2));
        actual.add(firstCC);
        actual.add(secondCC);


        //1. Test equal.
        assertThat(dfsVisit.traverse(graph), is(actual));
        //2. Check List Size
        assertThat(actual, hasSize(2));
    }

    @Test
    public void testConnectedComponents_threeColumnsCCInRegularMatrix() {
        int[][] arr = {{1, 1, 1, 1, 1}, {0, 0, 0, 0, 0}, {1, 1, 1, 1, 1}, {0, 0, 0, 0, 0}, {1, 1, 1, 1, 1}};
        RegularMatrix regularMatrix = new RegularMatrix(arr);
        MatrixGraphAdapter<Integer> graph = new MatrixGraphAdapter<>(regularMatrix, new Index(0, 0));
        DFSVisit<Index> dfsVisit = new DFSVisit<>();

        HashSet<List<Index>> actual = new HashSet<>();
        List<Index> firstCC = new ArrayList<>();
        firstCC.add(new Index(0, 0));
        firstCC.add(new Index(0, 1));
        firstCC.add(new Index(0, 2));
        firstCC.add(new Index(0, 3));
        firstCC.add(new Index(0, 4));
        List<Index> secondCC = new ArrayList<>();
        secondCC.add(new Index(2, 0));
        secondCC.add(new Index(2, 1));
        secondCC.add(new Index(2, 2));
        secondCC.add(new Index(2, 3));
        secondCC.add(new Index(2, 4));
        List<Index> thirdCC = new ArrayList<>();
        thirdCC.add(new Index(4, 0));
        thirdCC.add(new Index(4, 1));
        thirdCC.add(new Index(4, 2));
        thirdCC.add(new Index(4, 3));
        thirdCC.add(new Index(4, 4));
        actual.add(firstCC);
        actual.add(secondCC);
        actual.add(thirdCC);


        //1. Test equal.
        assertThat(actual, is(dfsVisit.traverse(graph)));
        //2. Check List Size
        assertThat(actual, hasSize(3));
    }

    @Test
    public void testConnectedComponents_fourColumnsCCInRegularMatrix() {
        int[][] arr = {{1, 0, 1}, {0, 0, 0}, {1, 0, 1}};
        RegularMatrix regularMatrix = new RegularMatrix(arr);
        MatrixGraphAdapter<Integer> graph = new MatrixGraphAdapter<>(regularMatrix, new Index(0, 0));
        DFSVisit<Index> dfsVisit = new DFSVisit<>();

        HashSet<List<Index>> actual = new HashSet<>();
        List<Index> firstCC = new ArrayList<>();
        firstCC.add(new Index(0, 0));
        List<Index> secondCC = new ArrayList<>();
        secondCC.add(new Index(0, 2));
        List<Index> thirdCC = new ArrayList<>();
        thirdCC.add(new Index(2, 0));
        List<Index> fourthCC = new ArrayList<>();
        fourthCC.add(new Index(2, 2));

        actual.add(firstCC);
        actual.add(secondCC);
        actual.add(thirdCC);
        actual.add(fourthCC);

        //1. Test equal.
        assertThat(actual, is(dfsVisit.traverse(graph)));
        //2. Check List Size
        assertThat(actual, hasSize(4));
    }

}
