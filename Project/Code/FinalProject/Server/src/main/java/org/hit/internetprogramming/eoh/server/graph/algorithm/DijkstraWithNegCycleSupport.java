package org.hit.internetprogramming.eoh.server.graph.algorithm;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.hit.internetprogramming.eoh.common.graph.IGraph;
import org.hit.internetprogramming.eoh.server.action.ActionThreadService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveAction;
import java.util.stream.Collectors;

/**
 * A class that implements Dijkstra algorithm in order to find shortest paths in a weighted graph.<br/>
 * There is additional guard in this implementation to detect negative cycles
 *
 * @param <V> Type of elements in an {@link IGraph} (We use the {@link org.hit.internetprogramming.eoh.common.mat.Index} class)
 * @author Haim Adrian
 * @since 22-Jul-21
 */
@Log4j2
public class DijkstraWithNegCycleSupport<V> implements ShortestPathAlgorithm<V> {
    /**
     * A thread safe structure holding all of the visited indices and their distance info,
     * to be used by our recursive actions and avoid of going in circles
     */
    private final Map<V, VertexDistanceInfo<V>> visitedVertices = new ConcurrentHashMap<>();

    /**
     * A destination vertex to look for.<br/>
     * If null, then we will traverse the whole graph.
     */
    private V destination;

    /**
     * @see #traverse(IGraph, Object)
     */
    @Override
    public Map<V, VertexDistanceInfo<V>> traverse(@NonNull IGraph<V> graph) {
        return traverse(graph, null);
    }

    /**
     * Algorithm: DIJKSTRA(G, w, s)<br/>
     * <pre>{@code
     * for each v ∈ G.V do:
     *     v.distance = ∞
     * s.distance = 0
     * S = ∅
     * Q = MinHeap(G.V) // vertices in Q ordered by distance, short-to-long
     * while !Q.isEmpty() do:
     *     u <- Q.pop()
     *     S.add(u)
     *     for each v ∈ neighbors(u) do:
     *         if v.distance > u.distance + w(u, v) then:
     *             v.distance <- u.distance + w(u, v)
     *             Q.heapify()
     * return TRUE
     * }</pre>
     *
     * @param graph       The graph to traverse, starting from its root.
     * @param destination A destination vertex to look for. If null, then we will traverse the whole graph.
     * @return All visited vertices, and their details (distance and path)
     */
    @Override
    public Map<V, VertexDistanceInfo<V>> traverse(@NonNull IGraph<V> graph, V destination) {
        this.destination = destination;

        if (visitedVertices.size() > 0) {
            visitedVertices.clear();
        }

        visitedVertices.computeIfAbsent(graph.getRoot(), VertexDistanceInfo::new).setDistance(0L);
        ShortestPathRecursiveAction task = new ShortestPathRecursiveAction(graph, graph.getRoot());
        ActionThreadService.getInstance().invoke(task);

        return visitedVertices;
    }

    /**
     * This class is a recursive action for a fork-join pool which splits itself to
     * multiple sub-tasks, recursively, such that each task can run on a different thread
     * from our fork-join pool.<br/>
     * This mechanism lets us implement Dijkstra algorithm using parallel search.
     */
    private class ShortestPathRecursiveAction extends RecursiveAction {
        private static final int THRESHOLD = 1;
        private final IGraph<V> graph;
        private final V vertex;

        /**
         * Constructs a new {@link ShortestPathRecursiveAction}
         * @param graph The graph we are searching in
         * @param vertex A vertex to calculate the distance of its neighbors from it, recursively
         */
        public ShortestPathRecursiveAction(IGraph<V> graph, V vertex) {
            this.graph = graph;
            this.vertex = vertex;
        }

