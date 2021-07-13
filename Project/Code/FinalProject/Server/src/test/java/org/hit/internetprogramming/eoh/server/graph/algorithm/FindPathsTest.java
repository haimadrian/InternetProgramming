package org.hit.internetprogramming.eoh.server.graph.algorithm;

import org.hit.internetprogramming.eoh.common.graph.IGraph;
import org.hit.internetprogramming.eoh.common.graph.MatrixGraphAdapter;
import org.hit.internetprogramming.eoh.common.mat.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Test class for {@link FindPaths} algorithm.
 * @author Haim Adrian
 */
public class FindPathsTest {

    private static void pathsValidation(Collection<? extends Collection<Index>> expectedPaths, Collection<Collection<Index>> actualPaths) {
        Assertions.assertEquals(expectedPaths.size(), actualPaths.size(), "Wrong amount of paths");
        for (Collection<Index> expectedPath : expectedPaths) {
            Assertions.assertTrue(actualPaths.contains(expectedPath), "Missing path: " + System.lineSeparator() +
                    expectedPath + System.lineSeparator() +
                    "Paths: " + System.lineSeparator() +
                    actualPaths.stream().map(Collection::toString).collect(Collectors.joining(System.lineSeparator())));
        }
    }

    /* ************************************************************************************** */
    /* *************************** Tests for Find Shortest Paths **************************** */
    /* ************************************************************************************** */

    @Test
    public void testFindShortestPaths_useStandardMatrix_findNoPath() {
        // Arrange
        //@formatter:off
        int[][] mat = {{1, 1, 1, 1, 0},
                       {1, 1, 0, 0, 1},
                       {1, 0, 1, 0, 1},
                       {1, 1, 0, 1, 1},
                       {1, 1, 1, 1, 1}};
        //@formatter:on
        IMatrix<Integer> matrix = new StandardMatrix(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(0, 0));

        List<List<Index>> expectedPaths = new ArrayList<>(0);

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        Collection<Collection<Index>> paths = findPaths.findShortestPaths(Index.from(2, 2));

        // Assert
        pathsValidation(expectedPaths, paths);
    }

    @Test
    public void testFindShortestPaths_useCrossMatrix_findNoPath() {
        // Arrange
        //@formatter:off
        int[][] mat = {{1, 1, 1, 1},
                       {1, 1, 0, 1},
                       {1, 1, 0, 1},
                       {1, 1, 1, 1}};
        //@formatter:on
        IMatrix<Integer> matrix = new CrossMatrix(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(0, 0));

        List<List<Index>> expectedPaths = new ArrayList<>(0);

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        Collection<Collection<Index>> paths = findPaths.findShortestPaths(Index.from(3, 3));

        // Assert
        pathsValidation(expectedPaths, paths);
    }

    @Test
    public void testFindShortestPaths_useRegularMatrix_findNoPath() {
        // Arrange
        //@formatter:off
        int[][] mat = {{1, 1, 1, 1},
                       {1, 1, 0, 1},
                       {1, 1, 0, 0},
                       {1, 1, 0, 1}};
        //@formatter:on
        IMatrix<Integer> matrix = new RegularMatrix(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(0, 0));

        List<List<Index>> expectedPaths = new ArrayList<>(0);

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        Collection<Collection<Index>> paths = findPaths.findShortestPaths(Index.from(3, 3));

        // Assert
        pathsValidation(expectedPaths, paths);
    }

    @Test
    public void testFindShortestPaths_useStandardMatrix_findOnePath() {
        // Arrange
        //@formatter:off
        int[][] mat = {{1, 0, 1, 1, 0},
                       {1, 1, 0, 0, 1},
                       {1, 0, 1, 0, 1},
                       {1, 0, 1, 0, 1},
                       {1, 1, 1, 1, 1}};
        //@formatter:on
        IMatrix<Integer> matrix = new StandardMatrix(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(0, 0));

        Collection<Collection<Index>> expectedPaths = new ArrayList<>(1);
        expectedPaths.add(Arrays.asList(Index.from(0, 0), Index.from(1, 0), Index.from(2, 0), Index.from(3, 0), Index.from(4, 0), Index.from(4, 1), Index.from(4, 2), Index.from(3, 2), Index.from(2, 2)));

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        List<Collection<Index>> paths = findPaths.findShortestPaths(Index.from(2, 2));

        // Assert
        pathsValidation(expectedPaths, paths);
    }

