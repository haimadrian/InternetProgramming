package org.hit.internetprogramming.eoh.common.comms;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Response extends AbstractWritable {
    /**
     * HTTP status code, to support OK / ERROR responses.
     * @see HttpStatus
     */
    @Getter
    private final int status;

    /**
     * An optional message to use as a result for simple OK / ERROR responses
     */
    @Getter
    private final String message;

    /**
     * Constructs a new {@link Response} model
     * @param status {@link HttpStatus HTTP status} to mark this response as.
     * @param message An optional message, in case of simple success, or a failure.
     * @param body The body to set
     * @param isHttpResponse Whether this is an HTTP response or regular socket one.
     * @see AbstractWritable#AbstractWritable(Object, boolean)
     */
    public Response(int status, String message, Object body, boolean isHttpResponse) {
        super(body, isHttpResponse);
        this.status = status;
        this.message = message;
    }

    /**
     * Constructs a new {@link Response} with json body. This constructor is for json marshalling. Don't use it
     * @param status {@link HttpStatus HTTP status} to mark this response as.
     * @param message An optional message, in case of simple success, or a failure.
     * @param body The body to set
     */
    @JsonCreator
    public Response(@JsonProperty(value = "status") int status, @JsonProperty(value = "message") String message, @JsonProperty(value = "body") JsonNode body) {
        this(status, message, body, false);
    }

    // Builders to ease the use of this class when returning a response from server.

    public static Response ok() {
        return ok(HttpStatus.OK.getCode(), (List<Index>) null);
    }

    public static <T> Response ok(T value) {
        return new Response(HttpStatus.OK.getCode(), null, value, false);
    }

    public static <T> Response ok(int status, T value) {
        return ok(status, value, false);
    }

    public static <T> Response ok(int status, T value, boolean isHttpResponse) {
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

