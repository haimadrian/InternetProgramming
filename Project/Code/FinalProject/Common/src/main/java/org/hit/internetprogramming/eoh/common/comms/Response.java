package org.hit.internetprogramming.eoh.common.comms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hit.internetprogramming.eoh.common.action.ActionType;
import org.hit.internetprogramming.eoh.common.graph.IGraph;
import org.hit.internetprogramming.eoh.common.graph.MatrixGraphAdapter;
import org.hit.internetprogramming.eoh.common.mat.Index;

import java.util.List;

/**
 * A response model is used by both server and client.<br/>
 * Server constructs a response, based on client {@link Request}, and return this response
 * as json string to the client. Then the client unmarshall this json string into Response model,
 * and extract its content.<br/>
 * A response is marked by some HTTP status, to ease response handling by client. (Whether it is a successful response or failure)<br/>
 * A response got a body in it, e.g. message to contain the error message from server, or a graph / list of indices,
 * depending on what a request was asking for.
 * <pr>
 * Use the builders of this class in order to make user code (server) cleaner.<br/>
 * For example, you can use {@code Response res = Response.ok()} in order to construct an empty 200 OK response.
 * </pr>
 * @author Haim Adrian
 * @since 13-Apr-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    /**
     * HTTP status code, to support OK / ERROR responses.
     * @see HttpStatus
     */
    private int status;

    /**
     * An optional message to use as a result for simple OK / ERROR responses
     */
    private String message;

    /**
     * An action response value, as a list. (list of neighbors / reachables)
     */
    private List<Index> value;

    /**
     * The content of a {@link ActionType#GET_GRAPH GET_GRAPH} response. Can be null
     */
    private IGraph<Index> graph;

    /**
     * Used to mark responses that we detect as HTTP requests, so the response can be an HTTP response.
     * @see Request#isHttpRequest()
     */
    @JsonIgnore
    private boolean isHttpResponse;

    /**
     * Constructs a new {@link Response} model
     * @param status {@link HttpStatus HTTP status} to mark this response as.
     * @param message An optional message, in case of simple success, or a failure.
     * @param value List of vertices to return using this response. (When responding to {@link ActionType#GET_NEIGHBORS} for example)
     * @param isHttpResponse Whether this is an HTTP response or regular socket one.
     */
    public Response(int status, String message, List<Index> value, boolean isHttpResponse) {
        this.status = status;
        this.message = message;
        this.value = value;
        this.isHttpResponse = isHttpResponse;
    }

    // Builders to ease the use of this class when returning a response from server.

    public static Response ok() {
        return ok(HttpStatus.OK.getCode(), (List<Index>) null);
    }

    public static Response ok(List<Index> value) {
        return ok(HttpStatus.OK.getCode(), value);
    }

    public static Response ok(MatrixGraphAdapter<?> value) {
        return new Response(HttpStatus.OK.getCode(), null, null, value, false);
    }

    public static Response ok(int status, List<Index> value) {
        return ok(status, value, false);
    }

    public static Response ok(int status, List<Index> value, boolean isHttpResponse) {
        return new Response(status, null, value, isHttpResponse);
    }

    public static Response ok(String message) {
        return ok(HttpStatus.OK.getCode(), message);
    }

    public static Response ok(String message, boolean isHttpResponse) {
        return ok(HttpStatus.OK.getCode(), message, isHttpResponse);
    }

    public static Response ok(int status, String message) {
        return ok(status, message, false);
    }

    public static Response ok(int status, String message, boolean isHttpResponse) {
        return new Response(status, message, null, isHttpResponse);
    }

    public static Response error() {
        return error(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), HttpStatus.INTERNAL_SERVER_ERROR.name());
    }

    public static Response error(String errorMessage) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), errorMessage);
    }

    public static Response error(int status, String errorMessage) {
        return error(status, errorMessage, false);
    }

    public static Response error(int status, String errorMessage, boolean isHttpResponse) {
        return new Response(status, errorMessage, null, isHttpResponse);
    }

    public static Response badRequest() {
        return badRequest(HttpStatus.BAD_REQUEST.getCode(), HttpStatus.BAD_REQUEST.name());
    }

    public static Response badRequest(String errorMessage) {
        return badRequest(HttpStatus.BAD_REQUEST.getCode(), errorMessage);
    }

    public static Response badRequest(int status, String errorMessage) {
        return badRequest(status, errorMessage, false);
    }

    public static Response badRequest(int status, String errorMessage, boolean isHttpResponse) {
        return new Response(status, errorMessage, null, isHttpResponse);
    }
}

