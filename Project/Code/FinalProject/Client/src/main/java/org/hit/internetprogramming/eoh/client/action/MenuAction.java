package org.hit.internetprogramming.eoh.client.action;

import org.hit.internetprogramming.eoh.common.action.ActionType;

/**
 * @author Haim Adrian
 * @since 23-Apr-21
 */
public enum MenuAction {
    DISCONNECT("Disconnect", ActionType.DISCONNECT),
    CREATE_GRAPH("Create Graph (Enter matrix and send it to server)", ActionType.PUT_GRAPH),
    LOAD_GRAPH("Load Graph From File (Load graph and send it to server)", null),
    SAVE_GRAPH("Save Graph To File (Get graph from server and save it)", null),
    GENERATE_RANDOM_GRAPH_STANDARD("Generate Random Graph (Using standard matrix)", ActionType.GENERATE_RANDOM_GRAPH_STANDARD),
    GENERATE_RANDOM_GRAPH_CROSS("Generate Random Graph (Using cross matrix)", ActionType.GENERATE_RANDOM_GRAPH_CROSS),
    GET_NEIGHBORS("Get Neighbor Vertices", ActionType.GET_NEIGHBORS),
    GET_REACHABLES("Get Reachable Vertices", ActionType.GET_REACHABLES),
    PRINT_GRAPH("Print Graph", ActionType.PRINT_GRAPH);

    private final String text;
    private final ActionType actionType;

    MenuAction(String text, ActionType actionType) {
        this.text = text;
        this.actionType = actionType;
    }

    public String getText() {
        return text;
    }

    public ActionType getActionType() {
        return actionType;
    }
}