    @Test
    public void testFindShortestPaths_useCrossMatrix_findOnePath() {
        // Arrange
        //@formatter:off
        int[][] mat = {{1, 1, 0, 1},
                       {1, 1, 0, 1},
                       {1, 1, 1, 1},
                       {1, 0, 1, 1},
                       {1, 1, 1, 1}};
        //@formatter:on
        IMatrix<Integer> matrix = new CrossMatrix(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(0, 0));

        Collection<Collection<Index>> expectedPaths = new ArrayList<>(1);
        expectedPaths.add(Arrays.asList(Index.from(0, 0), Index.from(1, 1), Index.from(2, 2), Index.from(3, 3)));

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        List<Collection<Index>> paths = findPaths.findShortestPaths(Index.from(3, 3));

        // Assert
        pathsValidation(expectedPaths, paths);
    }

    @Test
    public void testFindShortestPaths_useRegularMatrix_findOnePath() {
        // Arrange
        //@formatter:off
        int[][] mat = {{1, 1, 1, 1, 0},
                       {1, 0, 0, 0, 1},
                       {1, 0, 1, 0, 1},
                       {1, 0, 1, 0, 1},
                       {1, 1, 1, 1, 1}};
        //@formatter:on
        IMatrix<Integer> matrix = new RegularMatrix(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(0, 0));

        Collection<Collection<Index>> expectedPaths = new ArrayList<>(1);
        expectedPaths.add(Arrays.asList(Index.from(0, 0), Index.from(1, 0), Index.from(2, 0), Index.from(3, 0), Index.from(4, 1), Index.from(3, 2), Index.from(2, 2)));

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        List<Collection<Index>> paths = findPaths.findShortestPaths(Index.from(2, 2));

        // Assert
        pathsValidation(expectedPaths, paths);
    }

    @Test
    public void testFindShortestPaths_useStandardMatrix_findTwoShortPathsOutOfThreePaths() {
        // Arrange
        //@formatter:off
        int[][] mat = {{1, 1, 1, 1, 0},
                       {1, 0, 1, 0, 1},
                       {1, 1, 1, 1, 1},
                       {1, 0, 1, 0, 1},
                       {1, 1, 1, 0, 1}};
        //@formatter:on
        IMatrix<Integer> matrix = new StandardMatrix(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(0, 0));

        List<List<Index>> expectedPaths = new ArrayList<>(2);
        expectedPaths.add(Arrays.asList(Index.from(0, 0), Index.from(0, 1), Index.from(0, 2), Index.from(1, 2), Index.from(2, 2)));
        expectedPaths.add(Arrays.asList(Index.from(0, 0), Index.from(1, 0), Index.from(2, 0), Index.from(2, 1), Index.from(2, 2)));

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        Collection<Collection<Index>> paths = findPaths.findShortestPaths(Index.from(2, 2));

        // Assert
        pathsValidation(expectedPaths, paths);
    }

    @Test
    public void testFindShortestPaths_useCrossMatrix_findTwoShortPathsOutOfThreePaths() {
        // Arrange
        //@formatter:off
        int[][] mat = {{1, 0, 1, 0, 1},
                       {0, 1, 0, 1, 1},
                       {1, 0, 0, 1, 1},
                       {0, 1, 0, 1, 1},
                       {1, 1, 1, 1, 1},
                       {1, 1, 1, 1, 1}};
        //@formatter:on
        IMatrix<Integer> matrix = new CrossMatrix(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(0, 0));

        List<List<Index>> expectedPaths = new ArrayList<>(2);
        expectedPaths.add(Arrays.asList(Index.from(0, 0), Index.from(1, 1), Index.from(0, 2), Index.from(1, 3), Index.from(2, 4), Index.from(3, 3)));
        expectedPaths.add(Arrays.asList(Index.from(0, 0), Index.from(1, 1), Index.from(2, 0), Index.from(3, 1), Index.from(4, 2), Index.from(3, 3)));
        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        Collection<Collection<Index>> paths = findPaths.findShortestPaths(Index.from(3, 3));

        // Assert
        pathsValidation(expectedPaths, paths);
    }

