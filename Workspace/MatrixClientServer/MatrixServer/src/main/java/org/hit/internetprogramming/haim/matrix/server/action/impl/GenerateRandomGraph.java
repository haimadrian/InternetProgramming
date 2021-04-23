package org.hit.internetprogramming.haim.matrix.server.action.impl;

import org.hit.internetprogramming.haim.matrix.common.action.ActionType;
import org.hit.internetprogramming.haim.matrix.common.comms.Request;
import org.hit.internetprogramming.haim.matrix.common.comms.Response;
import org.hit.internetprogramming.haim.matrix.common.graph.MatrixGraphAdapter;
import org.hit.internetprogramming.haim.matrix.common.mat.Index;
import org.hit.internetprogramming.haim.matrix.common.mat.StandardMatrix;
import org.hit.internetprogramming.haim.matrix.server.action.Action;
import org.hit.internetprogramming.haim.matrix.server.action.ActionContext;
import org.hit.internetprogramming.haim.matrix.server.action.ActionExecutor;
import org.hit.internetprogramming.haim.matrix.server.impl.Graphs;

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

        StandardMatrix matrix = new StandardMatrix(dimension.getRow(), dimension.getColumn(), true);
        Index root = new Index(0, 0);
        matrix.setValue(root, 1);
        MatrixGraphAdapter<Integer> graph = new MatrixGraphAdapter<>(matrix, root);
        Graphs.getInstance().putGraph(actionContext.getClientInfo(), graph);

        // Now execute print graph so we will return the generated graph back to client
        return ActionExecutor.getInstance().execute(actionContext.getClientInfo(), new Request(ActionType.PRINT_GRAPH, null, actionContext.getRequest().isHttpRequest()));
    }
}

