package org.hit.internetprogramming.eoh.server.action.impl;

import org.hit.internetprogramming.eoh.common.action.ActionType;
import org.hit.internetprogramming.eoh.common.comms.HttpStatus;
import org.hit.internetprogramming.eoh.common.comms.Request;
import org.hit.internetprogramming.eoh.common.comms.Response;
import org.hit.internetprogramming.eoh.common.graph.MatrixGraphAdapter;
import org.hit.internetprogramming.eoh.common.mat.IMatrix;
import org.hit.internetprogramming.eoh.common.mat.Index;
import org.hit.internetprogramming.eoh.common.mat.impl.CrossMatrix;
import org.hit.internetprogramming.eoh.common.mat.impl.Matrix;
import org.hit.internetprogramming.eoh.common.mat.impl.StandardMatrix;
import org.hit.internetprogramming.eoh.server.action.Action;
import org.hit.internetprogramming.eoh.server.action.ActionContext;
import org.hit.internetprogramming.eoh.server.action.ActionExecutor;
import org.hit.internetprogramming.eoh.server.impl.Graphs;

import java.security.SecureRandom;

/**
 * A command that generates a new random graph for the requesting client.<br/>
 * The graph is cached at {@link Graphs} so the requesting client can later get/print it.
 * @author Haim Adrian
 * @since 23-Apr-21
 */
public class GenerateRandomGraph implements Action {
    /**
     * When we generate a random value we must define a bound. For binary matrix, the bound is 2 so we
     * will get 0 or 1 only.
     */
    private static final int BINARY_MATRIX_VALUE_BOUND = 2;

    /**
     * Use this bound for regular matrix, to make it comfortable working with small numbers and not thousands.
     */
    private static final int MATRIX_VALUE_BOUND = 1000;

    @Override
    public Response execute(ActionContext actionContext) {
        Index dimension = actionContext.getRequest().getBodyAs(Index.class);
        if (dimension == null) {
            dimension = Index.from(5, 5);
        }

        IMatrix<Integer> matrix;

        ActionType generateAction = actionContext.getRequest().getActionType();
        switch (generateAction) {
            case GENERATE_RANDOM_BINARY_GRAPH_REGULAR:
                matrix = new Matrix<>(generateRandomMatrix(dimension.getRow(), dimension.getColumn(), BINARY_MATRIX_VALUE_BOUND, true, false));
                break;
            case GENERATE_RANDOM_BINARY_GRAPH_STANDARD:
                matrix = new StandardMatrix<>(generateRandomMatrix(dimension.getRow(), dimension.getColumn(), BINARY_MATRIX_VALUE_BOUND, true, false));
                break;
            case GENERATE_RANDOM_BINARY_GRAPH_CROSS:
                matrix = new CrossMatrix<>(generateRandomMatrix(dimension.getRow(), dimension.getColumn(), BINARY_MATRIX_VALUE_BOUND, true, false));
                break;
            case GENERATE_RANDOM_GRAPH_REGULAR:
                matrix = new Matrix<>(generateRandomMatrix(dimension.getRow(), dimension.getColumn(), MATRIX_VALUE_BOUND, false, true));
                break;
            case GENERATE_RANDOM_GRAPH_STANDARD:
                matrix = new StandardMatrix<>(generateRandomMatrix(dimension.getRow(), dimension.getColumn(), MATRIX_VALUE_BOUND, false, true));
                break;
            case GENERATE_RANDOM_GRAPH_CROSS:
                matrix = new CrossMatrix<>(generateRandomMatrix(dimension.getRow(), dimension.getColumn(), MATRIX_VALUE_BOUND, false, true));
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

    /**
     * Generate a random matrix.<br/>
     * This method can generate a binary matrix or weighted matrix, depends on the arguments that we receive.<br/>
     * A binary matrix will not have 0 values in it, instead, we would like to replace them with null, to indicate
     * that there is no neighbor there.
     * @param rows How many rows to use
     * @param cols How many columns to use
     * @param bound The upper bound for random values, exclusive. (2 for binary)
     * @param replaceZeroWithNull Whether to replace 0 with null, or accept 0 as a valid neighbor
     * @param shouldAllowNegative Use positive numbers only, or accept negative as well
     * @return The random matrix
     */
    private Integer[][] generateRandomMatrix(int rows, int cols, int bound, boolean replaceZeroWithNull, boolean shouldAllowNegative) {
        Integer[][] matrix = new Integer[rows][cols];

        SecureRandom rand = new SecureRandom();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Integer randomValue = rand.nextInt(bound);

                if (randomValue != 0) {
                    // Randomly use negative values if necessary
                    if (shouldAllowNegative && rand.nextBoolean()) {
                        randomValue = -1 * randomValue;
                    }
                }
                // When value is 0 and we need to replace it with null:
                else if (replaceZeroWithNull) {
                    randomValue = null;
                }

                matrix[row][col] = randomValue;
            }
        }

        return matrix;
    }
}

