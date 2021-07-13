package org.hit.internetprogramming.eoh.server.action.impl;

import org.hit.internetprogramming.eoh.common.action.ActionType;
import org.hit.internetprogramming.eoh.common.comms.HttpStatus;
import org.hit.internetprogramming.eoh.common.comms.Request;
import org.hit.internetprogramming.eoh.common.comms.Response;
import org.hit.internetprogramming.eoh.common.graph.MatrixGraphAdapter;
import org.hit.internetprogramming.eoh.common.mat.*;
import org.hit.internetprogramming.eoh.server.action.Action;
import org.hit.internetprogramming.eoh.server.action.ActionContext;
import org.hit.internetprogramming.eoh.server.action.ActionExecutor;
import org.hit.internetprogramming.eoh.server.impl.Graphs;

/**
 * A command that generates a new random graph for the requesting client.<br/>
 * The graph is cached at {@link Graphs} so the requesting client can later get/print it.
 * @author Haim Adrian
 * @since 23-Apr-21
 */
public class GenerateRandomGraph implements Action {
    @Override
    public Response execute(ActionContext actionContext) {
        Index dimension = actionContext.getRequest().getBodyAs(Index.class);
        if (dimension == null) {
            dimension = Index.from(5, 5);
        }

        IMatrix<Integer> matrix;

        ActionType generateAction = actionContext.getRequest().getActionType();
        switch (generateAction) {
            case GENERATE_RANDOM_GRAPH_REGULAR:
                matrix = new RegularMatrix(dimension.getRow(), dimension.getColumn(), true);
                break;
            case GENERATE_RANDOM_GRAPH_STANDARD:
                matrix = new StandardMatrix(dimension.getRow(), dimension.getColumn(), true);
                break;
            case GENERATE_RANDOM_GRAPH_CROSS:
                matrix = new CrossMatrix(dimension.getRow(), dimension.getColumn(), true);
                break;
            default:
                return Response.error(HttpStatus.BAD_REQUEST.getCode(), "Unknown generate action type: " + generateAction, actionContext.getRequest().isHttp());
        }

        Index root = Index.from(0, 0);
        matrix.setValue(root, 1);
        MatrixGraphAdapter<Integer> graph = new MatrixGraphAdapter<>(matrix, root);
        Graphs.getInstance().putGraph(actionContext.getClientInfo(), graph);

        // Now execute print graph so we will return the generated graph back to client
        return ActionExecutor.getInstance().execute(actionContext.getClientInfo(), new Request(ActionType.PRINT_GRAPH, null, actionContext.getRequest().isHttp()));
    }
}