    @Test
    public void testFindShortestPaths_useStandardMatrixWithAnotherRoot_findOnePath() {
        // Arrange
        //@formatter:off
        int[][] mat = {{1, 0, 1, 1, 0},
                       {1, 1, 1, 0, 1},
                       {1, 0, 0, 1, 1},
                       {1, 0, 1, 0, 1},
                       {1, 1, 1, 1, 1}};
        //@formatter:on
        IMatrix<Integer> matrix = new StandardMatrix(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(1, 2));

        List<List<Index>> expectedPaths = new ArrayList<>(1);
        expectedPaths.add(Arrays.asList(Index.from(1, 2), Index.from(1, 1), Index.from(1, 0), Index.from(2, 0), Index.from(3, 0), Index.from(4, 0), Index.from(4, 1), Index.from(4, 2), Index.from(3, 2)));

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        Collection<Collection<Index>> paths = findPaths.findShortestPaths(Index.from(3, 2));

        // Assert
        pathsValidation(expectedPaths, paths);
    }

    @Test
    public void testFindShortestPaths_useCrossMatrixWithAnotherRoot_findOnePath() {
        // Arrange
        //@formatter:off
        int[][] mat = {{1, 1, 0, 1},
                       {0, 1, 1, 1},
                       {1, 1, 1, 0},
                       {1, 0, 1, 1},
                       {1, 0, 1, 1}};
        //@formatter:on
        IMatrix<Integer> matrix = new CrossMatrix(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(1, 2));

        List<List<Index>> expectedPaths = new ArrayList<>(1);
        expectedPaths.add(Arrays.asList(Index.from(1, 2), Index.from(2, 1), Index.from(3, 2), Index.from(4, 3)));

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        Collection<Collection<Index>> paths = findPaths.findShortestPaths(Index.from(4, 3));

        // Assert
        pathsValidation(expectedPaths, paths);
    }

    @Test
    public void testFindShortestPaths_useStandardMatrixWithAnotherRoot_findOneShortPathOutOfThreePaths() {
        // Arrange
        //@formatter:off
        int[][] mat = {{1, 1, 1, 1, 0},
                       {1, 0, 1, 0, 1},
                       {1, 1, 1, 0, 1},
                       {1, 0, 1, 0, 1},
                       {1, 1, 1, 0, 1}};
        //@formatter:on
        IMatrix<Integer> matrix = new StandardMatrix(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(1, 2));

        List<List<Index>> expectedPaths = new ArrayList<>(1);
        expectedPaths.add(Arrays.asList(Index.from(1, 2), Index.from(2, 2)));

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        Collection<Collection<Index>> paths = findPaths.findShortestPaths(Index.from(2, 2));

        // Assert
        pathsValidation(expectedPaths, paths);
    }

    @Test
    public void testFindShortestPaths_useCrossMatrixWithAnotherRoot_findOneShortPathOutOfTwoPaths() {
        // Arrange
        //@formatter:off
        int[][] mat = {{1, 0, 1, 0},
                       {0, 1, 0, 1},
                       {1, 1, 1, 1},
                       {0, 1, 0, 1}};
        //@formatter:on
        IMatrix<Integer> matrix = new CrossMatrix(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(1, 3));

        List<List<Index>> expectedPaths = new ArrayList<>(1);
        expectedPaths.add(Arrays.asList(Index.from(1, 3), Index.from(2, 2)));

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        Collection<Collection<Index>> paths = findPaths.findShortestPaths(Index.from(2, 2));

        // Assert
        pathsValidation(expectedPaths, paths);
    }

