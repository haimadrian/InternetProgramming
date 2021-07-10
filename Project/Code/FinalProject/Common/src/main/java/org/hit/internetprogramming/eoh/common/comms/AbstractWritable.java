package org.hit.internetprogramming.eoh.common.comms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.hit.internetprogramming.eoh.common.action.ActionType;
import org.hit.internetprogramming.eoh.common.util.JsonUtils;

/**
 * This class is the base class of {@link Request} and {@link Response}.<br/>
 * Here we manage the content (body) of a request/response, to ease the usage of it as a generic content.<br/>
 * This way we can use anything as a body, and work with it with generic type, hiding the marshalling here.
 * @author Haim Adrian
 * @since 10-Jul-2021
 */
@ToString
@EqualsAndHashCode
public abstract class AbstractWritable {
    /**
     * The body of a request can be anything. To support multiple request contents.<br/>
     * Optional contents:
     * <ul>
     *     <li>{@code IGraph<Index>} - The content of a {@link ActionType#PUT_GRAPH PUT_GRAPH} request</li>
     *     <li>{@code Index} - When sending GET neighbors / reachables request, there is additional parameter telling the location to get the response for.
     *                         When sending a GENERATE request, we use this as the dimension of a matrix. e.g. 3x3</li>
     * </ul>
     */
    @JsonProperty("body")
    private final JsonNode body;

    /**
     * Used to mark requests that we detect as HTTP requests, so the response can be an HTTP response.<br/>
     * We support receiving HTTP requests from browser, so we can have another client to play with<br/>
     * This field is ignored from json as it is relevant for server only.
     */
    @JsonIgnore
    private final boolean isHttp;

    /**
     * Constructs a new {@link AbstractWritable} with any body, and a flag indicating whether it is an HTTP request.<br/>
     * Used by server when it handles HTTP requests and map them to the Request model.
     * @param body The body to set
     * @param isHttpRequest Whether this is an HTTP request or regular socket one. Default value is false.
     */
    public AbstractWritable(Object body, boolean isHttpRequest) {
        this.body = body == null ? null : (body instanceof JsonNode ? (JsonNode) body : JsonUtils.convertValueToJsonNode(body));
        this.isHttp = isHttpRequest;
    }

    /**
     * Get the body of a request, as a specific type, to ease a generic usage of Request class.<br/>
     * For generic types use {@link #getBodyAs(TypeReference)}
     * @param type Type info to convert the body to
     * @param <T> Underlying type
     * @return Body as requested type, or {@code null} in case there is no body
     */
    @SuppressWarnings("unchecked")
    public <T> T getBodyAs(Class<T> type) {
        if (body == null) {
            return null;
        }

        if (type.equals(JsonNode.class)) {
            return (T)body;
        }

        return JsonUtils.convertValueFromJsonNode(body, type);
    }

    /**
     * Get the body of a request, as a specific type, to ease a generic usage of Request class.<br/>
     * This overload created to support conversion to generic collection types. For example:<br/>
     * {@code List<Collection<Index>> shortestPaths = request.getBodyAs(new TypeReference<>() {});}<br/>
     * or: {@code IGraph<Index> graph = request.getBodyAs(new TypeReference<>() {});}
     * @param type Type info to convert the body to
     * @param <T> Underlying type
     * @return Body as requested type, or {@code null} in case there is no body
     */
    public <T> T getBodyAs(TypeReference<T> type) {
        if (body == null) {
            return null;
        }

        return JsonUtils.convertValueFromJsonNode(body, type);
    }

    @JsonIgnore
    public boolean isHttp() {
        return isHttp;
    }
}
