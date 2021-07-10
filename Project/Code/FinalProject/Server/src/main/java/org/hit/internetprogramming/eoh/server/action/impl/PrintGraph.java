package org.hit.internetprogramming.eoh.server.action.impl;

import lombok.extern.log4j.Log4j2;
import org.hit.internetprogramming.eoh.common.comms.HttpStatus;
import org.hit.internetprogramming.eoh.common.comms.Response;
import org.hit.internetprogramming.eoh.common.graph.IGraph;
import org.hit.internetprogramming.eoh.server.action.Action;
import org.hit.internetprogramming.eoh.server.action.ActionContext;
import org.hit.internetprogramming.eoh.server.impl.Graphs;

/**
 * Print the graph cached for the requesting client to log, and return that string to client.
 * @author Haim Adrian
 * @since 23-Apr-21
 */
@Log4j2
public class PrintGraph implements Action {
    @Override
    public Response execute(ActionContext actionContext) {
        IGraph<Object> graph = Graphs.getInstance().getGraph(actionContext.getClientInfo());
        if (graph == null) {
            return Response.error(HttpStatus.NOT_FOUND.getCode(), "No graph was initialized. Please put graph or generate one", actionContext.getRequest().isHttp());
        }

        String graphAsString = graph.printGraph();
        log.info("Graph: " + System.lineSeparator() + graphAsString);
        return Response.ok(graphAsString, actionContext.getRequest().isHttp());
    }
}