    @Test
    public void testFindShortestPaths_useRegularMatrixWithAnotherRoot_findSixShortPathsOutOfMillionPaths() {
        // Arrange
        //@formatter:off
        int[][] mat = {{1, 1, 1, 0, 0, 1, 1, 0, 1, 0},
                       {0, 0, 0, 0, 0, 1, 1, 1, 0, 0},
                       {0, 1, 0, 1, 0, 0, 1, 1, 0, 1},
                       {1, 1, 1, 1, 0, 0, 0, 0, 1, 0},
                       {0, 0, 0, 1, 1, 1, 1, 0, 0, 0},
                       {0, 0, 0, 0, 1, 1, 1, 0, 1, 1},
                       {1, 1, 0, 0, 0, 0, 1, 1, 0, 1},
                       {0, 0, 1, 1, 1, 0, 1, 1, 1, 0},
                       {1, 0, 1, 1, 0, 1, 1, 0, 0, 0},
                       {1, 0, 0, 0, 1, 1, 0, 0, 1, 0}};
        //@formatter:on
        IMatrix<Integer> matrix = new RegularMatrix(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(3, 1));

        List<List<Index>> expectedPaths = new ArrayList<>(3);
        expectedPaths.add(new LinkedList<>(Arrays.asList(Index.from(3, 1), Index.from(3, 2), Index.from(3, 3), Index.from(4, 4), Index.from(5, 5), Index.from(6, 6), Index.from(7, 6), Index.from(8, 5), Index.from(7, 4), Index.from(8, 3))));
        expectedPaths.add(new LinkedList<>(Arrays.asList(Index.from(3, 1), Index.from(3, 2), Index.from(4, 3), Index.from(4, 4), Index.from(5, 5), Index.from(6, 6), Index.from(7, 6), Index.from(8, 5), Index.from(7, 4), Index.from(8, 3))));
        expectedPaths.add(new LinkedList<>(Arrays.asList(Index.from(3, 1), Index.from(3, 2), Index.from(4, 3), Index.from(5, 4), Index.from(5, 5), Index.from(6, 6), Index.from(7, 6), Index.from(8, 5), Index.from(7, 4), Index.from(8, 3))));
        expectedPaths.add(new LinkedList<>(Arrays.asList(Index.from(3, 1), Index.from(3, 2), Index.from(3, 3), Index.from(4, 4), Index.from(5, 5), Index.from(6, 6), Index.from(7, 6), Index.from(8, 5), Index.from(9, 4), Index.from(8, 3))));
        expectedPaths.add(new LinkedList<>(Arrays.asList(Index.from(3, 1), Index.from(3, 2), Index.from(4, 3), Index.from(4, 4), Index.from(5, 5), Index.from(6, 6), Index.from(7, 6), Index.from(8, 5), Index.from(9, 4), Index.from(8, 3))));
        expectedPaths.add(new LinkedList<>(Arrays.asList(Index.from(3, 1), Index.from(3, 2), Index.from(4, 3), Index.from(5, 4), Index.from(5, 5), Index.from(6, 6), Index.from(7, 6), Index.from(8, 5), Index.from(9, 4), Index.from(8, 3))));

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        Collection<Collection<Index>> paths = findPaths.findShortestPaths(Index.from(8, 3));

        // Assert
        pathsValidation(expectedPaths, paths);
    }

