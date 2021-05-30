package org.hit.internetprogramming.eoh.server.graph.algorithm;

import org.hit.internetprogramming.eoh.common.graph.IGraph;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Algorithm that runs on an {@link org.hit.internetprogramming.eoh.common.graph.IGraph} instance.<br/>
 * Find paths is an algorithm that collects all available paths from vertex A to vertex B,
 * with no duplications.<br/>
 * This functionality was introduced in the first lessons, and I do not know if we would even use it,
 * though I backup the code here. (took it from {@link org.hit.internetprogramming.eoh.common.graph.MatrixGraphAdapter}.
 * @author Haim Adrian
 * @since 30-May-21
 * @param <T> Same type of elements in the {@link IGraph} instance that this class works with. (Which is {@link org.hit.internetprogramming.eoh.common.mat.Index} in our case)
 */
public class FindPaths<T> {
    private final IGraph<T> graph;

    public FindPaths(IGraph<T> graph) {
        this.graph = graph;
    }

    public Collection<Collection<T>> findPaths(T from, T to) {
        return findPaths(from, to, new HashSet<>()).stream().filter(path -> !path.isEmpty()).collect(Collectors.toList());
    }

    /**
     * A helper method we use in order to find all sub paths from some vertex to destination.<br/>
     * We use a set in order to make sure we do not traverse in circles.<br/>
     * The method will never return null. In case there is no path to destination, the response will be empty.
     * @param from From which vertex to find paths until we reach to destination.
     * @param to The vertex to get to.
     * @param visited Set of vertices to fill in with vertices in each path, so we will not traverse in circles.
     * @return Collection of all paths to destination vertex, or empty if we could not reach to destination.
     */
    private List<List<T>> findPaths(T from, T to, Set<T> visited) {
        List<List<T>> paths = new ArrayList<>();

        // In case from and to are the same vertex, return it as the path.
        if (from.equals(to)) {
            // Create a new one and not Arrays.as, cause we need a writable collection
            List<T> path = new ArrayList<>();
            path.add(from);
            paths.add(path);
        } else {
            // Create a new set so one path will not affect other paths in the recursive calls. (Each path is a different sub-tree)
            Set<T> newSet = extendSetWith(visited, from);
            Collection<T> adjacentVertices = graph.getAdjacentVertices(from);

            for (T adjacentVertex : adjacentVertices) {
                // Avoid from traversing in circles
                if (!visited.contains(adjacentVertex)) {
                    paths.addAll(findPaths(adjacentVertex, to, newSet).stream().filter(path -> !path.isEmpty()).collect(Collectors.toList()));
                }
            }

            // For each non-empty path, add 'from' as the first node in the path.
            paths.forEach(currPath -> currPath.add(0, from));
        }

        return paths;
    }

    /**
     * When we traverse a graph and try to find path, we do not want to affect all paths when we are traversing one path,
     * so for each path we use a different set of 'visited' indices.
     * @param set The set to be extended
     * @param vertex A vertex to add to the set
     * @return A new set containing the elements from the specified set, and the specified vertex.
     */
    private Set<T> extendSetWith(Set<T> set, T vertex) {
        Set<T> newSet = new HashSet<>(set);
        newSet.add(vertex);
        return newSet;
    }
}

