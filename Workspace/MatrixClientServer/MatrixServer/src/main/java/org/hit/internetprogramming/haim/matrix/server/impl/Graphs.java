package org.hit.internetprogramming.haim.matrix.server.impl;

import org.hit.internetprogramming.haim.matrix.common.graph.IGraph;
import org.hit.internetprogramming.haim.matrix.server.common.ClientInfo;

import java.util.HashMap;
import java.util.Map;

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
        clientToGraph = new HashMap<>();
    }

    public static Graphs getInstance() {
        return GraphsHolder.instance;
    }

    @SuppressWarnings("unchecked")
    public <T> IGraph<T> getGraph(ClientInfo clientInfo) {
        return (IGraph<T>) clientToGraph.get(clientInfo);
    }

    public <T> void putGraph(ClientInfo clientInfo, IGraph<T> graph) {
        clientToGraph.put(clientInfo, graph);
    }

    private static final class GraphsHolder {
        private static final Graphs instance = new Graphs();
    }
}