    @Test
    public void testFindShortestPaths_useRegularMatrixWithAnotherRoot_findThreeShortPathsOutOfMillionPaths() {
        // Arrange
        //@formatter:off
        int[][] mat = {{1, 1, 1, 0, 0, 1, 1, 0, 1, 0},
                       {0, 0, 0, 0, 0, 1, 1, 1, 0, 0},
                       {0, 1, 0, 1, 0, 0, 1, 1, 0, 1},
                       {1, 1, 1, 1, 0, 0, 0, 0, 1, 0},
                       {0, 0, 0, 1, 1, 1, 1, 0, 0, 0},
                       {0, 0, 0, 0, 1, 1, 1, 0, 1, 1},
                       {1, 1, 0, 0, 0, 0, 1, 1, 0, 1},
                       {0, 0, 1, 1, 1, 1, 1, 1, 0, 0},
                       {1, 0, 1, 1, 0, 1, 1, 0, 0, 0},
                       {1, 0, 0, 0, 1, 1, 0, 0, 1, 0}};
        //@formatter:on
        IMatrix<Integer> matrix = new RegularMatrix(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(3, 1));

        Collection<List<Index>> expectedPaths = new ArrayList<>(3);
        expectedPaths.add(new LinkedList<>(Arrays.asList(Index.from(3, 1), Index.from(3, 2), Index.from(3, 3), Index.from(4, 4), Index.from(5, 5), Index.from(6, 6), Index.from(7, 5), Index.from(7, 4), Index.from(8, 3))));
        expectedPaths.add(new LinkedList<>(Arrays.asList(Index.from(3, 1), Index.from(3, 2), Index.from(4, 3), Index.from(4, 4), Index.from(5, 5), Index.from(6, 6), Index.from(7, 5), Index.from(7, 4), Index.from(8, 3))));
        expectedPaths.add(new LinkedList<>(Arrays.asList(Index.from(3, 1), Index.from(3, 2), Index.from(4, 3), Index.from(5, 4), Index.from(5, 5), Index.from(6, 6), Index.from(7, 5), Index.from(7, 4), Index.from(8, 3))));

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        Collection<Collection<Index>> paths = findPaths.findShortestPaths(Index.from(8, 3));

        // Assert
        pathsValidation(expectedPaths, paths);
    }

    /* ************************************************************************************** */
    /* ****************************** Tests for Find All Paths ****************************** */
    /* ************************************************************************************** */
    
    @Test
    public void testFindAllPaths_useStandardMatrix_findNoPath() {
        // Arrange
        //@formatter:off
        int[][] mat = {{1, 1, 1, 1, 0},
                       {1, 1, 0, 0, 1},
                       {1, 0, 1, 0, 1},
                       {1, 1, 0, 1, 1},
                       {1, 1, 1, 1, 1}};
        //@formatter:on
        IMatrix<Integer> matrix = new StandardMatrix(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(0, 0));

        List<List<Index>> expectedPaths = new ArrayList<>();

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        Collection<Collection<Index>> paths = findPaths.findAllPaths(Index.from(2, 2));

        // Assert
        pathsValidation(expectedPaths, paths);
    }

    @Test
    public void testFindAllPaths_useCrossMatrix_findNoPath() {
        // Arrange
        //@formatter:off
        int[][] mat = {{1, 1, 1, 1},
                       {1, 1, 0, 1},
                       {1, 1, 0, 1},
                       {1, 1, 1, 1}};
        //@formatter:on
        IMatrix<Integer> matrix = new CrossMatrix(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(0, 0));

        List<List<Index>> expectedPaths = new ArrayList<>(0);

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        Collection<Collection<Index>> paths = findPaths.findAllPaths(Index.from(3, 3));

        // Assert
        pathsValidation(expectedPaths, paths);
    }

    @Test
    public void testFindAllPaths_useStandardMatrix_findOnePath() {
        // Arrange
        //@formatter:off
        int[][] mat = {{1, 0, 1, 1, 0},
                       {1, 1, 0, 0, 1},
                       {1, 0, 1, 0, 1},
                       {1, 0, 1, 0, 1},
                       {1, 1, 1, 1, 1}};
        //@formatter:on
        IMatrix<Integer> matrix = new StandardMatrix(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(0, 0));

        Collection<Collection<Index>> expectedPaths = new ArrayList<>(1);
        expectedPaths.add(Arrays.asList(Index.from(0, 0), Index.from(1, 0), Index.from(2, 0), Index.from(3, 0), Index.from(4, 0), Index.from(4, 1), Index.from(4, 2), Index.from(3, 2), Index.from(2, 2)));

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        Collection<Collection<Index>> paths = findPaths.findAllPaths(Index.from(2, 2));

        // Assert
        pathsValidation(expectedPaths, paths);
    }

