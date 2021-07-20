package org.hit.internetprogramming.eoh.server.graph.algorithm;

import lombok.RequiredArgsConstructor;
import org.hit.internetprogramming.eoh.common.graph.IGraph;
import org.hit.internetprogramming.eoh.server.common.exception.InputTooLargeException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Algorithm that runs on an {@link org.hit.internetprogramming.eoh.common.graph.IGraph} instance.<br/>
 * This class implements two algorithms:
 * <ul>
 *     <li>{@link #findShortestPaths(Object) Find Shortest Paths} - to find all shortest paths from a graph's root vertex, to some destination vertex.</li>
 *     <li>{@link #findAllPaths(Object) Find All Paths} - to find all (not necessarily short) paths from a graph's root vertex, to some destination vertex.</li>
 * </ul>
 * Note that the result of these algorithms is already sorted. Meaning that you can traverse each path to have the actual
 * path (directed) from root vertex to destination.
 * @author Haim Adrian
 * @since 30-May-21
 * @param <T> Same type of elements in the {@link IGraph} instance that this class works with. (Which is {@link org.hit.internetprogramming.eoh.common.mat.Index} in our case)
 */
@RequiredArgsConstructor
public class FindPaths<T> {
    /**
     * Exercise two is limited to accept matrices of size 50x50 maximum, hence we hold this constant
     * so we can compare it against {@link IGraph#getGraphSize()}, and throw exception when size of the graph
     * exceeds 50x50.
     */
    private static final int MAXIMUM_SIZE_OF_GRAPH_FOR_BFS = 50*50;

    /**
     * A graph to find paths in
     */
    private final IGraph<T> graph;

    /**
     * Algorithms are lazily initialized. Those can be {@link BFSVisit} or {@link BellmanFord}
     * {@link BFSVisit} is lazily initialized at {@link #findShortestPaths(Object)}.
     */
    private final Map<ShortestPathAlgorithm.Algorithm, ShortestPathAlgorithm<T>> algorithms = new HashMap<>();

    /**
     * Find all shortest paths in the specified graph (passed to this {@link FindPaths}) between {@code root} and {@code to}.
     * @param to The vertex to get to.
     * @return Collection of all paths to destination vertex, or empty if we could not reach to destination.
     * @throws InputTooLargeException When input graph (matrix) is larger than 50x50
     */
    public List<Collection<T>> findShortestPaths(T to) throws InputTooLargeException {
        // Make sure input does not exceed 50x50, according to the assignment demand.
        if (graph.getGraphSize() > MAXIMUM_SIZE_OF_GRAPH_FOR_BFS) {
            throw new InputTooLargeException("Input graph is too large! Maximum size is: 50x50");
        }

        List<Collection<T>> paths = new ArrayList<>();

        ShortestPathAlgorithm<T> bfsAlgorithm = algorithms.computeIfAbsent(ShortestPathAlgorithm.Algorithm.BFS, algo -> new BFSVisit<>());
        Map<T, VertexDistanceInfo<T>> visitedVertices = bfsAlgorithm.traverse(graph, to);

        // It might be that destination was not reachable. So don't find paths in case destination wasn't reachable.
        if (visitedVertices.containsKey(to)) {
            findShortestPaths(to, new LinkedList<>(), new HashSet<>(), paths, visitedVertices);
        }

        return paths;
    }

    /**
     * Find all shortest paths in the specified weighted graph (passed to this {@link FindPaths}) between {@code root} and {@code to}.
     * @param to The vertex to get to.
     * @return Collection of all paths to destination vertex, or empty if we could not reach to destination.
     */
    public List<Collection<T>> findShortestPathsInWeightedGraph(T to) {
        List<Collection<T>> paths = new ArrayList<>();

        ShortestPathAlgorithm<T> bellmanFordAlgorithm = algorithms.computeIfAbsent(ShortestPathAlgorithm.Algorithm.BELLMAN_FORD, algo -> new BellmanFord<>());
        Map<T, VertexDistanceInfo<T>> visitedVertices = bellmanFordAlgorithm.traverse(graph, to);

        // It might be that destination was not reachable. So don't find paths in case destination wasn't reachable.
        if (visitedVertices.containsKey(to)) {
            findShortestPaths(to, new LinkedList<>(), new HashSet<>(), paths, visitedVertices);
        }

        return paths;
    }

    @SuppressWarnings("unchecked")
    private void findShortestPaths(T currentVertex, LinkedList<T> currentPath, Set<T> currentPathBackedBy, List<Collection<T>> paths, Map<T, VertexDistanceInfo<T>> visitedVertices) {
        // In case of cycle, exit.
        if (currentPathBackedBy.contains(currentVertex)) {
            return;
        }

        // Add current vertex as first vertex in the path, cause we traverse from leaves to root.
        currentPath.addFirst(currentVertex);
        currentPathBackedBy.add(currentVertex);
        VertexDistanceInfo<T> currentVertexInfo = visitedVertices.get(currentVertex);

        // Base case - no parents means a root.
        if (currentVertexInfo.getParents().isEmpty()) {
            // Once we finish with a path, clone it so it will not be affected by other paths.
            paths.add((LinkedList<T>) currentPath.clone());
        } else {
            // Go over all parents and call the method recursively, so we will add all paths. (a different path for each parent)
            for (T parent : currentVertexInfo.getParents()) {
                findShortestPaths(parent, currentPath, currentPathBackedBy, paths, visitedVertices);
            }
        }

        // Remove current vertex that we have inserted at the beginning of this method, so we will not affect
        // other recursive paths.
        currentPath.removeFirst();
        currentPathBackedBy.remove(currentVertex);
    }

    /**
     * Find all paths in the specified graph (passed to this {@link FindPaths}) between {@code root} and {@code to}.
     * @param to The vertex to get to.
     * @return Collection of all paths to destination vertex, or empty if we could not reach to destination.
     */
    public List<Collection<T>> findAllPaths(T to) {
        return findAllPaths(graph.getRoot(), to, new HashSet<>()).stream().filter(path -> !path.isEmpty()).collect(Collectors.toList());
    }

    /**
     * A helper method we use in order to find all sub paths from some vertex to destination.<br/>
     * We use a set in order to make sure we do not traverse in circles.<br/>
     * The method will never return null. In case there is no path to destination, the response will be empty.
     * @param to The vertex to get to.
     * @param visited Set of vertices to fill in with vertices in each path, so we will not traverse in circles.
     * @return Collection of all paths to destination vertex, or empty if we could not reach to destination.
     */
    private List<List<T>> findAllPaths(T from, T to, Set<T> visited) {
        List<List<T>> paths = new ArrayList<>();

        // In case from and to are the same vertex, return it as the path.
        if (from.equals(to)) {
            // Create a new one and not Arrays.as, cause we need a writable collection
            List<T> path = new ArrayList<>();
            path.add(from);
            paths.add(path);
        } else {
            // Mark current vertex as a visited one, so we will not go in circles
            visited.add(from);
            Collection<T> adjacentVertices = graph.getReachableVertices(from);

            for (T adjacentVertex : adjacentVertices) {
                // Avoid from traversing in circles
                if (!visited.contains(adjacentVertex)) {
                    paths.addAll(findAllPaths(adjacentVertex, to, visited).stream().filter(path -> !path.isEmpty()).collect(Collectors.toList()));
                }
            }

            // Remove current vertex from visited vertices, so we will not affect other recursive calls. (other paths)
            visited.remove(from);

            // For each non-empty path, add 'from' as the first node in the path.
            paths.forEach(currPath -> currPath.add(0, from));
        }

        return paths;
    }
}

