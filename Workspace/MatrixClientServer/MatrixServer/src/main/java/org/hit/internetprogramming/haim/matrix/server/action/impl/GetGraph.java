package org.hit.internetprogramming.haim.matrix.server.action.impl;

import lombok.extern.log4j.Log4j2;
import org.hit.internetprogramming.haim.matrix.common.comms.HttpStatus;
import org.hit.internetprogramming.haim.matrix.common.comms.Response;
import org.hit.internetprogramming.haim.matrix.common.graph.IGraph;
import org.hit.internetprogramming.haim.matrix.common.graph.MatrixGraphAdapter;
import org.hit.internetprogramming.haim.matrix.common.mat.Index;
import org.hit.internetprogramming.haim.matrix.server.action.Action;
import org.hit.internetprogramming.haim.matrix.server.action.ActionContext;
import org.hit.internetprogramming.haim.matrix.server.impl.Graphs;

/**
 * @author Haim Adrian
 * @since 23-Apr-21
 */
@Log4j2
public class GetGraph implements Action {
    @Override
    public Response execute(ActionContext actionContext) {
        IGraph<Index> graph = Graphs.getInstance().getGraph(actionContext.getClientInfo());
        if (graph == null) {
            return Response.error(HttpStatus.NOT_FOUND.getCode(), "No graph was initialized. Please put graph or generate one", actionContext.getRequest().isHttpRequest());
        }

        return Response.ok((MatrixGraphAdapter<?>) graph);
    }
}

