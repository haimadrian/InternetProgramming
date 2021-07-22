package org.hit.internetprogramming.eoh.server.graph.algorithm;

import org.hit.internetprogramming.eoh.common.graph.IGraph;
import org.hit.internetprogramming.eoh.common.graph.MatrixGraphAdapter;
import org.hit.internetprogramming.eoh.common.mat.IMatrix;
import org.hit.internetprogramming.eoh.common.mat.Index;
import org.hit.internetprogramming.eoh.common.mat.impl.StandardMatrix;
import org.hit.internetprogramming.eoh.server.common.exception.NegativeWeightCycleException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Test class for {@link FindPaths} algorithm.
 * @author Haim Adrian
 * @since 22-Jul-21
 */
public class FindPathsInWeightedGraphTest {
    @Test
    public void testFindShortestPathsBellmanFord_useWeightedGraph_findShortestPath() {
        // Arrange
        //@formatter:off
        Integer[][] mat = {{100, 100, 100},
                           {500, 900, 300}};
        //@formatter:on
        IMatrix<Integer> matrix = new StandardMatrix<>(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(1, 0));

        List<List<Index>> expectedPaths = new ArrayList<>(1);
        expectedPaths.add(Arrays.asList(Index.from(1, 0), Index.from(0, 0), Index.from(0, 1), Index.from(0, 2), Index.from(1, 2)));

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        Collection<Collection<Index>> paths = findPaths.findShortestPathsInWeightedGraphBellmanFord(Index.from(1, 2));

        // Assert
        FindPathsTest.pathsValidation(expectedPaths, paths);
    }

    @Test
    public void testFindShortestPathsBellmanFord_useWeightedGraph_findShortestPathWithNegativeWeight() {
        // Arrange
        //@formatter:off
        Integer[][] mat = {{100,  100, 100},
                           {500, -100, 300}};
        //@formatter:on
        IMatrix<Integer> matrix = new StandardMatrix<>(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(1, 0));

        List<List<Index>> expectedPaths = new ArrayList<>(1);
        expectedPaths.add(Arrays.asList(Index.from(1, 0), Index.from(1, 1), Index.from(1, 2)));

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        Collection<Collection<Index>> paths = findPaths.findShortestPathsInWeightedGraphBellmanFord(Index.from(1, 2));

        // Assert
        FindPathsTest.pathsValidation(expectedPaths, paths);
    }

    @Test
    public void testFindShortestPathsBellmanFord_useWeightedGraph_failOnNegativeCycle() {
        // Arrange
        //@formatter:off
        Integer[][] mat = {{100, -100, -100},
                           {500,  900,  300}};
        //@formatter:on
        IMatrix<Integer> matrix = new StandardMatrix<>(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(1, 0));

        Throwable thrown = null;

        // Act
        try {
            FindPaths<Index> findPaths = new FindPaths<>(graph);
            findPaths.findShortestPathsInWeightedGraphBellmanFord(Index.from(1, 2));
        } catch (Exception e) {
            thrown = e;
        }

        // Assert
        Assertions.assertNotNull(thrown, "Supposed to fail due to negative cycle");
        Assertions.assertTrue(thrown instanceof NegativeWeightCycleException, "Supposed to fail with negative cycle exception");
    }

    @Test
    public void testFindShortestPathsBellmanFord_useWeightedGraph_findShortestPathBigGraph() {
        // Arrange
        //@formatter:off
        Integer[][] mat = {{100,  100, 100,  100, 300, -200,  300, 100},
                           {500, -100, 900,  100, 300,  300,  300, 300},
                           {500,  200, 300,  100, 100,  100,  100, 300},
                           {500,  100, 300,  900, 100,  100,  100, 300},
                           {500, -100, 300, -100, 900,  100,  100, 300},
                           {500,  200, 900,  100, 900,  100,  100, 300},
                           {500,  100, 900,  100, 100,  100,  -50, 300},
                           {500, -100, 900,  200, 200,  100,  100, 300}};
        //@formatter:on
        IMatrix<Integer> matrix = new StandardMatrix<>(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(1, 0));

        List<List<Index>> expectedPaths = new ArrayList<>(1);
        expectedPaths.add(Arrays.asList(Index.from(1, 0), Index.from(1, 1),
                Index.from(2, 1),
                Index.from(3, 1),
                Index.from(4, 1), Index.from(4, 2), Index.from(4, 3),
                Index.from(5, 3),
                Index.from(6, 3), Index.from(6, 4), Index.from(6, 5), Index.from(6, 6),
                Index.from(7, 6), Index.from(7, 7)));

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        Collection<Collection<Index>> paths = findPaths.findShortestPathsInWeightedGraphBellmanFord(Index.from(7, 7));

        // Assert
        FindPathsTest.pathsValidation(expectedPaths, paths);
    }

