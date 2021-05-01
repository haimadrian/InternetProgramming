package org.hit.internetprogramming.haim.matrix.server.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.log4j.Log4j2;
import org.hit.internetprogramming.haim.matrix.common.action.ActionType;
import org.hit.internetprogramming.haim.matrix.common.comms.HttpStatus;
import org.hit.internetprogramming.haim.matrix.common.comms.Request;
import org.hit.internetprogramming.haim.matrix.common.comms.Response;
import org.hit.internetprogramming.haim.matrix.common.mat.Index;
import org.hit.internetprogramming.haim.matrix.server.action.ActionExecutor;
import org.hit.internetprogramming.haim.matrix.server.common.ClientInfo;
import org.hit.internetprogramming.haim.matrix.server.common.RequestHandler;
import org.hit.internetprogramming.haim.matrix.server.common.exception.FavIconException;
import org.hit.internetprogramming.haim.matrix.server.common.exception.WebException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.hit.internetprogramming.haim.matrix.server.common.ClientHandler.*;

/**
 * A matrix client handler used to handle requests from MatrixClient and server them.<br/>
 * As we use a common {@link org.hit.internetprogramming.haim.matrix.server.common.TCPServer}, we have to
 * specify the logic of handling user requests somewhere, so it happens in this class.
 * @author Haim Adrian
 * @since 13-Apr-21
 */
@Log4j2
public class MatrixClientHandler implements RequestHandler {
    /**
     * Support generating a random graph, using HTTP GET request. e.g. GET localhost:1234/graph/generate/standard?row=3&col=3
     */
    public static final String GENERATE_GRAPH_STANDARD_PATH = "graph/generate/standard";

    /**
     * Support generating a random graph, using HTTP GET request. e.g. GET localhost:1234/graph/generate/cross?row=3&col=3
     */
    public static final String GENERATE_GRAPH_CROSS_PATH = "graph/generate/cross";

    /**
     * Support GETting reachable vertices of a graph, using HTTP GET request. e.g. GET localhost:1234/graph/reachables?row=1&col=2
     */
    public static final String REACHABLES_PATH = "graph/reachables";

    /**
     * Support GETting neighbor vertices of a graph (matrix), using HTTP GET request. e.g. GET localhost:1234/graph/neighbors?row=2&col=2
     */
    public static final String NEIGHBORS_PATH = "graph/neighbors";

    /**
     * Support GETting a graph as string, using HTTP GET request. e.g. GET localhost:1234/graph/print
     */
    public static final String PRINT_PATH = "graph/print";

    /**
     * Support disconnecting a user, using HTTP GET request. e.g. GET localhost:1234/disconnect
     */
    public static final String DISCONNECT_PATH = "disconnect";

    /**
     * Support specifying query parameters. This is the row value of an {@link Index}, and we use it when getting neighbors for example
     */
    public static final String ROW_QUERY_PARAM = "row";

    /**
     * Support specifying query parameters. This is the column value of an {@link Index}, and we use it when getting neighbors for example
     */
    public static final String COL_QUERY_PARAM = "col";

    /**
     * Jackson object mapper to convert json string to bean and vice versa
     */
    private final ObjectMapper objectMapper;

    /**
     * Constructs a new {@link MatrixClientHandler}
     */
    public MatrixClientHandler() {
        objectMapper = initializeObjectMapper();
    }

    private ObjectMapper initializeObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();

