package org.hit.internetprogramming.eoh.server.graph.algorithm;

import lombok.NonNull;
import org.hit.internetprogramming.eoh.common.graph.IGraph;

import java.util.Map;

/**
 * This interface created in order to share the signatures to the shortest path finding algorithms. Those
 * are BFS and Bellman-Ford.<br/>
 * This way we can use the algorithms with the same API at {@link FindPaths}
 * @param <V> Type of elements in an {@link IGraph} (We use the {@link org.hit.internetprogramming.eoh.common.mat.Index} class)
 */
public interface ShortestPathAlgorithm<V> {
    /**
     * See {@link BFSVisit#traverse(IGraph)}, {@link BellmanFord#traverse(IGraph)} and {@link DijkstraWithNegCycleSupport#traverse(IGraph)}
     * @param graph The graph to traverse, starting from its root.
     * @return All visited vertices
     */
    Map<V, VertexDistanceInfo<V>> traverse(@NonNull IGraph<V> graph);

    /**
     * See {@link BFSVisit#traverse(IGraph, Object)}, {@link BellmanFord#traverse(IGraph, Object)} and {@link DijkstraWithNegCycleSupport#traverse(IGraph, Object)}
     * @param graph The graph to traverse, starting from its root.
     * @param destination Where to stop. This is a vertex we are looking for.
     * @return All visited vertices
     */
    Map<V, VertexDistanceInfo<V>> traverse(@NonNull IGraph<V> graph, V destination);

    enum Algorithm {
        /**
         * {@link BFSVisit}
         */
        BFS,

        /**
         * {@link BellmanFord}
         */
        BELLMAN_FORD,

        /**
         * {@link DijkstraWithNegCycleSupport}
         */
        DIJKSTRA
    }
}