    @Test
    public void testFindAllPaths_useCrossMatrix_findOnePath() {
        // Arrange
        //@formatter:off
        int[][] mat = {{1, 1, 0, 1},
                       {1, 1, 0, 1},
                       {1, 1, 1, 1},
                       {1, 0, 1, 1},
                       {1, 1, 1, 1}};
        //@formatter:on
        IMatrix<Integer> matrix = new CrossMatrix(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(0, 0));

        Collection<Collection<Index>> expectedPaths = new ArrayList<>(1);
        expectedPaths.add(Arrays.asList(Index.from(0, 0), Index.from(1, 1), Index.from(2, 2), Index.from(3, 3)));

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        Collection<Collection<Index>> paths = findPaths.findAllPaths(Index.from(3, 3));

        // Assert
        pathsValidation(expectedPaths, paths);
    }

    @Test
    public void testFindAllPaths_useStandardMatrix_findThreePaths() {
        // Arrange
        //@formatter:off
        int[][] mat = {{1, 1, 1, 1, 0},
                       {1, 0, 1, 0, 1},
                       {1, 1, 1, 1, 1},
                       {1, 0, 1, 0, 1},
                       {1, 1, 1, 0, 1}};
        //@formatter:on
        IMatrix<Integer> matrix = new StandardMatrix(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(0, 0));

        List<List<Index>> expectedPaths = new ArrayList<>(3);
        expectedPaths.add(Arrays.asList(Index.from(0, 0), Index.from(0, 1), Index.from(0, 2), Index.from(1, 2), Index.from(2, 2)));
        expectedPaths.add(Arrays.asList(Index.from(0, 0), Index.from(1, 0), Index.from(2, 0), Index.from(2, 1), Index.from(2, 2)));
        expectedPaths.add(Arrays.asList(Index.from(0, 0), Index.from(1, 0), Index.from(2, 0), Index.from(3, 0), Index.from(4, 0), Index.from(4, 1), Index.from(4, 2), Index.from(3, 2), Index.from(2, 2)));

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        Collection<Collection<Index>> paths = findPaths.findAllPaths(Index.from(2, 2));

        // Assert
        pathsValidation(expectedPaths, paths);
    }

    @Test
    public void testFindAllPaths_useCrossMatrix_findThreePaths() {
        // Arrange
        //@formatter:off
        int[][] mat = {{1, 0, 1, 0},
                       {0, 1, 0, 1},
                       {1, 1, 1, 1},
                       {0, 1, 0, 1}};
        //@formatter:on
        IMatrix<Integer> matrix = new CrossMatrix(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(0, 0));

        List<List<Index>> expectedPaths = new ArrayList<>(3);
        expectedPaths.add(Arrays.asList(Index.from(0, 0), Index.from(1, 1), Index.from(2, 2)));
        expectedPaths.add(Arrays.asList(Index.from(0, 0), Index.from(1, 1), Index.from(2, 0), Index.from(3, 1), Index.from(2, 2)));
        expectedPaths.add(Arrays.asList(Index.from(0, 0), Index.from(1, 1), Index.from(0, 2), Index.from(1, 3), Index.from(2, 2)));

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        Collection<Collection<Index>> paths = findPaths.findAllPaths(Index.from(2, 2));

        // Assert
        pathsValidation(expectedPaths, paths);
    }

