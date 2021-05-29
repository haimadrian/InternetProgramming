package org.hit.internetprogramming.eoh..server.action;

import lombok.extern.log4j.Log4j2;
import org.hit.internetprogramming.eoh..common.action.ActionType;
import org.hit.internetprogramming.eoh..common.comms.HttpStatus;
import org.hit.internetprogramming.eoh..common.comms.Request;
import org.hit.internetprogramming.eoh..common.comms.Response;
import org.hit.internetprogramming.eoh..server.action.impl.*;
import org.hit.internetprogramming.eoh..server.common.ClientInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Haim Adrian
 * @since 23-Apr-21
 */
@Log4j2
public class ActionExecutor {
    private static final ActionExecutor instance = new ActionExecutor();

    /**
     * Map each action type to implementing class, so we can initiate instances and execute actions
     */
    private final Map<ActionType, Class<? extends Action>> actions;

    private ActionExecutor() {
        actions = new HashMap<>();
        actions.put(ActionType.PUT_GRAPH, PutGraph.class);
        actions.put(ActionType.GET_GRAPH, GetGraph.class);
        actions.put(ActionType.GENERATE_RANDOM_GRAPH_STANDARD, GenerateRandomGraph.class);
        actions.put(ActionType.GENERATE_RANDOM_GRAPH_CROSS, GenerateRandomGraph.class);
        actions.put(ActionType.GET_NEIGHBORS, GetVertices.class);
        actions.put(ActionType.GET_REACHABLES, GetVertices.class);
        actions.put(ActionType.PRINT_GRAPH, PrintGraph.class);
    }

    public static ActionExecutor getInstance() {
        return instance;
    }

    /**
     * Execute an action based on specified client info and request details
     * @param clientInfo Client info is used by actions in order to fetch cached data of a client
     * @param request The request to know what client asked for
     * @return A response relevant to the specified action
     */
    public Response execute(ClientInfo clientInfo, Request request) {
        Response response = null;

        if (actions.containsKey(request.getActionType())) {
            log.info("Executing action for client: " + clientInfo + ". Action is: " + request.getActionType());

            try {
                Action action = actions.get(request.getActionType()).getDeclaredConstructor().newInstance();
                response = action.execute(new ActionContext(clientInfo, request));
            } catch (Exception e) {
                log.error("Error has occurred while executing action for client: " + clientInfo + ". Error: " + e, e);
                response = Response.error(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), "Failed executing action. Reason: " + e);
            }
        }

        return response;
    }
}

