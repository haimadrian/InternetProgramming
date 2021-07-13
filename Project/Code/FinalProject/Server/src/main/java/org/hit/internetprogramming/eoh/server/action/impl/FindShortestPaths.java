package org.hit.internetprogramming.eoh.server.action.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import org.hit.internetprogramming.eoh.common.comms.HttpStatus;
import org.hit.internetprogramming.eoh.common.comms.Response;
import org.hit.internetprogramming.eoh.common.comms.TwoVerticesBody;
import org.hit.internetprogramming.eoh.common.graph.IGraph;
import org.hit.internetprogramming.eoh.common.graph.MatrixGraphAdapter;
import org.hit.internetprogramming.eoh.common.mat.Index;
import org.hit.internetprogramming.eoh.server.action.Action;
import org.hit.internetprogramming.eoh.server.action.ActionContext;
import org.hit.internetprogramming.eoh.server.graph.algorithm.FindPaths;
import org.hit.internetprogramming.eoh.server.impl.Graphs;

/**
 * A command that returns requested vertices to client, based on a request.<br/>
 * When client asks for neighbors, we return the neighbors of the specified vertex, from the graph cached
 * for the requesting client at {@link Graphs}.<br/>
 * When client asks for reachable neighbors, then we will collect and return them.
 * @author Haim Adrian
 * @since 23-Apr-21
 */
public class FindShortestPaths implements Action {
    @Override
    public Response execute(ActionContext actionContext) {
        IGraph<Index> graph = Graphs.getInstance().getGraph(actionContext.getClientInfo());
        if (graph == null) {
            return Response.error(HttpStatus.NOT_FOUND.getCode(), "No graph was initialized. Please put graph or generate one", actionContext.getRequest().isHttp());
        }

        TwoVerticesBody<Index> params = actionContext.getRequest().getBodyAs(new TypeReference<>() {});
        if (params == null) {
            return Response.error(HttpStatus.BAD_REQUEST.getCode(), "Input vertices is missing", actionContext.getRequest().isHttp());
        }

        if (params.getFirst() == null) {
            return Response.error(HttpStatus.BAD_REQUEST.getCode(), "Source vertex is missing", actionContext.getRequest().isHttp());
        }

        if (params.getSecond() == null) {
            return Response.error(HttpStatus.BAD_REQUEST.getCode(), "Destination vertex is missing", actionContext.getRequest().isHttp());
        }

        // Modify the root to the source vertex.
        graph = new MatrixGraphAdapter<>(graph, params.getFirst());
        FindPaths<Index> shortestPaths = new FindPaths<>(graph);

        return Response.ok(HttpStatus.OK.getCode(), shortestPaths.findShortestPaths(params.getSecond()), actionContext.getRequest().isHttp());
    }
}

