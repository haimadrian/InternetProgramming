package org.hit.internetprogramming.eoh.server.action.impl;

import lombok.extern.log4j.Log4j2;
import org.hit.internetprogramming.eoh.common.comms.HttpStatus;
import org.hit.internetprogramming.eoh.common.comms.Response;
import org.hit.internetprogramming.eoh.common.graph.IGraph;
import org.hit.internetprogramming.eoh.common.graph.MatrixGraphAdapter;
import org.hit.internetprogramming.eoh.common.mat.Index;
import org.hit.internetprogramming.eoh.server.action.Action;
import org.hit.internetprogramming.eoh.server.action.ActionContext;
import org.hit.internetprogramming.eoh.server.graph.algorithm.DFSVisit;
import org.hit.internetprogramming.eoh.server.impl.Graphs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A class for receiving all the submarines in a graph.<br/>
 * The graph is represented by a matrix.
 *
 * Submarine rules:
 * 1. at least 2 horizontal 1-node
 * 2. at least 2 vertical 1-node
 * 3. There can be no two nodes diagonally unless
 *    there are sections 1 and 2 for both.
 * 4. The minimum distance between two submarines
 *    (regardless of orientation) is one square (0-node).
 *
 * @see FindConnectedComponents
 * @author Eden Zadikove
 * @since 21-July-21
 */
@Log4j2
public class Submarines implements Action {
    @Override
    public Response execute(ActionContext actionContext) {
        IGraph<Index> graph = Graphs.getInstance().getGraph(actionContext.getClientInfo());
        log.info("submarine");
        if (graph == null) {
            return Response.error(HttpStatus.NOT_FOUND.getCode(), "No graph was initialized. Please put graph or generate one", actionContext.getRequest().isHttp());
        }
        List<Set<Index>> connectedComponentsList = new ArrayList<>();
        Set<Index> connectedComponent = new HashSet<>();
        connectedComponent.add(new Index(2,1));
        connectedComponent.add(new Index(2,2));
        connectedComponent.add(new Index(1,0));
        connectedComponent.add(new Index(1,2));
        connectedComponent.add(new Index(0,0));
        connectedComponent.add(new Index(0,2));
        connectedComponent.add(new Index(2,0));

        connectedComponentsList.add(connectedComponent);
        System.out.println(connectedComponentsList);

        return Response.ok();
    }
}
