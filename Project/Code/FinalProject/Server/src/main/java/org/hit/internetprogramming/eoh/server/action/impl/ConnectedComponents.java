package org.hit.internetprogramming.eoh.server.action.impl;

import org.hit.internetprogramming.eoh.common.comms.HttpStatus;
import org.hit.internetprogramming.eoh.common.comms.Response;
import org.hit.internetprogramming.eoh.common.graph.IGraph;
import org.hit.internetprogramming.eoh.common.mat.Index;
import org.hit.internetprogramming.eoh.server.action.Action;
import org.hit.internetprogramming.eoh.server.action.ActionContext;
import org.hit.internetprogramming.eoh.server.graph.algorithm.DFSVisit;
import org.hit.internetprogramming.eoh.server.impl.Graphs;

import java.util.List;

/**
 * A class for receiving all the connected components in a graph.<br/>
 * The graph is represented by a matrix.
 * @author Orel Gershonovich
 * @since   9-July-21
 * @see DFSVisit
 */
public class ConnectedComponents implements Action {

    @Override
    public Response execute(ActionContext actionContext) {
        IGraph<Index> graph = Graphs.getInstance().getGraph(actionContext.getClientInfo());
        if (graph == null) {
            return Response.error(HttpStatus.NOT_FOUND.getCode(), "No graph was initialized. Please put graph or generate one", actionContext.getRequest().isHttpRequest());
        }

        DFSVisit<Index> dfsVisit = new DFSVisit<>();
        List<Index> connectedComponents = dfsVisit.traverse(graph);

        return Response.ok(HttpStatus.OK.getCode(), connectedComponents, actionContext.getRequest().isHttpRequest());
    }
}
