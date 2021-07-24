package org.hit.internetprogramming.eoh.server.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.hit.internetprogramming.eoh.common.action.ActionType;
import org.hit.internetprogramming.eoh.common.comms.HttpStatus;
import org.hit.internetprogramming.eoh.common.comms.Request;
import org.hit.internetprogramming.eoh.common.comms.Response;
import org.hit.internetprogramming.eoh.common.comms.TwoVerticesBody;
import org.hit.internetprogramming.eoh.common.graph.IGraph;
import org.hit.internetprogramming.eoh.common.mat.Index;
import org.hit.internetprogramming.eoh.common.util.JsonUtils;
import org.hit.internetprogramming.eoh.server.action.ActionExecutor;
import org.hit.internetprogramming.eoh.server.common.ClientInfo;
import org.hit.internetprogramming.eoh.server.common.RequestHandler;
import org.hit.internetprogramming.eoh.server.common.exception.FavIconException;
import org.hit.internetprogramming.eoh.server.common.exception.WebException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.hit.internetprogramming.eoh.server.common.ClientHandler.END_OF_HEADERS;
import static org.hit.internetprogramming.eoh.server.common.ClientHandler.HTTP_HEADERS;

/**
 * A matrix client handler used to handle requests from MatrixClient and server them.<br/>
 * As we use a common {@link org.hit.internetprogramming.eoh.server.common.TCPServer}, we have to
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
     * Support generating a random graph, using HTTP GET request. e.g. GET localhost:1234/graph/generate/regular?row=3&col=3
     */
    public static final String GENERATE_GRAPH_REGULAR_PATH = "graph/generate/regular";

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
     * Support GETting connected components in a graph, using HTTP GET request. e.g. GET localhost:1234/graph/algo/connectedcomponents
     */
    public static final String CONNECTED_COMPONENTS_PATH = "graph/algo/connectedcomponents";

    /**
     * Support GETting shortest paths in a graph, using HTTP GET request. e.g. GET localhost:1234/graph/algo/shortestpaths?srcrow=1&srccol=0&destrow=1&destcol=4
     */
    public static final String SHORTEST_PATHS_PATH = "graph/algo/shortestpaths";

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

    public static final String HTML_PAGE;

    /**
     * Jackson object mapper to convert json string to bean and vice versa
     */
    private final ObjectMapper objectMapper;

    static {
        String content;
        try {
            InputStream resourceAsStream = MatrixClientHandler.class.getClassLoader().getResourceAsStream("index.html");
            if (resourceAsStream != null) {
                content = new BufferedReader(new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
            } else {
                content = "##1<br/>##2";
            }
        } catch (Exception e) {
            content = "##1<br/>##2";
        }

        HTML_PAGE = content;
    }

    /**
     * Constructs a new {@link MatrixClientHandler}
     */
    public MatrixClientHandler() {
        objectMapper = JsonUtils.createObjectMapper();
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
            if (request.isHttp()) {
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
        return responseToString(response, request != null && request.isHttp(), request);
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

        return responseToString(response, thrown instanceof WebException, null);
    }

    private String responseToString(Response response, boolean httpRequest, Request request) throws IOException {
        String responseString = objectMapper.writeValueAsString(response);

        if (httpRequest || response.isHttp()) {
            String paragraph1 = (request == null ? "Error" : request.getActionType().name()) + " response:";
            String contentFormatted = formatContentForHttp(response);
            String body = HTML_PAGE.replace("##1", paragraph1).replace("##2", contentFormatted);
            responseString = String.format(HTTP_HEADERS, response.getStatus(), HttpStatus.valueOf(response.getStatus()).name(), "text/html", body.length()) + END_OF_HEADERS + body;
        }

        return responseString;
    }

    private String formatContentForHttp(Response response) {
        String result = null;
        boolean shouldPrepareTextForHtml = true;

        try {
            List<Index> body = response.getBodyAs(new TypeReference<>() {});
            result = body.stream().map(Index::toString).collect(Collectors.joining(System.lineSeparator())) + System.lineSeparator() +
                    "Count: " + body.size();
        } catch (Throwable ignore) {
            try {
                List<Collection<Index>> body = response.getBodyAs(new TypeReference<>() {});
                result = body.stream().map(Collection::toString).collect(Collectors.joining(System.lineSeparator())) + System.lineSeparator() +
                        "Count: " + body.size() + System.lineSeparator() +
                        "Minimum length: " + body.stream().mapToInt(Collection::size).min().orElse(0) + System.lineSeparator() +
                        "Maximum length: " + body.stream().mapToInt(Collection::size).max().orElse(0);
            } catch (Throwable ignore2) {
                try {
                    IGraph<Index> body = response.getBodyAs(new TypeReference<>() {});
                    result = body.printGraph();
                } catch (Throwable ignore3) {
                    try {
                        result = response.getBodyAs(String.class);

                        if (result != null) {
                            shouldPrepareTextForHtml = false;
                        }
                    } catch (Throwable ignore4) {
                        JsonNode body = response.getBodyAs(JsonNode.class);
                        if (body != null) {
                            result = body.toString();
                        }
                    }
                }
            }
        }

        if (result == null) {
            result = response.getMessage();
        }

        if (shouldPrepareTextForHtml) {
            result = result.replaceAll("\\n", "<br>").replaceAll(" ", "&nbsp;");
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
            request = new Request(ActionType.GENERATE_RANDOM_BINARY_GRAPH_STANDARD, fetchIndexFromQuery(httpPathLower, false), true);
        } else if (httpPathLower.contains(GENERATE_GRAPH_CROSS_PATH)) {
            request = new Request(ActionType.GENERATE_RANDOM_BINARY_GRAPH_CROSS, fetchIndexFromQuery(httpPathLower, false), true);
        }  else if (httpPathLower.contains(GENERATE_GRAPH_REGULAR_PATH)) {
            request = new Request(ActionType.GENERATE_RANDOM_BINARY_GRAPH_REGULAR, fetchIndexFromQuery(httpPathLower, false), true);
        }  else if (httpPathLower.contains(NEIGHBORS_PATH)) {
            request = new Request(ActionType.GET_NEIGHBORS, fetchIndexFromQuery(httpPathLower, true), true);
        } else if (httpPathLower.contains(REACHABLES_PATH)) {
            request = new Request(ActionType.GET_REACHABLES, fetchIndexFromQuery(httpPathLower, true), true);
        } else if (httpPathLower.contains(DISCONNECT_PATH)) {
            request = new Request(ActionType.DISCONNECT, null, true);
        } else if (httpPathLower.contains(PRINT_PATH)) {
            request = new Request(ActionType.PRINT_GRAPH, null, true);
        } else if (httpPathLower.contains(CONNECTED_COMPONENTS_PATH)) {
            request = new Request(ActionType.CONNECTED_COMPONENTS, null, true);
        }  else if (httpPathLower.contains(SHORTEST_PATHS_PATH)) {
            request = new Request(ActionType.SHORTEST_PATHS, new TwoVerticesBody<>(
                    fetchIndexFromQuery(httpPathLower, false, "srcrow", "srccol"),
                    fetchIndexFromQuery(httpPathLower, false, "destrow", "destcol")), true);
        } else if (httpPathLower.equals("/")) {
            request = new Request(ActionType.INDEX_HTML, null, true);
        } else {
            throw new WebException(HttpStatus.NOT_FOUND, "No handler for: " + httpPath);
        }

        return request;
    }

    private Index fetchIndexFromQuery(String httpPath, boolean isMandatory) throws WebException {
        return fetchIndexFromQuery(httpPath, isMandatory, ROW_QUERY_PARAM, COL_QUERY_PARAM);
    }

    private Index fetchIndexFromQuery(String httpPath, boolean isMandatory, String rowParamName, String colParamName) throws WebException {
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

                if (nameAndValue[0].equalsIgnoreCase(rowParamName)) {
                    row = parseInteger(nameAndValue[1], rowParamName);
                } else if (nameAndValue[0].equalsIgnoreCase(colParamName)) {
                    col = parseInteger(nameAndValue[1], colParamName);
                }
            }
        }

        return Index.from(row, col);
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

