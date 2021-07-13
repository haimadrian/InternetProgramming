package org.hit.internetprogramming.eoh.common.comms;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Request extends AbstractWritable {
    /**
     * What action to execute
     */
    @Getter
    private final ActionType actionType;

    /**
     * Constructs a new {@link Request} with no body
     * @param actionType The {@link ActionType action type}
     * @see AbstractWritable#AbstractWritable(Object, boolean)
     */
    public Request(ActionType actionType) {
        this(actionType, null);
    }

    /**
     * Constructs a new {@link Request} with json body. This constructor is for json marshalling. Don't use it
     * @param actionType The {@link ActionType action type}
     * @param body The body to set
     */
    @JsonCreator
    public Request(@JsonProperty("actionType") ActionType actionType, @JsonProperty(value = "body") JsonNode body) {
        this(actionType, body, false);
    }

    /**
     * Constructs a new {@link Request} with any body.
     * @param actionType The {@link ActionType action type}
     * @param body The body to set
     * @see AbstractWritable#AbstractWritable(Object, boolean)
     */
    public Request(ActionType actionType, Object body) {
        this(actionType, body, false);
    }

    /**
     * Constructs a new {@link Request} with any body, and a flag indicating whether it is an HTTP request.<br/>
     * Used by server when it handles HTTP requests and map them to the Request model.
     * @param actionType The {@link ActionType action type}
     * @param body The body to set
     * @param isHttpRequest Whether this is an HTTP request or regular socket one. Default value is false.
     * @see AbstractWritable#AbstractWritable(Object, boolean)
     */
    public Request(ActionType actionType, Object body, boolean isHttpRequest) {
        super(body, isHttpRequest);
        this.actionType = actionType;
    }
}