    @Test
    public void testFindShortestPathsBellmanFord_useWeightedGraph_find2ShortestPaths() {
        // Arrange
        //@formatter:off
        Integer[][] mat = {{100,  100, 100,  100, 300, -200, 300, 100},
                           {500, -100, 900,  100, 300,  300, 300, 300},
                           {500,  200, 300,  100, 100,  100, 100, 300},
                           {500,  100, 300,  900, 100,  100, 100, 300},
                           {500, -100, 300, -100, 900,  100, 100, 300},
                           {500,  200, 900,  100, 900,  100, 100, 300},
                           {500,  100, 900,  100, 100,  100, 100, 300},
                           {500, -100, 900,  200, 200,  100, 100, 300}};
        //@formatter:on
        IMatrix<Integer> matrix = new StandardMatrix<>(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(1, 0));

        List<List<Index>> expectedPaths = new ArrayList<>(1);
        expectedPaths.add(Arrays.asList(Index.from(1, 0), Index.from(1, 1),
                Index.from(2, 1),
                Index.from(3, 1),
                Index.from(4, 1), Index.from(4, 2), Index.from(4, 3),
                Index.from(5, 3),
                Index.from(6, 3), Index.from(6, 4), Index.from(6, 5), Index.from(6, 6),
                Index.from(7, 6), Index.from(7, 7)));
        expectedPaths.add(Arrays.asList(Index.from(1, 0), Index.from(1, 1),
                Index.from(2, 1),
                Index.from(3, 1),
                Index.from(4, 1), Index.from(4, 2), Index.from(4, 3),
                Index.from(5, 3),
                Index.from(6, 3), Index.from(6, 4), Index.from(6, 5),
                Index.from(7, 5), Index.from(7, 6), Index.from(7, 7)));

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        Collection<Collection<Index>> paths = findPaths.findShortestPathsInWeightedGraphBellmanFord(Index.from(7, 7));

        // Assert
        FindPathsTest.pathsValidation(expectedPaths, paths);
    }

