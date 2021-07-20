package org.hit.internetprogramming.eoh.server.graph.algorithm;


import lombok.*;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Some information about a vertex that we visit in one of the shortest paths algorithms (BFS
 * and Bellman-Ford)
 * @param <V> Type of a vertex in graph.
 * @author Haim Adrian
 * @since 18-Jul-21
 */
@ToString
public class VertexDistanceInfo<V> {
    /**
     * A vertex visited by a short path algorithm. (BFS or Bellman-Ford).
     */
    @Getter
    @NonNull
    private final V vertex;

    /**
     * The distance of this vertex from the root of the graph. This can be length or weight<br/>
     * Default value is {@value Integer#MAX_VALUE}, indicates that a vertex is not reachable from root.
     */
    @Getter
    @Setter(AccessLevel.PACKAGE)
    private long distance = Long.MAX_VALUE;

    /**
     * All parents of this vertex.<br/>
     * There might be multiple shortest paths, hence there might be multiple parents.
     */
    @Getter
    private final Collection<V> parents = ConcurrentHashMap.newKeySet();

    /**
     * Constructs a new {@link VertexDistanceInfo}
     * @param vertex The vertex to keep information for
     */
    public VertexDistanceInfo(@NonNull V vertex) {
        this.vertex = vertex;
    }
}
