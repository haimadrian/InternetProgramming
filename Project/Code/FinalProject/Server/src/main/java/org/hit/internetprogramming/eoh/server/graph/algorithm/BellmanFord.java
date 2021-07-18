package org.hit.internetprogramming.eoh.server.graph.algorithm;

import lombok.NonNull;
import org.apache.commons.lang3.tuple.Pair;
import org.hit.internetprogramming.eoh.common.graph.IGraph;
import org.hit.internetprogramming.eoh.server.common.exception.NegativeWeightCycleException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class that implements Bellman-Ford algorithm in order to find shortest paths in a weighted graph.
 *
 * @param <V> Type of elements in an {@link IGraph} (We use the {@link org.hit.internetprogramming.eoh.common.mat.Index} class)
 * @author Haim Adrian
 * @since 18-Jul-21
 */
public class BellmanFord<V> implements ShortestPathAlgorithm<V> {
    @Override
    public Map<V, VertexDistanceInfo<V>> traverse(@NonNull IGraph<V> graph) {
        return traverse(graph, null);
    }

    @Override
    public Map<V, VertexDistanceInfo<V>> traverse(@NonNull IGraph<V> graph, V destination) {
        Map<V, VertexDistanceInfo<V>> visitedVertices = new HashMap<>();

        List<V> vertices = graph.getVertices();
        List<Pair<V, V>> edges = graph.getEdges();

        // Step 1: Source vertex to source vertex weight 0, which means no move.
        visitedVertices.computeIfAbsent(graph.getRoot(), VertexDistanceInfo::new).setDistance(0);

        // Step 2: Relax edges repeatedly
        // Repeat |V|-1 times:
        for (int i = 1; i < vertices.size(); i++) {
            // For each edge (u, v) in edges do:
            for (Pair<V, V> edge : edges) {
                // Weight of the edge between u to v
                int weight = graph.getValue(edge.getRight());

                // Avoid of overflow
                long uVertexWeight = visitedVertices.computeIfAbsent(edge.getLeft(), VertexDistanceInfo::new).getDistance();
                if (uVertexWeight != Long.MAX_VALUE) {
                    long newWeight = uVertexWeight + weight;
                    if (newWeight < visitedVertices.computeIfAbsent(edge.getRight(), VertexDistanceInfo::new).getDistance()) {
                        visitedVertices.get(edge.getRight()).setDistance(newWeight);

                        // Clear old parents
                        visitedVertices.get(edge.getRight()).getParents().clear();
                    }

                    if (newWeight == visitedVertices.get(edge.getRight()).getDistance()) {
                        visitedVertices.get(edge.getRight()).getParents().add(edge.getLeft());
                    }
                }
            }
        }

        // Step 3: Check for negative weight cycles
        // For each edge (u, v) in edges do:
        for (Pair<V, V> edge : edges) {
            // Weight of the edge between u to v
            int weight = graph.getValue(edge.getRight());
            if (visitedVertices.get(edge.getLeft()).getDistance() + weight < visitedVertices.get(edge.getRight()).getDistance()) {
                throw new NegativeWeightCycleException("Graph contains negative cycle. No shortest path can be found.");
            }
        }

        return visitedVertices;
    }
}
