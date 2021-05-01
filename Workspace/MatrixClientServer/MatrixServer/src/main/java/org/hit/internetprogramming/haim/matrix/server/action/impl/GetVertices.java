package org.hit.internetprogramming.haim.matrix.server.action.impl;

import org.hit.internetprogramming.haim.matrix.common.action.ActionType;
import org.hit.internetprogramming.haim.matrix.common.comms.HttpStatus;
import org.hit.internetprogramming.haim.matrix.common.comms.Response;
import org.hit.internetprogramming.haim.matrix.common.graph.IGraph;
import org.hit.internetprogramming.haim.matrix.common.mat.Index;
import org.hit.internetprogramming.haim.matrix.server.action.Action;
import org.hit.internetprogramming.haim.matrix.server.action.ActionContext;
import org.hit.internetprogramming.haim.matrix.server.impl.Graphs;

import java.util.List;

/**
 * @author Haim Adrian
 * @since 23-Apr-21
 */
public class GetVertices implements Action {
    @Override
    public Response execute(ActionContext actionContext) {
        IGraph<Index> graph = Graphs.getInstance().getGraph(actionContext.getClientInfo());
        if (graph == null) {
            return Response.error(HttpStatus.NOT_FOUND.getCode(), "No graph was initialized. Please put graph or generate one", actionContext.getRequest().isHttpRequest());
        }

        List<Index> vertices;
        if (actionContext.getRequest().getActionType() == ActionType.GET_REACHABLES) {
            vertices = graph.getReachableVertices(actionContext.getRequest().getVertex());
        } else {
            vertices = graph.getAdjacentVertices(actionContext.getRequest().getVertex());
        }

        return Response.ok(HttpStatus.OK.getCode(), vertices, actionContext.getRequest().isHttpRequest());
    }
}

