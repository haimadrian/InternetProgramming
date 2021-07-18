package org.hit.internetprogramming.eoh.server.graph.algorithm;

import lombok.NonNull;
import org.hit.internetprogramming.eoh.common.graph.IGraph;

import java.util.*;

/**
 * A class that implements BFS algorithm in order to find shortest paths in a graph.
 *
 * @param <V> Type of elements in an {@link IGraph} (We use the {@link org.hit.internetprogramming.eoh.common.mat.Index} class)
 * @author Haim Adrian
 * @since 10-Jul-21
 */
public class BFSVisit<V> implements ShortestPathAlgorithm<V> {
    /**
     * A queue we are using during the BFS algorithm, to traverse the graph.
     */
    private final ThreadLocal<Deque<V>> workingQueue = ThreadLocal.withInitial(ArrayDeque::new);

    /**
     * A map between visited vertices and their info. (distance, parents)
     */
    private final ThreadLocal<Map<V, VertexDistanceInfo<V>>> visitedVertices = ThreadLocal.withInitial(HashMap::new);

    /**
     * Algorithm:<br/>
     * <pre>{@code
     * Add root of the graph to the workingQueue
     * While queue is not empty:
     *     currVertex = remove from queue.
     *     Invoke getReachableVertices on currVertex.
     *     For each reachableVertex do:
     *         If the distance[reachableVertex] is longer than distance[currVertex] + 1:
     *             Add reachableVertex to queue
     *             Update distance of reachableVertex to be distance[currVertex] + 1
     *             Clear previously stored parents of reachableVertex, and set currVertex as its parent
     *         Else if distance[reachableVertex] equals distance[currVertex] + 1:
     *             Add currVertex as additional parent of reachableVertex
     * }</pre>
     * @param graph The graph to traverse
     * @return Vertex info about all vertices we have visited while traversing from root. (Not necessarily all vertices in the graph)
     * @see #traverse(IGraph, Object)
     */
    @Override
    public Map<V, VertexDistanceInfo<V>> traverse(@NonNull IGraph<V> graph) {
        return traverse(graph, null);
    }

    /**
     * This method differs from {@link #traverse(IGraph)} by defining a vertex to stop searching from.<br/>
     * This is a destination vertex to avoid of running a full BFS, and just run it until some target.
     * Algorithm:<br/>
     * <pre>{@code
     * Add root of the graph to the workingQueue
     * While queue is not empty:
     *     currVertex = remove from queue.
     *     Invoke getReachableVertices on currVertex.
     *     For each reachableVertex do:
     *         If the distance[reachableVertex] is longer than distance[currVertex] + 1:
     *             Add reachableVertex to queue
     *             Update distance of reachableVertex to be distance[currVertex] + 1
     *             Clear previously stored parents of reachableVertex, and set currVertex as its parent
     *         Else if distance[reachableVertex] equals distance[currVertex] + 1:
     *             Add currVertex as additional parent of reachableVertex
     * }</pre>
     * @param graph The graph to traverse
     * @param destination A destination vertex, such that when we reach it we stop traversing. May be {@code null}. When null, it works the same as {@link #traverse(IGraph)}.
     * @return Vertex info about all vertices we have visited while traversing from root. (Not necessarily all vertices in the graph)
     * @see #traverse(IGraph)
     */
    @Override
    public Map<V, VertexDistanceInfo<V>> traverse(@NonNull IGraph<V> graph, V destination) {
        Deque<V> workingQueue = this.workingQueue.get();
        Map<V, VertexDistanceInfo<V>> visitedVertices = this.visitedVertices.get();

        workingQueue.clear();
        visitedVertices.clear();

        V currVertex = graph.getRoot();
        workingQueue.add(currVertex);
        visitedVertices.computeIfAbsent(currVertex, VertexDistanceInfo::new).setDistance(0);

        // As long as we haven't reached nowhere (We scanned all reachable vertices)
        while (!workingQueue.isEmpty()) {
            currVertex = workingQueue.remove();

            // In case we have reached to destination, stop traversing the graph.
            if (currVertex.equals(destination)) {
                // This will break the outer loop in an ordinary way
                workingQueue.clear();
            } else {
                Collection<V> reachableVertices = graph.getReachableVertices(currVertex);
                VertexDistanceInfo<V> parentVertexInfo = visitedVertices.get(currVertex);

                // Check if we have reached to destination, to avoid of adding other neighbors.
                if ((destination != null) && reachableVertices.contains(destination)) {
                    updateVisitedVertexIfNecessary(destination, currVertex, parentVertexInfo, workingQueue, visitedVertices);
                } else {
                    for (V currReachableVertex : reachableVertices) {
                        updateVisitedVertexIfNecessary(currReachableVertex, currVertex, parentVertexInfo, workingQueue, visitedVertices);
                    }
                }
            }
        }

        return visitedVertices;
    }

    /**
     * A helper method extracted from {@link #traverse(IGraph, Object)}, to avoid of duplicating vertex handling
     * code. In this method we check if a shorter path was found, to update lengths and parents of a vertex, thus
     * keeping the shortest paths only.
     * @param vertex The vertex to test
     * @param parentVertex Optional parent of the vertex we test
     * @param parentVertexInfo Vertex info to avoid of looking up is visitedVertices when running inside a loop
     * @param workingQueue A queue to insert into when the vertex distance got updated with a shorter distance
     * @param visitedVertices Map of previously visited vertices, to get their info
     */
    private void updateVisitedVertexIfNecessary(V vertex, V parentVertex, VertexDistanceInfo<V> parentVertexInfo, Deque<V> workingQueue, Map<V, VertexDistanceInfo<V>> visitedVertices) {
        VertexDistanceInfo<V> vertexInfo = visitedVertices.computeIfAbsent(vertex, VertexDistanceInfo::new);

        // In case the path from parent is shorter than the computed one, keep the shorter path and add that
        // vertex to the queue so we will continue traversing until we reach destination.
        if (vertexInfo.getDistance() > (parentVertexInfo.getDistance() + 1)) {
            vertexInfo.setDistance(parentVertexInfo.getDistance() + 1);
            workingQueue.add(vertex);

            // Clear old (longer) parents. We will add the new parent down below.
            vertexInfo.getParents().clear();
        }

        // When the distance equals, save current vertex as additional parent.
        // We might get here after the previous if as well, as we've updated the distance.
        if (vertexInfo.getDistance() == (parentVertexInfo.getDistance() + 1)) {
            vertexInfo.getParents().add(parentVertex);
        }
    }
}
