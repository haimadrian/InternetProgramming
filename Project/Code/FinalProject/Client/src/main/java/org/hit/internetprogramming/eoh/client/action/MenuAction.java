package org.hit.internetprogramming.eoh.client.action;

import org.hit.internetprogramming.eoh.common.action.ActionType;

/**
 * An enum that wraps {@link ActionType} enum to decorate it for client (menu) usage.<br/>
 * At {@link org.hit.internetprogramming.eoh.client.ClientMain} we iterate all values in this enum
 * so we will be able to print the menu automatically, just by modifying this enum, without
 * having to update the main class.
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

    /**
     * @see #getText()
     */
    private final String text;

    /**
     * @see #getActionType()
     */
    private final ActionType actionType;

    MenuAction(String text, ActionType actionType) {
        this.text = text;
        this.actionType = actionType;
    }

    /**
     * @return Text representation of this enum, to be used by menu
     */
    public String getText() {
        return text;
    }

    /**
     * @return The {@link ActionType} that this enum decorates
     */
    public ActionType getActionType() {
        return actionType;
    }
}

