package org.hit.internetprogramming.eoh.server.graph.algorithm;

import org.hamcrest.Matchers;
import org.hit.internetprogramming.eoh.common.graph.MatrixGraphAdapter;
import org.hit.internetprogramming.eoh.common.mat.Index;
import org.hit.internetprogramming.eoh.common.mat.impl.Matrix;
import org.hit.internetprogramming.eoh.common.mat.impl.StandardMatrix;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;


public class ConnectedComponentsTest {

    /* ************************************************************************************** */
    /* ************************* Tests for Find Connected Components ************************ */
    /* ************************************************************************************** */

    @Test
    public void testConnectedComponents_oneCCInRegularMatrix() {
        Integer[][] arr = {{1, 0, 0},
                           {1, 0, 1},
                           {0, 1, 1}};
        Matrix<Integer> matrix = new Matrix<>(replaceZeroesWithNulls(arr));
        MatrixGraphAdapter<Integer> graph = new MatrixGraphAdapter<>(matrix, Index.from(0, 0));
        ConnectedComponents connectedComponents = new ConnectedComponents();


        List<Set<Index>> actual = new ArrayList<>();
        Set<Index> oneCC = new HashSet<>();
        oneCC.add(Index.from(0, 0));
        oneCC.add(Index.from(1, 0));
        oneCC.add(Index.from(1, 2));
        oneCC.add(Index.from(2, 1));
        oneCC.add(Index.from(2, 2));
        actual.add(oneCC);


        //1. Test equal.
        assertThat(connectedComponents.execute(graph), is(actual));
    }
    
    @Test
    public void testConnectedComponents_oneCCInRegularMatrixButFiveCCInStandardMatrix() {
        Integer[][] arr = {{1, 0, 1},
                           {0, 1, 0},
                           {1, 0, 1}};

        replaceZeroesWithNulls(arr);
        Matrix<Integer> regularMatrix = new Matrix<>(arr);
        StandardMatrix<Integer> standardMatrix = new StandardMatrix<>(arr);
        MatrixGraphAdapter<Integer> graphOfMatrix = new MatrixGraphAdapter<>(regularMatrix, Index.from(0, 0));
        MatrixGraphAdapter<Integer> graphOfStandardMatrix = new MatrixGraphAdapter<>(standardMatrix, Index.from(0, 0));
        ConnectedComponents connectedComponents = new ConnectedComponents();

        Set<Index> firstCCInRegularMatrix = new HashSet<>();
        firstCCInRegularMatrix.add(Index.from(0, 0));
        firstCCInRegularMatrix.add(Index.from(0, 2));
        firstCCInRegularMatrix.add(Index.from(1, 1));
        firstCCInRegularMatrix.add(Index.from(2, 0));
        firstCCInRegularMatrix.add(Index.from(2, 2));

        Set<Index> firstCCInSM = new HashSet<>();
        firstCCInSM.add(Index.from(0, 0));

        Set<Index> secondCCInSM = new HashSet<>();
        secondCCInSM.add(Index.from(0, 2));

        Set<Index> thirdCCInSM = new HashSet<>();
        thirdCCInSM.add(Index.from(1, 1));

        Set<Index> fourthCCInSM = new HashSet<>();
        fourthCCInSM.add(Index.from(2, 0));

        Set<Index> fifthCCInSM = new HashSet<>();
        fifthCCInSM.add(Index.from(2, 2));

        //1. Test equal.
        List<Set<Index>> actual = connectedComponents.execute(graphOfMatrix);
        assertThat(actual, Matchers.containsInAnyOrder(firstCCInRegularMatrix));
        //2. Check List Size
        assertThat(actual, hasSize(1));

        //1. Test equal.
        actual = connectedComponents.execute(graphOfStandardMatrix);
        assertThat(actual, Matchers.containsInAnyOrder(firstCCInSM, secondCCInSM, thirdCCInSM, fourthCCInSM, fifthCCInSM));
        //2. Check List Size
        assertThat(actual, hasSize(5));
    }

