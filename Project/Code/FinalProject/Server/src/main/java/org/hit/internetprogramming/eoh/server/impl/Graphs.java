package org.hit.internetprogramming.eoh.server.impl;

import org.hit.internetprogramming.eoh.common.graph.IGraph;
import org.hit.internetprogramming.eoh.server.common.ClientInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A singleton used to cache graph for each client, so we can handle separate client requests independently,
 * and let a client to work on its graph without having to attach it to every request.
 * @author Haim Adrian
 * @since 23-Apr-21
 */
public class Graphs {
    /**
     * Cache the graph for each client, so subsequent requests will be executed on an already initialized graph.
     */
    private final Map<ClientInfo, IGraph<?>> clientToGraph;

    private Graphs() {
        clientToGraph = new ConcurrentHashMap<>();
    }

    /**
     * @return The unique instance of this class
     */
    public static Graphs getInstance() {
        return GraphsHolder.instance;
    }

    /**
     * Get a graph from cache, or {@code null} in case there is no graph cached for the specified client.<br/>
     * A client must first {@link org.hit.internetprogramming.eoh.server.action.impl.PutGraph} before it can
     * execute commands on graphs.
     * @param clientInfo The client info to get its cached graph
     * @param <T> Type of elements in {@link IGraph}
     * @return The graph, in case there is one, or {@code null} otherwise.
     */
    @SuppressWarnings("unchecked")
    public <T> IGraph<T> getGraph(ClientInfo clientInfo) {
        return (IGraph<T>) clientToGraph.get(clientInfo);
    }

    /**
     * Put a graph into the cache, mapped to the specified client info.
     * @param clientInfo The client info to map the specified graph to
     * @param graph The graph to put into the cache
     * @param <T> Type of elements in {@link IGraph}
     */
    public <T> void putGraph(ClientInfo clientInfo, IGraph<T> graph) {
        clientToGraph.put(clientInfo, graph);
    }

    // A lazy, thread-safe initializer for the unique instance of our singleton.
    private static final class GraphsHolder {
        private static final Graphs instance = new Graphs();
    }
}

