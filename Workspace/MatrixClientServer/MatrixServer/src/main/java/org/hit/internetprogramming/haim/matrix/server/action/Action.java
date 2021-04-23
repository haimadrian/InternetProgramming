package org.hit.internetprogramming.haim.matrix.server.action;

import org.hit.internetprogramming.haim.matrix.common.comms.Response;

/**
 * Main interface for actions in MatrixServer
 * @author Haim Adrian
 * @since 23-Apr-21
 */
public interface Action {
    /**
     * This method will be invoked by {@link ActionExecutor} when there is a new client request that asks the server to perform action
     * @param actionContext {@link ActionContext} to get request details from
     * @return A response to send back to client. Can be null when no response is needed
     */
    Response execute(ActionContext actionContext);
}

