package org.hit.internetprogramming.eoh.server.graph.algorithm;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.hit.internetprogramming.eoh.common.graph.IGraph;
import org.hit.internetprogramming.eoh.server.action.ActionThreadService;
import org.hit.internetprogramming.eoh.server.common.exception.NegativeWeightCycleException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A class that implements Bellman-Ford algorithm in order to find shortest paths in a weighted graph.
 *
 * @param <V> Type of elements in an {@link IGraph} (We use the {@link org.hit.internetprogramming.eoh.common.mat.Index} class)
 * @author Haim Adrian
 * @since 18-Jul-21
 */
@Log4j2
public class BellmanFord<V> implements ShortestPathAlgorithm<V> {
    private static final int MINIMUM__AMOUNT_OF_EDGES_PER_WORKER = 20;

    /**
     * Algorithm: BELLMAN-FORD(G, w, s)<br/>
     * <pre>{@code
     * for each v ∈ G.V do:
     *     v.distance = ∞
     * s.distance = 0
     * repeat |G.V|-1 times:
     *     for each edge (u, v) ∈ G.E do:
     *         RELAX(u, v, w) // Update weight if smaller
     * for each edge (u, v) ∈ G.E do:
     *     if v.distance > u.distance + w(u, v) then:
     *         return FALSE // (Error - a negative cycle found)
     * return TRUE
     * }</pre>
     * @param graph The graph to traverse, starting from its root.
     * @return All visited vertices, and their details (distance and path)
     */
    @Override
    public Map<V, VertexDistanceInfo<V>> traverse(@NonNull IGraph<V> graph) {
        log.info("BellmanFord traverse start");

        // Use a thread safe structure
        Map<V, VertexDistanceInfo<V>> visitedVertices = new ConcurrentHashMap<>();

        List<V> vertices = graph.getVertices();
        List<Pair<V, V>> edges = graph.getEdges();

        // Step 1: Source vertex to source vertex weight 0, which means no move.
        visitedVertices.computeIfAbsent(graph.getRoot(), VertexDistanceInfo::new).setDistance(0);
        List<Callable<Void>> tasks = new ArrayList<>();

        // Step 2: Relax edges repeatedly
        // Repeat |V|-1 times: (Longest simple path can be up to |V|-1 edges)
        for (int i = 1; i < vertices.size(); i++) {
            tasks.add(new RelaxTask<>(graph, edges, visitedVertices));
        }

        try {
            ActionThreadService.getInstance().invokeAll(tasks);
        } catch (InterruptedException e) {
            log.error("Bellman-Ford parallel search was interrupted.", e);
        }

        // Step 3: Check for negative weight cycles
        // For each edge (u, v) in edges do:
        for (Pair<V, V> edge : edges) {
            // Weight of the edge between u to v
            int weight = graph.getValue(edge.getRight());
            if (visitedVertices.get(edge.getLeft()).getDistance() + weight < visitedVertices.get(edge.getRight()).getDistance()) {
                throw new NegativeWeightCycleException("Graph contains negative cycle. No shortest path can be found. [Edge=" + edgeToString(visitedVertices, edge) + ", weight=" + weight + "]");
            }
        }

        log.info("BellmanFord traverse end");
        return visitedVertices;
    }

    /**
     * See {@link #traverse(IGraph)}
     * @param graph The graph to traverse, starting from its root.
     * @param destination Where to stop. This is irrelevant for Bellman-Ford
     * @return All visited vertices, and their details (distance and path)
     */
    @Override
    public Map<V, VertexDistanceInfo<V>> traverse(@NonNull IGraph<V> graph, @SuppressWarnings("unused") V destination) {
        return traverse(graph);
    }

    private void optionallyRunParallelRelax(IGraph<V> graph, List<Pair<V, V>> edges, Map<V, VertexDistanceInfo<V>> visitedVertices) {
        int portions = (ActionThreadService.getInstance().getAmountOfWorkers() / 2);
        int edgesPerWorker = edges.size() / portions;

        // Use parallel search only if we have at list the minimum of edges to separate between workers
        if (edgesPerWorker >= MINIMUM__AMOUNT_OF_EDGES_PER_WORKER) {
            log.debug("There are at least " + MINIMUM__AMOUNT_OF_EDGES_PER_WORKER + " edges per worker. Running in parallel mode");

            List<Callable<Void>> tasks = new ArrayList<>();
            for (int i = 0; i < portions; i++) {
                List<Pair<V, V>> portion = edges.subList(i * edgesPerWorker, Math.min(i * edgesPerWorker + edgesPerWorker, edges.size()));
                tasks.add(new RelaxTask<>(graph, portion, visitedVertices));
            }

            try {
                ActionThreadService.getInstance().invokeAll(tasks);
            } catch (InterruptedException e) {
                log.error("Bellman-Ford parallel search was interrupted.", e);
            }
        } else {
            log.debug("There are less than " + MINIMUM__AMOUNT_OF_EDGES_PER_WORKER + " edges per worker. Running in non-parallel mode");
            new RelaxTask<>(graph, edges, visitedVertices).call();
        }
    }

    private String edgeToString(Map<V, VertexDistanceInfo<V>> visitedVertices, Pair<V, V> edge) {
        V u = edge.getLeft();
        V v = edge.getRight();
        return String.format("(%s, w=%s) -> (%s, w=%s)", u, visitedVertices.get(u).getDistance(), v, visitedVertices.get(v).getDistance());
    }

    private static class RelaxTask<V> implements Callable<Void> {
        private final IGraph<V> graph;
        private final List<Pair<V, V>> edges;
        private final Map<V, VertexDistanceInfo<V>> visitedVertices;
        private final ReentrantLock locker = new ReentrantLock();

        public RelaxTask(IGraph<V> graph, List<Pair<V, V>> edges, Map<V, VertexDistanceInfo<V>> visitedVertices) {
            this.graph = graph;
            this.edges = edges;
            this.visitedVertices = visitedVertices;
        }

        @Override
        public Void call() {
            // For each edge (u, v) in edges do:
            for (Pair<V, V> edge : edges) {
                // If thread service instructed to shutdown now, we cannot continue executing.
                if (ActionThreadService.getInstance().isShutdownNow()) {
                    break;
                }

                // Weight of the edge between u to v
                int weight = graph.getValue(edge.getRight());

                locker.lock();
                try {
                    long uVertexWeight = visitedVertices.computeIfAbsent(edge.getLeft(), VertexDistanceInfo::new).getDistance();
                    long vVertexWeight = visitedVertices.computeIfAbsent(edge.getRight(), VertexDistanceInfo::new).getDistance();

                    // Avoid of overflow
                    if (uVertexWeight != Long.MAX_VALUE) {
                        long newWeight = uVertexWeight + weight;

                        if (newWeight < vVertexWeight) {

                            // Clear old parents
                            visitedVertices.get(edge.getRight()).getParents().clear();

                            // Update distance
                            visitedVertices.get(edge.getRight()).setDistance(newWeight);
                        }

                        if (newWeight == vVertexWeight) {
                            visitedVertices.get(edge.getRight()).getParents().add(edge.getLeft());
                        }
                    }
                } finally {
                    locker.unlock();
                }
            }

            return null;
        }
    }
}