    @Test
    public void testFindShortestPathsBellmanFord_useWeightedGraph_findShortestPathParallel() {
        // Arrange
        //@formatter:off
        Integer[][] mat = {{100,   100,   100,    100,   300, -200,  300, 100, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {500,  -100,   900,    100,   300,  300,  300, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {500,   200,   300,    100,   200,  200,  100, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {500,   100,   300,    900,   100,  200,  100, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {500,  -100,   300,   -100,   900,  100,  100, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {500,   200,   900,    100,   900,  100,  500, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {500,   100,   900,    100,   100,  100, -100, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {500,  -100,   900,    200,   200,  200,  100, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {500,   500,   900,    500,   500,  500,  500, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {600,   600,   1000,   500,   500,  500,  500, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {700,   500,   900,    500,   500,  500,  500, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {800,   400,   900,    500,   500,  500,  500, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {900,   500,   900,    500,   500,  500,  500, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {500,   500,   900,    500,   500,  500,  500, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {600,   500,   900,    500,   500,  500,  500, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {700,   500,   900,    500,   500,  500,  500, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {800,   500,   900,    500,   500,  500,  500, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {900,   500,   900,    500,   500,  500,  500, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {500,   500,   900,    500,   500,  500,  500, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {600,   500,   900,    500,   500,  500,  500, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {700,   500,   900,    500,   500,  500,  500, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {500,   500,   900,    500,   500,  500,  500, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {20000, 500,   900,    500,   500,  500,  500, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {30000, 50000, 20000,  50000, 500,  500,  500, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500}};
        //@formatter:on
        IMatrix<Integer> matrix = new StandardMatrix<>(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(1, 0));

        List<List<Index>> expectedPaths = new ArrayList<>(1);
        expectedPaths.add(Arrays.asList(Index.from(1, 0), Index.from(1, 1),
                Index.from(2, 1),
                Index.from(3, 1),
                Index.from(4, 1), Index.from(4, 2), Index.from(4, 3),
                Index.from(5, 3),
                Index.from(6, 3), Index.from(6, 4), Index.from(6, 5), Index.from(6, 6),
                Index.from(7, 6), Index.from(7, 7)));

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        Collection<Collection<Index>> paths = findPaths.findShortestPathsInWeightedGraphBellmanFord(Index.from(7, 7));

        // Assert
        FindPathsTest.pathsValidation(expectedPaths, paths);
    }

    @Test
    public void testFindShortestPathsDijkstra_useWeightedGraph_findShortestPath() {
        // Arrange
        //@formatter:off
        Integer[][] mat = {{100, 100, 100},
                           {500, 900, 300}};
        //@formatter:on
        IMatrix<Integer> matrix = new StandardMatrix<>(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(1, 0));

        List<List<Index>> expectedPaths = new ArrayList<>(1);
        expectedPaths.add(Arrays.asList(Index.from(1, 0), Index.from(0, 0), Index.from(0, 1), Index.from(0, 2), Index.from(1, 2)));

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        Collection<Collection<Index>> paths = findPaths.findShortestPathsInWeightedGraphDijkstra(Index.from(1, 2));

        // Assert
        FindPathsTest.pathsValidation(expectedPaths, paths);
    }

    @Test
    public void testFindShortestPathsDijkstra_useWeightedGraph_findShortestPathWithNegativeWeight() {
        // Arrange
        //@formatter:off
        Integer[][] mat = {{100,  100, 100},
                           {500, -100, 300}};
        //@formatter:on
        IMatrix<Integer> matrix = new StandardMatrix<>(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(1, 0));

        List<List<Index>> expectedPaths = new ArrayList<>(1);
        expectedPaths.add(Arrays.asList(Index.from(1, 0), Index.from(1, 1), Index.from(1, 2)));

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        Collection<Collection<Index>> paths = findPaths.findShortestPathsInWeightedGraphDijkstra(Index.from(1, 2));

        // Assert
        FindPathsTest.pathsValidation(expectedPaths, paths);
    }

    @Test
    public void testFindShortestPathsDijkstra_useWeightedGraph_succeedThroughNegativeCycle() {
        // Arrange
        //@formatter:off
        Integer[][] mat = {{100, -100, -100},
                           {500,  900,  300}};
        //@formatter:on
        IMatrix<Integer> matrix = new StandardMatrix<>(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(1, 0));

        List<List<Index>> expectedPaths = new ArrayList<>(1);
        expectedPaths.add(Arrays.asList(Index.from(1, 0), Index.from(0, 0), Index.from(0, 1), Index.from(0, 2), Index.from(1, 2)));

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        Collection<Collection<Index>> paths = findPaths.findShortestPathsInWeightedGraphDijkstra(Index.from(1, 2));

        // Assert
        FindPathsTest.pathsValidation(expectedPaths, paths);
    }

    @Test
    public void testFindShortestPathsDijkstra_useWeightedGraph_succeedThroughLongNegativeCycle() {
        // Arrange
        //@formatter:off
        Integer[][] mat = {{-100, -100, -100},
                           { 500,  900,  300}};
        //@formatter:on
        IMatrix<Integer> matrix = new StandardMatrix<>(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(1, 0));

        List<List<Index>> expectedPaths = new ArrayList<>(1);
        expectedPaths.add(Arrays.asList(Index.from(1, 0), Index.from(0, 0), Index.from(0, 1), Index.from(0, 2), Index.from(1, 2)));

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        Collection<Collection<Index>> paths = findPaths.findShortestPathsInWeightedGraphDijkstra(Index.from(1, 2));

        // Assert
        FindPathsTest.pathsValidation(expectedPaths, paths);
    }

    @Test
    public void testFindShortestPathsDijkstra_useWeightedGraph_findShortestPathBigGraph() {
        // Arrange
        //@formatter:off
        Integer[][] mat = {{100,  100, 100,  100, 300, -200,  300, 100},
                           {500, -100, 900,  100, 300,  300,  300, 300},
                           {500,  200, 300,  100, 100,  100,  100, 300},
                           {500,  100, 300,  900, 100,  100,  100, 300},
                           {500, -100, 300, -100, 900,  100,  100, 300},
                           {500,  200, 900,  100, 900,  100,  100, 300},
                           {500,  100, 900,  100, 100,  100,  -50, 300},
                           {500, -100, 900,  200, 200,  100,  100, 300}};
        //@formatter:on
        IMatrix<Integer> matrix = new StandardMatrix<>(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(1, 0));

        List<List<Index>> expectedPaths = new ArrayList<>(1);
        expectedPaths.add(Arrays.asList(Index.from(1, 0), Index.from(1, 1),
                Index.from(2, 1),
                Index.from(3, 1),
                Index.from(4, 1), Index.from(4, 2), Index.from(4, 3),
                Index.from(5, 3),
                Index.from(6, 3), Index.from(6, 4), Index.from(6, 5), Index.from(6, 6),
                Index.from(7, 6), Index.from(7, 7)));

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        Collection<Collection<Index>> paths = findPaths.findShortestPathsInWeightedGraphDijkstra(Index.from(7, 7));

        // Assert
        FindPathsTest.pathsValidation(expectedPaths, paths);
    }

    @Test
    public void testFindShortestPathsDijkstra_useWeightedGraph_find2ShortestPaths() {
        // Arrange
        //@formatter:off
        Integer[][] mat = {{100,  100, 100,  100, 300, -200, 300, 100},
                           {500, -100, 900,  100, 300,  300, 300, 300},
                           {500,  200, 300,  100, 100,  100, 100, 300},
                           {500,  100, 300,  900, 100,  100, 100, 300},
                           {500, -100, 300, -100, 900,  100, 100, 300},
                           {500,  200, 900,  100, 900,  100, 100, 300},
                           {500,  100, 900,  100, 100,  100, 100, 300},
                           {500, -100, 900,  200, 200,  100, 100, 300}};
        //@formatter:on
        IMatrix<Integer> matrix = new StandardMatrix<>(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(1, 0));

        List<List<Index>> expectedPaths = new ArrayList<>(1);
        expectedPaths.add(Arrays.asList(Index.from(1, 0), Index.from(1, 1),
                Index.from(2, 1),
                Index.from(3, 1),
                Index.from(4, 1), Index.from(4, 2), Index.from(4, 3),
                Index.from(5, 3),
                Index.from(6, 3), Index.from(6, 4), Index.from(6, 5), Index.from(6, 6),
                Index.from(7, 6), Index.from(7, 7)));
        expectedPaths.add(Arrays.asList(Index.from(1, 0), Index.from(1, 1),
                Index.from(2, 1),
                Index.from(3, 1),
                Index.from(4, 1), Index.from(4, 2), Index.from(4, 3),
                Index.from(5, 3),
                Index.from(6, 3), Index.from(6, 4), Index.from(6, 5),
                Index.from(7, 5), Index.from(7, 6), Index.from(7, 7)));

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        Collection<Collection<Index>> paths = findPaths.findShortestPathsInWeightedGraphDijkstra(Index.from(7, 7));

        // Assert
        FindPathsTest.pathsValidation(expectedPaths, paths);
    }

    @Test
    public void testFindShortestPathsDijkstra_useWeightedGraph_findShortestPathParallel() {
        // Arrange
        //@formatter:off
        Integer[][] mat = {{100,   100,   100,    100,   300, -200,  300, 100, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {500,  -100,   900,    100,   300,  300,  300, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {500,   200,   300,    100,   200,  200,  100, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {500,   100,   300,    900,   100,  200,  100, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {500,  -100,   300,   -100,   900,  100,  100, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {500,   200,   900,    100,   900,  100,  500, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {500,   100,   900,    100,   100,  100, -100, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {500,  -100,   900,    200,   200,  200,  100, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {500,   500,   900,    500,   500,  500,  500, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {600,   600,   1000,   500,   500,  500,  500, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {700,   500,   900,    500,   500,  500,  500, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {800,   400,   900,    500,   500,  500,  500, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {900,   500,   900,    500,   500,  500,  500, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {500,   500,   900,    500,   500,  500,  500, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {600,   500,   900,    500,   500,  500,  500, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {700,   500,   900,    500,   500,  500,  500, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {800,   500,   900,    500,   500,  500,  500, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {900,   500,   900,    500,   500,  500,  500, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {500,   500,   900,    500,   500,  500,  500, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {600,   500,   900,    500,   500,  500,  500, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {700,   500,   900,    500,   500,  500,  500, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {500,   500,   900,    500,   500,  500,  500, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {20000, 500,   900,    500,   500,  500,  500, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500},
                           {30000, 50000, 20000,  50000, 500,  500,  500, 300, 500, 500, 500, 500, 500, 500, 500, 500, 500}};
        //@formatter:on
        IMatrix<Integer> matrix = new StandardMatrix<>(mat);
        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, Index.from(1, 0));

        List<List<Index>> expectedPaths = new ArrayList<>(1);
        expectedPaths.add(Arrays.asList(Index.from(1, 0), Index.from(1, 1),
                Index.from(2, 1),
                Index.from(3, 1),
                Index.from(4, 1), Index.from(4, 2), Index.from(4, 3),
                Index.from(5, 3),
                Index.from(6, 3), Index.from(6, 4), Index.from(6, 5), Index.from(6, 6),
                Index.from(7, 6), Index.from(7, 7)));

        // Act
        FindPaths<Index> findPaths = new FindPaths<>(graph);
        Collection<Collection<Index>> paths = findPaths.findShortestPathsInWeightedGraphDijkstra(Index.from(7, 7));

        // Assert
        FindPathsTest.pathsValidation(expectedPaths, paths);
    }
}
