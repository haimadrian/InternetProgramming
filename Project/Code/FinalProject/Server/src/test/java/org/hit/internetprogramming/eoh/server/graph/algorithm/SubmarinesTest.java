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


public class SubmarinesTest {

    /* ************************************************************************************** */
    /* ************************* Tests for Find Submarines ************************ */
    /* ************************************************************************************** */

    @Test
    public void testSubmarine_oneSubmarine() {
        Integer[][] arr = {{1, 1, 0, 1, 1},
                           {1, 0, 0, 1, 1},
                           {1, 0, 0, 1, 1}};
        Submarines submarines = new Submarines();
        //1. Test equal.
        assertThat(submarines.findSubmarines(createGraph(arr)), is(1));
    }

    @Test
    public void testSubmarine_twoSubmarine() {
        Integer[][] arr = {{1, 0, 0, 1, 1},
                            {1, 0, 0, 1, 1},
                            {1, 0, 0, 1, 1}};

        Submarines submarines = new Submarines();

        //2. Test equal.
        assertThat(submarines.findSubmarines(createGraph(arr)), is(2));
    }

    @Test
    public void testSubmarine_threeSubmarine() {
        Integer[][] arr = {{1, 1, 0, 1, 1},
                            {0, 0, 0, 1, 1},
                            {1, 1, 0, 1, 1}};
        Submarines submarines = new Submarines();

        //3. Test equal.
        assertThat(submarines.findSubmarines(createGraph(arr)), is(3));
    }

    @Test
    public void testSubmarine_zeroSubmarine() {
        Integer[][] arr = {{1, 1, 1, 1, 1},
                            {1, 1, 0, 1, 1},
                            {1, 1, 1, 1, 1}};
        Submarines submarines = new Submarines();
        //4. Test equal.
        assertThat(submarines.findSubmarines(createGraph(arr)), is(0));
    }

    @Test
    public void testSubmarine_zerosGraphSubmarine() {
        Integer[][] arr = {{0, 0, 0, 0, 0},
                           {0, 0, 0, 0, 0},
                            {0, 0, 0, 0, 0}};
        Submarines submarines = new Submarines();
        //5. Test equal.
        assertThat(submarines.findSubmarines(createGraph(arr)), is(0));
    }

    @Test
    public void testSubmarine_crossedGraphSubmarine() {
        Integer[][] arr = {{1, 0, 1},
                        {0, 1, 0},
                        {1, 0, 1}};
        Submarines submarines = new Submarines();
        //6. Test equal.
        assertThat(submarines.findSubmarines(createGraph(arr)), is(0));
    }

    @Test
    public void testSubmarine_fullGraph() {
        Integer[][] arr = {{1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1}};
        Submarines submarines = new Submarines();
        //7. Test equal.
        assertThat(submarines.findSubmarines(createGraph(arr)), is(1));
    }


    private static  MatrixGraphAdapter<Integer> createGraph(Integer[][] arr) {
        Matrix<Integer> matrix = new Matrix<>(replaceZeroesWithNulls(arr));
        return new MatrixGraphAdapter<>(matrix, Index.from(0, 0));
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