        @Override
        protected void compute() {
            // If thread service instructed to shutdown now, we cannot continue executing.
            if (ActionThreadService.getInstance().isShutdownNow()) {
                return;
            }

            List<V> reachableVertices;
            long currVertexDistance = visitedVertices.computeIfAbsent(vertex, VertexDistanceInfo::new).getDistance();

            // Filter only those we have not reached to yet, or those that we've found a shortest path to.
            reachableVertices = graph.getReachableVertices(vertex).stream().filter(neighbor -> {
                long neighborDistance = visitedVertices.computeIfAbsent(neighbor, VertexDistanceInfo::new).getDistance();

                // For a vertex we are visiting for the first time, go ahead - there is nothing to limit here.
                if (neighborDistance == Long.MAX_VALUE) {
                    return true;
                }

                if (currVertexDistance + (int)graph.getValue(neighbor) <= neighborDistance) {
                    // Test for negative cycle
                    if ((currVertexDistance < 0) && ((int) graph.getValue(neighbor) < 0) && (isPredecessor(neighbor, vertex) || isPredecessor(vertex, neighbor))) {
                        // Collect necessary data and warn about the detected negative cycle.
                        Set<V> parents = new HashSet<>();
                        collectParentsRecursively(vertex, parents);
                        collectParentsRecursively(neighbor, parents);
                        log.warn("Negative cycle detected. Vertices: " + vertex + ", " + neighbor + ". Cycle with: " + parents);
                        return false;
                    }

                    // Continue to this neighbor as we've found a shorter distance
                    return true;
                }

                // Do not continue to this neighbor, cause its distance is not shorter than the existing one.
                return false;
            }).collect(Collectors.toList());

            if ((reachableVertices.size() >= THRESHOLD) && ((destination == null) || (!reachableVertices.contains(destination)))) {
                List<ShortestPathRecursiveAction> subTasks = createSubtasks(reachableVertices);
                if (!subTasks.isEmpty() && !ActionThreadService.getInstance().isShutdownNow()) {
                    invokeAll(subTasks);
                }
            } else if (reachableVertices.size() > 0) {
                if (reachableVertices.contains(destination)) {
                    processNeighbor(destination);
                } else {
                    processNeighbor(reachableVertices.get(0));
                }
            }
        }

        /**
         * Tests if a vertex is a predecessor of a specified vertex.<br/>
         * We use this method as a test to verify we do not enter into negative cycle.
         * @param vertex The vertex to check if one of its predecessors is the specified potentialPredecessor.
         * @param potentialPredecessor The vertex to check if it is one of the predecessors of the specified vertex.
         * @return Predecessor or not
         */
        private boolean isPredecessor(V vertex, V potentialPredecessor) {
            Set<V> parents = new HashSet<>();
            collectParentsRecursively(vertex, parents);
            return parents.contains(potentialPredecessor);
        }

        private void collectParentsRecursively(V vertex, Set<V> parents) {
            if (vertex == null) {
                return;
            }

            parents.add(vertex);
            visitedVertices.get(vertex).getParents().forEach(parent -> {
                // Avoid of cycles
                if (!parents.contains(parent)) {
                    collectParentsRecursively(parent, parents);
                }
            });
        }

        /**
         * Go over all reachable vertices, and for each vertex that we detect a shorter distance, we create a new subtask
         * to handle it.
         *
         * @param reachableVertices List of neighbor of a vertex
         * @return List of sub-tasks. May be empty in case of no update
         */
        private List<ShortestPathRecursiveAction> createSubtasks(List<V> reachableVertices) {
            List<ShortestPathRecursiveAction> subTasks = new ArrayList<>();

            for (V neighbor : reachableVertices) {
                if (processNeighbor(neighbor)) {
                    subTasks.add(new ShortestPathRecursiveAction(graph, neighbor));
                }
            }

            return subTasks;
        }

        /**
         * Process a neighbor. This applies the "relax" logic, where we update the distance of the neighbor, and its predecessor
         * in case we have reached it through a shorter distance.<br/>
         * In case there was any update, we will continue traversing from the neighbor, hence we return an indication
         * about update from this method.
         *
         * @param neighbor The neighbor to process
         * @return Whether there was any update to the neighbor info or not.
         */
        private boolean processNeighbor(V neighbor) {
            boolean isUpdated = false;
            VertexDistanceInfo<V> neighborInfo = visitedVertices.get(neighbor);

            // This block must be synchronized or we might get into a situation with two threads
            // where one clears the collection of parents and expects to be the only predecessor,
            // but then another thread adds its vertex as the predecessor and we end up having two
            // parents although there supposed to be one only.
            synchronized (neighborInfo.getParents()) {
                long neighborDistance = neighborInfo.getDistance();
                long newNeighborDistance = visitedVertices.get(vertex).getDistance() + (int) graph.getValue(neighbor);

                if (newNeighborDistance < neighborDistance) {
                    neighborInfo.setDistance(newNeighborDistance);
                    neighborInfo.getParents().clear();
                    neighborInfo.getParents().add(vertex);
                    isUpdated = true;
                }
                // Avoid of cycles
                else if ((newNeighborDistance == neighborDistance) && (!neighborInfo.getParents().contains(vertex))) {
                    neighborInfo.getParents().add(vertex);
                    isUpdated = true;
                }
            }

            return isUpdated;
        }

        @Override
        public String toString() {
            return "{" +
                    "vertex=" + vertex +
                    '}';
        }
    }
}
