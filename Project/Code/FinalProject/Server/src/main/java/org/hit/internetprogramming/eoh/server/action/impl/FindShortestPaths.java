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
import org.hit.internetprogramming.eoh.server.common.exception.InputTooLargeException;
import org.hit.internetprogramming.eoh.server.common.exception.NegativeWeightCycleException;
import org.hit.internetprogramming.eoh.server.graph.algorithm.FindPaths;
import org.hit.internetprogramming.eoh.server.impl.Graphs;

import java.util.Collection;
import java.util.List;

/**
 * A command that find all shortest paths between a source vertex to destination vertex.<br/>
 * This class uses {@link org.hit.internetprogramming.eoh.server.graph.algorithm.BFSVisit} algorithm.
 * @author Haim Adrian
 * @since 23-Apr-21
 */
public class FindShortestPaths implements Action {
    @Override
    public Response execute(ActionContext actionContext) {
        Response response = validateInput(actionContext);

        if (response == null) {
            IGraph<Index> graph = Graphs.getInstance().getGraph(actionContext.getClientInfo());
            TwoVerticesBody<Index> params = actionContext.getRequest().getBodyAs(new TypeReference<>() {
            });

            // Modify the root to the source vertex.
            graph = new MatrixGraphAdapter<>(graph, params.getFirst());
            FindPaths<Index> findPaths = new FindPaths<>(graph);

            try {
                List<Collection<Index>> shortestPaths = executeFindShortestPaths(findPaths, params.getSecond());
                response = Response.ok(HttpStatus.OK.getCode(), shortestPaths, actionContext.getRequest().isHttp());
            } catch (InputTooLargeException | NegativeWeightCycleException e) {
                // We might fail with InputTooLargeException or NegativeWeightCycleException.
                // In this case, return the error message to the caller.
                response = Response.error(HttpStatus.BAD_REQUEST.getCode(), e.getMessage(), actionContext.getRequest().isHttp());
            }
        }

        return response;
    }

    /**
     * This method should execute the correct function in {@link FindPaths} class.<br/>
     * At this level, we execute {@link FindPaths#findShortestPaths(Object)}, but at the derived class we should
     * use Bellman Ford, so it will call the corresponding method.
     * @param pathsFinder The paths finder to use for the search operation
     * @param destination The destination vertex
     * @return List of shortest paths
     */
    protected List<Collection<Index>> executeFindShortestPaths(FindPaths<Index> pathsFinder, Index destination) {
        return pathsFinder.findShortestPaths(destination);
    }

    protected Response validateInput(ActionContext actionContext) {
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

        return null;
    }
}

