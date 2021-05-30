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
 * A request model is used by both server and client.<br/>
 * Client sends requests to the server, as json strings. The server unmarshall the json string into Request model.
 * Server might also create a Request model when mapping HTTP requests received from browser / Postman.<br/>
 * The request model contains an {@link ActionType} that the server should execute, and some body, based on
 * the request. A body can be some {@link Index vertex} (in case we would like to receive neighbors of a vertex for example)
 * and it can also be a {@link IGraph graph}, for example when we create a new graph and use the PUT_GRAPH action.
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
     * Used to mark requests that we detect as HTTP requests, so the response can be an HTTP response.<br/>
     * We support receiving HTTP requests from browser, so we can have another client to play with<br/>
     * This field is ignored from json as it is relevant for server only.
     * @see Request#isHttpRequest()
     */
    @JsonIgnore
    private boolean isHttpRequest;

    /**
     * Constructs a new {@link Request} with no body
     * @param actionType The {@link ActionType action type}
     */
    public Request(ActionType actionType) {
        this(actionType, null);
    }

    /**
     * Constructs a new {@link Request} with vertex as its body.<br/>
     * Used for actions like {@link ActionType#GET_NEIGHBORS}, to define which vertex we want to get the neighbors for
     * @param actionType The {@link ActionType action type}
     * @param vertex The vertex to use as body. Can be {@code null}
     */
    @JsonCreator
    public Request(@JsonProperty("actionType") ActionType actionType, @JsonProperty(value = "vertex") Index vertex) {
        this(actionType, vertex, false);
    }

    /**
     * Constructs a new {@link Request} with vertex as its body, and a flag indicating whether it is an HTTP request.<br/>
     * Used by server when it handles HTTP requests and map them to the Request model.
     * @param actionType The {@link ActionType action type}
     * @param vertex The vertex to use as body. Can be {@code null}
     * @param isHttpRequest Whether this is an HTTP request or regular socket one. Default value is false.
     */
    public Request(ActionType actionType, Index vertex, boolean isHttpRequest) {
        this.actionType = actionType;
        this.vertex = vertex == null ? new Index(5, 5) : vertex;
        this.isHttpRequest = isHttpRequest;
    }
}

