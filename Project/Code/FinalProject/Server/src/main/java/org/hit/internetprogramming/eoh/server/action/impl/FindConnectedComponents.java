package org.hit.internetprogramming.eoh.server.action.impl;

import lombok.extern.log4j.Log4j2;
import org.hit.internetprogramming.eoh.common.comms.HttpStatus;
import org.hit.internetprogramming.eoh.common.comms.Response;
import org.hit.internetprogramming.eoh.common.graph.IGraph;
import org.hit.internetprogramming.eoh.common.mat.Index;
import org.hit.internetprogramming.eoh.server.action.Action;
import org.hit.internetprogramming.eoh.server.action.ActionContext;
import org.hit.internetprogramming.eoh.server.graph.algorithm.ConnectedComponents;
import org.hit.internetprogramming.eoh.server.graph.algorithm.DFSVisit;
import org.hit.internetprogramming.eoh.server.impl.Graphs;

/**
 * A class for receiving all the connected components in a graph.<br/>
 * This class uses with DFS algorithm in order to Apply the requirements of connected components in a graph.<br/>
 * This class works In parallel in order to improve the runtime.
 * @author Orel Gershonovich
 * @see DFSVisit
 * @since 9-July-21
 */
@Log4j2
public class FindConnectedComponents implements Action {

    @Override
    public Response execute(ActionContext actionContext) {
        IGraph<Index> graph = Graphs.getInstance().getGraph(actionContext.getClientInfo());
        if (graph == null) {
            return Response.error(HttpStatus.NOT_FOUND.getCode(), "No graph was initialized. Please put graph or generate one", actionContext.getRequest().isHttp());
        }

        ConnectedComponents connectedComponents = new ConnectedComponents();

        return Response.ok(HttpStatus.OK.getCode(), connectedComponents.collect(graph), actionContext.getRequest().isHttp());
    }
}
