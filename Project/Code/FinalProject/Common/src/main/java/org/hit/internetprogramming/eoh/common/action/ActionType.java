package org.hit.internetprogramming.eoh.common.action;

import org.hit.internetprogramming.eoh.common.mat.impl.CrossMatrix;
import org.hit.internetprogramming.eoh.common.mat.impl.Matrix;
import org.hit.internetprogramming.eoh.common.mat.impl.StandardMatrix;

/**
 * Actions supported by server are mentioned here so both client and server can use the same enumeration
 * @author Haim Adrian
 * @since 23-Apr-21
 */
public enum ActionType {
    /**
     * Send a graph to server, so it will be available for usage by our algorithms
     */
    PUT_GRAPH,

    /**
     * Generates a random graph, adapted by a {@link Matrix} that goes in ALL directions, having null or 1 values only.
     */
    GENERATE_RANDOM_BINARY_GRAPH_REGULAR,

    /**
     * Generates a random graph, adapted by a {@link StandardMatrix} that goes in UP,DOWN,LEFT,RIGHT directions, having null or 1 values only.
     */
    GENERATE_RANDOM_BINARY_GRAPH_STANDARD,

    /**
     * Generates a random graph, adapted by a {@link CrossMatrix} that goes in cross directions, having null or 1 values only.
     */
    GENERATE_RANDOM_BINARY_GRAPH_CROSS,

    /**
     * Generates a random graph, adapted by a {@link Matrix} that goes in ALL directions
     */
    GENERATE_RANDOM_GRAPH_REGULAR,

    /**
     * Generates a random graph, adapted by a {@link StandardMatrix} that goes in UP,DOWN,LEFT,RIGHT directions
     */
    GENERATE_RANDOM_GRAPH_STANDARD,

    /**
     * Generates a random graph, adapted by a {@link CrossMatrix} that goes in cross directions
     */
    GENERATE_RANDOM_GRAPH_CROSS,

    /**
     * Get all vertices reachable from some specified vertex
     */
    GET_REACHABLES,

    /**
     * Get all neighbors (reachable or not) of some specified vertex
     */
    GET_NEIGHBORS,

    /**
     * Get all connected components in a graph
     */
    CONNECTED_COMPONENTS,

    /**
     * Find all shortest paths between a source vertex and destination vertex
     */
    SHORTEST_PATHS,

    /**
     * Find all shortest paths, in a weighted graph, between a source vertex and destination vertex
     */
    SHORTEST_PATHS_IN_WEIGHTED_GRAPH,
    /**
     * Calculates how many submarines there are in a graph.
     * */
    SUBMARINES,
    /**
     * Print graph to console
     */
    PRINT_GRAPH,

    /**
     * Get the graph from server, for manipulation
     */
    GET_GRAPH,

    /**
     * Disconnects from server
     */
    DISCONNECT,

    /**
     * A special request, handled by HTTP part of the server, to build index.html with links to other actions
     */
    INDEX_HTML
}