        // Do not enable standard indentation ("pretty-printing"), cause the client depends on the new line character
        // objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Allow serialization of "empty" POJOs (no properties to serialize)
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        return objectMapper;
    }

    @Override
    public String onRequest(ClientInfo client, String requestString, Consumer<Boolean> stopCommunication) throws IOException {
        Boolean stopCommunicating = Boolean.TRUE;
        Response response;
        Request request;

        if (requestString.startsWith("{")) {
            try {
                request = objectMapper.readValue(requestString, Request.class);
            } catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        } else {
            request = readHttpRequest(requestString);
        }

        if ((request != null) && (request.getActionType() != ActionType.DISCONNECT)) {
            stopCommunicating = Boolean.FALSE;
            if (request.isHttpRequest()) {
                // In order to recognize a user from browser (HTTP), we reset its port because every HTTP request
                // arrives as a new socket, with a different port.
                client = client.toBuilder().port(0).build();
            }

            if (request.getActionType() == null) {
                response = Response.badRequest("actionType is mandatory");
            } else {
                // !-- Execute the action
                response = ActionExecutor.getInstance().execute(client, request);
                if (response == null) {
                    return null; // Return nothing back to client
                }
            }
        } else {
            response = Response.ok();
        }

        stopCommunication.accept(stopCommunicating);
        return responseToString(response, request != null && request.isHttpRequest());
    }

    @Override
    public String onError(ClientInfo client, Throwable thrown) throws IOException {
        Response response;

        if (thrown instanceof IllegalArgumentException) {
            response = Response.badRequest(thrown.getMessage());
        } else if (thrown instanceof WebException) {
            response = Response.error(((WebException) thrown).getHttpStatus().getCode(), thrown.getMessage());
        } else {
            response = Response.error(thrown.getMessage());
        }

        return responseToString(response, thrown instanceof WebException);
    }

    private String responseToString(Response response, boolean httpRequest) throws IOException {
        String responseString = objectMapper.writeValueAsString(response);

        if (httpRequest || response.isHttpResponse()) {
            String contentFormatted = formatContentForHttp(response);
            String body = String.format(HTTP_BODY, responseString, contentFormatted);
            responseString = String.format(HTTP_HEADERS, response.getStatus(), HttpStatus.valueOf(response.getStatus()).name(), "text/html", body.length()) + END_OF_HEADERS + body;
        }

        return responseString;
    }

    private String formatContentForHttp(Response response) {
        String result = "Content:<br>";
        if (response.getValue() != null) {
            result += response.getValue().toString();
        } else {
            result += response.getMessage().replaceAll("\\n", "<br>").replaceAll(" ", "&nbsp;");
        }
        return result;
    }

    private Request readHttpRequest(String httpRequest) throws IOException {
        String[] requestLines = httpRequest.split(System.lineSeparator());

        // First line is the request line. e.g. GET /graph/generate?row=7&col=7 HTTP/1.1
        String[] requestLine = requestLines[0].split(" ");
        String httpMethod = requestLine[0];
        String httpPath = requestLine[1];
        String httpPathLower = httpPath.toLowerCase();
        String httpVersion = requestLine[2];

        // Second line is the host. e.g. Host: 127.0.0.1:1234
        String host = requestLines[1].split(" ")[1];

        // Next lines are headers
        Map<String, String> headers = new HashMap<>();
        for (int i = 2; i < requestLines.length; i++) {
            String[] header = requestLines[i].split(":");
            if (header.length > 1) {
                headers.put(header[0].trim().toLowerCase(), header[1].trim());
            } else {
                headers.put(header[0].trim().toLowerCase(), header[0].trim());
            }
        }

        String accessLog = String.format("Client [%s], method %s, path %s, version %s", headers.get("user-agent"), httpMethod, httpPath, httpVersion);
        log.info(accessLog);

        if (!"get".equalsIgnoreCase(httpMethod)) {
            throw new WebException(HttpStatus.METHOD_NOT_ALLOWED, "Use GET only");
        }

        if (httpPathLower.contains("favicon.ico")) {
            throw new FavIconException(); // ClientHandler will write favicon.ico file
        }

        Request request;
        if (httpPathLower.contains(GENERATE_GRAPH_STANDARD_PATH)) {
            request = new Request(ActionType.GENERATE_RANDOM_GRAPH_STANDARD, fetchIndexFromQuery(httpPathLower, false), true);
        } else if (httpPathLower.contains(GENERATE_GRAPH_CROSS_PATH)) {
            request = new Request(ActionType.GENERATE_RANDOM_GRAPH_CROSS, fetchIndexFromQuery(httpPathLower, false), true);
        }  else if (httpPathLower.contains(NEIGHBORS_PATH)) {
            request = new Request(ActionType.GET_NEIGHBORS, fetchIndexFromQuery(httpPathLower, true), true);
        } else if (httpPathLower.contains(REACHABLES_PATH)) {
            request = new Request(ActionType.GET_REACHABLES, fetchIndexFromQuery(httpPathLower, true), true);
        } else if (httpPathLower.contains(DISCONNECT_PATH)) {
            request = new Request(ActionType.DISCONNECT, null, true);
        } else if (httpPathLower.contains(PRINT_PATH)) {
            request = new Request(ActionType.PRINT_GRAPH, null, true);
        } else {
            throw new WebException(HttpStatus.NOT_FOUND, "No handler for: " + httpPath);
        }

        return request;
    }

    private Index fetchIndexFromQuery(String httpPath, boolean isMandatory) throws WebException {
        String[] queryParams = httpPath.split("\\?");

        if (isMandatory && queryParams.length != 2) {
            throw new WebException(HttpStatus.BAD_REQUEST, "Missing query parameters. Was: " + httpPath);
        }

        int row = 5, col = 5;

        if (queryParams.length > 1) {
            queryParams = queryParams[1].split("&");
            for (String queryParam : queryParams) {
                String[] nameAndValue = queryParam.split("=");
                if (nameAndValue.length != 2) {
                    throw new WebException(HttpStatus.BAD_REQUEST, "Illegal query parameter. Was: " + nameAndValue[0]);
                }

                if (nameAndValue[0].equalsIgnoreCase(ROW_QUERY_PARAM)) {
                    row = parseInteger(nameAndValue[1], ROW_QUERY_PARAM);
                } else if (nameAndValue[0].equalsIgnoreCase(COL_QUERY_PARAM)) {
                    col = parseInteger(nameAndValue[1], COL_QUERY_PARAM);
                }
            }
        }

        return new Index(row, col);
    }

    private int parseInteger(String valueToParse, String paramName) throws WebException {
        int value;
        try {
            value = Integer.parseInt(valueToParse.trim());
        } catch (NumberFormatException e) {
            throw new WebException(HttpStatus.BAD_REQUEST, "Illegal query parameter. '" + paramName + "' must be of type int. Was: " + valueToParse);
        }
        return value;
    }
}

