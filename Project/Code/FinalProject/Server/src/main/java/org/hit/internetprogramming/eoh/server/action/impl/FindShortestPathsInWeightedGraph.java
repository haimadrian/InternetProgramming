package org.hit.internetprogramming.eoh.server.action.impl;

import org.hit.internetprogramming.eoh.common.mat.Index;
import org.hit.internetprogramming.eoh.server.graph.algorithm.FindPaths;

import java.util.Collection;
import java.util.List;

/**
 * A command that find all shortest paths in weighted graph between a source vertex to destination vertex.<br/>
 * This class uses {@link org.hit.internetprogramming.eoh.server.graph.algorithm.BellmanFord} algorithm.
 * @author Haim Adrian
 * @since 18-Jul-21
 */
public class FindShortestPathsInWeightedGraph extends FindShortestPaths {
    @Override
    protected List<Collection<Index>> executeFindShortestPaths(FindPaths<Index> pathsFinder, Index destination) {
        return pathsFinder.findShortestPathsInWeightedGraphDijkstra(destination);
    }
}