    @Test
    public void testConnectedComponents_twoColumnsCCInRegularMatrix() {
        Integer[][] arr = {{1, 0, 1},
                           {1, 0, 1},
                           {1, 0, 1}};
        Matrix<Integer> regularMatrix = new Matrix<>(replaceZeroesWithNulls(arr));
        MatrixGraphAdapter<Integer> graph = new MatrixGraphAdapter<>(regularMatrix, Index.from(0, 0));
        ConnectedComponents connectedComponents = new ConnectedComponents();

        List<Set<Index>> actual = new ArrayList<>();
        Set<Index> firstCC = new HashSet<>();
        firstCC.add(Index.from(0, 0));
        firstCC.add(Index.from(1, 0));
        firstCC.add(Index.from(2, 0));

        Set<Index> secondCC = new HashSet<>();
        secondCC.add(Index.from(0, 2));
        secondCC.add(Index.from(1, 2));
        secondCC.add(Index.from(2, 2));

        actual.add(firstCC);
        actual.add(secondCC);

        actual = connectedComponents.execute(graph);
        //1. Test equal.
        assertThat(actual, Matchers.containsInAnyOrder(firstCC, secondCC));
        //2. Check List Size
        assertThat(actual, hasSize(2));
    }

    @Test
    public void testConnectedComponents_threeColumnsCCInRegularMatrix() {
        Integer[][] arr = {{1, 1, 0, 0, 0},
                           {0, 0, 0, 0, 0},
                           {1, 1, 1, 1, 0},
                           {0, 0, 0, 0, 0},
                           {1, 1, 1, 1, 1}};
        Matrix<Integer> regularMatrix = new Matrix<>(replaceZeroesWithNulls(arr));
        MatrixGraphAdapter<Integer> graph = new MatrixGraphAdapter<>(regularMatrix, Index.from(0, 0));
        ConnectedComponents connectedComponents = new ConnectedComponents();

        List<Set<Index>> actual = new ArrayList<>();
        Set<Index> firstCC = new HashSet<>();
        firstCC.add(Index.from(0, 0));
        firstCC.add(Index.from(0, 1));

        Set<Index> secondCC = new HashSet<>();
        secondCC.add(Index.from(2, 0));
        secondCC.add(Index.from(2, 1));
        secondCC.add(Index.from(2, 2));
        secondCC.add(Index.from(2, 3));

        Set<Index> thirdCC = new HashSet<>();
        thirdCC.add(Index.from(4, 0));
        thirdCC.add(Index.from(4, 1));
        thirdCC.add(Index.from(4, 2));
        thirdCC.add(Index.from(4, 3));
        thirdCC.add(Index.from(4, 4));

        actual.add(firstCC);
        actual.add(secondCC);
        actual.add(thirdCC);


        //1. Test equal.
        List<Set<Index>> value = connectedComponents.execute(graph);
        System.out.println(value);


        int sizeFirstItem = 2;
        int sizeSecondItem = 4;
        int sizeThirdItem = 5;

        assertThat(value, Matchers.containsInAnyOrder(firstCC, secondCC, thirdCC));
        //2. Check List Size
        assertThat(value, hasSize(3));

        //3. Check ascending order
        assertThat(sizeFirstItem, is(value.get(0).size()));
        assertThat(sizeSecondItem, is(value.get(1).size()));
        assertThat(sizeThirdItem, is(value.get(2).size()));
    }

    @Test
    public void testConnectedComponents_fourColumnsCCInRegularMatrix() {
        Integer[][] arr = {{1, 0, 1},
                           {0, 0, 0},
                           {1, 0, 1}};
        Matrix<Integer> regularMatrix = new Matrix<>(replaceZeroesWithNulls(arr));
        MatrixGraphAdapter<Integer> graph = new MatrixGraphAdapter<>(regularMatrix, Index.from(0, 0));
        ConnectedComponents connectedComponents = new ConnectedComponents();

        List<Set<Index>> actual = new ArrayList<>();
        Set<Index> firstCC = new HashSet<>();
        firstCC.add(Index.from(0, 0));
        Set<Index> secondCC = new HashSet<>();
        secondCC.add(Index.from(0, 2));
        Set<Index> thirdCC = new HashSet<>();
        thirdCC.add(Index.from(2, 0));
        Set<Index> fourthCC = new HashSet<>();
        fourthCC.add(Index.from(2, 2));

        actual.add(firstCC);
        actual.add(secondCC);
        actual.add(fourthCC);

        //1. Test equal.
        actual = connectedComponents.execute(graph);
        assertThat(actual , Matchers.containsInAnyOrder(firstCC, secondCC, thirdCC, fourthCC));
        //2. Check List Size
        assertThat(actual, hasSize(4));
    }

    private static Integer[][] replaceZeroesWithNulls(Integer[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] == 0) {
                    matrix[i][j] = null;
                }
            }
        }

        return matrix;
    }
}
