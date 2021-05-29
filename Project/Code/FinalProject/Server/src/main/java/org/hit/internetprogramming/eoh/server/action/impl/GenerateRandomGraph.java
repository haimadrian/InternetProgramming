package org.hit.internetprogramming.eoh.server.action.impl;

import org.hit.internetprogramming.eoh.common.action.ActionType;
import org.hit.internetprogramming.eoh.common.comms.Request;
import org.hit.internetprogramming.eoh.common.comms.Response;
import org.hit.internetprogramming.eoh.common.graph.MatrixGraphAdapter;
import org.hit.internetprogramming.eoh.common.mat.CrossMatrix;
import org.hit.internetprogramming.eoh.common.mat.IMatrix;
import org.hit.internetprogramming.eoh.common.mat.Index;
import org.hit.internetprogramming.eoh.common.mat.StandardMatrix;
import org.hit.internetprogramming.eoh.server.action.Action;
import org.hit.internetprogramming.eoh.server.action.ActionContext;
import org.hit.internetprogramming.eoh.server.action.ActionExecutor;
import org.hit.internetprogramming.eoh.server.impl.Graphs;

/**
 * @author Haim Adrian
 * @since 23-Apr-21
 */
public class GenerateRandomGraph implements Action {
    @Override
    public Response execute(ActionContext actionContext) {
        Index dimension = actionContext.getRequest().getVertex();
        if (dimension == null) {
            dimension = new Index(5, 5);
        }

        IMatrix<Integer> matrix;

        if (actionContext.getRequest().getActionType() == ActionType.GENERATE_RANDOM_GRAPH_CROSS) {
            matrix = new CrossMatrix(dimension.getRow(), dimension.getColumn(), true);
        } else {
            matrix = new StandardMatrix(dimension.getRow(), dimension.getColumn(), true);
        }

        Index root = new Index(0, 0);
        matrix.setValue(root, 1);
        MatrixGraphAdapter<Integer> graph = new MatrixGraphAdapter<>(matrix, root);
        Graphs.getInstance().putGraph(actionContext.getClientInfo(), graph);

        // Now execute print graph so we will return the generated graph back to client
        return ActionExecutor.getInstance().execute(actionContext.getClientInfo(), new Request(ActionType.PRINT_GRAPH, null, actionContext.getRequest().isHttpRequest()));
    }
}