    @Test
    public void testFindAllPaths_useStandardMatrixWithAnotherRoot_findOnePath() {
        // Arrange
        //@formatter:off
        int[][] mat = {{1, 0, 1, 1, 0},
                       {1, 1, 1, 0, 1},
                       {1, 0, 0, 1, 1},
                       {1, 0, 1, 0, 1},
                       {1, 1, 1, 1, 1}};
        //@formatter:on
        IMatrix<Integer> matrix = new StandardMatrix(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(1, 2));

        List<List<Index>> expectedPaths = new ArrayList<>(1);
        expectedPaths.add(Arrays.asList(Index.from(1, 2), Index.from(1, 1), Index.from(1, 0), Index.from(2, 0), Index.from(3, 0), Index.from(4, 0), Index.from(4, 1), Index.from(4, 2), Index.from(3, 2)));

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        Collection<Collection<Index>> paths = findPaths.findAllPaths(Index.from(3, 2));

        // Assert
        pathsValidation(expectedPaths, paths);
    }

    @Test
    public void testFindAllPaths_useCrossMatrixWithAnotherRoot_findOnePath() {
        // Arrange
        //@formatter:off
        int[][] mat = {{1, 1, 0, 1},
                       {0, 1, 1, 1},
                       {1, 1, 1, 0},
                       {1, 0, 1, 1},
                       {1, 0, 1, 1}};
        //@formatter:on
        IMatrix<Integer> matrix = new CrossMatrix(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(1, 2));

        List<List<Index>> expectedPaths = new ArrayList<>(1);
        expectedPaths.add(Arrays.asList(Index.from(1, 2), Index.from(2, 1), Index.from(3, 2), Index.from(4, 3)));

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        Collection<Collection<Index>> paths = findPaths.findAllPaths(Index.from(4, 3));

        // Assert
        pathsValidation(expectedPaths, paths);
    }

    @Test
    public void testFindAllPaths_useStandardMatrixWithAnotherRoot_findThreePaths() {
        // Arrange
        //@formatter:off
        int[][] mat = {{1, 1, 1, 1, 0},
                       {1, 0, 1, 0, 1},
                       {1, 1, 1, 0, 1},
                       {1, 0, 1, 0, 1},
                       {1, 1, 1, 0, 1}};
        //@formatter:on
        IMatrix<Integer> matrix = new StandardMatrix(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(1, 2));

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        Collection<Collection<Index>> paths = findPaths.findAllPaths(Index.from(2, 2));

        // Assert
        Assertions.assertEquals(3, paths.size());
    }

    @Test
    public void testFindAllPaths_useCrossMatrixWithAnotherRoot_findThreePaths() {
        // Arrange
        //@formatter:off
        int[][] mat = {{1, 0, 1, 0},
                       {0, 1, 0, 1},
                       {1, 1, 1, 1},
                       {0, 1, 0, 1}};
        //@formatter:on
        IMatrix<Integer> matrix = new CrossMatrix(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(1, 3));

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        Collection<Collection<Index>> paths = findPaths.findAllPaths(Index.from(2, 2));

        // Assert
        Assertions.assertEquals(3, paths.size());
    }

    @Test
    public void testFindAllPaths_useRegularMatrixWithAnotherRoot_findAllPaths() {
        // Arrange
        //@formatter:off
        int[][] mat = {{1, 1, 1, 0, 0, 1, 1, 0, 1, 0},
                       {0, 0, 0, 0, 0, 1, 1, 1, 0, 0},
                       {0, 1, 0, 1, 0, 0, 1, 1, 0, 1},
                       {1, 1, 1, 1, 0, 0, 0, 0, 1, 0},
                       {0, 0, 0, 1, 1, 1, 1, 0, 0, 0},
                       {0, 0, 0, 0, 1, 1, 1, 0, 1, 1},
                       {1, 1, 0, 0, 0, 0, 1, 1, 0, 1},
                       {0, 0, 1, 1, 1, 0, 1, 1, 1, 0},
                       {1, 0, 1, 1, 0, 1, 1, 0, 0, 0},
                       {1, 0, 0, 0, 1, 1, 0, 0, 1, 0}};
        //@formatter:on
        IMatrix<Integer> matrix = new RegularMatrix(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(3, 1));

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        Collection<Collection<Index>> paths = findPaths.findAllPaths(Index.from(8, 3));

        // Assert
        Assertions.assertEquals(1138020, paths.size());
    }
}
