package org.hit.internetprogramming.haim.matrix.server.action.impl;

import org.hit.internetprogramming.haim.matrix.common.comms.Response;
import org.hit.internetprogramming.haim.matrix.server.action.Action;
import org.hit.internetprogramming.haim.matrix.server.action.ActionContext;
import org.hit.internetprogramming.haim.matrix.server.impl.Graphs;

/**
 * @author Haim Adrian
 * @since 23-Apr-21
 */
public class PutGraph implements Action {
    @Override
    public Response execute(ActionContext actionContext) {
        Graphs.getInstance().putGraph(actionContext.getClientInfo(), actionContext.getRequest().getGraph());
        return Response.ok();
    }
}

