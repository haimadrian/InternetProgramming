package org.hit.internetprogramming.eoh.server.action.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import org.hit.internetprogramming.eoh.common.comms.Response;
import org.hit.internetprogramming.eoh.common.graph.IGraph;
import org.hit.internetprogramming.eoh.common.mat.Index;
import org.hit.internetprogramming.eoh.server.action.Action;
import org.hit.internetprogramming.eoh.server.action.ActionContext;
import org.hit.internetprogramming.eoh.server.impl.Graphs;

/**
 * Save a graph specified by client into our cache at {@link Graphs}.
 * @author Haim Adrian
 * @since 23-Apr-21
 */
public class PutGraph implements Action {
    @Override
    public Response execute(ActionContext actionContext) {
        Graphs.getInstance().putGraph(actionContext.getClientInfo(), actionContext.getRequest().getBodyAs(new TypeReference<IGraph<Index>>() {}));
        return Response.ok();
    }
}

