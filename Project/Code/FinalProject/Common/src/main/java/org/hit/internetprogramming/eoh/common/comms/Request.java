package org.hit.internetprogramming.eoh.common.comms;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hit.internetprogramming.eoh.common.action.ActionType;
import org.hit.internetprogramming.eoh.common.graph.IGraph;
import org.hit.internetprogramming.eoh.common.mat.Index;

/**
 * @author Haim Adrian
 * @since 13-Apr-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Request {
    /**
     * What action to execute
     */
    private ActionType actionType;

    /**
     * The content of a {@link ActionType#PUT_GRAPH PUT_GRAPH} request. Can be null
     */
    private IGraph<Index> graph;

    /**
     * When sending GET neighbors / reachables request, there is additional parameter telling the location to get the response for.<br/>
     * When sending a GENERATE request, we use this as the dimension of a matrix. e.g. 3x3
     */
    private Index vertex;

    /**
     * Used to mark requests that we detect as HTTP requests, so the response can be an HTTP response.
     */
    @JsonIgnore
    private boolean isHttpRequest;

    public Request(ActionType actionType) {
        this(actionType, null);
    }

    @JsonCreator
    public Request(@JsonProperty("actionType") ActionType actionType, @JsonProperty(value = "vertex") Index vertex) {
        this(actionType, vertex, false);
    }

    public Request(ActionType actionType, Index vertex, boolean isHttpRequest) {
        this.actionType = actionType;
        this.vertex = vertex == null ? new Index(5, 5) : vertex;
        this.isHttpRequest = isHttpRequest;
    }
}

