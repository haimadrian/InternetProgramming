package org.hit.internetprogramming.haim.matrix.server.action.impl;

import org.hit.internetprogramming.haim.matrix.common.comms.HttpStatus;
import org.hit.internetprogramming.haim.matrix.common.comms.Response;
import org.hit.internetprogramming.haim.matrix.common.graph.IGraph;
import org.hit.internetprogramming.haim.matrix.common.mat.Index;
import org.hit.internetprogramming.haim.matrix.server.action.Action;
import org.hit.internetprogramming.haim.matrix.server.action.ActionContext;
import org.hit.internetprogramming.haim.matrix.server.impl.Graphs;

/**
 * @author Haim Adrian
 * @since 23-Apr-21
 */
public class GetReachables implements Action {
    @Override
    public Response execute(ActionContext actionContext) {
        IGraph<Index> graph = Graphs.getInstance().getGraph(actionContext.getClientInfo());
        if (graph == null) {
            return Response.error(HttpStatus.NOT_FOUND.getCode(), "No graph was initialized. Please put graph or generate one", actionContext.getRequest().isHttpRequest());
        }

        return Response.ok(HttpStatus.OK.getCode(), graph.getReachableVertices(actionContext.getRequest().getVertex()), actionContext.getRequest().isHttpRequest());
    }
}

